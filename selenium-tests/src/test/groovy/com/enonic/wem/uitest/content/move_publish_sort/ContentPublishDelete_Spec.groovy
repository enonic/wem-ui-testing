package com.enonic.wem.uitest.content.move_publish_sort

import com.enonic.autotests.pages.Application
import com.enonic.autotests.pages.contentmanager.browsepanel.ContentStatus
import com.enonic.autotests.pages.contentmanager.browsepanel.DeleteContentDialog
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

    def "GIVEN existing content WHEN content was selected and 'Publish' button on toolbar was pressed THEN notification message should appear and content is getting 'Online'"()
    {
        given: "existing content"
        content = buildFolderContent( "publish", DISPLAY_NAME );
        addContent( content );

        when: "content is published"
        filterPanel.typeSearchText( content.getName() )
        contentBrowsePanel.selectContentInTable( content.getName() ).clickToolbarPublish().clickOnPublishNowButton();
        String message = contentBrowsePanel.waitPublishNotificationMessage( Application.EXPLICIT_NORMAL );

        then: "Online status should be displayed in the grid"
        filterPanel.typeSearchText( content.getName() )
        contentBrowsePanel.getContentStatus( content.getName() ).equalsIgnoreCase( ContentStatus.ONLINE.getValue() );
        message == String.format( Application.ONE_CONTENT_PUBLISHED_NOTIFICATION_MESSAGE, DISPLAY_NAME );
    }

    def "GIVEN existing content with 'Online' status WHEN content was selected and 'Delete' button pressed THEN content is getting 'Pending delete'"()
    {
        given: "existing content with 'Online' status"
        filterPanel.typeSearchText( content.getName() )

        when: "content was selected and 'Delete' button pressed"
        contentBrowsePanel.selectContentInTable( content.getName() ).clickToolbarDelete().doDelete();
        String message = contentBrowsePanel.waitNotificationMessage();

        then: "content is getting 'Pending delete'"
        contentBrowsePanel.getContentStatus( content.getName() ).equalsIgnoreCase( ContentStatus.PENDING_DELETE.getValue() );
        and: "correct notification message should be shown"
        message == Application.ONE_CONTENT_MARKED_FOR_DELETION_MESSAGE;
    }

    def "GIVEN existing content with 'Pending Delete' status WHEN content is selected and 'Delete' button pressed THEN checkbox with label 'Instantly delete published items' should be checked"()
    {
        when: "existing content with 'Pending Delete' status"
        filterPanel.typeSearchText( content.getName() )
        DeleteContentDialog dialog = contentBrowsePanel.selectContentInTable( content.getName() ).clickToolbarDelete();
        saveScreenshot( "test_delete_dialog_checkbox" );

        then: "checkbox with label 'Instantly delete published items' should be checked"
        dialog.isInstantlyDeleteCheckboxChecked();
    }

    def "GIVEN existing content with 'Pending Delete' status WHEN content is selected and 'Publish' button pressed THEN content should not be listed in the browse panel"()
    {
        when: "existing content with 'Pending Delete' status"
        filterPanel.typeSearchText( content.getName() );
        and: "content is selected and 'Publish' button pressed"
        contentBrowsePanel.selectContentInTable( content.getName() ).clickToolbarPublish().clickOnPublishNowButton();
        String message = contentBrowsePanel.waitPublishNotificationMessage( Application.EXPLICIT_NORMAL );

        then: "content should not be listed in the browse panel"
        !contentBrowsePanel.exists( content.getName() );

        and: "correct notification message should be displayed"
        message == Application.ONE_PENDING_ITEM_IS_DELETED;
    }
}
