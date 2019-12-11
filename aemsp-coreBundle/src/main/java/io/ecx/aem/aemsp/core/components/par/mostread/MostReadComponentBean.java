package io.ecx.aem.aemsp.core.components.par.mostread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class MostReadComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(MostReadComponentBean.class);

    private int maxItems;
    private String breakingNewsText;

    @Override
    protected void init() throws Exception
    {
        final Page languagePage = getResourcePage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
        maxItems = getProperties().get("maxNumber", 3);
        breakingNewsText = languagePage.getProperties().get(GlobalConstants.BREAKING_NEWS_TEXT_PROPERTY, String.class);
    }

    public String getBreakingNewsText()
    {
        return breakingNewsText;
    }

    public int getMaxItems()
    {
        return maxItems;
    }

}
