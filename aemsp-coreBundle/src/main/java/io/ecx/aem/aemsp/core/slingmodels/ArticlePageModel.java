package io.ecx.aem.aemsp.core.slingmodels;

import java.util.Calendar;
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
import io.ecx.aem.aemsp.core.utils.ArticleUtils;
import io.ecx.aem.aemsp.core.utils.DateUtils;
import io.ecx.aem.aemsp.core.utils.ImageUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

@Model(adaptables = Resource.class)
public class ArticlePageModel
{
    private static final Logger LOG = LoggerFactory.getLogger(ArticlePageModel.class);

    @SlingObject
    private Resource resource;

    @Inject
    @Optional
    @Named("jcr:created")
    private Date created;

    @Inject
    @Optional
    @Named("jcr:createdBy")
    private String createdBy;

    @Inject
    @Optional
    @Named("jcr:content/articlePublisher")
    private String articlePublisher;

    @Inject
    @Optional
    @Named("jcr:content/articlePublisherFullName")
    private String articlePublisherFullName;

    @Inject
    @Optional
    @Named("jcr:content/publishDate")
    private Date publishDate;

    @Inject
    @Optional
    @Named("jcr:content/publishDate")
    private Calendar publishDateCalendar;

    @Inject
    @Optional
    @Named("jcr:content/cq:lastReplicated")
    private Date lastReplicated;

    @Inject
    @Optional
    @Named("jcr:content/cq:lastReplicatedBy")
    private String lastReplicatedBy;

    @Inject
    @Optional
    @Named("jcr:content/cq:lastReplicationAction")
    private String lastReplicatedAction;

    @Inject
    @Optional
    @Named("jcr:content/breakingNewsDate")
    private Date breakingNewsDate;

    @Inject
    @Optional
    @Named("jcr:content/parTeaser/articleteaser/text")
    private String articleTeaserText;

    @Inject
    @Optional
    @Named("jcr:content/parHead/articleprefix/text")
    private String articlePrefix;

    @Inject
    @Optional
    @Named("jcr:content/parHead/articlemainheadline/text")
    private String articleMainHeadline;

    @Inject
    @Optional
    @Named("jcr:content/parHead/articlesubline/text")
    private String articleSubline;

    @Inject
    @Optional
    @Named("jcr:content/articleThumbnail")
    private Resource articleThumbnailResource;

    @Inject
    @Optional
    @Named("jcr:content/articleThumbnail/fileReference")
    private String articleThumbnailPath;

    @Inject
    @Optional
    @Named("jcr:content/parArticleImage/articleimage")
    private Resource articleImageResource;

    @Inject
    @Optional
    @Named("jcr:content/parArticleImage/articleimage/fileReference")
    private String articleImagePath;

    private boolean breakingNews;
    private String publishDateFormatted;
    private String createdDateFormatted;
    private String articlePath;

    @PostConstruct
    private void init()
    {
        articlePath = resource.getPath();
        breakingNews = breakingNewsDate == null ? false : breakingNewsDate.after(new Date());
        publishDateFormatted = publishDate == null ? StringUtils.EMPTY : DateUtils.formatDateToString(publishDate, DateUtils.DISPLY_DATE_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
        createdDateFormatted = created == null ? StringUtils.EMPTY : DateUtils.formatDateToString(created, DateUtils.DISPLY_DATE_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
    }

    public String getArticlePath()
    {
        return articlePath;
    }

    public Date getCreated()
    {
        return created;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public String getArticlePublisher()
    {
        return articlePublisher;
    }

    public Date getPublishDate()
    {
        return publishDate;
    }

    public String getMetaInfo()
    {
        final Date metaInfoDate = publishDate == null ? created : publishDate;
        return DateUtils.formatDateToString(metaInfoDate, DateUtils.DISPLY_DATE_FORMAT, GlobalConstants.DEFAULT_LANGUAGE_LOCALE);
    }

    public Date getLastReplicated()
    {
        return lastReplicated;
    }

    public String getLastReplicatedBy()
    {
        return lastReplicatedBy;
    }

    public String getLastReplicatedAction()
    {
        return lastReplicatedAction;
    }

    public Date getBreakingNewsDate()
    {
        return breakingNewsDate;
    }

    public String getArticlePublisherFullName()
    {
        return articlePublisherFullName;
    }

    public Resource getArticleImageResource()
    {
        return articleImageResource;
    }

    public String getArticleThumbnailPath()
    {
        return articleThumbnailPath;
    }

    public Resource getArticleThumbnailResource()
    {
        return articleThumbnailResource;
    }

    public String getArticleImagePath()
    {
        return articleImagePath;
    }

    public String getArticleMainHeadline()
    {
        return articleMainHeadline;
    }

    public String getArticlePrefix()
    {
        return articlePrefix;
    }

    public String getArticleSubline()
    {
        return articleSubline;
    }

    public String getArticleTeaserText()
    {
        return articleTeaserText;
    }

    public String getTruncatedArticleTeaserText()
    {
        return ArticleUtils.truncateTeaserTextForList(articleTeaserText);
    }

    public Calendar getPublishDateCalendar()
    {
        return publishDateCalendar;
    }

    public boolean isBreakingNews()
    {
        return breakingNews;
    }

    public String getCreatedDateFormatted()
    {
        return createdDateFormatted;
    }

    public String getPublishDateFormatted()
    {
        return publishDateFormatted;
    }

    public JSONObject getArticleListJson()
    {
        final JSONObject result = new JSONObject();
        try
        {
            result.put("type", "article");
            result.put("metabottom", new JSONArray().put(new JSONObject().put("datetime", publishDateFormatted)));
            result.put("toparticle", breakingNews);
            result.put("topline", articlePrefix);
            result.put("headline", articleMainHeadline);
            result.put("text", articleTeaserText);
            result.put("image", ImageUtils.getArticleImages(articleThumbnailResource));
            result.put("link", TextUtils.appendStringObjects(articlePath, GlobalConstants.HTML_EXTENSION));
        }
        catch (final JSONException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return result;
    }
}
