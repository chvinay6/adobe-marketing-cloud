package io.ecx.aem.aemsp.core.listeners;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.ArticlePageModel;
import io.ecx.aem.aemsp.core.slingmodels.VotingModel;
import io.ecx.aem.aemsp.core.utils.ArticleUtils;
import io.ecx.aem.aemsp.core.utils.DateUtils;
import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.aem.aemsp.core.utils.VotingUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

/**
 * @author i.mihalina
 *
 */

@Component
@Service(value = JobConsumer.class)
@Property(name = JobConsumer.PROPERTY_TOPICS, value = ArticleActivationEventHandler.JOB_TOPIC)
public class ArticleActivationJob implements JobConsumer
{
    private static final Logger LOG = LoggerFactory.getLogger(ArticleActivationJob.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicator;

    @Reference
    protected SlingSettingsService settings;

    @Override
    public JobResult process(final Job job)
    {
        if (ResourceResolverUtils.isRunmodeAuthor(settings))
        {
            ResourceResolver resResolver = null;
            try
            {
                final String articlePath = (String) job.getProperty(ArticleActivationEventHandler.RESOURCE_PATH_PARAM);
                resResolver = ResourceResolverUtils.getWriteService(resourceResolverFactory);

                final Resource resource = resResolver.getResource(articlePath);
                if (ArticleUtils.checkIfArticlePage(resource))
                {
                    final ArticlePageModel articleModel = resource.adaptTo(ArticlePageModel.class);
                    saveArticlePublishData(articleModel, resource, resResolver);
                }
                else if (isVotingPage(resource))
                {
                    final Resource votingRes = resResolver.getResource(TextUtils.appendStringObjects(resource.getPath(), "/", NameConstants.NN_CONTENT, "/", VotingUtils.VOTING_MODULE_PROPERTY));
                    if (votingRes != null)
                    {
                        final VotingModel votingModel = votingRes.adaptTo(VotingModel.class);
                        replicateVotingModuleData(votingModel, resResolver);
                    }
                }

                LOG.info("Article Activation Job: {}", articlePath);
            }
            catch (LoginException | RepositoryException | ReplicationException ex)
            {
                CqExceptionHandler.handleException(ex, "Article Activation Job exception", LOG);
            }
            finally
            {
                ResourceResolverUtils.closeResourceResolver(resResolver);
            }
        }

        return JobResult.OK;

    }

    private void saveArticlePublishData(final ArticlePageModel articleModel, final Resource articleResource, final ResourceResolver resourceRes) throws RepositoryException, ReplicationException
    {
        final Session session = resourceRes.adaptTo(Session.class);
        final Node articleNode = articleResource.getChild(NameConstants.NN_CONTENT).adaptTo(Node.class);

        if (StringUtils.isBlank(articleModel.getArticlePublisher()))
        {
            articleNode.setProperty(GlobalConstants.ARTICLE_PUBLISHER_PROPERTY, articleModel.getLastReplicatedBy());
            articleNode.setProperty(GlobalConstants.ARTICLE_PUBLISHER_FULL_NAME_PROPERTY, ResourceResolverUtils.getAemUserFullName(articleModel.getLastReplicatedBy(), resourceRes));
        }
        else
        {
            articleNode.setProperty(GlobalConstants.ARTICLE_PUBLISHER_FULL_NAME_PROPERTY, ResourceResolverUtils.getAemUserFullName(articleModel.getArticlePublisher(), resourceRes));
        }

        if (articleModel.getPublishDate() == null)
        {
            articleNode.setProperty(GlobalConstants.ARTICLE_PUBLISH_DATE_PROPERTY, DateUtils.dateToCalendar(articleModel.getLastReplicated()));
        }

        session.save();
        replicator.replicate(session, ReplicationActionType.ACTIVATE, articleNode.getPath());
    }

    private boolean isVotingPage(final Resource votingResource)
    {
        boolean result = false;
        if (votingResource != null && NameConstants.NT_PAGE.equals(votingResource.getResourceType()))
        {
            final String template = votingResource.getChild(NameConstants.NN_CONTENT).getValueMap().get(NameConstants.NN_TEMPLATE, String.class);
            if (GlobalConstants.VOTING_MODULE_PAGE_TEMPLATE.equals(template))
            {
                result = true;
            }
        }

        return result;
    }

    private void replicateVotingModuleData(final VotingModel votingModel, final ResourceResolver resolver) throws ReplicationException
    {
        if (votingModel != null && StringUtils.isNotBlank(votingModel.getUniqueId()))
        {
            final String uniqueId = votingModel.getUniqueId();
            final Resource votingData = resolver.getResource(TextUtils.appendStringObjects(VotingUtils.USERGENERATED_PATH, uniqueId));
            if (votingData != null)
            {
                final Session session = resolver.adaptTo(Session.class);
                replicator.replicate(session, ReplicationActionType.ACTIVATE, votingData.getPath());
                final Iterator<Page> answerPage = votingData.adaptTo(Page.class).listChildren();
                while (answerPage.hasNext())
                {
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, answerPage.next().getPath());
                }

            }
        }
    }

}
