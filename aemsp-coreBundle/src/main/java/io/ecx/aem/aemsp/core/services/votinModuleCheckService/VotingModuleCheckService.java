package io.ecx.aem.aemsp.core.services.votinModuleCheckService;

import org.osgi.service.component.ComponentContext;

public interface VotingModuleCheckService
{
    void activate(final ComponentContext componentContext);

    void deactivate(final ComponentContext componentContext);
}
