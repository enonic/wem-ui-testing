package com.enonic.wem.uitest.content.move_publish_sort

import com.enonic.autotests.pages.contentmanager.browsepanel.SortContentDialog
import com.enonic.autotests.pages.contentmanager.browsepanel.SortMenuItem
import com.enonic.autotests.utils.TestUtils
import com.enonic.autotests.vo.contentmanager.Content
import com.enonic.wem.uitest.content.BaseContentSpec

class SortContentDialogSpec
    extends BaseContentSpec
{


    def "GIVEN Content BrowsePanel WHEN one content selected and 'Sort' button clicked THEN 'Sort Content' appears with correct control elements"()
    {
        given: "one selected content"
        findAndSelectContent( IMPORTED_FOLDER_NAME )

        when:
        SortContentDialog sortContentDialog = contentBrowsePanel.clickToolbarSort();

        then: "'SortContent' dialog displayed"
        sortContentDialog.isPresent();
        and: "has a correct title"
        sortContentDialog.getTitle() == SortContentDialog.TITLE;
        and: "has 'save, close' buttons"
        sortContentDialog.isCancelButtonEnabled();
        and:
        sortContentDialog.isSaveButtonEnabled();
        and:
        sortContentDialog.isSortMenuButtonEnabled();
    }

    def "GIVEN sort dialog opened WHEN 'Save' button clicked THEN dialog disappears"()
    {
        given: "one selected content"
        findAndSelectContent( IMPORTED_FOLDER_NAME )
        SortContentDialog sortContentDialog = contentBrowsePanel.clickToolbarSort();

        when: "'Save' clicked"
        sortContentDialog.clickOnSaveButton();

        then:
        !sortContentDialog.isPresent();
    }

    def "GIVEN sort dialog opened WHEN 'Cancel' button clicked THEN dialog disappears"()
    {
        given: "one selected content"
        findAndSelectContent( IMPORTED_FOLDER_NAME )
        SortContentDialog sortContentDialog = contentBrowsePanel.clickToolbarSort();

        when: "'Cancel' clicked"
        sortContentDialog.clickOnCancelButton();

        then:
        !sortContentDialog.isPresent();
    }

    def "GIVEN sort dialog opened WHEN 'Cancel' on top button clicked THEN dialog disappears"()
    {
        given: "one selected content"
        findAndSelectContent( IMPORTED_FOLDER_NAME )
        SortContentDialog sortContentDialog = contentBrowsePanel.clickToolbarSort();

        when: "'Cancel' on top clicked"
        sortContentDialog.clickOnCancelOnTop();

        then: "dialog disappears"
        !sortContentDialog.isPresent();
    }

    def "GIVEN folder selected and 'Sort' button clicked WHEN TabMenuButton clicked THEN five menu items appears"()
    {
        given: "one selected content"
        findAndSelectContent( IMPORTED_FOLDER_NAME )
        SortContentDialog sortContentDialog = contentBrowsePanel.clickToolbarSort();

        when:
        sortContentDialog.clickOnTabMenu();
        List<String> items = sortContentDialog.getMenuItems();

        then:
        items.size() == 5;
        and:
        items.contains( SortMenuItem.DNAME_ASCENDING.getValue() );
        and:
        items.contains( SortMenuItem.DNAME_DESCENDING.getValue() );
        and:
        items.contains( SortMenuItem.MODIFIED_ASCENDING.getValue() );
        and:
        items.contains( SortMenuItem.MODIFIED_DESCENDING.getValue() );
        and:
        items.contains( SortMenuItem.MANUALLY_SORTED.getValue() );
    }

    def "GIVEN sort dialog opened WHEN  'cancel' button on the top clicked THEN dialog disappears"()
    {
        given: "content selected and 'Sort' pressed"
        findAndSelectContent( IMPORTED_FOLDER_NAME )
        SortContentDialog sortContentDialog = contentBrowsePanel.clickToolbarSort();

        when: "'Cancel' on top clicked"
        sortContentDialog.clickOnCancelOnTop();

        then: "dialog disappears"
        !sortContentDialog.isPresent();
    }

    def "WHEN sort dialog opened THEN default sorting is present"()
    {
        given: "folder added at root"
        Content folderContent = buildFolderContent( "folder", "sort_test" );
        addContent( folderContent );

        when: "content selected and 'Sort' dialog opened"
        findAndSelectContent( folderContent.getName() );
        SortContentDialog sortContentDialog = contentBrowsePanel.clickToolbarSort();
        TestUtils.saveScreenshot( getSession(), "default_sorting" )

        then: "default sorting present in the dialog"
        sortContentDialog.getCurrentSortingName() == SortMenuItem.MODIFIED_DESCENDING.getValue();
    }

}
