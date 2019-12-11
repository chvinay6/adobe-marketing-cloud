package io.ecx.aem.aemsp.core.components.par.relatedarticle;

import org.apache.commons.lang.StringUtils;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.ArticlePageModel;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.vo.par.ImageResponsiveVO;

public class ArticleListItemVO
{
    private final ArticlePageModel article;
    private final String articlePath;
    private final ImageResponsiveVO image;
    private final ImageResponsiveVO tabletImage;
    private final ImageResponsiveVO mobileImage;

    public ArticleListItemVO(final ArticlePageModel article, final String articlePath, final ImageResponsiveVO image, final ImageResponsiveVO tabletImage, final ImageResponsiveVO mobileImage)
    {
        this.article = article;
        this.articlePath = articlePath;
        this.image = image;
        this.tabletImage = tabletImage;
        this.mobileImage = mobileImage;
    }

    public ArticlePageModel getArticle()
    {
        return article;
    }

    public String getArticlePath()
    {
        return articlePath;
    }

    public String getArticleLink()
    {
        String articleLink = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(articlePath))
        {
            articleLink = TextUtils.appendStringObjects(articlePath, GlobalConstants.HTML_EXTENSION);
        }
        return articleLink;
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
