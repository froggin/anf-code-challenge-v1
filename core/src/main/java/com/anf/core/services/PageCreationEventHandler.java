/* ***Begin Code - Nicholaus Chipping*** */

package com.anf.core.services;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.apache.sling.api.SlingConstants.TOPIC_RESOURCE_ADDED;

@Component(
        immediate = true,
        service = EventHandler.class,
        property = {
            Constants.SERVICE_DESCRIPTION + "= On resource create, this handler will fire",
            EventConstants.EVENT_TOPIC + "=" + TOPIC_RESOURCE_ADDED
        }
)
public class PageCreationEventHandler implements EventHandler {
    @Reference
    private ResourceResolverFactory resourceResolverFactory;
    private static final Logger LOG = LoggerFactory.getLogger(PageCreationEventHandler.class);

    @Override
    public void handleEvent(Event event) {
        final Map<String, Object> authenticationInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "anf-page-service");
        ResourceResolver resourceResolver = null;
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationInfo);
        } catch (LoginException e) {
            LOG.error("Issue with user {}", e.getMessage());
            throw new RuntimeException(e);
        }

        // make sure resourceResolver isn't null, and move foward
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (event.containsProperty("path") && pageManager != null) {
            String path = event.getProperty("path").toString();
            Page eventPage = pageManager.getContainingPage(path);
            if (eventPage != null) {
                // get the jcr:content & set the property
                Resource jcrContentResource = Objects.requireNonNull(eventPage.adaptTo(Resource.class)).getChild("jcr:content");
                if (jcrContentResource != null) {
                    ModifiableValueMap modifiableValueMap = jcrContentResource.adaptTo(ModifiableValueMap.class);
                    if (modifiableValueMap != null) {
                        modifiableValueMap.put("pageCreated", true);
                        try {
                            resourceResolver.commit();
                        } catch (PersistenceException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
}
/* ***END Code***** */