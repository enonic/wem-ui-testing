package com.enonic.wem.uitest.content

import com.enonic.autotests.pages.contentmanager.browsepanel.ContentBrowsePanel
import com.enonic.autotests.services.ContentService
import com.enonic.autotests.vo.contentmanager.*
import com.enonic.wem.uitest.BaseGebSpec
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class BrowsePanelSpec extends BaseGebSpec 
{
	@Shared String REPONAME = "test-folder";
	@Shared String FULL_REPONAME = "/"+REPONAME;

	@Shared ContentService contentService = new ContentService();

	def "Given BrowsePanel When adding Folder to root  Then the content should be listed in the table"() 
	{		
		given:
		go "admin"		
	
		when:
		BaseAbstractContent content = FolderContent.builder().withName(REPONAME).withDisplayName(REPONAME).build();
		contentService.addContent(getTestSession(), content, true)

		then:
		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
		grid.findContentInTable(content, 2l)
	}

//	def "Given content BrowsePanel and existing content When content deleted Then the content should not be listed in the table"() 
//	{		
//		given:
//		go "admin"
//		String name = "delete-content"+Math.abs( new Random().nextInt() );
//		StructuredContent content = StructuredContent.builder().withName(name).withDisplayName("content-to-delete").build();
//		String[] parent = [FULL_REPONAME]
//		content.setParentNames(parent);
//		contentService.addContent(getTestSession(), content, true);
//
//		when:
//		List<BaseAbstractContent> contents = new ArrayList<>();
//		contents.add(content);
//		ContentBrowsePanel grid = contentService.deleteContentUseToolbar(getTestSession(), contents);
//
//		then:
//		!grid.findContentInTable(content, 2l)
//	}


	def "Given BrowsePanel When adding Folder content Then the content should be listed in the table"() 
	{
		given:
		go "admin"
		BaseAbstractContent content = FolderContent.builder().withName("foldercontent").withDisplayName("folder").build();
		String[] parent = [FULL_REPONAME]
		content.setParentNames(parent)

		when:
		contentService.addContent(getTestSession(), content, true)
		
		then:
		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
		grid.findContentInTable(content, 2l)
	}

	def "Given BrowsePanel When adding Structured content Then the content should be listed in the table"() 
	{
		given:
		go "admin"

		BaseAbstractContent content = StructuredContent.builder().withName("structuredcontent").withDisplayName("structured").build();
		String[] parent = [FULL_REPONAME]
		content.setParentNames(parent)

		when:
		contentService.addContent(getTestSession(), content, true)

		then:
		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
		grid.findContentInTable(content, 2l)
	}

	def "Given BrowsePanel When adding Unstructured content Then the content should be listed in the table"() 
	{
		given:
		go "admin"
		BaseAbstractContent content = UnstructuredContent.builder().withName("unstructuredcontent").withDisplayName("unstructured").build();
		String[] parent = [FULL_REPONAME]
		content.setParentNames(parent)

		when:
		contentService.addContent(getTestSession(), content, true)

		then:
		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
		grid.findContentInTable(content, 2l)
	}

	def "Given BrowsePanel When adding Archive content Then the content should be listed in the table"() 
	{
		given:
		go "admin"
		BaseAbstractContent content = ArchiveContent.builder().withName("archivecontent").withDisplayName("archive").build();
		String[] parent = [FULL_REPONAME]
		content.setParentNames(parent)

		when:
		contentService.addContent(getTestSession(), content, true)

		then:
		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
		grid.findContentInTable(content, 2l)
	}

	def "Given BrowsePanel When adding Media content Then the content should be listed in the table"() 
	{
		given:
		go "admin"
		BaseAbstractContent content = MediaContent.builder().withName("mediacontent").withDisplayName("media").build();
		String[] parent = [FULL_REPONAME]
		content.setParentNames(parent)

		when:
		contentService.addContent(getTestSession(), content, true)


		then:
		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
		grid.findContentInTable(content, 2l)
	}

	def "Given BrowsePanel When adding Data content Then the content should be listed in the table"() 
	{
		given:
		go "admin"
		BaseAbstractContent content = DataContent.builder().withName("datacontent").withDisplayName("data").build();
		String[] parent = [FULL_REPONAME]
		content.setParentNames(parent)

		when:
		contentService.addContent(getTestSession(), content, true)


		then:
		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
		grid.findContentInTable(content, 2l)
	}


	def "Given BrowsePanel When adding Text content Then the content should be listed in the table"() 
	{
		given:
		go "admin"
		BaseAbstractContent content = TextContent.builder().withName("textcontent").withDisplayName("text").build();
		String[] parent = [FULL_REPONAME]
		content.setParentNames(parent)

		when:
		contentService.addContent(getTestSession(), content, true)

		then:
		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
		grid.findContentInTable(content, 2l)
	}
	def "Given BrowsePanel When adding Page content Then the content should be listed in the table"() 
	{
		given:
		go "admin"
		BaseAbstractContent content = PageContent.builder().withName("pagecontent").withDisplayName("page").build();
		String[] parent = [FULL_REPONAME]
		content.setParentNames(parent)

		when:
		contentService.addContent(getTestSession(), content, true)

		then:
		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
		grid.findContentInTable(content, 2l)
	}

	def "Given BrowsePanel When adding Shortcut content Then the content should be listed in the table"() 
	{
		given:
		go "admin"
		BaseAbstractContent content = ShortcutContent.builder().withName("shortcutcontent").withDisplayName("shortcut").build();
		String[] parent = [FULL_REPONAME]
		content.setParentNames(parent)

		when:
		contentService.addContent(getTestSession(), content, true)

		then:
		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
		grid.findContentInTable(content, 2l)
	}

	//TODO ISSUE with 'Edit' button

	//	def "Given BrowsePanel and exist content  When content editet, name changed  Then the content whit new name should be listed in the table"()
	//	{
	//		given:
	//		go "admin"
	//		String name = "editname"
	//		StructuredContent contentToEdit = StructuredContent.builder().withName(name).withDisplayName("edittest").build();
	//		String[] parent = [FULL_REPONAME]
	//		contentToEdit.setParentNames(parent);
	//		contentService.addContent(getTestSession(), contentToEdit, true)
	//
	//
	//		when:
	//		String newName = "edited" + Math.abs(new Random().nextInt());
	//		StructuredContent newcontent = StructuredContent.builder().withName(newName).withDisplayName("edited").build();
	//		newcontent.setParentNames(parent);
	//		contentService.doOpenContentAndEdit(getTestSession(), contentToEdit, newcontent);
	//
	//		then:
	//		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
	//		grid.findContentInTable(newcontent, 2l)
	//
	//
	//	}
	//
	//	def "Given BrowsePanel and exist content  When content editet, display name changed  Then the content whit new display-name should be listed in the table"()
	//	{
	//		given:
	//		go "admin"
	//
	//		String name = "editdisplayname"
	//		StructuredContent contentToEdit = StructuredContent.builder().withName(name).withDisplayName("edittest").build();
	//		String[] parent = [FULL_REPONAME]
	//		contentToEdit.setParentNames(parent);
	//		contentService.addContent(getTestSession(), contentToEdit, true)
	//		String newDisplayName = "edited" + Math.abs(new Random().nextInt());
	//		StructuredContent newcontent = StructuredContent.builder().withName(name).withDisplayName(newDisplayName).build();
	//		newcontent.setParentNames(parent);
	//
	//		when:
	//		contentService.doOpenContentAndEdit(getTestSession(), contentToEdit, newcontent);
	//
	//		then:
	//		ContentBrowsePanel grid = new ContentBrowsePanel(getTestSession())
	//		grid.findContentInTable(newcontent, 2l)
	//
	//
	//	}
	//


}
