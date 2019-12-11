package io.ecx.aem.aemsp.core.components.par.imageslider;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class ImageSliderComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ImageSliderComponentBean.class);
    private static final String IMAGES_NODE_NAME = "slides";
    private int numberOfImages = 0;

    static final String SLIDERID_PROP_NAME = "sliderId";

    @Override
    protected void init() throws Exception
    {
        if (getResource().getChild(IMAGES_NODE_NAME) != null)
        {
            numberOfImages = Iterables.size(getResource().getChild(IMAGES_NODE_NAME).getChildren());
        }
    }

    public List<Resource> getImages()
    {
        return Lists.newArrayList(getResource().getChild(IMAGES_NODE_NAME).getChildren());
    }

    public int getNumberOfImages()
    {
        return numberOfImages;
    }

}
