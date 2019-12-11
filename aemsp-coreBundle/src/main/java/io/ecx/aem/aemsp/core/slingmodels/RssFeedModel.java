package io.ecx.aem.aemsp.core.slingmodels;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;

@Model(adaptables = Resource.class)
public class RssFeedModel
{
    @Inject
    @Optional
    @Named("rssTitle")
    private String title;

    @Inject
    @Optional
    @Named("rssDescription")
    private String description;

    @Inject
    @Optional
    @Named("rssArticleNumber")
    private int articleNumber;

    public int getArticleNumber()
    {
        return articleNumber;
    }

    public String getDescription()
    {
        return description;
    }

    public String getTitle()
    {
        return title;
    }

}
