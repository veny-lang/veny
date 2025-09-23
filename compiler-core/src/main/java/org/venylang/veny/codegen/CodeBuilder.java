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

package org.venylang.veny.codegen;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to help build formatted code strings with indentation support.
 * Useful for generating code, structured text, or templates with consistent formatting.
 *
 * <p>Example usage:
 * <pre>{@code
 * CodeBuilder builder = new CodeBuilder();
 * String code = builder
 *     .appendLine("public class HelloWorld {")
 *     .indent()
 *     .appendLine("public static void main(String[] args) {")
 *     .indent()
 *     .appendLine("System.out.println(\"Hello, world!\");")
 *     .unindent()
 *     .appendLine("}")
 *     .unindent()
 *     .appendLine("}")
 *     .build();
 *
 * System.out.println(code);
 * }</pre>
 */
public class CodeBuilder {
    private final List<String> lines = new ArrayList<>();
    private final String indentUnit = "    "; // 4 spaces
    private int indentLevel = 0;

    /**
     * Increases the current indentation level by one.
     *
     * @return the current CodeBuilder instance for method chaining
     */
    public CodeBuilder indent() {
        indentLevel++;
        return this;
    }

    /**
     * Decreases the current indentation level by one, if greater than zero.
     *
     * @return the current CodeBuilder instance for method chaining
     */
    public CodeBuilder unindent() {
        if (indentLevel > 0) {
            indentLevel--;
        }
        return this;
    }

    /**
     * Appends a line to the builder with the current indentation applied.
     *
     * @param line the line of text to append
     * @return the current CodeBuilder instance for method chaining
     */
    public CodeBuilder appendLine(String line) {
        lines.add(getIndent() + line);
        return this;
    }

    /**
     * Appends a line to the builder without applying any indentation.
     *
     * @param line the raw line of text to append
     * @return the current CodeBuilder instance for method chaining
     */
    public CodeBuilder appendRawLine(String line) {
        lines.add(line);
        return this;
    }

    /**
     * Builds the final string by joining all lines with newline characters.
     *
     * @return the complete formatted string
     */
    public String build() {
        return String.join("\n", lines);
    }

  /**
   * Inserts the given text immediately after the package declaration.
   * If no package is present, inserts at the top of the file.
   * Adds a blank line after the inserted text if a package exists.
   *
   * @param text the line to insert (without indentation)
   * @return the current CodeBuilder instance for method chaining
   */
  public CodeBuilder insertAfterPackage(String text) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("package ")) {
                // insert after package
                lines.add(i + 1, "");
                lines.add(i + 2, text);
                // add a single blank line after inserted imports
                lines.add(i + 3, "");
                return this;
            }
        }
        // no package → insert at top
        lines.add(0, text);
        return this;
    }

    /**
     * Inserts multiple lines immediately after the package declaration.
     */
    public CodeBuilder insertAfterPackage(List<String> newLines) {
        int insertIndex = 0;
        boolean hasPackage = false;

        for (int i = 0; i < lines.size(); i++) {
            String l = lines.get(i).trim();
            if (l.startsWith("package ")) {
                insertIndex = i + 1;
                hasPackage = true;
                break;
            }
        }

        if (hasPackage) {
            lines.add(insertIndex + newLines.size(), "");
            insertIndex++;
        }

        // insert the new lines
        lines.addAll(insertIndex, newLines);

        // if package exists, add a single blank line after imports
        if (hasPackage) {
            lines.add(insertIndex + newLines.size(), "");
        }

        return this;
    }

    /**
     * Returns a string of spaces corresponding to the current indentation level.
     *
     * @return the indentation string
     */
    private String getIndent() {
        return indentUnit.repeat(indentLevel);
    }
}