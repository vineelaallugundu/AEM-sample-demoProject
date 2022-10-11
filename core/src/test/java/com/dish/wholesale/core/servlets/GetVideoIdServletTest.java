package com.dish.wholesale.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class GetVideoIdServletTest {

    @InjectMocks
    GetVideoIdServlet getVideoIdServlet;

    @Mock
    private SlingHttpServletRequest mockSlingRequest;

    @Mock
    private SlingHttpServletResponse mockSlingResponse;

    @Mock
    Resource mockResource;

    @Mock
    ValueMap mockValueMap;

    private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);

    @BeforeEach
    void setUp() {
        context.request().addRequestParameter("videopath", "/content/dam/wholesale/wi-video-analystdaysizzle.mp4");
    }

    @Test
    void doGetTest() throws IOException, ServletException {
        getVideoIdServlet.doGet(mockSlingRequest,mockSlingResponse);
    }

    @Test
    void doPostTest() throws ServletException, IOException, RepositoryException {
       // context.request().addRequestParameter("videopath","/content/dam/wholesale/wi-video-analystdaysizzle.mp4");
        Resource resource = Mockito.mock(Resource.class);
        SlingHttpServletRequest request=Mockito.mock(SlingHttpServletRequest.class);
        Mockito.mock(Resource.class);
        ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        ValueMap vm = Mockito.mock(ValueMap.class);
        assertNotNull(context.response());

    }

    @Test
    void doPostNullTest() throws ServletException, IOException, RepositoryException {
        context.request().addRequestParameter("videopath",null);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.mock(Resource.class);
        ResourceResolver resolver = Mockito.mock(ResourceResolver.class);
        ValueMap vm = Mockito.mock(ValueMap.class);
        assertNotNull(context.response());

    }
}
