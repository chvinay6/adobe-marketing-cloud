package io.ecx.aem.aemsp.core.slingmodels;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.DateUtils;
import io.ecx.aem.aemsp.core.utils.TagUtils;

@Model(adaptables = Resource.class)
public class EventPageModel
{
    @SlingObject
    private Resource resource;

    @Inject
    private ResourceResolver resourceResolver;

    @Inject
    @Optional
    @Named("jcr:content/parHead/articleprefix/text")
    private String location;

    @Inject
    @Optional
    @Named("jcr:content/parHead/articlemainheadline/text")
    private String headline;

    @Inject
    @Optional
    @Named("jcr:content/parRte/shorttext/text")
    @Default(values = StringUtils.EMPTY)
    private String shortText;

    @Inject
    @Optional
    @Named("jcr:content/fromDate")
    private Date fromDate;

    @Inject
    @Optional
    @Named("jcr:content/toDate")
    private Date toDate;

    @Inject
    @Optional
    @Named("jcr:content/additionalInfo")
    @Default(values = StringUtils.EMPTY)
    private String additionalInfo;

    private final List<Tag> categoryTags = new LinkedList<>();
    private String eventPath;

    @PostConstruct
    private void init()
    {
        eventPath = resource.getPath();
        setCategoryTags();
    }

    private void setCategoryTags()
    {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        final Page eventPage = pageManager.getContainingPage(resource);
        final Page calendarParent = eventPage.getParent();
        final Tag[] calendarTags = tagManager.getTags(calendarParent.getContentResource());
        final Tag[] eventTags = tagManager.getTags(eventPage.getContentResource());

        if (calendarTags.length > 0)
        {
            final Tag calendarRootTag = calendarTags[0];
            for (final Tag eventTag : eventTags)
            {
                if (TagUtils.isTagDescendantOfParent(eventTag, calendarRootTag) || eventTag.equals(calendarRootTag))
                {
                    categoryTags.add(eventTag);
                }
            }
        }
    }

    public List<Tag> getCategoryTags()
    {
        return categoryTags;
    }

    public String getEventPath()
    {
        return eventPath;
    }

    public String getLocation()
    {
        return location;
    }

    public String getHeadline()
    {
        return headline;
    }

    public String getShortText()
    {
        return shortText;
    }

    public String getFromDate()
    {
        return DateUtils.formatDateToString(fromDate, DateUtils.NO_TIME_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
    }

    public String getToDate()
    {
        return DateUtils.formatDateToString(toDate, DateUtils.NO_TIME_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
    }

    public String getAdditionalInfo()
    {
        return additionalInfo;
    }
}
