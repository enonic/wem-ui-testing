package com.enonic.autotests.pages.form;


import org.openqa.selenium.By;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.exceptions.TestFrameworkException;
import com.enonic.autotests.pages.Application;
import com.enonic.xp.data.PropertyTree;

import static com.enonic.autotests.utils.SleepHelper.sleep;

public abstract class FormViewPanel
    extends Application
{
    protected static final String FORM_VIEW = "//div[contains(@id,'api.form.FormView')]";

    public static String VALIDATION_MESSAGE_1_1 = "This field is required";

    public static String VALIDATION_MESSAGE = "Min %s occurrences required";

    protected String VALIDATION_VIEWER = FORM_VIEW + "//div[contains(@id, 'ValidationRecordingViewer')]";

    protected static String VALUE_INPUT =
        "//div[contains(@id,'api.form.InputView') and descendant::div[@title='%s']]//input[contains(@id,'TextInput')]";

    protected final String ADD_BUTTON_XPATH = FORM_VIEW + "//div[@class='bottom-button-row']//button[child::span[text()='Add']]";

    public FormViewPanel( final TestSession session )
    {
        super( session );
    }

    public abstract FormViewPanel type( final PropertyTree data );

    public boolean isValidationMessagePresent()
    {
        return waitUntilVisibleNoException( By.xpath( VALIDATION_VIEWER ), Application.EXPLICIT_NORMAL );

    }

    public boolean isAddButtonPresent()
    {
        return isElementDisplayed( ADD_BUTTON_XPATH );
    }

    public void clickOnAddButton()
    {
        if ( findElements( By.xpath( ADD_BUTTON_XPATH ) ).size() == 0 )
        {
            throw new TestFrameworkException( "Add button not present in Form View Panel!" );
        }
        findElements( By.xpath( ADD_BUTTON_XPATH ) ).get( 0 ).click();
        sleep( 500 );
    }

    public String getValidationMessage()
    {
        if ( isValidationMessagePresent() )
        {
            return findElements( By.xpath( VALIDATION_VIEWER + "//li" ) ).get( 0 ).getText();
        }
        else
        {
            throw new TestFrameworkException( "validation message was not found!" );
        }
    }
}
