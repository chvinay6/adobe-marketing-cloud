package io.ecx.aem.aemsp.core.components.navigation.mainnavigation;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.PageUtils;

public class MainNavigationPageFilter extends PageFilter
{
    private final static String[] HIDE_TEMPLATE_LIST = { GlobalConstants.GROUP_PAGE_TEMPLATE, GlobalConstants.ARTICLE_PAGE_TEMPLATE, GlobalConstants.HOMEPAGE_CONFIG_PAGE_TEMPLATE, GlobalConstants.VOTING_MODULE_PAGE_TEMPLATE, GlobalConstants.EVENT_PAGE_TEMPLATE, GlobalConstants.OFFER_PAGE_TEMPLATE };

    @Override
    public boolean includes(final Page page)
    {
        return PageUtils.pageFilterTest(page, HIDE_TEMPLATE_LIST);
    }
}