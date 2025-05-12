package org.venylang.veny.util;

public enum Visibility {
    PUBLIC,
    PRIVATE,
    DEFAULT  // Will be interpreted based on context (e.g., field = private, method = public)
}