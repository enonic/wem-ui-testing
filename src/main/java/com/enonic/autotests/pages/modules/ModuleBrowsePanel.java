package com.enonic.autotests.pages.modules;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.pages.BrowsePanel;

import static com.enonic.autotests.utils.SleepHelper.sleep;

public class ModuleBrowsePanel
    extends BrowsePanel
{

    public static final String TOOLBAR = "//div[contains(@id,'app.browse.ModuleBrowseToolbar')]";

    public static final String INSTALL_BUTTON =
        TOOLBAR + "/*[contains(@id, 'api.ui.button.ActionButton') and child::span[text()='Install']]";

    public static final String START_BUTTON = TOOLBAR + "/*[contains(@id, 'api.ui.button.ActionButton') and child::span[text()='Start']]";

    public static final String STOP_BUTTON = TOOLBAR + "/*[contains(@id, 'api.ui.button.ActionButton') and child::span[text()='Start']]";

    public static final String UNINSTALL_BUTTON =
        TOOLBAR + "/*[contains(@id, 'api.ui.button.ActionButton') and child::span[text()='Uninstall']]";


    @FindBy(xpath = INSTALL_BUTTON)
    private WebElement installButton;

    @FindBy(xpath = START_BUTTON)
    private WebElement startButton;

    @FindBy(xpath = STOP_BUTTON)
    private WebElement stopButton;

    @FindBy(xpath = UNINSTALL_BUTTON)
    private WebElement uninstallButton;


    /**
     * The Constructor
     *
     * @param session
     */
    public ModuleBrowsePanel( final TestSession session )
    {
        super( session );
    }

    @Override
    public BrowsePanel goToAppHome()
    {
        return null;
    }

    public InstallModuleDialog clickToolbarInstall()
    {
        installButton.click();
        InstallModuleDialog dialog = new InstallModuleDialog( getSession() );
        return dialog;
    }

    public ModuleBrowsePanel clickToolbarStop()
    {
        stopButton.click();

        return this;
    }

    public ModuleBrowsePanel clickToolbarStart()
    {
        startButton.click();
        sleep( 300 );
        return this;
    }

    public boolean waitAndCheckIsButtonEnabled( String buttonXpath )
    {
        return waitUntilElementEnabledNoException( By.xpath( buttonXpath ), 2 );
    }

    public String getModuleStatus( String moduleName )
    {
        String stateCell = String.format( ROW, moduleName ) + "//div[contains(@class,'state')]";
        if ( findElements( By.xpath( stateCell ) ).size() == 0 )
        {
            throw new TestFrameworkException( "state was not found in the table ! module name is " + moduleName );
        }
        return findElements( By.xpath( stateCell ) ).get( 0 ).getText();
    }
}
