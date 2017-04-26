package com.twosigma.testing.webui.page.path;

import com.twosigma.testing.webui.page.ElementValue;
import com.twosigma.testing.webui.page.NullWebElement;
import com.twosigma.testing.webui.page.PageElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author mykola
 */
public class GenericPageElement implements PageElement {
    private boolean isMultipleElements;
    private WebDriver driver;
    private ElementPath path;
    private ElementValue<String> elementValue;

    public GenericPageElement(WebDriver driver, ElementPath path) {
        this.driver = driver;
        this.path = path;
        this.elementValue = new ElementValue<>("value", this::fetchValue);
    }

    public PageElement all() {
        GenericPageElement element = new GenericPageElement(driver, path);
        element.isMultipleElements = true;

        return element;
    }

    public void click() {
        findElement().click();
    }

    public WebElement findElement() {
        List<WebElement> webElements = path.find(driver);
        return webElements.isEmpty() ?
                new NullWebElement(path.toString()) :
                webElements.get(0);
    }

    @Override
    public ElementValue<?> elementValue() {
        return new ElementValue<>("todo", this::getUnderlyingValue);
    }

    @Override
    public void setValue(Object value) {
        findElement().sendKeys(value.toString());
    }

    private String fetchValue() {
        return findElement().getText();
    }

    private String getText() {
        return findElement().getText();
    }

    private String getTagName() {
        return findElement().getTagName();
    }

    private String getAttribute(String name) {
        return findElement().getAttribute(name);
    }

    private Object getUnderlyingValue() {
        String tagName = getTagName().toUpperCase();
        return (tagName.equals("INPUT") || tagName.equals("TEXTAREA")) ?
                getAttribute("value") : getText();
    }
}
