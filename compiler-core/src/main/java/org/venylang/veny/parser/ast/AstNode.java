package org.venylang.veny.parser.ast;

public interface AstNode {
    <R> R accept(AstVisitor<R> visitor);
}
