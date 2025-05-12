package org.venylang.veny.parser.ast.statement;

import org.venylang.veny.parser.ast.AstVisitor;
import org.venylang.veny.parser.ast.Statement;

public class BreakStmt implements Statement {

    @Override
    public <R> R accept(AstVisitor<R> visitor) {
        return visitor.visitBreakStmt(this);
    }

    @Override
    public String toString() {
        return "break";
    }
}
