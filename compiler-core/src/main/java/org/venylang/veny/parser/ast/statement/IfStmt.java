package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;
import org.venylang.veny.parser.ast.Statement;

/**
 * @param elseBranch  may be null */
public record IfStmt(Expression condition, Statement thenBranch, Statement elseBranch) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitIfStmt(this);
    }

    @Override
    public String toString() {
        return "if (" + condition + ") " + thenBranch +
                (elseBranch != null ? " else " + elseBranch : "");
    }
}
