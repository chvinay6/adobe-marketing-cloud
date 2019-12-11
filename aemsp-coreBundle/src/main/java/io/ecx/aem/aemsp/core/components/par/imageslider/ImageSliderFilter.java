package io.ecx.aem.aemsp.core.components.par.imageslider;

import java.io.IOException;
import java.util.Iterator;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingFilter;
import org.apache.felix.scr.annotations.sling.SlingFilterScope;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

@SlingFilter(label = "ImageSliderComponent Filter", description = "Adapts the Image/Slide nodes created by the Multifield (TouchUIMultifield.js) to be compatible with ImageComponentBean. Constants set according to imageslider component dialog.", generateComponent = true, generateService = true, scope = SlingFilterScope.REQUEST, order = Integer.MAX_VALUE)
public class ImageSliderFilter implements Filter
{
    private static final Logger LOG = LoggerFactory.getLogger(ImageSliderFilter.class);
    private static final String IMAGESLIDER_COMPONENT_FQN = "aemsp/components/par/imageslider";
    private static final String SLIDES_FIELDSET_NAME = "slides";
    private static final String SLIDE_FILE_NAME_PARAMETER = "slideName";
    private static final String SLIDE_FILE_REFERENCE_PARAMETER = "slideReference";

    @Reference
    protected SlingSettingsService slingSettingsService;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
    {
        chain.doFilter(request, response);
        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        final Resource res = slingRequest.getResource();
        final Resource slides = res.getChild(SLIDES_FIELDSET_NAME);

        if (ResourceResolverUtils.isRunmodeAuthor(slingSettingsService) && StringUtils.equals(res.getResourceType(), IMAGESLIDER_COMPONENT_FQN) && StringUtils.equals(slingRequest.getParameter("./sling:resourceType"), IMAGESLIDER_COMPONENT_FQN) && slides != null)
        {
            final Iterator<Resource> it = slides.listChildren();
            while (it.hasNext())
            {
                final Resource slide = it.next();
                final ValueMap slideVM = slide.adaptTo(ValueMap.class);
                final Session session = slingRequest.getResourceResolver().adaptTo(Session.class);
                final boolean isUploadedImage = !StringUtils.equals(slideVM.get(SLIDE_FILE_NAME_PARAMETER, String.class), "false");

                if (isUploadedImage && slide.getChild("file") == null)
                {
                    // Uploaded image, ImageComponentBean needs it under the slide node and named "file", copy it from source
                    final Workspace workspace = session.getWorkspace();
                    final String src = slideVM.get(SLIDE_FILE_REFERENCE_PARAMETER, String.class).replaceAll("_jcr_", "jcr:");
                    final String dest = TextUtils.appendStringObjects(slide.getPath(), "/file");
                    try
                    {
                        workspace.copy(src, dest);
                        session.save();
                    }
                    catch (final RepositoryException e)
                    {
                        CqExceptionHandler.handleException(e, LOG);
                    }
                }
                else if (!isUploadedImage && slideVM.get("fileReference") == null)
                {
                    // DAM image, ImageComponentBean needs fileReference property for those
                    try
                    {
                        session.getNode(slide.getPath()).setProperty("fileReference", slideVM.get(SLIDE_FILE_REFERENCE_PARAMETER, String.class)); // replaceAll not needed? DAM path doesn't contain colons
                        session.save();
                    }
                    catch (final RepositoryException e)
                    {
                        CqExceptionHandler.handleException(e, LOG);
                    }
                }
                else
                {
                    // nop
                }
            }
        }
    }

    @Override
    public void destroy()
    {
        // do nothing
    }

    @Override
    public void init(final FilterConfig arg0) throws ServletException
    {
        // do nothing
    }

}