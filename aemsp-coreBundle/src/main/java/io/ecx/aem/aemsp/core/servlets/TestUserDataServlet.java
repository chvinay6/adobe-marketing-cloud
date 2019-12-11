package io.ecx.aem.aemsp.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.services.LoginService;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.aem.aemsp.core.utils.UserSessionUtils;
import io.ecx.aem.aemsp.core.vo.account.AccountVO;
import io.ecx.cq.common.core.CqExceptionHandler;

@SlingServlet(generateComponent = true, paths = { "/servlets/testuserdata.json" }, metatype = true)
public class TestUserDataServlet extends SlingAllMethodsServlet
{

    private static final long serialVersionUID = -2687456729721425415L;

    private static final Logger LOG = LoggerFactory.getLogger(TestUserDataServlet.class);

    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_LOGOUT = "logout";

    @Reference
    private LoginService loginService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException
    {
        handleRequest(request, response);
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException
    {
        handleRequest(request, response);
    }

    private void handleRequest(SlingHttpServletRequest request, SlingHttpServletResponse response)
    {
        PrintWriter printWriter = null;
        try
        {
            printWriter = response.getWriter();
            final String action = request.getParameter("action");
            if (StringUtils.equals(action, ACTION_LOGIN))
            {
                login(request, printWriter);
            }
            else if (StringUtils.equals(action, ACTION_LOGOUT))
            {
                logout(request, printWriter);
            }
            else
            {
                checkStatus(request, printWriter);
            }
            printWriter.flush();
            response.setStatus(HttpStatus.SC_OK);
        }
        catch (final Exception ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
        }
        finally
        {
            if (printWriter != null)
            {
                printWriter.close();
            }
        }
    }

    private void checkStatus(SlingHttpServletRequest request, final PrintWriter printWriter)
    {
        final AccountVO account = UserSessionUtils.getAccountDataFromSession(request);
        if (account == null)
        {
            printWriter.println("No account logged in");
        }
        else
        {
            printWriter.println(TextUtils.appendStringObjects(account.getFirstName(), " ", account.getLastName(), " ", account.getStatus()));
        }

    }

    private void logout(SlingHttpServletRequest request, final PrintWriter printWriter)
    {
        AccountVO account = UserSessionUtils.getAccountDataFromSession(request);
        if (account != null)
        {
            printWriter.println(TextUtils.appendStringObjects(account.getFirstName(), " ", account.getLastName()));
            UserSessionUtils.clearUserSession(request);
            account = UserSessionUtils.getAccountDataFromSession(request);
            if (account == null)
            {
                printWriter.println("Logout Successful");
            }
        }
    }

    private void login(SlingHttpServletRequest request, final PrintWriter printWriter)
    {
        final String username = request.getParameter("login-name");
        final String password = request.getParameter("login-password");
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password))
        {
            final AccountVO userData = loginService.getUserData(username, password);
            UserSessionUtils.setAccountDataToSession(userData, request);
            final AccountVO storedAccount = UserSessionUtils.getAccountDataFromSession(request);
            if (storedAccount != null)
            {
                printWriter.println("Login Successful");
                printWriter.println(TextUtils.appendStringObjects("Name: ", storedAccount.getFirstName(), " ", storedAccount.getLastName()));
                printWriter.println(TextUtils.appendStringObjects("Status: ", storedAccount.getStatus()));
            }
        }
        else
        {
            printWriter.println("Wrong Credentials");
        }
    }

}
