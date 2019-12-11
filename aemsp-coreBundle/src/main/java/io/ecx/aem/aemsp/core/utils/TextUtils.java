package io.ecx.aem.aemsp.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * i.mihalina
 */
public final class TextUtils
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(TextUtils.class);

    private TextUtils()
    {

    }

    public static String appendStringObjects(final String... objects)
    {
        final StringBuilder result = new StringBuilder();

        for (final String object : objects)
        {
            if (StringUtils.isNotEmpty(object))
            {
                result.append(object);
            }
        }

        return result.toString();
    }

}
