package com.enonic.wem.uitest.contentimport com.enonic.autotests.pages.contentmanager.ContentMenuItemimport com.enonic.autotests.pages.contentmanager.browsepanel.ContentStatusimport com.enonic.autotests.pages.contentmanager.browsepanel.SortContentDialogimport com.enonic.autotests.vo.contentmanager.Contentimport spock.lang.Sharedimport spock.lang.Stepwise/** * Tasks: * enonic/xp-ui-testing#24 Add selenium tests for 'Undo delete' menu item*/@Stepwiseclass ContentBrowsePanel_ContextMenuFolder_Spec    extends BaseContentSpec{    @Shared    String NEW_DISPLAY_NAME = "context-menu-edit-test";    @Shared    Content TEST_FOLDER;    def "WHEN existing 'offline' folder has been selected AND context menu opened THEN all menu-items should have expected state"()    {        when: "context menu opened"        findAndSelectContent( IMPORTED_FOLDER_NAME );        contentBrowsePanel.openContextMenu( IMPORTED_FOLDER_NAME );        saveScreenshot( "context-menu-folder" )        then: "Delete menu item should be enabled"        contentBrowsePanel.isContextMenuItemEnabled( ContentMenuItem.DELETE.getName() );        and: "Preview menu item should be disabled"        !contentBrowsePanel.isContextMenuItemEnabled( ContentMenuItem.PREVIEW.getName() );        and: "Edit menu item should be enabled"        contentBrowsePanel.isContextMenuItemEnabled( ContentMenuItem.EDIT.getName() );        and: "New menu item should be enabled"        contentBrowsePanel.isContextMenuItemEnabled( ContentMenuItem.NEW.getName() );        and: "Sort menu item should be enabled"        contentBrowsePanel.isContextMenuItemEnabled( ContentMenuItem.SORT.getName() );        and: "New menu item should be enabled"        contentBrowsePanel.isContextMenuItemEnabled( ContentMenuItem.DUPLICATE.getName() );        and: "Move menu item should be enabled"        contentBrowsePanel.isContextMenuItemEnabled( ContentMenuItem.MOVE.getName() );        and: "Publish menu item should be enabled"        contentBrowsePanel.isContextMenuItemEnabled( ContentMenuItem.PUBLISH.getName() );        and: "'unpublish'-item not present in the context menu, because content is 'offline'"        !contentBrowsePanel.isContextMenuItemDisplayed( "Unpublish..." );    }    def "WHEN right clicks on existing folder and select 'Delete' in the context menu AND deleting confirmed THEN the folder should be deleted"()    {        setup: "build a new folder-content"        Content content = buildFolderContent( "folder", "context menu delete" );        and: "new folder has been added"        addContent( content );        when: "right clicks on the selected folder and select 'Delete' in the context menu"        filterPanel.typeSearchText( content.getName() );        contentBrowsePanel.selectDeleteFromContextMenu( content.getName() ).doDelete();        then: "the folder should not be present in the grid"        !contentBrowsePanel.exists( content.getName(), true );    }    def "WHEN right clicks on existing folder and select 'Publish' in the context menu THEN the folder should be published"()    {        setup: "build a new folder-content"        TEST_FOLDER = buildFolderContent( "folder", "context menu publish" );        and: "new folder has been added"        addContent( TEST_FOLDER );        when: "right clicks on the selected folder and select 'Publish' from the context menu"        contentBrowsePanel.selectPublishFromContextMenu( TEST_FOLDER.getName() ).clickOnPublishNowButton().waitForDialogClosed();        filterPanel.typeSearchText( TEST_FOLDER.getName() );        saveScreenshot( "context-menu-folder-published" );        then: "the folder should be with 'Online' status"        contentBrowsePanel.getContentStatus( TEST_FOLDER.getName() ).equalsIgnoreCase( ContentStatus.PUBLISHED.getValue() );    }    def "GIVEN existing 'online' folder WHEN folder is selected and context menu opened THEN 'unpublish' menu item should be displayed and enabled"()    {        given: "existing 'online' folder"        findAndSelectContent( TEST_FOLDER.getName() );        when: "right clicks on the selected folder and a context menu opened"        contentBrowsePanel.openContextMenu( TEST_FOLDER.getName() )        saveScreenshot( "context-menu-online-folder-item-displayed" )        then: "'Unpublish' menu item should be displayed"        contentBrowsePanel.isContextMenuItemDisplayed( ContentMenuItem.UNPUBLISH.getName() );        and: "'Publish' menu item should not be displayed"        !contentBrowsePanel.isContextMenuItemDisplayed( ContentMenuItem.PUBLISH.getName() );    }    def "GIVEN existing 'online' folder WHEN content selected and 'Unpublish' selected from context menu  THEN the folder should be with 'Offline' status"()    {        given: "existing online folder is selected"        findAndSelectContent( TEST_FOLDER.getName() );        when: "right clicks on the selected folder and select 'Unublish' from the context menu"        contentBrowsePanel.selectUnPublishFromContextMenu( TEST_FOLDER.getName() ).clickOnUnpublishButton();        saveScreenshot( "context-menu-folder-unpublished" )        then: "the folder should be with 'Offline' status"        contentBrowsePanel.getContentStatus( TEST_FOLDER.getName() ).equalsIgnoreCase( ContentStatus.UNPUBLISHED.getValue() );    }    def "GIVEN existing 'deleted' folder WHEN context menu is opened THEN only two menu items should be displayed"()    {        given: "existing 'deleted' folder is selected"        findAndSelectContent( TEST_FOLDER.getName() ).clickToolbarPublish().clickOnPublishNowButton().waitForDialogClosed();        contentBrowsePanel.clickToolbarDelete().doDelete();        when: "right clicks on the selected folder and a context menu opened"        contentBrowsePanel.openContextMenu( TEST_FOLDER.getName() )        saveScreenshot( "context-menu-deleted-context-menu" )        then: "'Undo delete' menu item should be displayed"        contentBrowsePanel.isContextMenuItemDisplayed( ContentMenuItem.UNDO_DELETE.getName() );        and: "'Publish' menu item should be displayed"        contentBrowsePanel.isContextMenuItemDisplayed( ContentMenuItem.PUBLISH.getName() );    }    def "GIVEN existing 'deleted' folder WHEN content selected and 'Undo delete' selected from context menu THEN the content is getting 'Online'"()    {        given: "existing 'deleted' folder is selected"        findAndSelectContent( TEST_FOLDER.getName() );        when: "right clicks on the selected folder and a context menu opened"        contentBrowsePanel.selectUndoDeleteFromContextMenu( TEST_FOLDER.getName() );        saveScreenshot( "context-menu-undo-deleted-clicked" )        then: "the content is getting 'Online'"        contentBrowsePanel.getContentStatus( TEST_FOLDER.getName() ) == ContentStatus.PUBLISHED.getValue()    }    def "GIVEN existing folder WHEN content selected and 'Duplicate' selected from context menu  THEN new content with 'copy' in name is listed"()    {        setup: "build a new folder-content"        Content folder = buildFolderContent( "folder", "context menu duplicate" );        and: "new folder has been added"        addContent( folder );        when: "right clicks on the selected folder and select 'Duplicate' from the context menu"        contentBrowsePanel.doDuplicateContent( folder.getName() );        filterPanel.typeSearchText( folder.getName() + "-copy" );        saveScreenshot( "context-menu-folder-duplicated" );        then: "new content with 'copy' in name should be listed"        contentBrowsePanel.exists( folder.getName() + "-copy" );        and: "status should be 'New'"        contentBrowsePanel.getContentStatus( folder.getName() + "-copy" ) == ContentStatus.NEW.getValue();    }    def "WHEN right click on existing folder AND 'Edit' menu item clicked THEN the folder should be opened in wizard"()    {        setup: "builds a new folder-content"        Content content = buildFolderContent( "folder", "context menu edit" );        and: "new folder has been added"        addContent( content );        when: "right clicks on the selected folder and select 'Edit' from the context menu, change the display name and save it"        findAndSelectContent( content.getName() );        contentBrowsePanel.selectEditFromContextMenu( content.getName() ).typeDisplayName(            NEW_DISPLAY_NAME ).save().closeBrowserTab().switchToBrowsePanelTab();        then: "content should be listed with it's new display name"        filterPanel.typeSearchText( NEW_DISPLAY_NAME );        saveScreenshot( "context-menu-folder-edited" )        contentBrowsePanel.exists( content.getName(), true );    }    def "WHEN right click on existing folder AND 'Sort' menu item has been clicked THEN 'Sort' modal dialog should appear"()    {        when: "right clicks on the selected folder and select 'Sort' from the context menu"        filterPanel.typeSearchText( IMPORTED_FOLDER_NAME );        SortContentDialog modalDialog = contentBrowsePanel.selectSortInContextMenu( IMPORTED_FOLDER_NAME );        then: "'Sort' modal dialog should be displayed"        modalDialog.isDisplayed();    }    def "GIVEN parent content has been added WHEN the parent selected and 'New' from a context menu selected AND data typed AND child content saved THEN new child should be listed beneath the parent"()    {        setup: "builds a new folder-content"        Content parent = buildFolderContent( "folder", "context menu new" );        and: "the parent content has been added"        addContent( parent );        and: "builds a new folder content, that will be child for the first content"        Content childContent = buildFolderContentWithParent( "child", "child folder", parent.getName() );        when: "parent content selected and 'New' from the context menu selected AND data typed AND wizard saved"        contentBrowsePanel.selectNewFromContextMenu( parent.getName() ).selectContentType( childContent.getContentTypeName() ).typeData(            childContent ).save().closeBrowserTab().switchToBrowsePanelTab();        sleep( 400 );        and: "parent content expanded"        filterPanel.typeSearchText( parent.getName() );        saveScreenshot( "context-menu-folder-new" )        contentBrowsePanel.expandContent( parent.getPath() );        then: "new child content should be listed beneath the parent"        contentBrowsePanel.exists( childContent.getName(), true );    }}