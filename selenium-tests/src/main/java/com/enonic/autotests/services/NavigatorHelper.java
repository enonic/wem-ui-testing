package com.enonic.autotests.services;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.pages.HomePage;
import com.enonic.autotests.pages.LoginPage;
import com.enonic.autotests.pages.XpTourDialog;
import com.enonic.autotests.pages.contentmanager.browsepanel.ContentBrowsePanel;
import com.enonic.autotests.pages.modules.ApplicationBrowsePanel;
import com.enonic.autotests.pages.usermanager.browsepanel.UserBrowsePanel;
import com.enonic.autotests.vo.usermanager.User;

public class NavigatorHelper
{

    public static void closeXpTourDialogIfPresent( TestSession testSession )
    {
        XpTourDialog xpTourDialog = new XpTourDialog( testSession );
        if ( xpTourDialog.isOpened() )
        {
            xpTourDialog.clickOnCancelButton();
        }
    }

    /**
     * Opens 'Content Studio' application.
     *
     * @param testSession {@link TestSession} instance.
     * @return {@link ContentBrowsePanel} instance.
     */
    public static ContentBrowsePanel openContentStudioApp( TestSession testSession )
    {
        HomePage home = loginAndOpenHomePage( testSession );
        closeXpTourDialogIfPresent( testSession );
        ContentBrowsePanel cmPage = home.openContentStudioApplication();
        return cmPage;
    }

    public static UserBrowsePanel openUsersApp( TestSession testSession )
    {
        HomePage home = loginAndOpenHomePage( testSession );
        closeXpTourDialogIfPresent( testSession );
        UserBrowsePanel userBrowsePanel = home.openUsersApplication();
        return userBrowsePanel;
    }

    public static ApplicationBrowsePanel openApplications( TestSession testSession )
    {
        HomePage home = loginAndOpenHomePage( testSession );
        closeXpTourDialogIfPresent( testSession );
        ApplicationBrowsePanel userBrowsePanel = home.openApplications();
        return userBrowsePanel;
    }

    /**
     * @param testSession {@link TestSession} instance.
     * @param iframeXpath frame's xpath.
     */
    public static void switchToIframe( TestSession testSession, String iframeXpath )
    {
        String whandle = testSession.getDriver().getWindowHandle();
        testSession.getDriver().switchTo().window( whandle );
        List<WebElement> frames = testSession.getDriver().findElements( By.xpath( iframeXpath ) );
        if ( frames.size() == 0 )
        {
            throw new TestFrameworkException( "Unable to switch to the iframe " + iframeXpath );
        }
        testSession.getDriver().switchTo().frame( frames.get( 0 ) );
    }


    public static String switchToBrowserTab( TestSession session, String urlPart )
    {
        WebDriver driver = session.getDriver();
        Set<String> windowHandles = driver.getWindowHandles();
        if ( !windowHandles.isEmpty() )
        {
            for ( String windowHandle : windowHandles )
            {
                try
                {
                    if ( driver.switchTo().window( windowHandle ).getCurrentUrl().contains( urlPart ) )
                    {
                        //session.put( HomePage.APP_TAB_HANDLE, windowHandle );
                        return windowHandle;
                    }
                }
                catch ( NoSuchWindowException e )
                {
                    throw new TestFrameworkException( "NoSuchWindowException- wrong handle" );
                }
            }
        }
        throw new TestFrameworkException( "application was not found!" + urlPart );
    }


    public static HomePage loginAndOpenHomePage( TestSession testSession )
    {
        User user = testSession.getCurrentUser();
        LoginPage loginPage = new LoginPage( testSession );
        //save window-handle for HomePage
        String homeTabHandle = testSession.getDriver().getWindowHandle();
        testSession.put( HomePage.HOME_PAGE_TAB_HANDLE, homeTabHandle );
        if ( user != null )
        {
            return loginPage.doLogin( user.getDisplayName(), user.getPassword() );
        }
        else
        {
            return loginPage.doLogin( "su", "password" );
        }
    }
}
