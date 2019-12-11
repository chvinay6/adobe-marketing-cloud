package io.ecx.aem.aemsp.core.components.par.list;

import java.util.List;

public class SearchFilterVO
{
    private final FilterVO filter;
    private final List<FilterVO> items;

    public SearchFilterVO(final FilterVO filter, final List<FilterVO> items)
    {
        this.filter = filter;
        this.items = items;
    }

    public FilterVO getFilter()
    {
        return filter;
    }

    public List<FilterVO> getItems()
    {
        return items;
    }

}
