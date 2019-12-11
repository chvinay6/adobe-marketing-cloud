package io.ecx.aem.aemsp.core.components.page.base;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class IconComponentBean extends AbstractSightlyComponentBean
{

    private static String FAVICON_PATH_PROPERTY = "favIconPath";
    private static String RESOLUTION_57X57_PROPERTY = "touchIconPath57x57";
    private static String RESOLUTION_60X60_PROPERTY = "touchIconPath60x60";
    private static String RESOLUTION_72X72_PROPERTY = "touchIconPath72x72";
    private static String RESOLUTION_76X76_PROPERTY = "touchIconPath76x76";
    private static String RESOLUTION_114X114_PROPERTY = "touchIconPath114x114";
    private static String RESOLUTION_120X120_PROPERTY = "touchIconPath120x120";
    private static String RESOLUTION_144X144_PROPERTY = "touchIconPath144x144";
    private static String RESOLUTION_152X152_PROPERTY = "touchIconPath152x152";
    private static String RESOLUTION_180X180_PROPERTY = "touchIconPath180x180";

    private String faviconPath;
    private String touchiconPath128;
    private String touchiconPath192;
    private String touchiconPath57;
    private String touchiconPath60;
    private String touchiconPath72;
    private String touchiconPath76;
    private String touchiconPath114;
    private String touchiconPath120;
    private String touchiconPath144;
    private String touchiconPath152;
    private String touchiconPath180;

    @Override
    protected void init()
    {
        final Page languagePage = getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH);
        if (languagePage != null)
        {
            final ValueMap languageProperties = languagePage.getProperties();
            getLanguageProperties(languageProperties);
        }
    }

    private void getLanguageProperties(final ValueMap languageProperties)
    {
        faviconPath = languageProperties.get(FAVICON_PATH_PROPERTY, StringUtils.EMPTY);
        touchiconPath57 = languageProperties.get(RESOLUTION_57X57_PROPERTY, StringUtils.EMPTY);
        touchiconPath60 = languageProperties.get(RESOLUTION_60X60_PROPERTY, StringUtils.EMPTY);
        touchiconPath72 = languageProperties.get(RESOLUTION_72X72_PROPERTY, StringUtils.EMPTY);
        touchiconPath76 = languageProperties.get(RESOLUTION_76X76_PROPERTY, StringUtils.EMPTY);
        touchiconPath114 = languageProperties.get(RESOLUTION_114X114_PROPERTY, StringUtils.EMPTY);
        touchiconPath120 = languageProperties.get(RESOLUTION_120X120_PROPERTY, StringUtils.EMPTY);
        touchiconPath144 = languageProperties.get(RESOLUTION_144X144_PROPERTY, StringUtils.EMPTY);
        touchiconPath152 = languageProperties.get(RESOLUTION_152X152_PROPERTY, StringUtils.EMPTY);
        touchiconPath180 = languageProperties.get(RESOLUTION_180X180_PROPERTY, StringUtils.EMPTY);
    }

    public String getFaviconPath()
    {
        return faviconPath;
    }

    public String getTouchiconPath128()
    {
        return touchiconPath128;
    }

    public String getTouchiconPath192()
    {
        return touchiconPath192;
    }

    public String getTouchiconPath57()
    {
        return touchiconPath57;
    }

    public String getTouchiconPath60()
    {
        return touchiconPath60;
    }

    public String getTouchiconPath72()
    {
        return touchiconPath72;
    }

    public String getTouchiconPath76()
    {
        return touchiconPath76;
    }

    public String getTouchiconPath114()
    {
        return touchiconPath114;
    }

    public String getTouchiconPath120()
    {
        return touchiconPath120;
    }

    public String getTouchiconPath144()
    {
        return touchiconPath144;
    }

    public String getTouchiconPath152()
    {
        return touchiconPath152;
    }

    public String getTouchiconPath180()
    {
        return touchiconPath180;
    }

}
