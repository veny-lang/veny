package org.venylang.veny.parser.ast;

import java.util.List;

public record ClassDecl(String name, List<VarDecl> fields, List<MethodDecl> methods) implements AstNode {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitClassDecl(this);
    }
}
