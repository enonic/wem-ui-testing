package com.enonic.wem.selenium;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.enonic.autotests.services.ContentManagerService;

/**
 * 
 *
 */
public class ContentManagerTests extends BaseTest
{

	private ContentManagerService cManagerService = new ContentManagerService();
	
	private String repoName = "Bluman Trampoliner";

	@Test(description ="Opens 'Content  Manager' Application and verify title and controls elements")
	public void testVerifyCMApp()
	{
		logger.info("STARTED ##### opens a Content Manager Application and verify it ");
		boolean result = cManagerService.openContentManagerAppAndVerify(getTestSession());
		Assert.assertTrue(result);
		logger.info("Test Finished $$$$  Content Manager Application opened and verified");
	}

}
