package org.venylang.veny.parser.ast.expression;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;

import java.util.List;
import java.util.Objects;

/**
 * Represents a function or method call expression in the AST.
 * The callee can be either a simple function name (as a {@link VariableExpr})
 * or an expression that evaluates to an object or callable (e.g., {@link GetExpr} for obj.method()).
 *
 * <p>Examples:
 * <ul>
 *   <li><code>print("Hello")</code> –&gt; callee is VariableExpr("print")</li>
 *   <li><code>math.sqrt(9)</code> –&gt; callee is GetExpr(VariableExpr("math"), "sqrt")</li>
 * </ul>
 */
public record CallExpr(Expression callee, List<Expression> arguments) implements Expression {

    public CallExpr {
        Objects.requireNonNull(callee, "callee must not be null");
        Objects.requireNonNull(arguments, "arguments must not be null");
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitCallExpr(this);
    }

    @Override
    public String toString() {
        return callee + "(" + arguments + ")";
    }
}
