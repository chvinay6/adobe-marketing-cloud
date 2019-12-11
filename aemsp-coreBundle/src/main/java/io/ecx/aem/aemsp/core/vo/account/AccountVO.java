package io.ecx.aem.aemsp.core.vo.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.cq.common.core.CqExceptionHandler;

public class AccountVO
{
    private static final Logger LOG = LoggerFactory.getLogger(AccountVO.class);

    String[] messageList;
    int userId;
    String firstName;
    String lastName;
    String email;
    String userName;
    Date lastLogin;
    String status;
    String type;
    Date lastChanged;
    int lastChangedBy;
    Date created;
    int createdBy;
    List<PermissionVO> permissions;
    Date validFrom;
    String tracking;
    String comment;
    String salutation;
    CustomerDataVO customerData;
    String[] userAttributes;
    int maxLogin;
    int deleteDaysForApp;
    String acceptAGB;
    String onlyIPad;
    List<OrderVO> orders;

    public AccountVO(JSONObject userData)
    {
        try
        {
            readValues(userData);
            customerData = new CustomerDataVO(userData.getJSONObject("customerData"));

            final JSONArray perms = userData.getJSONArray("permissions");
            permissions = new ArrayList<>();
            for (int i = 0; i < perms.length(); i++)
            {
                permissions.add(new PermissionVO(perms.getJSONObject(i)));
            }

            final JSONArray ordrs = userData.getJSONArray("orders");
            orders = new ArrayList<>();
            for (int i = 0; i < ordrs.length(); i++)
            {
                orders.add(new OrderVO(ordrs.getJSONObject(i)));
            }
        }
        catch (final JSONException ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
        }

    }

    private void readValues(final JSONObject accountObject) throws JSONException
    {
        //messageList = (String[]) accountObject.get("messageList");
        userId = accountObject.getInt("userId");
        firstName = accountObject.getString("firstname");
        lastName = accountObject.getString("lastname");
        email = accountObject.getString("email");
        userName = accountObject.getString("username");
        lastLogin = new Date(accountObject.getLong("lastLogin"));
        status = accountObject.getString("status");
        type = accountObject.getString("type");
        lastChanged = new Date(accountObject.getLong("lastChanged"));
        lastChangedBy = accountObject.getInt("lastChangedBy");
        created = new Date(accountObject.getLong("created"));
        createdBy = accountObject.getInt("createdBy");
        validFrom = new Date(accountObject.getLong("validFrom"));
        tracking = accountObject.getString("tracking");
        comment = accountObject.getString("comment");
        salutation = accountObject.getString("salutation");
        //userAttributes = (String[]) accountObject.get("userAttributes");
        maxLogin = accountObject.getInt("maxLogin");
        deleteDaysForApp = accountObject.getInt("deleteDaysForApp");
        acceptAGB = accountObject.getString("acceptAGB");
        onlyIPad = accountObject.getString("onlyIPad");
    }

    public String[] getMessageList()
    {
        return messageList;
    }

    public int getUserId()
    {
        return userId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public String getUserName()
    {
        return userName;
    }

    public Date getLastLogin()
    {
        return lastLogin;
    }

    public String getStatus()
    {
        return status;
    }

    public String getType()
    {
        return type;
    }

    public Date getLastChanged()
    {
        return lastChanged;
    }

    public int getLastChangedBy()
    {
        return lastChangedBy;
    }

    public Date getCreated()
    {
        return created;
    }

    public int getCreatedBy()
    {
        return createdBy;
    }

    public List<PermissionVO> getPermissions()
    {
        return permissions;
    }

    public Date getValidFrom()
    {
        return validFrom;
    }

    public String getTracking()
    {
        return tracking;
    }

    public String getComment()
    {
        return comment;
    }

    public String getSalutation()
    {
        return salutation;
    }

    public CustomerDataVO getCustomerData()
    {
        return customerData;
    }

    public String[] getUserAttributes()
    {
        return userAttributes;
    }

    public int getMaxLogin()
    {
        return maxLogin;
    }

    public int getDeleteDaysForApp()
    {
        return deleteDaysForApp;
    }

    public String getAcceptAGB()
    {
        return acceptAGB;
    }

    public String getOnlyIPad()
    {
        return onlyIPad;
    }

    public List<OrderVO> getOrders()
    {
        return orders;
    }

}
