package io.ecx.aem.aemsp.core.components.par.sitemap;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.day.cq.commons.Filter;
import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.TextUtils;

public class SitemapVO
{
    private static final String START_UL_WITH_CLASS = "<ul class=\"sitemap\">";
    private static final String START_UL = "<ul>";
    private static final String START_SITE_ITEM = "<li><a href=\"";
    private static final String CLOSE_A_TAG = "\">";
    private static final String END_ITEM = "</a></li>";
    private static final String END_UL = "</ul>";

    private final Page rootPage;
    private final List<Page> descendants;

    public SitemapVO(final Page rootPage, final Filter<Page> childrenFilter)
    {
        this.rootPage = rootPage;
        descendants = new ArrayList<>();
        getDescendants(rootPage, childrenFilter);
    }

    private void getDescendants(final Page rootPage, final Filter<Page> childrenFilter)
    {
        final Iterator<Page> childPages = rootPage.listChildren(childrenFilter);
        while (childPages.hasNext())
        {
            final Page childPage = childPages.next();
            descendants.add(childPage);
            getDescendants(childPage, childrenFilter);
        }
    }

    public void draw(Writer writer)
    {
        final PrintWriter out = new PrintWriter(writer);

        out.print(TextUtils.appendStringObjects(START_UL_WITH_CLASS, START_SITE_ITEM, rootPage.getPath(), GlobalConstants.HTML_EXTENSION, CLOSE_A_TAG, rootPage.getTitle(), END_ITEM));
        int previousDepth = rootPage.getDepth();

        for (final Page descendant : descendants)
        {
            printSite(out, previousDepth, descendant);
            previousDepth = descendant.getDepth();
        }

        closeOpenListTags(out, previousDepth, rootPage.getDepth());
    }

    private void printSite(final PrintWriter out, int previousDepth, final Page descendant)
    {
        if (descendant.getDepth() > previousDepth)
        {
            out.print(START_UL);
        }
        else if (descendant.getDepth() < previousDepth)
        {
            closeOpenListTags(out, previousDepth, descendant.getDepth());
        }
        out.print(TextUtils.appendStringObjects(START_SITE_ITEM, descendant.getPath(), GlobalConstants.HTML_EXTENSION, CLOSE_A_TAG, descendant.getTitle(), END_ITEM));
    }

    private void closeOpenListTags(final PrintWriter out, int previousDepth, int currentDepth)
    {
        for (int i = currentDepth; i < previousDepth; i++)
        {
            out.print(END_UL);
        }
    }

    public Page getRootPage()
    {
        return rootPage;
    }

    public List<Page> getDescendants()
    {
        return descendants;
    }

}
