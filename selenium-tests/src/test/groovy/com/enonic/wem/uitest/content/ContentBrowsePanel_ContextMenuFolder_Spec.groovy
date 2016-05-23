package com.enonic.wem.uitest.contentimport com.enonic.autotests.pages.contentmanager.browsepanel.ContentStatusimport com.enonic.autotests.pages.contentmanager.browsepanel.SortContentDialogimport com.enonic.autotests.utils.TestUtilsimport com.enonic.autotests.vo.contentmanager.Contentimport spock.lang.Sharedimport spock.lang.Stepwise@Stepwiseclass ContentBrowsePanel_ContextMenuFolder_Spec    extends BaseContentSpec{    @Shared    String NEW_DISPLAY_NAME = "context-menu-edit-test";    @Shared    Content FOLDER_ONLINE;    def "GIVEN a folder WHEN context menu opened  THEN all items have correct state"()    {        when: "context menu opened"        findAndSelectContent( IMPORTED_FOLDER_NAME );        contentBrowsePanel.openContextMenu( IMPORTED_FOLDER_NAME );        TestUtils.saveScreenshot( getSession(), "context-menu-folder" )        then: "Delete menu item is enabled"        contentBrowsePanel.isContextMenuItemEnabled( "Delete" );        and: "Preview menu item is disabled"        !contentBrowsePanel.isContextMenuItemEnabled( "Preview" );        and: "Edit menu item is enabled"        contentBrowsePanel.isContextMenuItemEnabled( "Edit" );        and: "New menu item is enabled"        contentBrowsePanel.isContextMenuItemEnabled( "New" );        and: "Sort menu item is enabled"        contentBrowsePanel.isContextMenuItemEnabled( "Sort" );        and: "New menu item is enabled"        contentBrowsePanel.isContextMenuItemEnabled( "Duplicate" );        and: "Move menu item is enabled"        contentBrowsePanel.isContextMenuItemEnabled( "Move" );        and: "Publish menu item is enabled"        contentBrowsePanel.isContextMenuItemEnabled( "Publish" );        and: "'unpublish'-item not present in the context menu, because content is 'offline'"        !contentBrowsePanel.isContextMenuItemEnabled( "Unpublish" );    }    def "GIVEN a content WHEN content selected and 'Delete' selected from context menu  THEN content not exist in view"()    {        setup: "build a new folder-content"        Content content = buildFolderContent( "folder", "context menu delete" );        and: "add new content: click on 'new' button, populate a wizard and close it"        addContent( content );        when: "right clicks on the selected folder and select 'Delete' from the context menu"        filterPanel.typeSearchText( content.getName() );        contentBrowsePanel.selectDeleteFromContextMenu( content.getName() ).doDelete();        then: "content deleted and not exist in view"        !contentBrowsePanel.exists( content.getName(), true );    }    def "GIVEN a content WHEN content selected and 'Publish' selected from context menu  THEN content with 'Online' status listed"()    {        setup: "build a new folder-content"        FOLDER_ONLINE = buildFolderContent( "folder", "context menu publish" );        and: "add new content: click on 'new' button, populate a wizard and close it"        addContent( FOLDER_ONLINE );        when: "right clicks on the selected folder and select 'Publish' from the context menu"        contentBrowsePanel.selectPublishFromContextMenu( FOLDER_ONLINE.getName() ).clickOnPublishNowButton().waitForDialogClosed();        filterPanel.typeSearchText( FOLDER_ONLINE.getName() );        TestUtils.saveScreenshot( getSession(), "context-menu-folder-published" )        then: "content deleted and not exist in view"        contentBrowsePanel.getContentStatus( FOLDER_ONLINE.getName() ).equalsIgnoreCase( ContentStatus.ONLINE.getValue() );    }    def "GIVEN a 'online' folder WHEN folder selected context menu opened  THEN 'unpublish' menu item is displayed and enabled"()    {        given: "a 'online' folder"        findAndSelectContent( FOLDER_ONLINE.getName() );        when: "right clicks on the selected folder and a context menu opened"        contentBrowsePanel.openContextMenu( FOLDER_ONLINE.getName() )        TestUtils.saveScreenshot( getSession(), "context-menu-online-folder-item-displayed" )        then: "'Unpublish' menu item is displayed"        contentBrowsePanel.isContextMenuItemDisplayed( "Unpublish" );        and: "'Publish' menu item is not displayed"        !contentBrowsePanel.isContextMenuItemDisplayed( "Publish" );    }    def "GIVEN a 'online' folder WHEN content selected and 'Unpublish' selected from context menu  THEN the content with 'Offline' status listed"()    {        given:        findAndSelectContent( FOLDER_ONLINE.getName() );        when: "right clicks on the selected folder and select 'Unublish' from the context menu"        contentBrowsePanel.selectUnPublishFromContextMenu( FOLDER_ONLINE.getName() ).clickOnUnpublishButton();        TestUtils.saveScreenshot( getSession(), "context-menu-folder-unpublished" )        then: "content deleted and not exist in view"        contentBrowsePanel.getContentStatus( FOLDER_ONLINE.getName() ).equalsIgnoreCase( ContentStatus.OFFLINE.getValue() );    }    def "GIVEN a content WHEN content selected and 'Duplicate' selected from context menu  THEN new content with 'copy' in name  listed"()    {        setup: "build a new folder-content"        Content folder = buildFolderContent( "folder", "context menu duplicate" );        and: "add new content: click on 'new' button, populate a wizard and close it"        addContent( folder );        when: "right clicks on the selected folder and select 'Publish' from the context menu"        contentBrowsePanel.selectDuplicateFromContextMenu( folder.getName() );        filterPanel.typeSearchText( folder.getName() + "-copy" );        TestUtils.saveScreenshot( getSession(), "context-menu-folder-duplicated" );        then: "content deleted and not exist in view"        contentBrowsePanel.exists( folder.getName() + "-copy" );    }    def "GIVEN a content WHEN content selected and 'Edit' selected from context menu  THEN content is listed with it's new name"()    {        setup: "builds a new folder-content"        Content content = buildFolderContent( "folder", "context menu edit" );        and: "add new content: click on 'new' button, populate a wizard and close it"        addContent( content );        when: "right clicks on the selected folder and select 'Edit' from the context menu, change the display name and save it"        findAndSelectContent( content.getName() );        contentBrowsePanel.selectEditFromContextMenu( content.getName() ).typeDisplayName( NEW_DISPLAY_NAME ).save().close(            NEW_DISPLAY_NAME );        then: "content is listed with it's new display name"        filterPanel.typeSearchText( NEW_DISPLAY_NAME );        TestUtils.saveScreenshot( getSession(), "context-menu-folder-edited" )        contentBrowsePanel.exists( content.getName(), true );    }    def "GIVEN a folder WHEN folder selected and 'Sort' selected from context menu  THEN 'sort'- modal dialog appears"()    {        when: "right clicks on the selected folder and select 'Sort' from the context menu"        filterPanel.typeSearchText( IMPORTED_FOLDER_NAME );        SortContentDialog modalDialog = contentBrowsePanel.selectSortInContextMenu( IMPORTED_FOLDER_NAME );        then: "'Sort' modal dialog appears"        modalDialog.isPresent();    }    def "GIVEN parent content added WHEN the parent selected and 'New' from a context menu selected AND data typed AND child content saved THEN new child should be listed beneath the parent"()    {        setup: "builds a new folder-content"        Content parent = buildFolderContent( "folder", "context menu new" );        and: "the parent content added"        addContent( parent );        and: "builds a new folder content, that will be child for the first content"        Content childContent = buildFolderContentWithParent( "child", "child folder", parent.getName() );        when: "parent content selected and 'New' from the context menu selected AND data typed AND wizard saved"        contentBrowsePanel.selectNewFromContextMenu( parent.getName() ).selectContentType( childContent.getContentTypeName() ).typeData(            childContent ).save().close( childContent.getDisplayName() );        sleep( 400 );        and: "parent content expanded"        filterPanel.typeSearchText( parent.getName() );        TestUtils.saveScreenshot( getSession(), "context-menu-folder-new" )        contentBrowsePanel.expandContent( parent.getPath() );        then: "new child content should be listed beneath the parent"        contentBrowsePanel.exists( childContent.getName(), true );    }}