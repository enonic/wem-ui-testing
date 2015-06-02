package com.enonic.autotests.pages.contentmanager.browsepanel;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.SaveOrUpdateException;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.pages.Application;
import com.enonic.autotests.pages.BaseBrowseFilterPanel;
import com.enonic.autotests.pages.BrowsePanel;
import com.enonic.autotests.pages.contentmanager.wizardpanel.ContentWizardPanel;
import com.enonic.autotests.pages.contentmanager.wizardpanel.ItemViewPanelPage;
import com.enonic.autotests.services.NavigatorHelper;
import com.enonic.autotests.utils.NameHelper;
import com.enonic.autotests.utils.TestUtils;
import com.enonic.autotests.vo.contentmanager.Content;
import com.enonic.xp.content.ContentPath;

import static com.enonic.autotests.utils.SleepHelper.sleep;

/**
 * 'Content Manager' application, the Browse Panel page.
 */
public class ContentBrowsePanel
    extends BrowsePanel
{
    public final String NOTIFICATION_MESSAGE = "//div[contains(@id,'NotificationContainer')]//div[@class='notification-content']//span";

    public static final String CONTENT_MANAGER_BUTTON = "//button[@id='api.app.bar.HomeButton' ]//span[text()='Content Manager']";

    private final String BASE_TOOLBAR_XPATH = "//div[contains(@id,'app.browse.ContentBrowseToolbar')]";

    private final String BASE_PANEL_XPATH = "//div[contains(@id,'app.browse.ContentBrowsePanel')]";

    protected final String ALL_CONTENT_NAMES_FROM_BROWSE_PANEL_XPATH = BASE_PANEL_XPATH + ALL_NAMES_FROM_BROWSE_PANEL_XPATH;


    private final String NEW_BUTTON_XPATH = BASE_TOOLBAR_XPATH + "/*[contains(@id, 'ActionButton') and child::span[text()='New']]";

    private final String DUPLICATE_BUTTON_XPATH =
        BASE_TOOLBAR_XPATH + "/*[contains(@id, 'ActionButton') and child::span[text()='Duplicate']]";

    private final String PREVIEW_BUTTON_XPATH = BASE_TOOLBAR_XPATH + "/*[contains(@id, 'ActionButton') and child::span[text()='Preview']]";

    private final String MOVE_BUTTON_XPATH = BASE_TOOLBAR_XPATH + "/*[contains(@id, 'ActionButton') and child::span[text()='Move']]";

    private final String EDIT_BUTTON_XPATH = BASE_TOOLBAR_XPATH + "/*[contains(@id, 'ActionButton') and child::span[text()='Edit']]";

    private final String DELETE_BUTTON_XPATH = BASE_TOOLBAR_XPATH + "/*[contains(@id, 'ActionButton') and child::span[text()='Delete']]";

    private final String SORT_BUTTON_XPATH = BASE_TOOLBAR_XPATH + "/*[contains(@id, 'ActionButton') and child::span[text()='Sort']]";

    private final String PUBLISH_BUTTON_XPATH = BASE_TOOLBAR_XPATH + "/*[contains(@id, 'ActionButton') and child::span[text()='Publish']]";


    private String CONTEXT_MENU_ITEM = "//li[contains(@id,'api.ui.menu.MenuItem') and text()='%s']";

    @FindBy(xpath = DELETE_BUTTON_XPATH)
    protected WebElement deleteButton;

    @FindBy(xpath = NEW_BUTTON_XPATH)
    private WebElement newButton;

    @FindBy(xpath = EDIT_BUTTON_XPATH)
    private WebElement editButton;

    @FindBy(xpath = DUPLICATE_BUTTON_XPATH)
    private WebElement duplicateButton;

    @FindBy(xpath = CONTENT_MANAGER_BUTTON)
    private WebElement contentManagerButton;

    @FindBy(xpath = PREVIEW_BUTTON_XPATH)
    private WebElement previewButton;

    @FindBy(xpath = SORT_BUTTON_XPATH)
    private WebElement sortButton;

    @FindBy(xpath = MOVE_BUTTON_XPATH)
    private WebElement moveButton;

    @FindBy(xpath = PUBLISH_BUTTON_XPATH)
    private WebElement publishButton;

    private ContentBrowseFilterPanel filterPanel;

    private ContentBrowseItemsSelectionPanel itemsSelectionPanel;

    /**
     * The constructor.
     *
     * @param session {@link TestSession} instance
     */
    public ContentBrowsePanel( TestSession session )
    {
        super( session );
    }

    @Override
    public void waitsForSpinnerNotVisible()
    {
        boolean result = waitsElementNotVisible( By.xpath( BASE_PANEL_XPATH + SPINNER_XPATH ), Application.EXPLICIT_NORMAL );
        if ( !result )
        {
            throw new TestFrameworkException( "after " + EXPLICIT_NORMAL + " second, spinner still present" );
        }
    }

    public String waitNotificationMessage()
    {
        if ( !waitUntilVisibleNoException( By.xpath( NOTIFICATION_MESSAGE ), Application.EXPLICIT_NORMAL ) )
        {
            return null;
        }
        return findElements( By.xpath( NOTIFICATION_MESSAGE ) ).get( 0 ).getText();
    }

    /**
     * @param session {@link TestSession} instance
     * @return true if 'Content Manager' opened and CMSpacesPage showed, otherwise false.
     */
    public static boolean isOpened( TestSession session )
    {
        List<WebElement> searchInput = session.getDriver().findElements( By.xpath( ContentBrowseFilterPanel.SEARCH_INPUT_XPATH ) );
        if ( searchInput.size() > 0 && searchInput.get( 0 ).isDisplayed() )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isInvalidContent( String contentPath )
    {
        List<WebElement> elements = findElements( By.xpath( String.format( CONTENT_SUMMARY_VIEWER, contentPath ) ) );
        if ( elements.size() == 0 )
        {
            throw new TestFrameworkException( "content with path was not found!" + contentPath );
        }
        return waitAndCheckAttrValue( elements.get( 0 ), "class", "invalid", Application.EXPLICIT_NORMAL );

    }

    public ContentWizardPanel selectAndOpenContentFromToolbarMenu( Content content )
    {
        clickOnClearSelection();

        if ( findElements( By.linkText( BaseBrowseFilterPanel.CLEAR_FILTER_LINK ) ).size() > 0 )
        {
            findElements( By.linkText( BaseBrowseFilterPanel.CLEAR_FILTER_LINK ) ).get( 0 ).click();
        }

        filterPanel.typeSearchText( content.getName() );
        clickCheckboxAndSelectRow( content.getPath() ).clickToolbarEdit();
        ContentWizardPanel wizard = new ContentWizardPanel( getSession() );
        wizard.waitUntilWizardOpened();
        return wizard;
    }

    public String getContentStatus( ContentPath path )
    {
        String statusXpath = String.format(
            "//div[contains(@class,'slick-row') and descendant::p[contains(@title,'%s')]]//div[contains(@class,'slick-cell')][4]/span",
            path.toString() );
        if ( findElements( By.xpath( statusXpath ) ).size() == 0 )
        {
            throw new TestFrameworkException( "content was not found " + path.getName() );

        }
        return findElements( By.xpath( statusXpath ) ).get( 0 ).getText();

    }

    public ContentBrowsePanel refreshPanelInBrowser()
    {
        getDriver().navigate().refresh();
        NavigatorHelper.switchToIframe( getSession(), Application.CONTENT_MANAGER_FRAME_XPATH );
        return this;
    }


    public ContentBrowseFilterPanel getFilterPanel()
    {
        if ( filterPanel == null )
        {
            filterPanel = new ContentBrowseFilterPanel( getSession() );
        }
        return filterPanel;
    }

    public ContentBrowseItemsSelectionPanel getItemSelectionPanel()
    {
        if ( itemsSelectionPanel == null )
        {
            itemsSelectionPanel = new ContentBrowseItemsSelectionPanel( getSession() );
        }
        return itemsSelectionPanel;
    }

    public ContentBrowsePanel goToAppHome()
    {
        contentManagerButton.click();
        sleep( 1000 );
        waitUntilPageLoaded( Application.EXPLICIT_NORMAL );
        TestUtils.saveScreenshot( getSession(), NameHelper.uniqueName( "gotoapphome" ) );
        return this;
    }

    /**
     * Gets all content names, showed in the browse panel.
     *
     * @return list of names.
     */
    public List<String> getContentNamesFromBrowsePanel()
    {
        List<String> allNames = new ArrayList<>();
        List<WebElement> rows = getDriver().findElements( By.xpath( ALL_CONTENT_NAMES_FROM_BROWSE_PANEL_XPATH ) );
        for ( WebElement row : rows )
        {
            allNames.add( row.getAttribute( "title" ) );
        }
        return allNames;
    }

    /**
     * @param contentPath
     * @return
     */
    public boolean exists( ContentPath contentPath )
    {
        return exists( contentPath.getName(), false );
    }

    public boolean exists( ContentPath contentPath, boolean saveScreenshot )
    {
        return exists( contentPath.toString(), saveScreenshot );
    }

    /**
     * @param contentPath
     * @return {@link ContentBrowsePanel} instance
     */
    public ContentBrowsePanel unExpandContent( ContentPath contentPath )
    {

        if ( !doScrollAndFindGridItem( contentPath.toString() ) )
        {
            throw new TestFrameworkException( "unExpandContent: content was not found! " + contentPath );
        }

        if ( isRowExpanded( contentPath.toString() ) )
        {
            this.clickOnExpander( contentPath.toString() );
            getLogger().info( "content have been unexpanded: " + contentPath.toString() );
        }
        else
        {
            getLogger().info( "content was not expanded: " + contentPath.toString() );
        }
        sleep( 500 );
        return this;
    }

    /**
     * Clicks on expander icon near the content.
     *
     * @param contentPath
     */
    public ContentBrowsePanel expandContent( ContentPath contentPath )
    {
        if ( !doScrollAndFindGridItem( contentPath.toString() ) )
        {
            throw new TestFrameworkException( "expandContent: content was not found! " + contentPath );
        }

        ContentPath path = null;
        if ( contentPath != null )
        {
            for ( int i = 0; i < contentPath.elementCount(); i++ )
            {
                String parentContent = contentPath.getElement( i );
                if ( path == null )
                {
                    path = ContentPath.from( parentContent );
                }
                else
                {
                    path = ContentPath.from( path, parentContent );
                }
                if ( !isRowExpanded( path.getName() ) )
                {
                    if ( !this.clickOnExpander( path.toString() ) )
                    {
                        getLogger().info( "content with name " + parentContent + "has no children! " );
                    }
                }
            }
        }
        waitsForSpinnerNotVisible();
        sleep( 700 );
        return this;
    }

    public ContentWizardPanel doubleClickOnContent( ContentPath contentPath )
    {
        sleep( 500 );
        String rowXpath = String.format( DIV_NAMES_VIEW, contentPath );
        boolean result = waitAndFind( By.xpath( rowXpath ) );
        Actions builder = new Actions( getDriver() );
        builder.doubleClick( findElement( By.xpath( rowXpath ) ) ).build().perform();
        sleep( 500 );
        return new ContentWizardPanel( getSession() );
    }

    public List<String> getChildNames( ContentPath contentPath )
    {
        return getChildNames( contentPath.toString() );
    }

    /**
     * Clicks on 'Delete' button on toolbar.
     *
     * @return {@link DeleteContentDialog} instance.
     */
    public DeleteContentDialog clickToolbarDelete()
    {
        boolean isEnabledDeleteButton = waitUntilElementEnabledNoException( By.xpath( DELETE_BUTTON_XPATH ), 2l );
        if ( !isEnabledDeleteButton )
        {
            throw new SaveOrUpdateException( "Impossible to delete content, because the 'Delete' button is disabled!" );
        }
        deleteButton.click();
        DeleteContentDialog dialog = new DeleteContentDialog( getSession() );
        dialog.waitForOpened();
        return dialog;
    }

    public ContentBrowsePanel clickToolbarPublish()
    {
        boolean isEnabled = waitUntilElementEnabledNoException( By.xpath( PUBLISH_BUTTON_XPATH ), Application.EXPLICIT_NORMAL );
        if ( !isEnabled )
        {
            throw new SaveOrUpdateException( "Impossible to publish content, because the 'publish' button is disabled!" );
        }
        publishButton.click();

        return this;
    }

    public ContentBrowsePanel selectContentInTable( List<Content> contents )
    {
        for ( Content content : contents )
        {
            selectContentInTable( content.getPath() );
        }
        return this;
    }

    public ContentBrowsePanel selectContentInTable( ContentPath contentPath )
    {
        boolean exist = doScrollAndFindGridItem( contentPath.getName() );
        if ( !isRowSelected( contentPath.getName() ) )
        {
            clickCheckboxAndSelectRow( contentPath );
        }
        return this;
    }

    public ContentBrowsePanel deSelectContentInTable( ContentPath contentPath )
    {
        boolean exist = doScrollAndFindGridItem( contentPath.getName() );
        if ( isRowSelected( contentPath.toString() ) )
        {
            clickCheckboxAndSelectRow( contentPath );
        }
        return this;
    }

    /**
     * Clicks on a checkbox, linked with content and selects a row in the table.
     *
     * @param path a content path.
     */
    public ContentBrowsePanel clickCheckboxAndSelectRow( ContentPath path )
    {
        return clickCheckboxAndSelectRow( path.getName() );
    }

    public BrowsePanel pressKeyOnRow( ContentPath path, Keys key )
    {
        boolean result = doScrollAndFindGridItem( path.toString() );
        if ( !result )
        {
            throw new TestFrameworkException( "grid item was not found! " + path.toString() );
        }
        return pressKeyOnRow( path.toString(), key );
    }

    public boolean isExpanderPresent( ContentPath contentPath )
    {
        return isExpanderPresent( contentPath.toString() );
    }

    /**
     * Clicks on 'New' button and opens NewContentDialog
     *
     * @return {@link NewContentDialog} instance.
     */
    public NewContentDialog clickToolbarNew()
    {
        newButton.click();
        sleep( 500 );
        NewContentDialog newContentDialog = new NewContentDialog( getSession() );
        boolean isLoaded = newContentDialog.waitUntilDialogShowed( Application.EXPLICIT_NORMAL );
        if ( !isLoaded )
        {
            throw new TestFrameworkException( "Error during add content, NewContentDialog dialog was not showed!" );
        }
        return newContentDialog;
    }

    public SortContentDialog clickToolbarSort()
    {
        sortButton.click();
        sleep( 500 );
        SortContentDialog sortContentDialog = new SortContentDialog( getSession() );
        sortContentDialog.waitForLoaded( Application.EXPLICIT_NORMAL );

        return sortContentDialog;
    }

    public MoveContentDialog clickToolbarMove()
    {
        moveButton.click();
        sleep( 500 );
        MoveContentDialog moveContentDialog = new MoveContentDialog( getSession() );
        moveContentDialog.waitUntilDialogShowed( Application.EXPLICIT_NORMAL );

        return moveContentDialog;
    }

    /**
     * Expands all contents from 'content path' and clicks on a checkbox near the content, that will be parent for new content.
     *
     * @param contentPath {@link ContentPath} instance.
     */
    public ContentBrowsePanel clickOnParentCheckbox( ContentPath contentPath )
    {
        if ( contentPath.elementCount() == 0 )
        {
            return this;
        }

        if ( !doScrollAndFindGridItem( contentPath.toString() ) )
        {
            throw new TestFrameworkException( "content was not found: " + contentPath.toString() );
        }

        String checkBoxXpath = String.format( CHECKBOX_ROW_CHECKER, contentPath.toString() );
        boolean isPresentCheckbox = isDynamicElementPresent( By.xpath( checkBoxXpath ), 3 );
        if ( !isPresentCheckbox )
        {
            TestUtils.saveScreenshot( getSession(), "checkbox" + contentPath.getName() );
            throw new TestFrameworkException(
                "Time: " + TestUtils.timeNow() + "  wrong xpath:" + checkBoxXpath + " or item with name " + contentPath.toString() +
                    " was not found!" );
        }
        getDriver().findElement( By.xpath( checkBoxXpath ) ).click();
        sleep( 200 );
        boolean isNewEnabled = waitUntilElementEnabledNoException( By.xpath( NEW_BUTTON_XPATH ), 2l );
        if ( !isNewEnabled )
        {
            throw new SaveOrUpdateException( "Impossible to open 'ContentWizardPanel', because the 'New' button is disabled!" );
        }
        return this;
    }

    /**
     * Clicks on row with content(not clicks by a checkbox)
     *
     * @param contentPath {@link ContentPath} instance.
     */
    public ContentBrowsePanel selectRowByContentPath( String contentPath )
    {
        clickAndSelectRow( contentPath );
        return this;
    }

    /**
     * Clicks on 'Open' button in toolbar.
     *
     * @return {@link ItemViewPanelPage} instance.
     */
    public ItemViewPanelPage clickToolbarPreview()
    {
        previewButton.click();
        return new ItemViewPanelPage( getSession() );
    }

    /**
     * Clicks on 'Edit' button in toolbar.
     *
     * @return {@link ContentWizardPanel} instance.
     */
    public ContentWizardPanel clickToolbarEdit()
    {
        editButton.click();
        ContentWizardPanel wizard = new ContentWizardPanel( getSession() );
        wizard.waitUntilWizardOpened();
        return wizard;
    }

    /**
     * Start to delete a content from menu in context menu.
     *
     * @param path
     * @return {@link DeleteContentDialog} instance.
     */
    public DeleteContentDialog selectDeleteFromContextMenu( ContentPath path )
    {
        if ( !doScrollAndFindGridItem( path.toString() ) )
        {
            throw new TestFrameworkException( "content was not found: " + path.toString() );
        }
        openContextMenu( path.getName() );
        findElements( By.xpath( String.format( CONTEXT_MENU_ITEM, "Delete" ) ) ).get( 0 ).click();
        DeleteContentDialog dialog = new DeleteContentDialog( getSession() );
        dialog.waitForOpened();
        return dialog;
    }

    public ContentBrowsePanel selectPublishFromContextMenu( ContentPath path )
    {
        getFilterPanel().clickOnCleanFilter().typeSearchText( path.getName() );
        openContextMenu( path.getName() );
        findElements( By.xpath( String.format( CONTEXT_MENU_ITEM, "Publish" ) ) ).get( 0 ).click();
        sleep( 2000 );
        return this;

    }

    public ContentBrowsePanel selectDuplicateFromContextMenu( ContentPath path )
    {
        getFilterPanel().clickOnCleanFilter().typeSearchText( path.getName() );
        openContextMenu( path.getName() );
        findElements( By.xpath( String.format( CONTEXT_MENU_ITEM, "Duplicate" ) ) ).get( 0 ).click();
        sleep( 1000 );
        return this;

    }

    public boolean isEnableContextMenuItem( String contentName, String action )
    {
        getFilterPanel().clickOnCleanFilter().typeSearchText( contentName );
        openContextMenu( contentName );
        if ( findElements( By.xpath( String.format( CONTEXT_MENU_ITEM, action ) ) ).size() == 0 )
        {
            throw new TestFrameworkException( "menu item was not found!  " + action );
        }
        return findElements( By.xpath( String.format( CONTEXT_MENU_ITEM, action ) ) ).get( 0 ).isEnabled();
    }

    /**
     * Start to delete a content from menu in context menu.
     *
     * @param path
     * @return {@link DeleteContentDialog} instance.
     */
    public ContentWizardPanel selectEditFromContextMenu( ContentPath path )
    {
        if ( !doScrollAndFindGridItem( path.toString() ) )
        {
            throw new TestFrameworkException( "content was not found: " + path.toString() );
        }
        openContextMenu( path.getName() );
        findElements( By.xpath( String.format( CONTEXT_MENU_ITEM, "Edit" ) ) ).get( 0 ).click();
        ContentWizardPanel wizard = new ContentWizardPanel( getSession() );
        wizard.waitUntilWizardOpened();
        return wizard;
    }

    /**
     * Opens context menu and select 'New' item
     *
     * @param path
     * @return
     */
    public NewContentDialog selectNewFromContextMenu( ContentPath path )
    {
        if ( !doScrollAndFindGridItem( path.toString() ) )
        {
            throw new TestFrameworkException( "content was not found: " + path.toString() );
        }
        openContextMenu( path.getName() );
        findElements( By.xpath( String.format( CONTEXT_MENU_ITEM, "New" ) ) ).get( 0 ).click();
        NewContentDialog newContentDialog = new NewContentDialog( getSession() );
        newContentDialog.waitUntilDialogShowed( Application.EXPLICIT_NORMAL );
        return newContentDialog;
    }

    private void openContextMenu( String contentName )
    {

        getLogger().info( "opening a context menu, content path of content: " + contentName );
        TestUtils.saveScreenshot( getSession(), "menu_" + contentName );
        String contentDescriptionXpath = String.format( DIV_NAMES_VIEW, contentName );
        WebElement element = findElement( By.xpath( contentDescriptionXpath ) );
        Actions action = new Actions( getDriver() );

        action.contextClick( element ).build().perform();
        sleep( 100 );
    }

    /**
     * @return true if 'Delete' button enabled, otherwise false.
     */
    public boolean isDeleteButtonEnabled()
    {
        return deleteButton.isEnabled();
    }

    /**
     * @return true if 'New' button enabled, otherwise false.
     */
    public boolean isNewButtonEnabled()
    {
        return newButton.isEnabled();
    }

    public boolean isSortButtonEnabled()
    {
        return sortButton.isEnabled();
    }

    public boolean isDuplicateButtonEnabled()
    {
        return duplicateButton.isEnabled();
    }

    public boolean isMoveButtonEnabled()
    {
        return moveButton.isEnabled();
    }

    public boolean isEditButtonEnabled()
    {
        return editButton.isEnabled();
    }

    public boolean isPublishButtonEnabled()
    {
        return publishButton.isEnabled();
    }

    public boolean isPreviewButtonEnabled()
    {
        return previewButton.isEnabled();
    }
}
