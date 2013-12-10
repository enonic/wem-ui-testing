package com.enonic.autotests.services;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.enonic.autotests.AppConstants;
import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.model.User;
import com.enonic.autotests.pages.HomePage;
import com.enonic.autotests.pages.Page;
import com.enonic.autotests.pages.accounts.AccountsPage;
import com.enonic.autotests.pages.cm.ContentTablePage;
import com.enonic.autotests.pages.schemamanager.SchemaTablePage;
import com.enonic.autotests.pages.spaceadmin.SpaceAdminPage;
import com.enonic.autotests.utils.TestUtils;

public class NavigatorHelper
{

	/**
	 * Opens 'Space Admin' application.
	 * 
	 * @param testSession
	 *            {@link TestSession} instance
	 * @return {@link SpaceAdminPage} instance.
	 */
	public static SpaceAdminPage openSpaceAdmin(TestSession testSession)
	{

		HomePage home = loginAndOpenHomePage(testSession);
		return home.openSpaceAdminApplication();
	}

	/**
	 * Opens 'Content Manager' application.
	 * 
	 * @param testSession
	 *            {@link TestSession} instance.
	 * @return {@link SpaceAdminPage} instance.
	 */
	public static ContentTablePage openContentManager(TestSession testSession)
	{
		if (testSession.isLoggedIn())
		{
			if (ContentTablePage.isOpened(testSession))
			{
				return new ContentTablePage(testSession);
			}
			boolean isHomeButtonPresent = TestUtils.getInstance().waitAndFind(By.xpath(Page.HOME_BUTTON_XPATH), testSession.getDriver());
			if (!isHomeButtonPresent)
			{
				throw new TestFrameworkException("'go to home' button was not found");
			}
			testSession.getDriver().findElement(By.xpath(Page.HOME_BUTTON_XPATH)).click();
			HomePage homepage = new HomePage(testSession);
			testSession.getDriver().switchTo().window(testSession.getWindowHandle());
			homepage.openContentManagerApplication();
			return new ContentTablePage(testSession);
		}
      // if user not logged in:
		else
		{

			HomePage home = loginAndOpenHomePage(testSession);
			ContentTablePage cmPage = home.openContentManagerApplication();
			cmPage.waituntilPageLoaded(AppConstants.PAGELOAD_TIMEOUT);
			return cmPage;
		}

	}

	public static SchemaTablePage openSchemaManager(TestSession testSession)
	{
		if (ContentTablePage.isOpened(testSession))
		{
			return new SchemaTablePage(testSession);
		} else
		{
			HomePage home = loginAndOpenHomePage(testSession);
			SchemaTablePage schemasPage = home.openSchemaManagerApplication();
			schemasPage.waituntilPageLoaded(TestUtils.TIMEOUT_IMPLICIT);
			return schemasPage;
		}

	}

	/**
	 * Open 'Home' page, click by 'Accounts' link and open application's page.
	 * 
	 * @param testSession
	 *            {@link TestSession} instance.
	 * @return {@link AccountsPage} instance.
	 */
	public static AccountsPage openAccounts(TestSession testSession)
	{
		HomePage home = loginAndOpenHomePage(testSession);
		return home.openAccountsApplication();
	}

	/**
	 * @param testSession
	 *            {@link TestSession} instance.
	 * @param iframeXpath
	 *            frame's xpath.
	 */
	public static void switchToIframe(TestSession testSession, String iframeXpath)
	{
		String whandle = testSession.getDriver().getWindowHandle();
		testSession.getDriver().switchTo().window(whandle);
		List<WebElement> frames = testSession.getDriver().findElements(By.xpath(iframeXpath));
		if (frames.size() == 0)
		{
			throw new TestFrameworkException("Unable to switch to the iframe" + iframeXpath);
		}
		testSession.getDriver().switchTo().frame(frames.get(0));
	}

	/**
	 * 'Login' to cms and opens the 'Home' page that contains links to all
	 * applications.
	 * 
	 * @param testSession
	 *            {@link TestSession} instance.
	 * @return {@link HomePage} instance.
	 */
	public static HomePage loginAndOpenHomePage(TestSession testSession)
	{
		User user = testSession.getCurrentUser();
		HomePage home = new HomePage(testSession);
		if (user != null)
		{
			home.open(user.getUserInfo().getName(), user.getUserInfo().getPassword());
		} else
		{
			home.open("admin", "pass");
		}
		return home;
	}

}
