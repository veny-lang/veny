# Veny Formal Specification (v0.6.0)

© 2025 The Veny Language Project. Licensed under the Apache License 2.0.

---

## 1. Overview

Veny is a statically-typed, object-oriented programming language that compiles to Java.  
It emphasizes clarity, brevity, and strong static typing while being simple to parse and compile.

---

## 2. Lexical Structure

### 2.1 Character Set
- Unicode source text.
- Whitespace includes: space (`' '`), carriage return (`'\r'`), tab (`'\t'`), and newline (`'\n'`).

### 2.2 Tokens
Source code is broken into a stream of **tokens**. Tokens are separated by whitespace or punctuation and include:

#### Keywords
`class`, `interface`, `ext`, `impl`, `var`, `val`, `return`, `if`, `else`, `for`, `while`,  
`break`, `continue`, `import`, `package`, `true`, `false`, `null`, `pub`, `pri`, `in`

#### Identifiers
```ebnf
IDENTIFIER → [a-zA-Z_][a-zA-Z0-9_]*
````

#### Literals
- **Integer literal**: `123`, `0`, `42`
- **Float literal**: `3.14`, `0.0`
- **Text literal**: `"Hello World"`
- **Boolean literal**: `true`, `false`
- **Null literal**: `null`

#### Operators & Symbols
| Token             | Symbol(s)         | Description                 |
|-------------------|------------------|-----------------------------|
| `PLUS`            | `+`              | Addition                   |
| `MINUS`           | `-`              | Subtraction/negation       |
| `STAR`            | `*`              | Multiplication             |
| `SLASH`           | `/`              | Division                   |
| `MOD`             | `%`              | Modulo                     |
| `ASSIGN`          | `=`              | Assignment                 |
| `EQ`              | `==`             | Equals                     |
| `NEQ` or `BANG_EQ`| `!=`             | Not equals                 |
| `LT`, `GT`        | `<`, `>`         | Comparison                 |
| `LE`, `GE`        | `<=`, `>=`       | Comparison                 |
| `AND`             | `&&`             | Logical and                |
| `OR`              | `||`             | Logical or                 |
| `BANG`            | `!`              | Logical not                |
| `INCREMENT`       | `++`             | Increment                  |
| `DECREMENT`       | `--`             | Decrement                  |
| `ARROW`           | `->`             | Arrow                      |
| `DOUBLE_ARROW`    | `=>`             | Double arrow (future use)  |
| `COLON_EQ`        | `:=`             | Assignment (alt syntax)    |
| `DOUBLE_COLON`    | `::`             | Namespace access           |
| `DOUBLE_DOT`      | `..`             | Range                      |


####  Punctuation
```ebnf
( ) { } [ ] ; : , .
```
### 2.3 Comments
- Single-line comment starts with `//` and continues to the end of the line.
- (Multi-line comments not yet implemented.)

### 2.4 Whitespace
Whitespace is ignored except as a token separator.

---

## 3. Syntax Grammar (EBNF)
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
```

### 3.1 Informal Syntax Grammar (Overview)

This section explains the structure of Veny programs in prose, with examples.  
For precise definitions, see the **Formal Grammar (EBNF)**.

---

#### 3.1.1 Source Files
Every `.veny` file must begin with a **package declaration**:

```veny
package myapp.utils
```
Imports are optional and follow the package declaration:
```veny
import std.io
import myapp.models.User
```
After imports, the file may contain one or more class or interface declarations.

---
#### 3.1.2 Classes
A class declaration starts with the class keyword:
```veny
class Greeter {
    message: Text = "Hello"
    greet(): void {
        Console.print(message)
    }
}
```
- Classes may **extend** one parent class using `ext`:
```veny
class Student ext Person { }
```
- Classes may **implement** one or more interfaces using `impl`:
```veny
class App impl Runnable, Closeable { }
```
- Members inside `{ ... }` can be:
  - Fields (`val` for immutable, `var` for mutable)
  - Methods
---
#### 3.1.3 Interfaces
An interface starts with the `interface` keyword:
```veny
interface Printable {
    print(): void
}
```
- Interfaces may extend other interfaces with `impl`:
```veny
interface Loggable impl Serializable { 
    log(): void 
}
```
- The body contains only **method signatures** (no implementation).
---
#### 3.1.4 Fields
Fields are declared inside classes:
```veny
val name: Text = "Alice"
var counter: Int = 0
```
- `val` = immutable
- `var` = mutable
- Must include type and initializer.
Arrays use `[Type]` notation:
```veny
val numbers: [Int] = [1, 2, 3]
```
#### 3.1.5 Methods
Methods are defined inside classes or interfaces:
```veny
greet(name: Text): void {
    Console.print("Hello " + name)
}
```
- Syntax:
    ```veny
    visibility name(parameters): ReturnType { body }
    ```
- Parameters must declare a name and type:
    ```veny
    add(x: Int, y: Int): Int {
        return x + y
    }
    ```
- Interfaces only allow signatures (without a body).
---
### 3.1.5.1 Visibility and Accessors
Veny’s visibility system is designed to reduce boilerplate:
- **Fields** are `private` by default.
- **Methods** are `public` by default.
- Use `pub` for public fields, `pri` for private methods.

### Auto-generated accessors
Public fields automatically generate accessors:

- `obj.field()` → getter
- `obj.field(value)` → setter (only for `var` fields)
```veny
pub var count: Int = 0

run(): void {
    count(42)                  // setter
    System.print(count())      // getter
}
```

---
#### 3.1.6 Statements
Statements control program flow inside methods or blocks.
- **Variable declarations**
    ```veny
    val pi: Float = 3.14
    var active: Bool = true
    ```
- **Return**
    ```veny
    return 42
    ```
- **if / else**
     ```veny
    if active {
       Console.print("Running")
    } else {
       Console.print("Stopped")
    }
    ```
- **While loop**
     ```veny
    while counter < 10 {
       counter = counter + 1
    }
    ```
- **For-each loop**
     ```veny
    for item in numbers {
       Console.print(item)
    }
    ```
- **Break / Continue**
     ```veny
    while true {
       break
    }
    ```
---
#### 3.1.7 Expressions
An **expression** is a piece of code that **evaluates to a value**. They include:
- **Literals**
    ```veny
    42
    3.14
    "Hello"
    true
    false
    null
    ```
- **Variables**
    ```veny
    x
    ```
- **Arrays**
    ```veny
    [1,2,3]
    ```
- **Binary operators**
    ```veny
    a + b
    x == y
    count < 10
    ```
- **Unary operators**
    ```veny
    -x
    !done
    ```
- **Member access**
    ```veny
    greeter.greet("Alice")
    ```
#### 3.1.8 Entry Point
Programs begin at a method named `entry`:
```veny
class App {
    entry(args: [Text]): void {
        Console.print("Welcome to Veny")
    }
}
```
---

## 4. Types

### Built-in Types
- `Int`
- `Float`
- `Bool`
- `Text` (string)
- `Void`

### Arrays
- Written as `[Type]`
- Example: `[Int]`, `[Float]`

---

## 5. Entry Point

A `class` containing a method:

```veny
entry(args: [Text]): void
```

Marks the program entry point.

---

### 6. Semantics
- Variables declared with `val` are **immutable**; `var` are **mutable**.
- **Static typing**: variables must be explicitly typed (type inference is planned).
- **Classes** support single inheritance via `ext`.
- **Multiple interfaces** supported via `impl`.

---

### 7. Future Work
- Multi-line comments
- Type inference
- Fibers / concurrency
- Pattern matching
- Modules  