/* ***Begin Code - Nicholaus Chipping*** */
package com.anf.core.models.pojo;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)
public class FeedItem {
    private String author;
    private String content;
    private String description;
    private String title;
    private String url;
    private String urlImage;

    private static final Logger LOG = LoggerFactory.getLogger(FeedItem.class);
    public FeedItem(String author, String content, String description, String title, String url, String urlImage) {
        this.author = author;
        this.content = content;
        this.description = description;
        this.title = title;
        this.url = url;
        this.urlImage = urlImage;
    }

    public FeedItem(Resource resource) {
        if (!resource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
            //get the feed item & populate the object
            ValueMap feedItemValueMap = resource.getValueMap();
            this.author = feedItemValueMap.get("author").toString();
            this.content = feedItemValueMap.get("content").toString();
            this.description = feedItemValueMap.get("description").toString();
            this.title = feedItemValueMap.get("title").toString();
            this.url = feedItemValueMap.get("url").toString();
            this.urlImage = feedItemValueMap.get("urlImage").toString();
        } else {
            LOG.info("Invalid resource {}", resource.getPath());
        }
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlImage() {
        return urlImage;
    }
}
/* ***END Code***** */