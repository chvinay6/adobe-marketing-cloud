package io.ecx.aem.aemsp.core.slingmodels;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.wcm.api.PageManager;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.TextUtils;

@Model(adaptables = Resource.class)
public class UserInfoModel
{
    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    @Optional
    @Named("jcr:content/browserText")
    private String browserText;

    @Inject
    @Optional
    @Named("jcr:content/browserLink")
    private String browserLink;

    @Inject
    @Optional
    @Named("jcr:content/browserLinkText")
    private String browserLinkText;

    @Inject
    @Optional
    @Named("jcr:content/browserButtonText")
    private String browserButtonText;

    @Inject
    @Optional
    @Named("jcr:content/cookieHeadline")
    private String cookieHeadline;

    @Inject
    @Optional
    @Named("jcr:content/cookieText")
    private String cookieText;

    @Inject
    @Optional
    @Named("jcr:content/cookieLink")
    private String cookieLink;

    @Inject
    @Optional
    @Named("jcr:content/cookieLinkText")
    private String cookieLinkText;

    @Inject
    @Optional
    @Named("jcr:content/cookieButtonText")
    private String cookieButtonText;

    @Inject
    @Optional
    @Named("jcr:content/browserInfoEnabled")
    private String browserInfoEnabled;

    @Inject
    @Optional
    @Named("jcr:content/cookieInfoEnabled")
    private String cookieInfoEnabled;

    @Inject
    @Optional
    @Named("jcr:content/enableCookieLink")
    private String cookieLinkEnabled;

    public String getBrowserText()
    {
        return browserText;
    }

    public String getBrowserLink()
    {
        return checkLink(browserLink);
    }

    public String getBrowserLinkText()
    {
        return browserLinkText;
    }

    public String getBrowserButtonText()
    {
        return browserButtonText;
    }

    public String getCookieHeadline()
    {
        return cookieHeadline;
    }

    public String getCookieText()
    {
        return cookieText;
    }

    public String getCookieLink()
    {
        return checkLink(cookieLink);
    }

    public String getCookieLinkText()
    {
        return cookieLinkText;
    }

    public String getCookieButtonText()
    {
        return cookieButtonText;
    }

    public String getBrowserInfoEnabled()
    {
        return browserInfoEnabled;
    }

    public String getCookieInfoEnabled()
    {
        return cookieInfoEnabled;
    }

    public String getCookieLinkEnabled()
    {
        return cookieLinkEnabled;
    }

    private String checkLink(final String pageLink)
    {
        String result = pageLink;
        if (resourceResolver != null && StringUtils.isNotBlank(pageLink))
        {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

            if (pageManager.getPage(pageLink) != null)
            {
                result = TextUtils.appendStringObjects(pageLink, GlobalConstants.HTML_EXTENSION);
            }
        }

        return result;
    }
}
