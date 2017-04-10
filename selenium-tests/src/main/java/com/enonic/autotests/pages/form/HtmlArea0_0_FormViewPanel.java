package com.enonic.autotests.pages.form;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.pages.Application;
import com.enonic.xp.data.PropertyTree;

import static com.enonic.autotests.utils.SleepHelper.sleep;

public class HtmlArea0_0_FormViewPanel
    extends BaseHtmlAreaFormViewPanel
{

    private final String REMOVE_AREA_BUTTON_XPATH = FORM_VIEW + "//div[contains(@id,'HtmlArea')]//a[@class='remove-button']";

    public HtmlArea0_0_FormViewPanel( final TestSession session )
    {
        super( session );
    }

    public boolean isOpened()
    {
        return waitUntilVisibleNoException( By.xpath( STEP_XPATH ), Application.EXPLICIT_NORMAL );
    }

    @Override
    public FormViewPanel type( final PropertyTree data )
    {
        long numberOfEditors = 1;
        if ( data.getLong( NUMBER_OF_EDITORS ) != null )
        {
            numberOfEditors = data.getLong( NUMBER_OF_EDITORS );
            addEditors( numberOfEditors );
        }
        List<WebElement> frames = findElements( By.xpath( TINY_MCE ) );
        if ( frames.size() == 0 )
        {
            throw new TestFrameworkException( "no one text input was not found" );
        }
        int i = 0;
        for ( final String sourceString : data.getStrings( STRINGS_PROPERTY ) )
        {
            buildActions().click( frames.get( i ) ).build().perform();
            sleep( 500 );
            setTextIntoArea( frames.get( i ).getAttribute( "id" ), sourceString );
            sleep( 300 );
            i++;
            if ( i >= numberOfEditors )
            {
                break;
            }
        }
        sleep( 300 );
        return this;
    }

    public void addEditors( long numberOfEditors )
    {
        for ( int i = 1; i < numberOfEditors; i++ )
        {
            clickOnAddButton();
            sleep( 500 );
        }
    }

    public boolean waitUntilAddButtonNotVisible()
    {
        return waitsElementNotVisible( By.xpath( ADD_BUTTON_XPATH ), Application.EXPLICIT_NORMAL );
    }

    public HtmlArea0_0_FormViewPanel removeLastTextArea()
    {
        List<WebElement> buttons = findElements( By.xpath( REMOVE_AREA_BUTTON_XPATH ) );
        if ( buttons.size() != 0 )
        {
            Actions builder = new Actions( getDriver() );
            builder.moveToElement( buttons.get( buttons.size() - 1 ) ).click().build().perform();
            //builder.click( buttons.get( buttons.size() - 1 ).getLocation(). ) .build().perform();
            //buttons.get( buttons.size() - 1 ).click();
            sleep( 700 );
            return this;
        }
        else
        {
            throw new TestFrameworkException( "no one 'close' button was found!" );
        }
    }
}
