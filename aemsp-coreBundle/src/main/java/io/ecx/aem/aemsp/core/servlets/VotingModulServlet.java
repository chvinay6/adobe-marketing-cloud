package io.ecx.aem.aemsp.core.servlets;

import java.io.IOException;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.NameConstants;

import io.ecx.aem.aemsp.core.components.par.votingmodule.VotingAnswerVO;
import io.ecx.aem.aemsp.core.slingmodels.VotingModel;
import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.aem.aemsp.core.utils.VotingUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

/**
 * @author i.mihalina
 *
 */
@SlingServlet(resourceTypes = "sling/servlet/default", paths = "/servlets/voting", selectors = { "voting" }, extensions = { "json" })
public class VotingModulServlet extends SlingAllMethodsServlet
{
    private static final long serialVersionUID = 3181885020323402699L;
    private static final Logger LOG = LoggerFactory.getLogger(VotingModulServlet.class);

    private static final String PAGE_CONTENT_TYPE = "cq:PageContent";
    private static final String CQ_DISTRIBUTE_TYPE = "cq:distribute";

    @Reference
    protected SlingSettingsService settings;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            response.setContentType("application/json");
            response.setCharacterEncoding(CharEncoding.UTF_8);

            JSONObject jsonResponse = new JSONObject();
            final ResourceResolver resolver = request.getResourceResolver();
            final VotingModel model = VotingUtils.getVotingModel(request.getResource(), resolver);
            if (model != null)
            {
                jsonResponse = generateJsonResponse(model.getUniqueId(), resolver);
            }

            final String jsonResponseString = jsonResponse.toString();
            response.getWriter().write(jsonResponseString);

        }
        catch (final Exception ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
        }
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            if (ResourceResolverUtils.isRunmodePublish(settings))
            {
                final ResourceResolver resolver = ResourceResolverUtils.getWriteService(resourceResolverFactory);
                final String path = request.getParameter("path");

                if (StringUtils.isNotBlank(path))
                {
                    final VotingModel model = VotingUtils.getVotingModel(resolver.getResource(path), resolver);
                    saveVoteToRepository(model, resolver, request.getParameterValues("vote"));
                }
            }
        }
        catch (final Exception ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
        }
    }

    private void saveVoteToRepository(final VotingModel model, final ResourceResolver resolver, final String[] votes) throws RepositoryException
    {
        if (votes != null && votes.length > 0)
        {
            final Session session = resolver.adaptTo(Session.class);
            final Node votingNode = getVotingRepositoryNode(model, resolver, session);
            for (final String vote : votes)
            {
                saveVote(votingNode, vote, session);
            }
        }
    }

    private JSONObject generateJsonResponse(final String uuid, final ResourceResolver resolver) throws RepositoryException, JSONException
    {
        JSONObject result = new JSONObject();
        if (StringUtils.isNotBlank(uuid))
        {
            final Resource votingResource = resolver.getResource(TextUtils.appendStringObjects(VotingUtils.USERGENERATED_PATH, uuid));
            if (votingResource != null)
            {
                final Node votingNode = votingResource.adaptTo(Node.class);
                final float number = VotingUtils.getVotingCountNumber(votingNode, false);
                result = calculateVotingResults(result, votingNode, number);
            }
        }

        return result;
    }

    private JSONObject calculateVotingResults(final JSONObject result, final Node votingNode, final float number) throws RepositoryException, JSONException
    {
        final NodeIterator answers = votingNode.getNodes();
        final JSONArray results = new JSONArray();
        int sum = 0;
        while (answers.hasNext())
        {
            final Node answerNode = answers.nextNode();
            if (answerNode.hasNode(JcrConstants.JCR_CONTENT))
            {
                final Node answerContent = answerNode.getNode(JcrConstants.JCR_CONTENT);
                final int percentage = VotingUtils.getAnswerPercentage(!answers.hasNext(), answerContent.getProperty(VotingUtils.COUNT_TEXT).getLong(), sum, number);
                sum += percentage;
                results.put(new JSONObject().put(VotingUtils.VALUE_TEXT, percentage).put("label", answerContent.getProperty(VotingUtils.VALUE_TEXT).getString()));
            }
        }

        return result.put("results", results);

    }

    private void saveVote(final Node votingNode, final String vote, final Session session) throws RepositoryException
    {
        final NodeIterator answers = votingNode.getNodes();
        while (answers.hasNext())
        {
            final Node answer = answers.nextNode();
            if (answer.hasNode(JcrConstants.JCR_CONTENT))
            {
                final Node contentNode = answer.getNode(JcrConstants.JCR_CONTENT);
                if (StringUtils.equals(vote, contentNode.getProperty(VotingUtils.VALUE_TEXT).getString()))
                {
                    contentNode.setProperty(VotingUtils.COUNT_TEXT, contentNode.getProperty(VotingUtils.COUNT_TEXT).getLong() + 1);
                    saveNodeForReverseReplication(contentNode, session);
                    break;
                }
            }
        }
    }

    private Node getVotingRepositoryNode(final VotingModel model, final ResourceResolver resolver, final Session session) throws RepositoryException
    {
        Node result = null;
        final Resource votingModuleResource = resolver.getResource(VotingUtils.USERGENERATED_PATH + model.getUniqueId());
        if (votingModuleResource == null)
        {
            result = createVotingModuleNode(model, session);
        }
        else
        {
            result = votingModuleResource.adaptTo(Node.class);
        }

        return result;
    }

    private Node createVotingModuleNode(final VotingModel model, final Session session) throws RepositoryException
    {
        final Node votingNode = JcrUtil.createPath(TextUtils.appendStringObjects(VotingUtils.USERGENERATED_PATH, model.getUniqueId()), NameConstants.NT_PAGE, session);
        final Node contentNode = votingNode.addNode(JcrConstants.JCR_CONTENT);
        contentNode.setProperty("question", model.getQuestion());
        contentNode.setProperty("path", model.getPath());
        saveNodeForReverseReplication(contentNode, session);

        int i = 1;
        for (final VotingAnswerVO answer : model.getAnswerList())
        {
            final Node answerNode = votingNode.addNode(String.valueOf(i));
            answerNode.setPrimaryType(NameConstants.NT_PAGE);
            final Node content = answerNode.addNode(JcrConstants.JCR_CONTENT);
            content.setPrimaryType(PAGE_CONTENT_TYPE);
            content.setProperty(VotingUtils.VALUE_TEXT, answer.getAnswer());
            content.setProperty(VotingUtils.COUNT_TEXT, answer.getStartValue());
            content.setProperty(VotingUtils.START_VALUE, answer.getStartValue());
            saveNodeForReverseReplication(content, session);
            i++;
        }

        return votingNode;
    }

    private void saveNodeForReverseReplication(final Node node, final Session session) throws RepositoryException
    {
        session.save();
        node.setProperty(CQ_DISTRIBUTE_TYPE, true);
        node.setProperty(NameConstants.PN_PAGE_LAST_MOD, Calendar.getInstance());
        node.setProperty(NameConstants.PN_PAGE_LAST_MOD_BY, session.getUserID());
        session.save();
        node.setProperty(CQ_DISTRIBUTE_TYPE, false);
        session.save();
    }

}
