package com.enonic.autotests.services;

import com.enonic.autotests.TestSession;
import com.enonic.autotests.pages.Page;
import com.enonic.autotests.pages.schemamanager.AddNewContentTypeWizard;
import com.enonic.autotests.pages.schemamanager.KindOfContentTypes;
import com.enonic.autotests.pages.schemamanager.SchemaGridPage;
import com.enonic.autotests.vo.schemamanger.ContentType;

public class ContentTypeService
{

	/**
	 * Navigates to SchemaManager application and create a new content type.
	 * 
	 * @param testSession
	 * @param contentType
	 * @param isCloseWizard
	 * @return {@link SchemaGridPage} instance if wizard was closed.
	 */
	public Page createContentType(TestSession testSession, ContentType contentType, boolean isCloseWizard)
	{
		SchemaGridPage schemaManagerPage = NavigatorHelper.openSchemaManager(testSession);
		schemaManagerPage.doAddContentType(contentType,isCloseWizard);

		if (isCloseWizard)
		{
			return schemaManagerPage;
			
		} else
		{
			return new AddNewContentTypeWizard(testSession);
		}

	}
	public AddNewContentTypeWizard openAddContentTypeWizard(TestSession testSession,KindOfContentTypes kind)
	{
		SchemaGridPage schemaManagerPage = NavigatorHelper.openSchemaManager(testSession);
		schemaManagerPage.doOpenAddNewTypeWizard(kind.getValue());
		return new AddNewContentTypeWizard(testSession);
	
	}
	
	/**
	 * Navigates to SchemaManager application select a content type in the table and delete it.
	 * 
	 * @param testSession
	 * @param contentTypeToDelete
	 * @return {@link SchemaGridPage} instance.
	 */
	public SchemaGridPage deleteContentType(TestSession testSession, ContentType contentTypeToDelete)
	{
		SchemaGridPage spacesPage = NavigatorHelper.openSchemaManager(testSession);

		spacesPage.doDeleteContentType(contentTypeToDelete);
		return spacesPage;
	}
	

	/**
	 * Navigates to SchemaManager application select a content type in the table and delete it.
	 * 
	 * @param testSession
	 * @param contentTypeToDelete
	 * @return {@link SchemaGridPage} instance.
	 */
	public SchemaGridPage editContentType(TestSession testSession, ContentType contentTypeToEdit, ContentType newContentType)
	{
		SchemaGridPage spacesPage = NavigatorHelper.openSchemaManager(testSession);

		spacesPage.doEditContentType(contentTypeToEdit, newContentType);
		return spacesPage;
	}
}
