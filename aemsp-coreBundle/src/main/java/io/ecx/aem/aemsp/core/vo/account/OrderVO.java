package io.ecx.aem.aemsp.core.vo.account;

import java.util.Date;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class OrderVO
{
    int id;
    String transactionId;
    String status;
    Date creationTime;
    Date lastChangeTime;
    ProductVO product;

    public OrderVO(JSONObject orderObject) throws JSONException
    {
        id = orderObject.getInt("id");
        transactionId = orderObject.getString("transactionId");
        status = orderObject.getString("status");
        creationTime = new Date(orderObject.getLong("creationTime"));
        lastChangeTime = new Date(orderObject.getLong("lastChangeTime"));
        product = new ProductVO(orderObject.getJSONObject("product"));
    }

    public int getId()
    {
        return id;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public String getStatus()
    {
        return status;
    }

    public Date getCreationTime()
    {
        return creationTime;
    }

    public Date getLastChangeTime()
    {
        return lastChangeTime;
    }

    public ProductVO getProduct()
    {
        return product;
    }

}
