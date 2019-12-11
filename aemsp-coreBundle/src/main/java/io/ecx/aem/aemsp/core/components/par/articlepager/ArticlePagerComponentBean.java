package io.ecx.aem.aemsp.core.components.par.articlepager;

import java.util.Date;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.ArticlePageModel;
import io.ecx.aem.aemsp.core.utils.DateUtils;
import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class ArticlePagerComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ArticlePagerComponentBean.class);
    private static String DATEFORMAT_DATE_ONLY = "dd.MM.yyyy - HH:mm";

    private ArticlePagerVO nextArticle;
    private ArticlePagerVO previousArticle;

    @Override
    protected void init() throws Exception
    {
        final ResourceResolver resourceResolver = getResourceResolver();
        final Session session = resourceResolver.adaptTo(Session.class);
        final ArticlePageModel currentArticle = getResourcePage().adaptTo(ArticlePageModel.class);
        final String currentArticlePath = getResourcePage().getPath();
        final String mainCategoryPath = getMainCategoryPath(getCurrentPage());

        final String queryPreviousArticle;
        final String queryNextArticle;

        final boolean isRunmodePublish = isRunmodePublish();
        if (isRunmodePublish)
        {
            final String publishDate = DateUtils.getCalendarStringFromDate(currentArticle.getPublishDate());
            queryPreviousArticle = generateQueryString(mainCategoryPath, currentArticlePath, publishDate, GlobalConstants.ARTICLE_PUBLISH_DATE_PROPERTY, true);
            queryNextArticle = generateQueryString(mainCategoryPath, currentArticlePath, publishDate, GlobalConstants.ARTICLE_PUBLISH_DATE_PROPERTY, false);
        }
        else
        {
            final String createdDate = DateUtils.getCalendarStringFromDate(currentArticle.getCreated());
            queryPreviousArticle = generateQueryString(mainCategoryPath, currentArticlePath, createdDate, JcrConstants.JCR_CREATED, true);
            queryNextArticle = generateQueryString(mainCategoryPath, currentArticlePath, createdDate, JcrConstants.JCR_CREATED, false);
        }

        final QueryManager queryManager = session.getWorkspace().getQueryManager();
        previousArticle = executeQuery(queryManager, queryPreviousArticle, isRunmodePublish);
        nextArticle = executeQuery(queryManager, queryNextArticle, isRunmodePublish);

    }

    private ArticlePagerVO executeQuery(final QueryManager queryManager, final String queryString, final boolean isPublishRunmode) throws RepositoryException
    {
        ArticlePagerVO result = null;
        final Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);
        query.setLimit(1);
        final NodeIterator queryResult = query.execute().getNodes();
        if (queryResult.hasNext())
        {
            final Node article = queryResult.nextNode();
            result = new ArticlePagerVO(getPublishOrCreatedDate(isPublishRunmode, article), article.getProperty(GlobalConstants.ARTICLE_MAINHEADLINE_PROPERTY).getString(), TextUtils.appendStringObjects(article.getParent().getPath(), GlobalConstants.HTML_EXTENSION));
        }

        return result;
    }

    private String generateQueryString(final String categoryPath, final String articlePath, final String publishDate, final String dateProperty, final boolean previous)
    {
        return TextUtils.appendStringObjects("SELECT * FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE([", categoryPath, "]) AND NOT ISDESCENDANTNODE([", articlePath, "]) AND page.[", ResourceResolver.PROPERTY_RESOURCE_TYPE, "] = '", GlobalConstants.ARTICLE_PAGE_RESOURCETYPE, "' AND page.[", dateProperty, "]", previous ? " < " : " > ", "CAST('", publishDate, "' AS DATE) ORDER BY page.[", dateProperty, "] ", previous ? "DESC" : "ASC");
    }

    private String getPublishOrCreatedDate(boolean publish, final Node node) throws RepositoryException
    {
        Date date;
        if (publish && node.hasProperty(GlobalConstants.ARTICLE_PUBLISH_DATE_PROPERTY))
        {
            date = node.getProperty(GlobalConstants.ARTICLE_PUBLISH_DATE_PROPERTY).getDate().getTime();
        }
        else
        {
            date = node.getProperty(JcrConstants.JCR_CREATED).getDate().getTime();
        }

        return DateUtils.formatDateToString(date, DATEFORMAT_DATE_ONLY, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
    }

    private String getMainCategoryPath(final Page currentPage)
    {
        return currentPage.getAbsoluteParent(GlobalConstants.CATEGORY_PAGE_DEPTH).getPath();
    }

    private boolean isRunmodePublish() throws Exception
    {
        final SlingSettingsService settings = getService(SlingSettingsService.class);
        return ResourceResolverUtils.isRunmodePublish(settings);
    }

    public ArticlePagerVO getNextArticle()
    {
        return nextArticle;
    }

    public ArticlePagerVO getPreviousArticle()
    {
        return previousArticle;
    }
}
