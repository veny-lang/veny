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
import org.venylang.veny.parser.ast.Statement;

import java.util.List;

import java.util.Objects;

/**
 * Represents a block of statements enclosed in braces.
 *
 * <p>Example:
 * <pre>{@code
 * {
 *   val x = 10
 *   System.print(x)
 * }
 * }</pre>
 */
public record BlockStmt(List<Statement> statements) implements Statement {

  /**
   * Creates a new block statement with a list of statements.
   *
   * @param statements The list of statements in the block.
   * @return a new BlockStmt instance.
   * @throws NullPointerException if statements is null.
   */
  public static BlockStmt of(List<Statement> statements) {
    Objects.requireNonNull(statements, "statements must not be null");
    return new BlockStmt(statements);
  }

  /**
   * Accepts a visitor, dispatching to the visitor's method for block statements.
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
   * Returns a string representation of this block statement.
   *
   * @return a string in the format "{ [stmt1, stmt2, ...] }"
   */
  @Override
  public String toString() {
    return "{ " + statements + " }";
  }
}

