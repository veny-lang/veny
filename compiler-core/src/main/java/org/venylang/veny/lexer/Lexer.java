package org.venylang.veny.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords = Map.ofEntries(
            Map.entry("class", TokenType.CLASS),
            Map.entry("var", TokenType.VAR),
            Map.entry("val", TokenType.VAL),
            Map.entry("pub", TokenType.PUB),
            Map.entry("pri", TokenType.PRI),
            Map.entry("return", TokenType.RETURN),
            Map.entry("if", TokenType.IF),
            Map.entry("else", TokenType.ELSE),
            Map.entry("for", TokenType.FOR),
            Map.entry("in", TokenType.IN),
            Map.entry("while", TokenType.WHILE),
            Map.entry("break", TokenType.BREAK),
            Map.entry("continue", TokenType.CONTINUE),
            Map.entry("import", TokenType.IMPORT),
            Map.entry("package", TokenType.PACKAGE),
            Map.entry("true", TokenType.TRUE),
            Map.entry("false", TokenType.FALSE),
            Map.entry("null", TokenType.NULL)
    );

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.OPEN_PAREN); break;
            case ')': addToken(TokenType.CLOSE_PAREN); break;
            case '{': addToken(TokenType.OPEN_BRACE); break;
            case '}': addToken(TokenType.CLOSE_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case ':': addToken(TokenType.COLON); break;
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.STAR); break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance(); // comment
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            case '=': addToken(match('=') ? TokenType.EQ : TokenType.ASSIGN); break;
            case '!': addToken(match('=') ? TokenType.NEQ : null); break;
            case '<': addToken(match('=') ? TokenType.LE : TokenType.LT); break;
            case '>': addToken(match('=') ? TokenType.GE : TokenType.GT); break;
            case '"': string(); break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                }
                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // consume dot
            while (isDigit(peek())) advance();
            addToken(TokenType.FLOAT_LITERAL);
        } else {
            addToken(TokenType.INT_LITERAL);
        }
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            System.err.println("Unterminated string at line " + line);
            return;
        }

        advance(); // closing "
        String value = source.substring(start + 1, current - 1);
        tokens.add(new Token(TokenType.STRING_LITERAL, value, line));
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(current);
    }

    private char peekNext() {
        return (current + 1 >= source.length()) ? '\0' : source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        if (type != null) {
            String text = source.substring(start, current);
            tokens.add(new Token(type, text, line));
        }
    }
}
