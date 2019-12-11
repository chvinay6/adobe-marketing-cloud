package io.ecx.aem.aemsp.core.components.page.base;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.AemUserModel;
import io.ecx.aem.aemsp.core.slingmodels.UserInfoModel;
import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class BasePageComponentBean extends AbstractSightlyComponentBean
{
    private static final Logger LOG = LoggerFactory.getLogger(BasePageComponentBean.class);
    private String language = StringUtils.EMPTY;
    private String pageLanguage = StringUtils.EMPTY;
    private UserInfoModel userInfo;
    private String contactURL;

    @Override
    protected void init()
    {
        pageLanguage = getCurrentPage().getLanguage(true).getLanguage();

        final AemUserModel userModel = ResourceResolverUtils.getAemUserModel(getResourceResolver(), getResourceResolver().adaptTo(Session.class).getUserID());
        if (userModel != null)
        {
            language = StringUtils.isNotBlank(userModel.getLanguage()) ? userModel.getLanguage() : GlobalConstants.DEFAULT_LANGUAGE;
        }

        if (StringUtils.isBlank(language))
        {
            LOG.debug("Unable to find user language - falling back to page language");
            language = pageLanguage;
        }

        LOG.debug("Language: {}", language);

        userInfo = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH).adaptTo(UserInfoModel.class);

        contactURL = TextUtils.appendStringObjects(getCurrentPage().getPath(), ".", GlobalConstants.CONTACT_EXTENSION);
    }

    public UserInfoModel getUserInfo()
    {
        return userInfo;
    }

    public String getLanguage()
    {
        return language;
    }

    public String getPageLanguage()
    {
        return pageLanguage;
    }

    public String getContactURL()
    {
        return contactURL;
    }

}
