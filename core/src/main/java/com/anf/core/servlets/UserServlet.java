/* ***Begin Code - Nicholaus Chipping*** */
package com.anf.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component(service = { Servlet.class })
@SlingServletPaths(
        value = "/bin/saveUserDetails"
)
public class UserServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(UserServlet.class);
    private final String SAVE_PATH = "/var/anf-code-challenge";
    private final String AGE_NODE = "/etc/age";
    private final String ERROR_MESSAGE = "You are not eligible";
    private final String SUCCESS_MESSAGE = "You have done well";
    private ResourceResolver resourceResolver = null;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    protected void doGet(final SlingHttpServletRequest request,
            final SlingHttpServletResponse response) throws ServletException, IOException {
        login();
        Resource ageResource = resourceResolver.getResource(AGE_NODE);
        boolean error = false;

        int maxAge = 7;
        int minAge = 3;

        if (ageResource != null) {
            ValueMap ageValueMap = ageResource.getValueMap();
            maxAge = (ageValueMap.containsKey("maxAge")) ? Integer.parseInt(ageValueMap.get("maxAge").toString()) : 7;
            minAge = (ageValueMap.containsKey("minAge")) ? Integer.parseInt(ageValueMap.get("minAge").toString()) : 3;
        }

        int userAge = Integer.parseInt(request.getParameter("age"));
        if (userAge> maxAge || userAge < minAge) {
            error = true;
        }

        response.setContentType("application/json");
        JSONObject responseJSONObject = new JSONObject();
        try {
            if (error) {
                responseJSONObject.append("error", true);
                responseJSONObject.append("message", ERROR_MESSAGE);
            } else {
                responseJSONObject.append("success", true);
                responseJSONObject.append("message", SUCCESS_MESSAGE);
                // save the data into the JCR
                Map<String, String[]> requestMap = request.getParameterMap();
                Map<String, Object> saveData = new HashMap<>();
                String firstName = "unnamed";
                for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
                    String key = entry.getKey();
                    if (key.equals("firstName") || key.equals("lastName") || key.equals("country") || key.equals("age")) {
                        // it's a field we are ok with
                        if (key.equals("firstName")) {
                            firstName = entry.getValue()[0];
                        }
                        saveData.put(key, entry.getValue()[0]);
                    }
                }
                saveInfo(saveData, firstName, resourceResolver.resolve(SAVE_PATH));
            }
        } catch (JSONException e) {
            LOG.error("Unable to write to JSONObject {}", e.getMessage());
        }

        response.getWriter().write(responseJSONObject.toString());
    }

    private void saveInfo(Map<String, Object> saveData, String name, Resource resource) {
        if (resource != null && saveData.size() > 0) {
            Map<String, Object> baseData = new HashMap<>();
            baseData.put("jcr:primaryType", "nt:unstructured");
            Resource newResource = null;
            try {
                newResource = resourceResolver.create(resource, name, baseData);
            } catch (PersistenceException ex) {
                LOG.error("Unable to save {}", ex.getMessage());
            }

            if (newResource != null) {
                ModifiableValueMap modifiableValueMap = newResource.adaptTo(ModifiableValueMap.class);
                if (modifiableValueMap != null) {
                    for (Map.Entry<String, Object> entry : saveData.entrySet()) {
                        modifiableValueMap.put(entry.getKey(), entry.getValue());
                    }
                    try {
                        resourceResolver.commit();
                    } catch (PersistenceException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private void login() {
        final Map<String, Object> authenticationInfo = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "anf-page-service");
        try {
            this.resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationInfo);
        } catch (LoginException e) {
            LOG.error("Issue with user {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
/* ***END Code***** */