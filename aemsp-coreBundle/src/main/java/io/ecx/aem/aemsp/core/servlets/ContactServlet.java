package io.ecx.aem.aemsp.core.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.i18n.I18n;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.wcm.api.Page;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

@SlingServlet(resourceTypes = { "sling/servlet/default" }, methods = { "POST" }, extensions = { "contact" })
public class ContactServlet extends SlingAllMethodsServlet
{
    private static final Logger LOG = LoggerFactory.getLogger(ContactServlet.class);
    private static final long serialVersionUID = 82760911159164034L;
    private static final String NAME_PARAM = "name";
    private static final String EMAIL_PARAM = "email";
    private static final String MSG_PARAM = "message";
    private static final String DEFAULT_TEMPLATE = "aemsp_contact_placeholder_template";
    private static final String DEFAULT_SUBJECT = "aemsp_contact_placeholder_subject";
    private static final String CONTACT_TEMPLATE_PROP = "contactTemplate";
    private static final String CONTACT_SUBJECT_PROP = "contactSubject";
    private static final String RECIPIENT_EMAIL_PROP = "recipient";

    @Reference
    private MessageGatewayService messageGatewayService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
    {
        try
        {
            final String name = request.getParameter(NAME_PARAM);
            final String email = request.getParameter(EMAIL_PARAM);
            final String msg = request.getParameter(MSG_PARAM);

            if (StringUtils.isBlank(name) || StringUtils.isBlank(email) || StringUtils.isBlank(msg))
            {
                throw new ServletException("Request contains blank parameter(s)");
            }

            final Page currentPage = request.getResource().adaptTo(Page.class);
            final Resource langPageContent = currentPage.getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH).getContentResource();
            final ValueMap langPageVM = langPageContent.getValueMap();

            final String template = getStringOrDefaultFromVM(CONTACT_TEMPLATE_PROP, DEFAULT_TEMPLATE, langPageVM, request);
            final String subject = getStringOrDefaultFromVM(CONTACT_SUBJECT_PROP, DEFAULT_SUBJECT, langPageVM, request);
            final String recipient = langPageVM.get(RECIPIENT_EMAIL_PROP, String.class);
            if (recipient == null)
            {
                throw new ServletException("Receiver is null");
            }

            sendEmail(name, email, msg, subject, recipient, template);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.getWriter().flush();
        }
        catch (final Exception e)
        {
            CqExceptionHandler.handleException(e, LOG);
            sendInternalServerError(response);
        }

    }

    private String getStringOrDefaultFromVM(final String propName, final String defaultValue, final ValueMap vm, final SlingHttpServletRequest request)
    {
        final String prop = vm.get(propName, String.class);
        return (prop == null) ? I18n.get(request, defaultValue) : prop;
    }

    private void sendInternalServerError(final SlingHttpServletResponse response)
    {
        try
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        catch (final IOException e)
        {
            CqExceptionHandler.handleException(e, LOG);
        }
    }

    private void sendEmail(final String name, final String addr, final String msg, final String subject, final String recipient, String template) throws EmailException
    {
        final HtmlEmail email = new HtmlEmail();
        email.addTo(recipient);
        email.setSubject(subject);

        final String nameTemplate = StringUtils.replace(template, TextUtils.appendStringObjects("${", NAME_PARAM, "}"), name);
        final String nameAddrTemplate = StringUtils.replace(nameTemplate, TextUtils.appendStringObjects("${", EMAIL_PARAM, "}"), addr);
        final String nameAddrMsgTemplate = StringUtils.replace(nameAddrTemplate, TextUtils.appendStringObjects("${", MSG_PARAM, "}"), msg);

        email.setMsg(nameAddrMsgTemplate);

        final MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
        messageGateway.send(email);
    }
}
