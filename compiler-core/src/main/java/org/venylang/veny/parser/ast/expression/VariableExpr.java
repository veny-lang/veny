package org.venylang.veny.parser.ast.expression;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;

public record VariableExpr(String name) implements Expression {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return null;
    }

    @Override
    public String toString() {
        return "VariableExpr(" + name + ")";
    }

}
