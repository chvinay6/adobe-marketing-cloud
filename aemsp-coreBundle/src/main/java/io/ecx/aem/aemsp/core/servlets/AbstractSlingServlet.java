package io.ecx.aem.aemsp.core.servlets;

import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;

public abstract class AbstractSlingServlet extends SlingAllMethodsServlet
{
    private static final long serialVersionUID = 5783317055824408400L;

    @Reference
    protected SlingSettingsService slingSettingsService;

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            handleRequest(request, response);
        }
        catch (final RepositoryException e)
        {
            handleException(e);
        }
    }

    protected abstract void handleRequest(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException, RepositoryException;

    protected abstract void handleException(final RepositoryException e);

}
