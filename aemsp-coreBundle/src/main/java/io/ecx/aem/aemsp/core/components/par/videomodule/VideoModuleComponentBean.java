package io.ecx.aem.aemsp.core.components.par.videomodule;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class VideoModuleComponentBean extends AbstractSightlyComponentBean
{

    private static final Logger LOG = LoggerFactory.getLogger(VideoModuleComponentBean.class);
    private static final String TITLE_PROP = "title";
    private static final String CAPTION_PROP = "caption";
    private static final String COPYRIGHT_PROP = "copyright";
    private static final String VIDEOID_PROP = "id";
    private static final String VIDEOTYPE_PROP = "type";
    private static final String VIDEOTYPE_YOUTUBE = "youtube";
    private static final String VIDEOTYPE_VIMEO = "vimeo";
    private static final String YOUTUBE_URL_PREFIX = "https://www.youtube.com/embed/";
    private static final String VIMEO_URL_PREFIX = "https://player.vimeo.com/video/";
    private static final int YOUTUBE_WIDTH = 560;
    private static final int YOUTUBE_HEIGHT = 315;
    private static final int VIMEO_WIDTH = 640;
    private static final int VIMEO_HEIGHT = 358;

    private String title;
    private String caption;
    private String copyright;
    private String type;
    private String url;
    private int width;
    private int height;

    @Override
    protected void init() throws Exception
    {
        final ValueMap vm = getResource().getValueMap();
        title = vm.get(TITLE_PROP, String.class);
        caption = vm.get(CAPTION_PROP, String.class);
        copyright = vm.get(COPYRIGHT_PROP, String.class);
        type = vm.get(VIDEOTYPE_PROP, String.class);
        final String videoId = vm.get(VIDEOID_PROP, String.class);

        if (VIDEOTYPE_YOUTUBE.equals(type))
        {
            url = YOUTUBE_URL_PREFIX.concat(videoId);
            width = YOUTUBE_WIDTH;
            height = YOUTUBE_HEIGHT;
        }
        else if (VIDEOTYPE_VIMEO.equals(type))
        {
            url = VIMEO_URL_PREFIX.concat(videoId);
            width = VIMEO_WIDTH;
            height = VIMEO_HEIGHT;
        }
        else
        {
            LOG.error(TextUtils.appendStringObjects("Video type '", type, "' not supported"));
        }
    }

    public String getTitle()
    {
        return title;
    }

    public String getCaption()
    {
        return StringUtils.isNotEmpty(caption) ? caption.concat(" ") : null;
    }

    public String getCopyright()
    {
        return StringUtils.isNotEmpty(copyright) ? "© ".concat(copyright) : null;
    }

    public String getType()
    {
        return type;
    }

    public String getUrl()
    {
        return url;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

}
