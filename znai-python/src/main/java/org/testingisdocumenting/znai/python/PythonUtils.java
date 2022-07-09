/*
 * Copyright 2022 znai maintainers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.testingisdocumenting.znai.python;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class PythonUtils {
    public static class FileNameAndRelativeName {
        private final String file;
        private final String packageName;
        private final String relativeName;

        public FileNameAndRelativeName(String file, String packageName, String relativeName) {
            this.file = file;
            this.packageName = packageName;
            this.relativeName = relativeName;
        }

        public String getFile() {
            return file;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getRelativeName() {
            return relativeName;
        }
    }

    static String convertQualifiedNameToFilePath(String qualifiedName) {
        String[] parts = splitIntoParts(qualifiedName);
        return combineFileNameParts(parts, 1);
    }

    static List<String> convertQualifiedNameToMultipleFilePaths(String qualifiedName) {
        String[] parts = splitIntoParts(qualifiedName);

        List<String> result = new ArrayList<>();
        for (int partsToOmit = 1; partsToOmit < parts.length; partsToOmit++) {
            result.add(combineFileNameParts(parts, partsToOmit));
        }

        return result;
    }

    static String entityNameFromQualifiedName(String qualifiedName) {
        String[] parts = qualifiedName.split("\\.");
        return parts[parts.length - 1];
    }

    static List<FileNameAndRelativeName> entityNameFileNameCombos(String qualifiedName) {
        String[] parts = qualifiedName.split("\\.");

        List<FileNameAndRelativeName> result = new ArrayList<>();
        for (int namePartsToUse = 1; namePartsToUse < parts.length; namePartsToUse++) {
            String relativeName = Arrays.stream(parts)
                    .skip(parts.length - namePartsToUse)
                    .collect(Collectors.joining("."));

            String fileName = combineFileNameParts(parts, namePartsToUse);
            String packageName = combinePackageNameParts(parts, namePartsToUse);

            result.add(new FileNameAndRelativeName(fileName, packageName, relativeName));
        }

        return result;
    }

    private static String combineFileNameParts(String[] parts, int partsToOmit) {
        return Arrays.stream(parts)
                .limit(parts.length - partsToOmit)
                .collect(Collectors.joining("/")) + ".py";
    }

    private static String combinePackageNameParts(String[] parts, int partsToOmit) {
        return Arrays.stream(parts)
                .limit(parts.length - partsToOmit)
                .collect(Collectors.joining("."));
    }

    private static String[] splitIntoParts(String qualifiedName) {
        String[] parts = qualifiedName.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("expect the qualified name to be of a form: module.[optional.].name, given: " + qualifiedName);
        }
        return parts;
    }
}
