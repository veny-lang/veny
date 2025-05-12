package org.venylang.veny.parser.ast.expression;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;

/**
 * Represents accessing a field or property from an object in the AST.
 *
 * Examples:
 * <pre>
 *   this.name        // get field "name" from 'this'
 *   user.age         // get field "age" from variable 'user'
 *   config.debug     // get field "debug" from variable 'config'
 * </pre>
 *
 * This is a read operation, not an assignment.
 */
public class GetExpr implements Expression {

    private final Expression target;
    private final String field;

    /**
     * Constructs a field access expression (e.g., obj.field).
     *
     * @param target The expression evaluating to the object (e.g., VariableExpr("user")).
     * @param field  The field name being accessed (e.g., "age").
     */
    public GetExpr(Expression target, String field) {
        this.target = target;
        this.field = field;
    }

    /**
     * @return the expression representing the object.
     */
    public Expression target() {
        return target;
    }

    /**
     * @return the name of the field being accessed.
     */
    public String field() {
        return field;
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitGetExpr(this);
    }

    @Override
    public String toString() {
        return target + "." + field;
    }
}
