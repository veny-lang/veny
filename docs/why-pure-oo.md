## Why Pure OO?

Veny is designed as a *pure object-oriented* language, which means **there are no free-floating functions or global variables** — everything is a method on a class. This design choice is intentional and offers several benefits:

### 1. Strong Encapsulation

By tying behavior strictly to objects, Veny ensures that data and methods that operate on that data live together. This reduces unexpected side effects and enforces clear boundaries between components.

### 2. Improved Modularity and Composability

Since all code lives inside classes, you build software by composing objects and their interactions. This leads to clearer, more maintainable architectures and reusable components.

### 3. Avoiding Global State and Side Effects

Global functions and data can create hidden dependencies and make reasoning about code difficult. Veny avoids these pitfalls by disallowing global scope functions, encouraging better software practices and easier testing.

### 4. Consistent Mental Model

When every action is a message sent to an object, developers benefit from a uniform way of thinking about programs, simplifying learning and reasoning.

By embracing *pure* OO principles, Veny helps you write code that’s clearer, safer, and easier to evolve.
