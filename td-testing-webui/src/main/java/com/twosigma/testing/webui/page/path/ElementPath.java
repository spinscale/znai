package com.twosigma.testing.webui.page.path;

import com.twosigma.testing.webui.page.path.selector.CssSelector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mykola
 */
public class ElementPath {
    private List<ElementPathEntry> entries;

    public ElementPath() {
        entries = new ArrayList<>();
    }

    public void addSelector(ElementsSelector selector) {
        ElementPathEntry entry = new ElementPathEntry(selector);
        entries.add(entry);
    }

    public static ElementPath css(String selector) {
        ElementPath path = new ElementPath();
        path.addSelector(new CssSelector(selector));

        return path;
    }

    public List<WebElement> find(WebDriver driver) {
        WebElement root = null;

        List<WebElement> webElements = Collections.emptyList();
        for (ElementPathEntry entry : entries) {
            webElements = entry.find(driver, root);
            if (webElements.isEmpty()) {
                return webElements;
            }

            root = webElements.get(0);
        }

        return webElements;
    }

    @Override
    public String toString() {
        return entries.stream().map(ElementPathEntry::toString)
                .collect(Collectors.joining(", "));
    }
}
