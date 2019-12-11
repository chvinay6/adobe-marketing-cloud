package io.ecx.aem.aemsp.core.components.par.votingmodule;

import java.io.IOException;

import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ecx.aem.aemsp.core.GlobalConstants;
import io.ecx.aem.aemsp.core.slingmodels.VotingModel;
import io.ecx.aem.aemsp.core.utils.PageUtils;
import io.ecx.aem.aemsp.core.utils.ResourceResolverUtils;
import io.ecx.aem.aemsp.core.utils.TextUtils;
import io.ecx.aem.aemsp.core.utils.VotingUtils;
import io.ecx.cq.common.core.components.AbstractSightlyComponentBean;

public class VotingModuleComponentBean extends AbstractSightlyComponentBean
{
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(VotingModuleComponentBean.class);

    private VotingModel model;
    private boolean authorMode;
    private boolean votingPage;
    private boolean validQuestions;

    @Override
    protected void init() throws Exception
    {
        authorMode = ResourceResolverUtils.isRunmodeAuthor(getService(SlingSettingsService.class));
        votingPage = PageUtils.isPageTemplate(getCurrentPage(), GlobalConstants.VOTING_MODULE_PAGE_TEMPLATE);

        redirectToHomepage();
        model = VotingUtils.getVotingModel(getResource(), getResourceResolver());
        checkQuestionListSize();
    }

    private void redirectToHomepage() throws IOException
    {
        if (votingPage && !authorMode)
        {
            getResponse().sendRedirect(TextUtils.appendStringObjects(getCurrentPage().getAbsoluteParent(GlobalConstants.LANGUAGE_PAGE_DEPTH).getPath(), GlobalConstants.HTML_EXTENSION));
        }
    }

    private void checkQuestionListSize()
    {
        if (model == null || model.getAnswerList() == null)
        {
            validQuestions = false;
        }
        else
        {
            validQuestions = model.getAnswerList().size() > 1;
        }
    }

    public boolean isValidQuestions()
    {
        return validQuestions;
    }

    public VotingModel getModel()
    {
        return model;
    }

    public boolean isAuthorMode()
    {
        return authorMode;
    }

    public boolean isVotingPage()
    {
        return votingPage;
    }

}
