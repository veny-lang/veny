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

public class SrcFile {
  private final String name;
  private final int base;
  private final String content;

  public SrcFile(String name, int base, String content) {
    this.name = name;
    this.base = base;
    this.content = content;
  }

  public String name() {
    return name;
  }

  public int base() {
    return base;
  }

  public String content() {
    return content;
  }

  public Pos getPos(int offset) {
    if (offset < 0 || offset > content.length()) {
      throw new IllegalArgumentException("Offset out of bounds");
    }
    return new Pos(base + offset);
  }
  
}
