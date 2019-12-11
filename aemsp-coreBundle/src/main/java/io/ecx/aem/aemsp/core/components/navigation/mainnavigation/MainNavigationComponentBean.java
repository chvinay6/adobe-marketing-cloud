package io.ecx.aem.aemsp.core.components.navigation.mainnavigation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.PageFilter;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.components.navigation.NavigationItemVO;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;
import io.ecx.cq.common.core.utils.NavigationUtils;
import io.ecx.cq.common.core.vo.navigation.NavigationVO;

public class MainNavigationComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MainNavigationComponentBean.class);
    List<NavigationVO> navigation;

    @Override
    protected void init() throws Exception
    {
        final PageFilter filter = new MainNavigationPageFilter();
        navigation = new ArrayList<>();
        navigation.addAll(NavigationUtils.getMainNavigation(NavigationItemVO.class, getCurrentPage(), GlobalConstants.LANGUAGE_PAGE_DEPTH, filter, GlobalConstants.MAIN_NAVIGATION_DEPTH, getResourceResolver()));
    }

    public List<NavigationVO> getNavigation()
    {
        return navigation;
    }
}