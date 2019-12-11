package io.ecx.aem.aemsp.core.components.page.eventpage;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.EventPageModel;
import io.ecx.aem.aemsp.core.utils.DateUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class EventPageComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(EventPageComponentBean.class);

    private static final String FROM_DATE_PROPERTY = "fromDate";
    private static final String TO_DATE_PROPERTY = "toDate";
    private static final String ADDITIONAL_INFO_PROPERTY = "additionalInfo";

    private String fromDate;
    private String toDate;
    private String metaInfo;
    private String categories;
    private String backAndShareBarPosition;

    @Override
    protected void init() throws Exception
    {
        setMetaInfo();
        setCategories();
        final String backSharePos = getCurrentPage().getAbsoluteParent(GlobalConstants.MAGAZINE_PAGE_DEPTH).getContentResource().getValueMap().getOrDefault(GlobalConstants.MAGAZINE_BACKSHAREBAR_POSITION_PROPERTY, StringUtils.EMPTY).toString();
        backAndShareBarPosition = StringUtils.isNotBlank(backSharePos) ? backSharePos : GlobalConstants.MAGAZINE_BACKSHAREBAR_POSITION_NONE;
    }

    private void setCategories()
    {
        categories = StringUtils.EMPTY;
        final EventPageModel eventModel = getCurrentPage().adaptTo(EventPageModel.class);
        if (eventModel != null)
        {
            final List<Tag> eventTags = eventModel.getCategoryTags();
            for (final Tag eventTag : eventTags)
            {
                categories = TextUtils.appendStringObjects(categories, " | ", eventTag.getName());
            }
        }
    }

    private void setMetaInfo()
    {
        final ValueMap eventValues = getPageProperties();
        fromDate = DateUtils.formatDateToString(eventValues.get(FROM_DATE_PROPERTY, Date.class), DateUtils.NO_TIME_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
        toDate = DateUtils.formatDateToString(eventValues.get(TO_DATE_PROPERTY, Date.class), DateUtils.NO_TIME_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
        final String additionalInfo = eventValues.get(ADDITIONAL_INFO_PROPERTY, StringUtils.EMPTY);
        if (StringUtils.isNotBlank(toDate))
        {
            metaInfo = TextUtils.appendStringObjects(fromDate, " - ", toDate, " ", additionalInfo);
        }
        else
        {
            metaInfo = TextUtils.appendStringObjects(fromDate, " ", additionalInfo);
        }
    }

    public String getFromDate()
    {
        return fromDate;
    }

    public String getToDate()
    {
        return toDate;
    }

    public String getMetaInfo()
    {
        return metaInfo;
    }

    public String getCategories()
    {
        return categories;
    }

    public String getBackAndShareBarPosition()
    {
        return backAndShareBarPosition;
    }

}