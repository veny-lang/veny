package org.venylang.veny.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Utility class for parsing Lumina source files to extract package declarations and import statements.
 * <p>
 * This class provides a static method to read a Lumina source file and return a {@link ParsedFile}
 * containing its package name and import statements.
 */
public class ParsedFileExtractor {

    private static final String LUMINA_EXTENSION = ".lm";
    private static final String LUMINA_PACKAGE_PREFIX = "package ";
    private static final String LUMINA_IMPORT_PREFIX = "import ";

    private Path path;

    public static ParsedFileExtractor of(Path path) {
        return new ParsedFileExtractor(path);
    }

    private ParsedFileExtractor() {}

    private ParsedFileExtractor(Path path) {
        this.path = path;
    }

    public Optional<ParsedFile> extract() throws IOException {
        String source = Files.readString(path);
        String packageName = null;
        List<String> imports = new ArrayList<>();

        String[] lines = source.split("\\R");
        for (String line : lines) {
            line = line.strip();

            if (line.startsWith(LUMINA_PACKAGE_PREFIX)) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    packageName = parts[1];
                }
            }

            if (line.startsWith(LUMINA_IMPORT_PREFIX)) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    imports.add(parts[1]);
                }
            }
        }

        if (packageName == null) {
            return Optional.empty();
        }

        return Optional.of(new ParsedFile(path, packageName, imports));
    }
}

