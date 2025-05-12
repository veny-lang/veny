package org.venylang.veny.parser.ast.expression;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;

import java.util.List;

public record NewExpr(String className, List<Expression> arguments) implements Expression {

    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitNewExpr(this);
    }
}
