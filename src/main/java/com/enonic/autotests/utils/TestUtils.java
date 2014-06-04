package com.enonic.autotests.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.pages.Application;

public class TestUtils
{
    public static Logger logger = Logger.getLogger( TestUtils.class );

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd-HH-mm-ss";

    /**
     * The Default constructor.
     */
    private TestUtils()
    {

    }

    public static String saveScreenshot( final TestSession testSession, String screenshotName )
    {
        WebDriver driver = testSession.getDriver();
        String fileName = screenshotName + ".png";
        File folder = new File( "target/screenshots" );
        if ( !folder.exists() )
        {
            if ( !folder.mkdir() )
            {
                System.out.println( "Folder for snapshots was not created " );
            }
            else
            {
                System.out.println( "Folder for snapshots was created " + folder.getAbsolutePath() );
            }
        }
        File screenshot = ( (TakesScreenshot) driver ).getScreenshotAs( OutputType.FILE );
        String fullFileName = folder.getAbsolutePath() + File.separator + fileName;
        try
        {
            FileUtils.copyFile( screenshot, new File( fullFileName ) );
        }
        catch ( IOException e )
        {

        }
        return fileName;
    }

    /**
     * @param testSession
     */
    public static String saveScreenshot( final TestSession testSession )
    {
        String fileName = timeNow();
        return saveScreenshot( testSession, fileName );
    }

    public static String timeNow()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat( DATE_FORMAT_NOW );
        return sdf.format( cal.getTime() );
    }

    public static void clickByLocator( final By locator, WebDriver driver )
    {
        WebElement myDynamicElement = ( new WebDriverWait( driver, 10 ) ).until( ExpectedConditions.presenceOfElementLocated( locator ) );
        myDynamicElement.click();
    }

    public static int getNumberFromFilterLabel( String label )
    {
        int start = label.indexOf( "(" );
        int end = label.indexOf( ")" );
        if ( start == -1 || end == -1 )
        {
            throw new TestFrameworkException( "wrong label!" );
        }
        return Integer.valueOf( label.substring( start + 1, end ) );

    }

    /**
     * @param locator
     * @param driver
     */
    public static void clickByElement( final By locator, final WebDriver driver )
    {
        final long startTime = System.currentTimeMillis();
        Wait<WebDriver> wait =
            new FluentWait<WebDriver>( driver ).withTimeout( Application.IMPLICITLY_WAIT, TimeUnit.MILLISECONDS ).pollingEvery( 500,
                                                                                                                                TimeUnit.MILLISECONDS );
        // .ignoring( StaleElementReferenceException.class );
        wait.until( new ExpectedCondition<Boolean>()
        {
            @Override
            public Boolean apply( WebDriver webDriver )
            {
                try
                {
                    webDriver.findElement( locator ).click();
                    return true;
                }
                catch ( StaleElementReferenceException e )
                {
                    logger.info( e.getMessage() + "\n" );
                    logger.info( "Trying again..." );
                    return false;
                }
            }
        } );
        final long endTime = System.currentTimeMillis();
        logger.info( "clickByElement time is " + ( endTime - startTime ) );
    }

    public static String waitNotificationMessage( final By locator, final WebDriver driver, long timeout )
    {
        WebDriverWait wait = new WebDriverWait( driver, timeout );
        WebElement element = wait.until( ExpectedConditions.visibilityOfElementLocated( locator ) );
        return element.getText();
    }

    public static String createTempFile( String s )
    {
        try
        {
            File f = File.createTempFile( "uploadTest", "tempfile" );
            f.deleteOnExit();
            writeStringToFile( s, f );
            return f.getAbsolutePath();
        }
        catch ( Exception e )
        {
            throw new TestFrameworkException( "Error during creation TMP-file" );
        }
    }

    public static void writeStringToFile( String s, File file )
        throws IOException
    {
        FileOutputStream in = null;
        try
        {
            in = new FileOutputStream( file );
            FileChannel fchan = in.getChannel();
            BufferedWriter bf = new BufferedWriter( Channels.newWriter( fchan, "UTF-8" ) );
            bf.write( s );
            bf.close();
        }
        finally
        {
            if ( in != null )
            {
                in.close();
            }
        }
    }

}
