package io.ecx.aem.aemsp.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;

public class TagUtils
{
    public static String[] getTagIds(Tag[] tags)
    {
        final String[] result = new String[tags.length];
        for (int i = 0; i < tags.length; i++)
        {
            result[i] = tags[i].getTagID();
        }
        return result;
    }

    public static boolean isTagDescendantOfParent(Tag tag, Tag parent)
    {
        boolean result = false;
        Tag currentParent = tag;
        while ((currentParent = currentParent.getParent()) != null)
        {
            if (StringUtils.equals(currentParent.getTagID(), parent.getTagID()))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    public static String getBiddingPageRegion(final Resource resource, final Page page)
    {
        String result = StringUtils.EMPTY;

        final TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
        final Page offerPage = page.getParent();
        final Tag[] offerTags = tagManager.getTags(offerPage.getContentResource());
        final Tag[] biddingTags = tagManager.getTags(page.getContentResource());

        if (offerTags.length > 0)
        {
            final Tag offerRootTag = offerTags[0];
            for (final Tag tag : biddingTags)
            {
                if (TagUtils.isTagDescendantOfParent(tag, offerRootTag) || tag.equals(offerRootTag))
                {
                    result = StringUtils.join(result, tag.getName(), ", ");
                }
            }

            if (result.length() > 0)
            {
                // trim ", "
                result = StringUtils.substring(result, 0, result.length() - 2);
            }
        }

        return result;
    }
}
