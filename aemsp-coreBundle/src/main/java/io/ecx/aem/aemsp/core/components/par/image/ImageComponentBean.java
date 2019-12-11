package io.ecx.aem.aemsp.core.components.par.image;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.components.par.ImageSightlyComponentBean;
import io.ecx.cq.common.core.vo.par.ImageResponsiveVO;

public class ImageComponentBean extends ImageSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ImageComponentBean.class);

    private ImageResponsiveVO tabletImage;
    private ImageResponsiveVO mobileImage;
    private String imageText;

    @Override
    protected void init() throws Exception
    {
        getParameter();

        final String orginalRendition = renditionSelector;

        image = new ImageResponsiveVO(getResource(), getImageRenditionInfos());

        renditionSelector = TextUtils.appendStringObjects(orginalRendition, "_tablet");
        tabletImage = new ImageResponsiveVO(getResource(), getImageRenditionInfos());

        renditionSelector = TextUtils.appendStringObjects(orginalRendition, "_mobile");
        mobileImage = new ImageResponsiveVO(getResource(), getImageRenditionInfos());

        final String caption = getPropertyAsString("caption");
        final String copyRight = getCopyRightText(getPropertyAsString("copyright"));
        imageText = TextUtils.appendStringObjects(caption, StringUtils.isNotBlank(caption) && StringUtils.isNotBlank(copyRight) ? " " : StringUtils.EMPTY, copyRight);

    }

    private String getCopyRightText(final String copyRight)
    {
        String result = StringUtils.EMPTY;

        if (StringUtils.isNotBlank(copyRight))
        {
            if (copyRight.charAt(0) == '©')
            {
                result = copyRight;
            }
            else
            {
                result = TextUtils.appendStringObjects("© ", copyRight);
            }
        }

        return result;
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

    public String getImageText()
    {
        return imageText;
    }

}
