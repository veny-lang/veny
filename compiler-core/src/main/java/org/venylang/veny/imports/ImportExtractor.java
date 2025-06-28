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

package org.venylang.veny.imports;

import org.venylang.veny.parser.ast.VenyFile;

import java.util.ArrayList;
import java.util.List;

public class ImportExtractor {

    public static List<ImportRecord> extract(VenyFile file) {
        List<ImportRecord> imports = new ArrayList<>();

        for (String fullImport : file.imports()) {
            int lastDot = fullImport.lastIndexOf('.');
            if (lastDot == -1 || lastDot == fullImport.length() - 1) {
                // Invalid import format: no dot or ends with dot
                // You may want to log or throw an exception here
                continue;
            }

            String packageName = fullImport.substring(0, lastDot);
            String className = fullImport.substring(lastDot + 1);
            imports.add(new ImportRecord(packageName, className));
        }

        return imports;
    }
}