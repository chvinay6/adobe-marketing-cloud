package io.ecx.aem.aemsp.core.servlets;

import org.apache.felix.scr.annotations.sling.SlingServlet;

import io.ecx.cq.common.core.images.DefaultImageFitInServlet;

@SlingServlet(resourceTypes = "sling/servlet/default", selectors = "fitIn", methods = { "GET" }, extensions = { "jpg", "png" })
public class ImageFitInServlet extends DefaultImageFitInServlet
{
    private static final long serialVersionUID = 1186863006388776784L;

}