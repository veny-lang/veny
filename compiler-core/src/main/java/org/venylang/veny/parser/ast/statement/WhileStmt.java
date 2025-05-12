package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;
import org.venylang.veny.parser.ast.Statement;

public record WhileStmt(Expression condition, BlockStmt body) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitWhileStmt(this);
    }

    @Override
    public String toString() {
        return "while (" + condition + ") " + body;
    }
}
