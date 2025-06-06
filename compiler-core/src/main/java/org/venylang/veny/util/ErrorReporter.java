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

package org.venylang.veny.util;

import org.venylang.veny.lexer.Token;
import java.util.ArrayList;
import java.util.List;

public class ErrorReporter {

    public static class Diagnostic {
        public final String message;
        public final String file;
        public final int line;
        public final int column;

        public Diagnostic(String message, String file, int line, int column) {
            this.message = message;
            this.file = file;
            this.line = line;
            this.column = column;
        }

        @Override
        public String toString() {
            return String.format("%s:%d:%d: %s", file, line, column, message);
        }
    }

    private final List<Diagnostic> diagnostics = new ArrayList<>();

    public void report(String file, int line, int column, String message) {
        diagnostics.add(new Diagnostic(message, file, line, column));
    }

    public void report(Token token) {
        //TODO report(token.getFilename(), token.getLine(), token.getColumn(), token.getLexeme());
    }

    public boolean hasErrors() {
        return !diagnostics.isEmpty();
    }

    public int errorCount() {
        return diagnostics.size();
    }

    public void printAll() {
        for (Diagnostic diag : diagnostics) {
            System.err.println("[Error] " + diag);
        }
        if (!diagnostics.isEmpty()) {
            System.err.printf("\n%d error%s found.\n", diagnostics.size(), diagnostics.size() > 1 ? "s" : "");
        }
    }

    public void clear() {
        diagnostics.clear();
    }

    public List<Diagnostic> getAllDiagnostics() {
        return diagnostics;
    }
}
