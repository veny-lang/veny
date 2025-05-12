package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;
import org.venylang.veny.parser.ast.Statement;

/**
 * @param type  Optional, depending on language rules */
public record VarStmt(String name, String type, Expression initializer) implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitVarStmt(this);
    }

    @Override
    public String toString() {
        return "var " + name + ": " + type + (initializer != null ? " = " + initializer : "");
    }
}
