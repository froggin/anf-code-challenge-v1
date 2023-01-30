/* ***Begin Code - Nicholaus Chipping*** */
package com.anf.core.models;

import com.anf.core.models.pojo.FeedItem;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class)
public class NewsFeedModel {

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private int numberOfItems;

    @SlingObject
    private ResourceResolver resourceResolver;

    private List<FeedItem> feedItems = new ArrayList<>();
    private static final String FEED_PATH = "/var/commerce/products/anf-code-challenge/newsData";

    public NewsFeedModel(FeedItem feedItem) {
        if (feedItem != null) {
            feedItems.add(feedItem);
        }
    }
    public List<FeedItem> getItems() {
        if (feedItems.size() < 1) {
            Resource baseFeedResource = resourceResolver.resolve(FEED_PATH);
            if (!baseFeedResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                for (Resource currentFeedResource : baseFeedResource.getChildren()) {
                    if (feedItems.size() + 1 > numberOfItems) {
                        break;
                    }
                    feedItems.add(new FeedItem(currentFeedResource));
                }
            }
        }

        return feedItems;
    }
}
/* ***END Code***** */