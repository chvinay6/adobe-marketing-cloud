package io.ecx.aem.aemsp.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.GaData;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.cq.common.core.CqExceptionHandler;

public final class AnalyticsUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsUtils.class);

    private static final String APPLICATION_NAME = "aemsp Google Analytics";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public static final String ANALYTICS_METRICS_PROPERTY = "metrics";
    public static final String ANALYTICS_PAGEPATH_PROPERTY = "pagePath";
    public static final String ANALYTICS_DATA_NODE = "analyticsdata";
    public static final String ANALYTICS_DATA_NODE_PATH = "/content/analyticsdata";

    public static final String KEY_START_DATE = "start-date";
    public static final String KEY_END_DATE = "end-date";
    public static final String KEY_METRICS = "metrics";
    public static final String KEY_DIMENSIONS = "dimensions";
    public static final String KEY_FILTERS = "filters";
    public static final String KEY_SORT = "sort";
    public static final String KEY_MAX_RESULTS = "max-results";

    private static final String DEFAULT_START_DATE = "30daysAgo";
    private static final String DEFAULT_END_DATE = "today";
    private static final String DEFAULT_METRICS = "ga:uniquePageviews";
    private static final String DEFAULT_SORT = "-ga:uniquePageviews";
    private static final String DEFAULT_MAX = "20";

    private static final int PAGEPATH_COLUMN = 0;
    private static final int METRICS_COLUMN = 2;

    private AnalyticsUtils()
    {

    }

    public static Analytics initializeAnalytics(String serviceAccountEmail, String fullPrivateKey)
    {
        Analytics result = null;
        try
        {
            final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            final PrivateKey privateKey = getTrimmedPrivateKey(fullPrivateKey);
            final GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(JSON_FACTORY).setServiceAccountId(serviceAccountEmail).setServiceAccountPrivateKey(privateKey).setServiceAccountScopes(AnalyticsScopes.all()).build();
            result = new Analytics.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
        }
        catch (final Exception ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
        }
        return result;
    }

    private static PrivateKey getTrimmedPrivateKey(String fullPrivateKey) throws Exception
    {
        final StringBuilder pkcs8Lines = new StringBuilder();
        final BufferedReader rdr = new BufferedReader(new StringReader(fullPrivateKey));
        String line;
        while ((line = rdr.readLine()) != null)
        {
            pkcs8Lines.append(line);
        }

        // Remove the "BEGIN" and "END" lines, as well as any whitespace

        String pkcs8Pem = pkcs8Lines.toString();
        pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
        pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
        pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

        // Base64 decode the result

        final byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem);

        // extract the private key

        final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    public static GaData getResults(Analytics analytics, String profileId, HashMap<String, String> requestParams) throws IOException
    {
        GaData results;
        if (requestParams == null)
        {
            results = analytics.data().ga().get(profileId, DEFAULT_START_DATE, DEFAULT_END_DATE, DEFAULT_METRICS).setSort(DEFAULT_SORT).execute();
        }
        else
        {
            final int maxResults = Integer.parseInt(requestParams.getOrDefault(KEY_MAX_RESULTS, DEFAULT_MAX));
            results = analytics.data().ga().get(profileId, requestParams.getOrDefault(KEY_START_DATE, DEFAULT_START_DATE), requestParams.getOrDefault(KEY_END_DATE, DEFAULT_END_DATE), requestParams.getOrDefault(KEY_METRICS, DEFAULT_METRICS)).setDimensions(requestParams.getOrDefault(KEY_DIMENSIONS, StringUtils.EMPTY)).setFilters(requestParams.getOrDefault(KEY_FILTERS, StringUtils.EMPTY)).setSort(requestParams.getOrDefault(KEY_SORT, DEFAULT_SORT)).setMaxResults(maxResults).execute();
        }
        return results;
    }

    public static void deleteOldData(final String path, final ResourceResolver resourceResolver) throws AccessDeniedException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        ResourceResolverUtils.deleteIfExists(TextUtils.appendStringObjects(ANALYTICS_DATA_NODE_PATH, path), resourceResolver);
    }

    public static String getAnalyticsRelativePath(final String pagePath)
    {
        return TextUtils.appendStringObjects(ANALYTICS_DATA_NODE_PATH, pagePath);
    }

    public static void createAnalyticsPageviewNodeFromResponseRow(final List<String> row, final String rootPath, final ResourceResolver resourceResolver) throws RepositoryException, Exception
    {
        String trackedPagePath = row.get(PAGEPATH_COLUMN);
        if (!StringUtils.startsWith(trackedPagePath, rootPath))
        {
            trackedPagePath = TextUtils.appendStringObjects(rootPath, trackedPagePath);
        }
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put(ANALYTICS_PAGEPATH_PROPERTY, StringUtils.remove(trackedPagePath, GlobalConstants.HTML_EXTENSION));
        properties.put(ANALYTICS_METRICS_PROPERTY, Integer.parseInt(row.get(METRICS_COLUMN)));
        properties.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
        ResourceResolverUtils.createResource(getAnalyticsRelativePath(trackedPagePath), resourceResolver, properties, true);
        LOG.debug("saved Node {}", trackedPagePath);
        resourceResolver.adaptTo(Session.class).save();
    }

}
