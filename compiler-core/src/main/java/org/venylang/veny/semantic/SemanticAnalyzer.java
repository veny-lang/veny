/*
 * Copyright 2025 Stoyan Petkov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.venylang.veny.semantic;

import org.venylang.veny.parser.ast.*;
import org.venylang.veny.parser.ast.expression.*;
import org.venylang.veny.parser.ast.statement.*;
import org.venylang.veny.semantic.symbols.*;
import org.venylang.veny.semantic.types.*;
import org.venylang.veny.util.ErrorReporter;
import org.venylang.veny.util.Visibility;

import java.util.*;

/**
 * Performs semantic analysis on the Abstract Syntax Tree (AST) nodes of a Veny program.
 * <p>
 * The semantic analysis process includes:
 * <ul>
 *   <li>Building symbol tables and managing scopes</li>
 *   <li>Validating type correctness and visibility rules</li>
 *   <li>Detecting and reporting semantic errors such as redefinitions and undeclared symbols</li>
 * </ul>
 * This class implements the {@code AstVisitor} interface, visiting each node in the AST
 * and performing appropriate checks.
 * </p>
 */
public class SemanticAnalyzer implements AstVisitor<Void> {

    /** Stack to manage nested scopes such as global, class, method, and block scopes. */
    private final Deque<Scope> scopeStack = new ArrayDeque<>();

    /** Collected semantic error messages. */
    private final List<String> errors = new ArrayList<>();

    /** The global (top-level) scope shared across all files. */
    private final GlobalScope globalScope;

    private final ErrorReporter errorReporter;

    /** Track loop nesting for break/continue */
    private int loopDepth = 0;

    public SemanticAnalyzer(GlobalScope globalScope, ErrorReporter errorReporter) {
        this.globalScope = globalScope;
        this.errorReporter = errorReporter;
        enterScope(globalScope);
    }

    /**
     * Returns the list of semantic errors encountered during analysis.
     *
     * @return list of error messages
     */
    public List<String> getErrors() {
        return errors;
    }

    public GlobalScope getGlobalScope() {
        return globalScope;
    }

    public ClassSymbol resolveClass(String name) {
        Symbol sym = globalScope.resolve(name);
        return (sym instanceof ClassSymbol cls) ? cls : null;
    }

    public Symbol resolveSymbol(String name) {
        return globalScope.resolve(name);
    }

    /** @return the current (top of the stack) scope */
    private Scope currentScope() {
        return scopeStack.peek();
    }

    /**
     * Pushes a new scope onto the scope stack.
     *
     * @param scope the scope to enter
     */
    private void enterScope(Scope scope) {
        scopeStack.push(scope);
    }

    /**
     * Pops the current scope off the scope stack.
     */
    private void exitScope() {
        scopeStack.pop();
    }

    /**
     * Records a semantic error message.
     *
     * @param message the error message to record
     */
    private void error(String message) {
        errors.add(message);
    }

    /**
     * Handles unsupported AST nodes during analysis.
     *
     * @param node the unsupported node
     * @return always returns {@code null}
     */
    private Void unsupported(AstNode node) {
        error("Semantic analysis not implemented for: " + node.getClass().getSimpleName());
        return null;
    }

    // === Declarations ===

    /** {@inheritDoc} */
    @Override
    public Void visit(Program node) {
        for (VenyFile file : node.files()) {
            file.accept(this);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(VenyFile node) {
        for (ClassDecl cls : node.classes()) {
            cls.accept(this);
        }
        for (InterfaceDecl iface : node.interfaces()) {
            iface.accept(this);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(ClassDecl node) {
        if (currentScope() != globalScope) {
            throw new IllegalStateException("Classes must be declared in the global scope.");
        }

        if (globalScope.resolveLocal(node.name()) != null) {
            throw new SemanticException("Class '" + node.name() + "' is already defined.");
        }

        ClassSymbol classSymbol = new ClassSymbol(node.name(), globalScope);
        globalScope.define(classSymbol);
        enterScope(classSymbol);

        try {
            for (VarDecl field : node.fields()) {
                field.accept(this);
            }
            for (MethodDecl method : node.methods()) {
                method.accept(this);
            }
        } finally {
            exitScope();
        }
        return null;
    }

    @Override
    public Void visit(InterfaceDecl node) {
        if (currentScope() != globalScope) {
            throw new IllegalStateException("Interfaces must be declared in the global scope.");
        }

        if (globalScope.resolveLocal(node.name()) != null) {
            error("Interface '" + node.name() + "' is already defined.");
            //throw new SemanticException("Interface '" + node.name() + "' is already defined.");
            return null;
        }

        InterfaceSymbol ifaceSymbol = new InterfaceSymbol(node.name(), globalScope);
        globalScope.define(ifaceSymbol);
        enterScope(ifaceSymbol);

        try {
            // Visit interface members (methods only)
            for (MethodDecl method : node.methods()) {
                method.accept(this);
            }
        } finally {
            exitScope();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(VarDecl node) {
        if (currentScope().resolveLocal(node.name()) != null) {
            error("Variable '" + node.name() + "' is already declared in this scope.");
            //throw new SemanticException("Variable '" + node.name() + "' is already declared in this scope.");
            return null;
        }

        Type type = resolveType(node.typeName());
        VariableSymbol var = new VariableSymbol(
                node.name(),
                type,
                node.visibility(),
                false, // isParameter
                !node.isMutable()  // isVal
        );
        currentScope().define(var);

        if (node.initializer() != null) {
            node.initializer().accept(this);
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(MethodDecl node) {
        Type returnType = resolveType(node.returnType());
        MethodSymbol method = new MethodSymbol(
                node.name(), returnType,
                node.visibility() == Visibility.PUBLIC ? Visibility.PUBLIC : Visibility.PRIVATE,
                currentScope()
        );
        currentScope().define(method);
        enterScope(method);

        try {
            for (MethodDecl.Parameter param : node.parameters()) {
                // 1. Resolve the type of the parameter
                Type paramType = resolveType(param.type());

                // 2. Create a variable symbol
                VariableSymbol paramSymbol = new VariableSymbol(
                        param.name(),
                        paramType,
                        Visibility.PUBLIC, // parameters are usually accessible within the method
                        true,  // isParameter
                        false  // // not a val by default
                );

                // 3. Define it in the current (method) scope
                currentScope().define(paramSymbol);
            }

            // Only visit the body if it exists (interfaces will have null)
            if (node.body() != null) {
                for (Statement stmt : node.body()) {
                    stmt.accept(this);
                }
            }
        } finally {
            // Exit method scope
            exitScope();
        }
        return null;
    }

    // === Statements ===

    /** {@inheritDoc} */
    @Override
    public Void visit(BlockStmt node) {
        enterScope(new LocalScope(currentScope()));
        try {
            for (Statement stmt : node.statements()) {
                stmt.accept(this);
            }
        } finally {
            exitScope();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(IfStmt node) {
        node.condition().accept(this);
        node.thenBranch().accept(this);
        if (node.elseBranch() != null) {
            node.elseBranch().accept(this);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(WhileStmt node) {
        loopDepth++;
        node.condition().accept(this);
        node.body().accept(this);
        loopDepth--;
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(ForStmt node) {
        loopDepth++;
        node.iterable().accept(this);
        enterScope(new LocalScope(currentScope()));
        try {
            // Define loop variable
            VariableSymbol var = new VariableSymbol(node.variable(), TypeResolver.resolveBuiltin("Unknown"), Visibility.DEFAULT, false, false);
            currentScope().define(var);
            node.body().accept(this);
        } finally {
            exitScope();
        }
        loopDepth--;
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(ReturnStmt node) {
        if (node.value() != null) {
            node.value().accept(this);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(ExprStmt node) {
        node.expression().accept(this);
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(VarStmt node) {
        return node.initializer().accept(this);
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(ValStmt node) {
        return node.initializer().accept(this);
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(BreakStmt node) {
        if (loopDepth == 0) {
            error("'break' outside of loop");
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(ContinueStmt node) {
        if (loopDepth == 0) {
            error("'break' outside of loop");
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(ArrayLiteralExpr node) {
        for (Expression elem : node.elements()) {
            elem.accept(this);
        }
        return null;
    }

    // === Expressions ===

    /** {@inheritDoc} */
    @Override
    public Void visit(BinaryExpr node) {
        node.left().accept(this);
        node.right().accept(this);
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(UnaryExpr node) {
        node.operand().accept(this);
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(LiteralExpr node) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(VariableExpr node) {
        Symbol symbol = currentScope().resolve(node.name());
        if (symbol == null) {
            error("Undefined variable: " + node.name());
        } else {
            // TODO: Associate resolved symbol with node
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(AssignExpr node) {
        node.value().accept(this);

        Symbol targetSym = currentScope().resolve(node.name());
        if (targetSym == null) {
            error("Undefined variable: " + node.name());
        } else if (targetSym instanceof VariableSymbol varSym && !varSym.isMutable()) {
            error("Cannot assign to immutable variable: " + node.name());
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(CallExpr node) {
        node.callee().accept(this);
        for (Expression arg : node.arguments()) {
            arg.accept(this);
        }
        // Optional: validate callee type / arity
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(NewExpr node) {
        Symbol cls = currentScope().resolve(node.className());
        if (!(cls instanceof ClassSymbol)) {
            error("Unknown class: " + node.className());
        }
        for (Expression arg : node.arguments()) {
            arg.accept(this);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(GetExpr node) {
        node.target().accept(this);
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(SetExpr node) {
        node.target().accept(this); // resolve object
        node.value().accept(this);
        // Optional: validate field exists in class/type
        return null;
    }

    // === Type resolution helper ===

    /**
     * Resolves a type name to a {@link Type} object, either built-in or user-defined.
     *
     * @param typeName the name of the type to resolve
     * @return the resolved {@code Type} or {@code null} if not found
     */
    private Type resolveType(String typeName) {
        if (typeName.startsWith("[") && typeName.endsWith("]")) {
            String inner = typeName.substring(1, typeName.length() - 1);
            Type elemType = resolveType(inner);
            return new ArrayType(elemType);
        }

        BuiltinType builtin = TypeResolver.resolveBuiltin(typeName);
        if (builtin != null) {
            return builtin;
        }

        Symbol sym = currentScope().resolve(typeName);
        if (sym instanceof ClassSymbol clsSym) {
            return new ClassType(clsSym);
        }

        if (sym instanceof InterfaceSymbol ifaceSym) {
            return new InterfaceType(ifaceSym);
        }

        error("Unknown type: " + typeName);
        return null;
    }
}
