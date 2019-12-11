package io.ecx.aem.aemsp.core.slingmodels;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.DateUtils;
import io.ecx.aem.aemsp.core.utils.TagUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

@Model(adaptables = Resource.class)
public class OfferPageModel
{
    private static final Logger LOG = LoggerFactory.getLogger(OfferPageModel.class);

    @SlingObject
    private Resource resource;

    @Inject
    @Optional
    @Named("jcr:content/endDate")
    private Date endDate;

    @Inject
    @Optional
    @Named("jcr:content/parHead/biddingmainheadline/offer")
    private String title;

    @Inject
    @Optional
    @Named("jcr:content/parRte/rte/text")
    private String text;

    private String dateFormatted;
    private String articlePath;
    private String region;

    @PostConstruct
    private void init()
    {
        articlePath = resource.getPath();
        region = TagUtils.getBiddingPageRegion(resource, getBiddingPage(resource));
        dateFormatted = endDate == null ? StringUtils.EMPTY : DateUtils.formatDateToString(endDate, DateUtils.NO_TIME_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
    }

    private Page getBiddingPage(final Resource resource)
    {
        final PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
        return pageManager.getContainingPage(resource);
    }

    public String getDateFormatted()
    {
        return dateFormatted;
    }

    public String getArticlePath()
    {
        return articlePath;
    }

    public String getRegion()
    {
        return region;
    }

    public String getText()
    {
        return text;
    }

    public JSONObject getOfferListJson()
    {
        final JSONObject result = new JSONObject();
        try
        {
            result.put("type", "offer");
            result.put("topline", region);
            result.put("headline", title);
            result.put("text", text);
            result.put("link", TextUtils.appendStringObjects(articlePath, GlobalConstants.HTML_EXTENSION));
            result.put("metatop", new JSONArray().put(new JSONObject().put("startdate", dateFormatted)));
        }
        catch (final JSONException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return result;
    }
}
