package io.ecx.aem.aemsp.core.components.par.imageslider;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.components.par.image.ImageComponentBean;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.vo.par.ImageResponsiveVO;

public class SliderImageComponentBean extends ImageComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(SliderImageComponentBean.class);

    private ImageResponsiveVO lightboxImage;
    private ImageResponsiveVO xsImage;
    private UUID sliderId;

    @Override
    protected void init() throws Exception
    {
        getParameter();
        final String orginalRendition = renditionSelector;

        super.init();

        renditionSelector = TextUtils.appendStringObjects(orginalRendition, "_lightbox");
        lightboxImage = new ImageResponsiveVO(getResource(), getImageRenditionInfos());

        renditionSelector = TextUtils.appendStringObjects(orginalRendition, "_xs");
        xsImage = new ImageResponsiveVO(getResource(), getImageRenditionInfos());

        sliderId = UUID.nameUUIDFromBytes(getResource().getParent().getParent().getPath().getBytes());
    }

    public ImageResponsiveVO getLightboxImage()
    {
        return lightboxImage;
    }

    public ImageResponsiveVO getXsImage()
    {
        return xsImage;
    }

    public String getSliderId()
    {
        return sliderId.toString();
    }
}
