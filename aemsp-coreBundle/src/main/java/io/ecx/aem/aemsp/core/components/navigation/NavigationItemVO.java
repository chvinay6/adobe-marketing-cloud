package io.ecx.aem.aemsp.core.components.navigation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

import io.ecx.cq.common.core.vo.navigation.NavigationVO;

/**
 * @author i.mihalina
 *
 */
public class NavigationItemVO extends NavigationVO
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(NavigationItemVO.class);

    public NavigationItemVO(final Page page, final ResourceResolver resourceResolver) throws Exception
    {
        super(page, resourceResolver);
        name = StringUtils.isNotBlank(page.getNavigationTitle()) ? page.getNavigationTitle() : page.getTitle();
    }

    public NavigationItemVO(final Page page, final Boolean active, final ResourceResolver resourceResolver) throws Exception
    {
        this(page, resourceResolver);
        this.active = active.booleanValue();
    }

    public NavigationItemVO(final Page page, final Boolean active, final List<NavigationVO> children, final ResourceResolver resourceResolver) throws Exception
    {
        this(page, active, resourceResolver);
        this.children = children;
    }
}