package io.ecx.aem.aemsp.core.components.par.upcomingevents;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.EventPageModel;
import io.ecx.aem.aemsp.core.utils.ArticleUtils;
import io.ecx.aem.aemsp.core.utils.QueryUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class UpcomingEventsComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(UpcomingEventsComponentBean.class);
    private static final int MAX_EVENTS_DEFAULT = 3;
    private static final String MAX_EVENTS_PROP = "maxEvents";

    private final List<EventPageModel> events = new LinkedList<>();

    @Override
    protected void init() throws Exception
    {
        final int maxEvents = getProperties().get(MAX_EVENTS_PROP, MAX_EVENTS_DEFAULT);

        final Page langPage = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
        final String query = QueryUtils.generateUpcomingPagesSearchQuery(GlobalConstants.EVENT_PAGE_RESOURCETYPE, langPage.getPath(), GlobalConstants.EVENT_PAGE_FROMDATE_PROPERTY, "ASC", new String[] {});
        final List<Page> eventPages = ArticleUtils.getPages(maxEvents, query, getResourceResolver());
        for (final Page page : eventPages)
        {
            final EventPageModel eventModel = page.adaptTo(EventPageModel.class);
            if (eventModel != null)
            {
                events.add(eventModel);
            }
        }
    }

    public boolean isEventsExist()
    {
        return !events.isEmpty();
    }

    public List<EventPageModel> getEvents()
    {
        return events;
    }

}
