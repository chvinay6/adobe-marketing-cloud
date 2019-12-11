package io.ecx.aem.aemsp.core.components.par.votingmodule;

public class VotingAnswerVO
{
    private final String answer;
    private final boolean selected;
    private final int startValue;

    public VotingAnswerVO(final String answer, final boolean selected, final int startValue)
    {
        this.answer = answer;
        this.selected = selected;
        this.startValue = startValue;
    }

    public String getAnswer()
    {
        return answer;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public int getStartValue()
    {
        return startValue;
    }
}
