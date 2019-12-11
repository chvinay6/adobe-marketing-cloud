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

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.DateUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

@Model(adaptables = Resource.class)
public class CommercialPageModel
{
    private static final Logger LOG = LoggerFactory.getLogger(CommercialPageModel.class);

    @SlingObject
    private Resource resource;

    @Inject
    @Optional
    @Named("jcr:content/date")
    private Date date;

    @Inject
    @Optional
    @Named("jcr:content/jurisdiction")
    private String jurisdiction;

    @Inject
    @Optional
    @Named("jcr:content/parHead/commentrymainheadline/company")
    private String company;

    @Inject
    @Optional
    @Named("jcr:content/parHead/commentryprefix/region")
    private String region;

    @Inject
    @Optional
    @Named("jcr:content/category")
    private String category;

    @Inject
    @Optional
    @Named("jcr:content/parRte/rte/text")
    private String text;

    private String dateFormatted;

    private String articlePath;

    @PostConstruct
    private void init()
    {
        articlePath = resource.getPath();
        dateFormatted = date == null ? StringUtils.EMPTY : DateUtils.formatDateToString(date, DateUtils.NO_TIME_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
    }

    public String getDateFormatted()
    {
        return dateFormatted;
    }

    public String getArticlePath()
    {
        return articlePath;
    }

    public String getCategory()
    {
        return category;
    }

    public String getCompany()
    {
        return company;
    }

    public Date getDate()
    {
        return date;
    }

    public String getJurisdiction()
    {
        return jurisdiction;
    }

    public String getRegion()
    {
        return region;
    }

    public String getText()
    {
        return text;
    }

    public JSONObject getCommercialListJson()
    {
        final JSONObject result = new JSONObject();
        try
        {
            result.put("type", "commercial");
            result.put("topline", region);
            result.put("headline", company);
            result.put("text", text);
            result.put("link", TextUtils.appendStringObjects(articlePath, GlobalConstants.HTML_EXTENSION));
            result.put("metabottom", new JSONArray().put(new JSONObject().put("metatext", jurisdiction)));

            final JSONObject metaTop = new JSONObject().put("startdate", dateFormatted);
            metaTop.put("tags", new JSONArray().put(new JSONObject().put("tag", category)));
            result.put("metatop", new JSONArray().put(metaTop));
        }
        catch (final JSONException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return result;
    }
}
