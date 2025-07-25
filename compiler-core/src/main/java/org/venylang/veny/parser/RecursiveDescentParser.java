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

import org.venylang.veny.context.ParseContext;
import org.venylang.veny.lexer.Token;
import org.venylang.veny.lexer.TokenType;
import org.venylang.veny.parser.ast.*;
import org.venylang.veny.parser.ast.expression.*;
import org.venylang.veny.parser.ast.statement.*;
import org.venylang.veny.util.Visibility;
import org.venylang.veny.util.source.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A recursive descent parser for the Veny language.
 * Parses a list of tokens into an abstract syntax tree (AST) represented by {@link VenyFile}.
 */
public class RecursiveDescentParser implements Parser {
    private final List<Token> tokens;
    private final ParseContext parseContext;
    private int current = 0;

    /**
     * Constructs a new parser with the provided list of tokens.
     *
     * @param tokens the token stream to parse
     */
    public RecursiveDescentParser(List<Token> tokens, ParseContext parseContext) {
        this.tokens = tokens;
        this.parseContext = parseContext;
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


    /**
     * Parses the tokens and returns the root AST node representing the Veny source file.
     *
     * @return the parsed {@link VenyFile}
     * @throws ParseException if a syntax error is encountered
     */
    public VenyFile parse() {
        // 🔒 Require `package` declaration
        if (!match(TokenType.PACKAGE)) {
            Token token = peek();
            Position pos = parseContext.srcFilePosMap().positionFor(token.offset(), true);
            throw new ParseException("Error at " + pos + ": Expected `package` declaration at the top of the file.");
        }
        String packageName = parseQualifiedName();

        // 🟡 Optional: Parse `import` declarations
        List<String> imports = new ArrayList<>();
        while (match(TokenType.IMPORT)) {
            String importName = parseQualifiedName();
            imports.add(importName);
        }

        // 🧱 Parse class and interface declarations
        List<ClassDecl> classes = new ArrayList<>();
        List<InterfaceDecl> interfaces = new ArrayList<>();

        while (peek() != null && peek().type() != TokenType.EOF) {
            TokenType type = peek().type();
            if (type == TokenType.CLASS) {
                classes.add(parseClassDecl());
            } else if (type == TokenType.INTERFACE) {
                interfaces.add(parseInterfaceDecl());
            } else {
                throw new ParseException("Unexpected top-level declaration: " + peek().lexeme());
            }
        }

        return new VenyFile(packageName, imports, classes, interfaces);
    }

    /**
     * Parses a qualified name, e.g., a.b.c
     *
     * @return the qualified name as a string
     * @throws ParseException if the qualified name is malformed
     */
    private String parseQualifiedName() {
        StringBuilder name = new StringBuilder();
        name.append(expect(TokenType.IDENTIFIER, "Expected identifier in qualified name").lexeme());

        while (match(TokenType.DOT)) {
            name.append('.');
            name.append(expect(TokenType.IDENTIFIER, "Expected identifier after `.`").lexeme());
        }

        return name.toString();
    }

    /**
     * Parses a class declaration including its fields and methods.
     *
     * @return the parsed {@link ClassDecl}
     * @throws ParseException if class declaration syntax is invalid
     */
    private ClassDecl parseClassDecl() {
        expect(TokenType.CLASS);
        String className = expect(TokenType.IDENTIFIER).lexeme();

        // Handle optional 'ext' parent class
        String parent = null;
        if (match(TokenType.EXT)) {
            parent = expect(TokenType.IDENTIFIER).lexeme();
        }

        // Handle optional 'impl' interfaces
        List<String> interfaces = new ArrayList<>();
        if (match(TokenType.IMPL)) {
            interfaces.add(expect(TokenType.IDENTIFIER).lexeme());
            while (match(TokenType.COMMA)) {
                interfaces.add(expect(TokenType.IDENTIFIER).lexeme());
            }
        }

        expect(TokenType.LBRACE);

        List<VarDecl> fields = new ArrayList<>();
        List<MethodDecl> methods = new ArrayList<>();

        while (peek() != null && peek().type() != TokenType.RBRACE) {
            Visibility visibility = Visibility.DEFAULT;

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
                throw new ParseException("Unexpected token in class body: " + next);
            }
        }

        expect(TokenType.RBRACE);
        return new ClassDecl(className, parent, interfaces, fields, methods);
    }

    private InterfaceDecl parseInterfaceDecl() {
        expect(TokenType.INTERFACE);
        String interfaceName = expect(TokenType.IDENTIFIER).lexeme();

        // Handle optional parent interfaces via 'impl'
        List<String> parents = new ArrayList<>();
        if (match(TokenType.IMPL)) {
            parents.add(expect(TokenType.IDENTIFIER).lexeme());
            while (match(TokenType.COMMA)) {
                parents.add(expect(TokenType.IDENTIFIER).lexeme());
            }
        }

        expect(TokenType.LBRACE);

        List<MethodDecl> methods = new ArrayList<>();

        while (peek() != null && peek().type() != TokenType.RBRACE) {
            Token next = peek();

            // Method declarations in the interface must be without a body
            if (next.type() == TokenType.IDENTIFIER && lookAhead(1).type() == TokenType.LPAREN) {
                methods.add(parseInterfaceMethod());
            } else {
                throw new ParseException("Only method signatures allowed in interfaces. Found: " + next);
            }
        }

        expect(TokenType.RBRACE);
        return new InterfaceDecl(interfaceName, parents, methods);
    }

    /**
     * Parses a variable declaration with optional initializer.
     *
     * @param visibility the visibility modifier for the variable
     * @return the parsed {@link VarDecl}
     * @throws ParseException if syntax is invalid
     */
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

    /**
     * Parses a method declaration including parameters, return type, and body.
     *
     * @param visibility the visibility modifier for the method
     * @return the parsed {@link MethodDecl}
     * @throws ParseException if syntax is invalid
     */
    private MethodDecl parseMethodDecl(Visibility visibility) {
        MethodHeader header = parseMethodHeader();
        expect(TokenType.LBRACE);
        List<Statement> body = parseStatements();
        expect(TokenType.RBRACE);
        return new MethodDecl(header.name(), header.params(), header.returnType(), body, visibility);
    }

    private MethodDecl parseInterfaceMethod() {
        MethodHeader header = parseMethodHeader();
        return new MethodDecl(header.name(), header.params(), header.returnType(), null, Visibility.DEFAULT);
    }

    private MethodHeader parseMethodHeader() {
        String methodName = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.LPAREN);

        List<MethodDecl.Parameter> parameters = new ArrayList<>();
        while (peek().type() != TokenType.RPAREN) {
            String paramName = expect(TokenType.IDENTIFIER).lexeme();
            expect(TokenType.COLON);
            String paramType;
            if (match(TokenType.LBRACKET)) { // check for '['
                String elementType = expect(TokenType.IDENTIFIER).lexeme();
                expect(TokenType.RBRACKET); // expect ']'
                paramType = "[" + elementType + "]";
            } else {
                paramType =  expect(TokenType.IDENTIFIER).lexeme();
            }
            //String paramType = expect(TokenType.IDENTIFIER).lexeme();
            parameters.add(new MethodDecl.Parameter(paramName, paramType));
            if (peek().type() != TokenType.RPAREN) expect(TokenType.COMMA);
        }
        expect(TokenType.RPAREN);

        String returnType = "void";
        if (peek().type() == TokenType.COLON) {
            consume();
            returnType = expect(TokenType.IDENTIFIER).lexeme();
        }

        return new MethodHeader(methodName, parameters, returnType);
    }

    private record MethodHeader(String name, List<MethodDecl.Parameter> params, String returnType) {}

    /**
     * Parses a sequence of statements until the end of the block.
     *
     * @return a list of parsed {@link Statement} nodes
     * @throws ParseException if any statement is invalid
     */
    private List<Statement> parseStatements() {
        List<Statement> statements = new ArrayList<>();
        while (peek() != null && Objects.requireNonNull(peek()).type() != TokenType.RBRACE) {
            statements.add(parseStatement());
        }
        return statements;
    }

    /**
     * Parses a single statement.
     *
     * @return a parsed {@link Statement}
     * @throws ParseException if the statement is invalid or unsupported
     */
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

    /**
     * Parses a return statement starting with the 'return' keyword.
     *
     * @return a {@link ReturnStmt} representing the return statement
     * @throws ParseException if the syntax after 'return' is invalid
     */
    private ReturnStmt parseReturnStmt() {
        expect(TokenType.RETURN);
        Expression value = parseExpression();
        return new ReturnStmt(value);
    }

    /**
     * Parses an immutable variable declaration statement starting with 'val'.
     *
     * @return a {@link ValStmt} representing the immutable variable declaration
     * @throws ParseException if syntax is invalid
     */
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

    /**
     * Parses a mutable variable declaration statement starting with 'var'.
     *
     * @return a {@link VarStmt} representing the mutable variable declaration
     * @throws ParseException if syntax is invalid
     */
    private Statement parseVarStatement() {
        consume(TokenType.VAR); // consume 'var'
        String name = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.COLON);
        String type = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.ASSIGN);
        Expression initializer = parseExpression();
        return new VarStmt(name, type, initializer);
    }

    /**
     * Parses an if statement with an optional else block.
     * Expects the 'if' token followed by a condition expression and a block.
     *
     * @return an {@link IfStmt} node representing the conditional
     * @throws ParseException if syntax is invalid or blocks are malformed
     */
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

    /**
     * Parses a while loop statement.
     * Expects 'while' token followed by a condition expression and a block or statement.
     *
     * @return a {@link WhileStmt} representing the loop
     * @throws ParseException if syntax is invalid
     */
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

    /**
     * Parses a for-each loop statement.
     * Expects 'for' token, loop variable name, 'in' keyword, iterable expression, and loop body block.
     *
     * @return a {@link ForStmt} representing the loop
     * @throws ParseException if syntax is invalid
     */
    private Statement parseForStatement() {
        consume(TokenType.FOR);
        String varName = expect(TokenType.IDENTIFIER).lexeme();
        expect(TokenType.IN);
        Expression iterable = parseExpression();
        BlockStmt body = (BlockStmt) parseBlockOrStatement();
        return new ForStmt(varName, iterable, body);
    }

    /**
     * Parses a break statement.
     * Expects the 'break' token.
     *
     * @return a {@link BreakStmt} representing the break statement
     */
    private Statement parseBreakStatement() {
        consume(TokenType.BREAK);
        return new BreakStmt(); // You must define BreakStmt in the AST
    }

    /**
     * Parses a continue statement.
     * Expects the 'continue' token.
     *
     * @return a {@link ContinueStmt} representing the continue statement
     */
    private Statement parseContinueStatement() {
        consume(TokenType.CONTINUE);
        return new ContinueStmt(); // Same here
    }

    /**
     * Parses either a block (surrounded by braces) or a single statement.
     *
     * @return a {@link Statement} which is either a {@link BlockStmt} or a single statement
     * @throws ParseException if syntax is invalid
     */
    private Statement parseBlockOrStatement() {
        if (match(TokenType.LBRACE)) {
            return parseBlock(); // assuming you have a parseBlock() method returning a BlockStmt
        } else {
            return parseStatement();
        }
    }

    /**
     * Parses a block of statements enclosed in '{' and '}'.
     * Expects the opening brace to have been consumed already.
     *
     * @return a {@link BlockStmt} containing the list of statements
     * @throws ParseException if a closing brace is missing or statements are invalid
     */
    private BlockStmt parseBlock() {
        List<Statement> statements = new ArrayList<>();

        // We've already consumed the opening '{' before calling this method
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }

        expect(TokenType.RBRACE); // consume '}'
        return new BlockStmt(statements);
    }

    /**
     * Parses an expression according to operator precedence starting with assignment.
     *
     * @return a parsed {@link Expression}
     * @throws ParseException if the expression syntax is invalid
     */
    private Expression parseExpression() {
        return parseAssignment();
    }

    /**
     * Parses assignment expressions.
     * Recognizes assignment operators and validates left-hand side targets.
     *
     * @return a parsed {@link Expression} representing assignment or lower-precedence expressions
     * @throws ParseException if assignment target is invalid
     */
    private Expression parseAssignment() {
        Expression expr = parseEquality();

        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            Expression value = parseAssignment();

            if (expr instanceof VariableExpr) {
                return AssignExpr.of(((VariableExpr) expr).name(), value);
            } else if (expr instanceof GetExpr) {
                return new SetExpr(((GetExpr) expr).target(), ((GetExpr) expr).field(), value);
            }

            throw new ParseException("Invalid assignment target at " + equals);
        }

        return expr;
    }

    /**
     * Parses equality expressions (==, !=).
     *
     * @return a parsed {@link Expression}
     */
    private Expression parseEquality() {
        Expression expr = parseComparison();

        while (match(TokenType.EQ, TokenType.NEQ)) {
            Token operator = previous();
            Expression right = parseComparison();
            expr = new BinaryExpr(expr, operator.lexeme(), right);
        }

        return expr;
    }

    /**
     * Parses comparison expressions (<, >, <=, >=).
     *
     * @return a parsed {@link Expression}
     */
    private Expression parseComparison() {
        Expression expr = parseTerm();

        while (match(TokenType.LT, TokenType.GT, TokenType.LE, TokenType.GE)) {
            Token operator = previous();
            Expression right = parseTerm();
            expr = new BinaryExpr(expr, operator.lexeme(), right);
        }

        return expr;
    }

    /**
     * Parses addition and subtraction expressions (+, -).
     *
     * @return a parsed {@link Expression}
     */
    private Expression parseTerm() {
        Expression expr = parseFactor();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Expression right = parseFactor();
            expr = new BinaryExpr(expr, operator.lexeme(), right);
        }

        return expr;
    }

    /**
     * Parses multiplication and division expressions (*, /).
     *
     * @return a parsed {@link Expression}
     */
    private Expression parseFactor() {
        Expression expr = parseUnary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Expression right = parseUnary();
            expr = new BinaryExpr(expr, operator.lexeme(), right);
        }

        return expr;
    }

    /**
     * Parses unary expressions, including negation.
     *
     * @return a parsed {@link Expression}
     */
    private Expression parseUnary() {
        if (match(TokenType.MINUS)) {
            Token operator = previous();
            Expression right = parseUnary();
            return new UnaryExpr(operator.lexeme(), right);
        }

        return parsePrimary();
    }

    /**
     * Parses primary expressions: literals, variables, array literals, function calls, member access.
     *
     * @return a parsed {@link Expression}
     * @throws ParseException if the token is unexpected or malformed
     */
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
                TokenType.TEST_LITERAL, TokenType.FLOAT_LITERAL, TokenType.TRUE, TokenType.FALSE);

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
            case TEST_LITERAL:
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

    /**
     * Returns the previously consumed token.
     *
     * @return the last consumed {@link Token}
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Expects the next token to be one of the provided types and consumes it.
     *
     * @param types expected token types
     * @return the consumed {@link Token}
     * @throws ParseException if the next token is not among the expected types
     */
    private Token expectAny(TokenType... types) {
        for (TokenType type : types) {
            if (peek().type() == type) {
                return consume();
            }
        }

        throw new ParseException("Expected one of " + Arrays.toString(types) + " but found " + peek());
    }

    /**
     * Parses a variable expression (identifier).
     *
     * @return a {@link VariableExpr}
     */
    private Expression parseVariableExpr() {
        String varName = expect(TokenType.IDENTIFIER).lexeme();
        return new VariableExpr(varName);
    }

    /**
     * Parses a literal expression (integer or string).
     *
     * @return a {@link LiteralExpr}
     * @throws ParseException if the literal token is invalid
     */
    private Expression parseLiteralExpr() {
        Token literal = consume();
        if (literal.type() == TokenType.INT_LITERAL) {
            return new LiteralExpr(Integer.parseInt(literal.lexeme()));
        } else if (literal.type() == TokenType.TEST_LITERAL) {
            return new LiteralExpr(literal.lexeme());
        }
        throw new ParseException("Invalid literal: " + literal);
    }

    // Parse binary expressions (for the future, can be expanded)
    private Expression parseBinaryExpr() {
        // Placeholder for future expansion
        return parseExpression();
    }
}
