package com.enonic.wem.uitest.content

import com.enonic.autotests.pages.Application
import com.enonic.autotests.pages.contentmanager.browsepanel.ContentBrowsePanel
import com.enonic.autotests.pages.contentmanager.wizardpanel.ContentWizardPanel
import com.enonic.autotests.pages.contentmanager.wizardpanel.PageComponentsViewDialog
import com.enonic.autotests.pages.form.HtmlArea0_1_FormViewPanel
import com.enonic.autotests.pages.form.SiteFormViewPanel
import com.enonic.autotests.pages.form.liveedit.LiveFormPanel
import com.enonic.autotests.pages.form.liveedit.MceToolbar
import com.enonic.autotests.pages.usermanager.browsepanel.UserBrowsePanel
import com.enonic.autotests.pages.usermanager.wizardpanel.UserWizardPanel
import com.enonic.autotests.services.NavigatorHelper
import com.enonic.autotests.utils.NameHelper
import com.enonic.autotests.utils.TestUtils
import com.enonic.autotests.vo.contentmanager.Content
import com.enonic.autotests.vo.contentmanager.security.ContentAclEntry
import com.enonic.autotests.vo.contentmanager.security.PermissionSuite
import com.enonic.autotests.vo.usermanager.RoleName
import com.enonic.autotests.vo.usermanager.User
import com.enonic.wem.uitest.BaseGebSpec
import com.enonic.xp.content.ContentPath
import com.enonic.xp.data.PropertyTree
import com.enonic.xp.schema.content.ContentTypeName
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Stepwise

/**
 * Created  on 08.12.2016.
 * Task: XP-4658 Add selenium tests to verify displaying of the Source Button
 * */
//TODO remove Ignore when the  XP-4681 SiteWizard -Page controller - drop down disappears, when permission was added
@Ignore
@Stepwise
class Display_SourceButton_Spec
    extends BaseGebSpec
{
    @Shared
    UserBrowsePanel userBrowsePanel;

    @Shared
    ContentBrowsePanel contentBrowsePanel;


    @Shared
    private String USER_NAME = NameHelper.uniqueName( "user" );

    @Shared
    User USER

    @Shared
    String USER_PASSWORD = "1q2w3e";

    @Shared
    Content SITE;

    def "setup: adding of a user with roles: 'Content Manager App' AND 'Administration Console Login' "()
    {
        setup: "navigate to USERS app"
        go "admin"
        userBrowsePanel = NavigatorHelper.openUsersApp( getTestSession() );

        and: "adding a user by the SU"
        String[] roles = [RoleName.ADMIN_CONSOLE.getValue(), RoleName.CM_APP.getValue()];
        USER = User.builder().displayName( USER_NAME ).email( USER_NAME + "@gmail.com" ).password( USER_PASSWORD ).roles(
            roles.toList() ).build();

        userBrowsePanel.clickOnExpander( UserBrowsePanel.BrowseItemType.SYSTEM.getValue() );
        UserWizardPanel userWizardPanel = userBrowsePanel.clickCheckboxAndSelectFolder(
            UserBrowsePanel.BrowseItemType.USERS_FOLDER ).clickToolbarNew().waitUntilWizardOpened();

        when: "data typed and user saved"
        userWizardPanel.typeData( USER ).save().close( USER.getDisplayName() );
        userBrowsePanel.getFilterPanel().typeSearchText( USER.getDisplayName() );

        then: "new user present beneath a system store"
        userBrowsePanel.exists( USER.getDisplayName(), true );
    }

    def "GIVEN Content Studio is opened with SU WHEN new site with 'Full Access' for just created user added THEN Content is listed in BrowsePanel"()
    {
        given: "navigate to 'Content Studio'"
        ContentAclEntry entry = ContentAclEntry.builder().principalName( USER_NAME ).suite( PermissionSuite.FULL_ACCESS ).build();
        List<ContentAclEntry> aclEntries = new ArrayList<>()
        aclEntries.add( entry );
        SITE = buildSimpleSiteApp( aclEntries );

        go "admin"
        ContentBrowsePanel contentBrowsePanel = NavigatorHelper.openContentStudioApp( getTestSession() );

        when: "new site with permissions 'Full Access' for the user has been saved"
        contentBrowsePanel.clickToolbarNew().selectContentType( SITE.getContentTypeName() ).
            typeData( SITE ).selectPageDescriptor( "main region" ).save().closeBrowserTab().switchToBrowsePanelTab();

        then: "content listed in the grid"
        contentBrowsePanel.exists( SITE.getName() );
    }

    def "GIVEN Content Studio is opened with SU WHEN the site is opened and text component added THEN component is displayed"()
    {
        given: "navigate to 'Content Studio'"
        go "admin"
        contentBrowsePanel = NavigatorHelper.openContentStudioApp( getTestSession() );
        and: "open the site"
        ContentWizardPanel siteWizard = findAndSelectContent( SITE.getName() ).clickToolbarEdit();
        PageComponentsViewDialog pageComponentsView = siteWizard.showComponentView();


        when: "new text component has been inserted"
        pageComponentsView.openMenu( "main" ).selectMenuItem( "Insert", "Text" );
        siteWizard.switchToLiveEditFrame();
        LiveFormPanel liveFormPanel = new LiveFormPanel( getSession() );
        liveFormPanel.typeTextInTextComponent( "test text" );
        siteWizard.switchToDefaultWindow();
        pageComponentsView.doCloseDialog();

        and: "site saved in the wizard"
        siteWizard.save();
        siteWizard.waitInvisibilityOfSpinner( Application.EXPLICIT_NORMAL );

        then: "text component is displayed"
        siteWizard.switchToLiveEditFrame();
        liveFormPanel.isTextComponentPresent();
    }

    def "GIVEN existing site with text component WHEN user without required roles tries to edit the text component THEN Source Button is not displayed"()
    {
        given: "user manager opened"
        go "admin"
        getTestSession().setUser( USER );
        ContentBrowsePanel contentBrowsePanel = NavigatorHelper.openContentStudioApp( getTestSession() );
        saveScreenshot( "logged_" + USER_NAME );
        ContentWizardPanel wizard = contentBrowsePanel.clickCheckboxAndSelectRow( SITE.getName() ).clickToolbarEdit();
        wizard.switchToLiveEditFrame();
        LiveFormPanel liveFormPanel = new LiveFormPanel( getSession() );
        MceToolbar mceToolbar = liveFormPanel.getMceToolbar();

        when: "double click on the text component is performed"
        liveFormPanel.doubleClickOnTextComponent( "test text" );
        saveScreenshot( "text_component_edit_inline_mode" );

        then: "'Source Button button is not displayed"
        !mceToolbar.isSourceCodeButtonDisplayed();
    }

    def "GIVEN existing site WHEN user without required roles tries to add a htmlArea content THEN Source Button should not be displayed on the htmlarea-toolbar"()
    {
        given: "user manager opened"
        go "admin"
        getTestSession().setUser( USER );
        ContentBrowsePanel contentBrowsePanel = NavigatorHelper.openContentStudioApp( getTestSession() );
        saveScreenshot( "logged_" + USER_NAME );

        when: "double click on the text component is performed"
        contentBrowsePanel.clickCheckboxAndSelectRow( SITE.getName() ).clickToolbarNew().selectContentType(
            "com.enonic.xp.testing.simple_page:htmlarea" );
        saveScreenshot( "htmlarea_source_button_not_displayed" );
        HtmlArea0_1_FormViewPanel htmlArea = new HtmlArea0_1_FormViewPanel( getSession() );

        then: "'Source Button button is not displayed"
        !htmlArea.isSourceCodeButtonDisplayed();
    }

    def "GIVEN existing user opened in the wizard WHEN required role added THEN new role is displayed on the wizard"()
    {
        setup: "navigate to USERS app"
        go "admin"
        getTestSession().setUser( null );
        userBrowsePanel = NavigatorHelper.openUsersApp( getTestSession() );

        and: "existing user is found"
        userBrowsePanel.getFilterPanel().typeSearchText( USER.getDisplayName() );

        and: "the user is selected and opened"
        userBrowsePanel.clickCheckboxAndSelectUser( USER.getDisplayName() );
        UserWizardPanel userWizardPanel = userBrowsePanel.clickToolbarEdit();

        when: "required role added"
        userWizardPanel.addRole( RoleName.CM_EXPERT.getValue() );
        userWizardPanel.save();

        then: "new role is displayed on the wizard"
        TestUtils.isContains( userWizardPanel.getRoleNames(), RoleName.CM_EXPERT.getValue() );
    }

    def "GIVEN existing site with text component WHEN user with required roles tries to edit the text component THEN Source Button should be displayed on the mce-toolbar"()
    {
        given: "user manager opened"
        go "admin"
        getTestSession().setUser( USER );
        ContentBrowsePanel contentBrowsePanel = NavigatorHelper.openContentStudioApp( getTestSession() );
        saveScreenshot( "logged_" + USER_NAME );
        ContentWizardPanel wizard = contentBrowsePanel.clickCheckboxAndSelectRow( SITE.getName() ).clickToolbarEdit();
        wizard.switchToLiveEditFrame();
        LiveFormPanel liveFormPanel = new LiveFormPanel( getSession() );
        MceToolbar mceToolbar = liveFormPanel.getMceToolbar();

        when: "double click on the text component is performed"
        liveFormPanel.doubleClickOnTextComponent( "test text" );
        saveScreenshot( "text_component_edit_inline_mode" );

        then: "'Source Button button is not displayed"
        mceToolbar.isSourceCodeButtonDisplayed();
    }

    def "GIVEN existing site WHEN user with required roles tries to add a htmlArea content THEN 'Source Button' should be displayed on the htmlarea-toolbar"()
    {
        given: "user manager opened"
        go "admin"
        getTestSession().setUser( USER );
        ContentBrowsePanel contentBrowsePanel = NavigatorHelper.openContentStudioApp( getTestSession() );
        saveScreenshot( "logged_" + USER_NAME );

        when: "double click on the text component is performed"
        contentBrowsePanel.clickCheckboxAndSelectRow( SITE.getName() ).clickToolbarNew().selectContentType(
            "com.enonic.xp.testing.simple_page:htmlarea" );
        saveScreenshot( "htmlarea_source_button_should_be_displayed" );
        HtmlArea0_1_FormViewPanel htmlArea = new HtmlArea0_1_FormViewPanel( getSession() );

        then: "'Source Button button is not displayed"
        htmlArea.isSourceCodeButtonDisplayed();
    }


    protected Content buildSimpleSiteApp( List<ContentAclEntry> aclEntries )
    {
        String name = NameHelper.uniqueName( "site" );
        PropertyTree data = new PropertyTree();
        data.addStrings( SiteFormViewPanel.APP_KEY, "Simple Site App" );
        data.addStrings( "description", "simple site " )
        Content site = Content.builder().
            parent( ContentPath.ROOT ).
            name( name ).
            displayName( "simple site" ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.site() ).data( data ).aclEntries( aclEntries ).
            build();
        return site;
    }

    private ContentBrowsePanel findAndSelectContent( String name )
    {
        contentBrowsePanel.getFilterPanel().typeSearchText( name );
        if ( !contentBrowsePanel.isRowSelected( name ) )
        {
            contentBrowsePanel.clickCheckboxAndSelectRow( name );
        }

        return contentBrowsePanel;
    }

}
