package io.ecx.aem.aemsp.core.vo.account;

import java.util.Date;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class AppSaleTypeVO
{
    int appSaleTypeId;
    String prodIdent;
    String info;
    String validDuration;
    Date lastChanged;
    String status;
    String platform;

    public AppSaleTypeVO(JSONObject appSaleTypeObject) throws JSONException
    {
        appSaleTypeId = appSaleTypeObject.getInt("appSaleTypeId");
        prodIdent = appSaleTypeObject.getString("prodIdent");
        info = appSaleTypeObject.getString("info");
        validDuration = appSaleTypeObject.getString("validDuration");
        lastChanged = new Date(appSaleTypeObject.getLong("lastChanged"));
        status = appSaleTypeObject.getString("status");
        platform = appSaleTypeObject.getString("platform");
    }

    public int getAppSaleTypeId()
    {
        return appSaleTypeId;
    }

    public void setAppSaleTypeId(int appSaleTypeId)
    {
        this.appSaleTypeId = appSaleTypeId;
    }

    public String getProdIdent()
    {
        return prodIdent;
    }

    public void setProdIdent(String prodIdent)
    {
        this.prodIdent = prodIdent;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public String getValidDuration()
    {
        return validDuration;
    }

    public void setValidDuration(String validDuration)
    {
        this.validDuration = validDuration;
    }

    public Date getLastChanged()
    {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged)
    {
        this.lastChanged = lastChanged;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPlatform()
    {
        return platform;
    }

    public void setPlatform(String platform)
    {
        this.platform = platform;
    }

}
