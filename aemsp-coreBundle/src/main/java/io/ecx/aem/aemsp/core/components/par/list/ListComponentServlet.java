package io.ecx.aem.aemsp.core.components.par.list;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.PageManager;

import io.ecx.aem.aemsp.core.slingmodels.list.ListComponentModel;
import io.ecx.aem.aemsp.core.utils.ArticleListUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

/**
 * @author i.mihalina
 *
 */
@SlingServlet(generateComponent = true, paths = "/servlets/listcomponent", extensions = { "json" }, metatype = true)
public class ListComponentServlet extends SlingAllMethodsServlet
{
    private static final long serialVersionUID = -3153534642437040676L;
    private static final Logger LOG = LoggerFactory.getLogger(ListComponentServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException
    {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException
    {
        handleRequest(request, response);
    }

    private void handleRequest(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        String jsonResponse = StringUtils.EMPTY;
        try
        {
            final JSONObject json = getJsonParameter(request);
            final String componentPath = json.getString("sitepath");
            final int pageSize = json.getInt("pageSize");
            final int loadMore = json.getInt("loadMoreSize");
            final int currentPage = pageSize + (json.getInt("page") - 1) * loadMore;
            final String[] filter = getFiltersFromJson(json.getJSONArray("filter"));
            if (StringUtils.isNotBlank(componentPath))
            {
                final Resource listResource = request.getResourceResolver().getResource(componentPath);
                if (listResource != null)
                {
                    final ListComponentModel listModel = listResource.adaptTo(ListComponentModel.class);
                    final PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
                    jsonResponse = ArticleListUtils.generateListJsonResponse(listModel.getListType(), currentPage + loadMore, pageManager.getContainingPage(listResource).getPath(), request.getResourceResolver(), filter);
                }
            }
        }
        catch (final JSONException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        response.getWriter().write(jsonResponse);
    }

    private String[] getFiltersFromJson(final JSONArray filtersJson) throws JSONException
    {
        final String[] result = new String[filtersJson.length()];
        for (int i = 0; i < filtersJson.length(); i++)
        {
            result[i] = filtersJson.getString(i);
        }

        return result;
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
