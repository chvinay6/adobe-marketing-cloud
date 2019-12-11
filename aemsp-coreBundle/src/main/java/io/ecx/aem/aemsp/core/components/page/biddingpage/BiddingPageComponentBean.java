package io.ecx.aem.aemsp.core.components.page.biddingpage;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.i18n.I18n;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.DateUtils;
import io.ecx.aem.aemsp.core.utils.TagUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class BiddingPageComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(BiddingPageComponentBean.class);

    private static final String END_DATE_PROPERTY = "endDate";

    private String region;
    private String endDate;
    private String backAndShareBarPosition;

    @Override
    protected void init() throws Exception
    {
        final ValueMap offerValues = getPageProperties();

        endDate = DateUtils.formatDateToString(offerValues.get(END_DATE_PROPERTY, Date.class), DateUtils.NO_TIME_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
        setRegion();

        final String backSharePos = getCurrentPage().getAbsoluteParent(GlobalConstants.MAGAZINE_PAGE_DEPTH).getContentResource().getValueMap().getOrDefault(GlobalConstants.MAGAZINE_BACKSHAREBAR_POSITION_PROPERTY, StringUtils.EMPTY).toString();
        backAndShareBarPosition = StringUtils.isNotBlank(backSharePos) ? backSharePos : GlobalConstants.MAGAZINE_BACKSHAREBAR_POSITION_NONE;
    }

    private void setRegion()
    {
        region = TagUtils.getBiddingPageRegion(getResource(), getCurrentPage());
        if (StringUtils.isBlank(region))
        {
            region = I18n.get(getRequest(), "aemsp_region_other");
        }
    }

    public String getRegion()
    {
        return region;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getBackAndShareBarPosition()
    {
        return backAndShareBarPosition;
    }
}
