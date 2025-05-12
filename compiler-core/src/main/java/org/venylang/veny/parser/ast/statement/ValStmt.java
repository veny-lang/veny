package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;
import org.venylang.veny.parser.ast.Statement;

public record ValStmt(String name, String type, Expression initializer) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitValStmt(this);
    }

    @Override
    public String toString() {
        return "val " + name + ": " + type + (initializer != null ? " = " + initializer : "");
    }
}
