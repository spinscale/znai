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

package org.testingisdocumenting.znai.structure

import org.junit.Test
import org.testingisdocumenting.znai.core.MarkupPathWithError
import org.testingisdocumenting.znai.parser.PageSectionIdTitle
import org.testingisdocumenting.znai.resources.UnresolvedResourceException

import java.nio.file.Paths
import java.util.stream.Stream

class TableOfContentsTest {
    @Test
    void "should know which toc item is defined and which is not"() {
        def toc = new TableOfContents()
        toc.addTocItem(new TocNameAndOpts("chapter1"), "page-a")
        toc.addTocItem(new TocNameAndOpts("chapter1"), "page-b")

        toc.contains("chapter1", "page-a", "").should == true
        toc.contains("chapter1", "page-c", "").should == false

        toc.contains("chapter1", "page-a", "page section title").should == false
        def tocItem = toc.findTocItem("chapter1", "page-a")

        def pageSection1 = new PageSectionIdTitle("page section title", [:])
        def pageSection2 = new PageSectionIdTitle("another title", [:])
        tocItem.setPageSectionIdTitles([pageSection1, pageSection2])

        toc.contains("chapter1", "page-a", pageSection1.id).should == true
        toc.contains("chapter1", "page-a", "another title").should == false
    }

    @Test
    void "index page title should be empty"() {
        def toc = new TableOfContents()
        toc.addIndex()

        toc.index.pageTitle.should == ""
    }

    @Test
    void "should detect newly added items by comparing with another toc"() {
        def toc = new TableOfContents()
        def updated = new TableOfContents()

        toc.detectNewTocItems(updated).should == []

        toc.addTocItem(new TocNameAndOpts("chapter1"), "page-a")
        toc.addTocItem(new TocNameAndOpts("chapter1"), "page-b")
        toc.addTocItem(new TocNameAndOpts("chapter2"), "page-c")

        updated.addTocItem(new TocNameAndOpts("chapter1"), "page-a")
        updated.addTocItem(new TocNameAndOpts("chapter1"), "page-e")
        updated.addTocItem(new TocNameAndOpts("chapter2"), "page-c")
        updated.addTocItem(new TocNameAndOpts("chapter2"), "page-d")

        def newItems = toc.detectNewTocItems(updated)
        newItems.should == ['dirName'  | 'fileNameWithoutExtension'] {
                           ________________________________________
                            'chapter1' | 'page-e'
                            'chapter2' | 'page-d'  }
    }

    @Test
    void "should detect removed items by comparing with another toc"() {
        def toc = new TableOfContents()
        def updated = new TableOfContents()

        toc.detectRemovedTocItems(updated).should == []

        toc.addTocItem(new TocNameAndOpts("chapter1"), "page-a")
        toc.addTocItem(new TocNameAndOpts("chapter1"), "page-e")
        toc.addTocItem(new TocNameAndOpts("chapter2"), "page-c")
        toc.addTocItem(new TocNameAndOpts("chapter2"), "page-d")

        updated.addTocItem(new TocNameAndOpts("chapter1"), "page-a")
        updated.addTocItem(new TocNameAndOpts("chapter1"), "page-b")
        updated.addTocItem(new TocNameAndOpts("chapter2"), "page-c")

        def removedItems = toc.detectRemovedTocItems(updated)
        removedItems.should == ['dirName'  | 'fileNameWithoutExtension'] {
                               ________________________________________
                                'chapter1' | 'page-e'
                                'chapter2' | 'page-d'  }
    }

    @Test
    void "should resolve toc item paths and detect missing"() {
        def pathA = Paths.get("/path/a")
        def pathC = Paths.get("/path/c")
        def pathD = Paths.get("/path/d")

        def toc = new TableOfContents()

        def tocItemA = toc.addTocItem(new TocNameAndOpts("chapter1"), "page-a")
        toc.addTocItem(new TocNameAndOpts("chapter1"), "page-e")
        def tocItemC = toc.addTocItem(new TocNameAndOpts("chapter2"), "page-c")
        toc.addTocItem(new TocNameAndOpts("chapter2"), "page-d")

        def filePathResolver = { tocItem ->
            if (tocItem == tocItemA) {
                return new MarkupPathWithError(pathA, null)
            }
            if (tocItem == tocItemC)  {
                return new MarkupPathWithError(pathC, null)
            }

            return new MarkupPathWithError(null, new UnresolvedResourceException(Stream.empty(), "files"))
        }

        def missing = toc.resolveTocItemPathsAndReturnMissing(filePathResolver)
        missing.should == ['dirName'  | 'fileNameWithoutExtension'] {
                           ________________________________________
                           'chapter1' | 'page-e'
                           'chapter2' | 'page-d'  }

        toc.findTocItem(pathC).should == [dirName: "chapter2", fileNameWithoutExtension: "page-c"]
        toc.findTocItem(pathD).should == null
    }
}
