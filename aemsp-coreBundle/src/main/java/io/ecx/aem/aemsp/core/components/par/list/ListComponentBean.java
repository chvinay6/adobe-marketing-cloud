package io.ecx.aem.aemsp.core.components.par.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.Lists;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.list.ListComponentModel;
import io.ecx.aem.aemsp.core.utils.ArticleListUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class ListComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ListComponentBean.class);

    private String listJson;
    private int maxItems;
    private int loadMore;
    private String breakingNewsText;
    private List<SearchFilterVO> filterList;
    private boolean validList = false;

    @Override
    protected void init() throws Exception
    {
        final Page languagePage = getResourcePage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
        final ListComponentModel listModel = getResource().adaptTo(ListComponentModel.class);
        if (listModel != null)
        {
            breakingNewsText = languagePage.getProperties().get(GlobalConstants.BREAKING_NEWS_TEXT_PROPERTY, String.class);
            maxItems = listModel.getItemNumber();
            loadMore = listModel.getLoadMoreNumber();
            listJson = ArticleListUtils.generateListJsonResponse(listModel.getListType(), maxItems, getCurrentPage().getPath(), getResourceResolver(), new String[] {});
            validList = true;
        }
        filterList = defineFilterList();
    }

    private List<SearchFilterVO> defineFilterList()
    {
        final List<SearchFilterVO> result = new ArrayList<>();

        final String[] filters = getProperties().get("filters", String[].class);
        if (filters != null)
        {
            final TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
            for (final String filter : filters)
            {
                final Tag tag = tagManager.resolve(filter);
                if (tag != null)
                {
                    final Iterator<Tag> tags = tag.listChildren();
                    if (tags != null && tags.hasNext())
                    {
                        final List<Tag> filterItems = Lists.newArrayList(tags);
                        final List<FilterVO> filterItem = new ArrayList<>();
                        for (final Tag filerTag : filterItems)
                        {
                            filterItem.add(new FilterVO(filerTag.getTitle(getCurrentPage().getLanguage(true)), filerTag.getTagID()));
                        }

                        result.add(new SearchFilterVO(new FilterVO(tag.getTitle(getCurrentPage().getLanguage(true)), tag.getTagID()), filterItem));
                    }
                }
            }
        }

        return result;
    }

    public String getListJson()
    {
        return listJson;
    }

    public String getBreakingNewsText()
    {
        return breakingNewsText;
    }

    public int getMaxItems()
    {
        return maxItems;
    }

    public int getLoadMore()
    {
        return loadMore;
    }

    public List<SearchFilterVO> getFilterList()
    {
        return filterList;
    }

    public boolean isValidList()
    {
        return validList;
    }

}
