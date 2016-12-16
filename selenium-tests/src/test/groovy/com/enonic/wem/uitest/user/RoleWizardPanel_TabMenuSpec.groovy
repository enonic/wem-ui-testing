package com.enonic.wem.uitest.user

import com.enonic.autotests.pages.SaveBeforeCloseDialog
import com.enonic.autotests.pages.usermanager.browsepanel.UserBrowsePanel
import com.enonic.autotests.pages.usermanager.wizardpanel.RoleWizardPanel
import com.enonic.autotests.utils.TestUtils
import spock.lang.Shared

class RoleWizardPanel_TabMenuSpec
    extends BaseUsersSpec
{
    @Shared
    String ROLE_TAB_TITLE = "<Unnamed Role>"

    def "WHEN started adding a 'Role' and Wizard opened  THEN new tab with  name 'New Role' is present"()
    {
        when: "'Roles' folder clicked and 'New' button clicked and role wizard opened"
        RoleWizardPanel wizard = userBrowsePanel.clickCheckboxAndSelectFolder(
            UserBrowsePanel.BrowseItemType.ROLES_FOLDER ).clickToolbarNew().waitUntilWizardOpened();
        saveScreenshot( "tab_role" );

        then: "tab with title 'New Role' is present "
        userBrowsePanel.isTabMenuItemPresent( ROLE_TAB_TITLE );
    }

    def "GIVEN role Wizard opened, no any data typed WHEN TabmenuItem(close) clicked THEN wizard closed and BrowsePanel showed"()
    {
        given: "content wizard was opened ad AppBarTabMenu clicked"
        RoleWizardPanel wizard = userBrowsePanel.clickCheckboxAndSelectFolder(
            UserBrowsePanel.BrowseItemType.ROLES_FOLDER ).clickToolbarNew().waitUntilWizardOpened();

        when: "no any data typed and 'close' button pressed"
        SaveBeforeCloseDialog dialog = wizard.close( ROLE_TAB_TITLE );
        TestUtils.saveScreenshot( getTestSession(), "role_closed" );

        then: "close dialog should not be showed"
        dialog == null;
    }

    def "GIVEN role Wizard opened and name is typed WHEN 'Close' clicked THEN 'SaveBeforeClose' dialog showed"()
    {
        given: "role Wizard opened and name is typed"
        String displayName = "testname";
        RoleWizardPanel wizard = userBrowsePanel.clickCheckboxAndSelectFolder(
            UserBrowsePanel.BrowseItemType.ROLES_FOLDER ).clickToolbarNew().waitUntilWizardOpened().typeDisplayName( displayName );

        when: "'Close' clicked"
        SaveBeforeCloseDialog dialog = wizard.close( displayName );
        TestUtils.saveScreenshot( getTestSession(), "role_not_close" );

        then: "'SaveBeforeClose' dialog showed"
        dialog != null;
    }
}
