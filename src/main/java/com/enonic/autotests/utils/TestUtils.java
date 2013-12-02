package com.enonic.autotests.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.logger.Logger;
import com.enonic.autotests.pages.cm.SelectContentTypeDialog.ContentTypes;

public class TestUtils
{
	public static Logger logger = Logger.getLogger();
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH-mm-ss";

	private static TestUtils instance;

	public static final long TIMEOUT_IMPLICIT = 5;

	/**
	 * @return
	 */
	public static synchronized TestUtils getInstance()
	{
		if (instance == null)
		{
			instance = new TestUtils();

		}
		return instance;
	}

	public String buildFullNameOfContent(String contentName, String... parentNames)
	{
		StringBuilder builder = new StringBuilder();
		if (parentNames.length == 0)
		{
			builder.append(contentName).append(":/");
			return builder.toString();
		}
		builder.append(parentNames[0].toLowerCase()).append("/");
		for (int i = 1; i < parentNames.length; i++)
		{
			builder.append(parentNames[i].toLowerCase()).append("/");
		}

		String fullContentName = builder.append(contentName).toString();
		return fullContentName;
	}

	/**
	 * The Default constructor.
	 */
	private TestUtils()
	{

	}

	public ContentTypes getContentType(String ctype)
	{
		ContentTypes result = null;
		ContentTypes[] values = ContentTypes.values();
		for(ContentTypes val: values )
		{
			if(val.getValue().equals(ctype))
			{
				result =  val;
				
			}
		}
		return result;
	}
	public boolean waitAndCheckAttrValue(WebDriver webDriver, final WebElement element, final String attributeName, final String attributeValue, long timeout)
	{
		WebDriverWait wait = new WebDriverWait(webDriver, timeout);
		try
		{
			return wait.until(new ExpectedCondition<Boolean>()
			{
				@Override
				public Boolean apply(WebDriver webDriver)
				{
					try
					{
						return element.getAttribute(attributeName).contains(attributeValue);

					} catch (Exception e)
					{

						return false;
					}
				}
			});
		} catch (org.openqa.selenium.TimeoutException e)
		{
			return false;
		}

	}

	public WebElement scrollTableAndFind(TestSession session, String elementXpath, String scrollXpath)
	{
		WebElement element = null;
		List<WebElement> divScroll = session.getDriver().findElements(By.xpath(scrollXpath));
		if (divScroll.size() == 0)
		{
			throw new TestFrameworkException("Div was not found xpath: " + scrollXpath);
		}
		long gridHeight = (Long) ((JavascriptExecutor) session.getDriver()).executeScript("return arguments[0].scrollHeight", divScroll.get(0));
		// ((JavascriptExecutor)getSession().getDriver()).executeScript("arguments[0].scrollTop=arguments[1]",
		// divScroll.get(0),170);

		for (int scrollTop = 0; scrollTop <= gridHeight;)
		{
			scrollTop += 40;
			((JavascriptExecutor) session.getDriver()).executeScript("arguments[0].scrollTop=arguments[1]", divScroll.get(0), scrollTop);
			element = session.getDriver().findElement(By.xpath(elementXpath));
			if (element.isDisplayed())
			{
				return element;
			}
		}
		return null;
	}

	/**
	 * Types text in input field.
	 * 
	 * @param session
	 * @param input input type=text
	 * @param text text for input.
	 */
	public void clearAndType(TestSession session, WebElement input, String text)
	{
		if(session.getIsRemote())
		{
			input.sendKeys(Keys.chord(Keys.CONTROL, "a"), text);
		} 
		else{
			
			String os = System.getProperty("os.name").toLowerCase();
			logger.info("clearAndType: OS System is " + os);
			if (os.indexOf("mac") >= 0)
			{
				input.sendKeys(Keys.chord(Keys.COMMAND, "a"), text);
			}else{
				input.sendKeys(Keys.chord(Keys.CONTROL, "a"), text);
			}
			
		}
		

	}


	public void waitUntilVisible(final TestSession testSession, final By by)
	{
		new WebDriverWait(testSession.getDriver(), TIMEOUT_IMPLICIT).until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	public void waitUntilTitleVisible(final TestSession testSession, final String title)
	{
		(new WebDriverWait(testSession.getDriver(), TestUtils.TIMEOUT_IMPLICIT)).until(new ExpectedCondition<Boolean>()
		{
			public Boolean apply(WebDriver d)
			{
				return d.getTitle().trim().contains(title);
			}
		});
	}

	/**
	 * @param by
	 * @param driver
	 * @return
	 */
	public boolean waitAndFind(final By by, final WebDriver driver)
	{

		return waitAndFind(by, driver, 4l);
	}

	public boolean waitAndFind(final By by, final WebDriver driver, long timeout)
	{

		driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
		List<WebElement> elements = driver.findElements(by);
		return ((elements.size() > 0) && (elements.get(0).isDisplayed()));
	}

	/**
	 * An expectation for checking an element is visible and enabled such that
	 * you can click it.
	 * 
	 * @param testSession
	 * @param by
	 * @throws WebElementException
	 */
	public void waitUntilElementEnabled(final TestSession testSession, final By by) throws TestFrameworkException
	{
		try
		{
			new WebDriverWait(testSession.getDriver(), TIMEOUT_IMPLICIT).until(ExpectedConditions.elementToBeClickable(by));
		} catch (TimeoutException ex)
		{
			logger.error("TimeoutException, element is disabled" + by.toString(), testSession);
			throw new TestFrameworkException("Element is disabled but should be enabled!!! " + ex.getMessage());
		}
	}

	public boolean waitUntilElementEnabledNoException(final TestSession testSession, final By by, long timeout)
	{
		try
		{
			new WebDriverWait(testSession.getDriver(), timeout).until(ExpectedConditions.elementToBeClickable(by));
			return true;
		} catch (TimeoutException ex)
		{
			logger.info("TimeoutException, element is disabled" + by.toString());
			return false;
		}
	}

	public boolean waitUntilVisibleNoException(final TestSession testSession, By by, long timeout)
	{
		WebDriverWait wait = new WebDriverWait(testSession.getDriver(), timeout);
		try
		{

			wait.until(ExpectedConditions.visibilityOfElementLocated(by));

			return true;
		} catch (Exception e)
		{

			return false;
		}
	}

	public boolean waitsElementNotVisible(final TestSession testSession, By by, long timeout)
	{
		WebDriverWait wait = new WebDriverWait(testSession.getDriver(), timeout);
		try
		{

			wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
			System.out.println("Element not presented on the page or not visible: " + by.toString());
			return true;
		} catch (Exception e)
		{
			System.out.println("Element is visible:" + by.toString());
			return false;

		}

	}

	/**
	 * @param screenshotFileName
	 * @param driver
	 */
	public String saveScreenshot(final TestSession testSession)
	{
		WebDriver driver = testSession.getDriver();
		String fileName = timeNow() + ".png";
		File folder = new File(System.getProperty("user.dir") + File.separator + "snapshots");

		if (!folder.exists())
		{
			if (!folder.mkdir())
			{
				System.out.println("Folder for snapshots was not created ");
			} else
			{
				System.out.println("Folder for snapshots was created " + folder.getAbsolutePath());
			}
		}
		File screenshot = null;

		if ((Boolean) testSession.get(TestSession.IS_REMOTE))
		{

			WebDriver augmentedDriver = new Augmenter().augment(driver);
			screenshot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
		} else
		{
			screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		}

		String fullFileName = folder.getAbsolutePath() + File.separator + fileName;

		try
		{
			FileUtils.copyFile(screenshot, new File(fullFileName));
		} catch (IOException e)
		{
			// TODO Auto-generated catch block

		}
		return fileName;
	}

	public String timeNow()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

	public void clickByLocator(final By locator, WebDriver driver)
	{
		WebElement myDynamicElement = (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(locator));
		myDynamicElement.click();
	}

	/**
	 * @param locator
	 * @param driver
	 */
	public void clickByLocator1(final By locator, final WebDriver driver)
	{
		final long startTime = System.currentTimeMillis();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(90000, TimeUnit.MILLISECONDS).pollingEvery(5500, TimeUnit.MILLISECONDS);
		// .ignoring( StaleElementReferenceException.class );
		wait.until(new ExpectedCondition<Boolean>()
		{
			@Override
			public Boolean apply(WebDriver webDriver)
			{
				try
				{
					webDriver.findElement(locator).click();
					return true;
				} catch (StaleElementReferenceException e)
				{
					// staticlogger.info( e.getMessage() + "\n");
					// staticlogger.info("Trying again...");
					return false;
				}
			}
		});
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		// TODO add perfomance log
		// logger.perfomance("clickByLocator:" + locator.toString(), startTime);
		// staticlogger.info("Finished click after waiting for " + totalTime +
		// " milliseconds.");
	}

	public String getNotificationMessage(final By locator, final WebDriver driver, long timeout)
	{
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
		return element.getText();

	}

	/**
	 * @param by
	 * @param driver
	 * @return
	 */
	public boolean checkIfDisplayed(final By by, final WebDriver driver)
	{
		List<WebElement> elements = driver.findElements(by);
		return ((elements.size() > 0) && (elements.get(0).isDisplayed()));
	}

	public WebElement getIfDisplayed(final By by, final WebDriver driver)
	{
		List<WebElement> elements = driver.findElements(by);
		if ((elements.size() > 0) && (elements.get(0).isDisplayed()))
		{
			return elements.get(0);
		}
		return null;
	}

	public String createTempFile(String s)
	{
		try
		{
			File f = File.createTempFile("uploadTest", "tempfile");
			f.deleteOnExit();
			writeStringToFile(s, f);
			return f.getAbsolutePath();
		} catch (Exception e)
		{
			throw new TestFrameworkException("Error during creation TMP-file");

		}
	}

	public void writeStringToFile(String s, File file) throws IOException
	{
		FileOutputStream in = null;
		try
		{
			in = new FileOutputStream(file);
			FileChannel fchan = in.getChannel();
			BufferedWriter bf = new BufferedWriter(Channels.newWriter(fchan, "UTF-8"));
			bf.write(s);
			bf.close();
		} finally
		{
			if (in != null)
			{
				in.close();
			}
		}
	}

}
