package automationTesting.objects;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Profile {

    private static final String url = "http://andbilous.com-staging.s3-website.eu-central-1.amazonaws.com";
    private static final By myLots = By.cssSelector("#app > div > div > div > div.profilePageRight-1BWBC2t > a:nth-child(8)");
    private static final By addLots = By.cssSelector("#app > div > div > div > div.profilePageRight-1BWBC2t > a:nth-child(11)");
    private static final By editProfile = By.cssSelector("#app > div > div > div > div.profilePageRight-1BWBC2t > a:nth-child(14)");




    private WebDriver driver;
    private WebDriverWait wait;

    public Profile(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, 5);
    }


    @Step
    public Profile open() {
        driver.get(url);
        return this;
    }


    @Step
    public Profile goToMyLotsList() {
        driver.findElement(myLots).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#app > div > div > div > div > h1"), "Lots list"));
        return this;
    }


    @Step
    public Profile goToAddNewLotPage() {
        driver.findElement(addLots).click();
        return this;
    }


    @Step
    public Profile goToEditeProfileList() {
        driver.findElement(editProfile).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#app > div > div > h2"), "Profile"));
        return this;
    }

}
