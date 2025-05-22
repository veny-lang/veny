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

/**
 * Represents a continue statement in the AST.
 *
 * <p>This statement is used to skip the current iteration of a loop and continue with the next.
 */
public class ContinueStmt implements Statement {

    /**
     * Creates a new continue statement.
     *
     * @return a new ContinueStmt instance.
     */
    public static ContinueStmt of() {
        return new ContinueStmt();
    }

    /**
     * Accepts a visitor, dispatching to the visitor's method for continue statements.
     *
     * @param visitor The visitor to process this AST node.
     * @param <R>     The return type of the visitor.
     * @return The result of the visitor's processing.
     */
    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitContinueStmt(this);
    }

    /**
     * Returns the string representation of the continue statement.
     *
     * @return the string "continue"
     */
    @Override
    public String toString() {
        return "continue";
    }
}

