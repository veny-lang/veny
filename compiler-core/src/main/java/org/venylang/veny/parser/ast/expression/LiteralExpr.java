package org.venylang.veny.parser.ast.expression;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;

public class LiteralExpr implements Expression {

    private final Object value;

    public LiteralExpr(Object value) {
        this.value = value;
    }

    public Object value() {
        return value;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitLiteralExpr(this);
    }

    @Override
    public String toString() {
        return "LiteralExpr(" + value + ")";
    }

}
