package com.enonic.autotests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.SaveOrUpdateException;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.utils.TestUtils;

/**
 * Base class for wizards.
 */
public abstract class WizardPanel
    extends Application
{

    public static String RED_CIRCLE_XPATH = "//span[@class='tabcount' and contains(.,'%s')]";

    public static String APP_BAR_TABMENU_TITLE_XPATH = "//div[@class='tab-menu appbar-tabmenu']//span[@class='label']";

    public static final String TOOLBAR_SAVE_BUTTON_XPATH =
        "//div[@class='panel wizard-panel']/div[@class='toolbar']//button[text()='Save']";

    public static final String TOOLBAR_CLOSEWIZARD_BUTTON_XPATH =
        "//div[@class='panel wizard-panel']/div[@class='toolbar']//button[text()='Close']";


    @FindBy(xpath = TOOLBAR_CLOSEWIZARD_BUTTON_XPATH)
    protected WebElement closeButton;

    @FindBy(xpath = TOOLBAR_SAVE_BUTTON_XPATH)
    protected WebElement toolbarSaveButton;

    @FindBy(name = "displayName")
    protected WebElement displayNameInput;

    @FindBy(name = "name")
    protected WebElement nameInput;


    /**
     * The constructor
     *
     * @param session
     */
    public WizardPanel( TestSession session )
    {
        super( session );
    }

    public String getAppBarTabMenuTitle()
    {
        boolean result = waitAndFind( By.xpath( APP_BAR_TABMENU_TITLE_XPATH ), 1 );
        if ( result )
        {
            return getDriver().findElement( By.xpath( APP_BAR_TABMENU_TITLE_XPATH ) ).getText();
        }
        else
        {
            throw new TestFrameworkException( "title was not found in AppBarTabMenu!" );
        }
    }

    public String getNameInputValue()
    {
        return nameInput.getAttribute( "value" );
    }

    /**
     * Press the button 'Save', which located in the wizard's toolbar.
     */
    protected void doSaveFromToolbar()
    {
        boolean isSaveButtonEnabled = waitUntilElementEnabledNoException( By.xpath( TOOLBAR_SAVE_BUTTON_XPATH ), 2l );
        if ( !isSaveButtonEnabled )
        {
            throw new SaveOrUpdateException( "Impossible to save, button 'Save' is disabled!" );
        }
        toolbarSaveButton.click();
    }

    public boolean isEnabledSaveButton()
    {
        return waitUntilElementEnabledNoException( By.xpath( TOOLBAR_SAVE_BUTTON_XPATH ), Application.IMPLICITLY_WAIT );
    }

    /**
     * Checks tab-count on the Home page.(checks that one wizard was opened)
     *
     * @return
     */
    public HomePage showHomePageAndVerifyCircle()
    {
        gotoHomeButton.click();
        HomePage page = new HomePage( getSession() );

        getDriver().switchTo().window( getSession().getWindowHandle() );
        waitUntilVisible( By.xpath( "//div[@class='tab-count-container' and contains(@title,'1 tab(s) open')]" ) );
        return page;
    }

    /**
     * Gets notification message(Space 'namesapce' was saved), that appears at
     * the bottom of the WizardPage. <br>
     *
     * @return notification message or null.
     */
    public String waitNotificationMessage()
    {
        String message =
            TestUtils.waitNotificationMessage( By.xpath( "//div[@class='admin-notification-content']/span" ), getDriver(), 2l );
        return message;
    }

    /**
     * Verify that red circle and "New Space" message presented on the top of
     * Page.
     */
    public void waitUntilWizardOpened( Integer numberPage )
    {
        String circleXpath = String.format( RED_CIRCLE_XPATH, numberPage.toString() );
        //String titleXpath = String.format(APP_BAR_TABMENU_TITLE, displayName);
        waitUntilVisible( By.xpath( circleXpath ) );
        //TestUtils.getInstance().waitUntilVisible(getSession(), By.xpath(titleXpath));
    }

    public void waitElementClickable( By by, long timeout )
    {
        new WebDriverWait( getDriver(), timeout ).until( ExpectedConditions.elementToBeClickable( by ) );
    }

    public WebElement getNameInput()
    {
        return nameInput;
    }
}
