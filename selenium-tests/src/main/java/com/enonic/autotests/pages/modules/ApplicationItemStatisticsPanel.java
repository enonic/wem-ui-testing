package com.enonic.autotests.pages.modules;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.pages.Application;
import com.enonic.autotests.utils.NameHelper;
import com.enonic.autotests.utils.TestUtils;
import com.enonic.autotests.vo.application.ApplicationInfo;

import static com.enonic.autotests.utils.SleepHelper.sleep;

public class ApplicationItemStatisticsPanel
    extends Application
{
    private final String STATISTIC_PANEL_CONTAINER = "//div[contains(@id,'ApplicationItemStatisticsPanel')]";

    private final String DATA_CONTAINER = "//div[@class='application-data-container']";

    private final String BUILD_DATE = DATA_CONTAINER + "//li[text()='Build date']/following-sibling::li";

    private final String VERSION = DATA_CONTAINER + "//li[text()='Version']/following-sibling::li";

    private final String KEY = DATA_CONTAINER + "//li[text()='Key']/following-sibling::li";

    private final String SYSTEM_REQUIRED = DATA_CONTAINER + "//li[text()='System Required']/following-sibling::li";

    private final String LIST_ITEMS = "/following-sibling::li";

    private final String INFO_DATA_GROUP = "//div[contains(@id,'ItemDataGroup') and contains(@class,'info')]";

    private final String SCHEMA_DATA_GROUP = "//div[contains(@id,'ItemDataGroup') and contains(@class,'schema')]";

    private final String PROVIDERS_DATA_GROUP = "//div[contains(@id,'ItemDataGroup') and contains(@class,'providers')]";

    private final String PROVIDERS_DATA_LIST = PROVIDERS_DATA_GROUP + "//li[@class='list-header' and text()='Key']";

    private final String DESCRIPTORS_DATA_GROUP = "//div[contains(@id,'ItemDataGroup') and contains(@class,'descriptors')]";

    private final String CONTENT_TYPES_HEADER = DATA_CONTAINER + "//div[contains(@class,'schemas')]//li[text()='Content Types']";

    private final String CONTENT_TYPES = CONTENT_TYPES_HEADER + LIST_ITEMS;

    private final String MIXINS_HEADER = DATA_CONTAINER + "//div[contains(@class,'schemas')]//li[text()='Mixins']";

    private final String MIXINS = MIXINS_HEADER + LIST_ITEMS;

    private final String RELATIONSHIP_TYPES_HEADER = DATA_CONTAINER + "//div[contains(@class,'schemas')]//li[text()='RelationshipTypes']";

    private final String PAGE_HEADER = DATA_CONTAINER + "//div[contains(@class,'descriptors')]//li[text()='Page']";

    private final String PAGES = PAGE_HEADER + LIST_ITEMS;

    private final String PART_HEADER = DATA_CONTAINER + "//div[contains(@class,'descriptors')]//li[text()='Part']";

    private final String PARTS = PART_HEADER + LIST_ITEMS;

    private final String LAYOUT_HEADER = DATA_CONTAINER + "//div[contains(@class,'descriptors')]//li[text()='Layout']";

    private final String LAYOUTS = LAYOUT_HEADER + LIST_ITEMS;

    private final String RELATIONSHIP_TYPES = RELATIONSHIP_TYPES_HEADER + LIST_ITEMS;

    public ApplicationItemStatisticsPanel( TestSession session )
    {
        super( session );
    }

    public boolean isLayoutHeaderPresent()
    {
        return doScrollAndFindHeader( LAYOUT_HEADER );
    }

    public boolean isRelationShipTypesHeaderPresent()
    {
        return doScrollAndFindHeader( RELATIONSHIP_TYPES_HEADER );
    }

    public List<String> getLayouts()
    {
        return getDisplayedStrings( By.xpath( LAYOUTS ) );
    }

    public boolean isInfoDataGroupDisplayed()
    {
        return isElementDisplayed( INFO_DATA_GROUP );
    }

    public boolean isSchemaDataGroupDisplayed()
    {
        return isElementDisplayed( SCHEMA_DATA_GROUP );
    }

    public boolean isProvidersDataGroupDisplayed()
    {
        return isElementDisplayed( PROVIDERS_DATA_GROUP );
    }

    public boolean isProvidersDataKeyDisplayed()
    {
        return isElementDisplayed( PROVIDERS_DATA_LIST );
    }

    public boolean isDescriptorsDataGroupDisplayed()
    {
        return isElementDisplayed( DESCRIPTORS_DATA_GROUP );
    }

    public boolean isPartHeaderPresent()
    {
        return doScrollAndFindHeader( PART_HEADER );
    }

    public List<String> getParts()
    {
        return findElements( By.xpath( PARTS ) ).stream().filter( WebElement::isDisplayed ).map( WebElement::getText ).collect(
            Collectors.toList() );
    }

    public boolean isPageHeaderPresent()
    {
        return doScrollAndFindHeader( PAGE_HEADER );
    }

    public boolean isContentTypesHeaderPresent()
    {
        return doScrollAndFindHeader( CONTENT_TYPES_HEADER );
    }

    public List<String> getPages()
    {
        return getDisplayedStrings( By.xpath( PAGES ) );
    }

    public void scrollPanelToTop()
    {
        ( (JavascriptExecutor) getDriver() ).executeScript( "return document.getElementsByClassName('slick-viewport')[0].scrollTop=0" );
        sleep( 1000 );
    }

    public boolean doScrollAndFindHeader( String itemXpath )
    {
        boolean isPresent = isElementDisplayed( itemXpath );
        if ( isPresent )
        {
            return true;
        }
        if ( !isPanelScrollable() )
        {
            return false;
        }
        scrollPanelToTop();
        String panelId = getPanelId();
        int scrollTopValue = getScrollHeight( panelId ) - getClientHeight( panelId );
        long scrollTopBefore;
        long scrollTopAfter;
        for ( ; ; )
        {
            scrollTopBefore = 0;
            scrollTopAfter = doScrollPanel( scrollTopValue );

            if ( isElementDisplayed( itemXpath ) )
            {
                getLogger().info( "header was found: " + itemXpath );
                return true;
            }
            if ( scrollTopBefore == scrollTopAfter )
            {
                break;
            }
            scrollTopValue += scrollTopValue;
        }
        getLogger().info( "header was not found! " + itemXpath );
        TestUtils.saveScreenshot( getSession(), "scrolled_" + NameHelper.resolveScreenshotName( "panel" ) );
        return false;
    }

    public Long getPanelScrollTopValue( String panelId )
    {
        return (Long) ( (JavascriptExecutor) getDriver() ).executeScript(
            String.format( "return document.getElementById('%s').scrollTop", panelId ) );
    }

    public long doScrollPanel( int value )
    {
        String id = getPanelId();
        WebElement panel =
            findElements( By.xpath( STATISTIC_PANEL_CONTAINER ) ).stream().filter( WebElement::isDisplayed ).findFirst().get();
        ( (JavascriptExecutor) getDriver() ).executeScript( "arguments[0].scrollTop=arguments[1]", panel, value );
        return getPanelScrollTopValue( id );
    }

    private String getPanelId()
    {
        return getDisplayedElement( By.xpath( STATISTIC_PANEL_CONTAINER ) ).getAttribute( "id" );
    }

    public boolean isPanelScrollable()
    {
        String id = getPanelId();
        int clientHeight = getClientHeight( id );
        int scrollHeight = getScrollHeight( id );
        return scrollHeight > clientHeight;
    }

    public int getScrollHeight( String applicationItemStatisticsPanelId )
    {
        JavascriptExecutor executor = (JavascriptExecutor) getSession().getDriver();

        String script = String.format( "return document.getElementById('%s').scrollHeight", applicationItemStatisticsPanelId );
        Object obj = executor.executeScript( script );
        int scrollHeight = Integer.valueOf( obj.toString() );
        return scrollHeight;
    }

    public int getClientHeight( String applicationItemStatisticsPanelId )
    {
        String script = String.format( "return document.getElementById('%s').clientHeight", applicationItemStatisticsPanelId );
        Object obj = getJavaScriptExecutor().executeScript( script );
        int scrollHeight = Integer.valueOf( obj.toString() );
        return scrollHeight;
    }

    public List<String> getRelationShipTypes()
    {
        return findElements( By.xpath( RELATIONSHIP_TYPES ) ).stream().map( WebElement::getText ).collect( Collectors.toList() );
    }

    public List<String> getContentTypes()
    {
        waitUntilVisibleNoException( By.xpath( CONTENT_TYPES_HEADER ), 1 );
        return getDisplayedStrings( By.xpath( CONTENT_TYPES ) );
    }

    public List<String> getMixins()
    {
        return getDisplayedStrings( By.xpath( MIXINS ) );
    }

    public boolean isBuildDatePresent()
    {
        return isElementDisplayed( BUILD_DATE );
    }

    public boolean isVersionPresent()
    {
        return findElements( By.xpath( VERSION ) ).stream().filter( WebElement::isDisplayed ).count() > 0;
    }

    public boolean isKeyPresent()
    {
        return findElements( By.xpath( KEY ) ).stream().filter( WebElement::isDisplayed ).count() > 0;
    }

    public boolean isSystemRequiredPresent()
    {
        return findElements( By.xpath( SYSTEM_REQUIRED ) ).stream().filter( WebElement::isDisplayed ).count() > 0;
    }

    public String getBuildDate()
    {
        return findElements( By.xpath( BUILD_DATE ) ).stream().filter( WebElement::isDisplayed ).findFirst().get().getText();
    }

    public String getVersion()
    {
        return findElements( By.xpath( VERSION ) ).stream().filter( WebElement::isDisplayed ).findFirst().get().getText();
    }

    public String getKey()
    {
        return getDisplayedString( KEY );
    }

    public String getSystemRequired()
    {
        return findElements( By.xpath( SYSTEM_REQUIRED ) ).stream().filter( WebElement::isDisplayed ).findFirst().get().getText();

    }

    public ApplicationInfo getApplicationInfo()
    {
        return ApplicationInfo.builder().buildDate( getBuildDate() ).version( getVersion() ).key( getKey() ).build();
    }
}
