package io.ecx.aem.aemsp.core.components.par.linkchecker;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.PageManager;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class LinkCheckerComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(LinkCheckerComponentBean.class);
    private String validLink;
    private boolean internal;

    @Override
    protected void init() throws Exception
    {
        validLink = get("link", String.class);

        final PageManager pageManager = getResourceResolver().adaptTo(PageManager.class);
        if (StringUtils.isNotBlank(validLink) && pageManager.getPage(validLink) != null)
        {
            internal = true;
            validLink = TextUtils.appendStringObjects(validLink, GlobalConstants.HTML_EXTENSION);
        }
    }

    public String getValidLink()
    {
        return validLink;
    }

    public boolean isInternal()
    {
        return internal;
    }

}
