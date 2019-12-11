package io.ecx.aem.aemsp.core.utils;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;

import io.ecx.aem.aemsp.core.slingmodels.AemUserModel;

/**
 * i.mihalina
 */
public final class ResourceResolverUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(ResourceResolverUtils.class);

    private ResourceResolverUtils()
    {

    }

    public static ResourceResolver getReadService(final ResourceResolverFactory resourceResolverFactory) throws LoginException
    {
        final Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "contentWriterService");
        return resourceResolverFactory.getServiceResourceResolver(param);
    }

    public static ResourceResolver getWriteService(final ResourceResolverFactory resourceResolverFactory) throws LoginException
    {
        final Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "contentWriterService");
        return resourceResolverFactory.getServiceResourceResolver(param);
    }

    public static void closeResourceResolver(final ResourceResolver resourceResolver)
    {
        if (resourceResolver != null)
        {
            resourceResolver.close();
        }
    }

    public static AemUserModel getAemUserModel(final ResourceResolver resourceResolver, final String userId)
    {
        AemUserModel result = null;

        try
        {
            final UserManager userManager = resourceResolver.adaptTo(UserManager.class);
            final Authorizable auth = userManager.getAuthorizable(userId);
            if (auth != null)
            {
                result = resourceResolver.getResource(auth.getPath()).adaptTo(AemUserModel.class);
            }
        }
        catch (final RepositoryException e)
        {
            LOG.debug("Error while trying to get user model: {}", e);
        }

        return result;
    }

    public static String getAemUserFullName(final String userId, final ResourceResolver resourceResolver)
    {
        String result = userId;

        final AemUserModel user = ResourceResolverUtils.getAemUserModel(resourceResolver, userId);
        if (user != null && StringUtils.isNotBlank(user.getDisplayName()))
        {
            result = user.getDisplayName();
        }

        return result;
    }

    public static boolean isRunmodeAuthor(final SlingSettingsService service)
    {
        return service.getRunModes().contains("author");
    }

    public static boolean isRunmodePublish(final SlingSettingsService service)
    {
        return service.getRunModes().contains("publish");
    }

    public static Resource createResource(final String path, ResourceResolver resourceResolver, HashMap<String, Object> properties, boolean overWrite) throws Exception
    {
        Resource result = null;
        if (overWrite)
        {
            ResourceResolverUtils.deleteIfExists(path, resourceResolver);
        }
        if (resourceResolver.getResource(path) == null)
        {
            final String[] splitPath = StringUtils.split(path, "/");
            final Resource lastParent = createPath(splitPath, resourceResolver);
            result = resourceResolver.create(lastParent, splitPath[splitPath.length - 1], properties);
        }
        else
        {
            result = resourceResolver.getResource(path);
        }
        return result;
    }

    private static Resource createPath(final String[] splitPath, ResourceResolver resourceResolver) throws PersistenceException
    {
        final HashMap<String, Object> pathProperties = new HashMap<>();
        pathProperties.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
        final StringBuilder pathBuilder = new StringBuilder();
        Resource currentParent = resourceResolver.getResource("/");
        for (int i = 0; i < splitPath.length - 1; i++)
        {
            pathBuilder.append(TextUtils.appendStringObjects("/", splitPath[i]));
            final Resource currentPathResource = resourceResolver.getResource(pathBuilder.toString());
            if (currentPathResource == null)
            {
                currentParent = resourceResolver.create(currentParent, splitPath[i], pathProperties);
            }
            else
            {
                currentParent = currentPathResource;
            }
        }
        return currentParent;
    }

    public static void deleteIfExists(final String pathTo, final ResourceResolver resourceResolver) throws AccessDeniedException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        if (resourceResolver.getResource(pathTo) != null)
        {
            resourceResolver.getResource(pathTo).adaptTo(Node.class).remove();
        }
    }

}
