package io.ecx.aem.aemsp.core.utils;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.commons.lang3.StringUtils;

import com.day.cq.commons.jcr.JcrConstants;

public final class NodeUtils
{
    private NodeUtils()
    {
    }

    public static Node createNodeIfNotExists(final Node parent, final String nodeName) throws PathNotFoundException, RepositoryException
    {
        Node result;
        if (parent.hasNode(nodeName))
        {
            result = parent.getNode(nodeName);
        }
        else
        {
            result = parent.addNode(nodeName, JcrConstants.NT_UNSTRUCTURED);
        }
        return result;
    }

    public static void deleteChildNodeIfExists(final Node contentNode, final String nodeName) throws AccessDeniedException, VersionException, LockException, ConstraintViolationException, PathNotFoundException, RepositoryException
    {
        if (contentNode.hasNode(nodeName))
        {
            contentNode.getNode(nodeName).remove();
        }
    }

    public static void checkAndSetProperty(final Node node, final String propertyName, final String propertyValue, final String alternativeValue, final String defaultValue) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        if (StringUtils.isNotBlank(propertyValue))
        {
            node.setProperty(propertyName, propertyValue);
        }
        else if (StringUtils.isNotBlank(alternativeValue))
        {
            node.setProperty(propertyName, alternativeValue);
        }
        else
        {
            node.setProperty(propertyName, defaultValue);
        }
    }

}
