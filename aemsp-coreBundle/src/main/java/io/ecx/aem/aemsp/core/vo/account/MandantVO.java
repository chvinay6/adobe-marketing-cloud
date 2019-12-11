package io.ecx.aem.aemsp.core.vo.account;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class MandantVO
{
    String[] messageList;
    int mandantId;
    String name;
    String description;
    String status;

    public MandantVO(JSONObject mandantObject) throws JSONException
    {
        //messageList = (String[]) mandantObject.get("messageList");
        mandantId = mandantObject.getInt("mandant_id");
        name = mandantObject.getString("name");
        description = mandantObject.getString("description");
        status = mandantObject.getString("status");
    }

    public String[] getMessageList()
    {
        return messageList;
    }

    public int getMandantId()
    {
        return mandantId;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getStatus()
    {
        return status;
    }

}
