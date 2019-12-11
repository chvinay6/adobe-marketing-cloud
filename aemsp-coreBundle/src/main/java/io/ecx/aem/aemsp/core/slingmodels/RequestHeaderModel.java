package io.ecx.aem.aemsp.core.slingmodels;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = SlingHttpServletRequest.class)
public class RequestHeaderModel
{
    private final SlingHttpServletRequest request;
    private static String SSI_HEADER_NAME_ = "AEMSSIMode";
    private static String SSI_HEADER_ACTIVE = "active";

    public RequestHeaderModel(SlingHttpServletRequest request) {
        this.request = request;
    }


    public boolean isSsiActive()
    {
        return StringUtils.equals(SSI_HEADER_ACTIVE,  request.getHeader(SSI_HEADER_NAME_));
    }

}
