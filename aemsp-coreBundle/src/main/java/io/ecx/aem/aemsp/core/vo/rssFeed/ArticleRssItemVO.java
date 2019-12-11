package io.ecx.aem.aemsp.core.vo.rssFeed;

import java.io.IOException;
import java.util.Calendar;

import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.SimpleXml;
import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.ArticlePageModel;
import io.ecx.aem.aemsp.core.utils.DateUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;

/**
 * @author i.mihalina
 *
 */
public class ArticleRssItemVO
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ArticleRssItemVO.class);

    protected ResourceResolver resourceResolver;
    protected Page page;
    protected String urlPrefix;
    protected String link;
    protected String title;
    protected String description;
    protected Calendar pubDate;
    protected String author;

    public ArticleRssItemVO(final Page page, final String urlPrefix, final ResourceResolver resourceResolver)
    {
        final ArticlePageModel article = page.adaptTo(Resource.class).adaptTo(ArticlePageModel.class);
        this.resourceResolver = resourceResolver;
        this.page = page;
        this.urlPrefix = urlPrefix;
        link = page.getPath();
        title = article.getArticleMainHeadline();
        description = article.getArticleTeaserText();
        pubDate = article.getPublishDateCalendar();
        author = article.getArticlePublisherFullName();
    }

    public String getLink()
    {
        return TextUtils.appendStringObjects(urlPrefix, link, GlobalConstants.HTML_EXTENSION);
    }

    public void setLink(final String link)
    {
        this.link = link;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(final String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public Calendar getPubDate()
    {
        return pubDate;
    }

    public void setPubDate(final Calendar pubDate)
    {
        this.pubDate = pubDate;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(final String author)
    {
        this.author = author;
    }

    public void generateXml(final SlingHttpServletResponse response) throws IOException
    {
        SimpleXml xml = null;
        xml = new SimpleXml(response.getWriter());
        xml.omitXmlDeclaration(true);
        xml.open("item");
        xml.open("link", getLink(), false).close();
        xml.open("guid", getLink(), false).close();
        xml.open("title", getTitle(), false).close();
        xml.open("description", getDescription(), true).close();
        xml.open("pubDate", DateUtils.formatRssDate(getPubDate()), false).close();
        xml.open("author", getAuthor(), true).close();
        xml.tidyUp();
    }

}
