package com.enonic.autotests.pages.form.liveedit;

import org.openqa.selenium.By;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.utils.NameHelper;

import static com.enonic.autotests.utils.SleepHelper.sleep;


public class PartComponentView
    extends UIComponent
{
    private final String COMPONENT_CONTAINER = "//div[contains(@id,'PartComponentView')]";

    public static String PART_XPATH = "//div[contains(@id,'PartDescriptorViewer') and descendant::p[contains(.,'%s')]]";

    public PartComponentView( final TestSession session )
    {
        super( session );
    }

    public LiveFormPanel selectItem( String partName )
    {
        if ( !isElementDisplayed( COMPONENT_CONTAINER + COMBOBOX_OPTION_FILTER_INPUT ) )
        {
            saveScreenshot( NameHelper.uniqueName( "err_opt_filter" ) );
            throw new TestFrameworkException( "option filter input was not displayed" );
        }
        getDisplayedElement( By.xpath( COMPONENT_CONTAINER + COMBOBOX_OPTION_FILTER_INPUT ) ).sendKeys( partName );
        sleep( 400 );
        if ( !isPartExists( partName ) )
        {
            saveScreenshot( NameHelper.uniqueName( "err_" + partName ) );
            throw new TestFrameworkException( "The part with name: " + partName + "  was not found!" );
        }
        clickOnOptionsItem( partName );
        sleep( 1000 );
        return new LiveFormPanel( getSession() );
    }

    private boolean isPartExists( String partName )
    {
        return isElementDisplayed( COMPONENT_CONTAINER + String.format( NAMES_ICON_VIEW, partName ) );
    }

    private void clickOnOptionsItem( String partName )
    {
        getDisplayedElement( By.xpath( COMPONENT_CONTAINER + String.format( NAMES_ICON_VIEW, partName ) ) ).click();
    }
}
