package io.ecx.aem.aemsp.core.servlets;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.services.LoginService;
import io.ecx.aem.aemsp.core.utils.UserSessionUtils;
import io.ecx.aem.aemsp.core.vo.account.AccountVO;

@SlingServlet(generateComponent = true, paths = { "/servlets/login" }, methods = { "POST" }, metatype = true)
public class LoginServlet extends SlingAllMethodsServlet
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(SearchSuggestionsServlet.class);
    private static final long serialVersionUID = -7649080023030701037L;

    @Reference
    private LoginService loginService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException
    {
        UserSessionUtils.clearUserSession(request);
        final String username = request.getParameter("login-name");
        final String password = request.getParameter("login-password");
        final AccountVO userData = loginService.getUserData(username, password);
        if (userData == null)
        {
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        else
        {
            UserSessionUtils.setAccountDataToSession(userData, request);
            response.setStatus(HttpStatus.SC_OK);
        }
    }

}
