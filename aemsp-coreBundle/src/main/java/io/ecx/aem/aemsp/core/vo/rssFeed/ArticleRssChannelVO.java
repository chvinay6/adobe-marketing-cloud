package io.ecx.aem.aemsp.core.vo.rssFeed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.abdera.i18n.text.io.CharsetSniffingInputStream.Encoding;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.SimpleXml;
import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.utils.DateUtils;

/**
 * @author i.mihalina
 *
 */
public class ArticleRssChannelVO
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ArticleRssChannelVO.class);
    protected ResourceResolver resourceResolver;
    protected String urlPrefix;
    protected String link;
    protected String title;
    protected String description;
    protected List<ArticleRssItemVO> items;

    public ArticleRssChannelVO(final Iterator<Page> rssPages, final String urlPrefix, final String link, final String title, final String description, final ResourceResolver resourceResolver)
    {
        this.resourceResolver = resourceResolver;
        this.urlPrefix = urlPrefix;
        this.link = link;
        this.title = title;
        this.description = description;
        items = readRssItems(rssPages);
    }

    public void generateXml(final SlingHttpServletResponse response) throws IOException
    {
        SimpleXml xml = null;

        xml = new SimpleXml(response.getWriter());
        xml.setEncoding(Encoding.UTF8.getEncoding());
        xml.openDocument();
        xml.open("rss").attr("version", "2.0").attr("xmlns:atom", "http://www.w3.org/2005/Atom");
        xml.open("channel");
        xml.open("link", getLink(), false).close();
        xml.open("atom:link").attr("rel", "self").attr("href", getLink()).close();
        xml.open("title", getTitle(), false).close();
        xml.open("description", getDescription(), false).close();
        xml.open("pubDate", DateUtils.formatRssDate(getPubDate()), false).close();
        if (items != null)
        {
            for (final ArticleRssItemVO item : items)
            {
                item.generateXml(response);
            }
        }
        xml.closeDocument();
    }

    private List<ArticleRssItemVO> readRssItems(final Iterator<Page> rssPages)
    {
        final List<ArticleRssItemVO> result = new ArrayList<>();

        if (rssPages != null)
        {
            while (rssPages.hasNext())
            {
                result.add(new ArticleRssItemVO(rssPages.next(), urlPrefix, resourceResolver));
            }
        }

        return result;
    }

    public Calendar getPubDate()
    {
        Calendar result = null;

        if (items != null)
        {
            result = items.get(0).getPubDate();
        }

        return result;
    }

    public ResourceResolver getResourceResolver()
    {
        return resourceResolver;
    }

    public String getUrlPrefix()
    {
        return urlPrefix;
    }

    public String getLink()
    {
        return link;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public List<ArticleRssItemVO> getItems()
    {
        return items;
    }

}
