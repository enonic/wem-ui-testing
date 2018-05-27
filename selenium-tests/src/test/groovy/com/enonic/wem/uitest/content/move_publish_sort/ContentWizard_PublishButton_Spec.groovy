package com.enonic.wem.uitest.content.move_publish_sort

import com.enonic.autotests.pages.Application
import com.enonic.autotests.pages.contentmanager.ContentUnpublishDialog
import com.enonic.autotests.pages.contentmanager.browsepanel.ContentStatus
import com.enonic.autotests.pages.contentmanager.wizardpanel.ContentWizardPanel
import com.enonic.autotests.vo.contentmanager.Content
import com.enonic.wem.uitest.content.BaseContentSpec
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class ContentWizard_PublishButton_Spec
    extends BaseContentSpec
{
    @Shared
    Content CONTENT;

    def "GIVEN existing 'New' content WHEN the content opened AND has been 'published' THEN 'online' status for this content is displayed on the wizard"()
    {
        given: "new folder is added"
        CONTENT = buildFolderContent( "folder", "unpublish of pending delete content" );
        addContent( CONTENT );

        when: "the content has been 'published'"
        ContentWizardPanel wizard = findAndSelectContent( CONTENT.getName() ).clickToolbarEdit();
        wizard.clickOnWizardPublishButton().clickOnPublishNowButton();
        wizard.waitInvisibilityOfSpinner( Application.EXPLICIT_NORMAL );

        then: "folder should be 'Published' now"
        wizard.getStatus() == ContentStatus.PUBLISHED.getValue();

        and: "'publish' button should be disabled"
        !wizard.isPublishButtonEnabled();

        and: "'publish-menu' becomes available"
        wizard.isPublishMenuAvailable();
    }

    def "GIVEN existing 'online' content WHEN the content opened AND display name was changed THEN 'modified' status for this content is displayed on the wizard"()
    {
        given: "existing 'online' content"
        findAndSelectContent( CONTENT.getName() );

        when: "display name has been changed"
        ContentWizardPanel wizard = findAndSelectContent( CONTENT.getName() ).clickToolbarEdit();
        wizard.typeDisplayName( "new display name" ).save();
        sleep( 700 );

        then: "folder should be 'modified' now"
        wizard.getStatus() == ContentStatus.MODIFIED.getValue();

        and: "'Publish' menu becomes enabled"
        wizard.isPublishButtonEnabled();

        and: "'publish-menu' should be available"
        wizard.isPublishMenuAvailable();
    }

    def "GIVEN existing 'modified' content WHEN the content opened AND 'unpublish' button was pressed THEN 'offline' status for this content should be displayed on the wizard"()
    {
        given: "existing 'modified' content"
        findAndSelectContent( CONTENT.getName() );
        ContentWizardPanel wizard = contentBrowsePanel.clickToolbarEdit();

        when: "'unpublish' button was pressed"
        ContentUnpublishDialog modalDialog = wizard.showPublishMenu().selectUnPublishMenuItem();
        modalDialog.waitUntilDialogShown( Application.EXPLICIT_NORMAL );
        modalDialog.clickOnUnpublishButton();
        sleep( 1000 );

        then: "folder should be 'Unpublished' now"
        wizard.getStatus() == ContentStatus.UNPUBLISHED.getValue();

        and: "'Publish' menu becomes enabled"
        wizard.isPublishButtonEnabled();

        and: "'publish-menu' should be enabled, because 'Create Issue' should be available"
        wizard.isPublishMenuAvailable();
    }

    def "GIVEN existing 'online' content WHEN 'Delete' button on the wizard-toolbar pressed AND content deleted THEN 'Deleted' status should be displayed on the wizard"()
    {
        given: "existing 'online' content"
        findAndSelectContent( CONTENT.getName() );
        ContentWizardPanel wizard = contentBrowsePanel.clickToolbarEdit();
        wizard.clickOnWizardPublishButton().clickOnPublishNowButton();

        when: "'Delete' button on the wizard-toolbar pressed AND content deleted"
        wizard.clickToolbarDelete().doDelete();

        then: "'Deleted' status should be displayed on the wizard"
        wizard.getStatus() == ContentStatus.DELETED.getValue();

        and: "'Publish' button should be enabled"
        saveScreenshot("wizard_online_content_was_deleted");
        wizard.isPublishButtonEnabled();

        and: "'publish-menu' should be enabled"
        wizard.isPublishMenuAvailable();
    }
}
