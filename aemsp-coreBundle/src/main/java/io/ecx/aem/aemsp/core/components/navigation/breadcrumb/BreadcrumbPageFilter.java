package io.ecx.aem.aemsp.core.components.navigation.breadcrumb;

import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.components.navigation.mainnavigation.MainNavigationPageFilter;
import io.ecx.aem.aemsp.core.utils.PageUtils;

public class BreadcrumbPageFilter extends MainNavigationPageFilter
{

    private final static String[] HIDE_TEMPLATE_LIST = { GlobalConstants.GROUP_PAGE_TEMPLATE };

    @Override
    public boolean includes(final Page page)
    {
        return PageUtils.pageFilterTest(page, HIDE_TEMPLATE_LIST);
    }
}