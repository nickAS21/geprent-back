package automationTesting.objects;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegisterPage {


    private static final String url = "http://andbilous.com-staging.s3-website.eu-central-1.amazonaws.com/signup";

    private static final By firstName = By.id("firstName");
    private static final By lastName = By.id("lastName");
    private static final By registerEmail = By.id("email");
    private static final By phoneNumber = By.id("phoneNumber");
    private static final By password = By.id("password");
    private static final By repeatPassword = By.id("repeatPassword");
    private static final By registerBtn = By.cssSelector("#app > div > div > div.registrationPageRight-xnj87_W > div > form > button > span");

    private static final By profileBtn = By.cssSelector("a[href=\"/profile\"]");
    private static final By exitBtn = By.cssSelector("#app > div > nav > div > ul > li > a:nth-child(3) > button > span");



    private WebDriver driver;
    private WebDriverWait wait;

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, 5);
    }


    @Step
    public RegisterPage open() {
        driver.get(url);
        return this;
    }


    @Step
    public RegisterPage enterFirstName (String firstname) {
        driver.findElement(firstName).sendKeys(firstname);
        return this;
    }


    @Step
    public RegisterPage enterLastName (String lastname) {
        driver.findElement(lastName).sendKeys(lastname);
        return this;
    }


    @Step
    public RegisterPage enterPhoneNumber (String phonenumber) {
        driver.findElement(phoneNumber).sendKeys(phonenumber);
        return this;
    }


    @Step
    public RegisterPage enterRegistrationEmail (String email) {
        driver.findElement(registerEmail).sendKeys(email);
        return this;
    }

    @Step
    public RegisterPage enterRegistrationPassword(String pwd) {
        driver.findElements(password).get(0).sendKeys(pwd);
        return this;
    }


    @Step
    public RegisterPage repeatRegistrationPassword(String pwd) {
        driver.findElements(repeatPassword).get(0).sendKeys(pwd);
        return this;
    }


    @Step
    public RegisterPage createNewUser() {
        driver.findElements(registerBtn).get(0).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(exitBtn));
        return this;
    }

    public boolean isProfileButtonDisplayed() {
        return driver.findElement(profileBtn).isDisplayed();
    }

}
