package org.venylang.veny.parser.ast;

import org.venylang.veny.util.Visibility;

/**
 * @param typeName  nullable for type inference
 * @param initializer  can be null
 */
public record VarDecl(String name, String typeName, Expression initializer,
                      boolean isMutable, Visibility visibility) implements AstNode {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitVarDecl(this);
    }
}
