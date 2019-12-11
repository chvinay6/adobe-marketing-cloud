package io.ecx.aem.aemsp.core.slingmodels;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.google.common.collect.Lists;

import io.ecx.aem.aemsp.core.components.par.votingmodule.VotingAnswerVO;

@Model(adaptables = Resource.class)
public class VotingModel
{
    private static final String VOTING_TYPE_RADIO = "radio";
    private static final String VOTING_ANSWER = "answer";
    private static final String VOTING_SELECTED = "selected";
    private static final String VOTING_START_VALUE = "startValue";

    @SlingObject
    private Resource resource;

    @Inject
    @Optional
    @Named("headline")
    private String headline;

    @Inject
    @Optional
    @Named("submitButton")
    private String submit;

    @Inject
    @Optional
    @Named("thankyouMessage")
    private String thankyouMessage;

    @Inject
    @Optional
    @Named("type")
    private String type;

    @Inject
    @Optional
    @Named("question")
    private String question;

    @Inject
    @Optional
    @Named("answers")
    private Resource answers;

    private List<VotingAnswerVO> answerList;
    private boolean multiselection;
    private String uniqueId;
    private String path;

    @PostConstruct
    private void init()
    {
        answerList = generateAnswerList(answers);
        multiselection = VOTING_TYPE_RADIO.equals(type) ? false : true;
        uniqueId = UUID.nameUUIDFromBytes(resource.getPath().getBytes()).toString();
        path = resource.getPath();
    }

    private List<VotingAnswerVO> generateAnswerList(final Resource answers)
    {
        final List<VotingAnswerVO> result = new ArrayList<>();

        if (answers != null && answers.hasChildren())
        {
            final List<Resource> resList = Lists.newArrayList(answers.getChildren());
            for (final Resource res : resList)
            {
                final ValueMap vmap = res.getValueMap();
                result.add(new VotingAnswerVO(vmap.get(VOTING_ANSWER, StringUtils.EMPTY), vmap.get(VOTING_SELECTED, false), vmap.get(VOTING_START_VALUE, 0)));
            }
        }

        return result;
    }

    public String getHeadline()
    {
        return headline;
    }

    public String getSubmit()
    {
        return submit;
    }

    public String getThankyouMessage()
    {
        return thankyouMessage;
    }

    public String getType()
    {
        return type;
    }

    public List<VotingAnswerVO> getAnswerList()
    {
        return answerList;
    }

    public String getQuestion()
    {
        return question;
    }

    public boolean isMultiselection()
    {
        return multiselection;
    }

    public String getUniqueId()
    {
        return uniqueId;
    }

    public String getPath()
    {
        return path;
    }
}
