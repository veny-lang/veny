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
