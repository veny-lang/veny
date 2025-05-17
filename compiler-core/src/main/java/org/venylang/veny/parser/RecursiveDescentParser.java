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

package org.venylang.veny.parser;

import org.venylang.veny.lexer.Token;
import org.venylang.veny.lexer.TokenType;
import org.venylang.veny.parser.ast.*;
import org.venylang.veny.parser.ast.expression.*;
import org.venylang.veny.parser.ast.statement.*;
import org.venylang.veny.util.Visibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RecursiveDescentParser implements Parser {
    private final List<Token> tokens;
    private int current = 0;

    public RecursiveDescentParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Helper method to consume the current token and advance
    private Token consume() {
        return tokens.get(current++);
    }

    private Token consume(TokenType expectedType) {
        if (peek().type() != expectedType) {
            throw new ParseException("Expected " + expectedType + " but found " + peek().type() + " '" + peek().lexeme() + "'");
        }
        return consume();
    }

    // Helper method to look at the current token without consuming it
    private Token peek() {
        return current < tokens.size() ? tokens.get(current) : null;
    }

    // Helper method to peek ahead in the token stream
    private Token lookAhead(int distance) {
        if (current + distance >= tokens.size()) return tokens.get(tokens.size() - 1);
        return tokens.get(current + distance);
    }

    // Helper method to check if the current token matches a specific type
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (peek() != null && Objects.requireNonNull(peek()).type() == type) {
                consume();
                return true;
            }
        }
        return false;
    }

    // Helper method to ensure that the next token matches a specific type
    private Token expect(TokenType type) {
        if (peek() != null && Objects.requireNonNull(peek()).type() == type) {
            return consume();
        }
        throw new ParseException("Expected " + type + " but found " + peek());
    }

    private Token expect(TokenType type, String message) {
        if (peek() != null && Objects.requireNonNull(peek()).type() == type) {
            return consume();
        }
        throw new ParseException(message + " but found " + peek());
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && peek().type() == type;
    }

    private boolean isAtEnd() {
        return current >= tokens.size() || peek().type() == TokenType.EOF;
    }


    // Main parsing method for the Program
    public VenyFile parse() {
        // 🔒 Require `package` declaration
        if (!match(TokenType.PACKAGE)) {
            Token token = peek();
            throw new ParseException("[" + token.line() + "] Error: Expected `package` declaration at the top of the file.");
        }
        String packageName = parseQualifiedName();

        // 🟡 Optional: Parse `import` declarations
        List<String> imports = new ArrayList<>();
        while (match(TokenType.IMPORT)) {
            String importName = parseQualifiedName();
            imports.add(importName);
        }

        // 🧱 Parse class declarations
        List<ClassDecl> classes = new ArrayList<>();
        while (peek() != null && peek().type() != TokenType.EOF) {
            classes.add(parseClassDecl());
        }

        return new VenyFile(packageName, imports, classes);
    }

    private String parseQualifiedName() {
        StringBuilder name = new StringBuilder();
        name.append(expect(TokenType.IDENTIFIER, "Expected identifier in qualified name").lexeme());

        while (match(TokenType.DOT)) {
            name.append('.');
            name.append(expect(TokenType.IDENTIFIER, "Expected identifier after `.`").lexeme());
        }

        return name.toString();
    }

    // Parse a Class declaration
    private ClassDecl parseClassDecl() {
        expect(TokenType.CLASS);
        String className = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.LBRACE);

        List<VarDecl> fields = new ArrayList<>();
        List<MethodDecl> methods = new ArrayList<>();

        while (peek() != null && Objects.requireNonNull(peek()).type() != TokenType.RBRACE) {
            // Default visibility if no modifier appears
            Visibility visibility = Visibility.DEFAULT;

            // Check for visibility modifiers
            if (match(TokenType.PUB)) {
                visibility = Visibility.PUBLIC;
            } else if (match(TokenType.PRI)) {
                visibility = Visibility.PRIVATE;
            }

            Token next = peek();

            if (next.type() == TokenType.VAR || next.type() == TokenType.VAL) {
                fields.add(parseVarDecl(visibility));
            } else if (next.type() == TokenType.IDENTIFIER && lookAhead(1).type() == TokenType.LPAREN) {
                methods.add(parseMethodDecl(visibility));
            } else {
                throw new ParseException("Unexpected token in class body: " + peek());
            }
        }

        expect(TokenType.RBRACE);
        return new ClassDecl(className, fields, methods);
    }

    // Parse a Variable declaration
    private VarDecl parseVarDecl(Visibility visibility) {
        boolean isMutable;

        if (match(TokenType.VAR)) {
            isMutable = true;
        } else if (match(TokenType.VAL)) {
            isMutable = false;
        } else {
            throw new ParseException("Expected 'var' or 'val'");
        }

        String varName = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.COLON);

        // Allow a variable type to be either identifier or array
        String typeName;
        if (match(TokenType.LBRACKET)) {
            typeName = "[" + expect(TokenType.IDENTIFIER).lexeme() + "]";
            expect(TokenType.RBRACKET);
        } else {
            typeName = expect(TokenType.IDENTIFIER).lexeme();
        }

        if (!match(TokenType.ASSIGN)) {
            throw new ParseException("Expected '=' to initialize variable '" + varName + "' after type declaration.");
        }

        Expression initializer = parseExpression();
        return new VarDecl(varName, typeName, initializer, isMutable, visibility);
    }

    // Parse a Method declaration
    private MethodDecl parseMethodDecl(Visibility visibility) {
        String methodName = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.LPAREN);

        List<MethodDecl.Parameter> parameters = new ArrayList<>();
        while (Objects.requireNonNull(peek()).type() != TokenType.RPAREN) {
            String paramName = expect(TokenType.IDENTIFIER).lexeme();
            expect(TokenType.COLON);
            String paramType = expect(TokenType.IDENTIFIER).lexeme();
            parameters.add(new MethodDecl.Parameter(paramName, paramType));
            if (Objects.requireNonNull(peek()).type() != TokenType.RPAREN) expect(TokenType.COMMA);
        }

        expect(TokenType.RPAREN);

        // Optional return type
        String returnType = "void";
        if (peek().type() == TokenType.COLON) {
            consume();
            returnType = expect(TokenType.IDENTIFIER).lexeme();
        }

        expect(TokenType.LBRACE);
        List<Statement> body = parseStatements();
        expect(TokenType.RBRACE);
        return new MethodDecl(methodName, parameters, returnType, body, visibility);
    }

    // Parse statements inside method body
    private List<Statement> parseStatements() {
        List<Statement> statements = new ArrayList<>();
        while (peek() != null && Objects.requireNonNull(peek()).type() != TokenType.RBRACE) {
            statements.add(parseStatement());
        }
        return statements;
    }

    // Parse a single statement (e.g., if, while, return, etc.)
    private Statement parseStatement() {
        Token token = Objects.requireNonNull(peek());

        switch (token.type()) {
            case RETURN:
                return parseReturnStmt();
            case VAL:
                return parseValStatement();
            case VAR:
                return parseVarStatement(); // if you support mutable variables too
            case IF:
                return parseIfStatement();
            case WHILE:
                return parseWhileStatement();
            case FOR:
                return parseForStatement();
            case BREAK:
                return parseBreakStatement();
            case CONTINUE:
                return parseContinueStatement();
            // Add more cases here: IF, WHILE, etc.
            default:
                Expression expr = parseExpression();
                return new ExprStmt(expr);
        }
    }

    // Parse a Return statement
    private ReturnStmt parseReturnStmt() {
        expect(TokenType.RETURN);
        Expression value = parseExpression();
        return new ReturnStmt(value);
    }

    private Statement parseValStatement() {
        consume(TokenType.VAL); // consume 'val'
        String name = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.COLON);

        String type;
        if (match(TokenType.LBRACKET)) { // check for '['
            String elementType = expect(TokenType.IDENTIFIER).lexeme();
            expect(TokenType.RBRACKET); // expect ']'
            type = "[" + elementType + "]";
        } else {
            type = expect(TokenType.IDENTIFIER).lexeme();
        }

        expect(TokenType.ASSIGN);
        Expression initializer = parseExpression();
        return new ValStmt(name, type, initializer);
    }

    private Statement parseVarStatement() {
        consume(TokenType.VAR); // consume 'var'
        String name = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.COLON);
        String type = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.ASSIGN);
        Expression initializer = parseExpression();
        return new VarStmt(name, type, initializer);
    }

    private Statement parseIfStatement() {
        // Consume 'if' token
        consume(TokenType.IF);

        // Expect an open parenthesis for the condition
        //consume(TokenType.OPEN_PAREN);

        // Parse the condition expression
        Expression condition = parseExpression();

        // Expect a closing parenthesis after the condition
        //consume(TokenType.CLOSE_PAREN);
        consume(TokenType.LBRACE);

        // Parse the "if" block (a sequence of statements)
        BlockStmt ifBlock = parseBlock();

        // Check for an optional 'else' block
        BlockStmt elseBlock = null;
        if (match(TokenType.ELSE)) {
            consume(TokenType.LBRACE);
            elseBlock = parseBlock();
        }

        // Return an IfStmt with the condition and both blocks (if present)
        return new IfStmt(condition, ifBlock, elseBlock);
    }

    private Statement parseWhileStatement() {
        consume(TokenType.WHILE);

        // Expect: while (condition)
        //consume(TokenType.OPEN_PAREN);
        Expression condition = parseExpression();
        //consume(TokenType.CLOSE_PAREN);

        // Expect: body block
        BlockStmt body = (BlockStmt) parseBlockOrStatement();

        return new WhileStmt(condition, body);
    }

    private Statement parseForStatement() {
        consume(TokenType.FOR);
        String varName = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.IN);
        Expression iterable = parseExpression();
        BlockStmt body = (BlockStmt) parseBlockOrStatement();
        return new ForStmt(varName, iterable, body);
    }

    private Statement parseBreakStatement() {
        consume(TokenType.BREAK);
        return new BreakStmt(); // You must define BreakStmt in the AST
    }

    private Statement parseContinueStatement() {
        consume(TokenType.CONTINUE);
        return new ContinueStmt(); // Same here
    }

    private Statement parseBlockOrStatement() {
        if (match(TokenType.LBRACE)) {
            return parseBlock(); // assuming you have a parseBlock() method returning a BlockStmt
        } else {
            return parseStatement();
        }
    }

    private BlockStmt parseBlock() {
        List<Statement> statements = new ArrayList<>();

        // We've already consumed the opening '{' before calling this method
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }

        expect(TokenType.RBRACE); // consume '}'
        return new BlockStmt(statements);
    }

    // Parse expressions (simplified for this example)
    private Expression parseExpression() {
        return parseAssignment();
    }

    private Expression parseAssignment() {
        Expression expr = parseEquality();

        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            Expression value = parseAssignment();

            if (expr instanceof VariableExpr) {
                return new AssignExpr(((VariableExpr) expr).name(), value);
            } else if (expr instanceof GetExpr) {
                return new SetExpr(((GetExpr) expr).target(), ((GetExpr) expr).field(), value);
            }

            throw new ParseException("Invalid assignment target at " + equals);
        }

        return expr;
    }

    private Expression parseEquality() {
        Expression expr = parseComparison();

        while (match(TokenType.EQ, TokenType.NEQ)) {
            Token operator = previous();
            Expression right = parseComparison();
            expr = new BinaryExpr(expr, operator.lexeme(), right);
        }

        return expr;
    }

    private Expression parseComparison() {
        Expression expr = parseTerm();

        while (match(TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE)) {
            Token operator = previous();
            Expression right = parseTerm();
            expr = new BinaryExpr(expr, operator.lexeme(), right);
        }

        return expr;
    }

    private Expression parseTerm() {
        Expression expr = parseFactor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = parseFactor();
            expr = new BinaryExpr(expr, operator.lexeme(), right);
        }

        return expr;
    }

    private Expression parseFactor() {
        Expression expr = parseUnary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expression right = parseUnary();
            expr = new BinaryExpr(expr, operator.lexeme(), right);
        }

        return expr;
    }

    private Expression parseUnary() {
        if (match(TokenType.MINUS)) {
            Token operator = previous();
            Expression right = parseUnary();
            return new UnaryExpr(operator.lexeme(), right);
        }

        return parsePrimary();
    }

    private Expression parsePrimary() {

        if (check(TokenType.LBRACKET)) {
            consume(); // consume '['
            List<Expression> elements = new ArrayList<>();
            if (!check(TokenType.RBRACKET)) {
                do {
                    elements.add(parseExpression());
                } while (match(TokenType.COMMA));
            }
            expect(TokenType.RBRACKET);
            return new ArrayLiteralExpr(elements);
        }

        Token token = expectAny(TokenType.IDENTIFIER, TokenType.INT_LITERAL,
                TokenType.STRING_LITERAL, TokenType.FLOAT_LITERAL, TokenType.TRUE, TokenType.FALSE);

        Expression expr;

        switch (token.type()) {
            case IDENTIFIER:
                expr = new VariableExpr(token.lexeme());

                // Check for function/constructor call directly after identifier
                if (match(TokenType.LPAREN)) {
                    List<Expression> args = new ArrayList<>();
                    if (peek().type() != TokenType.RPAREN) {
                        do {
                            args.add(parseExpression());
                        } while (match(TokenType.COMMA));
                    }
                    expect(TokenType.RPAREN);
                    expr = new CallExpr(expr, args);
                }
                break;

            case INT_LITERAL:
                expr = new LiteralExpr(Integer.parseInt(token.lexeme()));
                break;
            case FLOAT_LITERAL:
                expr = new LiteralExpr(Float.parseFloat(token.lexeme()));
                break;
            case STRING_LITERAL:
                expr = new LiteralExpr(token.lexeme());
                break;
            case TRUE:
                expr = new LiteralExpr(true);
                break;
            case FALSE:
                expr = new LiteralExpr(false);
                break;
            default:
                throw new ParseException("Unexpected token in expression: " + token);
        }

        // Handle chained member access and method calls (e.g. System.out.print())
        while (true) {
            if (match(TokenType.DOT)) {
                String name = expect(TokenType.IDENTIFIER).lexeme();
                Expression target = new GetExpr(expr, name);

                if (match(TokenType.LPAREN)) {
                    List<Expression> args = new ArrayList<>();
                    if (peek().type() != TokenType.RPAREN) {
                        do {
                            args.add(parseExpression());
                        } while (match(TokenType.COMMA));
                    }
                    expect(TokenType.RPAREN);
                    expr = new CallExpr(target, args); // e.g., System.out.print(...)
                } else {
                    expr = target; // field access: obj.field
                }
            } else {
                break;
            }
        }

        return expr;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token expectAny(TokenType... types) {
        for (TokenType type : types) {
            if (peek().type() == type) {
                return consume();
            }
        }

        throw new ParseException("Expected one of " + Arrays.toString(types) + " but found " + peek());
    }

    // Parse a variable expression
    private Expression parseVariableExpr() {
        String varName = expect(TokenType.IDENTIFIER).lexeme();
        return new VariableExpr(varName);
    }

    // Parse a literal expression (number or string)
    private Expression parseLiteralExpr() {
        Token literal = consume();
        if (literal.type() == TokenType.INT_LITERAL) {
            return new LiteralExpr(Integer.parseInt(literal.lexeme()));
        } else if (literal.type() == TokenType.STRING_LITERAL) {
            return new LiteralExpr(literal.lexeme());
        }
        throw new ParseException("Invalid literal: " + literal);
    }

    // Parse binary expressions (for future, can be expanded)
    private Expression parseBinaryExpr() {
        // Placeholder for future expansion
        return parseExpression();
    }
}
