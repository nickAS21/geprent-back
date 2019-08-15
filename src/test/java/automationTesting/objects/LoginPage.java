package automationTesting.objects;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

    private static final String url = "http://andbilous.com-staging.s3-website.eu-central-1.amazonaws.com/login";
    private static final By loginEmail = By.id("email");
    private static final By password = By.id("password");
    private static final By enterBtn = By.cssSelector("#app > div > div > div:nth-child(3) > div > button:nth-child(3) > span");
    private static final By profileBtn = By.cssSelector("a[href=\"/profile\"]");
    private static final By exitBtn = By.cssSelector("#app > div > nav > div > ul > li > a:nth-child(3) > button > span");



    private WebDriver driver;
    private WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, 5);
    }


    @Step
    public LoginPage open() {
        driver.get(url);
        return this;
    }


    @Step
    public LoginPage enterLoginEmail (String email) {
        driver.findElement(loginEmail).clear();
        driver.findElement(loginEmail).sendKeys(email);
        return this;
    }

    @Step
    public LoginPage enterLoginPassword(String pwd) {
        driver.findElement(password).clear();
        driver.findElement(password).sendKeys(pwd);
        return this;
    }

    @Step
    public LoginPage loginIntoAccount() {
        driver.findElement(enterBtn).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(profileBtn));
        return this;
    }

    public boolean isProfileButtonDisplayed() {
        return driver.findElement(profileBtn).isDisplayed();
    }

}
