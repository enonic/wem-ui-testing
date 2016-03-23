package com.enonic.wem.uitest.content

import com.enonic.autotests.XP_Windows
import com.enonic.autotests.pages.contentmanager.browsepanel.*
import com.enonic.autotests.pages.contentmanager.wizardpanel.ContentWizardPanel
import com.enonic.autotests.pages.form.PageTemplateFormViewPanel
import com.enonic.autotests.pages.form.SiteFormViewPanel
import com.enonic.autotests.services.NavigatorHelper
import com.enonic.autotests.utils.NameHelper
import com.enonic.autotests.utils.TestUtils
import com.enonic.autotests.vo.contentmanager.Content
import com.enonic.autotests.vo.contentmanager.ContentSettings
import com.enonic.wem.uitest.BaseGebSpec
import com.enonic.xp.content.ContentPath
import com.enonic.xp.data.PropertyTree
import com.enonic.xp.schema.content.ContentTypeName
import spock.lang.Shared

class BaseContentSpec
    extends BaseGebSpec
{
    @Shared
    String TEMPLATE_SUPPORTS_COUNTRY = "country";

    @Shared
    String NORSK_LANGUAGE = "norsk (no)";

    @Shared
    String TEMPLATE_SUPPORTS_SITE = "site";

    @Shared
    String CONTENT_STUDIO_URL_PART = "content-studio";

    @Shared
    String COUNTRY_REGION_PAGE_CONTROLLER = "Country Region";

    @Shared
    String IMPORTED_FOLDER_NAME = "all-content-types-images";

    @Shared
    String IMPORTED_BOOK_IMAGE = "book.jpg";

    @Shared
    String CIRCLES = "circles.svg"

    @Shared
    String SIMPLE_SITE_APP = "Simple Site App";

    @Shared
    String MY_FIRST_APP = "My First App";

    @Shared
    String CONTENT_TYPES_NAME_APP = "All Content Types App";

    @Shared
    String COUNTRY_SITE_HTML_HEADER = "<title>Country Region</title>";

    @Shared
    String SUPER_USER = "Super User";

    @Shared
    String ANONYMOUS_USER = "Anonymous User";

    @Shared
    String EXECUTABLE_BAT = "server.bat";

    @Shared
    String EXECUTABLE_SH = "server.sh";

    @Shared
    String EXECUTABLE_EXE = "Notepad2.exe";

    @Shared
    ContentBrowsePanel contentBrowsePanel;

    @Shared
    ContentBrowseItemPanel contentBrowseItemPanel;

    @Shared
    ContentBrowseFilterPanel filterPanel;

    @Shared
    ContentBrowseItemsSelectionPanel itemsSelectionPanel;

    @Shared
    ContentDetailsPanel contentDetailsPanel;

    @Shared
    ContentItemPreviewPanel contentItemPreviewPanel;


    def setup()
    {
        go "admin"
        contentBrowsePanel = NavigatorHelper.openContentApp( getTestSession() );
        filterPanel = contentBrowsePanel.getFilterPanel();
        itemsSelectionPanel = contentBrowsePanel.getItemSelectionPanel();
        contentBrowseItemPanel = new ContentBrowseItemPanel( getSession() );
        contentItemPreviewPanel = contentBrowseItemPanel.getContentItemStatisticsPanel().getContentItemPreviewPanel();
        contentDetailsPanel = contentBrowsePanel.getContentDetailsPanel();
    }

    public Content buildFolderContent( String name, String displayName )
    {
        String generated = NameHelper.uniqueName( name );
        Content content = Content.builder().
            name( generated ).
            displayName( displayName ).
            contentType( ContentTypeName.folder() ).
            parent( ContentPath.ROOT ).
            build();
        return content;
    }

    public Content buildUnstructuredContent( String name, String displayName )
    {
        String generated = NameHelper.uniqueName( name );
        Content content = Content.builder().
            name( generated ).
            displayName( displayName ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.unstructured() ).
            build();
        return content;
    }

    public Content buildFolderWithSettingsContent( String name, String displayName, ContentSettings settings )
    {
        String generated = NameHelper.uniqueName( name );
        Content content = Content.builder().
            name( generated ).
            displayName( displayName ).
            contentType( ContentTypeName.folder() ).
            parent( ContentPath.ROOT ).settings( settings ).
            build();
        return content;
    }

    public Content buildFolderWithEmptyDisplayNameContent( String name )
    {
        String generated = NameHelper.uniqueName( name );
        Content content = Content.builder().
            name( generated ).
            contentType( ContentTypeName.folder() ).
            parent( ContentPath.ROOT ).
            build();
        return content;
    }

    public Content buildFolderContentWithParent( String name, String displayName, String parentName )
    {
        String generated = NameHelper.uniqueName( name );
        Content content = Content.builder().
            name( generated ).
            displayName( displayName ).
            contentType( ContentTypeName.folder() ).
            parent( ContentPath.from( parentName ) ).
            build();
        return content;
    }

    public void addContent( Content content )
    {
        contentBrowsePanel.clickToolbarNew().selectContentType( content.getContentTypeName() ).typeData( content ).save().close(
            content.getDisplayName() );
        contentBrowsePanel.waitsForSpinnerNotVisible();
    }

    public ContentBrowsePanel findAndSelectContent( String name )
    {
        filterPanel.typeSearchText( name );
        if ( !contentBrowsePanel.isRowSelected( name ) )
        {
            contentBrowsePanel.clickCheckboxAndSelectRow( name );
        }

        return contentBrowsePanel;
    }

    protected void switchToContentStudioWindow()
    {
        NavigatorHelper.switchToAppWindow( getSession(), CONTENT_STUDIO_URL_PART );
        getSession().setCurrentWindow( XP_Windows.CONTENT_STUDIO );
    }

    protected ContentWizardPanel selectSiteOpenWizard( String siteName, String contentTypeName )
    {
        filterPanel.typeSearchText( siteName );
        return contentBrowsePanel.clickCheckboxAndSelectRow( siteName ).clickToolbarNew().selectContentType( contentTypeName );
    }

    protected Content buildMyFirstAppSite( String siteName )
    {
        PropertyTree data = new PropertyTree();
        data.addStrings( SiteFormViewPanel.APP_KEY, "My First App" );
        data.addStrings( "description", "My first Site " )
        Content site = Content.builder().
            parent( ContentPath.ROOT ).
            name( siteName ).
            displayName( "my-site" ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.site() ).data( data ).
            build();
        return site;
    }

    protected Content buildSimpleSiteApp()
    {
        String name = NameHelper.uniqueName( "site" );
        PropertyTree data = new PropertyTree();
        data.addStrings( SiteFormViewPanel.APP_KEY, "Simple Site App" );
        data.addStrings( "description", "simple site " )
        Content site = Content.builder().
            parent( ContentPath.ROOT ).
            name( name ).
            displayName( "site with layout" ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.site() ).data( data ).
            build();
        return site;
    }

    protected Content buildSiteWithApps( String... appNames )
    {
        String name = NameHelper.uniqueName( "site" );
        PropertyTree data = new PropertyTree();
        data.addStrings( SiteFormViewPanel.APP_KEY, appNames );
        data.addStrings( "description", "simple site " )
        Content site = Content.builder().
            parent( ContentPath.ROOT ).
            name( name ).
            displayName( "test site " ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.site() ).data( data ).
            build();
        return site;
    }

    protected Content buildSite( String name, String displayName, String description )
    {
        String siteName = NameHelper.uniqueName( name );
        PropertyTree data = new PropertyTree();
        data.addStrings( "description", description )
        Content site = Content.builder().
            parent( ContentPath.ROOT ).
            name( siteName ).
            displayName( displayName ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.site() ).data( data ).
            build();
        return site;
    }

    protected Content buildUnstructured( String name, String parentName, String displayName )
    {
        ContentPath parent = null;
        if ( parentName != null )
        {
            parent = ContentPath.from( parentName )
        }
        else
        {
            parent = ContentPath.ROOT;
        }
        Content unstructured = Content.builder().
            name( NameHelper.uniqueName( name ) ).
            displayName( displayName ).
            parent( parent ).
            contentType( ContentTypeName.unstructured() ).
            build();
        return unstructured;
    }

    protected Content buildShortcut( String name, String parentName, String displayName )
    {
        ContentPath parent = null;
        if ( parentName != null )
        {
            parent = ContentPath.from( parentName )
        }
        else
        {
            parent = ContentPath.ROOT;
        }
        Content shortcut = Content.builder().
            name( NameHelper.uniqueName( name ) ).
            displayName( displayName ).
            parent( parent ).
            contentType( ContentTypeName.shortcut() ).
            build();
        return shortcut;
    }

    protected void addSiteBasedOnFirstApp( Content site )
    {
        ContentWizardPanel wizard = contentBrowsePanel.clickToolbarNew().selectContentType( site.getContentTypeName() ).typeData(
            site ).save();
        TestUtils.saveScreenshot( getSession(), "site-wizard_" + site.getName() );
        wizard.close( site.getDisplayName() );
        TestUtils.saveScreenshot( getSession(), site.getName() );
    }

    protected Content buildPageTemplate( String pageDescriptorName, String supports, String displayName, String parentName )
    {
        String name = "template";
        PropertyTree data = new PropertyTree();
        data.addStrings( PageTemplateFormViewPanel.PAGE_CONTROLLER, pageDescriptorName );
        data.addStrings( PageTemplateFormViewPanel.SUPPORTS, supports );

        Content pageTemplate = Content.builder().
            name( NameHelper.uniqueName( name ) ).
            displayName( displayName ).
            parent( ContentPath.from( parentName ) ).
            contentType( ContentTypeName.pageTemplate() ).data( data ).
            build();
        return pageTemplate;
    }

    protected void openResourceInMaster( String resource )
    {
        getDriver().navigate().to( browser.baseUrl + "admin/portal/preview/master/" + resource );
    }

    protected void openResourceInDraft( String resource )
    {
        getDriver().navigate().to( browser.baseUrl + "admin/portal/preview/draft/" + resource );
    }

    protected void openHomePage()
    {
        getDriver().navigate().to( browser.baseUrl + "admin/#/home" );
    }
}