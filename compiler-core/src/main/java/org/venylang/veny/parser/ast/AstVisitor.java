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

public interface AstVisitor<R> {
    R visitProgram(Program node);
    R visitClassDecl(ClassDecl node);
    R visitVarDecl(VarDecl node);
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
    R visitBreakStmt(BreakStmt breakStmt);
    R visitContinueStmt(ContinueStmt continueStmt);
}
