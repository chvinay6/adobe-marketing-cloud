package io.ecx.aem.aemsp.core.listeners;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;

/**
 * @author i.mihalina
 *
 */

@Component(immediate = true)
@Service(value = EventHandler.class)
@Properties({ @Property(name = EventConstants.EVENT_TOPIC, value = { ReplicationAction.EVENT_TOPIC }) })

public class ArticleActivationEventHandler implements EventHandler
{
    @Reference
    private JobManager jobManager;

    private static final Logger LOG = LoggerFactory.getLogger(ArticleActivationEventHandler.class);
    public static final String JOB_TOPIC = "com/sling/eventing/articleactivation/job";
    public static final String RESOURCE_PATH_PARAM = "articlePath";

    @Override
    public void handleEvent(final Event event)
    {
        LOG.info("Article Page Activation Handle Event topic: {}", event.getTopic());

        final ReplicationAction action = ReplicationAction.fromEvent(event);
        final ReplicationActionType actionType = action.getType();

        if (ReplicationActionType.ACTIVATE.equals(actionType))
        {
            final String articlePath = action.getPath();
            if (StringUtils.isNotBlank(articlePath))
            {
                final Map<String, Object> payload = new HashMap<String, Object>();
                payload.put(RESOURCE_PATH_PARAM, articlePath);

                jobManager.addJob(JOB_TOPIC, payload);
            }
        }
    }
}
