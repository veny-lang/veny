# 📘 Markdown Syntax Cheat Sheet

## 🏷️ Headings
Use `#` for headings:

```
markdown

# H1 - Project Title
## H2 - Section
### H3 - Subsection
#### H4
```
## 🔢 Ordered Lists
```
markdown

1. First item
2. Second item
3. Third item

```
   ✅ You can also use 1. repeatedly, Markdown auto-numbers:

```
markdown

1. Item one
1. Item two
1. Item three
```

Copy
Edit
1. Item one
1. Item two
1. Item three
   🔹 Unordered Lists
   markdown
   Copy
   Edit
- Bullet item
* Another bullet
+ Yet another
  ✍️ Text Formatting
  markdown
  Copy
  Edit
  *Italic* or _Italic_

**Bold** or __Bold__

***Bold Italic***

~~Strikethrough~~
📄 Code
Inline code
markdown
Copy
Edit
Use `org.venylang.stdlib.io` as the I/O package.
Code block
<pre> ```java package org.venylang.veny.codegen; public class Generator { // ... } ``` </pre>
🔗 Links
markdown
Copy
Edit
[OpenAI](https://openai.com)
Reference-style:
markdown
Copy
Edit
[OpenAI][1]

[1]: https://openai.com
🖼️ Images
markdown
Copy
Edit
![Alt text](https://example.com/image.png)
📋 Blockquotes
markdown
Copy
Edit
> This is a blockquote.
>> Nested quote.
📐 Tables
markdown
Copy
Edit
| Module        | Description                |
|---------------|----------------------------|
| compiler-core | Compiler internals         |
| stdlib        | Standard library packages  |
| veny-cli      | Command-line interface     |
🔄 Horizontal Rule / Divider
markdown
Copy
Edit
---
📚 Task Lists
markdown
Copy
Edit
- [x] Finish compiler-core
- [ ] Write docs for stdlib
- [ ] Add REPL to veny-cli
  📦 Escaping Markdown (like dots in package names)
  Use backslashes to prevent formatting:

markdown
Copy
Edit
1\. `org.venylang.stdlib.io`
This covers all the essentials you'll likely need for writing clear, professional project documentation in Markdown.