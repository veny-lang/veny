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

package org.venylang.veny.codegen;

import org.venylang.veny.parser.ast.*;
import org.venylang.veny.parser.ast.expression.*;
import org.venylang.veny.parser.ast.statement.*;

public class JavaCodeGenerator implements AstVisitor<String> {
    @Override
    public String visitProgram(Program node) {
        return "";
    }

    @Override
    public String visitClassDecl(ClassDecl node) {
        return "";
    }

    @Override
    public String visitVarDecl(VarDecl node) {
        return "";
    }

    @Override
    public String visitMethodDecl(MethodDecl node) {
        return "";
    }

    @Override
    public String visitBlockStmt(BlockStmt node) {
        return "";
    }

    @Override
    public String visitIfStmt(IfStmt node) {
        return "";
    }

    @Override
    public String visitWhileStmt(WhileStmt node) {
        return "";
    }

    @Override
    public String visitForStmt(ForStmt node) {
        return "";
    }

    @Override
    public String visitReturnStmt(ReturnStmt node) {
        return "";
    }

    @Override
    public String visitExprStmt(ExprStmt node) {
        return "";
    }

    @Override
    public String visitVarStmt(VarStmt node) {
        return "";
    }

    @Override
    public String visitValStmt(ValStmt node) {
        return "";
    }

    @Override
    public String visitBinaryExpr(BinaryExpr node) {
        return "";
    }

    @Override
    public String visitUnaryExpr(UnaryExpr node) {
        return "";
    }

    @Override
    public String visitLiteralExpr(LiteralExpr node) {
        return "";
    }

    @Override
    public String visitVariableExpr(VariableExpr node) {
        return "";
    }

    @Override
    public String visitAssignExpr(AssignExpr node) {
        return "";
    }

    @Override
    public String visitCallExpr(CallExpr node) {
        return "";
    }

    @Override
    public String visitNewExpr(NewExpr node) {
        return "";
    }

    @Override
    public String visitGetExpr(GetExpr node) {
        return "";
    }

    @Override
    public String visitSetExpr(SetExpr node) {
        return "";
    }

    @Override
    public String visitBreakStmt(BreakStmt breakStmt) {
        return "";
    }

    @Override
    public String visitContinueStmt(ContinueStmt continueStmt) {
        return "";
    }
}
