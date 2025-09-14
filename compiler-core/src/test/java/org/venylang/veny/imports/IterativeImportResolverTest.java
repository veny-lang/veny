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

package org.venylang.veny.imports;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.venylang.veny.semantic.Symbol;
import org.venylang.veny.semantic.symbols.GlobalScope;
import org.venylang.veny.util.Visibility;

/**
 * Unit tests for {@link IterativeImportResolver}.
 * <p>
 * These tests use the default Java file system with a temporary directory.
 * The heavy compiler operations are stubbed via a test double – {@link TestableResolver} –
 * which overrides the compile/analyse steps.
 */
class IterativeImportResolverTest {

    @TempDir
    Path srcRoot;
    private GlobalScope globalScope;

    @BeforeEach
    void setUp() {
    globalScope = new GlobalScope();
    }

    /**
     * Provides a resolver instance whose expensive phases are stubbed out so the
     * tests focus solely on import traversal and bookkeeping.
     */
    private IterativeImportResolver newResolver() {
        return new TestableResolver(srcRoot, globalScope);
    }

    //@Test TODO
    void lazyPackageIndex_buildsIndexOnDemand() throws Exception {
        // Arrange – create two classes in package veny.lang
        Path pkgDir = Files.createDirectories(srcRoot.resolve("veny/lang"));
        Files.writeString(pkgDir.resolve("console.veny"), "package veny.lang\nclass Console {}\n");
        Files.writeString(pkgDir.resolve("string.veny"),  "package veny.lang\nclass String  {}\n");

        ImportRecord console = new ImportRecord("veny.lang", "Console");
        IterativeImportResolver resolver = newResolver();

        // Act
        resolver.resolveImports(List.of(console));

        // Assert – both classes should now be in the global symbol table
        assertNotNull(globalScope.resolve("veny.lang.Console"));
        assertNotNull(globalScope.resolve("veny.lang.String"));
    }

    //@Test TODO
    void circularImport_throwsCircularImportException() throws Exception {
        Path aDir = Files.createDirectories(srcRoot.resolve("test").resolve("a"));
        Path bDir = Files.createDirectories(srcRoot.resolve("test").resolve("b"));
        Files.writeString(aDir.resolve("a.veny"), "package test.a\nimport b.B\n class A {}\n");
        Files.writeString(bDir.resolve("b.veny"), "package test.b\nimport a.A\n class B {}\n");

        IterativeImportResolver resolver = newResolver();
        ImportRecord a = new ImportRecord("test.a", "A");

        assertThrows(CircularImportException.class, () -> resolver.resolveImports(List.of(a)));
    }

    @Test
    void duplicateClass_throwsImportResolutionException() throws Exception {
        Path pkgDir = Files.createDirectories(srcRoot.resolve("dupe"));
        Files.writeString(pkgDir.resolve("Dupe.veny"), "class Dupe {}\n");
        Files.writeString(pkgDir.resolve("DupeAgain.veny"), "class Dupe {}\n");

        IterativeImportResolver resolver = newResolver();
        ImportRecord dupe1 = new ImportRecord("dupe", "Dupe");

        assertThrows(ImportResolutionException.class, () -> resolver.resolveImports(List.of(dupe1)));
    }

    @Test
    void missingClass_throwsImportResolutionException() {
        IterativeImportResolver resolver = newResolver();
        ImportRecord missing = new ImportRecord("foo.bar", "DoesNotExist");

        assertThrows(ImportResolutionException.class, () -> resolver.resolveImports(List.of(missing)));
    }

    private static class TestableResolver extends IterativeImportResolver {

        TestableResolver(Path sourceRoot, GlobalScope globalScope) {

            super(globalScope);
        }

        protected AstNode compileFileToAST(Path filePath) {
            try {
                String content = Files.readString(filePath);
                return new AstNode(content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        protected List<ImportRecord> extractImports(AstNode ast) {
            String[] lines = ast.source().split("\\R");
            return java.util.Arrays.stream(lines)
                    .filter(l -> l.startsWith("import "))
                    .map(l -> l.substring("import ".length(), l.length() - 1))
                    .map(fqcn -> {
                        int dot = fqcn.lastIndexOf('.');
                        return new ImportRecord(fqcn.substring(0, dot), fqcn.substring(dot + 1));
                    })
                    .toList();
        }

        protected ClassSymbol analyzeAndExtractSymbols(AstNode ast, ImportRecord imp) {
            return new ClassSymbol(imp.packageName() + "." + imp.className());
        }
    }

    /*private static class SymbolTable {
        private final Map<String, ClassSymbol> map = new HashMap<>();
        ClassSymbol resolve(String fqcn) { return map.get(fqcn); }
        void define(String fqcn, ClassSymbol sym) { map.put(fqcn, sym); }
        boolean contains(String fqcn) { return map.containsKey(fqcn); }
    }*/

    private static class ClassSymbol extends Symbol {
        public ClassSymbol(String fqcn) {
            super(fqcn, Visibility.PUBLIC); // Use whatever makes sense (PUBLIC or PRIVATE)
        }
    }

    private static class AstNode {
        private final String source;
        AstNode(String source) { this.source = source; }
        String source() { return source; }
    }
}
