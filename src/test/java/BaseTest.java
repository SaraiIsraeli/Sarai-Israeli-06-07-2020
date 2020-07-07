package test.java;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.TakesScreenshot;
import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.ScreenshotException;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public abstract class BaseTest {

    public enum DriverType {
        CHROME, FIREFOX
    }

    protected static WebDriver webDriver;
    private static DriverType driverType = DriverType.CHROME;
    private final static String CHROME_URL = "C:\\chromedriver.exe";
    private final static String FIREFOX_URL = "C:\\geckodriver.exe";
    private static DesiredCapabilities capabilities = new DesiredCapabilities();

    @Rule
    public ScreenshotRule screenshotRule = new ScreenshotRule();


    @BeforeClass
    public static void baseDriver() throws Exception{
        switch (driverType){
            case CHROME:
                System.setProperty("webdriver.chrome.driver",CHROME_URL);
                webDriver = new ChromeDriver();
                break;
            case FIREFOX:
                System.setProperty("webdriver.gecko.driver",FIREFOX_URL);
                webDriver = new FirefoxDriver();
                break;
        }
    }

    @AfterClass
     public static void cleanup(){
      webDriver.quit();
    }

    protected void takeScreenShot (String str){
        try{
            String dirName = "screenshots/";
            new File(dirName).mkdirs();
            File screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            File fileName = new File(dirName+str+".png");
            Files.copy(screenshot.toPath(),fileName.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class ScreenshotRule implements MethodRule {
        @Override
        public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, Object o) {
                try {
                    statement.evaluate();
                    return statement;
                } catch (Throwable t) {
                    takeScreenShot(frameworkMethod.getName() + "-fail");
                    try {
                        throw t;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            return statement;
        }
        }

}
