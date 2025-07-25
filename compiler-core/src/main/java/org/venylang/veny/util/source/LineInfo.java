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

package org.venylang.veny.util.source;

/**
 * Represents information about a specific line in a file.
 * <p>
 * This record encapsulates the file name, the character offset from the beginning of the file,
 * and the line number where a particular event or token occurs.
 * </p>
 *
 * @param fileName the name of the file
 * @param offset   the character offset from the beginning of the file
 * @param line     the line number in the file (starting from 1)
 */
public record LineInfo(String fileName, int offset, int line) {
}
