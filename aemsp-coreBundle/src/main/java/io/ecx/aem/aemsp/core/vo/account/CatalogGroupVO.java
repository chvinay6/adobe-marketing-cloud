package io.ecx.aem.aemsp.core.vo.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class CatalogGroupVO
{
    String[] messageList;
    int catalogGroupId;
    String name;
    String description;
    int creatorId;
    MandantVO mandant;
    int archiveCount;
    boolean pdfDownloadDenied;
    String[] aboTypes;
    String changeAllowedFor;
    int deltaMinutesToPublish;
    boolean archiveEnabled;
    boolean cdnEnabled;
    int cdnNofIssues;
    List<AppSaleTypeVO> appSaleTypes;

    public CatalogGroupVO(JSONObject catalogObject) throws JSONException
    {
        readValues(catalogObject);
        mandant = new MandantVO(catalogObject.getJSONObject("mandant"));

        final JSONArray saleTypes = catalogObject.getJSONArray("appSaleTypes");
        appSaleTypes = new ArrayList<>();
        for (int i = 0; i < saleTypes.length(); i++)
        {
            appSaleTypes.add(new AppSaleTypeVO(saleTypes.getJSONObject(i)));
        }
    }

    private void readValues(JSONObject catalogObject) throws JSONException
    {
        //messageList = (String[]) catalogObject.get("messageList");
        catalogGroupId = catalogObject.getInt("catalogGroupId");
        name = catalogObject.getString("name");
        description = catalogObject.getString("description");
        creatorId = catalogObject.getInt("creatorId");
        archiveCount = catalogObject.getInt("archiveCount");
        pdfDownloadDenied = catalogObject.getBoolean("pdfDownloadDenied");
        //aboTypes = (String[]) catalogObject.get("aboTypes");
        changeAllowedFor = catalogObject.getString("changeAllowedFor");
        deltaMinutesToPublish = catalogObject.getInt("deltaMinutesToPublish");
        archiveEnabled = catalogObject.getBoolean("archiveEnabled");
        cdnEnabled = catalogObject.getBoolean("cdnEnabled");
        cdnNofIssues = catalogObject.getInt("cdnNofIssues");
    }

    public String[] getMessageList()
    {
        return messageList;
    }

    public int getCatalogGroupId()
    {
        return catalogGroupId;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public int getCreatorId()
    {
        return creatorId;
    }

    public MandantVO getMandant()
    {
        return mandant;
    }

    public int getArchiveCount()
    {
        return archiveCount;
    }

    public boolean isPdfDownloadDenied()
    {
        return pdfDownloadDenied;
    }

    public String[] getAboTypes()
    {
        return aboTypes;
    }

    public String getChangeAllowedFor()
    {
        return changeAllowedFor;
    }

    public int getDeltaMinutesToPublish()
    {
        return deltaMinutesToPublish;
    }

    public boolean isArchiveEnabled()
    {
        return archiveEnabled;
    }

    public boolean isCdnEnabled()
    {
        return cdnEnabled;
    }

    public int getCdnNofIssues()
    {
        return cdnNofIssues;
    }

    public List<AppSaleTypeVO> getAppSaleTypes()
    {
        return appSaleTypes;
    }

}
