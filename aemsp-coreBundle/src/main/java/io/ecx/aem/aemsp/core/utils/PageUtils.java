package io.ecx.aem.aemsp.core.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Filter;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;

/**
 * i.mihalina
 */
public final class PageUtils
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(TextUtils.class);

    private PageUtils()
    {

    }

    public static Filter<Page> getPageTemplateFilter(final List<String> allowedTemplates)
    {
        return new Filter<Page>()
        {
            @Override
            public boolean includes(Page item)
            {
                boolean result = false;
                final Resource contentResource = item.getContentResource();
                if (contentResource != null)
                {
                    final String pageTemplate = contentResource.getValueMap().get(NameConstants.NN_TEMPLATE, StringUtils.EMPTY);
                    result = allowedTemplates.contains(pageTemplate);
                }
                return result;
            }
        };
    }

    public static boolean pageFilterTest(final Page page, final String[] hideTemplateList)
    {
        boolean result = true;
        final ValueMap map = page.getProperties();
        if (map != null)
        {
            final String template = map.get(NameConstants.NN_TEMPLATE, String.class);
            if ((map.get(GlobalConstants.NAV_HIDE_IN_NAV, false)) || Arrays.asList(hideTemplateList).contains(template))
            {
                result = false;
            }
        }

        return result;
    }

    public static boolean isPageTemplate(final Page page, final String template)
    {
        boolean result = false;
        final ValueMap vmap = page.getProperties();
        if (vmap != null && template.equals(vmap.get(NameConstants.NN_TEMPLATE, String.class)))
        {
            result = true;
        }

        return result;
    }

    public static boolean isResourceTemplate(final Resource resource, final String templatePath)
    {
        boolean result = false;
        final ValueMap vmap = resource.getValueMap();
        if (vmap != null && templatePath.equals(vmap.get(NameConstants.NN_TEMPLATE, String.class)))
        {
            result = true;
        }

        return result;
    }
}
