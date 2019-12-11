package io.ecx.aem.aemsp.core.services.votinModuleCheckService;

import java.util.Dictionary;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;

import io.ecx.cq.common.core.CqExceptionHandler;

@Service(value = VotingModuleCheckService.class)
@Component(label = "aemsp -  Voting Module Check Service", description = "aemsp Voting Module Check Service", metatype = true, immediate = true)
public class VotingModuleCheckServiceImpl implements VotingModuleCheckService
{

    private static final String DEF_SCHEDULING_EXPRESSION = "0 0 1 ? * SUN";
    @Property(cardinality = 0, label = "Sheduling expression", description = "Refer to http://www.docjar.com/docs/api/org/quartz/CronTrigger.html to define more scheduling expressions.", value = { DEF_SCHEDULING_EXPRESSION })
    private static final String CONF_SCHEDULING_EXPRESSION = "votingchecker.scheduling.expression";

    @Reference
    protected Scheduler scheduler;

    @Reference
    protected SlingSettingsService settings;

    @Reference
    protected ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicator;

    private static final Logger LOG = LoggerFactory.getLogger(VotingModuleCheckServiceImpl.class);
    private static final String JOB_NAME = "aemsp Voting Module Checker Service";

    @Override
    public void activate(final ComponentContext componentContext)
    {
        final Dictionary<?, ?> properties = componentContext.getProperties();

        final VotingModuleCheckJob votingModuleCheckJob = new VotingModuleCheckJob();
        votingModuleCheckJob.setResResolverFactory(resourceResolverFactory);

        if (addJob(scheduler, JOB_NAME, votingModuleCheckJob, PropertiesUtil.toString(properties.get(CONF_SCHEDULING_EXPRESSION), DEF_SCHEDULING_EXPRESSION)))
        {
            LOG.debug("Voting Module Check Job activated");
        }
        else
        {
            LOG.debug("Could not add Voting Module Check Job");
        }
    }

    private boolean addJob(final Scheduler scheduler, final String jobname, final VotingModuleCheckJob votingModuleCheckJob, final String shedulingExpression)
    {
        boolean returnValue = true;
        try
        {
            scheduler.schedule(votingModuleCheckJob, scheduler.EXPR(shedulingExpression).name(jobname));
        }
        catch (final Exception ex)
        {
            returnValue = false;
            CqExceptionHandler.handleException(ex, "Could not schedule Asset Activation periodic job - ", LOG);
        }

        return returnValue;
    }

    @Override
    public void deactivate(final ComponentContext componentContext)
    {
        LOG.info("Voting Module Check Job deactivated");

        scheduler.unschedule(JOB_NAME);
    }

}