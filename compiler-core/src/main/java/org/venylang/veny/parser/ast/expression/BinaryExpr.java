package org.venylang.veny.parser.ast.expression;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;

/**
 * @param operator  e.g., "+", "=="
 */
public record BinaryExpr(Expression left, String operator, Expression right) implements Expression {

    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitBinaryExpr(this);
    }

    @Override
    public String toString() {
        return "BinaryExpr(" + left + " " + operator + " " + right + ")";
    }

}
