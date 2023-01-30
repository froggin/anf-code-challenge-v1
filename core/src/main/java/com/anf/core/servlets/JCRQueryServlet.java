/***
 * ***Begin Code - Nicholaus Chipping***
 */
package com.anf.core.servlets;

import com.drew.lang.annotations.NotNull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Objects;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component (
    service = { Servlet.class },
    property = {
            SLING_SERVLET_PATHS + "=/bin/anf",
            SLING_SERVLET_SELECTORS + "=challenge",
            SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
            SLING_SERVLET_EXTENSIONS + "=json"
    }
)
public class JCRQueryServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(JCRQueryServlet.class);

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) {
        // go do the query
        String items = getAnfItems(request);
        try {
            response.setContentType("application/json");
            response.getWriter().write(items);
        } catch (IOException e) {
            LOG.error("Error while writing response {}", e.getMessage());
        }
    }

    private String getAnfItems(SlingHttpServletRequest request) {
        JSONArray jsonArray = new JSONArray();
        String queryString = "SELECT * FROM [cq:PageContent] " +
                "WHERE ISDESCENDANTNODE('/content/anf-code-challenge/us/en') " +
                "AND [anfCodeChallenge] IS NOT NULL";

        QueryResult result = null;
        try {
            QueryManager queryManager = Objects.requireNonNull(request.getResourceResolver().adaptTo(Session.class)).getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);
            query.setLimit(10);
            result = query.execute();
        } catch (RepositoryException e) {
            LOG.error("Error while querying {}", e.getMessage());
        }

        if (result != null) {
            RowIterator rowIterator = null;
            try {
                rowIterator = result.getRows();
            } catch (RepositoryException e) {
                LOG.error("Unable to iterate {}", e.getMessage());
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.nextRow();
                try {
                    jsonArray.put(row.getPath());
                } catch (RepositoryException e) {
                    LOG.error("Error while getting information {}", e.getMessage());
                }
            }
        }

        return jsonArray.toString();
    }
}

/***
 * ***END Code*****
 */