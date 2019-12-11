package io.ecx.aem.aemsp.core.servlets;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.cq.common.core.CqExceptionHandler;

@SlingServlet(resourceTypes = "sling/servlet/default", selectors = { "searchtags" }, methods = { "GET" }, extensions = { "json" })
public class SearchSuggestionsServlet extends SlingSafeMethodsServlet
{
    private static final Logger LOG = LoggerFactory.getLogger(SearchSuggestionsServlet.class);
    private static final long serialVersionUID = -8855015623824827818L;
    private static final String SEARCHROOT_PROP = "searchRootTag";
    private static final String SEARCH_PARAM = "term";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
    {
        try
        {
            final PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
            final Page langPage = pageManager.getContainingPage(request.getResource()).getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
            final Resource langPageContent = langPage.getContentResource();

            final String rootTagId = langPageContent.getValueMap().get(SEARCHROOT_PROP, String.class);
            final Locale locale = langPage.getLanguage(false);

            final PrintWriter out = response.getWriter();
            final JSONArray tagsArray = new JSONArray();

            if (!StringUtils.isBlank(rootTagId))
            {
                final TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
                final Tag root = tagManager.resolve(rootTagId);

                final Iterator<Tag> it = root.listAllSubTags();

                final String searchTerm = request.getParameter(SEARCH_PARAM);

                while (it.hasNext())
                {
                    final JSONObject tagsObj = new JSONObject();
                    final Tag tag = it.next();
                    final String label = tag.getLocalizedTitle(locale) == null ? tag.getTitle() : tag.getLocalizedTitle(locale);

                    if (StringUtils.startsWith(label, searchTerm))
                    {
                        tagsObj.put("label", label);
                        tagsObj.put("value", tag.getTagID());
                        tagsArray.put(tagsObj);
                    }
                }
            }

            final JSONObject obj = new JSONObject();
            obj.put("tags", tagsArray);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");

            out.print(obj);
            out.flush();
        }
        catch (final Exception e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }
    }

}
