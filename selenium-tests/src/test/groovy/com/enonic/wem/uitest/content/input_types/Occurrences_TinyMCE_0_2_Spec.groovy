package com.enonic.wem.uitest.content.input_types

import com.enonic.autotests.pages.contentmanager.wizardpanel.ContentWizardPanel
import com.enonic.autotests.pages.form.BaseTinyMCEFormViewPanel
import com.enonic.autotests.pages.form.TinyMCE0_0_FormViewPanel
import com.enonic.autotests.utils.NameHelper
import com.enonic.autotests.vo.contentmanager.Content
import com.enonic.xp.content.ContentPath
import com.enonic.xp.data.PropertyTree
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class Occurrences_TinyMCE_0_2_Spec
    extends Base_InputFields_Occurrences
{
    @Shared
    String TEST_TEXT1 = "tinyMce text1";

    @Shared
    String TEST_TEXT2 = "tinyMce text2";

    @Shared
    String EXPECTED_TEXT1 = "<p>" + TEST_TEXT1 + "</p>";

    @Shared
    String EXPECTED_TEXT2 = "<p>" + TEST_TEXT2 + "</p>";

    def "WHEN wizard for adding a content with TinyMCE(0:2) opened THEN text area is present "()
    {
        when: "start to add a content with type 'TinyMCE 0:2'"
        Content tinyMceContent = buildTinyMce0_2_Content( 1, TEST_TEXT1 );
        selectSiteOpenWizard( tinyMceContent.getContentTypeName() );
        TinyMCE0_0_FormViewPanel formViewPanel = new TinyMCE0_0_FormViewPanel( getSession() );

        then: "wizard with form view opened"
        formViewPanel.isOpened();

        and: "text area present"
        formViewPanel.getNumberOfMCE() == 1;

        and: "'Add' button is present"
        formViewPanel.isAddButtonPresent();
    }

    def "GIVEN wizard for adding a content with TinyMCE(0:2) opened WHEN button 'Add' clicked THEN two text area is present and button 'Add' disappears "()
    {
        given: "start to add a content with type 'TinyMCE 0:2'"
        Content tinyMceContent = buildTinyMce0_2_Content( 2, TEST_TEXT1 );
        ContentWizardPanel wizard = selectSiteOpenWizard( tinyMceContent.getContentTypeName() );
        TinyMCE0_0_FormViewPanel formViewPanel = new TinyMCE0_0_FormViewPanel( getSession() );
        wizard.typeName( tinyMceContent.getName() )

        when: "clock on 'Add' button"
        formViewPanel.clickOnAddButton();

        then: "wait until the button 'add' disappears"
        formViewPanel.waitUntilAddButtonNotVisible()

        and: "button 'Add' not present"
        !formViewPanel.isAddButtonPresent();

        and: "two text area present"
        formViewPanel.getNumberOfMCE() == 2;
    }

    def "GIVEN wizard with two text area present WHEN one text area removed THEN one area present and button 'Add' appears "()
    {
        given: "start to add a content with type 'TinyMCE 0:2'"
        Content tinyMceContent = buildTinyMce0_2_Content( 2, TEST_TEXT1 );

        ContentWizardPanel wizard = selectSiteOpenWizard( tinyMceContent.getContentTypeName() );
        TinyMCE0_0_FormViewPanel formViewPanel = new TinyMCE0_0_FormViewPanel( getSession() );
        wizard.typeName( tinyMceContent.getName() );
        formViewPanel.clickOnAddButton();
        int beforeRemoving = formViewPanel.getNumberOfMCE();

        when: "remove last text area"
        formViewPanel.removeLastTextArea();

        then: "button 'Add' present on page"
        formViewPanel.isAddButtonPresent();

        and: "one text area present"
        formViewPanel.getNumberOfMCE() == 1;

        and:
        beforeRemoving == 2;
    }


    def "GIVEN wizard opened and two strings typed into two text areas and content saved WHEN content opened for edit THEN both strings saved correctly"()
    {
        given: "add a content with type 'TinyMCE 0:2'"
        Content tinyMceContent = buildTinyMce0_2_Content( 2, TEST_TEXT1, TEST_TEXT2 );
        ContentWizardPanel wizard = selectSiteOpenWizard( tinyMceContent.getContentTypeName() );
        wizard.typeData( tinyMceContent ).save().close( tinyMceContent.getDisplayName() );

        when: "just created content opened"
        contentBrowsePanel.selectAndOpenContentFromToolbarMenu( tinyMceContent );
        TinyMCE0_0_FormViewPanel formViewPanel = new TinyMCE0_0_FormViewPanel( getSession() );
        List<String> strings = formViewPanel.getTextFromAreas();

        then: "correct values of strings are showed in both text areas"
        strings.contains( EXPECTED_TEXT1 );
        and:
        strings.contains( EXPECTED_TEXT2 );
    }

    private Content buildTinyMce0_2_Content( long numberOfEditors, String... text )
    {
        PropertyTree data = new PropertyTree();
        if ( text != null || !text.length == 0 )
        {
            data.addLong( BaseTinyMCEFormViewPanel.NUMBER_OF_EDITORS, numberOfEditors );
            data.addStrings( BaseTinyMCEFormViewPanel.STRINGS_PROPERTY, text );
        }

        Content tinyMceContent = Content.builder().
            name( NameHelper.uniqueName( "mce0_2_" ) ).
            displayName( "mce0_2 content" ).
            parent( ContentPath.from( SITE_NAME ) ).
            contentType( ALL_CONTENT_TYPES_APP_NAME + ":tiny_mce0_2" ).data( data ).
            build();
        return tinyMceContent;
    }
}
