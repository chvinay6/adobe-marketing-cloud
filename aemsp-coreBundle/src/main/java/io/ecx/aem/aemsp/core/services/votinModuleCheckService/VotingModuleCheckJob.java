package io.ecx.aem.aemsp.core.services.votinModuleCheckService;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.aem.aemsp.core.utils.VotingUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

public class VotingModuleCheckJob implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(VotingModuleCheckJob.class);
    private ResourceResolverFactory resResolverFactory;

    @Override
    public void run()
    {
        try
        {
            LOG.info("Start - Voting Module Check Job.");
            votingModuleChecker();
        }
        catch (final Exception exception)
        {
            CqExceptionHandler.handleException(exception, "Could not run Asset Activation job - ", LOG);
        }
        finally
        {
            LOG.info("End - Voting Module Check Job.");
        }
    }

    private void votingModuleChecker() throws LoginException
    {
        final ResourceResolver resResolver = ResourceResolverUtils.getWriteService(resResolverFactory);

        try
        {
            final Session session = resResolver.adaptTo(Session.class);
            final Resource userGeneratedResource = resResolver.getResource(VotingUtils.USERGENERATED_PATH);
            if (userGeneratedResource != null)
            {
                final Iterator<Page> votingPages = userGeneratedResource.adaptTo(Page.class).listChildren();
                while (votingPages.hasNext())
                {
                    checkVotingModulePage(votingPages.next(), resResolver);
                }

                session.save();
            }
        }
        catch (final Exception exception)
        {
            CqExceptionHandler.handleException(exception, LOG);
        }
        finally
        {
            ResourceResolverUtils.closeResourceResolver(resResolver);
        }
    }

    private void checkVotingModulePage(final Page page, final ResourceResolver resResolver) throws RepositoryException
    {
        final ValueMap vmap = page.getContentResource().getValueMap();
        if (vmap != null)
        {
            final String path = vmap.get("path", StringUtils.EMPTY);
            if (StringUtils.isNotBlank(path))
            {
                final Resource votingPage = resResolver.getResource(path);
                if (votingPage == null)
                {
                    page.adaptTo(Node.class).remove();
                }
            }
        }
    }

    public void setResResolverFactory(final ResourceResolverFactory resResolverFactory)
    {
        this.resResolverFactory = resResolverFactory;
    }

}
