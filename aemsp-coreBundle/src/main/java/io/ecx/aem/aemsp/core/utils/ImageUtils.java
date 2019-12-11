package io.ecx.aem.aemsp.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.cq.common.core.CqExceptionHandler;
import io.ecx.cq.common.core.vo.par.ImageRenditionInfosVO;
import io.ecx.cq.common.core.vo.par.ImageResponsiveVO;

/**
 * i.mihalina
 */
public final class ImageUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(ImageUtils.class);
    public static final String ARTICLE_IMAGE_RENDITION = "articleimage";

    private static ImageResponsiveVO generateImage(final String servletSelector, final String renditionSelector, final String fileExtension, final Resource imageResource)
    {
        ImageResponsiveVO image = null;
        try
        {
            if (imageResource != null)
            {
                final ImageRenditionInfosVO infos = new ImageRenditionInfosVO(servletSelector, renditionSelector, fileExtension);
                image = new ImageResponsiveVO(imageResource, infos);
            }
        }
        catch (final Exception e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return image;
    }

    public static List<String> getArticleImages(final Resource imageResource)
    {
        final List<String> result = new ArrayList<>();
        try
        {
            if (imageResource != null)
            {
                final ImageResponsiveVO image = generateImage(GlobalConstants.DEFAULT_SERVLET_SELECTOR, ARTICLE_IMAGE_RENDITION, GlobalConstants.DEFAULT_FILE_EXTENSION, imageResource);
                if (image != null && StringUtils.isNotBlank(image.getCachablePath()))
                {
                    result.add(image.getCachablePath());
                    result.add(generateImage(GlobalConstants.DEFAULT_SERVLET_SELECTOR, TextUtils.appendStringObjects(ARTICLE_IMAGE_RENDITION, "_tablet"), GlobalConstants.DEFAULT_FILE_EXTENSION, imageResource).getCachablePath());
                    result.add(generateImage(GlobalConstants.DEFAULT_SERVLET_SELECTOR, TextUtils.appendStringObjects(ARTICLE_IMAGE_RENDITION, "_mobile"), GlobalConstants.DEFAULT_FILE_EXTENSION, imageResource).getCachablePath());
                    result.add(image.getAlt());
                }
            }
        }
        catch (final Exception e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return result;
    }

}
