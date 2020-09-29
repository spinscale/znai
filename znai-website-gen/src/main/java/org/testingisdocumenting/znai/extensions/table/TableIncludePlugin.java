/*
 * Copyright 2020 znai maintainers
 * Copyright 2019 TWO SIGMA OPEN SOURCE, LLC
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

package org.testingisdocumenting.znai.extensions.table;

import org.testingisdocumenting.znai.core.AuxiliaryFile;
import org.testingisdocumenting.znai.core.ComponentsRegistry;
import org.testingisdocumenting.znai.extensions.PluginParams;
import org.testingisdocumenting.znai.extensions.PluginResult;
import org.testingisdocumenting.znai.extensions.include.IncludePlugin;
import org.testingisdocumenting.znai.parser.MarkupParser;
import org.testingisdocumenting.znai.parser.ParserHandler;
import org.testingisdocumenting.znai.resources.ResourcesResolver;
import org.testingisdocumenting.znai.search.SearchScore;
import org.testingisdocumenting.znai.search.SearchText;

import java.nio.file.Path;
import java.util.stream.Stream;

public class TableIncludePlugin implements IncludePlugin {
    private Path fullPath;
    private TableDocElementFromParams docElementFromParams;

    @Override
    public String id() {
        return "table";
    }

    @Override
    public IncludePlugin create() {
        return new TableIncludePlugin();
    }

    @Override
    public PluginResult process(ComponentsRegistry componentsRegistry,
                                ParserHandler parserHandler,
                                Path markupPath,
                                PluginParams pluginParams) {
        ResourcesResolver resourcesResolver = componentsRegistry.resourceResolver();
        MarkupParser parser = componentsRegistry.defaultParser();
        String fileName = pluginParams.getFreeParam();
        String textContent = resourcesResolver.textContent(fileName);

        fullPath = resourcesResolver.fullPath(fileName);

        docElementFromParams = new TableDocElementFromParams(pluginParams, parser, resourcesResolver, fullPath, textContent);
        return docElementFromParams.create();
    }

    @Override
    public Stream<AuxiliaryFile> auxiliaryFiles(ComponentsRegistry componentsRegistry) {
        return Stream.concat(
                Stream.of(AuxiliaryFile.builtTime(fullPath)),
                docElementFromParams.mappingAuxiliaryFile());
    }

    @Override
    public SearchText textForSearch() {
        return SearchScore.STANDARD.text(docElementFromParams.getRearrangedTable().allText());
    }
}
