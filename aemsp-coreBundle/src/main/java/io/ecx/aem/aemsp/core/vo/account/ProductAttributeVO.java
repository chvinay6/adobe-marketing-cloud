package io.ecx.aem.aemsp.core.vo.account;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class ProductAttributeVO
{
    int id;
    String attributeName;
    String attributeValue;

    public ProductAttributeVO(JSONObject productAttributeObject) throws JSONException
    {
        id = productAttributeObject.getInt("id");
        attributeName = productAttributeObject.getString("attributeName");
        attributeValue = productAttributeObject.getString("attributeValue");
    }

    public int getId()
    {
        return id;
    }

    public String getAttributeName()
    {
        return attributeName;
    }

    public String getAttributeValue()
    {
        return attributeValue;
    }

}
