package com.enonic.autotests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.AuthenticationException;
import com.enonic.autotests.exceptions.TestFrameworkException;

/**
 * Page Object for Login page version 5.0
 * 
 */
public class LoginPage extends Page
{

	private String TITLE = "Enonic WEM Admin";
	
	private long LOGIN_PAGE_TIMEOUT = 10;
	
	private final String EMAIL_INPUT_XPATH = "//input[@placeholder = 'userid or e-mail']";

	@FindBy(xpath = EMAIL_INPUT_XPATH)
	private WebElement usernameInput;

	@FindBy(xpath = "//input[@placeholder = 'password']")
	private WebElement passwordInput;

	private final String loginEnabledClass = "login-button";
	private String loginButtonXpath = "//button[contains(@class,'%s')]";

	/**
	 * The constructor.
	 * 
	 * @param session
	 */
	public LoginPage( TestSession session )
	{
		super(session);
		
	}

	/**
	 * Types the username and password and click the 'login' button.
	 * 
	 * @param username
	 * @param password
	 */
	public void doLogin(String username, String password)
	{
		boolean isLoginPageLoaded = waitUntilTitleLoad(TITLE, LOGIN_PAGE_TIMEOUT);
		if(!isLoginPageLoaded)
		{
			throw new TestFrameworkException("Login page was not loaded, timeout sec:" + LOGIN_PAGE_TIMEOUT);
		}
		getLogger().info("Login page title: "+getDriver().getTitle());
		getLogger().info("Login action started. Username: " + username + " Password:" + password);
		
		boolean isEmailInputPresent = waitAndFind(By.xpath(EMAIL_INPUT_XPATH));
		if(!isEmailInputPresent)
		{
			throw new TestFrameworkException(" input 'userid or e-mail' was not found on page!");
		}
		usernameInput.sendKeys(username);

		passwordInput.sendKeys(password);

		boolean isEnabledButton = waitAndFind(By.xpath(String.format(loginButtonXpath, loginEnabledClass)));
		if (!isEnabledButton)
		{
			logError("the Button 'Log in' is disabled");
			throw new AuthenticationException("wrong password or username");
		}
		WebElement loginButton = findElement(By.xpath(String.format(loginButtonXpath, loginEnabledClass)));
		loginButton.click();

	}

}
