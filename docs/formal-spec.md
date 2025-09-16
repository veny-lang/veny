🔍 Steps to Verify Spec with Lexer & Parser

Extract All Tokens from Lexer

Look for enum TokenType or similar.

List every keyword, operator, and punctuation symbol.

That gives us the “alphabet” of your language.

Check Parser Entry Points

Usually there’s a parse() method in RecursiveDescentParser (or similar) that defines Program.

From there, follow parseFile, parseClass, parseMethod, etc.

This lets us reconstruct the actual grammar your compiler enforces.

Match AST Node Classes

ClassDecl, MethodDecl, VarDecl, Expr, etc., show exactly what your parser produces.

This helps define the syntax tree and ensure every production rule is covered.

Build a Verified Grammar

Write out an EBNF spec that directly corresponds to parser methods.

Example: if you see parseIfStmt() consumes if + expr + block, you codify that.

Cross-Check Semantics

Where semantic rules are implied (like val meaning immutable), confirm in your semantic analyzer.

Example: VarSymbol for val probably has isMutable=false.

✅ Deliverables We Can Produce

After this review, we can produce:

A Verified Grammar (EBNF) for Veny v0.6.0.

A Semantic Table mapping Veny constructs → Java constructs.

A Keyword/Operator Table based on the lexer.

🔥 Why This is Worth Doing

Makes sure docs ≡ implementation.

Prevents codegen bugs: we won’t generate code for constructs that don’t exist.

Sets foundation for IDE features (syntax highlighting, autocompletion, etc.).

Easier onboarding for contributors.