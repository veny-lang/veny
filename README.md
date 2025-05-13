# Veny Programming Language

**Veny** is a modern, lightweight, and pure object-oriented programming language designed for clarity, consistency, and expressiveness. Built from the ground up, Veny is ideal for developers who want a fully OO-first approach without the legacy overhead of older languages.

---

## Features

- **Object-Oriented First**: Everything is an object — no primitives, no exceptions.
- **Lightweight Syntax**: Designed for readability and minimalism.
- **Modern Constructs**: Includes class-based inheritance, polymorphism, first-class functions, and clean module support.
- **Cross-Platform Tooling**: Compiler and CLI written in modern Java.

---

## Project Structure

This repository contains multiple subprojects that make up the Veny language ecosystem:

| Subproject      | Description                                      |
|-----------------|--------------------------------------------------|
| `compiler-core` | The main compiler and AST handling logic         |
| `stdlib`        | Core standard library for Veny                   |
| `veny-cli`      | Command-line interface for compiling and running Veny code |

---

## Getting Started

> Note: Veny is in active development and not yet ready for production use.

### Prerequisites

- Java 17+
- Maven 3.9.6

### Build and Run

```bash
# Clone the repository
git clone https://github.com/yourusername/veny.git
cd veny

# Build all modules
mvn clean install

# Run the CLI
./veny-cli/build/libs/veny-cli.jar path/to/file.veny
```

---

## Contributing

We welcome contributions, feedback, and discussions!

- Fork the repo and submit a pull request
- Join discussions on issues or submit a feature idea
- Check out `CONTRIBUTING.md` for guidelines (coming soon)

---

## License

This project is licensed under the [Apache License 2.0](LICENSE).

© 2025 Stoyan Petkov  
Website: [www.venylang.org](https://www.venylang.org)  
Email: [admin@venylang.org](mailto:admin@venylang.org)

---

## Roadmap

- [ ] Parser and AST stabilisation
- [ ] Type inference engine
- [ ] First stable release of `veny-cli`
- [ ] Package manager
- [ ] VS Code integration

---

## Screenshot or Example

```veny
package example

class Person {
    var name: String

    entry(args: [String]): void {
      Person().name("Veny").greet()
    }
 
    greet() {
        System.print("Hello, I am " + name)
    }
}
```

---

## Community & Updates

Stay tuned via [venylang.org](https://www.venylang.org) or follow project updates in this repo. Coming soon: Discord community, blog posts, and release notes.
