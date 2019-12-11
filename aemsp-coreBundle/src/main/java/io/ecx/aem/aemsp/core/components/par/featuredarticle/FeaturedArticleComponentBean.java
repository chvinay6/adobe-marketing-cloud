package io.ecx.aem.aemsp.core.components.par.featuredarticle;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.i18n.I18n;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.ArticlePageModel;
import io.ecx.aem.aemsp.core.utils.ArticleUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.components.par.ImageSightlyComponentBean;
import io.ecx.cq.common.core.vo.par.ImageResponsiveVO;

public class FeaturedArticleComponentBean extends ImageSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(FeaturedArticleComponentBean.class);

    private static final String FEATURED_ARTICLE_ARTICLEPATH = "articlePath";
    private static final String RENDITIONS_THUMBNAIL_EXTENSION = "_thumbnail";

    private boolean selectionValid = false;
    private String validationMessage;
    private boolean breakingNews = false;
    private ImageResponsiveVO tabletImage;
    private ImageResponsiveVO mobileImage;
    private String articleLink;
    private String articlePrefixText;
    private String articleMainHeadline;
    private String articleTeaserText;
    private String metaInfo;

    @Override
    protected void init() throws Exception
    {
        getParameter();
        final I18n i18n = new I18n(getRequest());
        final String articlePath = getPropertyAsString(FEATURED_ARTICLE_ARTICLEPATH);
        articleLink = TextUtils.appendStringObjects(articlePath, GlobalConstants.HTML_EXTENSION);
        final Resource selectedResource = getResourceResolver().getResource(articlePath);
        validationMessage = ArticleUtils.isValidArticle(selectedResource, getResourcePage().getName(), i18n);
        selectionValid = StringUtils.equals(validationMessage, ArticleUtils.ARTICLE_VALID);
        if (selectionValid)
        {
            final ArticlePageModel articleModel = selectedResource.adaptTo(ArticlePageModel.class);
            getArticleValues(articleModel);
            if (isThumbnail())
            {
                defineImageRenditions(articleModel.getArticleThumbnailResource(), RENDITIONS_THUMBNAIL_EXTENSION);
            }
            else
            {
                defineImageRenditions(articleModel.getArticleImageResource(), StringUtils.EMPTY);
            }
        }

    }

    private boolean isThumbnail()
    {
        boolean result = false;
        final Resource parsys = getResource().getParent();
        final String parsysName = parsys.getName();
        if (StringUtils.equals(parsysName, GlobalConstants.SIDEBAR_CONTENT))
        {
            result = true;
        }
        return result;
    }

    private void defineImageRenditions(final Resource imageResource, final String extension) throws Exception
    {
        renditionSelector = TextUtils.appendStringObjects(renditionSelector, extension);
        final String originalRendition = renditionSelector;
        image = new ImageResponsiveVO(imageResource, getImageRenditionInfos());

        renditionSelector = TextUtils.appendStringObjects(originalRendition, "_tablet");
        tabletImage = new ImageResponsiveVO(imageResource, getImageRenditionInfos());

        renditionSelector = TextUtils.appendStringObjects(originalRendition, "_mobile");
        mobileImage = new ImageResponsiveVO(imageResource, getImageRenditionInfos());
    }

    private void getArticleValues(final ArticlePageModel articleModel) throws Exception
    {
        metaInfo = articleModel.getPublishDate() == null ? articleModel.getCreatedDateFormatted() : articleModel.getPublishDateFormatted();
        breakingNews = articleModel.isBreakingNews();
        articlePrefixText = articleModel.getArticlePrefix();
        articleMainHeadline = articleModel.getArticleMainHeadline();
        articleTeaserText = articleModel.getArticleTeaserText();
    }

    public boolean isSelectionValid()
    {
        return selectionValid;
    }

    public String getValidationMessage()
    {
        return validationMessage;
    }

    public boolean isBreakingNews()
    {
        return breakingNews;
    }

    public String getArticleLink()
    {
        return articleLink;
    }

    public String getArticlePrefixText()
    {
        return articlePrefixText;
    }

    public String getArticleMainHeadline()
    {
        return articleMainHeadline;
    }

    public String getArticleTeaserText()
    {
        return articleTeaserText;
    }

    public String getMetaInfo()
    {
        return metaInfo;
    }

    public ImageResponsiveVO getDesktopImage()
    {
        return image;
    }

    public ImageResponsiveVO getTabletImage()
    {
        return tabletImage;
    }

    public ImageResponsiveVO getMobileImage()
    {
        return mobileImage;
    }

}
