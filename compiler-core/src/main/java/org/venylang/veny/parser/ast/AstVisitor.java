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
