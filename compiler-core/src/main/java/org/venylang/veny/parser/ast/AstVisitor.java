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

package org.venylang.veny.parser.ast;

import org.venylang.veny.parser.ast.expression.*;
import org.venylang.veny.parser.ast.statement.*;

/**
 * Represents a visitor for traversing or processing the Abstract Syntax Tree (AST)
 * of the Veny language using the Visitor design pattern.
 * <p>
 * Each method corresponds to a specific AST node type. Implementations of this interface
 * define how each node is handled, often used for operations such as interpretation,
 * compilation, type checking, or pretty-printing.
 *
 * @param <R> The return type of each visitor method (e.g., {@code Void} if no return value is needed).
 */
public interface AstVisitor<R> {

    // Top-level structures

    /**
     * Visits a program root node.
     *
     * @param node the Program node
     * @return result of visiting
     */
    R visitProgram(Program node);

    /**
     * Visits a single Veny source file.
     *
     * @param node the VenyFile node
     * @return result of visiting
     */
    R visitVenyFile(VenyFile node);

    /**
     * Visits a class declaration.
     *
     * @param node the ClassDecl node
     * @return result of visiting
     */
    R visitClassDecl(ClassDecl node);

    /**
     * Visits an interface declaration.
     *
     * @param node the InterfaceDecl node
     * @return result of visiting
     */
    R visitInterfaceDecl(InterfaceDecl node);

    /**
     * Visits a field or variable declaration in a class.
     *
     * @param node the VarDecl node
     * @return result of visiting
     */
    R visitVarDecl(VarDecl node);

    /**
     * Visits a method declaration in a class.
     *
     * @param node the MethodDecl node
     * @return result of visiting
     */
    R visitMethodDecl(MethodDecl node);

    // Statements

    R visitBlockStmt(BlockStmt node);
    R visitIfStmt(IfStmt node);
    R visitWhileStmt(WhileStmt node);
    R visitForStmt(ForStmt node);
    R visitReturnStmt(ReturnStmt node);
    R visitExprStmt(ExprStmt node);
    R visitVarStmt(VarStmt node);
    R visitValStmt(ValStmt node);
    R visitBreakStmt(BreakStmt node);
    R visitContinueStmt(ContinueStmt node);

    // Expressions

    R visitBinaryExpr(BinaryExpr node);
    R visitUnaryExpr(UnaryExpr node);
    R visitLiteralExpr(LiteralExpr node);
    R visitVariableExpr(VariableExpr node);
    R visitAssignExpr(AssignExpr node);
    R visitCallExpr(CallExpr node);
    R visitNewExpr(NewExpr node);
    R visitGetExpr(GetExpr node);
    R visitSetExpr(SetExpr node);
    R visitArrayLiteralExpr(ArrayLiteralExpr node);
}
