package io.ecx.aem.aemsp.core.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.ArticlePageModel;
import io.ecx.cq.common.core.CqExceptionHandler;

public class ArticleUtils
{

    private static final Logger LOG = LoggerFactory.getLogger(ArticleUtils.class);

    public static final String ARTICLE_VALID = "VALID";
    public static final String I18N_TYPE_ERROR = "aemsp_articleNotValidTypeError";
    private static final String I18N_LANGUAGE_ERROR = "aemsp_articleNotValidLanguageError";
    private static final String I18N_PUBLISH_ERROR = "aemsp_articleNotValidPublishError";
    private static final int TEASER_TRUNCATE_MAX_LENGTH = 200;

    public static String isValidArticle(final Resource articleResource, final String currentLanguageNode, final I18n i18n) throws RepositoryException
    {
        String result = i18n.get(I18N_TYPE_ERROR);
        if (checkIfArticlePage(articleResource))
        {
            final Page article = articleResource.adaptTo(Page.class);
            final Page articleLanguagePage = article.getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
            final ArticlePageModel articleModel = article.adaptTo(ArticlePageModel.class);
            if (articleModel != null && StringUtils.equals(currentLanguageNode, articleLanguagePage.getName()))
            {
                if (articleModel.getPublishDate() == null)
                {
                    result = i18n.get(I18N_PUBLISH_ERROR);
                }
                else
                {
                    result = articleModel.getPublishDate().before(new Date()) ? ARTICLE_VALID : i18n.get(I18N_PUBLISH_ERROR);
                }
            }
            else
            {
                result = i18n.get(I18N_LANGUAGE_ERROR);
            }
        }

        return result;
    }

    public static boolean checkIfArticlePage(final Resource articleResource)
    {
        boolean result = false;
        if (articleResource != null && NameConstants.NT_PAGE.equals(articleResource.getResourceType()))
        {
            final String template = articleResource.getChild(NameConstants.NN_CONTENT).getValueMap().get(NameConstants.NN_TEMPLATE, String.class);
            if (GlobalConstants.ARTICLE_PAGE_TEMPLATE.equals(template))
            {
                result = true;
            }
        }

        return result;
    }

    public static List<Page> getPages(final int maxNumber, final String query, final ResourceResolver resolver)
    {
        final List<Page> articleList = new ArrayList<>();
        try
        {
            final PageManager pageManager = resolver.adaptTo(PageManager.class);
            final NodeIterator queryResult = getArticleNodes(maxNumber, query, resolver);
            while (queryResult != null && queryResult.hasNext())
            {
                final Page article = pageManager.getPage(queryResult.nextNode().getParent().getPath());
                if (article != null)
                {
                    articleList.add(article);
                }
            }
        }
        catch (final RepositoryException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return articleList;
    }

    public static NodeIterator getArticleNodes(final int maxNumber, final String queryString, final ResourceResolver resolver)
    {
        NodeIterator result = null;
        try
        {
            if (maxNumber > 0)
            {
                final QueryManager queryManager = resolver.adaptTo(Session.class).getWorkspace().getQueryManager();
                final Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);
                query.setLimit(maxNumber);

                result = query.execute().getNodes();
            }
        }
        catch (final RepositoryException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return result;
    }

    public static String truncateTeaserTextForList(final String teaserText)
    {
        String result = teaserText;
        if (result.length() > TEASER_TRUNCATE_MAX_LENGTH)
        {
            result = StringUtils.substring(result, 0, TEASER_TRUNCATE_MAX_LENGTH);
            result = StringUtils.substringBeforeLast(result, " ");
            result = TextUtils.appendStringObjects(result, "...");
        }
        return result;
    }

    public static String generateMostReadJsonResponse(final int pageSize, final String pagePath, final ResourceResolver resourceResolver)
    {
        final List<ArticlePageModel> articleList = new ArrayList<>();
        try
        {
            int articlesFound = 0;
            final NodeIterator mostReadIterator = getNodes(QueryUtils.generateNodeSearchQuery(AnalyticsUtils.getAnalyticsRelativePath(pagePath), AnalyticsUtils.ANALYTICS_METRICS_PROPERTY), resourceResolver);
            while (mostReadIterator.hasNext() && articlesFound < pageSize)
            {
                final Node mostReadNode = mostReadIterator.nextNode();
                if (mostReadNode.hasProperty(AnalyticsUtils.ANALYTICS_PAGEPATH_PROPERTY))
                {
                    final String articlePath = mostReadNode.getProperty(AnalyticsUtils.ANALYTICS_PAGEPATH_PROPERTY).getString();
                    final Resource articleResource = resourceResolver.getResource(articlePath);
                    if (articleResource != null)
                    {
                        articleList.add(articleResource.adaptTo(ArticlePageModel.class));
                        articlesFound++;
                    }
                }
            }
        }
        catch (final RepositoryException ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
        }
        return ArticleListUtils.generateJsonResponse(articleList, false);
    }

    public static NodeIterator getNodes(final String queryString, final ResourceResolver resolver) throws RepositoryException
    {
        final QueryManager queryManager = resolver.adaptTo(Session.class).getWorkspace().getQueryManager();
        final Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);
        return query.execute().getNodes();
    }
}
