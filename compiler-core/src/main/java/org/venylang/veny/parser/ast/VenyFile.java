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
 * Represents a single Veny source file (.veny) within a compilation unit.
 * A Veny file must declare its package and can contain multiple class and interface declarations.
 *
 * This class is part of the AST and participates in semantic analysis and code generation.
 */
public record VenyFile(
        String packageName,
        List<String> imports,
        List<ClassDecl> classes,
        List<InterfaceDecl> interfaces
) implements AstNode {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "VenyFile{" +
                "package='" + packageName + '\'' +
                ", imports=" + imports +
                ", classes=" + classes +
                ", interfaces=" + interfaces +
                '}';
    }
}
