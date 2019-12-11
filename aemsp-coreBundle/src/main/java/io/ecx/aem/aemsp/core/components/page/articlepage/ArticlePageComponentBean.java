package io.ecx.aem.aemsp.core.components.page.articlepage;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.ArticlePageModel;
import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class ArticlePageComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ArticlePageComponentBean.class);

    private boolean breakingNews;
    private String breakingNewsText;
    private String publishDate;
    private String author;
    private String backAndShareBarPosition;
    private boolean validThumb;

    @Override
    protected void init() throws Exception
    {
        final SlingSettingsService settings = getService(SlingSettingsService.class);
        final ArticlePageModel articleModel = getResourcePage().adaptTo(ArticlePageModel.class);

        breakingNews = articleModel.isBreakingNews();
        if (breakingNews)
        {
            final Page languagePage = getResourcePage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
            breakingNewsText = languagePage.getProperties().get(GlobalConstants.BREAKING_NEWS_TEXT_PROPERTY, String.class);
        }

        validThumb = checkArticleThumbnail(articleModel.getArticleThumbnailPath());

        if (ResourceResolverUtils.isRunmodeAuthor(settings))
        {
            publishDate = articleModel.getCreatedDateFormatted();
            author = ResourceResolverUtils.getAemUserModel(getResourceResolver(), articleModel.getCreatedBy()).getDisplayName();
        }
        else
        {
            publishDate = articleModel.getPublishDateFormatted();
            author = articleModel.getArticlePublisherFullName();
        }

        final String backSharePos = getCurrentPage().getAbsoluteParent(GlobalConstants.MAGAZINE_PAGE_DEPTH).getContentResource().getValueMap().getOrDefault(GlobalConstants.MAGAZINE_BACKSHAREBAR_POSITION_PROPERTY, StringUtils.EMPTY).toString();
        backAndShareBarPosition = StringUtils.isNotBlank(backSharePos) ? backSharePos : GlobalConstants.MAGAZINE_BACKSHAREBAR_POSITION_NONE;
    }

    private boolean checkArticleThumbnail(final String path)
    {
        boolean result = false;

        if (StringUtils.isNotBlank(path))
        {
            final Resource image = getResourceResolver().getResource(path);
            if (image != null && image.adaptTo(Asset.class) != null)
            {
                result = true;
            }
        }

        return result;
    }

    public boolean isBreakingNews()
    {
        return breakingNews;
    }

    public String getBreakingNewsText()
    {
        return breakingNewsText;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getPublishDate()
    {
        return publishDate;
    }

    public String getBackAndShareBarPosition()
    {
        return backAndShareBarPosition;
    }

    public boolean isValidThumb()
    {
        return validThumb;
    }
}