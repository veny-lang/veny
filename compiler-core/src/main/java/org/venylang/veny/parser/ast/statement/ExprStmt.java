/*
 * Copyright 2025 Stoyan Petkov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Expression;
import org.venylang.veny.parser.ast.Statement;

import java.util.Objects;

/**
 * Represents a statement consisting of a single expression.
 *
 * <p>Example:
 * <pre>{@code
 *   foo();
 *   x + y;
 * }</pre>
 */
public record ExprStmt(Expression expression) implements Statement {

  /**
   * Creates a new expression statement.
   *
   * @param expression The expression contained in this statement.
   * @return a new ExprStmt instance.
   * @throws NullPointerException if expression is null.
   */
  public static ExprStmt of(Expression expression) {
    Objects.requireNonNull(expression, "expression must not be null");
    return new ExprStmt(expression);
  }

  /**
   * Accepts a visitor, dispatching to the visitor's method for expression statements.
   *
   * @param visitor The visitor to process this AST node.
   * @param <R>     The return type of the visitor.
   * @return The result of the visitor's processing.
   */
  @Override
  public <R> R accept(AstVisitor<R> visitor) {
    return visitor.visit(this);
  }

  /**
   * Returns a string representation of this expression statement.
   *
   * @return a string with the expression followed by a semicolon.
   */
  @Override
  public String toString() {
    return expression.toString() + ";";
  }
}

