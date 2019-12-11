package io.ecx.aem.aemsp.core.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.vo.account.AccountVO;

public class UserSessionUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(UserSessionUtils.class);

    private static final String ACCOUNT_DATA_SESSION_NAME = "accountData";
    private static final String EXCEPTION_AFTER_UPDATE_MESSAGE = "Exception after CQ-Package update";

    public static void clearUserSession(final SlingHttpServletRequest request)
    {
        setAccountDataToSession(null, request);
    }

    public static void setAccountDataToSession(final AccountVO accountData, final SlingHttpServletRequest request)
    {
        if (request == null)
        {
            throw new IllegalArgumentException("request must not be null");
        }

        request.getSession().setAttribute(ACCOUNT_DATA_SESSION_NAME, accountData);
    }

    public static AccountVO getAccountDataFromSession(final SlingHttpServletRequest request)
    {
        AccountVO result = null;
        Object sessionAttribute = null;

        if (request == null)
        {
            throw new IllegalArgumentException("request must not be null");
        }

        sessionAttribute = request.getSession().getAttribute(ACCOUNT_DATA_SESSION_NAME);
        if (sessionAttribute != null)
        {
            try
            {
                result = (AccountVO) sessionAttribute;
            }
            catch (final ClassCastException ex)
            {
                LOG.warn(EXCEPTION_AFTER_UPDATE_MESSAGE, ex);
                clearUserSession(request);
            }
        }

        return result;
    }

}
