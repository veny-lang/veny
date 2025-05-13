### Future Enhancements for Condition Expressions

Once you're ready to revisit conditions, here are a few common enhancements you might consider:

- **Support for logical expressions**  
  Allow operators like `&&`, `||`, and `!` to enable more expressive conditional logic.

- **Parenthesized sub-expressions**  
  Permit grouping with parentheses to control evaluation order, e.g., `a && (b || c)`.

- **Comparison chaining**  
  Enable clean chaining like `a < b && b < c` for more readable comparisons.

- **Boolean variable names and function calls**  
  Accept identifiers and calls that return booleans directly in conditionals, e.g.,  
  `if isReady` or `if checkStatus()`.

🌟 For Statement Summary

| Style       | Syntax Example           | Complexity | Suggestion       |
| ----------- | ------------------------ | ---------- | ---------------- |
| For-each    | `for item in list`       | Easy       | ✅ Start here     |
| Range-based | `for i in 0..10`         | Medium     | ✅ Optional later |
| C-style     | `for i = 0; i < 10; i++` | Hard       | ❌ Skip for now   |

---
**Issues**

1. **String interpolation / formatting**
You show "Hello, " + name — that's fine.
But will you later want native interpolation like "Hello, ${name}"?
(If so, plan it early to avoid syntax conflicts.)


2. **Type Inference** (Future?)
   You currently require type annotations, which is safe.
   However, would you eventually allow var x = 5 (infer Int)?
3. Null Safety or Optional
---

This document is part of the Veny programming language project.

© 2025 Stoyan Petkov  
Website: [www.venylang.org](https://www.venylang.org)  
Email: [admin@venylang.org](mailto:admin@venylang.org)  
Licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
