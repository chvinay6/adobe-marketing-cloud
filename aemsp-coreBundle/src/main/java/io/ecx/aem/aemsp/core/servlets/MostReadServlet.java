package io.ecx.aem.aemsp.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.ArticleUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

@SlingServlet(generateComponent = true, paths = "/servlets/mostread", extensions = { "json" }, metatype = true)
public class MostReadServlet extends SlingAllMethodsServlet
{
    /**
     *
     */
    private static final long serialVersionUID = -1438579471489395942L;

    private static final Logger LOG = LoggerFactory.getLogger(MostReadServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException
    {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException
    {
        handleRequest(request, response);
    }

    private void handleRequest(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        String jsonResponse = StringUtils.EMPTY;
        try
        {
            final JSONObject json = getJsonParameter(request);
            final String componentPath = json.getString("sitepath");
            final int pageSize = json.getInt("pageSize");
            if (StringUtils.isNotBlank(componentPath))
            {
                final ResourceResolver resourceResolver = request.getResourceResolver();
                final Resource listResource = resourceResolver.getResource(componentPath);
                if (listResource != null)
                {
                    final Page searchRootPage = getSearchRootPage(listResource, resourceResolver);
                    jsonResponse = ArticleUtils.generateMostReadJsonResponse(pageSize, searchRootPage.getPath(), resourceResolver);
                }
            }
        }
        catch (final JSONException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        response.getWriter().write(jsonResponse);
    }

    private Page getSearchRootPage(final Resource listResource, ResourceResolver resourceResolver)
    {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page searchRootPage = pageManager.getContainingPage(listResource);
        if (searchRootPage.getDepth() - 1 > GlobalConstants.CATEGORY_PAGE_DEPTH)
        {
            searchRootPage = searchRootPage.getAbsoluteParent(GlobalConstants.CATEGORY_PAGE_DEPTH);
        }
        return searchRootPage;
    }

    private JSONObject getJsonParameter(final SlingHttpServletRequest request) throws IOException, JSONException
    {
        final StringBuilder sb = new StringBuilder();
        final BufferedReader br = request.getReader();
        String str;
        while ((str = br.readLine()) != null)
        {
            sb.append(str);
        }

        return new JSONObject(sb.toString());
    }
}
