package io.ecx.aem.aemsp.core.components.par.languagepicker;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class LanguagePickerComponentBean extends AbstractSightlyComponentBean
{
    private static final String LANG_PROP_PREFIX = "lang_";
    private final Set<LanguageVO> languages = new HashSet<>();
    private String currentLanguage;

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(LanguagePickerComponentBean.class);

    @Override
    protected void init() throws Exception
    {
        final Resource pageContent = getCurrentPage().getContentResource();
        final Set<String> keys = pageContent.getValueMap().keySet();
        for (final String key : keys)
        {
            if (StringUtils.startsWith(key, LANG_PROP_PREFIX))
            {
                final Locale loc = new Locale(StringUtils.substringAfterLast(key, LANG_PROP_PREFIX));
                languages.add(new LanguageVO(loc.getDisplayLanguage(loc), pageContent.getValueMap().get(key) + GlobalConstants.HTML_EXTENSION)); // link is always internal
            }
        }

        final Locale currentLoc = getCurrentPage().getLanguage(true);
        currentLanguage = currentLoc.getDisplayLanguage(currentLoc);
    }

    public Set<LanguageVO> getLanguageProps()
    {
        return languages;
    }

    public String getCurrentLanguage()
    {
        return currentLanguage;
    }

    public boolean isHasMultipleVersions()
    {
        return !languages.isEmpty();
    }
}
