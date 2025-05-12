package org.venylang.veny.parser;

import org.venylang.veny.lexer.Token;
import org.venylang.veny.parser.ast.Program;

import java.util.List;

public interface Parser {
    Program parse();
}
