package com.enonic.wem.uitest.user

import com.enonic.autotests.pages.usermanager.browsepanel.UserBrowseFilterPanel
import com.enonic.autotests.pages.usermanager.browsepanel.UserBrowsePanel
import com.enonic.autotests.pages.usermanager.wizardpanel.GroupWizardPanel
import com.enonic.autotests.pages.usermanager.wizardpanel.RoleWizardPanel
import com.enonic.autotests.pages.usermanager.wizardpanel.UserWizardPanel
import com.enonic.autotests.services.NavigatorHelper
import com.enonic.autotests.utils.NameHelper
import com.enonic.autotests.vo.usermanager.Group
import com.enonic.autotests.vo.usermanager.Role
import com.enonic.autotests.vo.usermanager.User
import com.enonic.autotests.vo.usermanager.UserStore
import com.enonic.wem.uitest.BaseGebSpec
import spock.lang.Shared

class BaseUsersSpec
    extends BaseGebSpec
{
    @Shared
    UserBrowsePanel userBrowsePanel;

    @Shared
    UserBrowseFilterPanel userBrowseFilterPanel;


    def setup()
    {
        go "admin"
        userBrowsePanel = NavigatorHelper.openUserManager( getTestSession() );
        userBrowseFilterPanel = userBrowsePanel.getUserBrowseFilterPanel();
    }

    protected GroupWizardPanel openSystemGroupWizard()
    {
        userBrowsePanel.clickOnExpander( UserBrowsePanel.BrowseItemType.SYSTEM.getValue() );
        return userBrowsePanel.clickCheckboxAndSelectFolder(
            UserBrowsePanel.BrowseItemType.GROUPS_FOLDER ).clickToolbarNew().waitUntilWizardOpened();
    }

    protected UserWizardPanel openSystemUserWizard()
    {
        userBrowsePanel.clickOnExpander( UserBrowsePanel.BrowseItemType.SYSTEM.getValue() );
        return userBrowsePanel.clickCheckboxAndSelectFolder(
            UserBrowsePanel.BrowseItemType.USERS_FOLDER ).clickToolbarNew().waitUntilWizardOpened();
    }

    protected RoleWizardPanel openRoleWizard()
    {

        return userBrowsePanel.clickCheckboxAndSelectFolder(
            UserBrowsePanel.BrowseItemType.ROLES_FOLDER ).clickToolbarNew().waitUntilWizardOpened();
    }

    protected Group buildGroup( String name, String displayName, String description )
    {
        String generated = NameHelper.uniqueName( name );
        return Group.builder().displayName( displayName ).name( generated ).description( description ).build();
    }

    protected Role buildRole( String name, String displayName, String description )
    {
        String generated = NameHelper.uniqueName( name );
        return Role.builder().displayName( displayName ).name( generated ).description( description ).build();
    }

    protected User buildUser( String userName, String password )
    {
        String generated = NameHelper.uniqueName( userName );
        return User.builder().displayName( generated ).email( generated + "@gmail.com" ).password( password ).build();
    }

    protected User buildUserWithRolesAndGroups( String userName, String password, List<String> roles, List<String> groups )
    {
        String generated = NameHelper.uniqueName( userName );
        return User.builder().displayName( generated ).email( generated + "@gmail.com" ).password( password ).roles( roles ).groups(
            groups ).build();
    }

    protected UserStore buildUserStore( String displayName )
    {
        return UserStore.builder().displayName( displayName ).build();

    }
}
