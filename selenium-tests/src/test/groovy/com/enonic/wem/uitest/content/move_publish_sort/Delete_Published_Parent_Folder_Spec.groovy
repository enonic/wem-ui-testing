package com.enonic.wem.uitest.content.move_publish_sort

import com.enonic.autotests.pages.Application
import com.enonic.autotests.pages.contentmanager.ContentPublishDialog
import com.enonic.autotests.pages.contentmanager.ContentUnpublishDialog
import com.enonic.autotests.pages.contentmanager.browsepanel.ContentStatus
import com.enonic.autotests.vo.contentmanager.Content
import com.enonic.wem.uitest.content.BaseContentSpec
import spock.lang.Ignore
import spock.lang.Shared

/**
 * Created on 24.10.2016.
 *
 * XP-4314 Up-to-date selenium tests for notification messages
 * */
class Delete_Published_Parent_Folder_Spec
    extends BaseContentSpec
{
    @Shared
    Content PARENT_FOLDER;

    def "GIVEN existing root folder with a child WHEN parent content selected and 'Publish' button on toolbar pressed THEN notification message appears and content is getting 'Published'"()
    {
        given:
        PARENT_FOLDER = buildFolderContent( "publish", "parent folder" );
        addContent( PARENT_FOLDER );
        findAndSelectContent( PARENT_FOLDER.getName() );
        Content child = buildFolderContent( "child", "child folder" );
        addContent( child );

        when:
        findAndSelectContent( PARENT_FOLDER.getName() ).clickToolbarPublish().includeChildren( true ).clickOnPublishButton();
        String message = contentBrowsePanel.waitPublishNotificationMessage( Application.EXPLICIT_NORMAL );
        saveScreenshot( "parent_with_child_published" );

        then:
        contentBrowsePanel.getContentStatus( PARENT_FOLDER.getName() ).equalsIgnoreCase( ContentStatus.PUBLISHED.getValue() );

        and: "correct notification message should be displayed"
        message == String.format( Application.CONTENTS_PUBLISHED_NOTIFICATION_MESSAGE, "2" );
    }

    def "GIVEN existing 'Published'-parent folder with a child WHEN parent folder has been selected AND 'Unpublish' menu item has been clicked THEN expected notification message appears"()
    {
        given:
        findAndSelectContent( PARENT_FOLDER.getName() );

        when: "'Unpublish' menu item was selected"
        ContentUnpublishDialog modalDialog = contentBrowsePanel.showPublishMenu().selectUnPublishMenuItem();
        modalDialog.clickOnUnpublishButton();
        String message = contentBrowsePanel.waitForNotificationMessage();

        then: "correct notification message appears"
        message == String.format( Application.CONTENTS_UNPUBLISHED_NOTIFICATION_MESSAGE, "2" );
    }

    def "GIVEN existing 'Published'-folder with a child WHEN the folder has been selected AND 'Delete' button has been clicked THEN expected notification message is displayed'"()
    {
        given: "existing online-folder with a child"
        findAndSelectContent( PARENT_FOLDER.getName() ).clickToolbarPublish().includeChildren( true ).clickOnPublishButton();
        sleep( 1000 );

        when: "the folder selected AND 'Delete' button clicked"
        contentBrowsePanel.clickToolbarDelete().clickOnDeleteButtonAndConfirm( "2" );
        def expectedMessage = String.format( Application.CONTENTS_DELETED_AND_MARKED_FOR_DELETION_MESSAGE, "2", "2" );

        then: "'Deleted 2 items ( Marked for deletion: 2 ).' - message should appear"
        contentBrowsePanel.waitExpectedNotificationMessage( expectedMessage, Application.EXPLICIT_NORMAL );
    }

    def "GIVEN existing pending-folder with a child WHEN the folder has been selected AND 'Unpublish' menu item has been clicked THEN expected notification message is displayed"()
    {
        given: "existing online-folder with a child"
        ContentPublishDialog dialog = findAndSelectContent( PARENT_FOLDER.getName() ).clickToolbarPublish().includeChildren( true );
        sleep( 1000 );

        when: "the folder selected AND 'Unpublish' menu item has been clicked"
        dialog.clickOnPublishButton();
        def expectedMessage = String.format( Application.CONTENTS_PUBLISHED_AND_DELETED_MESSAGE, "2", "2" );

        then: "expected notification message should be displayed'"
        contentBrowsePanel.waitExpectedNotificationMessage( expectedMessage, Application.EXPLICIT_NORMAL );
    }
}
