package io.ecx.aem.aemsp.core.components.par.mobileappbanner;

import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class MobileAppBannerComponentBean extends AbstractSightlyComponentBean
{
    private static final String ACTIVATED_PROPERTY = "appBannerActivated";
    private static final String TITLE_PROPERTY = "appTitle";
    private static final String AUTHOR_PROPERTY = "appAuthor";
    private static final String PRICE_PROPERTY = "appPrice";
    private static final String APPLE_CODE_PROPERTY = "appleCode";
    private static final String GOOGLE_CODE_PROPERTY = "googleCode";

    private boolean activated;
    private String title;
    private String author;
    private String price;
    private String appleCode;
    private String googleCode;

    @Override
    protected void init() throws Exception
    {
        final Page languagePage = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
        if (languagePage != null)
        {
            final ValueMap valueMap = languagePage.getProperties();
            activated = valueMap.get(ACTIVATED_PROPERTY, false);
            if (activated)
            {
                title = valueMap.get(TITLE_PROPERTY, String.class);
                author = valueMap.get(AUTHOR_PROPERTY, String.class);
                price = valueMap.get(PRICE_PROPERTY, String.class);
                appleCode = valueMap.get(APPLE_CODE_PROPERTY, String.class);
                googleCode = valueMap.get(GOOGLE_CODE_PROPERTY, String.class);
            }
        }
    }

    public boolean isActivated()
    {
        return activated;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getPrice()
    {
        return price;
    }

    public String getAppleCode()
    {
        return appleCode;
    }

    public String getGoogleCode()
    {
        return googleCode;
    }

}
