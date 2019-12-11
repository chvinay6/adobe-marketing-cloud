package io.ecx.aem.aemsp.core.utils;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.ArticlePageModel;
import io.ecx.aem.aemsp.core.slingmodels.CommercialPageModel;
import io.ecx.aem.aemsp.core.slingmodels.OfferPageModel;
import io.ecx.cq.common.core.CqExceptionHandler;

public class ArticleListUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(ArticleListUtils.class);

    private static final String ARTICLE_LIST_TYPE = "articleList";
    private static final String COMMERCAIL_LIST_TYPE = "commercialList";
    private static final String OFFER_LIST_TYPE = "offerList";
    public static final int DEFAULT_LIST_MAX_ITEMS = 5;

    public static String generateListJsonResponse(final String listType, final int maxNumber, final String listResource, final ResourceResolver resolver, final String[] filter)
    {
        String result = StringUtils.EMPTY;

        if (ARTICLE_LIST_TYPE.equals(listType))
        {
            result = generateArticleList(maxNumber, listResource, resolver, filter);
        }
        else if (COMMERCAIL_LIST_TYPE.equals(listType))
        {
            result = generateCommercialList(maxNumber, listResource, resolver, filter);
        }
        else if (OFFER_LIST_TYPE.equals(listType))
        {
            result = generateOfferList(maxNumber, listResource, resolver, filter);
        }

        return result;
    }

    private static String generateArticleList(final int maxNumber, final String searchPath, final ResourceResolver resolver, final String[] filter)
    {
        final int hasMore = maxNumber + 1;
        final NodeIterator breakingNewsNodes = ArticleUtils.getArticleNodes(hasMore, QueryUtils.generateBreakingNewsSearchQuery(GlobalConstants.ARTICLE_PAGE_RESOURCETYPE, searchPath, filter), resolver);
        final NodeIterator articleNodes = ArticleUtils.getArticleNodes(hasMore - (int) breakingNewsNodes.getSize(), QueryUtils.generateArticleSearchWithoutBreakingQuery(GlobalConstants.ARTICLE_PAGE_RESOURCETYPE, searchPath, filter), resolver);
        final List<ArticlePageModel> articleList = new ArrayList<>();
        addArticleModelToList(articleList, breakingNewsNodes, true, resolver);
        addArticleModelToList(articleList, articleNodes, false, resolver);

        boolean hasMoreBoolean = false;
        if (articleList.size() == hasMore)
        {
            articleList.remove(articleList.size() - 1);
            hasMoreBoolean = true;
        }

        return generateJsonResponse(articleList, hasMoreBoolean);
    }

    private static String generateCommercialList(final int maxNumber, final String searchPath, final ResourceResolver resolver, final String[] filter)
    {
        final int hasMore = maxNumber + 1;
        final NodeIterator commercialNodes = ArticleUtils.getArticleNodes(hasMore, QueryUtils.generateArticleSearchQuery(GlobalConstants.COMMERCIAL_PAGE_RESOURCETYPE, searchPath, GlobalConstants.COMMERCIAL_DATE_PROPERTY, filter), resolver);
        final List<CommercialPageModel> commercialList = new ArrayList<>();
        addCommercialModelToList(commercialList, commercialNodes, resolver);

        boolean hasMoreBoolean = false;
        if (commercialList.size() == hasMore)
        {
            commercialList.remove(commercialList.size() - 1);
            hasMoreBoolean = true;
        }

        return generateCommercialJsonResponse(commercialList, hasMoreBoolean);
    }

    private static String generateOfferList(final int maxNumber, final String searchPath, final ResourceResolver resolver, final String[] filter)
    {
        final int hasMore = maxNumber + 1;
        final NodeIterator offerNodes = ArticleUtils.getArticleNodes(hasMore, QueryUtils.generateOfferSearchQuery(GlobalConstants.OFFER_PAGE_RESOURCETYPE, searchPath, filter), resolver);
        final List<OfferPageModel> offerList = new ArrayList<>();
        addOfferModelToList(offerList, offerNodes, resolver);

        boolean hasMoreBoolean = false;
        if (offerList.size() == hasMore)
        {
            offerList.remove(offerList.size() - 1);
            hasMoreBoolean = true;
        }

        return generateOfferJsonResponse(offerList, hasMoreBoolean);
    }

    private static void addArticleModelToList(final List<ArticlePageModel> list, final NodeIterator iterator, final boolean checkBreakingNews, final ResourceResolver resolver)
    {
        while (iterator != null && iterator.hasNext())
        {
            try
            {
                final ArticlePageModel model = resolver.getResource(iterator.nextNode().getParent().getPath()).adaptTo(ArticlePageModel.class);
                if (model != null && checkBreakingNews == model.isBreakingNews())
                {
                    list.add(model);
                }
            }
            catch (final RepositoryException e)
            {
                CqExceptionHandler.handleException(e, LOG);
            }
        }
    }

    private static void addCommercialModelToList(final List<CommercialPageModel> list, final NodeIterator iterator, final ResourceResolver resolver)
    {
        while (iterator != null && iterator.hasNext())
        {
            try
            {
                final CommercialPageModel model = resolver.getResource(iterator.nextNode().getParent().getPath()).adaptTo(CommercialPageModel.class);
                if (model != null)
                {
                    list.add(model);
                }
            }
            catch (final RepositoryException e)
            {
                CqExceptionHandler.handleException(e, LOG);
            }
        }
    }

    private static void addOfferModelToList(final List<OfferPageModel> list, final NodeIterator iterator, final ResourceResolver resolver)
    {
        while (iterator != null && iterator.hasNext())
        {
            try
            {
                final OfferPageModel model = resolver.getResource(iterator.nextNode().getParent().getPath()).adaptTo(OfferPageModel.class);
                if (model != null)
                {
                    list.add(model);
                }
            }
            catch (final RepositoryException e)
            {
                CqExceptionHandler.handleException(e, LOG);
            }
        }
    }

    public static String generateJsonResponse(final List<ArticlePageModel> articleList, final boolean moreResults)
    {
        String result = StringUtils.EMPTY;
        try
        {
            final JSONObject json = new JSONObject();
            json.put("moreResultsAvailable", moreResults);

            final JSONArray jsonArray = new JSONArray();
            for (final ArticlePageModel article : articleList)
            {
                jsonArray.put(article.getArticleListJson());
            }

            json.put("results", jsonArray);
            result = json.toString();
        }
        catch (final JSONException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return result;
    }

    private static String generateCommercialJsonResponse(final List<CommercialPageModel> commercialList, final boolean moreResults)
    {
        String result = StringUtils.EMPTY;
        try
        {
            final JSONObject commercialJson = new JSONObject();
            commercialJson.put("moreResultsAvailable", moreResults);

            final JSONArray jsonArray = new JSONArray();
            for (final CommercialPageModel article : commercialList)
            {
                jsonArray.put(article.getCommercialListJson());
            }

            commercialJson.put("results", jsonArray);
            result = commercialJson.toString();
        }
        catch (final JSONException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return result;
    }

    private static String generateOfferJsonResponse(final List<OfferPageModel> commercialList, final boolean moreResults)
    {
        String result = StringUtils.EMPTY;
        try
        {
            final JSONObject commercialJson = new JSONObject();
            commercialJson.put("moreResultsAvailable", moreResults);

            final JSONArray jsonArray = new JSONArray();
            for (final OfferPageModel article : commercialList)
            {
                jsonArray.put(article.getOfferListJson());
            }

            commercialJson.put("results", jsonArray);
            result = commercialJson.toString();
        }
        catch (final JSONException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return result;
    }
}