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

package org.testingisdocumenting.znai.parser.sphinx;

import org.testingisdocumenting.znai.structure.TableOfContents;
import org.testingisdocumenting.znai.structure.TocGenerator;
import org.testingisdocumenting.znai.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocTreeTocGenerator implements TocGenerator {
    private final TableOfContents toc;

    public DocTreeTocGenerator() {
        toc = new TableOfContents();
    }

    @Override
    public TableOfContents generate(String indexXml) {
        Document doc = XmlUtils.parseXml(indexXml);
        Element tocTreeWrapper = findTocTreeWrapperElement(doc);
        Element firstBulletList = (Element) tocTreeWrapper.getElementsByTagName("bullet_list").item(0);

        NodeList chapterNodes = firstBulletList.getChildNodes();
        for (int chapterIdx = 0; chapterIdx < chapterNodes.getLength(); chapterIdx++) {
            Node node = chapterNodes.item(chapterIdx);

            if (node.getNodeType() == Node.TEXT_NODE) {
                continue;
            }

            handleChapter(node);
        }

        toc.addIndex();
        return toc;
    }

    private Element findTocTreeWrapperElement(Document doc) {
        NodeList compoundNodes = doc.getElementsByTagName("compound");
        int length = compoundNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node node = compoundNodes.item(i);
            String classes = node.getAttributes().getNamedItem("classes").getTextContent();

            if (classes.equals("toctree-wrapper")) {
                return (Element) node;
            }
        }

        throw new IllegalArgumentException("<compound classes=\"toctree-wrapper\"> element is not found");
    }

    private void handleChapter(Node node) {
        Element element = (Element) node;
        NodeList nestedBulletList = element.getElementsByTagName("bullet_list");
        if (nestedBulletList.getLength() == 0) {
            return;
        }

        Element nestedBulletListElement = (Element) nestedBulletList.item(0);
        Node chapterReference = element.getElementsByTagName("reference").item(0);

        NodeList pages = nestedBulletListElement.getElementsByTagName("reference");

        String sectionTitle = chapterReference.getTextContent();
        for (int pageIdx = 0; pageIdx < pages.getLength(); pageIdx++) {
            Node page = pages.item(pageIdx);
            handlePage(sectionTitle, page);
        }
    }

    private void handlePage(String sectionTitle, Node node) {
        String dirAndFileName = node.getAttributes().getNamedItem("refuri").getTextContent();
        String[] parts = dirAndFileName.split("[/\\\\]");
        if (parts.length != 2) {
            return;
        }

        toc.addTocItem(parts[0], parts[1], sectionTitle);
    }
}
