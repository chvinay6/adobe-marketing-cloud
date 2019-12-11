package io.ecx.aem.aemsp.core.utils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.VotingModel;

/**
 * i.mihalina
 */
public final class VotingUtils
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(VotingUtils.class);
    public static final String VOTING_MODULE_PROPERTY = "votingmodule";
    public static final String VALUE_TEXT = "value";
    public static final String COUNT_TEXT = "count";
    public static final String START_VALUE = "startValue";
    public static final String USERGENERATED_PATH = "/content/usergenerated/votingmodule/";

    private VotingUtils()
    {

    }

    public static VotingModel getVotingModel(final Resource currentResource, final ResourceResolver resolver)
    {
        VotingModel result = null;

        if (currentResource != null)
        {
            final Resource votingModuleRes = getVotingModuleResource(currentResource, resolver);
            if (votingModuleRes != null)
            {
                result = votingModuleRes.adaptTo(VotingModel.class);
            }
        }

        return result;
    }

    public static float getVotingCountNumber(final Node votingNode, final boolean startValue) throws RepositoryException
    {
        long count = 0;
        final NodeIterator answers = votingNode.getNodes();
        while (answers.hasNext())
        {
            final Node answerNode = answers.nextNode();
            if (answerNode.hasNode(JcrConstants.JCR_CONTENT))
            {
                final Node answerContent = answerNode.getNode(JcrConstants.JCR_CONTENT);
                count += answerContent.getProperty(COUNT_TEXT).getLong();
                if (startValue)
                {
                    count = count - answerContent.getProperty(START_VALUE).getLong();
                }
            }
        }

        return 100 / (float) count;
    }

    private static Resource getVotingModuleResource(final Resource currentResource, final ResourceResolver resolver)
    {
        Resource result = null;

        final PageManager pageManager = resolver.adaptTo(PageManager.class);
        final Page currentPage = pageManager.getContainingPage(currentResource.getPath());
        if (PageUtils.isPageTemplate(currentPage, GlobalConstants.VOTING_MODULE_PAGE_TEMPLATE))
        {
            result = currentResource;
        }
        else
        {
            final Page languagePage = currentPage.getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
            final String votingModulePath = languagePage.getProperties().get(GlobalConstants.VOTING_MODULE_PATH_PROPERTY, String.class);
            if (StringUtils.isNotBlank(votingModulePath))
            {
                result = checkVotingModulePath(votingModulePath, resolver);
            }
        }

        return result;
    }

    private static Resource checkVotingModulePath(final String path, final ResourceResolver resolver)
    {
        Resource result = null;

        final Resource pathResource = resolver.getResource(TextUtils.appendStringObjects(path, "/", NameConstants.NN_CONTENT));
        if (pathResource != null && PageUtils.isResourceTemplate(pathResource, GlobalConstants.VOTING_MODULE_PAGE_TEMPLATE))
        {
            final Resource votingModuleRes = pathResource.getChild(VOTING_MODULE_PROPERTY);
            if (votingModuleRes != null)
            {
                result = votingModuleRes;
            }
        }

        return result;
    }

    public static int getAnswerPercentage(final boolean lastAnswer, final long countValue, final int sum, final float number) throws RepositoryException
    {
        int result = 0;
        result = Math.round(number * countValue);

        if (sum + result > 100 && lastAnswer)
        {
            result = result - 1;
        }

        return result;
    }

}
