package com.enonic.autotests.pages.form;


import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.services.NavigatorHelper;
import com.enonic.autotests.utils.NameHelper;
import com.enonic.autotests.utils.TestUtils;
import com.enonic.xp.data.PropertyTree;

import static com.enonic.autotests.utils.SleepHelper.sleep;

public class PageTemplateFormViewPanel
    extends FormViewPanel
{
    public static final String SUPPORTS = "supports";

    public static final String PAGE_CONTROLLER = "pageController";

    private String PAGE_DESCRIPTOR_DROP_DOWN_FILTER_INPUT =
        "//div[@id='api.content.page.PageDescriptorDropdown']//input[contains(@id,'api.ui.selector.dropdown.DropdownOptionFilterInput')]";

    @FindBy(xpath = "//div[contains(@id,'api.form.FormView')]//input[contains(@class,'option-filter-input')]")
    private WebElement optionFilterInput;

    @FindBy(xpath = "//div[contains(@id,'api.form.FormView')]//input[contains(@name,'menuName')]")
    private WebElement menuNameInput;

    public PageTemplateFormViewPanel( final TestSession session )
    {
        super( session );
    }

    @Override
    public FormViewPanel type( final PropertyTree data )
    {
        String supports = data.getString( SUPPORTS );
        selectSupportOption( supports );
        selectPageController( data.getString( PAGE_CONTROLLER ) );
        return this;
    }

    private void selectSupportOption( String supports )
    {
        optionFilterInput.sendKeys( supports );
        sleep( 500 );
        String siteContentTypeGridItem = String.format( "//div[contains(@id,'NamesView')]/p[contains(.,'%s')]", supports );
        if ( !isElementDisplayed( siteContentTypeGridItem ) )
        {
            throw new TestFrameworkException( "content type with name: " + supports + "  was not found!" );
        }
        //select supports: portal:site
        getDisplayedElement( By.xpath( siteContentTypeGridItem ) ).click();
        sleep( 500 );
    }

    private void selectPageController( String pageName )
    {
        NavigatorHelper.switchToLiveEditFrame( getSession() );
        getDisplayedElement( By.xpath( PAGE_DESCRIPTOR_DROP_DOWN_FILTER_INPUT ) ).sendKeys( pageName );
        sleep( 500 );
        //select a 'page name'
        String pageItemXpath = String.format( "//div[contains(@id,'PageDescriptorDropdown')]//h6[text()='%s']", pageName );
        if ( !isElementDisplayed( pageItemXpath ) )
        {
            TestUtils.saveScreenshot( getSession(), "err_" + NameHelper.uniqueName( pageName ) );
            throw new TestFrameworkException( "page controller was not found! " + pageName );
        }
        TestUtils.saveScreenshot( getSession(), NameHelper.uniqueName( pageName ) );
        getDisplayedElement( By.xpath( pageItemXpath ) ).click();
        NavigatorHelper.switchToContentManagerFrame( getSession() );

    }
}
