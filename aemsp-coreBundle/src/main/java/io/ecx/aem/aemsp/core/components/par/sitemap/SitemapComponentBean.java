package io.ecx.aem.aemsp.core.components.par.sitemap;

import java.util.Arrays;

import com.day.cq.commons.Filter;
import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.PageUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class SitemapComponentBean extends AbstractSightlyComponentBean
{
    private SitemapVO sitemap;

    @Override
    protected void init() throws Exception
    {
        final Page rootPage = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
        final Filter<Page> sitemapFilter = PageUtils.getPageTemplateFilter(Arrays.asList(GlobalConstants.CATEGORY_PAGE_TEMPLATE, GlobalConstants.CONTENT_PAGE_TEMPLATE));
        sitemap = new SitemapVO(rootPage, sitemapFilter);
        sitemap.draw(getResponse().getWriter());
    }

    public SitemapVO getSitemap()
    {
        return sitemap;
    }

}
