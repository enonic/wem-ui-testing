package com.enonic.autotests.pages.form;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.enonic.autotests.TestSession;
import com.enonic.xp.data.PropertyTree;

import static com.enonic.autotests.utils.SleepHelper.sleep;

public class GeoPointFormViewPanel
    extends FormViewPanel
{
    public static String GEO_POINT_PROPERTY = "geo_location";

    @FindBy(xpath = FORM_VIEW +
        "//div[contains(@id,'api.form.InputView') and descendant::div[@title='Geo Location']]//input[contains(@id,'TextInput')]")
    private WebElement geoLocationInput;


    public GeoPointFormViewPanel( final TestSession session )
    {
        super( session );
    }

    @Override
    public FormViewPanel type( final PropertyTree data )
    {

        String geoPointValue = data.getString( GEO_POINT_PROPERTY );
        // type a date
        geoLocationInput.sendKeys( geoPointValue );
        sleep( 300 );
        return this;
    }

    public String getGeoPointValue()
    {
        return geoLocationInput.getAttribute( "value" );
    }
}
