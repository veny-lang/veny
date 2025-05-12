package org.venylang.veny.util;

import java.nio.file.Path;
import java.util.List;

public record ParsedFile(
        Path path,
        String packageName,
        List<String> imports
) {}
