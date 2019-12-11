package io.ecx.aem.aemsp.core.utils;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

import io.ecx.aem.aemsp.core.GlobalConstants;

public final class QueryUtils
{

    private static final String DESC_ORDER = "DESC";

    private static String generateQueryStart(final String searchPath, final String resourceType)
    {
        return TextUtils.appendStringObjects("SELECT * FROM [cq:PageContent] AS page WHERE ISDESCENDANTNODE([", searchPath, "]) AND page.[", ResourceResolver.PROPERTY_RESOURCE_TYPE, "] = '", resourceType, "' ");
    }

    private static String addOrderToOuery(final String orderProperty, final String orderType)
    {
        return TextUtils.appendStringObjects(" ORDER BY page.[", orderProperty, "] ", orderType);
    }

    public static String generateUpcomingPagesSearchQuery(final String resourceType, final String searchPath, final String orderField, final String order, final String[] filter)
    {
        final Calendar currentCal = Calendar.getInstance(GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
        return TextUtils.appendStringObjects(generateQueryStart(searchPath, resourceType), "AND page.[", orderField, "] >= CAST('", DateUtils.getFormattedJCRDate(currentCal.getTime()), "' AS DATE) ", addFiltersToQuery(filter), addOrderToOuery(orderField, order));
    }

    public static String generateArticleSearchQuery(final String resourceType, final String searchPath, final String orderField, final String[] filter)
    {
        return TextUtils.appendStringObjects(generateQueryStart(searchPath, resourceType), addFiltersToQuery(filter), addOrderToOuery(orderField, DESC_ORDER));
    }

    public static String generateArticleSearchWithoutBreakingQuery(final String resourceType, final String searchPath, final String[] filter)
    {
        final Calendar currentCal = Calendar.getInstance(GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
        return TextUtils.appendStringObjects(generateQueryStart(searchPath, resourceType), "AND (page.[", GlobalConstants.BREAKING_NEWS_DATE_PROPERTY, "] IS NULL OR page.[", GlobalConstants.BREAKING_NEWS_DATE_PROPERTY, "] < cast('", DateUtils.getFormattedJCRDate(currentCal.getTime()), "' as date))", addFiltersToQuery(filter), addOrderToOuery(GlobalConstants.ARTICLE_PUBLISH_DATE_PROPERTY, DESC_ORDER));
    }

    public static String generateBreakingNewsSearchQuery(final String resourceType, final String searchPath, final String[] filter)
    {
        final Calendar currentCal = Calendar.getInstance(GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
        return TextUtils.appendStringObjects(generateQueryStart(searchPath, resourceType), "AND page.[", GlobalConstants.BREAKING_NEWS_DATE_PROPERTY, "] > cast('", DateUtils.getFormattedJCRDate(currentCal.getTime()), "' as date)", addFiltersToQuery(filter), addOrderToOuery(GlobalConstants.ARTICLE_PUBLISH_DATE_PROPERTY, DESC_ORDER));
    }

    public static String generateOfferSearchQuery(final String resourceType, final String searchPath, final String[] filter)
    {
        final Calendar currentDate = Calendar.getInstance(GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
        return TextUtils.appendStringObjects(generateQueryStart(searchPath, resourceType), "AND page.[", GlobalConstants.OFFER_DATE_PROPERTY, "] > cast('", DateUtils.getFormattedJCRDate(currentDate.getTime()), "' as date)", addFiltersToQuery(filter), addOrderToOuery(GlobalConstants.OFFER_DATE_PROPERTY, DESC_ORDER));
    }

    public static String generateNodeSearchQuery(final String searchPath, final String orderField)
    {
        return TextUtils.appendStringObjects("SELECT * FROM [nt:base] AS node WHERE ISDESCENDANTNODE([", searchPath, "]) AND node.[", orderField, "] IS NOT NULL ORDER BY node.[", orderField, "] DESC");
    }

    private static String addFiltersToQuery(final String[] filters)
    {
        String result = StringUtils.EMPTY;
        if (filters != null && filters.length > 0)
        {
            result = " AND (";
            int i = 0;
            for (final String filter : filters)
            {
                result = TextUtils.appendStringObjects(result, i > 0 ? " OR " : StringUtils.EMPTY, "(CONTAINS(page.[cq:tags], '", filter, "'))");
                i++;
            }
            result = TextUtils.appendStringObjects(result, ")");
        }

        return result;
    }

}
