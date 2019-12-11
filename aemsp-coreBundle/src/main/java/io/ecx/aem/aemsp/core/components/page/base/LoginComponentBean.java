package io.ecx.aem.aemsp.core.components.page.base;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class LoginComponentBean extends AbstractSightlyComponentBean
{
    private static final String FORGOT_TEXT_PROPERTY = "forgotText";
    private static final String FORGOT_LINK_PROPERTY = "forgotLink";
    private static final String REGISTER_TEXT_PROPERTY = "registerText";
    private static final String REGISTER_LINK_PROPERTY = "registerLink";

    private String forgotPasswordText;
    private String forgotPasswordLink;
    private String registerText;
    private String registerLink;

    @Override
    protected void init() throws Exception
    {
        final Page languageNode = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
        final ValueMap valueMap = languageNode.getContentResource().getValueMap();
        forgotPasswordText = valueMap.get(FORGOT_TEXT_PROPERTY, StringUtils.EMPTY);
        forgotPasswordLink = valueMap.get(FORGOT_LINK_PROPERTY, StringUtils.EMPTY);
        registerText = valueMap.get(REGISTER_TEXT_PROPERTY, StringUtils.EMPTY);
        registerLink = valueMap.get(REGISTER_LINK_PROPERTY, StringUtils.EMPTY);
    }

    public String getForgotPasswordText()
    {
        return forgotPasswordText;
    }

    public String getForgotPasswordLink()
    {
        return forgotPasswordLink;
    }

    public String getRegisterText()
    {
        return registerText;
    }

    public String getRegisterLink()
    {
        return registerLink;
    }

}
