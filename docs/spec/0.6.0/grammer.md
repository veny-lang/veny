### Veny EBNF (based on Lexer)

### Lexical Grammar

```ebnf
// Keywords
KEYWORD = "class" | "interface" | "ext" | "impl" |
          "var" | "val" | "return" | "if" | "else" |
          "for" | "in" | "while" | "break" | "continue" |
          "import" | "package" | "true" | "false" | "null" |
          "pub" | "pri" ;

// Identifiers
IDENTIFIER = LETTER (LETTER | DIGIT | "_")* ;
LETTER     = "A".."Z" | "a".."z" | "_" ;
DIGIT      = "0".."9" ;

// Literals
INT_LITERAL    = DIGIT+ ;
FLOAT_LITERAL  = DIGIT+ "." DIGIT+ ;
STRING_LITERAL = '"' CHAR* '"' ;   // No escapes supported yet
BOOL_LITERAL   = "true" | "false" ;
NULL_LITERAL   = "null" ;

// Symbols
OPERATOR = "+" | "-" | "*" | "/" | "%" |
           "==" | "!=" | "<" | "<=" | ">" | ">=" |
           "=" | "+=" | "-=" | "*=" | "/=" | "%=" | ":=" |
           "!" | "&&" | "||" | "&" | "|" | "^" |
           "<<" | ">>" |
           "->" | "=>" | "::" | ".." ;

PUNCTUATION = "(" | ")" | "{" | "}" | "[" | "]" |
              ";" | ":" | "," | "." ;

// Special
COMMENT = "//" <any characters until newline> ;
EOF     = <end of file> ;
```

### Syntax Grammar
```ebnf
File          = "package" QualifiedName
                { "import" QualifiedName }
                { ClassDecl | InterfaceDecl } EOF ;

QualifiedName = Identifier { "." Identifier } ;

ClassDecl     = "class" Identifier
                [ "ext" Identifier ]
                [ "impl" Identifier { "," Identifier } ]
                "{" { ClassMember } "}" ;

InterfaceDecl = "interface" Identifier
                [ "impl" Identifier { "," Identifier } ]
                "{" { MethodSignature } "}" ;

ClassMember   = ( "pub" | "pri" )? ( VarDecl | MethodDecl ) ;

VarDecl       = ( "var" | "val" ) Identifier ":" Type "=" Expression ;

MethodDecl    = MethodHeader Block ;
MethodHeader  = Identifier "(" [ Param { "," Param } ] ")" [ ":" Type ] ;
MethodSignature = MethodHeader ";" ;

Param         = Identifier ":" Type ;

Type          = Identifier | "[" Identifier "]" ;

Block         = "{" { Statement } "}" ;

Statement     = ReturnStmt
              | ValStmt
              | VarStmt
              | IfStmt
              | WhileStmt
              | ForStmt
              | BreakStmt
              | ContinueStmt
              | ExprStmt ;

ReturnStmt    = "return" Expression ;
ValStmt       = "val" Identifier ":" Type "=" Expression ;
VarStmt       = "var" Identifier ":" Type "=" Expression ;
IfStmt        = "if" Expression Block [ "else" Block ] ;
WhileStmt     = "while" Expression Block ;
ForStmt       = "for" Identifier "in" Expression Block ;
BreakStmt     = "break" ;
ContinueStmt  = "continue" ;
ExprStmt      = Expression ;

Expression    = Assignment ;
Assignment    = ( VariableExpr | GetExpr ) "=" Assignment | Equality ;

Equality      = Comparison { ( "==" | "!=" ) Comparison } ;
Comparison    = Term { ( "<" | ">" | "<=" | ">=" ) Term } ;
Term          = Factor { ( "+" | "-" ) Factor } ;
Factor        = Unary { ( "*" | "/" ) Unary } ;
Unary         = [ "-" ] Primary ;

Primary       = Literal
              | ArrayLiteral
              | VariableExpr
              | CallExpr
              | MemberAccess ;

ArrayLiteral  = "[" [ Expression { "," Expression } ] "]" ;
Literal       = IntLiteral | FloatLiteral | StringLiteral | "true" | "false" ;
VariableExpr  = Identifier ;
CallExpr      = Primary "(" [ Expression { "," Expression } ] ")" ;
MemberAccess  = Primary "." Identifier ;

Identifier    = /* token: IDENTIFIER */ ;
IntLiteral    = /* token: INT_LITERAL */ ;
FloatLiteral  = /* token: FLOAT_LITERAL */ ;
StringLiteral = /* token: TEST_LITERAL */ ;
EntryMethodDecl = "entry" "(" "args" ":" "[" "Text" "]" ")" ":" "void" Block
```
