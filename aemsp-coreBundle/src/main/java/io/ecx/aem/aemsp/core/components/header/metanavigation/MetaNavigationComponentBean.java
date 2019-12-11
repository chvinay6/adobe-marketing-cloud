package io.ecx.aem.aemsp.core.components.header.metanavigation;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.UserSessionUtils;
import io.ecx.aem.aemsp.core.vo.account.AccountVO;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class MetaNavigationComponentBean extends AbstractSightlyComponentBean
{

    private static final Logger LOG = LoggerFactory.getLogger(MetaNavigationComponentBean.class);
    private String subscriptionLink;
    private String subscriptionText;
    private boolean subscriptionShown;
    private static final String SUBS_LINK_PROP = "subscriptionLink";
    private static final String SUBS_TEXT_PROP = "subscriptionText";
    private static final String SUBS_SHOWN_PROP = "subscriptionShown";

    private AccountVO account;

    private String currentHomeDomain;
    private static final String DOMAIN_PROP = "domain";

    @Override
    protected void init() throws Exception
    {
        final Page langPage = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
        final Resource langPageContent = langPage.getContentResource();
        final ValueMap langPageVM = langPageContent.getValueMap();

        if (langPageVM == null)
        {
            LOG.error("Language page has no content");
        }
        else
        {
            subscriptionLink = langPageVM.get(SUBS_LINK_PROP, String.class);
            subscriptionText = langPageVM.get(SUBS_TEXT_PROP, String.class);
            subscriptionShown = langPageVM.get(SUBS_SHOWN_PROP, false);
            currentHomeDomain = langPageVM.get(DOMAIN_PROP, String.class);

        }

        account = UserSessionUtils.getAccountDataFromSession(getRequest());
    }

    public String getSubscriptionLink()
    {
        return subscriptionLink;
    }

    public String getSubscriptionText()
    {
        return subscriptionText;
    }

    public boolean isSubscriptionShown()
    {
        return subscriptionShown;
    }

    public boolean isLinkSet()
    {
        return !StringUtils.isBlank(subscriptionText) && !StringUtils.isBlank(subscriptionLink);
    }



    public String getCurrentHomeDomain()
    {
        return currentHomeDomain;
    }

    public AccountVO getAccount()
    {
        return account;
    }

    public String getName()
    {
    	return "";
    }
}
