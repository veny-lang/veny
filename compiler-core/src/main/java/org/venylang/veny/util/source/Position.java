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
 * {@code Position} describes a resolved source position, including the file name, line number, and column number.
 *
 * <p>A {@code Position} is typically derived from an {@code Offset} using a {@code SrcFileSet}.
 * It is considered valid if the {@code line} number is greater than zero.
 *
 * <p>{@code Position} is used to present human-readable diagnostic information (e.g. in error messages).
 *
 * @param fileName the name of the file (may be empty if unknown)
 * @param line     the line number in the file (1-based); 0 means invalid
 * @param column   the column number in the file (1-based); 0 means invalid
 */
public record Position(String fileName, int line, int column) {

  /**
   * Represents an invalid or no position.
   * Line = 0 means invalid position by definition.
   */
  public static final Position NO_POSITION = new Position("", 0, 0);

  /**
   * Checks whether this {@code Position} is valid.
   * A position is valid if both {@code line} and {@code column} are greater than 0.
   * This ensures the position refers to a real, addressable character in a source file.
   *
   * @return {@code true} if both line and column are > 0; {@code false} otherwise
   */
  public boolean isValid() {
    return line > 0 && column > 0;
  }

  /**
   * Returns a human-readable string representation of this position.
   * The format varies based on what information is available:
   * <ul>
   *   <li>{@code file:line:column} — valid position with file name</li>
   *   <li>{@code line:column} — valid position without file name</li>
   *   <li>{@code file} — invalid position with file name</li>
   *   <li>{@code -} — invalid position without file name</li>
   * </ul>
   *
   * @return the string representation of this position
   */
  @Override
  public String toString() {
    String s = fileName;
    if (isValid()) {
      if (!s.isEmpty()) {
        s += ":";
      }
      s += String.format("%d:%d", line, column);
    } else if (s.isEmpty()) {
      s = "-";
    }
    return s;
  }
}
