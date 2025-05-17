# Veny Language Bootstrapping Roadmap

This document outlines the high-level roadmap to take **Veny** from a Java-hosted prototype to a fully self-hosted, optionally native, modern programming language.

---

## Phase 1: Java-based Bootstrap Compiler ✅

- Implement `venyc` in Java
- Modules:
    - `compiler-core`: Lexer, Parser, Analyzer, CodeGen (Java source)
    - `cli`: Command-line tool (`venyc`)
    - `stdlib`: Built-in APIs (Java or Veny)
- Output: Veny → `.java` → `.class` via `javac`
- Build system: Maven (multimodule)

> **Goal:** Working language prototype and CLI using Java tooling.

---

## Phase 2: Self-Compiling Compiler

- Re-implement `compiler-core` in Veny
    - Start with basic components: `Token`, `Lexer`, `AST`
- Use Java-hosted `venyc` to compile Veny source into `.java`
- Gradually replace Java classes with Veny-compiled equivalents

> **Goal:** Use Veny to compile parts of its own compiler.

---

## Phase 3: CLI Tool (`venyc`) in Veny

- Rewrite the CLI in Veny using OO design
    - `Command`, `CompileCommand`, etc.
- Compile CLI code using Veny
- Java wrapper remains temporarily for bootstrapping

> **Goal:** `venyc` written and compiled using Veny.

---

## Phase 4: Direct JVM Bytecode Generation

- Add backend to generate `.class` files directly from Veny
- Remove `javac` dependency
- Possible tools: ASM, BCEL, or custom bytecode emitter

> **Goal:** Veny → `.class` → run (no Java source step).

---

## Phase 5: Native Binary Distribution (Optional)

- Option A: Use GraalVM to build `venyc` as a native executable
- Option B: Build a native backend (e.g., Veny → LLVM IR → native)
- Package `venyc` + stdlib as a portable binary

> **Goal:** Fast, standalone CLI like Go or Rust.

---

## Phase 6: Toolchain Maturity

- Implement `venyc test`, `venyc fmt`, `venyc repl`
- Add package manager (e.g., `veny mod`)
- CI bootstrapping: Veny builds itself
- Language spec, documentation, official standard library

> **Goal:** Fully self-hosted, production-ready language and tools.

---

## Long-Term Vision

- Clean OO-first language with modern features
- Excellent tooling and fast feedback loops
- Education-friendly, systems-ready, and hackable

