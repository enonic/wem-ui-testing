package com.enonic.autotests.pages.form;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.utils.TestUtils;
import com.enonic.xp.data.PropertyTree;

import static com.enonic.autotests.utils.SleepHelper.sleep;

public class CheckBoxFormViewPanel
    extends FormViewPanel
{
    public static String CHECKBOX_PROPERTY = "checkbox";


    @FindBy(xpath = FORM_VIEW +
        "//div[contains(@id,'api.form.InputView') and descendant::div[@title='To Check']]//div[contains(@id,'api.ui.Checkbox')]")
    private WebElement checkBox;


    public CheckBoxFormViewPanel( final TestSession session )
    {
        super( session );
    }

    @Override
    public FormViewPanel type( final PropertyTree data )
    {
        boolean checkboxValue = data.getBoolean( CHECKBOX_PROPERTY );
        setChecked( checkboxValue );
        sleep( 300 );
        return this;
    }

    public CheckBoxFormViewPanel pressKeyOnRow( WebElement element, Keys key )
    {

        Actions actions = new Actions( getDriver() );
        actions.moveToElement( element ).build().perform();
        sleep( 1000 );
        // actions.click( element );
        actions.sendKeys( key );
        actions.build().perform();
        sleep( 500 );

        return this;
    }

    private CheckBoxFormViewPanel setChecked( boolean checked )
    {
        JavascriptExecutor executor = (JavascriptExecutor) getSession().getDriver();
        String id = checkBox.getAttribute( "id" );
        String script = String.format( "window.api.dom.ElementRegistry.getElementById('%s')" + ".setChecked(%b)", id, checked );
        executor.executeScript( script );
        sleep( 1000 );
        TestUtils.saveScreenshot( getSession(), "checkbox" );
        return this;
    }

    public boolean isChecked()
    {
        return checkBox.getAttribute( "checked" ) != null;
    }
}
