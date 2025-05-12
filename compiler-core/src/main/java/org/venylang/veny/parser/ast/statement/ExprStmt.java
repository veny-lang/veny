package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;
import org.venylang.veny.parser.ast.Statement;

public record ExprStmt(Expression expression) implements Statement {

  @Override
  public <R> R accept(AstVisitor<R> visitor) {
    return visitor.visitExprStmt(this);
  }

  @Override
  public String toString() {
    return expression.toString() + ";";
  }
}
