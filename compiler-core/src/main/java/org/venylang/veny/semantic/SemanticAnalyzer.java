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

    /** Track current method symbol */
    private MethodSymbol currentMethod = null;

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

        MethodSymbol prevMethod = currentMethod;
        currentMethod = method;

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

        currentMethod = prevMethod;
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
        if (currentMethod == null) {
            error("Return statement not inside a method");
            return null;
        }

        Type expected = currentMethod.returnType();

        if (node.value() != null) {
            // Make sure we traverse / type-check the return expression subtree
            node.value().accept(this);

            // After visiting, the expression node should have its resolved type set
            Type actual = node.value().getResolvedType();
            if (actual == null) {
                error("Could not determine type of return expression");
            } else if (!expected.isAssignableFrom(actual)) {
                error("Return type mismatch: expected " + expected + " but got " + actual);
            }
            node.value().setResolvedType(actual);
        } else {
            // returning nothing; ensure expected is void
            if (!BuiltinType.VOID.equals(expected)) {
                error("Return statement missing value, expected " + expected);
            }
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
        Type elemType = null;
        for (Expression elem : node.elements()) {
            elem.accept(this);
            Type t = getExprType(elem);
            if (elemType == null) {
                elemType = t;
            } else if (!elemType.isAssignableFrom(t)) {
                error("Array elements must have the same type");
            }
        }
        if (elemType == null) {
            elemType = BuiltinType.NULL; // or some sentinel
        }
        node.setResolvedType(new ArrayType(elemType));
        node.elementType(elemType);
        return null;
    }

    // === Expressions ===

    /** {@inheritDoc} */
    @Override
    public Void visit(BinaryExpr node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);

        Type leftType = node.getLeft().getResolvedType();
        Type rightType = node.getRight().getResolvedType();

        // Simplest rule: both sides must match
        if (!leftType.equals(rightType)) {
            error("Type mismatch in binary expression: " + leftType + " vs " + rightType);
            node.setResolvedType(BuiltinType.ERROR);
            return null;
        }

        // Decide result type
        switch (node.getOperator()) {
            case "+", "-", "*", "/" -> {
                if (!leftType.equals(BuiltinType.INT)) {
                    error("Arithmetic operators require Int");
                }
                node.setResolvedType(BuiltinType.INT);
            }
            case "==" , "!=" , "<" , "<=" , ">" , ">=" -> {
                node.setResolvedType(BuiltinType.BOOL);
            }
            default -> error("Unsupported operator: " + node.getOperator());
        }        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(UnaryExpr node) {
        node.getOperand().accept(this);
        Type operandType = node.getOperand().getResolvedType();

        switch (node.getOperator()) {
            case "-" -> {
                if (!operandType.equals(BuiltinType.INT)) {
                    error("Unary - requires Int");
                }
                node.setResolvedType(BuiltinType.INT);
            }
            case "!" -> {
                if (!operandType.equals(BuiltinType.BOOL)) {
                    error("Unary ! requires Bool");
                }
                node.setResolvedType(BuiltinType.BOOL);
            }
            default -> error("Unsupported unary operator: " + node.getOperator());
        }
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
            node.setResolvedType(BuiltinType.ERROR);
        } else {
            node.setResolvedType(symbol.getType());
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(AssignExpr node) {
        // Visit the RHS expression first
        node.value().accept(this);

        // Resolve the variable symbol
        Symbol targetSym = currentScope().resolve(node.name());
        if (targetSym == null) {
            error("Undefined variable: " + node.name());
            node.setResolvedType(BuiltinType.ERROR);
            return null;
        }

        if (targetSym instanceof VariableSymbol varSym) {
            if (!varSym.isMutable()) {
                error("Cannot assign to immutable variable: " + node.name());
            }

            Type varType = varSym.getType();
            Type valueType = node.value().getResolvedType();

            if (!varType.isAssignableFrom(valueType)) {
                error("Type mismatch: cannot assign " + valueType + " to " + varType);
            }

            // The type of an assignment expression is the type of the variable (like in Java, C#, etc.)
            node.setResolvedType(varType);
        } else {
            error("Cannot assign to non-variable symbol: " + node.name());
            node.setResolvedType(BuiltinType.ERROR);
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

        Type calleeType = node.callee().getResolvedType();
        if (!(calleeType instanceof CallableType callableType)) {
            error("Cannot call non-callable type: " + calleeType);
            node.setResolvedType(BuiltinType.ERROR);
            return null;
        }

        // Arity check
        if (callableType.paramTypes().size() != node.arguments().size()) {
            error("Wrong number of arguments in call");
        }

        // Type check arguments
        for (int i = 0; i < node.arguments().size(); i++) {
            Type argType = node.arguments().get(i).getResolvedType();
            if (!callableType.paramTypes().get(i).isAssignableFrom(argType)) {
                error("Argument type mismatch at position " + i);
            }
        }

        node.setResolvedType(callableType.returnType());
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(NewExpr node) {
        // If NewExpr has arguments or other children, analyze them
        if (!node.arguments().isEmpty()) {
            for (Expression arg : node.arguments()) {
                arg.accept(this);
            }
        }

        // optionally: validate class exists
        Symbol cls = currentScope().resolve(node.className());
        if (!(cls instanceof ClassSymbol)) {
            error("Unknown class: " + node.className());
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Void visit(GetExpr node) {
        // First analyze the target expression
        node.target().accept(this);
        Type targetType = node.target().getResolvedType();

        if (targetType instanceof ClassType clsType) {
            ClassSymbol cls = clsType.getClassSymbol();

            // Look up the field
            VariableSymbol field = cls.getField(node.field());
            if (field == null) {
                error("Unknown field '" + node.field() + "' in class " + cls.getName());
                node.setResolvedType(BuiltinType.ERROR);
            } else {
                node.setResolvedType(field.getType());
            }

        } else {
            error("Field access '" + node.field() + "' on non-class type: " + targetType);
            node.setResolvedType(BuiltinType.ERROR);
        }
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

    @Override
    public Void visit(IndexExpr node) {
        node.target().accept(this);
        node.index().accept(this);

        Type targetType = node.target().getResolvedType();
        Type indexType = node.index().getResolvedType();

        if (!(targetType instanceof ArrayType arr)) {
            error("Indexing non-array type: " + targetType);
            node.setResolvedType(BuiltinType.ERROR);
            return null;
        }
        if (!indexType.equals(BuiltinType.INT)) {
            error("Array index must be Int");
        }
        node.setResolvedType(arr.getElementType());
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

    /**
     * Returns a Type for literal / variable / array / new / index / call.
     *
     * @param expr
     * @return
     */
    private Type getExprType(Expression expr) {
        if (expr == null) {
            return null;
        }

        // ───────────── Literals ─────────────
        if (expr instanceof LiteralExpr lit) {
            return lit.getType(); // assuming LiteralExpr stores its type (e.g. Int, Text, Bool)
        }

        // ───────────── Variables ─────────────
        if (expr instanceof VariableExpr var) {
            Symbol sym = currentScope().resolve(var.name());
            if (sym instanceof VariableSymbol v) {
                return v.getType();
            }
            error("Unknown variable: " + var.name());
            return null;
        }

        if (expr instanceof BinaryExpr bin) {
            Type left = getExprType(bin.getLeft());
            Type right = getExprType(bin.getRight());
            // Example rule: both sides must match
            if (left != null && !left.equals(right)) {
                error("Type mismatch in binary expression: " + left + " vs " + right);
            }
            return left; // return common type
        }

        if (expr instanceof CallExpr call) {
            // two main uses: factory calls like Int.of(...) or constructor-style like Calculator(...)
            Expression callee = call.callee();

            // Case 1: constructor-style
            if (callee instanceof VariableExpr fnVar) {
                Symbol sym = currentScope().resolve(fnVar.name());
                if (sym instanceof ClassSymbol cls) {// returning nothing; ensure expected is void
                    return new ClassType(cls);
                }
            }

            // Case 2: factory call like Int.of(...)
            if (callee instanceof GetExpr get) {
                if (get.target() instanceof VariableExpr v) {
                    String t = v.name();
                    if ("Int".equals(t) && "of".equals(get.field())) return BuiltinType.INT;
                    if ("Text".equals(t) && "of".equals(get.field())) return BuiltinType.TEXT;
                    if ("Bool".equals(t) && "of".equals(get.field())) return BuiltinType.BOOL;
                    // add Bool.of, etc.
                }
            }

            // Case 3: normal method call
            if (callee instanceof VariableExpr fnVar) {
                Symbol sym = currentScope().resolve(fnVar.name());
                if (sym instanceof MethodSymbol m) {
                    return m.returnType();
                }
            }

            error("Unable to resolve call expression");
            return null;
        }

        // ───────────── Object creation ─────────────
        if (expr instanceof NewExpr n) {
            // new ClassName() → resolve ClassSymbol
            Type t = resolveType(n.className());
            return t;
        }

        // ───────────── Array literals ─────────────
        if (expr instanceof ArrayLiteralExpr arr) {
            if (!arr.elements().isEmpty()) {
                Type firstElemType = getExprType(arr.elements().get(0));
                for (Expression e : arr.elements()) {
                    Type t = getExprType(e);
                    if (t != null && !t.equals(firstElemType)) {
                        error("Inconsistent element types in array literal: expected " + firstElemType + " but found " + t);
                    }
                }
                arr.elementType(firstElemType);
                return new ArrayType(firstElemType);
            }
            error("Cannot infer type for empty array literal");
            return null;
        }

        // ───────────── Index expressions ─────────────
        if (expr instanceof IndexExpr idx) {
            Type targetType = getExprType(idx.target());
            if (targetType instanceof ArrayType at) {
                return at.getElementType();
            }
            error("Indexing a non-array type: " + targetType);
            return null;
        }

        // ───────────── Member access (foo.bar) ─────────────
        if (expr instanceof GetExpr get) {
            // Handle something like Int.of, Console.println, object.field
            Type targetType = getExprType(get.target());

            // Static access: e.g. Int.of
            // If target is a class symbol (static access), treat accordingly
            if (get.target() instanceof VariableExpr v) {
                Symbol sym = currentScope().resolve(v.name());
                if (sym instanceof ClassSymbol cls) {
                    Symbol member = cls.resolve(get.field());
                    if (member instanceof MethodSymbol m) return m.returnType();
                    if (member instanceof VariableSymbol f) return f.getType();
                }
            }

            // Instance access: e.g. calc.result
            // If target is an instance type, resolve member type (field/method)
            if (targetType instanceof ClassType ct) {
                Symbol member = ct.getClassSymbol().resolve(get.field());
                if (member instanceof VariableSymbol v) return v.getType();
                if (member instanceof MethodSymbol m) return m.returnType();
            }

            error("Unknown member: " + get.field());
            return null;
        }

        // ───────────── Binary expressions ─────────────
        if (expr instanceof BinaryExpr bin) {
            Type left = getExprType(bin.getLeft());
            Type right = getExprType(bin.getRight());

            if (left != null && right != null) {
                switch (bin.getOperator()) {
                    case "+", "-", "*", "/" -> {
                        if (!left.equals(right)) {
                            error("Type mismatch in binary expression: " + left + " vs " + right);
                        }
                        return left; // result same as operands
                    }
                    case "==", "!=", "<", "<=", ">", ">=" -> {
                        return BuiltinType.BOOL;
                    }
                }
            }
            return left;
        }

        // Fallback
        error("Type inference not implemented for: " + expr.getClass().getSimpleName());
        return null;
    }

}
