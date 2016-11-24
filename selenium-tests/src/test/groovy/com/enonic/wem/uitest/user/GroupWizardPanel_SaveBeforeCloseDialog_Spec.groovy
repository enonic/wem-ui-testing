package com.enonic.wem.uitest.user

import com.enonic.autotests.pages.SaveBeforeCloseDialog
import com.enonic.autotests.pages.usermanager.wizardpanel.GroupWizardPanel
import com.enonic.autotests.utils.TestUtils
import com.enonic.autotests.vo.usermanager.Group
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class GroupWizardPanel_SaveBeforeCloseDialog_Spec
    extends BaseUsersSpec
{
    @Shared
    String newDisplayName = "changeDisplayName"

    @Shared
    Group TEST_GROUP

    @Shared
    String NEW_DISPLAY_NAME = "no button selected";

    def "GIVEN a unchanged group WHEN closing THEN SaveBeforeCloseDialog must not appear"()
    {
        given:
        GroupWizardPanel groupWizardPanel = openSystemGroupWizard();
        TEST_GROUP = buildGroup( "group", "test-group", "description" );
        groupWizardPanel.typeData( TEST_GROUP ).save();

        when:
        SaveBeforeCloseDialog dialog = groupWizardPanel.close( TEST_GROUP.getDisplayName() );

        then:
        dialog == null;
    }

    def " GIVEN a changed group WHEN closing THEN SaveBeforeCloseDialog must appear"()
    {
        given: "wizard opened data typed and 'Save' pressed AND displayName changed"
        GroupWizardPanel groupWizardPanel = openSystemGroupWizard();
        Group group = buildGroup( "group", "test-group", "description" );
        groupWizardPanel.typeData( group ).save();
        groupWizardPanel.typeDisplayName( newDisplayName );

        when: "'Close' button pressed"
        SaveBeforeCloseDialog dialog = groupWizardPanel.close( newDisplayName )
        saveScreenshot( "SaveBeforeCloseDialog-displayed-group" );

        then: "modal dialog appears"
        dialog != null;
    }

    def "GIVEN changing name of an existing group and wizard closing WHEN Yes is chosen THEN new display name not saved"()
    {
        given:
        userBrowseFilterPanel.typeSearchText( TEST_GROUP.getName() );
        GroupWizardPanel groupWizardPanel = userBrowsePanel.clickCheckboxAndSelectGroup( TEST_GROUP.getName() ).clickToolbarEdit();
        groupWizardPanel.typeDisplayName( newDisplayName ).close( newDisplayName );
        SaveBeforeCloseDialog dialog = new SaveBeforeCloseDialog( getSession() );
        dialog.waitForPresent();

        when: "Yes is chosen"
        dialog.clickYesButton();
        saveScreenshot( "SaveBeforeCloseDialog-yes-pressed-group" );

        then: "new display name not saved"
        userBrowseFilterPanel.typeSearchText( newDisplayName );
        userBrowsePanel.exists( TEST_GROUP.getName() );
    }

    def "GIVEN changing name of an existing group and wizard closing WHEN No is chosen THEN group is listed in BrowsePanel with it's original name"()
    {
        given:
        userBrowseFilterPanel.typeSearchText( TEST_GROUP.getName() );
        GroupWizardPanel groupWizardPanel = userBrowsePanel.clickCheckboxAndSelectGroup( TEST_GROUP.getName() ).clickToolbarEdit();
        groupWizardPanel.typeDisplayName( NEW_DISPLAY_NAME ).close( NEW_DISPLAY_NAME );
        SaveBeforeCloseDialog dialog = new SaveBeforeCloseDialog( getSession() );
        dialog.waitForPresent();

        when: "No is chosen"
        dialog.clickNoButton();
        TestUtils.saveScreenshot( getSession(), "SaveBeforeCloseDialog-no-gr" );

        then: "Content is listed in BrowsePanel with it's original name"
        userBrowseFilterPanel.typeSearchText( NEW_DISPLAY_NAME );
        !userBrowsePanel.exists( TEST_GROUP.getName() );
    }

    def "GIVEN changing an existing group and wizard closing WHEN 'Cancel' is chosen THEN wizard is still open"()
    {
        given:
        userBrowseFilterPanel.typeSearchText( TEST_GROUP.getName() );
        GroupWizardPanel groupWizardPanel = userBrowsePanel.clickCheckboxAndSelectGroup( TEST_GROUP.getName() ).clickToolbarEdit();
        SaveBeforeCloseDialog dialog = groupWizardPanel.typeDisplayName( "cancel test" ).close( "cancel test" );

        when:
        dialog.clickCancelButton();
        saveScreenshot( "SaveBeforeCloseDialog-cancel-clicked" );

        then:
        groupWizardPanel.isOpened();
    }
}
