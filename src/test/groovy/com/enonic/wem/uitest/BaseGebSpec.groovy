package com.enonic.wem.uitest

import com.enonic.autotests.TestSession
import com.enonic.autotests.services.ContentService
import com.enonic.autotests.services.ContentTypeService
import com.enonic.autotests.utils.ContentPathHelper
import com.enonic.autotests.utils.NameHelper
import com.enonic.autotests.vo.contentmanager.BaseAbstractContent
import com.enonic.autotests.vo.contentmanager.FolderContent
import com.enonic.wem.api.content.ContentPath
import geb.spock.GebSpec
import spock.lang.Shared

class BaseGebSpec
    extends GebSpec
{
    @Shared
    Properties defaultProperties;

    @Shared
    TestSession session;

    @Shared
    ContentService contentService = new ContentService();

    @Shared
    ContentTypeService contentTypeService = new ContentTypeService();

    @Override
    def cleanup()
    {
        if ( session != null )
        {
            session.setLoggedIn( false )
        }

        resetBrowser();
    }

    def setupSpec()
    {
        String baseUrl = System.getProperty( "geb.build.baseUrl" );
        println baseUrl
        if ( baseUrl == null )
        {
            loadProperties();
        }

    }

    def setup()
    {
        String baseUrl = System.getProperty( "geb.build.baseUrl" );
        if ( baseUrl == null )
        {
            browser.baseUrl = defaultProperties.get( "base.url" )
        }
    }


    TestSession getTestSession()
    {
        println "    geTestSesion called!"
        if ( session == null )
        {
            println "creating new test session"
            session = new TestSession()
            session.setDriver( browser.driver )
            session.setIsRemote( false )
            println "testSession is" + session.getBaseUrl();
            println browser.baseUrl
        }
        return session
    }

    void setSessionBaseUrl( String navigationPath )
    {
        StringBuilder sb = new StringBuilder()
        sb.append( browser.baseUrl ).append( navigationPath )
        println "  buildUrl changed  now url is:  " + sb.toString()
        getTestSession().setBaseUrl( sb.toString() )
    }

    BaseAbstractContent addRootContentToBeDeleted()
    {
        String name = NameHelper.unqiueName( "deletecontent" );
        ContentPath cpath = ContentPathHelper.buildContentPath( null, name );
        BaseAbstractContent content = FolderContent.builder().withName( name ).withDisplayName( "contenttodelete" ).withContentPath(
            cpath ).build();

        contentService.addContent( getTestSession(), content, true )
        return content;
    }

    void loadProperties()
    {
        defaultProperties = new Properties()
        InputStream input = null

        try
        {

            input = new FileInputStream( "tests.properties" )

            // load a properties file
            defaultProperties.load( input );
            println defaultProperties.getProperty( "base.url" )

        }
        catch ( IOException ex )
        {
            ex.printStackTrace();
        }
        finally
        {
            if ( input != null )
            {
                try
                {
                    input.close()
                }
                catch ( IOException e )
                {
                    e.printStackTrace()
                }
            }
        }
    }
}
