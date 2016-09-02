package com.enonic.wem.uitest.application

import com.enonic.autotests.utils.TestUtils
import spock.lang.Stepwise

@Stepwise
class ApplicationBrowsePanelToolbarSpec
    extends BaseApplicationSpec
{

    def "GIVEN Applications BrowsePanel WHEN no selected module THEN 'Uninstall' button should be disabled"()
    {
        expect:
        !applicationBrowsePanel.isUninstallButtonEnabled();
    }

    def "GIVEN Applications BrowsePanel WHEN no selected module THEN 'Install' button should be enabled"()
    {
        expect:
        applicationBrowsePanel.isInstallButtonEnabled();
    }

    def "WHEN one local application is selected  THEN 'Uninstall' button should be disabled"()
    {
        when: "one local application is selected"
        applicationBrowsePanel.clickCheckboxAndSelectRowByDisplayName( SIMPLE_APP_DISPLAY_NAME );
        TestUtils.saveScreenshot( getSession(), "uninstall-local-disabled" )

        then: "'Uninstall' button is disabled"
        !applicationBrowsePanel.isUninstallButtonEnabled();
    }

    def "GIVEN Applications BrowsePanel WHEN no selected module THEN Start button should be disabled"()
    {
        expect:
        !applicationBrowsePanel.isStartButtonEnabled();
    }

    def "GIVEN Applications BrowsePanel WHEN no selected application THEN 'Stop' button should be disabled"()
    {
        expect:
        !applicationBrowsePanel.isStopButtonEnabled();
    }

    def "GIVEN a started application WHEN one started application selected  THEN 'Stop' button should be enabled AND 'Start' button is disabled"()
    {
        when: " one application selected in the table"
        applicationBrowsePanel.clickCheckboxAndSelectRowByDisplayName( SIMPLE_APP_DISPLAY_NAME );

        then: "app status is started"
        applicationBrowsePanel.findAppByDisplayNameAndGetStatus( SIMPLE_APP_DISPLAY_NAME ) == STARTED_STATE;
        and: "Stop button is enabled"
        applicationBrowsePanel.isStopButtonEnabled();

        and: "'Start' button is disabled"
        !applicationBrowsePanel.isStartButtonEnabled()
    }

    def "GIVEN a started application WHEN  module selected in the grid and stopped THEN Stop button should be disabled AND 'Start' button should be enabled"()
    {
        given: " one application selected in the table"
        applicationBrowsePanel.clickCheckboxAndSelectRowByDisplayName( SIMPLE_APP_DISPLAY_NAME );

        when: "stop button on the toolbar pressed"
        applicationBrowsePanel.clickOnToolbarStop();

        then: "Stop button becomes disabled"
        !applicationBrowsePanel.isStopButtonEnabled();

        and: "'Start' button is enabled now"
        applicationBrowsePanel.isStartButtonEnabled()
    }

    def "GIVEN one stopped application WHEN started and stopped are selected THEN 'Stop' and 'Start' buttons should be enabled"()
    {
        when: " started and stopped are selected"
        applicationBrowsePanel.clickCheckboxAndSelectRowByDisplayName( SIMPLE_APP_DISPLAY_NAME );
        applicationBrowsePanel.clickCheckboxAndSelectRowByDisplayName( SECOND_APP_DISPLAY_NAME );

        then: "Stop button is enabled"
        applicationBrowsePanel.isStopButtonEnabled();

        and: "'Start' button is disabled"
        applicationBrowsePanel.isStartButtonEnabled()
    }

    def "GIVEN two started applications are selected WHEN 'stop' button on toolbar pressed THEN both applications are 'stopped'"()
    {
        given: " started and stopped are selected"
        applicationBrowsePanel.clickCheckboxAndSelectRowByDisplayName( FIRST_APP_DISPLAY_NAME );
        applicationBrowsePanel.clickCheckboxAndSelectRowByDisplayName( SECOND_APP_DISPLAY_NAME );

        when: "Stop button pressed"
        applicationBrowsePanel.clickOnToolbarStop();
        TestUtils.saveScreenshot( getSession(), "multiple_select_stop" )

        then: "both applications are 'stopped'"
        applicationBrowsePanel.findAppByDisplayNameAndGetStatus( FIRST_APP_DISPLAY_NAME ) == STOPPED_STATE;
        and:
        applicationBrowsePanel.findAppByDisplayNameAndGetStatus( FIRST_APP_DISPLAY_NAME ) == STOPPED_STATE;
    }

    def "GIVEN two started applications are selected WHEN 'stop' button on toolbar pressed THEN 'Stop' button is disabled and 'Start' button is enabled"()
    {
        when: " started and stopped are selected"
        applicationBrowsePanel.clickCheckboxAndSelectRowByDisplayName( FIRST_APP_DISPLAY_NAME );
        applicationBrowsePanel.clickCheckboxAndSelectRowByDisplayName( SECOND_APP_DISPLAY_NAME );

        then: "Stop button is disabled"
        !applicationBrowsePanel.isStopButtonEnabled();

        and: "'Start' button is enabled"
        applicationBrowsePanel.isStartButtonEnabled()
    }

}