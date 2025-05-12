package org.venylang.veny.parser.ast.expression;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;

/**
 * Represents an assignment expression in the AST.
 *
 * Examples:
 * <pre>
 *   x = 42
 *   name = "Alice"
 *   flag = true
 *   radius = radius + 1.0
 * </pre>
 *
 * This class handles only simple variable assignments (not object field or array element assignments).
 * For object fields (e.g., obj.field = value), use {@link SetExpr}.
 */
public record AssignExpr(String name, Expression value) implements Expression {

    /**
     * Constructs an assignment expression.
     *
     * @param name  The variable name being assigned to.
     * @param value The expression representing the assigned value.
     */
    public AssignExpr {
    }

    /**
     * Gets the variable name.
     *
     * @return the name of the variable.
     */
    @Override public String name() {
        return name;
    }

    /**
     * Gets the assigned value expression.
     *
     * @return the expression on the right-hand side of the assignment.
     */
    @Override public Expression value() {
        return value;
    }


    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitAssignExpr(this);
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }
}
