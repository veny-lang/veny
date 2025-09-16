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

package org.venylang.veny.lexer;

import org.venylang.veny.util.source.Offset;
import org.venylang.veny.util.source.SrcFileSet;

/**
 * Represents a lexical token produced by the lexer.
 * <p>
 * A token consists of a type (e.g., identifier, keyword, literal), the matched lexeme
 * (i.e., the exact substring from the source), an optional parsed literal value,
 * and an {@link Offset} indicating its position within the source file.
 * <p>
 * Line and column information can be retrieved from the {@link SrcFileSet} using the offset.
 *
 * @param type    the type of the token (e.g., IDENTIFIER, NUMBER, TEXT_LITERAL)
 * @param lexeme  the exact text matched for this token
 * @param literal the parsed literal value (e.g., Integer, Double, String, Boolean, null),
 *                or {@code null} if not applicable
 * @param offset  the file-relative offset of the token’s starting character
 */
public record Token(TokenType type, String lexeme, Object literal, Offset offset) {

    @Override
    public String toString() {
        return type + " '" + lexeme + "'" +
                (literal != null ? " (literal=" + literal + ")" : "") +
                " (offset " + offset + ")";
    }
}

