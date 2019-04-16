package com.enonic.wem.uitest.content.move_publish_sort

import com.enonic.autotests.pages.Application
import com.enonic.autotests.pages.contentmanager.browsepanel.ContentStatus
import com.enonic.autotests.pages.contentmanager.wizardpanel.ContentWizardPanel
import com.enonic.autotests.vo.contentmanager.Content
import com.enonic.wem.uitest.content.BaseContentSpec
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * Created  on 3/17/2017.
 *
 * */
@Stepwise
class Unpublish_Deleted_Content_Spec
    extends BaseContentSpec
{
    @Shared
    Content CONTENT;

    def "Preconditions: WHEN existing folder has been published THEN 'Published' status should be displayed"()
    {
        given: "existing folder"
        CONTENT = buildFolderContent( "folder", "unpublish of deleted" )
        addContent( CONTENT );

        when: "the folder has been published"
        findAndSelectContent( CONTENT.getName() ).clickToolbarPublish().clickOnPublishNowButton();

        then: "'Online' status should be displayed"
        contentBrowsePanel.getContentStatus( CONTENT.getName() ) == ContentStatus.PUBLISHED.getValue();
    }

    def "GIVEN existing 'Published' folder is opened AND has been deleted in the wizard WHEN the folder has been 'unpublished' in the wizard THEN wizard closes AND the content should not be listed in the grid"()
    {
        given: "existing 'deleted' content is opened"
        findAndSelectContent( CONTENT.getName() );
        ContentWizardPanel wizard = contentBrowsePanel.clickToolbarEdit();
        wizard.clickToolbarDelete().doDelete();

        when: "'Unpublish' menu item has been clicked"
        wizard.showPublishMenu().selectUnPublishMenuItem().clickOnUnpublishButton();
        wizard.switchToBrowsePanelTab();
        sleep( 400 );
        saveScreenshot( "test_wizard_unpublish_of_deleted" );

        then: "the content should not be listed in the grid"
        !contentBrowsePanel.exists( CONTENT.getName() );

        //TODO https://github.com/enonic/app-contentstudio/issues/385
        //and:"expected notification should appear"
        //contentBrowsePanel.waitForNotificationMessage(  )== String.format(Application.ITEM_IS_DELETED ,CONTENT.getName(  ));
    }
}
