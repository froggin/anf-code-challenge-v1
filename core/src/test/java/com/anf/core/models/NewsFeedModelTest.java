/* ***Begin Code - Nicholaus Chipping*** */
package com.anf.core.models;

import com.anf.core.models.pojo.FeedItem;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple JUnit test verifying the HelloWorldModel
 */
@ExtendWith(AemContextExtension.class)
class NewsFeedModelTest {

    private NewsFeedModel newsFeedModel;

    private Page page;
    private Resource resource;

    @BeforeEach
    public void setup(AemContext context) throws Exception {

        // prepare a page with a test resource
        page = context.create().page("/content/mypage");
        resource = context.create().resource(page, "feedItem",
    "jcr:primaryType", "nt:unstructured",
               "author","Bob Villa",
               "content","content here",
               "description","description here",
               "title","title",
               "url","https://google.com",
               "urlImage","https://www.nhm.ac.uk/content/dam/nhmwww/discover/frog-eyes-evolution/frog-eyes-chubby-frog-flower-full-width.jpg");

        FeedItem feedItem = resource.adaptTo(FeedItem.class);
        newsFeedModel = new NewsFeedModel(feedItem);
    }

    @Test
    void testGetItems() throws Exception {
        // some very basic junit tests
        List<FeedItem> feedItemList = newsFeedModel.getItems();
        assertNotNull(feedItemList);
        FeedItem feedItem = feedItemList.get(0);
        assertEquals(feedItem.getContent(), "content here");
        assertEquals(feedItem.getAuthor(), "Bob Villa");
        assertEquals(feedItem.getDescription(), "description here");
        assertEquals(feedItem.getTitle(), "title");
        assertEquals(feedItem.getUrl(), "https://google.com");
        assertEquals(feedItem.getUrlImage(), "https://www.nhm.ac.uk/content/dam/nhmwww/discover/frog-eyes-evolution/frog-eyes-chubby-frog-flower-full-width.jpg");
    }

}
/* ***END Code***** */