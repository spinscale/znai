/*
 * Copyright 2022 znai maintainers
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

package org.testingisdocumenting.znai.search

import org.testingisdocumenting.znai.structure.TocItem
import org.junit.Test
import org.testingisdocumenting.znai.structure.TocNameAndOpts

class PageSearchEntriesTest {
    @Test
    void "should generate list representation of entries for local search indexer"() {
        def searchEntries = new PageSearchEntries(
                new TocItem(new TocNameAndOpts('dir-name'), 'file-name'),
                [new PageSearchEntry('section one', SearchScore.STANDARD.text('hello world')),
                 new PageSearchEntry('section two', SearchScore.STANDARD.text('how is the weather')),
                ])

        searchEntries.toListOfLists().should == [
                ['dir-name@@file-name@@section-one', 'Dir Name', 'File Name', 'section one', 'hello world'],
                ['dir-name@@file-name@@section-two', 'Dir Name', 'File Name', 'section two', 'how is the weather']]
    }
}
