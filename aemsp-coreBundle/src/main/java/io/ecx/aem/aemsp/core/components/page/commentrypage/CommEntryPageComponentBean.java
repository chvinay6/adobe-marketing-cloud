package io.ecx.aem.aemsp.core.components.page.commentrypage;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.DateUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class CommEntryPageComponentBean extends AbstractSightlyComponentBean
{

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(CommEntryPageComponentBean.class);
    private String backAndShareBarPosition;
    private Date date;

    @Override
    protected void init() throws Exception
    {
        final Resource pageContent = getCurrentPage().getContentResource();

        final String backSharePos = getCurrentPage().getAbsoluteParent(GlobalConstants.MAGAZINE_PAGE_DEPTH).getContentResource().getValueMap().getOrDefault(GlobalConstants.MAGAZINE_BACKSHAREBAR_POSITION_PROPERTY, StringUtils.EMPTY).toString();
        backAndShareBarPosition = StringUtils.isNotBlank(backSharePos) ? backSharePos : GlobalConstants.MAGAZINE_BACKSHAREBAR_POSITION_NONE;

        date = pageContent.getValueMap().get("date", Date.class);
    }

    public String getBackAndShareBarPosition()
    {
        return backAndShareBarPosition;
    }

    public String getDate()
    {
        return DateUtils.formatDateToString(date, DateUtils.NO_TIME_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
    }

}
