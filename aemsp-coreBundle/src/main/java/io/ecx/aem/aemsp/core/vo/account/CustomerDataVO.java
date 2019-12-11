package io.ecx.aem.aemsp.core.vo.account;

import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

public class CustomerDataVO
{
    String[] messageList;
    int userAttributeId;
    String street;
    String houseNumber;
    String postalCode;
    String city;
    String telephone;
    String mobile;
    String offlineCustomer;
    String birthDate;

    public CustomerDataVO(JSONObject customerDataObject) throws JSONException
    {
        //messageList = (String[]) customerDataObject.get("messageList");
        userAttributeId = customerDataObject.getInt("userAttributeId");
        street = customerDataObject.getString("street");
        houseNumber = customerDataObject.getString("housenumber");
        postalCode = customerDataObject.getString("postalcode");
        city = customerDataObject.getString("city");
        telephone = customerDataObject.getString("telephone");
        mobile = customerDataObject.getString("mobile");
        offlineCustomer = customerDataObject.getString("offlineCustomer");
        birthDate = customerDataObject.getString("birthdate");

    }

    public String[] getMessageList()
    {
        return messageList;
    }

    public int getUserAttributeId()
    {
        return userAttributeId;
    }

    public String getStreet()
    {
        return street;
    }

    public String getHouseNumber()
    {
        return houseNumber;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public String getCity()
    {
        return city;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public String getMobile()
    {
        return mobile;
    }

    public String getOfflineCustomer()
    {
        return offlineCustomer;
    }

    public String getBirthDate()
    {
        return birthDate;
    }

}
