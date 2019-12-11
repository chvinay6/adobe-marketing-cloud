package io.ecx.aem.aemsp.core.services;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.vo.account.AccountVO;
import io.ecx.cq.common.core.CqExceptionHandler;

@Component
@Service(value = LoginService.class)
public class MockLoginServiceImpl implements LoginService
{
    private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);
    private static final String ACCESS_TOKEN_URL = "https://aemspH-test.mein-epaper.at/rest/v1/oauth/token";
    private static final String USER_DATA_URL = "https://aemspH-test.mein-epaper.at/rest/v1/api/user";

    private String getAccessToken(String username, String password)
    {
        final PostMethod postMethod = new PostMethod(ACCESS_TOKEN_URL);
        String token = null;
        try
        {
            postMethod.setRequestHeader("Accept", "application/json");
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            postMethod.addParameter("grant_type", "password");
            postMethod.addParameter("client_id", "my-trusted-client");
            postMethod.addParameter("username", username);
            postMethod.addParameter("password", password);

            final HttpClient httpClient = new HttpClient();
            final int statusCode = httpClient.executeMethod(postMethod);

            if (statusCode == HttpStatus.SC_OK)
            {
                final JSONObject jsonResponse = new JSONObject(postMethod.getResponseBodyAsString());
                token = jsonResponse.getString("access_token");
            }
        }
        catch (final Exception e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }
        finally
        {
            postMethod.releaseConnection();
        }

        return token;
    }

    @Override
    public AccountVO getUserData(String username, String password)
    {
        AccountVO result = null;
        final String token = getAccessToken(username, password);
        final GetMethod getMethod = new GetMethod(USER_DATA_URL);
        JSONObject userData = null;

        try
        {
            getMethod.setRequestHeader("Accept", "application/json");
            getMethod.setQueryString(new NameValuePair[] { new NameValuePair("access_token", token) });

            final HttpClient httpClient = new HttpClient();
            final int statusCode = httpClient.executeMethod(getMethod);

            if (statusCode == HttpStatus.SC_OK)
            {
                userData = new JSONObject(getMethod.getResponseBodyAsString());
                result = new AccountVO(userData);
            }
        }
        catch (final Exception e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }

        return result;
    }

}
