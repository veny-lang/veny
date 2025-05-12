package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;
import org.venylang.veny.parser.ast.Statement;

public record ForStmt(String variable, Expression iterable, BlockStmt body) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitForStmt(this);
    }

    @Override
    public String toString() {
        return "for " + variable + " in " + iterable + " " + body;
    }
}