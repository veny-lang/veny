package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;
import org.venylang.veny.parser.ast.Statement;

public record ReturnStmt(Expression value) implements Statement {

    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitReturnStmt(this);
    }

    @Override
    public String toString() {
        return "return " + value;
    }
}
