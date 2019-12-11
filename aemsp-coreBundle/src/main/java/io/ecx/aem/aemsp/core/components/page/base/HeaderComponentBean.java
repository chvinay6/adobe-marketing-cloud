package io.ecx.aem.aemsp.core.components.page.base;

import org.apache.commons.lang3.StringUtils;

import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class HeaderComponentBean extends AbstractSightlyComponentBean
{

    private static String LOGO_PATH_PROPERTY = "logoPath";

    private String logoPath;
    private String homePageLink;

    @Override
    protected void init() throws Exception
    {
        final Page languagePage = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
        if (languagePage != null)
        {
            logoPath = languagePage.getProperties().get(LOGO_PATH_PROPERTY, StringUtils.EMPTY);
            homePageLink = TextUtils.appendStringObjects(languagePage.getPath(), GlobalConstants.HTML_EXTENSION);
        }
    }

    public String getLogoPath()
    {
        return logoPath;
    }

    public String getHomePageLink()
    {
        return homePageLink;
    }

}