package com.enonic.autotests.pages.contentmanager.browsepanel;

import org.openqa.selenium.By;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.pages.Application;
import com.enonic.autotests.pages.contentmanager.wizardpanel.ContentWizardPanel;
import com.enonic.autotests.utils.NameHelper;
import com.enonic.autotests.utils.TestUtils;
import com.enonic.wem.api.schema.content.ContentTypeName;

/**
 * Content Manager application/add new content/select content type
 */
public class NewContentDialog
    extends Application
{
    private final static String DIALOG_TITLE_XPATH =
        "//div[contains(@class,'modal-dialog')]/div[contains(@class,'dialog-header') and contains(.,'What do you want to create?')]";

    public static String CONTENT_TYPE_NAME =
        "//div[contains(@id,'app.create.NewContentDialogList')]//li[@class='content-types-list-item' and descendant::p[text()='%s']]";

    /**
     * The constructor.
     *
     * @param session  {@link TestSession}   instance.
     */
    public NewContentDialog( TestSession session )
    {
        super( session );
    }

    /**
     * Checks that 'AddNewContentWizard' is opened.
     *
     * @return true if dialog opened, otherwise false.
     */
    public boolean isOpened()
    {
        return waitUntilVisibleNoException( By.xpath( DIALOG_TITLE_XPATH ), 1 );
    }

    /**
     * Select content type by name.
     *
     * @param contentTypeName the name of a content type.
     */
    public ContentWizardPanel selectContentType( String contentTypeName )
    {
        String ctypeXpath = String.format( CONTENT_TYPE_NAME, contentTypeName );
        boolean isContentNamePresent = waitElementExist( ctypeXpath, Application.EXPLICIT_4 );
        if ( !isContentNamePresent )
        {
            throw new TestFrameworkException( "content type with name " + contentTypeName + " was not found!" );
        }
        getDriver().findElement( By.xpath( ctypeXpath ) ).click();
        //TestUtils.clickByElement( By.xpath( ctypeXpath ), getDriver() );
        waitsForSpinnerNotVisible();
        ContentWizardPanel wizard = new ContentWizardPanel( getSession() );
        wizard.waitUntilWizardOpened();
        TestUtils.saveScreenshot( getSession(), NameHelper.uniqueName( "contentwizard" ) );
        return wizard;

    }

    public ContentWizardPanel selectContentType( ContentTypeName contentTypeName )
    {
        return selectContentType( contentTypeName.toString() );
    }
}
