# Veny Language Roadmap (v0.4 → 1.0 and beyond)

---

## Phase 1: Foundation & Tooling (v0.4 → v0.6)
**Goal:** Establish a complete pipeline from source to executable, including semantics.

### Features:
- [x] Lexer, parser, AST, and semantic analyzer
- [x] Access modifiers (`pub`, `pri`) and mutability (`val`, `var`)
- [ ] Add interfaces, single inheritance
- [ ] Improve error recovery and error messages
- [ ] Basic standard library (Strings, Lists, IO)
- [ ] Java code generator (initial form)
- [ ] Unit test framework for Veny
- [ ] CLI tool to compile & run Veny code (`veny <file>`)

**Milestone:** Can build OO apps and compile to Java for execution via Graal or JVM

---

## Phase 2: Usability & Developer Experience (v0.7 → v0.9)
**Goal:** Make Veny ergonomic and pleasant to develop in.

### Features:
- [ ] REPL / scripting support
- [ ] IDE integration (VS Code or LSP support)
- [ ] Type inference (where appropriate)
- [ ] Function overloads & default arguments
- [ ] Native `Optional<T>` support (with safe access syntax)
- [ ] No-null mode (or eliminate `null` altogether)
- [ ] Improved diagnostics & linter

**Milestone:** Developer-ready language with great syntax, tooling, and safety

---

## Phase 3: Intermediate Features & Interop (v1.0 Beta)
**Goal:** Expand the language into a full-scale OO platform.

### Features:
- [ ] Traits or mixins
- [ ] Object-oriented patterns: `abstract` classes, `final`, `sealed`
- [ ] Java interoperability (import and use Java classes)
- [ ] Collections and concurrency libraries
- [ ] Standard library design stabilization

**Milestone:** A production-ready language suitable for real-world use

---

## Phase 4: Advanced Features & Native Execution (v1.0 Release & Beyond)
**Goal:** Unlock high-performance use cases and advanced design capabilities.

### Features:
- [ ] Coroutines / `async`/`await`
- [ ] Pattern matching (e.g., for `sealed` classes or `Optional`)
- [ ] Compile to bytecode (ASM) or directly to Graal native image
- [ ] Module system and package manager
- [ ] Performance optimization & benchmarks

**Milestone:** Fast, expressive, modern language ready for any scale

---

This document is part of the Veny programming language project.

© 2025 Stoyan Petkov  
Website: [www.venylang.org](https://www.venylang.org)  
Email: [admin@venylang.org](mailto:admin@venylang.org)  
Licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
