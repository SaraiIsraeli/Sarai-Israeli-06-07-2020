package test.java;


import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class webTest extends BaseTest{

    private final static String HOME_PAGE_TITLE = "הירולו - חברת פיתוח מובילה המתמחה בפתרונות פרונט אנד";
    private final static String WHATSAPP_URL = "https://api.whatsapp.com/send?phone=972544945333";
    private final static String FACEBOOK_URL = "https://www.facebook.com/Herolofrontend";
    private final static String INSTAGRAM_URL = "https://www.instagram.com/herolofrontend/";
    private final static String LINKEDIN_URL = "https://www.linkedin.com/company/herolo/";
    private final static String THANK_YOU_URL = "https://automation.herolo.co.il/thank-you/";
    private final static String HEROLO_AUTOMATION_URL = "https://automation.herolo.co.il";
    private final static String HEROLO_WEB_HEBREW_URL = "https://herolo.co.il/?lang=he";
    private final static String HEROLO_WEB_URL = "https://herolo.co.il/";
    private final static String CONTACT_EMAIL = "mailto:mati@herolo.co.il";
    private final static String CONTACT_PHONE = "tel:0544945333";
    private final static String FORM_NAME = "בדיקה";
    private final static String FORM_COMPANY = "בדיקה";
    private final static String FORM_EMAIL_VALID = "check@check.com";
    private final static String FORM_EMAIL_IN_VALID = "c@";
    private final static String FORM_PHONE_VALID = "0501231231";
    private final static String FORM_PHONE_IN_VALID = "p";
    private final static int SOCIAL_MEDIA_BUTTONS_FOOTER = 4;

    @Before
    public void init(){
        webDriver.get(HEROLO_AUTOMATION_URL);
        webDriver.manage().window().fullscreen();
        webDriver.manage().timeouts().implicitlyWait(10000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void resolutionTest(){
        String[] resolutions = {
                "1366x768",
                "1920x1080",
                "375x812",
                "768x1024"
        };

        for (String resolution : resolutions) {
            String[] parts = resolution.split("x");

            Dimension screenRes = new Dimension(Integer.parseInt(parts[0]),Integer.parseInt(parts[1]));
            webDriver.manage().window().setSize(screenRes);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }

            webDriver.navigate().refresh();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }

            takeScreenShot(resolution);
        }
    }

    @Test
    public void homePageTest()  {
        Assert.assertEquals(webDriver.getTitle(),HOME_PAGE_TITLE);
        List<WebElement> socialMediaLogos = webDriver.findElements(By.xpath("//*[contains(@class,'socialMediaBar__ImgSocial')]"));

        // Test footer social media buttons
        Assert.assertEquals("wrong number of socialMediaLogos", SOCIAL_MEDIA_BUTTONS_FOOTER,socialMediaLogos.size());
        List<String> socialMediaURL = new ArrayList<>();
        socialMediaURL.add(HEROLO_WEB_HEBREW_URL);
        socialMediaURL.add(FACEBOOK_URL);
        socialMediaURL.add(WHATSAPP_URL);
        socialMediaURL.add(LINKEDIN_URL);

        for (int index = 0; index < socialMediaLogos.size() ; index ++){
            socialMediaLogos.get(index).click();
        }
        List<String> browserTabs = new ArrayList<> (webDriver.getWindowHandles());
        for (int index = 0; index < socialMediaLogos.size() ; index ++){
            webDriver.switchTo().window(browserTabs.get(index+1));
            Assert.assertEquals(socialMediaURL.get(index),webDriver.getCurrentUrl());
            webDriver.close();
            webDriver.switchTo().window(browserTabs.get(0));
        }

        //Test Mati's email and phone number buttons
        WebElement email = webDriver.findElement(By.linkText("mati@herolo.co.il"));
        WebElement phone = webDriver.findElement(By.xpath("//*[contains(@class,'commun__Button-mgrfny-0 commun__ButtonContact-mgrfny-1 contactPerson__ButtonContact-sc-1boew59-6')]"));
        String emailLink = email.getAttribute("href");
        String phoneLink = phone.getAttribute("href");
        Assert.assertEquals(CONTACT_EMAIL,emailLink);
        Assert.assertEquals(CONTACT_PHONE,phoneLink);

        // test WhatsApp button on the left side of the screen
        WebElement whatsApp = webDriver.findElement(By.cssSelector(".cPQmgB"));
        Assert.assertEquals(WHATSAPP_URL,whatsApp.getAttribute("href"));

        // Test the text - string, font and colors match the request (example)
        List<WebElement> titleText = webDriver.findElements(By.xpath("//*[contains(@class,'logo__Text-sc-1gco7ve-3')]"));
        Assert.assertEquals(2,titleText.size());

        Map <Integer, Map<String,String>> textMap = new HashMap<>();
        Map<String,String> cssValuesFirst = new HashMap<>();
        cssValuesFirst.put("font-size","33.6px");
        cssValuesFirst.put("font-family","OpenSansBold, sans-serif");
        cssValuesFirst.put("color","rgba(175, 118, 222, 1)");
        textMap.put(0,cssValuesFirst);
        Map<String,String> cssValuesSecond = new HashMap<>();
        cssValuesSecond.put("font-size","33.6px");
        cssValuesSecond.put("font-family","OpenSansBold, sans-serif");
        cssValuesSecond.put("color","rgba(255, 255, 255, 1)");
        textMap.put(1,cssValuesSecond);

        for (int index = 0 ; index < titleText.size(); index ++){
            Assert.assertEquals(textMap.get(index).get("font-size"),titleText.get(index).getCssValue("font-size"));
            Assert.assertEquals(textMap.get(index).get("font-family"),titleText.get(index).getCssValue("font-family"));
            Assert.assertEquals(textMap.get(index).get("color"),titleText.get(index).getCssValue("color"));
        }
    }

    @Test
    public void testBackToTopButton(){

        int height = webDriver.manage().window().getSize().height;
        webDriver.manage().window().setPosition(new Point(webDriver.manage().window().getPosition().x,height));

        webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);

        WebElement backToTop= webDriver.findElement(By.xpath("//*[contains(@class,'backToTop__BtnGoUp')]"));
        backToTop.click();

        // Sleep is required to wait till it goes to top
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int currentScrollHeightAfterClickingOnBackToTop= webDriver.manage().window().getPosition().y;
        Assert.assertEquals(height,currentScrollHeightAfterClickingOnBackToTop);

    }


    @Test
    public void testFooter(){
        webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.HOME);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        WebElement footerVisible= webDriver.findElement(By.xpath("//*[contains(@class,'Footer__FooterWrapper')]"));
        Assert.assertEquals("visible", footerVisible.getCssValue("visibility"));

        webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
        // Sleep is required to wait till it goes down
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement footerHidden= webDriver.findElement(By.xpath("//*[contains(@class,'Footer__FooterWrapper')]"));
        Assert.assertEquals("hidden", footerHidden.getCssValue("visibility"));
    }

    public List<WebElement> getErrors(){
        List<WebElement> errors = webDriver.findElements(By.cssSelector(".yqHnF"));
        errors.stream().filter(e->e.isDisplayed()).collect(Collectors.toList());
        return errors;
    }

    public void checkErrorsStrings(List<String> errorMessageList, int errorsNumberExpected){
        List<WebElement> errors = getErrors();
        Assert.assertEquals(errorsNumberExpected,errors.size());
        for (int index=0; index < errors.size(); index ++){
            Assert.assertEquals(errorMessageList.get(index),errors.get(index).getText());
        }
    }

    @Test
    public void mandatoryFieldsTest(){
        WebElement buttonElement = webDriver.findElement(By.cssSelector(".gGWtQr"));
        Assert.assertFalse("no send button", buttonElement == null);
        buttonElement.click();
        List<String> errorMessageList = new ArrayList<>();
        errorMessageList.add("שדה שם הוא שדה חובה");
        errorMessageList.add("שדה חברה הוא שדה חובה");
        errorMessageList.add("שדה אימייל הוא שדה חובה");
        errorMessageList.add("שדה טלפון הוא שדה חובה");
        checkErrorsStrings(errorMessageList,4);
    }

    @Test
    public void submitFormTest() throws InterruptedException {
        List<WebElement> formFields = webDriver.findElements(By.cssSelector(".fzCqMp"));
        formFields.stream().filter(e->e.isDisplayed()).collect(Collectors.toList());
        Assert.assertEquals(4,formFields.size());
        List<String> inputListFailure = new ArrayList<>();
        inputListFailure.add(FORM_NAME);
        inputListFailure.add(FORM_COMPANY);
        inputListFailure.add(FORM_EMAIL_IN_VALID);
        inputListFailure.add(FORM_PHONE_IN_VALID);
        for (int index=0; index < formFields.size(); index ++){
            formFields.get(index).sendKeys(inputListFailure.get(index));
        }
        WebElement buttonElement = webDriver.findElement(By.cssSelector(".gGWtQr"));
        buttonElement.click();
        List<String> errorMessageList = new ArrayList<>();
        errorMessageList.add("כתובת אימייל לא חוקית");
        errorMessageList.add("מספר טלפון לא חוקי");
        checkErrorsStrings(errorMessageList,2);
        List<String> inputListSuccess = new ArrayList<>();
        inputListSuccess.add(FORM_NAME);
        inputListSuccess.add(FORM_COMPANY);
        inputListSuccess.add(FORM_EMAIL_VALID);
        inputListSuccess.add(FORM_PHONE_VALID);
        for (int index=0; index < formFields.size(); index ++){
            formFields.get(index).sendKeys(Keys.BACK_SPACE);
            formFields.get(index).sendKeys(inputListSuccess.get(index));
        }
        buttonElement.click();
        WebDriverWait wait=new WebDriverWait(webDriver, 120);
        wait.until(ExpectedConditions.visibilityOf(webDriver.findElement(By.cssSelector(".NQqYi"))));
        Assert.assertEquals(THANK_YOU_URL,webDriver.getCurrentUrl());
        //thank you page test
        List<String> socialMediaURL = new ArrayList<>();
        socialMediaURL.add(LINKEDIN_URL);
        socialMediaURL.add(INSTAGRAM_URL);
        socialMediaURL.add(FACEBOOK_URL);
        List<WebElement> socialMediaLogos = webDriver.findElements(By.cssSelector(".qodte"));
        for (int index = 0; index < socialMediaLogos.size() ; index ++){
            socialMediaLogos.get(index).click();
        }
        List<String> browserTabs = new ArrayList<> (webDriver.getWindowHandles());
        for (int index = 0; index < socialMediaLogos.size() ; index ++){
            webDriver.switchTo().window(browserTabs.get(index+1));
            Assert.assertEquals(socialMediaURL.get(index),webDriver.getCurrentUrl());
            webDriver.close();
            webDriver.switchTo().window(browserTabs.get(0));
        }
        WebElement backButton = webDriver.findElement(By.cssSelector(".idImZT"));
        backButton.click();
        Assert.assertEquals(HEROLO_AUTOMATION_URL, webDriver.getCurrentUrl());

        webDriver.navigate().back();

        WebElement websiteButton = webDriver.findElement(By.cssSelector(".NQqYi"));
        websiteButton.click();
        List<String> tabs = new ArrayList<> (webDriver.getWindowHandles());
        webDriver.switchTo().window(tabs.get(tabs.size()-1));
        Assert.assertEquals(HEROLO_WEB_URL.concat("/frontend-developers"), webDriver.getCurrentUrl());
        webDriver.close();
        webDriver.switchTo().window(tabs.get(0));
    }
}
