package com.enonic.wem.uitest.content

import com.enonic.autotests.pages.contentmanager.browsepanel.ContentStatus
import com.enonic.autotests.pages.contentmanager.wizardpanel.ContentWizardPanel
import com.enonic.autotests.utils.NameHelper
import com.enonic.autotests.utils.TestUtils
import com.enonic.autotests.vo.contentmanager.Content
import com.enonic.xp.schema.content.ContentTypeName

class ContentWizardPanel_Toolbar_Spec
    extends BaseContentSpec
{
    def "WHEN content wizard opened THEN all buttons on toolbar have correct state"()
    {
        when: "content wizard opened"
        ContentWizardPanel wizardPanel = contentBrowsePanel.clickToolbarNew().selectContentType( ContentTypeName.folder() );

        then: "'Delete' button enabled"
        wizardPanel.isDeleteButtonEnabled();

        and: "'Save draft' button enabled"
        wizardPanel.isSaveButtonEnabled();

        and: "'Publish' button disabled"
        !wizardPanel.isPublishButtonEnabled();

        and: "'Duplicate' button is enabled"
        wizardPanel.isDuplicateButtonEnabled();

        and: "content status is offline"
        wizardPanel.getStatus() == ContentStatus.OFFLINE.getValue();
    }

    def "GIVEN content wizard opened WHEN data typed but not saved yet THEN all buttons on toolbar have correct state"()
    {
        given: "content wizard opened"
        ContentWizardPanel wizardPanel = contentBrowsePanel.clickToolbarNew().selectContentType( ContentTypeName.folder() );

        when: "display name typed"
        wizardPanel.typeDisplayName( NameHelper.uniqueName( "toolbar" ) );

        then: "'Delete' button enabled"
        wizardPanel.isDeleteButtonEnabled();

        and: "'Save draft' button enabled"
        wizardPanel.isSaveButtonEnabled();

        and: "'Publish' button disabled"
        wizardPanel.isPublishButtonEnabled();

        and: "'Duplicate' button is enabled"
        wizardPanel.isDuplicateButtonEnabled();

        and: "content status is offline"
        wizardPanel.getStatus() == ContentStatus.OFFLINE.getValue();
    }

    def "GIVEN content wizard opened WHEN data typed and content saved THEN all buttons on toolbar have correct state"()
    {
        given: "content wizard opened"
        ContentWizardPanel wizardPanel = contentBrowsePanel.clickToolbarNew().selectContentType( ContentTypeName.folder() );

        when: "display name typed"
        wizardPanel.typeDisplayName( NameHelper.uniqueName( "toolbar" ) ).save();

        then: "'Delete' button enabled"
        wizardPanel.isDeleteButtonEnabled();

        and: "'Save draft' button enabled"
        wizardPanel.isSaveButtonEnabled();

        and: "'Publish' button enabled"
        wizardPanel.isPublishButtonEnabled();

        and: "'Duplicate' button enabled"
        wizardPanel.isDuplicateButtonEnabled();

        and: "content status is offline"
        wizardPanel.getStatus() == ContentStatus.OFFLINE.getValue();
    }

    def "GIVEN content wizard opened WHEN content saved AND published THEN 'online' status appears in the wizard AND publish-button is disabled now"()
    {
        given: "content wizard opened"
        ContentWizardPanel wizardPanel = contentBrowsePanel.clickToolbarNew().selectContentType( ContentTypeName.folder() );
        wizardPanel.typeDisplayName( NameHelper.uniqueName( "toolbar" ) ).save();

        when: "content has been published"
        wizardPanel.clickOnWizardPublishButton().clickOnPublishNowButton();
        TestUtils.saveScreenshot( getSession(), "wizard_published" );

        then: "'Delete' button enabled"
        wizardPanel.isDeleteButtonEnabled();

        and: "'Save draft' button enabled"
        wizardPanel.isSaveButtonEnabled();

        and: "'Publish' button disabled"
        !wizardPanel.isPublishButtonEnabled();

        and: "content status is online"
        wizardPanel.getStatus() == ContentStatus.ONLINE.getValue();
    }

    def "GIVEN existing content opened WHEN delete button pressed and deleting confirmed THEN wizard closed and content not present in a grid"()
    {
        given: "existing content opened"
        Content folderContent = buildFolderContent( "folder", "wizard_toolbar" );
        addContent( folderContent );

        when: "delete button pressed and deleting confirmed"
        filterPanel.typeSearchText( folderContent.getName() );
        ContentWizardPanel wizard = contentBrowsePanel.selectContentInTable( folderContent.getName() ).clickToolbarEdit();
        wizard.clickToolbarDelete().doDelete();

        then: "content not present in a grid"
        filterPanel.typeSearchText( folderContent.getName() );
        !contentBrowsePanel.exists( folderContent.getName() )
    }
}
