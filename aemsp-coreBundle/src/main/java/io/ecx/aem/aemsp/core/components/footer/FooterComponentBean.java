package io.ecx.aem.aemsp.core.components.footer;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class FooterComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(FooterComponentBean.class);
    private static final String FOOTER_PARSYS_NODE_NAME = "footPar";
    private String footParPath;

    @Override
    protected void init() throws Exception
    {
        final Resource footPar = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH).getContentResource(FOOTER_PARSYS_NODE_NAME);
        footParPath = footPar == null ? null : footPar.getPath();
    }

    public String getFootParPath()
    {
        return footParPath;
    }
    
    public void testMethod()
    {
    	System.out.println("test msg");
    }
}
