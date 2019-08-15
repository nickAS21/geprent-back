package automationTesting;

import automationTesting.objects.*;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.stqa.selenium.factory.WebDriverPool;

public class WebBrowserDriverFactory {


    public static WebDriver driver;
    public  static WebDriverWait wait;

    RegisterPage registerPage;
    LoginPage loginPage;
    Search search;
    Profile profile;
    EditeProfile editeProfile;


    @Before
    public void setupClass() {

        driver = WebDriverPool.DEFAULT.getDriver(new ChromeOptions());

        driver.manage().window().maximize();
        wait= new WebDriverWait(driver, 15);

        registerPage = new RegisterPage(driver);
        loginPage = new LoginPage(driver);
        search = new Search(driver);
        profile = new Profile(driver);
        editeProfile = new EditeProfile(driver);
    }


    @After
    public void closeBrowser(){
        WebDriverPool.DEFAULT.dismissAll();
    }
}
