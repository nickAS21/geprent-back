package automationTesting.objects;

import cucumber.api.java.eo.Se;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Search {

    private static final String url = "http://andbilous.com-staging.s3-website.eu-central-1.amazonaws.com";
    private static final By itemName = By.cssSelector("#app > div > div > div > div.homePageLeft-1EVsWqP > div > div:nth-child(1) > div:nth-child(1) > input");
    private static final By searchNameBtn = By.cssSelector("#app > div > div > div > div.homePageLeft-1EVsWqP > div > div:nth-child(1) > div:nth-child(1) > button");
    private static final By address = By.cssSelector("#app > div > div > div > div.homePageLeft-1EVsWqP > div > div:nth-child(1) > div:nth-child(2) > input");
    private static final By searchAddressBtn = By.cssSelector("#app > div > div > div > div.homePageLeft-1EVsWqP > div > div:nth-child(1) > div:nth-child(2) > button > span");
    private static final By searchWithNameAddressCheckbox = By.cssSelector(".p-checkbox-box");


    private WebDriver driver;
    private WebDriverWait wait;

    public Search(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, 5);
    }


    @Step
    public Search open() {
        driver.get(url);
        return this;
    }


    @Step
    public Search enterItemName (String itemname) {
        driver.findElement(itemName).sendKeys(itemname);
        return this;
    }



    @Step
    public Search clickSearchNameBtn () {
        driver.findElement(searchNameBtn).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#app > div > div > div > div.homePageLeft-1EVsWqP > div > div.lotsListContainer-3DBC5i7 > div:nth-child(2) > div.CardIMGStyle-2pLSQ9H")));
        return this;
    }



    @Step
    public Search enterAddress (String addr) {
        driver.findElement(address).sendKeys(addr);
        return this;
    }


    @Step
    public Search clickSearchAddressBtn () {
        driver.findElement(searchAddressBtn).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#app > div > div > div > div.homePageLeft-1EVsWqP > div > div.lotsListContainer-3DBC5i7 > div:nth-child(2) > div.CardIMGStyle-2pLSQ9H")));
        return this;
    }


    @Step
    public Search clicksearchWithNameAddressCheckbox () {
        driver.findElement(searchWithNameAddressCheckbox).click();
        return this;
    }


    @Step
    public String getItemNameContent() {
        return driver.findElement(itemName).getAttribute("value");
    }

    @Step
    public String getAddressContent() {
        return driver.findElement(address).getAttribute("value");
    }

    @Step
    public String getCheckboxContent() {
        return driver.findElement(searchWithNameAddressCheckbox).getAttribute("value");
    }


}
