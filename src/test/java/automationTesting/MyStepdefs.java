package automationTesting;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class MyStepdefs {

    @Given("GeoRent  is opened in my browser")
    public void geoRent_is_opened_in_my_browser() {
        open("http://andbilous.com-staging.s3-website.eu-central-1.amazonaws.com");
    }


    @When("^press button with text \"([^\"]*)\"$")
    public void pressButtonWithText(String button) {
        $(byText(button)).click();

    }



    @And("^press button with value \"([^\"]*)\"$")
    public void pressButtonWithValue(String value) {
        $(byCssSelector("#app > div > div > div:nth-child(3) > div > button:nth-child(3) > span")).shouldHave(text(value)).click();

    }


    @And("^click the link with value \"([^\"]*)\"$")
    public void clickOnLinkWithValue(String value) {
        $(byCssSelector("#app > div > div > div > div.profilePageRight-1BWBC2t > a:nth-child(14)")).shouldHave(text(value)).click();

    }


    @And("select button with text \"([^\"]*)\"")
    public void selectButtonWithText(String button) {
        $(byText(button)).click();

    }


    @And("type to input with name \"([^\"]*)\" text: \"([^\"]*)\"")
    public void typeToInputWithNameText(String name, String text) {
        $(byAttribute("id",name)).clear();
        $(byAttribute("id",name)).sendKeys(text);
    }


    @And("type to input with nameField text: \"([^\"]*)\"")
    public void typeToInputWithNameField(String text) {
        $(byId("firstName")).sendKeys(text);
    }


    @And("type to input with lastNameField text: \"([^\"]*)\"")
    public void typeToInputWithLastNameField(String text) {
        $(byId("lastName")).sendKeys(text);
    }




    @And("type to input with field text: \"([^\"]*)\"")
    public void typeToInputWithField(String text) {
        $(byId("phoneNumber")).sendKeys(text);
    }



    @And("press Save button")
    public void pressElementWithTextSave() {
        $(byCssSelector("#app > div > div > div.profileEditWrapper-2CPs4Mc > div.form-container.card > form > div > button:nth-child(5) > span")).click();

    }


    @And("press Exit button")
    public void pressElementWithTextExit() {
        $(byCssSelector("#app > div > nav > div > ul > li > a:nth-child(3) > button > span")).click();

    }



    @And("press Reset button")
    public void pressElementWithTextReset() {
        $(byCssSelector("#app > div > div > div.profileEditWrapper-2CPs4Mc > div.form-container.card > form > div > button:nth-child(4) > span")).click();

    }


}
