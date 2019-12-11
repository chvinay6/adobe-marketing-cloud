package io.ecx.aem.aemsp.core.components.par.languagepicker;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanguageVO
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(LanguageVO.class);

    private String localName;
    private String link;

    public LanguageVO(final String localName, final String link)
    {
        this.localName = localName;
        this.link = link;
    }

    public String getLocalName()
    {
        return localName;
    }

    public void setLocalName(final String localName)
    {
        this.localName = localName;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(final String link)
    {
        this.link = link;
    }

    /* Generated */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((link == null) ? 0 : link.hashCode());
        result = prime * result + ((localName == null) ? 0 : localName.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        LanguageVO other = null;
        if (obj instanceof LanguageVO)
        {
            other = (LanguageVO) obj;
        }
        return (this == obj) || (other != null && Objects.equals(localName, other.localName) && Objects.equals(link, other.link));
    }
}
