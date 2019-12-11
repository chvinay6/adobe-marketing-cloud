package io.ecx.aem.aemsp.core.workflow;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.crx.JcrConstants;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;

@Component(immediate = true)
@Service
@Properties({ @Property(name = "service.description", value = { "Article Page Activation Approval Process" }), @Property(name = "chooser.label", value = { "Article Page Approval Participant Chooser" }) })
public class ActivationApproval implements ParticipantStepChooser
{
    private static final String CHIEF_EDITOR_PROPERTY = "chiefEditor";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public String getParticipant(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException
    {
        String result = item.getWorkflow().getInitiator();

        final WorkflowData workflowData = item.getWorkflowData();
        if (workflowData.getPayloadType().equals(GlobalConstants.TYPE_JCR_PATH))
        {
            final String articlePath = workflowData.getPayload().toString();

            try
            {
                final Session jcrSession = session.adaptTo(Session.class);
                final ResourceResolver resolver = ResourceResolverUtils.getWriteService(resourceResolverFactory);
                final Page page = getArticlePageFromWorkflow(articlePath, jcrSession, resolver);
                if (page != null)
                {
                    result = getChiefEditor(page, result);
                }
            }
            catch (final RepositoryException | LoginException e)
            {
                throw new WorkflowException(e.getMessage(), e);
            }
        }

        return result;
    }

    private String getChiefEditor(final Page article, final String defaultEditor)
    {
        String result = defaultEditor;

        final Page parentPage = article.getParent();
        if (parentPage.getDepth() == GlobalConstants.CATEGORY_PAGE_DEPTH)
        {
            result = parentPage.getContentResource().getValueMap().get(CHIEF_EDITOR_PROPERTY, result);
        }
        else
        {
            final ValueMap pageProps = parentPage.getProperties();
            if (GlobalConstants.CATEGORY_PAGE_TEMPLATE.equals(pageProps.get(NameConstants.NN_TEMPLATE, String.class)))
            {
                result = getChiefEditorFromCategory(pageProps, parentPage, result);
            }
            else
            {
                result = getChiefEditor(parentPage, result);
            }
        }

        return result;
    }

    private String getChiefEditorFromCategory(final ValueMap pageProps, final Page page, final String defaultEditor)
    {
        String result = defaultEditor;
        final String chiefEditor = pageProps.get(CHIEF_EDITOR_PROPERTY, String.class);
        if (StringUtils.isNotBlank(chiefEditor))
        {
            result = chiefEditor;
        }
        else
        {
            result = getChiefEditor(page, result);
        }

        return result;
    }

    private Page getArticlePageFromWorkflow(final String path, final Session session, final ResourceResolver resolver) throws RepositoryException, LoginException
    {
        Page result = null;
        final Node articleNode = (Node) session.getItem(path);
        if (articleNode != null)
        {
            final String nodeType = getNodeProperty(articleNode, JcrConstants.JCR_PRIMARYTYPE);
            final String template = getNodeProperty(articleNode, TextUtils.appendStringObjects(JcrConstants.JCR_CONTENT, "/", NameConstants.NN_TEMPLATE));
            if (NameConstants.NT_PAGE.equals(nodeType) && GlobalConstants.ARTICLE_PAGE_TEMPLATE.equals(template))
            {
                result = resolver.getResource(path).adaptTo(Page.class);
            }
        }

        return result;
    }

    private String getNodeProperty(final Node metadata, final String property) throws RepositoryException
    {
        String result = StringUtils.EMPTY;
        if (metadata.hasProperty(property))
        {
            result = metadata.getProperty(property).getString();
        }

        return result;
    }
}