package org.venylang.veny.parser.ast;

import java.util.List;

public record Program(String packageName, List<String> imports, List<ClassDecl> classes) implements AstNode {
  @Override
  public <R> R accept(AstVisitor<R> visitor) {
    return visitor.visitProgram(this);
  }
}
