package com.enonic.wem.uitest.content.input_types

import com.enonic.autotests.pages.Application
import com.enonic.autotests.pages.contentmanager.browsepanel.ContentStatus
import com.enonic.autotests.pages.contentmanager.wizardpanel.ContentWizardPanel
import com.enonic.autotests.pages.form.ComboBoxFormViewPanel
import com.enonic.autotests.vo.contentmanager.Content
import spock.lang.Shared

class Occurrences_ComboBox_2_4_Spec
    extends Base_InputFields_Occurrences
{

    @Shared
    Content content_with_opt;

    def "GIVEN  wizard for ComboBox-content(2:4) opened WHEN name typed but no options are selected THEN content is invalid and publish button is disabled"()
    {
        given: "start to add a content with type 'ComboBox 2:4'"
        Content comboBoxContent = buildComboBox2_4_Content( 0 );
        ContentWizardPanel wizard = selectSitePressNew( comboBoxContent.getContentTypeName() );
        ComboBoxFormViewPanel formViewPanel = new ComboBoxFormViewPanel( getSession() );

        when: "only the name typed and no option were selected"
        wizard.typeDisplayName( comboBoxContent.getDisplayName() );

        then: "option filter input is present and enabled"
        formViewPanel.isOptionFilterInputEnabled();

        and: " and no options selected on the page"
        formViewPanel.getSelectedOptionValues().size() == 0;

        and: "content should be invalid, because required field- combobox2:4 not selected"
        !wizard.isPublishButtonEnabled();
    }

    def "GIVEN existing ComboBox-content (2:4) without selected options WHEN content opened for edit THEN no one selected options present on page "()
    {
        given: "new content with type ComboBox2_4 added'"
        Content comboBoxContent = buildComboBox2_4_Content( 0 );
        selectSitePressNew( comboBoxContent.getContentTypeName() ).typeData(
            comboBoxContent ).save().closeBrowserTab().switchToBrowsePanelTab();

        when: "content opened for edit"
        contentBrowsePanel.selectAndOpenContentFromToolbarMenu( comboBoxContent );
        ComboBoxFormViewPanel formViewPanel = new ComboBoxFormViewPanel( getSession() );
        List<String> optValues = formViewPanel.getSelectedOptionValues();

        then: "no options selected on the page"
        optValues.size() == 0;

        and: "options filter input is enabled"
        formViewPanel.isOptionFilterInputEnabled();

        and: "content should be invalid, because required field- combobox2:4 not selected"
        formViewPanel.isValidationMessagePresent();
    }

    def "GIVEN saving of ComboBox-content (2:4) with two option WHEN content opened for edit THEN two selected options present on page and options filter input is enabled"()
    {
        given: "new content with type ComboBox2_4 added'"
        content_with_opt = buildComboBox2_4_Content( 2 );
        selectSitePressNew( content_with_opt.getContentTypeName() ).typeData(
            content_with_opt ).save().closeBrowserTab().switchToBrowsePanelTab();

        when: "content opened for edit"
        contentBrowsePanel.selectAndOpenContentFromToolbarMenu( content_with_opt );
        ComboBoxFormViewPanel formViewPanel = new ComboBoxFormViewPanel( getSession() );
        List<String> optValues = formViewPanel.getSelectedOptionValues();

        then: "two option value  present in form view"
        optValues.size() == 2;

        and: "options with correct text present"
        String[] options = ["option A", "option B"];
        optValues.containsAll( options.toList() );

        and: "options filter input is enabled, because 2 less than 4"
        formViewPanel.isOptionFilterInputEnabled();
    }

    def "GIVEN ComboBox-content (2:4) with two selected options and one option removed and content saved WHEN content opened for edit THEN one option selected on the page "()
    {
        given: "content with one required option opened for edit' and one option removed"
        ContentWizardPanel wizard = contentBrowsePanel.selectAndOpenContentFromToolbarMenu( content_with_opt );
        ComboBoxFormViewPanel formViewPanel = new ComboBoxFormViewPanel( getSession() );
        formViewPanel.clickOnLastRemoveButton();
        wizard.save().closeBrowserTab().switchToBrowsePanelTab();

        when: "when content selected in the grid and opened for edit again"
        contentBrowsePanel.selectAndOpenContentFromToolbarMenu( content_with_opt );

        then: "one option selected in the form "
        saveScreenshot( "combobox_remove_option" )
        List<String> optValues = formViewPanel.getSelectedOptionValues();
        optValues.size() == 1;

        and: "content is invalid, because required fields- combobox2:4 not selected"
        formViewPanel.isValidationMessagePresent();
    }

    def "GIVEN saving of ComboBox-content (2:4) with four options WHEN content opened for edit THEN four selected options present on page and 'filter input' is disabled"()
    {
        given: "new content with type ComboBox2_4 added'"
        Content comboBoxContent = buildComboBox2_4_Content( 4 );
        selectSitePressNew( comboBoxContent.getContentTypeName() ).typeData(
            comboBoxContent ).save().closeBrowserTab().switchToBrowsePanelTab();

        when: "content opened for edit"
        contentBrowsePanel.selectAndOpenContentFromToolbarMenu( comboBoxContent );
        ComboBoxFormViewPanel formViewPanel = new ComboBoxFormViewPanel( getSession() );
        List<String> optValues = formViewPanel.getSelectedOptionValues();

        then: "four options present in form view"
        optValues.size() == 4;

        and: "and options have a correct text"
        String[] options = ["option A", "option B", "option C", "option D"];
        optValues.containsAll( options.toList() );

        and: "options filter input is disabled, because this content have a maximum options"
        !formViewPanel.isOptionFilterInputEnabled();
    }

    def "WHEN content with 2 selected option saved and published THEN it content with 'Online'-status listed"()
    {
        when: "content without a selected option saved and published"
        Content comboBoxContent = buildComboBox2_4_Content( 2 );
        ContentWizardPanel wizard = selectSitePressNew( comboBoxContent.getContentTypeName() ).typeData( comboBoxContent ).save();
        wizard.clickOnWizardPublishButton().clickOnPublishNowButton();
        String publishedMessage = contentBrowsePanel.waitPublishNotificationMessage( Application.EXPLICIT_NORMAL );

        and: "wizard closed"
        wizard.closeBrowserTab().switchToBrowsePanelTab();
        filterPanel.typeSearchText( comboBoxContent.getName() );

        then: "content has a 'online' status"
        contentBrowsePanel.getContentStatus( comboBoxContent.getName() ).equalsIgnoreCase( ContentStatus.ONLINE.getValue() );
        and:
        publishedMessage == String.format( Application.ONE_CONTENT_PUBLISHED_NOTIFICATION_MESSAGE, comboBoxContent.getDisplayName() );
    }
}
