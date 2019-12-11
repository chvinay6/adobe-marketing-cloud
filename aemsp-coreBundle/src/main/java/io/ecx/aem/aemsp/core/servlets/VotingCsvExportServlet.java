package io.ecx.aem.aemsp.core.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.text.csv.Csv;

import io.ecx.aem.aemsp.core.slingmodels.VotingModel;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.aem.aemsp.core.utils.VotingUtils;
import io.ecx.cq.common.core.CqExceptionHandler;

/**
 * @author i.mihalina
 *
 */
@SlingServlet(resourceTypes = "sling/servlet/default", selectors = { "votingexport" }, extensions = { "csv" })
public class VotingCsvExportServlet extends SlingAllMethodsServlet
{
    private static final long serialVersionUID = 1925510850952508099L;
    private static final Logger LOG = LoggerFactory.getLogger(VotingCsvExportServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            response.setContentType("text/csv");
            response.setCharacterEncoding(CharEncoding.UTF_8);

            final ResourceResolver resolver = request.getResourceResolver();
            final VotingModel model = VotingUtils.getVotingModel(request.getResource(), resolver);
            if (model != null)
            {
                final Csv csvFile = generateVotingCsvFile(request, response, model);
                csvFile.close();
            }
        }
        catch (final Exception ex)
        {
            CqExceptionHandler.handleException(ex, LOG);
        }
    }

    private Csv generateVotingCsvFile(final SlingHttpServletRequest request, final SlingHttpServletResponse response, final VotingModel voting) throws IOException, RepositoryException
    {
        final Csv csvFile = new Csv();
        final OutputStream outputStream = response.getOutputStream();
        outputStream.write(239); //set BOM for Excel
        outputStream.write(187); //set BOM for Excel
        outputStream.write(191); //set BOM for Excel

        csvFile.setFieldSeparatorWrite(";");
        csvFile.writeInit(outputStream, CharEncoding.UTF_8);

        if (StringUtils.isNotBlank(voting.getUniqueId()))
        {
            response.setHeader("Content-Disposition", TextUtils.appendStringObjects("attachment; filename=\"", voting.getHeadline(), ".csv\""));
            csvFile.writeRow(voting.getHeadline());
            csvFile.writeRow(voting.getQuestion());
            csvFile.writeRow(StringUtils.EMPTY);

            final Resource votingResource = request.getResourceResolver().getResource(TextUtils.appendStringObjects(VotingUtils.USERGENERATED_PATH, voting.getUniqueId()));
            if (votingResource != null)
            {
                final Node votingNode = votingResource.adaptTo(Node.class);
                final float number = VotingUtils.getVotingCountNumber(votingNode, true);

                final NodeIterator answers = votingNode.getNodes();
                int sum = 0;
                while (answers.hasNext())
                {
                    final Node answerNode = answers.nextNode();
                    if (answerNode.hasNode(JcrConstants.JCR_CONTENT))
                    {
                        final Node contentNode = answerNode.getNode(JcrConstants.JCR_CONTENT);
                        final long answerCount = contentNode.getProperty(VotingUtils.COUNT_TEXT).getLong() - contentNode.getProperty(VotingUtils.START_VALUE).getLong();
                        final int percentage = VotingUtils.getAnswerPercentage(!answers.hasNext(), answerCount, sum, number);
                        sum += percentage;

                        csvFile.writeRow(contentNode.getProperty(VotingUtils.VALUE_TEXT).getString(), String.valueOf(answerCount), TextUtils.appendStringObjects(String.valueOf(percentage), " %"));
                    }
                }
            }
        }

        LOG.debug("CSV file phonebookExport.csv created.");
        return csvFile;
    }
}
