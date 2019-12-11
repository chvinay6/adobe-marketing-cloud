package io.ecx.aem.aemsp.core.slingmodels.list;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class ListComponentModel
{
    @Inject
    @Optional
    @Named("listType")
    private String listType;

    @Inject
    @Optional
    @Named("itemNumber")
    private Integer itemNumber;

    @Inject
    @Optional
    @Named("loadMoreNumber")
    private Integer loadMoreNumber;

    public String getListType()
    {
        return listType;
    }

    public Integer getItemNumber()
    {
        return itemNumber;
    }

    public Integer getLoadMoreNumber()
    {
        return loadMoreNumber;
    }
}
