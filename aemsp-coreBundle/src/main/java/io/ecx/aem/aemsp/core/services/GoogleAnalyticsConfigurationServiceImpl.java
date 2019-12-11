package io.ecx.aem.aemsp.core.services;

import java.util.Dictionary;
import java.util.UUID;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.utils.TextUtils;

@Component(configurationFactory = true, policy = ConfigurationPolicy.REQUIRE, label = "aemsp Google Analytics Configuration Service", description = "aemsp Google Analytics Configuration Service", immediate = true, metatype = true)
@Service()
public class GoogleAnalyticsConfigurationServiceImpl implements GoogleAnalyticsConfigurationService
{
    private final static Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsConfigurationService.class);

    private static final String DEF_PROFILE_ID = StringUtils.EMPTY;
    @Property(name = "aemsp.googleanalytics.profile.id", label = "Analytics Profile Id", value = { DEF_PROFILE_ID }, description = "The analytics Profile Id for this Configuration in the format ga:12345678. Reference to: https://concept.ecx.io/pages/editpage.action?pageId=105350471")
    private static final String CONF_PROFILE_ID = "aemsp.googleanalytics.profile.id";

    private static final String DEF_SCHEDULING_EXPRESSION = "0 0 * * * ?";
    @Property(name = "aemsp.googleanalytics.scheduling.expression", label = "Sheduling expression for Google Analytics Import", description = "Refer to http://www.docjar.com/docs/api/org/quartz/CronTrigger.html to define more scheduling expressions.", value = { DEF_SCHEDULING_EXPRESSION })
    private static final String CONF_SCHEDULING_EXPRESSION = "aemsp.googleanalytics.scheduling.expression";

    private static final String DEF_CONFIGURATION_PATH = "";
    @Property(name = "aemsp.googleanalytics.configuration.path", label = "Magazine", value = { DEF_CONFIGURATION_PATH }, description = "Path (Homepage), which Google Analytics is configured for. Choose a langugage page")
    private static final String CONF_CONFIGURATION_PATH = "aemsp.googleanalytics.configuration.path";

    private static final String DEF_GSERVICE_EMAIL = StringUtils.EMPTY;
    @Property(name = "aemsp.googleanalytics.gservice.email", label = "Service E-mail", value = { DEF_GSERVICE_EMAIL }, description = "Google Service Account E-mail")
    private static final String CONF_GSERVICE_EMAIL = "aemsp.googleanalytics.gservice.email";

    private static final String DEF_PRIVATE_KEY = StringUtils.EMPTY;
    @Property(name = "aemsp.googleanalytics.privatekey", label = "Private Key", value = { DEF_PRIVATE_KEY }, description = "Private Key related to the Service E-mail")
    private static final String CONF_PRIVATE_KEY = "aemsp.googleanalytics.privatekey";

    private static final String DEF_START_DATE = "30daysAgo";
    @Property(name = "aemsp.googleanalytics.startdate", label = "Start date", value = { DEF_START_DATE }, description = "Start date Google Analytics Data will be requested from. Reference to: https://developers.google.com/analytics/devguides/reporting/core/v3/reference#start-date")
    private static final String CONF_START_DATE = "aemsp.googleanalytics.startdate";

    private static final String DEF_END_DATE = "today";
    @Property(name = "aemsp.googleanalytics.enddate", label = "End Date", value = { DEF_END_DATE }, description = "End date Google Analytics Data will be requested to. Reference to: https://developers.google.com/analytics/devguides/reporting/core/v3/reference#end-date")
    private static final String CONF_END_DATE = "aemsp.googleanalytics.enddate";

    private static final String DEF_METRICS = "ga:uniquePageviews";
    @Property(name = "aemsp.googleanalytics.metrics", label = "Metrics", value = { DEF_METRICS }, description = "Reference to: https://developers.google.com/analytics/devguides/reporting/core/v3/reference#metrics")
    private static final String CONF_METRICS = "aemsp.googleanalytics.metrics";

    private static final String DEF_DIMENSIONS = "ga:pagePath,ga:dimension1";
    @Property(name = "aemsp.googleanalytics.dimensions", label = "Dimensions", value = { DEF_DIMENSIONS }, description = "Reference to: https://developers.google.com/analytics/devguides/reporting/core/v3/reference#dimensions")
    private static final String CONF_DIMENSIONS = "aemsp.googleanalytics.dimensions";

    private static final String DEF_SORT = "-ga:uniquePageviews";
    @Property(name = "aemsp.googleanalytics.sort", label = "Sort", value = { DEF_SORT }, description = "Reference to: https://developers.google.com/analytics/devguides/reporting/core/v3/reference#sort")
    private static final String CONF_SORT = "aemsp.googleanalytics.sort";

    public static final String DEF_MAX_RESULTS = "20";
    @Property(name = "aemsp.googleanalytics.maxresults", label = "Max Results", value = { DEF_MAX_RESULTS }, description = "Max results per Main Category. Reference to: https://developers.google.com/analytics/devguides/reporting/core/v3/reference#maxResults")
    public static final String CONF_MAX_RESULTS = "aemsp.googleanalytics.maxresults";

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Scheduler scheduler;

    private String googleAnalyticsJobName = "Google Analytics Request Job";

    private GoogleAnalyticsConfigurationVO googleAnalyticsConfiguration = new GoogleAnalyticsConfigurationVO();

    @Activate
    protected void activate(final ComponentContext componentContext)
    {
        googleAnalyticsJobName = TextUtils.appendStringObjects(googleAnalyticsJobName, UUID.randomUUID().toString());
        LOG.debug("activate {}", googleAnalyticsJobName);

        readOsgiProperties(componentContext);

        final GoogleAnalyticsRequester googleAnalyticsRequester = new GoogleAnalyticsRequester();
        setGoogleAnalyticsProperties(googleAnalyticsRequester);

        if (scheduler.schedule(googleAnalyticsRequester, scheduler.EXPR(googleAnalyticsConfiguration.getSchedulingExpression()).name(googleAnalyticsJobName)))
        {
            LOG.info("schedule job {}", googleAnalyticsJobName);
        }
        else
        {
            LOG.error("schedule job {} failed", googleAnalyticsJobName);
        }
    }

    private void setGoogleAnalyticsProperties(final GoogleAnalyticsRequester googleAnalyticsRequester)
    {
        googleAnalyticsRequester.setProfileId(googleAnalyticsConfiguration.getProfileId());
        googleAnalyticsRequester.setRootPath(googleAnalyticsConfiguration.getRootPath());
        googleAnalyticsRequester.setSchedulingExpression(googleAnalyticsConfiguration.getSchedulingExpression());
        googleAnalyticsRequester.setResourceResolverFactory(resourceResolverFactory);
        googleAnalyticsRequester.setGserviceEmail(googleAnalyticsConfiguration.getGserviceEmail());
        googleAnalyticsRequester.setPrivateKey(googleAnalyticsConfiguration.getPrivateKey());
        googleAnalyticsRequester.setMetrics(googleAnalyticsConfiguration.getMetrics());
        googleAnalyticsRequester.setDimensions(googleAnalyticsConfiguration.getDimensions());
        googleAnalyticsRequester.setStartDate(googleAnalyticsConfiguration.getStartDate());
        googleAnalyticsRequester.setEndDate(googleAnalyticsConfiguration.getEndDate());
        googleAnalyticsRequester.setSort(googleAnalyticsConfiguration.getSort());
        googleAnalyticsRequester.setMaxResults(googleAnalyticsConfiguration.getMaxResults());
    }

    @Deactivate
    protected void deactivate(final ComponentContext componentContext) throws RepositoryException
    {
        LOG.debug("deactivate started");
        scheduler.unschedule(googleAnalyticsJobName);
        LOG.debug("deactivate");
    }

    private void readOsgiProperties(final ComponentContext componentContext)
    {
        final Dictionary<?, ?> osgiProperties = componentContext.getProperties();

        googleAnalyticsConfiguration.setProfileId(PropertiesUtil.toString(osgiProperties.get(CONF_PROFILE_ID), DEF_PROFILE_ID));
        googleAnalyticsConfiguration.setRootPath(PropertiesUtil.toString(osgiProperties.get(CONF_CONFIGURATION_PATH), DEF_CONFIGURATION_PATH));
        googleAnalyticsConfiguration.setSchedulingExpression(PropertiesUtil.toString(osgiProperties.get(CONF_SCHEDULING_EXPRESSION), DEF_SCHEDULING_EXPRESSION));
        googleAnalyticsConfiguration.setGserviceEmail(PropertiesUtil.toString(osgiProperties.get(CONF_GSERVICE_EMAIL), DEF_GSERVICE_EMAIL));
        googleAnalyticsConfiguration.setPrivateKey(PropertiesUtil.toString(osgiProperties.get(CONF_PRIVATE_KEY), DEF_PRIVATE_KEY));
        googleAnalyticsConfiguration.setMetrics(PropertiesUtil.toString(osgiProperties.get(CONF_METRICS), DEF_METRICS));
        googleAnalyticsConfiguration.setDimensions(PropertiesUtil.toString(osgiProperties.get(CONF_DIMENSIONS), DEF_DIMENSIONS));
        googleAnalyticsConfiguration.setStartDate(PropertiesUtil.toString(osgiProperties.get(CONF_START_DATE), CONF_END_DATE));
        googleAnalyticsConfiguration.setEndDate(PropertiesUtil.toString(osgiProperties.get(CONF_END_DATE), DEF_END_DATE));
        googleAnalyticsConfiguration.setSort(PropertiesUtil.toString(osgiProperties.get(CONF_SORT), DEF_SORT));
        googleAnalyticsConfiguration.setMaxResults(PropertiesUtil.toString(osgiProperties.get(CONF_MAX_RESULTS), DEF_MAX_RESULTS));
    }

}
