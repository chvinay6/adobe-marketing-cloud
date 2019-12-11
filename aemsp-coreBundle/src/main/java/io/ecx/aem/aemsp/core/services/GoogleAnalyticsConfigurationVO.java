package io.ecx.aem.aemsp.core.services;

public class GoogleAnalyticsConfigurationVO
{
    private String profileId;
    private String schedulingExpression;
    private String rootPath;
    private String gserviceEmail;
    private String privateKey;
    private String metrics;
    private String dimensions;
    private String startDate;
    private String endDate;
    private String sort;
    private String maxResults;

    public String getProfileId()
    {
        return profileId;
    }

    public void setProfileId(String profileId)
    {
        this.profileId = profileId;
    }

    public String getSchedulingExpression()
    {
        return schedulingExpression;
    }

    public void setSchedulingExpression(String schedulingExpression)
    {
        this.schedulingExpression = schedulingExpression;
    }

    public String getRootPath()
    {
        return rootPath;
    }

    public void setRootPath(String rootPath)
    {
        this.rootPath = rootPath;
    }

    public String getGserviceEmail()
    {
        return gserviceEmail;
    }

    public void setGserviceEmail(String gserviceEmail)
    {
        this.gserviceEmail = gserviceEmail;
    }

    public String getPrivateKey()
    {
        return privateKey;
    }

    public void setPrivateKey(String privateKey)
    {
        this.privateKey = privateKey;
    }

    public String getMetrics()
    {
        return metrics;
    }

    public void setMetrics(String metrics)
    {
        this.metrics = metrics;
    }

    public String getDimensions()
    {
        return dimensions;
    }

    public void setDimensions(String dimensions)
    {
        this.dimensions = dimensions;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getSort()
    {
        return sort;
    }

    public void setSort(String sort)
    {
        this.sort = sort;
    }

    public String getMaxResults()
    {
        return maxResults;
    }

    public void setMaxResults(String maxResults)
    {
        this.maxResults = maxResults;
    }
}