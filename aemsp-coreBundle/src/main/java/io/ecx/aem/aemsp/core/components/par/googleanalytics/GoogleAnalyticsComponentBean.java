package io.ecx.aem.aemsp.core.components.par.googleanalytics;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class GoogleAnalyticsComponentBean extends AbstractSightlyComponentBean
{
    private static final String GOOGLE_ANALYTICS_ID_PROPERTY = "googleAnalyticsId";

    private String googleAnalyticsId;
    private String templateName;

    @Override
    protected void init() throws Exception
    {
        final Page languagePage = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
        if (languagePage != null)
        {
            final ValueMap valueMap = languagePage.getProperties();
            googleAnalyticsId = valueMap.get(GOOGLE_ANALYTICS_ID_PROPERTY, String.class);
        }
        final String[] template = getCurrentPage().getContentResource().getValueMap().get(NameConstants.NN_TEMPLATE, StringUtils.EMPTY).split("/");
        templateName = template[template.length - 1];
    }

    public String getGoogleAnalyticsId()
    {
        return googleAnalyticsId;
    }

    public String getTemplateName()
    {
        return templateName;
    }

}
