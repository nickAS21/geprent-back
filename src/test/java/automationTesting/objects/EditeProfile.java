package automationTesting.objects;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EditeProfile {

    private static final String url = "http://andbilous.com-staging.s3-website.eu-central-1.amazonaws.com";
    private static final By firstName = By.id("firstName");
    private static final By lastName = By.id("lastName");
    private static final By phoneNumber = By.id("phoneNumber");
    private static final By resetBtn = By.cssSelector("#app > div > div > div.profileEditWrapper-2CPs4Mc > div.form-container.card > form > div > button:nth-child(4) > span");

    private static final By saveBtn = By.cssSelector("#app > div > div > div.profileEditWrapper-2CPs4Mc > div.form-container.card > form > div > button:nth-child(5) > span");
    private static final By changePasswordBtn = By.cssSelector("#app > div > div > div.profileEditWrapper-2CPs4Mc > div.form-container.card > form > div > button:nth-child(6) > span");



    private WebDriver driver;
    private WebDriverWait wait;

    public EditeProfile(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, 5);
    }


    @Step
    public EditeProfile open() {
        driver.get(url);
        return this;
    }



    @Step
    public EditeProfile enterFirstName (String firstname) {
        driver.findElement(firstName).sendKeys(firstname);
        return this;
    }


    @Step
    public EditeProfile enterLastName (String lastname) {
        driver.findElement(lastName).sendKeys(lastname);
        return this;
    }


    @Step
    public EditeProfile enterPhoneNumber (String phonenumber) {
        driver.findElement(phoneNumber).sendKeys(phonenumber);
        return this;
    }



    @Step
    public EditeProfile updateUserInfo() {
        driver.findElement(saveBtn).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(.,'User data updated successfully!')]")));
        return this;
    }



    @Step
    public EditeProfile resetUserInfo() {
        driver.findElement(resetBtn).click();
        return this;
    }



}
