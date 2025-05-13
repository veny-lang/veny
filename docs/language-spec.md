# Veny Language Specification (v0.5.1)

## Overview

Veny is a statically-typed, object-oriented programming language designed for clarity, brevity, and efficient compilation to Java. It draws inspiration from languages like Kotlin, Python, and Go, while staying grounded in strong static typing.

---

## Syntax and Structure

### 1. **Packages and Files**

* Each file must declare a package:

```veny
package myapp.utils
```

### 2. **Class Declaration**

```veny
class Greeter {
    val name: String = "Alice"

    greet(): void {
        System.print("Hello, " + name)
    }
}
```

* `val` denotes an immutable field.
* `var` denotes a mutable field.

### 3. **Method Declarations**

* Methods are defined within classes.
* Syntax:

```veny
add(x: Int, y: Int): Int {
    return x + y
}
```

### 4. **Entry Point**

```veny
class App {
    entry(args: [String]): void {
        System.print("Welcome to Veny")
    }
}
```

* The `entry` method is the program's starting point.
* `args` is a list of string arguments passed from the CLI.

---

## Types

* **Primitive Types**: `Int`, `Float`, `Bool`, `String`, `Void`
* **Array Types**: `[Int]`, `[Float]`, etc.
* **Type Inference**: Allowed (not implemented at the moment) where context is enough:

```veny
val name = "Veny"  // type inferred as String
```

---

## Statements and Expressions

* Variable declarations:

```veny
val radius: Float = 5.0
var active: Bool = true
```

* Conditionals:

```veny
if active {
    System.print("Running")
} else {
    System.print("Stopped")
}
```

* Loops:

```veny
while active {
    System.print("Looping")
    break
}
```

---

## Arrays

* Declared with `[Type]` syntax:

```veny
val numbers: [Int] = [1, 2, 3, 4]
```

* Array literals support type inference:

```veny
val names = ["Alice", "Bob"]  // Inferred as [String]
```

---

## Standard Library (WIP)

Initial functions include:

```veny
System.print(message: String): void
```

Future expansions will include `String`, `Math`, and `Array` utilities.

---

## Concurrency (Future Work)

* Planned support for fibers:

```veny
Fiber.run {
    // lightweight task
}
```

---

## Tooling

* **Compiler**: Generates Java code.
* **Execution**: Built and run using GraalVM for native executables.

---

## Project Structure

* Compiler: `veny-compiler`
* CLI: `veny-cli`
* Stdlib: `veny-stdlib` (Java project, imported in generated code)

---

## Version

* `v0.5.1`
* Entry point updated to `entry(args: [String])`
