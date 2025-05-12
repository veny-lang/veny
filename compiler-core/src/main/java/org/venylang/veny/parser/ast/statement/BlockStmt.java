package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Statement;

import java.util.List;

public record BlockStmt(List<Statement> statements) implements Statement {

  @Override
  public <R> R accept(AstVisitor<R> visitor) {
    return visitor.visitBlockStmt(this);
  }

  @Override
  public String toString() {
    return "{ " + statements + " }";
  }
}
