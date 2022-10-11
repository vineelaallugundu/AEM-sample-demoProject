package com.dish.wholesale.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FeedBackDataServletTest {

    private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @InjectMocks
    private FeedbackDataStoreServlet feedbackDataStoreServlet;

    @BeforeEach
    void setUp() {
        context.request().addRequestParameter("feedbackData","feedbackData");
        context.request().addRequestParameter("url","url");
    }

    @Test
    void doPostTest() throws IOException {
        feedbackDataStoreServlet.doPost(context.request(),context.response());
        assertNotNull(feedbackDataStoreServlet);
        assertNotNull(context.response());
    }

    @Test
    void doGetTest() throws IOException {
        feedbackDataStoreServlet.doGet(context.request(),context.response());
    }

}
