package io.ecx.aem.aemsp.core.components.navigation.breadcrumb;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.components.navigation.NavigationItemVO;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;
import io.ecx.cq.common.core.utils.NavigationUtils;
import io.ecx.cq.common.core.vo.navigation.NavigationVO;

public class BreadcrumbComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(BreadcrumbComponentBean.class);
    List<NavigationVO> breadcrumb;

    @Override
    protected void init() throws Exception
    {
        breadcrumb = new ArrayList<NavigationVO>();
        final List<NavigationVO> breadcrumbList = NavigationUtils.getBreadcrumb(NavigationItemVO.class, getCurrentPage(), GlobalConstants.SITE_PAGE_DEPTH, getResourceResolver());
        final PageFilter filter = new BreadcrumbPageFilter();

        for (final NavigationVO navItem : breadcrumbList)
        {
            final Page page = getResourceResolver().getResource(navItem.getPath()).adaptTo(Page.class);

            if (page != null && filter.includes(page))
            {
                breadcrumb.add(navItem);
            }
        }
    }

    public List<NavigationVO> getBreadcrumb()
    {
        return breadcrumb;
    }
}