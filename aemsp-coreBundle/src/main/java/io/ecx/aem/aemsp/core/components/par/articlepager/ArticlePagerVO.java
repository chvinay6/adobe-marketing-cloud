package io.ecx.aem.aemsp.core.components.par.articlepager;

public class ArticlePagerVO
{
    private final String date;
    private final String headline;
    private final String path;

    public ArticlePagerVO(final String date, final String headline, final String path)
    {
        this.date = date;
        this.headline = headline;
        this.path = path;
    }

    public String getHeadline()
    {
        return headline;
    }

    public String getDate()
    {
        return date;
    }

    public String getPath()
    {
        return path;
    }

}
