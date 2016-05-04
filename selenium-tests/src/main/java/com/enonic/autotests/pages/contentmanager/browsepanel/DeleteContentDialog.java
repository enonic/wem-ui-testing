package com.enonic.autotests.pages.contentmanager.browsepanel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.pages.Application;
import com.enonic.autotests.utils.TestUtils;

import static com.enonic.autotests.utils.SleepHelper.sleep;

/**
 * This Dialog appears, when customer try to delete a content.
 */
public class DeleteContentDialog
    extends Application
{
    public static final String CHECKBOX_LABEL_TEXT = "Instantly delete published items";

    private final String CONTAINER_DIV = "//div[contains(@id,'ContentDeleteDialog')]";

    private String CONTENT_STATUS = CONTAINER_DIV +
        "//div[contains(@id,'ContentDeleteSelectionItem') and descendant::h6[@class='main-name' and contains(.,'%s')]]//div[contains(@class,'status')][2]";

    private final String CHECKBOX_DELETE_PUBLISHED_ITEMS =
        CONTAINER_DIV + "//div[contains(@id,'Checkbox') and contains(@class,'instant-delete-check')]";

    private final String CHECKBOX_LABEL = CHECKBOX_DELETE_PUBLISHED_ITEMS + "//label";

    protected Logger logger = Logger.getLogger( this.getClass() );

    private final String ITEMS_TO_DELETE_BY_DISPLAY_NAME =
        CONTAINER_DIV + "//div[contains(@id,'ContentDeleteSelectionItem')]" + H6_DISPLAY_NAME; //"//div[@class='item-list']//h6";

    private final String TITLE_HEADER_XPATH =
        CONTAINER_DIV + "//div[contains(@id,'ModalDialogHeader') and child::h2[text()='Delete item']]";

    private final String TITLE_TEXT = CONTAINER_DIV + "//div[contains(@id,'ModalDialogHeader')]//h2[@class='title']";

    protected final String DELETE_BUTTON_XPATH = CONTAINER_DIV + "//button/span[contains(.,'Delete')]";

    protected final String CANCEL_BUTTON = CONTAINER_DIV + "//button/span[text()='Cancel']";

    private final String CANCEL_BUTTON_TOP = CONTAINER_DIV + "//div[@class='cancel-button-top']";

    @FindBy(xpath = DELETE_BUTTON_XPATH)
    private WebElement deleteButton;

    @FindBy(xpath = CANCEL_BUTTON)
    private WebElement cancelButton;

    @FindBy(xpath = CHECKBOX_DELETE_PUBLISHED_ITEMS)
    private WebElement checkBox;

    @FindBy(xpath = CANCEL_BUTTON_TOP)
    private WebElement cancelButtonTop;

    /**
     * The constructor.
     *
     * @param session
     */
    public DeleteContentDialog( TestSession session )
    {
        super( session );
    }

    public boolean isDeleteButtonPresent()
    {
        return deleteButton.isDisplayed();
    }

    public boolean isCancelButtonPresent()
    {
        return cancelButton.isDisplayed();
    }

    public boolean isCheckboxForDeletePublishedItemsDisplayed()
    {
        return checkBox.isDisplayed();
    }

    public String getCheckboxLabelText()
    {
        return getDisplayedString( CHECKBOX_LABEL );
    }

    public void doDelete()
    {
        deleteButton.click();
        boolean isClosed = waitForClosed();
        if ( !isClosed )
        {
            throw new TestFrameworkException( "Confirm 'delete content' dialog was not closed!" );
        }
        sleep( 1000 );
    }

    public DeleteContentDialog clickOnInstantlyCheckbox()
    {
        checkBox.click();
        return this;
    }

    public void clickOnCancelButton()
    {
        cancelButton.click();
        waitForClosed();
    }

    public void clickOnCancelTop()
    {
        cancelButtonTop.click();
        waitForClosed();
    }

    public boolean waitForClosed()
    {
        return waitElementNotVisible( By.xpath( TITLE_HEADER_XPATH ), Application.EXPLICIT_NORMAL );
    }

    public boolean waitForOpened()
    {
        return waitUntilVisibleNoException( By.xpath( TITLE_HEADER_XPATH ), Application.EXPLICIT_NORMAL );
    }

    public boolean isOpened()
    {
        return isElementDisplayed( TITLE_HEADER_XPATH );
    }

    public List<String> getDisplayNamesToDelete()
    {
        List<String> names = new ArrayList<>();
        List<WebElement> itemsToDelete = getDriver().findElements( By.xpath( ITEMS_TO_DELETE_BY_DISPLAY_NAME ) );

        for ( WebElement el : itemsToDelete )
        {
            names.add( el.getText() );
            logger.info( "this item present in the confirm-delete dialog and will be deleted:" + el.getText() );
        }
        return names;
    }

    public String getContentStatus( String displayName )
    {
        String status = String.format( CONTENT_STATUS, displayName );
        if ( !isElementDisplayed( status ) )
        {
            TestUtils.saveScreenshot( getSession(), "err_status_" + displayName );
            throw new TestFrameworkException( "status was not found! " + displayName );
        }
        return getDisplayedString( status );
    }

    public String getTitle()
    {
        return getDisplayedString( TITLE_TEXT );
    }
}
