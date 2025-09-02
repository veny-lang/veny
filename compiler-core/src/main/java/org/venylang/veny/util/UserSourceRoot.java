/*
 * Copyright 2025 Stoyan Petkov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.venylang.veny.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserSourceRoot implements SourceRoot{
    private final Path basePath;

    public UserSourceRoot(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    public Path rootPath() {
        return basePath;
    }

    @Override
    public List<SourceFile> loadSources() throws IOException {
        List<SourceFile> result = new ArrayList<>();
        if (Files.exists(basePath)) {
            Files.walk(basePath)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".veny"))
                .forEach(p -> {
                    try {
                        result.add(SourceFile.of(p));
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to read file: " + p, e);
                    }
                });
        }
        return result;
    }

    @Override
    public boolean isDevOverride() {
        return false;
    }
}
