package org.venylang.veny.lexer;

/**
*
*/
public record Token(TokenType type, String lexeme, int line) {

    public String toString() {
        return type + " '" + lexeme + "' (line " + line + ")";
    }
}
