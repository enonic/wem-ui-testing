package com.enonic.wem.uitest.contentimport com.enonic.autotests.pages.contentmanager.browsepanel.ContentBrowsePanelimport com.enonic.autotests.pages.contentmanager.wizardpanel.ItemViewPanelPageimport com.enonic.autotests.services.NavigatorHelperimport com.enonic.autotests.utils.NameHelperimport com.enonic.autotests.vo.contentmanager.Contentimport com.enonic.wem.api.content.ContentPathimport com.enonic.wem.api.schema.content.ContentTypeNameimport com.enonic.wem.uitest.BaseGebSpecimport spock.lang.Sharedclass ContentBrowsePanel_ContextMenu_Spec    extends BaseGebSpec{    @Shared    ContentBrowsePanel contentBrowsePanel;    def setup()    {        go "admin"        contentBrowsePanel = NavigatorHelper.openContentApp( getTestSession() );    }    def "GIVEN a content WHEN content selected and 'Delete' selected from context menu  THEN content not exist in view"()    {        given:        String name = NameHelper.uniqueName( "folder" );        Content content = Content.builder().            name( name ).            displayName( "folder" ).            contentType( ContentTypeName.folder() ).            parent( ContentPath.ROOT ).            build();        contentBrowsePanel.clickToolbarNew().selectContentType( content.getContentTypeName() ).typeData( content ).save().close();        contentBrowsePanel.waitsForSpinnerNotVisible();        when:        contentBrowsePanel.selectDeleteFromContextMenu( content.getPath() ).doDelete();        contentBrowsePanel.waituntilPageLoaded( 3 );        then:        !contentBrowsePanel.exists( content.getPath(), 1 );    }    def "GIVEN a content WHEN content selected and 'Edit' selected from context menu  THEN content is listed with it's new name"()    {        given:        String name = NameHelper.uniqueName( "archive" );        Content content = Content.builder().            name( name ).            displayName( "archive" ).            contentType( ContentTypeName.archiveMedia() ).            parent( ContentPath.ROOT ).            build();        contentBrowsePanel.clickToolbarNew().selectContentType( content.getContentTypeName() ).typeData( content ).save().close();        contentBrowsePanel.waitsForSpinnerNotVisible();        Content newcontent = cloneContentWithNewName( content )        when:        contentBrowsePanel.selectEditFromContextMenu( content.getPath() ).typeData( newcontent ).save().close();        contentBrowsePanel.waituntilPageLoaded( 3 );        then:        contentBrowsePanel.exists( newcontent.getPath(), 1 );    }    def "GIVEN a content WHEN content selected and 'Open' selected from a context menu THEN title with content display-name showed"()    {        given:        String name = NameHelper.uniqueName( "archive" );        Content content = Content.builder().            name( name ).            displayName( "archive" ).            contentType( ContentTypeName.archiveMedia() ).            parent( ContentPath.ROOT ).            build();        contentBrowsePanel.clickToolbarNew().selectContentType( content.getContentTypeName() ).typeData( content ).save().close();        contentBrowsePanel.waitsForSpinnerNotVisible();        when:        ItemViewPanelPage itemView = contentBrowsePanel.selectOpenFromContextMenu( content.getPath() );        itemView.waitUntilOpened( content.getDisplayName() );        then:        itemView.getTitle().equals( content.getDisplayName() );    }    def "GIVEN a content WHEN content selected and 'New' selected from a context menu THEN new Content should be listed beneath parent"()    {        given:        String name = NameHelper.uniqueName( "archive" );        Content content = Content.builder().            name( name ).            displayName( "archive" ).            contentType( ContentTypeName.archiveMedia() ).            parent( ContentPath.ROOT ).            build();        contentBrowsePanel.clickToolbarNew().selectContentType( content.getContentTypeName() ).typeData( content ).save().close();        contentBrowsePanel.waitsForSpinnerNotVisible();        Content newcontent = Content.builder().            name( "child" ).            displayName( "child" ).            parent( ContentPath.from( name ) ).            contentType( ContentTypeName.archiveMedia() ).            build();        when:        contentBrowsePanel.selectNewFromContextMenu( content.getPath() ).selectContentType( newcontent.getContentTypeName() ).typeData(            newcontent ) save().close();        contentBrowsePanel.waituntilPageLoaded( 3 );        contentBrowsePanel.expandContent( content.getPath() );        then:        contentBrowsePanel.exists( newcontent.getPath(), 1 );    }    Content cloneContentWithNewName( Content source )    {        String newName = NameHelper.uniqueName( "newname" );        return Content.builder().            name( newName ).            displayName( source.getDisplayName() ).            parent( source.getParent() ).            contentType( source.getContentTypeName() ).            build();    }}