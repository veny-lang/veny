package org.venylang.veny.semantic;

import org.venylang.veny.lexer.Lexer;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.RecursiveDescentParser;
import org.venylang.veny.parser.ast.Program;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SemanticAnalyzerTest {

    private void analyze(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        RecursiveDescentParser parser = new RecursiveDescentParser(tokens);
        Program program = parser.parse();
        new SemanticAnalyzer().visitProgram(program);
    }

    @Test
    void duplicateClassNameShouldFail() {
        String source = """
            class Foo {}
            class Foo {}
        """;
        assertThrows(SemanticException.class, () -> analyze(source));
    }

    @Test
    void duplicateVariableInSameScopeShouldFail() {
        String source = """
            class Test {
                var x: Int = 1
                var x: Int = 2
            }
        """;
        assertThrows(SemanticException.class, () -> analyze(source));
    }

    @Test
    void variableShadowingInInnerScopeShouldPass() {
        String source = """
            class Test {
                var x: Int = 1
                shadow(): void {
                    var x: Int = 2
                }
            }
        """;
        assertDoesNotThrow(() -> analyze(source));
    }

    @Test
    void uniqueClassNamesShouldPass() {
        String source = """
            class Alpha {}
            class Beta {}
        """;
        assertDoesNotThrow(() -> analyze(source));
    }

    @Test
    void duplicateMethodInClassShouldPassTemporarily() {
        // Overloading isn't handled yet, so allow for now unless restricted
        String source = """
            class Service {
                foo(): void {}
                foo(): void {}
            }
        """;
        assertDoesNotThrow(() -> analyze(source)); // Adjust based on policy
    }
}