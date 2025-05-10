
# Veny: The Language

Veny is a modern, statically typed, object-oriented programming language designed with a focus on clarity, composability, and performance. It aims to combine the simplicity of early languages like C and Pascal with the modeling power of modern object-oriented paradigms. Inspired by the readability of Python, the conciseness of Kotlin, the structure of Java, and the tooling philosophy of Go, Veny strives to offer a language that is both expressive and pragmatic.

This document is an informal but precise introduction to the core ideas and features of Veny. It is intended as a practical guide for writing and reasoning about Veny programs.

## 1. Hello, Veny

Every Veny program begins with a `package` declaration, followed by class definitions. There are no global functions—everything lives in a class.

```veny
package hello

class Hello {
    run(): void {
        System.print("Hello, world!")
    }
}

Veny uses methods (not free functions) and class-based organization, but its syntax is kept minimal and expressive.

2. Classes and Fields

Classes are defined with the class keyword. Inside, you declare fields and methods. Fields are private by default.

class Greeter {
    val greeting: String = "Hello"

    greet(name: String): void {
        System.print(greeting + ", " + name)
    }
}

To expose a field outside the class, use the pub modifier:

pub val version: String = "v0.5"

3. Visibility and Accessors

Veny’s visibility system is designed to reduce boilerplate:

Fields are private by default.

Methods are public by default.

Use pub for public fields, pri for private methods.


Public fields automatically gain accessors:

obj.field() → getter

obj.field(value) → setter (only for var fields)


pub var count: Int = 0

run(): void {
    count(42)                  // setter
    System.print(count())      // getter
}

If you declare a method with the same name as a field, it overrides the auto-generated accessor.

4. Variables and Types

Veny uses val for immutable bindings and var for mutable ones:

val name: String = "Alice"
var score: Int = 100

The type system is static and explicit. The compiler checks all types at compile time.

5. Expressions and Statements

Everything in Veny is an expression where possible. You write code like this:

if score > 90 {
    System.print("Excellent")
} else {
    System.print("Keep trying")
}

while active {
    tick()
    break
}

Assignments and arithmetic follow standard forms:

var x: Int = 10
x = x + 1

6. Object Construction and Method Calls

Objects are created by calling the class as a function:

val g: Greeter = Greeter()
g.greet("Ada")

Method calls use dot syntax and always pass the receiver as an implicit this.

7. The Philosophy of Veny

Veny is opinionated:

Everything is a class or a method.

Clarity is favored over flexibility.

Access control is minimal but meaningful.

Boilerplate is the enemy.


Veny draws on:

The structural clarity of C and Pascal

The expressive, OO-first mindset of Java

The concise and readable syntax of Kotlin

The smooth on-ramp and productivity of Python

The tooling discipline and build philosophy of Go


The goal is to make Veny programs easy to read, write, and maintain—while still being powerful enough for real systems.

8. What’s Next

This guide covers the core of the language. The full specification includes additional features like:

Package structure

Type system details

Planned concurrency model

Compilation targets


As Veny evolves, so will this guide.


---

This document is a work in progress. Contributions, suggestions, and ideas are welcome.

---

Let me know when it’s in—next I can help you format the `spec.md`, update your `README.md`, or prep GitHub Pages.

