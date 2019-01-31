package com.enonic.wem.uitest.content.move_publish_sort

import com.enonic.autotests.pages.Application
import com.enonic.autotests.pages.contentmanager.browsepanel.ContentStatus
import com.enonic.autotests.vo.contentmanager.Content
import com.enonic.wem.uitest.content.BaseContentSpec
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class ContentPublishDelete_Spec
    extends BaseContentSpec

{
    @Shared
    String DISPLAY_NAME = "publishDisplayName";

    @Shared
    Content content;

    def "GIVEN new folder is added WHEN the folder has been selected and 'Publish' button on toolbar pressed THEN notification message should appear and content is getting 'Published'"()
    {
        given: "new folder is added"
        content = buildFolderContent( "publish", DISPLAY_NAME );
        addContent( content );

        when: "the folder has been published"
        filterPanel.typeSearchText( content.getName() )
        contentBrowsePanel.selectContentInTable( content.getName() ).clickToolbarPublish().clickOnPublishNowButton();
        String message = contentBrowsePanel.waitPublishNotificationMessage( Application.EXPLICIT_NORMAL );

        then: "'Published' status should be displayed in the grid"
        filterPanel.typeSearchText( content.getName() )
        contentBrowsePanel.getContentStatus( content.getName() ).equalsIgnoreCase( ContentStatus.PUBLISHED.getValue() );
        and: "correct notification message should be displayed"
        message == String.format( Application.ONE_CONTENT_PUBLISHED_NOTIFICATION_MESSAGE_TMP, content.getName() );
    }

    def "GIVEN existing content with 'Published' status WHEN the content has been selected and 'Delete' button pressed THEN content is getting 'Deleted'"()
    {
        given: "existing content with 'Published' status"
        filterPanel.typeSearchText( content.getName() )

        when: "content was selected and 'Delete' button pressed"
        contentBrowsePanel.selectContentInTable( content.getName() ).clickToolbarDelete().doDelete();
        String message = contentBrowsePanel.waitForNotificationMessage();
        saveScreenshot("content_should_be_pending");

        then: "content is getting 'Deleted'"
        contentBrowsePanel.getContentStatus( content.getName() ).equalsIgnoreCase( ContentStatus.DELETED.getValue() );
        and: "expected notification message should be displayed"
        message == String.format( Application.ONE_CONTENT_MARKED_FOR_DELETION_MESSAGE, content.getName() );
    }

    def "GIVEN existing content with 'Deleted' status WHEN content is selected and 'Delete' button pressed THEN 'Undo deleted' button should be displayed"()
    {
        when: "existing content with 'Deleted' status"
        filterPanel.typeSearchText( content.getName() )
        contentBrowsePanel.selectContentInTable( content.getName() );
        saveScreenshot( "test_deleted_content" );

        then: "'Undo deleted' button should be displayed"
        contentBrowsePanel.isUndoDeleteButtonDisplayed();
    }

    def "GIVEN existing content with 'Deleted' status WHEN content is selected and 'Publish' button pressed THEN content should not be listed in the browse panel"()
    {
        when: "existing content with 'Deleted' status"
        filterPanel.typeSearchText( content.getName() );
        and: "content is selected and 'Publish' button pressed"
        contentBrowsePanel.selectContentInTable( content.getName() ).clickToolbarPublish().clickOnPublishNowButton();
        String message = contentBrowsePanel.waitPublishNotificationMessage( Application.EXPLICIT_NORMAL );

        then: "content should not be listed in the browse panel"
        !contentBrowsePanel.exists( content.getName() );

        and: "correct notification message should be displayed"
        message == String.format( Application.ONE_PENDING_ITEM_IS_DELETED, content.getName() );
    }
}
