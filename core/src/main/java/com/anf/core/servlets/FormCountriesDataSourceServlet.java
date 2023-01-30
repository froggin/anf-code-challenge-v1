/***
 * ***Begin Code - Nicholaus Chipping***
 */
package com.anf.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.dam.api.Asset;
import com.drew.lang.annotations.NotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_EXTENSIONS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;

@Component (
    service = { Servlet.class },
    property = {
            SLING_SERVLET_RESOURCE_TYPES + "=anf-code-challenge/components/form/country/datasource/countries",
            SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
            SLING_SERVLET_EXTENSIONS + "=html"
    }
)
public class FormCountriesDataSourceServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(FormCountriesDataSourceServlet.class);

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {
        List<Resource> countriesList = getCountriesChildren(request);
        DataSource countriesDataSource = new SimpleDataSource(countriesList.iterator());
        request.setAttribute(DataSource.class.getName(), countriesDataSource);
    }

    private List<Resource> getCountriesChildren(SlingHttpServletRequest request) {
        List<Resource> itemList = new ArrayList<>();
        ResourceResolver resourceResolver = request.getResourceResolver();
        String countriesResourceDataPath = "/content/dam/anf-code-challenge/exercise-1/countries.json";

        if (StringUtils.isNotBlank(countriesResourceDataPath)) {
            Resource countriesResource = resourceResolver.getResource(countriesResourceDataPath);

            if (countriesResource != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Asset asset = countriesResource.adaptTo(Asset.class);
                InputStream countriesStream = asset.getOriginal().getStream();

                try {
                    Map<String, Object> jsonMap = objectMapper.readValue(countriesStream, Map.class);
                    for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                        ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
                        valueMap.put("text", entry.getKey());
                        valueMap.put("value", entry.getValue());
                        itemList.add(new ValueMapResource(resourceResolver, new ResourceMetadata(),
                                "nt:unstructured", valueMap));
                        LOG.debug("Added countries item {}", valueMap);
                    }
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                }
            }
        }
        return itemList;
    }
}

/***
 * ***END Code*****
 */