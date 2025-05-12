package org.venylang.veny.parser.ast;

import org.venylang.veny.util.Visibility;

import java.util.List;

/**
 * @param returnType  nullable for void
 */
public record MethodDecl(String name, List<Parameter> parameters, String returnType, List<Statement> body,
                         Visibility visibility) implements AstNode {

    public record Parameter(String name, String type) {
    }

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitMethodDecl(this);
    }
}
