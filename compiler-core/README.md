# Veny Compiler

**Veny** is a modern, lightweight, object-oriented programming language inspired by Java and Go.  
It simplifies the Java programming experience by reducing boilerplate while preserving strong typing, classes, and methods.  
Lumina programs are compiled to executable code, combining ease of writing with high performance.

## ✨ Features

- **Simplified Syntax:** Fewer keywords, minimalistic class definitions.
- **Object-Oriented:** Full support for classes, fields, methods, and access control.
- **Executable Output:** Compile to JVM bytecode or native binaries (planned with GraalVM).
- **Concurrent Future:** (Planned) Easy-to-use concurrency model inspired by Go.
- **Tooling First:** Lexer, Parser, Semantic Analyzer, Code Generator — all from scratch.

## 🚀 Example Code (Lumina)

```lumina
class Person {
    var name: String

    greet() {
        print("Hello, " + name)
    }
}
```

## 🛠 Project Structure

```
compiler-core/
├── src/main/java/org/venylang/veny/
│   ├── lexer/
│   │   ├── Lexer.java
│   │   ├── Token.java
│   ├── parser/
│   │   ├── Parser.java
│   │   └── AST/
│   │       ├── ASTNode.java
│   │       ├── ClassNode.java
│   │       ├── MethodNode.java
│   │       ├── FieldNode.java
│   ├── semantic/
│   │   └── SemanticAnalyzer.java
│   ├── codegen/
│   │   └── CodeGenerator.java
│   ├── util/
│   │   └── JavaCompilerInvoker.java
│   └── Compiler.java
├── src/test/java/org/venylang/veny
│   ├── lexer/
│   │   └── LexerTest.java
│   ├── parser/
│       └── ParserTest.java
├── pom.xml
└── README.md
```

## 📚 Build & Run

### Requirements:

- Java 17+
- Maven
- (Optional) GraalVM for native image compilation

### To Build:

```bash
mvn clean install> 🧠 **Future Feature: Implicit Typing in Variable Declarations**  
> Lumina aims to support type inference in variable declarations where the type is obvious from the initializer, such as:  
>  
> ```lumina
> var person = new Person()
> ```  
>  
> This feature is currently not implemented, but future versions will allow omitting the type when it can be clearly inferred at compile time.

```

### To Run Compiler:

```bash
mvn exec:java -Dexec.args="path/to/source.lm"
```

> Note: `.lm` is the suggested file extension for Lumina source files.

## 🔥 Roadmap

- [x] Lexer and Tokenizer
- [ ] Basic Parser and AST generation
- [ ] Semantic Analysis (type checking, scopes)
- [ ] Java code generation
- [ ] Native executable compilation with GraalVM
- [ ] Basic standard library (strings, collections)
- [ ] Concurrency support

## ❤️ Contributing

This project is in early stages!  
If you are excited about language design, compiler theory, or clean Java projects, feel free to join, suggest features, or fix bugs.

## 📄 License

MIT License — free to use, free to build on.

# ✨ Welcome to Lumina ✨
*"Programming should be powerful, but never painful."*
