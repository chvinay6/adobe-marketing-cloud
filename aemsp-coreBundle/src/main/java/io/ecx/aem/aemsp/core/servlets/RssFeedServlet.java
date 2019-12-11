package io.ecx.aem.aemsp.core.servlets;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.RssFeedModel;
import io.ecx.aem.aemsp.core.utils.ArticleUtils;
import io.ecx.aem.aemsp.core.utils.QueryUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.aem.aemsp.core.vo.rssFeed.ArticleRssChannelVO;
import io.ecx.cq.common.core.CqExceptionHandler;
import io.ecx.cq.common.core.utils.SlingRequestUtils;

/**
 * @author i.mihalina
 *
 */
@SlingServlet(resourceTypes = "sling/servlet/default", selectors = { "rssFeed" }, methods = { "GET" }, extensions = { "xml" })
public class RssFeedServlet extends SlingSafeMethodsServlet
{
    private static final Logger LOG = LoggerFactory.getLogger(RssFeedServlet.class);
    private static final long serialVersionUID = -2250281027134456975L;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            response.setContentType("text/xml");
            response.setCharacterEncoding("utf-8");

            final Resource currentResource = request.getResource();
            if (currentResource != null)
            {
                final Page currentPage = currentResource.adaptTo(Page.class);
                if (currentPage != null)
                {
                    final Resource magazinResource = currentPage.getDepth() == GlobalConstants.MAGAZINE_PAGE_DEPTH ? currentPage.getContentResource() : currentPage.getAbsoluteParent(GlobalConstants.MAGAZINE_PAGE_DEPTH).getContentResource();
                    final RssFeedModel rssModel = magazinResource.adaptTo(RssFeedModel.class);
                    if (StringUtils.isNotBlank(rssModel.getTitle()))
                    {
                        final String query = QueryUtils.generateArticleSearchQuery(GlobalConstants.ARTICLE_PAGE_RESOURCETYPE, currentPage.getPath(), GlobalConstants.ARTICLE_PUBLISH_DATE_PROPERTY, new String[] {});
                        final Iterator<Page> articles = ArticleUtils.getPages(rssModel.getArticleNumber(), query, request.getResourceResolver()).iterator();
                        final ArticleRssChannelVO rssChannel = new ArticleRssChannelVO(articles, SlingRequestUtils.getUrlPrefix(request), TextUtils.appendStringObjects(currentPage.getPath(), GlobalConstants.HTML_EXTENSION), rssModel.getTitle(), rssModel.getDescription(), request.getResourceResolver());
                        rssChannel.generateXml(response);
                    }
                }
            }
        }
        catch (final Exception ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
            throw new ServletException(ex);
        }
    }

}
