package org.venylang.veny.parser.ast.expression;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;

public record SetExpr(Expression target, String field, Expression value) implements Expression {

    /**
     * Constructs a field assignment expression (e.g., obj.field = value).
     *
     * @param target The object whose field is being assigned (e.g., VariableExpr("user")).
     * @param field  The field name (e.g., "age").
     * @param value  The value to assign to the field.
     */
    public SetExpr {
    }

    /**
     * @return the expression representing the object (left-hand side before the dot).
     */
    @Override public Expression target() {
        return target;
    }

    /**
     * @return the field name being assigned.
     */
    @Override public String field() {
        return field;
    }

    /**
     * @return the value being assigned to the field.
     */
    @Override public Expression value() {
        return value;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitSetExpr(this);
    }

    @Override
    public String toString() {
        return target + "." + field + " = " + value;
    }
}
