package io.ecx.aem.aemsp.core.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.NameConstants;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.AnalyticsUtils;
import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

public class GoogleAnalyticsRequester implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsRequester.class);

    private final GoogleAnalyticsConfigurationVO googleAnalyticsConfiguration = new GoogleAnalyticsConfigurationVO();

    private Analytics analytics;

    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void run()
    {
        ResourceResolver resourceResolver = null;
        try
        {
            analytics = AnalyticsUtils.initializeAnalytics(googleAnalyticsConfiguration.getGserviceEmail(), googleAnalyticsConfiguration.getPrivateKey());
            resourceResolver = ResourceResolverUtils.getWriteService(resourceResolverFactory);
            AnalyticsUtils.deleteOldData(googleAnalyticsConfiguration.getRootPath(), resourceResolver);
            getMostReadArticlesPerMainCategory(resourceResolver);
        }
        catch (final Exception ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
        }
        finally
        {
            if (resourceResolver != null && resourceResolver.isLive())
            {
                resourceResolver.close();
            }
        }

    }

    private void getMostReadArticlesPerMainCategory(final ResourceResolver resourceResolver) throws RepositoryException, InvalidQueryException, ItemNotFoundException, AccessDeniedException, IOException
    {
        final NodeIterator categoryContents = getAllCategoryContentNodesOfMagazine(resourceResolver);
        while (categoryContents.hasNext())
        {
            getAndSaveAnalyticsDataForCategory(resourceResolver, categoryContents.nextNode().getParent());
        }
    }

    private NodeIterator getAllCategoryContentNodesOfMagazine(final ResourceResolver resourceResolver) throws RepositoryException, InvalidQueryException
    {
        final QueryManager queryManager = resourceResolver.adaptTo(Session.class).getWorkspace().getQueryManager();
        final String queryString = "SELECT * FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE([" + googleAnalyticsConfiguration.getRootPath() + "]) AND page.[" + NameConstants.NN_TEMPLATE + "]='" + GlobalConstants.CATEGORY_PAGE_TEMPLATE + "'";
        final Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);
        return query.execute().getNodes();
    }

    private void getAndSaveAnalyticsDataForCategory(final ResourceResolver resourceResolver, final Node currentCategory)
    {
        try
        {
            if (currentCategory.getDepth() - 1 == GlobalConstants.CATEGORY_PAGE_DEPTH) //GlobalConstants depth starts with 0
            {
                final List<List<String>> mostReadData = getAnalyticsDataForArticlesOfCategory(currentCategory.getName());
                saveMostReadData(mostReadData, resourceResolver);
            }
        }
        catch (final Exception ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
        }
    }

    private List<List<String>> getAnalyticsDataForArticlesOfCategory(final String categoryName) throws IOException, RepositoryException
    {
        final HashMap<String, String> requestParameters = getRequestParameters(categoryName);
        final GaData requestResult = AnalyticsUtils.getResults(analytics, googleAnalyticsConfiguration.getProfileId(), requestParameters);
        LOG.debug("Requested Analytics Data for {}: {}", categoryName, requestResult.toString());
        return requestResult.getRows();
    }

    private HashMap<String, String> getRequestParameters(final String categoryName)
    {
        final HashMap<String, String> result = new HashMap<>();
        result.put(AnalyticsUtils.KEY_METRICS, googleAnalyticsConfiguration.getMetrics());
        result.put(AnalyticsUtils.KEY_DIMENSIONS, googleAnalyticsConfiguration.getDimensions());
        result.put(AnalyticsUtils.KEY_FILTERS, TextUtils.appendStringObjects("ga:dimension1==articlepage;ga:pagePath=~", "^/", categoryName, ".*"));
        result.put(AnalyticsUtils.KEY_START_DATE, googleAnalyticsConfiguration.getStartDate());
        result.put(AnalyticsUtils.KEY_END_DATE, googleAnalyticsConfiguration.getEndDate());
        result.put(AnalyticsUtils.KEY_SORT, googleAnalyticsConfiguration.getSort());
        result.put(AnalyticsUtils.KEY_MAX_RESULTS, googleAnalyticsConfiguration.getMaxResults());
        return result;
    }

    private void saveMostReadData(final List<List<String>> mostReadData, final ResourceResolver resourceResolver) throws Exception
    {
        if (mostReadData != null)
        {
            for (final List<String> row : mostReadData)
            {
                AnalyticsUtils.createAnalyticsPageviewNodeFromResponseRow(row, googleAnalyticsConfiguration.getRootPath(), resourceResolver);
            }
        }
    }

    public String getProfileId()
    {
        return googleAnalyticsConfiguration.getProfileId();
    }

    public void setProfileId(String profileId)
    {
        googleAnalyticsConfiguration.setProfileId(profileId);
    }

    public String getSchedulingExpression()
    {
        return googleAnalyticsConfiguration.getSchedulingExpression();
    }

    public void setSchedulingExpression(String schedulingExpression)
    {
        googleAnalyticsConfiguration.setSchedulingExpression(schedulingExpression);
    }

    public String getRootPath()
    {
        return googleAnalyticsConfiguration.getRootPath();
    }

    public void setRootPath(String magazinePath)
    {
        googleAnalyticsConfiguration.setRootPath(magazinePath);
    }

    public String getGserviceEmail()
    {
        return googleAnalyticsConfiguration.getGserviceEmail();
    }

    public void setGserviceEmail(String gserviceEmail)
    {
        googleAnalyticsConfiguration.setGserviceEmail(gserviceEmail);
    }

    public String getPrivateKey()
    {
        return googleAnalyticsConfiguration.getPrivateKey();
    }

    public void setPrivateKey(String privateKey)
    {
        googleAnalyticsConfiguration.setPrivateKey(privateKey);
    }

    public String getMetrics()
    {
        return googleAnalyticsConfiguration.getMetrics();
    }

    public void setMetrics(String metrics)
    {
        googleAnalyticsConfiguration.setMetrics(metrics);
    }

    public String getDimensions()
    {
        return googleAnalyticsConfiguration.getDimensions();
    }

    public void setDimensions(String dimensions)
    {
        googleAnalyticsConfiguration.setDimensions(dimensions);
    }

    public String getStartDate()
    {
        return googleAnalyticsConfiguration.getStartDate();
    }

    public void setStartDate(String startDate)
    {
        googleAnalyticsConfiguration.setStartDate(startDate);
    }

    public String getEndDate()
    {
        return googleAnalyticsConfiguration.getEndDate();
    }

    public void setEndDate(String endDate)
    {
        googleAnalyticsConfiguration.setEndDate(endDate);
    }

    public String getSort()
    {
        return googleAnalyticsConfiguration.getSort();
    }

    public void setSort(String sort)
    {
        googleAnalyticsConfiguration.setSort(sort);
    }

    public String getMaxResults()
    {
        return googleAnalyticsConfiguration.getMaxResults();
    }

    public void setMaxResults(String maxResults)
    {
        googleAnalyticsConfiguration.setMaxResults(maxResults);
    }

    public ResourceResolverFactory getResourceResolverFactory()
    {
        return resourceResolverFactory;
    }

    public void setResourceResolverFactory(ResourceResolverFactory resourceResolverFactory)
    {
        this.resourceResolverFactory = resourceResolverFactory;
    }

}
