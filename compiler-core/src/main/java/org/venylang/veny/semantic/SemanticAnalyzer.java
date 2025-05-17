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
import org.venylang.veny.semantic.symbols.ClassSymbol;
import org.venylang.veny.semantic.symbols.GlobalScope;
import org.venylang.veny.semantic.symbols.MethodSymbol;
import org.venylang.veny.semantic.symbols.VariableSymbol;
import org.venylang.veny.semantic.types.BuiltinType;
import org.venylang.veny.semantic.types.ClassType;
import org.venylang.veny.semantic.types.TypeResolver;
import org.venylang.veny.util.Visibility;

import java.util.*;

/**
 * Performs semantic analysis on Veny AST nodes.
 * Builds symbol tables, enforces scoping and type rules,
 * and collects semantic errors.
 */
public class SemanticAnalyzer implements AstVisitor<Void> {

    private final Deque<Scope> scopeStack = new ArrayDeque<>();
    private final List<String> errors = new ArrayList<>();
    private final GlobalScope globalScope = new GlobalScope();

    public List<String> getErrors() {
        return errors;
    }

    private Scope currentScope() {
        return scopeStack.peek();
    }

    private void enterScope(Scope scope) {
        scopeStack.push(scope);
    }

    private void exitScope() {
        scopeStack.pop();
    }

    private void error(String message) {
        errors.add(message);
    }

    private Void unsupported(AstNode node) {
        error("Semantic analysis not implemented for: " + node.getClass().getSimpleName());
        return null;
    }

    // === Declarations ===

    @Override
    public Void visitProgram(Program node) {
        enterScope(globalScope);

        for (VenyFile file : node.files()) {
            file.accept(this);
        }

        exitScope();
        return null;
    }

    @Override
    public Void visitVenyFile(VenyFile node) {
        for (ClassDecl cls : node.classes()) {
            cls.accept(this);
        }
        return null;
    }

    @Override
    public Void visitClassDecl(ClassDecl node) {
        if (currentScope() != globalScope) {
            throw new IllegalStateException("Classes must be declared in the global scope.");
        }

        if (globalScope.resolveLocal(node.name()) != null) {
            throw new SemanticException("Class '" + node.name() + "' is already defined.");
        }

        ClassSymbol classSymbol = new ClassSymbol(node.name(), globalScope);
        globalScope.define(classSymbol);

        enterScope(classSymbol);

        for (VarDecl field : node.fields()) {
            field.accept(this);
        }

        for (MethodDecl method : node.methods()) {
            method.accept(this);
        }

        exitScope();
        return null;
    }

    @Override
    public Void visitVarDecl(VarDecl node) {
        Scope scope = currentScope();
        if (currentScope().resolveLocal(node.name()) != null) {
            throw new SemanticException("Variable '" + node.name() + "' is already declared in this scope.");
        }

        Type type = resolveType(node.typeName());
        VariableSymbol var = new VariableSymbol(
                node.name(),
                type,
                node.visibility(),
                false, //TODO node.isParameter,
                true //TODO node.isVar
        );
        currentScope().define(var);

        if (node.initializer() != null) {
            node.initializer().accept(this);
        }

        return null;
    }

    @Override
    public Void visitMethodDecl(MethodDecl node) {
        Type returnType = resolveType(node.returnType());
        MethodSymbol method = new MethodSymbol(
                node.name(),
                returnType,
                node.visibility() == Visibility.PUBLIC ? Visibility.PUBLIC : Visibility.PRIVATE,
                currentScope()
        );
        currentScope().define(method);
        enterScope(method);

        for (MethodDecl.Parameter param : node.parameters()) {
            //TODO
            //param.isParameter = true;
            //param.accept(this);
        }

        for (Statement stmt : node.body()) {
            stmt.accept(this); // BlockStmt
        }

        exitScope();
        return null;
    }

    // === Statements ===

    @Override
    public Void visitBlockStmt(BlockStmt node) {
        for (Statement stmt : node.statements()) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visitIfStmt(IfStmt node) {
        node.condition().accept(this);
        node.thenBranch().accept(this);
        if (node.elseBranch() != null) {
            node.elseBranch().accept(this);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(WhileStmt node) {
        node.condition().accept(this);
        node.body().accept(this);
        return null;
    }

    @Override
    public Void visitForStmt(ForStmt node) {
        //node.init.accept(this);
        node.iterable().accept(this);
        //node.update().accept(this);
        node.body().accept(this);
        return null;
    }

    @Override
    public Void visitReturnStmt(ReturnStmt node) {
        if (node.value() != null) {
            node.value().accept(this);
        }
        return null;
    }

    @Override
    public Void visitExprStmt(ExprStmt node) {
        node.expression().accept(this);
        return null;
    }

    @Override
    public Void visitVarStmt(VarStmt node) {
        return node.initializer().accept(this);
    }

    @Override
    public Void visitValStmt(ValStmt node) {
        return node.initializer().accept(this);
    }

    @Override
    public Void visitBreakStmt(BreakStmt node) {
        // Optional: validate we are inside a loop
        return null;
    }

    @Override
    public Void visitContinueStmt(ContinueStmt node) {
        // Optional: validate we are inside a loop
        return null;
    }

    // === Expressions ===

    @Override
    public Void visitBinaryExpr(BinaryExpr node) {
        node.left().accept(this);
        node.right().accept(this);
        return null;
    }

    @Override
    public Void visitUnaryExpr(UnaryExpr node) {
        node.operand().accept(this);
        return null;
    }

    @Override
    public Void visitLiteralExpr(LiteralExpr node) {
        return null;  // Nothing to check
    }

    @Override
    public Void visitVariableExpr(VariableExpr node) {
        Symbol symbol = currentScope().resolve(node.name());
        if (symbol == null) {
            error("Undefined variable: " + node.name());
        } else {
            //TODO node.symbol = symbol;
        }
        return null;
    }

    @Override
    public Void visitAssignExpr(AssignExpr node) {
        //TODO
        //node.target().accept(this);
        //node.value.accept(this);
        return null;
    }

    @Override
    public Void visitCallExpr(CallExpr node) {
        node.callee().accept(this);
        for (Expression arg : node.arguments()) {
            arg.accept(this);
        }
        return null;
    }

    @Override
    public Void visitNewExpr(NewExpr node) {
        // Type/class constructor check
        return null;
    }

    @Override
    public Void visitGetExpr(GetExpr node) {
        node.target().accept(this);
        return null;
    }

    @Override
    public Void visitSetExpr(SetExpr node) {
        node.target().accept(this);
        node.value().accept(this);
        return null;
    }

    // === Type resolution helper ===

    private Type resolveType(String typeName) {
        BuiltinType builtin = TypeResolver.resolveBuiltin(typeName);
        if (builtin != null) return builtin;

        Symbol sym = currentScope().resolve(typeName);
        if (sym instanceof ClassSymbol clsSym) {
            return new ClassType(clsSym);
        }

        error("Unknown type: " + typeName);
        return null;
    }
}
