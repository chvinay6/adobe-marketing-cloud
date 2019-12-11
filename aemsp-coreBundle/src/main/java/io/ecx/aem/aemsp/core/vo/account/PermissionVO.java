package io.ecx.aem.aemsp.core.vo.account;

import java.util.Date;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class PermissionVO
{
    String[] messageList;
    int permissionId;
    String catalogRight;
    Date validFrom;
    String firstOfMonthValidation;
    CatalogGroupVO catalogGroup;

    public PermissionVO(JSONObject permissionObject) throws JSONException
    {
        //messageList = (String[]) permissionObject.get("messageList");
        permissionId = permissionObject.getInt("permissionId");
        catalogRight = permissionObject.getString("catalogRight");
        validFrom = new Date(permissionObject.getLong("validFrom"));
        firstOfMonthValidation = permissionObject.getString("firstOfMonthLimitation");
        catalogGroup = new CatalogGroupVO(permissionObject.getJSONObject("catalogGroup"));
    }

    public String[] getMessageList()
    {
        return messageList;
    }

    public int getPermissionId()
    {
        return permissionId;
    }

    public String getCatalogRight()
    {
        return catalogRight;
    }

    public Date getValidFrom()
    {
        return validFrom;
    }

    public String getFirstOfMonthValidation()
    {
        return firstOfMonthValidation;
    }

    public CatalogGroupVO getCatalogGroup()
    {
        return catalogGroup;
    }

}
