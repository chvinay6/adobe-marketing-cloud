package io.ecx.aem.aemsp.core.components.par.sharemodule;

import java.net.URLEncoder;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class ShareModuleComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ShareModuleComponentBean.class);

    private static final String MAINIMAGE_RES = GlobalConstants.PAR_ARTICLE_IMAGE + "/" + GlobalConstants.ARTICLE_IMAGE_COMPONENT_NAME;
    private static final String IMGSERVLET_EXT = "." + GlobalConstants.DEFAULT_SERVLET_SELECTOR + "." + GlobalConstants.ARTICLE_IMAGE_COMPONENT_NAME + "." + GlobalConstants.DEFAULT_IMAGE_FORMAT;

    private String encodedTitle;
    private String encodedURL;
    private String encodedImageURL;

    private Resource pageContent;

    @Override
    protected void init() throws Exception
    {
        pageContent = getCurrentPage().getContentResource();

        if (pageContent != null)
        {
            final ValueMap vm = pageContent.getValueMap();
            final String title = vm.get(JcrConstants.JCR_TITLE, String.class);
            final String url = getRequest().getRequestURL().toString();
            encodedTitle = URLEncoder.encode(title, CharEncoding.UTF_8);
            encodedURL = URLEncoder.encode(url, CharEncoding.UTF_8);

            final String imagePath = getPinterestImage();
            if (StringUtils.isNotEmpty(imagePath))
            {
                final String canonicalImageURL = getCanonicalImageURL(imagePath, getService(SlingSettingsService.class));
                encodedImageURL = URLEncoder.encode(canonicalImageURL, CharEncoding.UTF_8);
            }
        }
    }

    private String getCanonicalImageURL(String imagePath, SlingSettingsService service)
    {
        String canonicalURL = null;

        final ResourceResolver resourceResolver = getRequest().getResourceResolver();
        final Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);

        if (ResourceResolverUtils.isRunmodeAuthor(service))
        {
            canonicalURL = externalizer.externalLink(resourceResolver, Externalizer.AUTHOR, imagePath);
        }
        else
        {
            canonicalURL = externalizer.externalLink(resourceResolver, Externalizer.PUBLISH, imagePath);
        }

        return canonicalURL;
    }

    private String getPinterestImage()
    {
        String path = getArticleImage();

        if (StringUtils.isEmpty(path))
        {
            // no article image, check thumbnail
            path = getThumbnail();
        }

        if (StringUtils.isEmpty(path))
        {
            // no thumbnail. check homepage logo
            path = getHomePageLogo();
        }

        return path;
    }

    public String getEncodedTitle()
    {
        return encodedTitle;
    }

    public String getEncodedURL()
    {
        return encodedURL;
    }

    public String getEncodedImageURL()
    {
        return encodedImageURL;
    }

    private String getArticleImage()
    {
        String path = null;

        final Resource articleImage = pageContent.getChild(MAINIMAGE_RES);

        if (articleImage != null)
        {
            final ValueMap articleImageVM = articleImage.getValueMap();
            final String fileReference = articleImageVM.get(GlobalConstants.DEFAULT_IMAGE_FILEREFERENCE_PROPERTY, String.class);

            if (StringUtils.isNotEmpty(fileReference))
            {
                // has DAM image
                path = fileReference;
            }
            else if (StringUtils.isNotEmpty(articleImageVM.get(GlobalConstants.DEFAULT_IMAGE_FILENAME_PROPERTY, String.class)))
            {
                // has uploaded image
                path = StringUtils.join(articleImage.getPath(), IMGSERVLET_EXT);
            }
        }

        return path;
    }

    private String getHomePageLogo()
    {
        String path = null;

        final Resource langPageContent = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH).getContentResource();
        final String logoPath = langPageContent.getValueMap().get(GlobalConstants.MAGAZINE_LOGO_PATH_PROPERTY, String.class);

        if (StringUtils.isNotEmpty(logoPath))
        {
            path = logoPath;
        }

        return path;
    }

    private String getThumbnail()
    {
        String path = null;

        final Resource articleThumbnail = pageContent.getChild(GlobalConstants.ARTICLE_THUMBNAIL_COMPONENT_NAME);

        if (articleThumbnail != null)
        {
            final ValueMap articleThumbnailVM = articleThumbnail.getValueMap();
            final String fileReference = articleThumbnailVM.get(GlobalConstants.DEFAULT_IMAGE_FILEREFERENCE_PROPERTY, String.class);
            if (StringUtils.isNotEmpty(fileReference))
            {
                path = fileReference;
            }
        }

        return path;
    }

}
