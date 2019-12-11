package io.ecx.aem.aemsp.core.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.CqExceptionHandler;
import io.ecx.cq.common.core.utils.SlingRequestUtils;
import io.ecx.cq.common.core.utils.StringUtils;

@SlingServlet(resourceTypes = "sling/servlet/default", selectors = { "sitemap" }, methods = { "GET" }, extensions = { "xml" })
public class DomainGoogleSitemapServlet extends SlingSafeMethodsServlet
{
    /*
     * Copied code from GoogleSitemap in Ecx.io common, because it was not possible to resolve to the GoogleSitemap Servlet
     */
    private static final long serialVersionUID = -4501447997881617979L;
    private static final Logger LOG = LoggerFactory.getLogger(DomainGoogleSitemapServlet.class);
    private static final SimpleDateFormat DATEFORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException
    {
        StringBuilder sitemapXml = null;
        String host = null;
        ResourceResolver resourceResolver = null;
        PageManager pageManager = null;
        PageFilter pageFilter = null;

        try
        {
            resourceResolver = request.getResourceResolver();
            pageManager = resourceResolver.adaptTo(PageManager.class);
            pageFilter = getPageFilter();

            response.setContentType("text/xml");
            response.setCharacterEncoding("utf-8");

            host = getHost(request);

            sitemapXml = new StringBuilder();
            sitemapXml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
            sitemapXml.append(StringUtils.NEWLINE);
            addPageToSitemap(getCurrentPage(request, pageManager), sitemapXml, host, resourceResolver, pageFilter);
            sitemapXml.append(StringUtils.NEWLINE);
            sitemapXml.append("</urlset>");
            response.getWriter().print(sitemapXml.toString());
        }
        catch (final Exception ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
            throw new ServletException(ex);
        }
    }

    protected PageFilter getPageFilter() throws Exception
    {
        PageFilter result = null;
        result = new PageFilter(false, true);
        return result;
    }

    protected String getHost(final SlingHttpServletRequest request) throws Exception
    {
        return SlingRequestUtils.getUrlPrefix(request);
    }

    private Page getCurrentPage(final SlingHttpServletRequest request, final PageManager pageManager) throws Exception
    {
        Page result = null;

        result = pageManager.getContainingPage(request.getResource());
        return result;
    }

    protected void addPageToSitemap(final Page page, final StringBuilder sitemapXml, final String host, final ResourceResolver resourceResolver, final PageFilter pageFilter) throws Exception
    {
        Iterator<Page> children = null;
        Calendar lastModified = null;

        if ((page != null) && (page.isValid()))
        {
            sitemapXml.append("<url>");
            sitemapXml.append(StringUtils.NEWLINE);
            sitemapXml.append("<loc>");
            sitemapXml.append(host);
            sitemapXml.append(TextUtils.appendStringObjects(resourceResolver.map(page.getPath()), GlobalConstants.HTML_EXTENSION));
            sitemapXml.append("</loc>");
            sitemapXml.append(StringUtils.NEWLINE);
            lastModified = page.getLastModified();
            if (lastModified != null)
            {
                sitemapXml.append("<lastmod>");
                synchronized (DATEFORMATTER)
                {
                    sitemapXml.append(DATEFORMATTER.format(lastModified.getTime()));
                }
                sitemapXml.append("</lastmod>");
                sitemapXml.append(StringUtils.NEWLINE);
            }
            sitemapXml.append("</url>");
            sitemapXml.append(StringUtils.NEWLINE);
            children = page.listChildren(pageFilter);
            while (children.hasNext())
            {
                addPageToSitemap(children.next(), sitemapXml, host, resourceResolver, pageFilter);
            }
        }
    }
}