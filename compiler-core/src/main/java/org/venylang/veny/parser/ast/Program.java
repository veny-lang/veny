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

package org.venylang.veny.parser.ast;

import java.util.List;

/**
 * Represents the full compilation unit for a Veny program.
 *
 * A program consists of one or more Veny source files (`VenyFile`), each
 * with its own package declaration and class definitions. This structure
 * allows multi-file, multi-package projects to be compiled as a single unit.
 */
public record Program(List<VenyFile> files) implements AstNode {

  /**
   * Creates a Program from a list of VenyFile instances.
   */
  public static Program of(List<VenyFile> files) {
    return new Program(List.copyOf(files));
  }

  /**
   * Creates a Program from one or more VenyFile instances (varargs).
   */
  public static Program of(VenyFile... files) {
    return new Program(List.of(files));
  }

  @Override
  public <R> R accept(AstVisitor<R> visitor) {
    return visitor.visitProgram(this);
  }

  @Override
  public String toString() {
    return "Program{" +
            "files=" + files +
            '}';
  }
}
