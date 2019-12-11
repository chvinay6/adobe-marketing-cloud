package io.ecx.aem.aemsp.core.components.par.list;

public class FilterVO
{
    private final String name;
    private final String value;

    public FilterVO(final String name, final String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

}
