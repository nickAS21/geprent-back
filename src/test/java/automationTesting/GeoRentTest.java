package automationTesting;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Assert;
import org.junit.Test;

public class GeoRentTest extends WebBrowserDriverFactory{

    String itemname = "lotName7";
    String addr = "Демеевка";

    String loginEmail = "user1@gmail.com.ua";
    String loginPwd = "12345678";



    @Test
    @DisplayName("Log in with valid credentials")
    public void loginTest() {

        loginPage
                .open()
                .enterLoginEmail(loginEmail)
                .enterLoginPassword(loginPwd)
                .loginIntoAccount();

        Assert.assertEquals(loginPage.isProfileButtonDisplayed(), true);

    }



    @Test
    @DisplayName("Search by itemName is correct")
    public void searchLotByItemNameTest() {
        search
                .open()
                .enterItemName(itemname)
                .clickSearchNameBtn();

        Assert.assertEquals(itemname, search.getItemNameContent());
    }

    @Test
    @DisplayName("Search by itemName is correct")
    public void searchLotByItemAddressTest() {
        search
                .open()
                .enterAddress(addr)
                .clickSearchAddressBtn();

        Assert.assertEquals(addr, search.getAddressContent());
    }


    @Test
    @DisplayName("Search by itemName and address is correct")
    public void searchLotByItemNameAndAddressTest() {
        search
                .open()
                .enterItemName(itemname)
                .enterAddress(addr)
                .clicksearchWithNameAddressCheckbox()
                .clickSearchNameBtn();

        Assert.assertEquals(itemname, search.getItemNameContent());
        Assert.assertEquals(addr, search.getAddressContent());
    }


}
