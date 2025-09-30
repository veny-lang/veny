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
    R visit(Program node);

    /**
     * Visits a single Veny source file.
     *
     * @param node the VenyFile node
     * @return result of visiting
     */
    R visit(VenyFile node);

    /**
     * Visits a class declaration.
     *
     * @param node the ClassDecl node
     * @return result of visiting
     */
    R visit(ClassDecl node);

    /**
     * Visits an interface declaration.
     *
     * @param node the InterfaceDecl node
     * @return result of visiting
     */
    R visit(InterfaceDecl node);

    /**
     * Visits a field or variable declaration in a class.
     *
     * @param node the VarDecl node
     * @return result of visiting
     */
    R visit(VarDecl node);

    /**
     * Visits a method declaration in a class.
     *
     * @param node the MethodDecl node
     * @return result of visiting
     */
    R visit(MethodDecl node);

    // Statements

    R visit(BlockStmt node);
    R visit(IfStmt node);
    R visit(WhileStmt node);
    R visit(ForStmt node);
    R visit(ReturnStmt node);
    R visit(ExprStmt node);
    R visit(VarStmt node);
    R visit(ValStmt node);
    R visit(BreakStmt node);
    R visit(ContinueStmt node);

    // Expressions

    R visit(BinaryExpr node);
    R visit(UnaryExpr node);
    R visit(LiteralExpr node);
    R visit(VariableExpr node);
    R visit(AssignExpr node);
    R visit(CallExpr node);
    R visit(NewExpr node);
    R visit(GetExpr node);
    R visit(SetExpr node);
    R visit(ArrayLiteralExpr node);
    R visit(IndexExpr node);
}
