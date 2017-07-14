package com.enonic.wem.uitest.user

import com.enonic.autotests.pages.HomePage
import com.enonic.autotests.pages.LauncherPanel
import com.enonic.autotests.pages.LoginPage
import com.enonic.autotests.pages.contentmanager.browsepanel.ContentBrowsePanel
import com.enonic.autotests.pages.contentmanager.wizardpanel.ContentWizardPanel
import com.enonic.autotests.pages.usermanager.browsepanel.UserBrowsePanel
import com.enonic.autotests.pages.usermanager.wizardpanel.ChangeUserPasswordDialog
import com.enonic.autotests.pages.usermanager.wizardpanel.UserWizardPanel
import com.enonic.autotests.services.NavigatorHelper
import com.enonic.autotests.utils.NameHelper
import com.enonic.autotests.vo.contentmanager.Content
import com.enonic.autotests.vo.contentmanager.security.ContentAclEntry
import com.enonic.autotests.vo.contentmanager.security.PermissionSuite
import com.enonic.autotests.vo.usermanager.RoleName
import com.enonic.autotests.vo.usermanager.User
import com.enonic.wem.uitest.BaseGebSpec
import com.enonic.xp.content.ContentPath
import com.enonic.xp.schema.content.ContentTypeName
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class LoginUserSpec
    extends BaseGebSpec
{

    @Shared
    UserBrowsePanel userBrowsePanel;

    @Shared
    private String USER_NAME = NameHelper.uniqueName( "user" );

    @Shared
    User USER_ADMIN_CONSOLE

    @Shared
    String USER_PASSWORD = "1q2w3e";

    @Shared
    String NEW_USER_PASSWORD = "password1";

    @Shared
    Content contentCanWrite

    @Shared
    Content contentCanNotWrite

    def "setup: add a test user to the system user store"()
    {
        setup: "'Users' app is opened"
        go "admin"
        userBrowsePanel = NavigatorHelper.openUsersApp( getTestSession() );

        and: "User-wizard is opened"
        String[] roles = [RoleName.ADMIN_CONSOLE.getValue(), RoleName.CM_APP.getValue()];
        USER_ADMIN_CONSOLE = User.builder().displayName( USER_NAME ).email( USER_NAME + "@gmail.com" ).password( USER_PASSWORD ).roles(
            roles.toList() ).build();

        userBrowsePanel.clickOnExpander( UserBrowsePanel.BrowseItemType.SYSTEM.getValue() );
        UserWizardPanel userWizardPanel = userBrowsePanel.clickCheckboxAndSelectFolder(
            UserBrowsePanel.BrowseItemType.USERS_FOLDER ).clickToolbarNew().waitUntilWizardOpened();

        when: "data typed and user saved"
        userWizardPanel.typeData( USER_ADMIN_CONSOLE ).save().close( USER_ADMIN_CONSOLE.getDisplayName() );
        userBrowsePanel.getFilterPanel().typeSearchText( USER_ADMIN_CONSOLE.getDisplayName() );

        then: "new user present beneath a system store"
        userBrowsePanel.exists( USER_ADMIN_CONSOLE.getDisplayName(), true );
    }

    def "WHEN new content with permissions for just created user added THEN Content is listed in BrowsePanel"()
    {
        given:
        ContentAclEntry entry = ContentAclEntry.builder().principalName( USER_NAME ).suite( PermissionSuite.CAN_WRITE ).build();
        List<ContentAclEntry> aclEntries = new ArrayList<>()
        aclEntries.add( entry );
        contentCanWrite = Content.builder().
            name( NameHelper.uniqueName( "folder-login" ) ).
            displayName( "folder" ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.folder() ).
            aclEntries( aclEntries ).
            build();

        go "admin"
        ContentBrowsePanel contentBrowsePanel = NavigatorHelper.openContentStudioApp( getTestSession() );

        when: "new content with permissions CAN_READ for user  saved"
        contentBrowsePanel.clickToolbarNew().selectContentType( ContentTypeName.folder().toString() ).
            typeData( contentCanWrite ).save().closeBrowserTab().switchToBrowsePanelTab();

        then: "content listed in the grid"
        contentBrowsePanel.exists( contentCanWrite.getName() );
    }

    def "WHEN new content without any permissions for just created user added THEN Content is listed in BrowsePanel"()
    {
        given: "new content without any permissions for just created user added"
        contentCanNotWrite = Content.builder().
            name( NameHelper.uniqueName( "folder-login" ) ).
            displayName( "folder" ).
            parent( ContentPath.ROOT ).
            contentType( ContentTypeName.folder() ).
            build();

        go "admin"
        ContentBrowsePanel contentBrowsePanel = NavigatorHelper.openContentStudioApp( getTestSession() );

        when: "new content with permissions CAN_READ for user  saved"
        contentBrowsePanel.clickToolbarNew().selectContentType( ContentTypeName.folder().toString() ).
            typeData( contentCanNotWrite ).save().closeBrowserTab().switchToBrowsePanelTab();

        then: "content listed in the grid"
        contentBrowsePanel.exists( contentCanNotWrite.getName() );
    }

    def "GIVEN existing user with role 'Content Manager App' WHEN user logged in THEN correct user's display name shown AND 'Applications' and 'Users' links are not present on the launcher"()
    {
        given:
        go "admin"
        User user = User.builder().displayName( USER_NAME ).password( USER_PASSWORD ).build();
        getTestSession().setUser( user );

        when:
        NavigatorHelper.loginAndOpenHomePage( getTestSession() );
        saveScreenshot( "logged_home" + USER_NAME );
        LauncherPanel launcherPanel = new LauncherPanel( getSession() );

        then: "'Applications' link not displayed"
        !launcherPanel.isApplicationsLinkDisplayed();

        and: "'Users' link not displayed"
        !launcherPanel.isUsersLinkDisplayed();

        and: "'Home' link is displayed"
        launcherPanel.isHomeLinkDisplayed();
    }

    def "GIVEN just created user 'logged in' WHEN user opened a content with CAN_WRITE permission and typed new 'display name' THEN 'save draft' button is enabled"()
    {
        given: "user manager opened"
        go "admin"
        getTestSession().setUser( USER_ADMIN_CONSOLE );
        ContentBrowsePanel contentBrowsePanel = NavigatorHelper.openContentStudioApp( getTestSession() );
        saveScreenshot( "logged_" + USER_NAME );

        when: "user opened a content with CAN_WRITE permission and typed new 'display name'"
        ContentWizardPanel wizard = contentBrowsePanel.clickCheckboxAndSelectRow( contentCanWrite.getName() ).clickToolbarEdit();
        wizard.typeDisplayName( "content updated" );
        saveScreenshot( "save_enabled" );

        then: "'save draft' button is enabled"
        wizard.isSaveButtonEnabled()
    }

    def "GIVEN just created user is 'logged in' WHEN user has 'CAN_READ' for the content THEN 'Edit' button should be disabled"()
    {
        given: "just created user is 'logged in'"
        go "admin"
        getTestSession().setUser( USER_ADMIN_CONSOLE );
        ContentBrowsePanel contentBrowsePanel = NavigatorHelper.openContentStudioApp( getTestSession() );
        saveScreenshot( "logged_" + USER_NAME );

        when: "user opened a content without 'CAN_WRITE' permission and typed new display name"
        contentBrowsePanel.clickCheckboxAndSelectRow( contentCanNotWrite.getName() )
        saveScreenshot( "test_perm_edit_disabled" );

        then: "'Edit' button should be disabled"
        !contentBrowsePanel.isEditButtonEnabled();
    }

    def "GIVEN user-wizard opened WHEN 'change password' button has been pressed THEN 'change password' dialog should appear"()
    {
        given: "admin opens a user in the wizard"
        go "admin"
        getTestSession().setUser( null );
        userBrowsePanel = NavigatorHelper.openUsersApp( getTestSession() );
        userBrowsePanel.expandUsersFolder( "system" );
        UserWizardPanel userWizardPanel = userBrowsePanel.clickCheckboxAndSelectUser(
            USER_ADMIN_CONSOLE.getDisplayName() ).clickToolbarEdit().waitUntilWizardOpened();

        when: "'change password' button pressed"
        ChangeUserPasswordDialog dialog = userWizardPanel.clickOnChangePassword().waitForLoaded( 2 );
        saveScreenshot( "test_open_change_password_dialog" );

        then: "'change password' dialog should appear"
        dialog.isOpened();

        and: "'change' and 'cancel' buttons should be present"
        dialog.isCancelButtonDisplayed();

        and:
        dialog.isChangeButtonDisplayed();
    }

    def "GIVEN user-wizard opened WHEN changing a password for existing user AND wizard closed THEN user browse panel shown "()
    {
        given: "user-wizard opened"
        go "admin"
        getTestSession().setUser( null );
        userBrowsePanel = NavigatorHelper.openUsersApp( getTestSession() );
        userBrowsePanel.expandUsersFolder( "system" ).doClearSelection();
        UserWizardPanel userWizardPanel = userBrowsePanel.clickCheckboxAndSelectUser(
            USER_ADMIN_CONSOLE.getDisplayName() ).clickToolbarEdit().waitUntilWizardOpened();

        when: "user's password was changed by administrator"
        ChangeUserPasswordDialog dialog = userWizardPanel.clickOnChangePassword().waitForLoaded( 2 );
        dialog.doChangePassword( NEW_USER_PASSWORD );
        saveScreenshot( "test_password_for_user_changed" )
        userWizardPanel.save().close( USER_ADMIN_CONSOLE.getDisplayName() );

        then: "user-browse panel shown"
        userBrowsePanel.waitUntilPageLoaded( 2 );
    }

    def "WHEN login page opened and old password typed THEN old password should not work for login"()
    {
        when: "login page opened and old password typed"
        go "admin"
        User user = User.builder().displayName( USER_NAME ).password( USER_PASSWORD ).build();
        getTestSession().setUser( user );
        NavigatorHelper.loginAndOpenHomePage( getTestSession() );
        LoginPage loginPage = new LoginPage( getSession() );
        saveScreenshot( "test_login_old_password" );

        then: "old password should not work for login, login page should still displayed"
        loginPage.isDisplayed();
    }

    def "WHEN user 'logged in' with the new password THEN home page loaded"()
    {
        when: "login page opened and new password typed"
        go "admin"
        User user = User.builder().displayName( USER_NAME ).password( NEW_USER_PASSWORD ).build();
        getTestSession().setUser( user );
        HomePage home = NavigatorHelper.loginAndOpenHomePage( getTestSession() );
        saveScreenshot( "test_login_with_new_pass" );

        then: "home page successfully loaded"
        home.isDisplayed();
    }
}
