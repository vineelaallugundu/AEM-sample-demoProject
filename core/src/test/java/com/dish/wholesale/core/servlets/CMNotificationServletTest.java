package com.dish.wholesale.core.servlets;

import com.dish.wholesale.core.helpers.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junit.framework.Assert;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
@ExtendWith(MockitoExtension.class)
public class CMNotificationServletTest {

    private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);

    @InjectMocks
    CMNotificationServlet cmNotificationServlet;

    @Mock
    private MockSlingHttpServletRequest mockSlingRequest;

    @Mock
    private MockSlingHttpServletResponse mockSlingResponse;

    @BeforeEach
    void setUp() {
        mockSlingRequest = aemContext.request();
        mockSlingResponse = aemContext.response();
        aemContext.registerService(cmNotificationServlet);
        CMNotificationServlet.Config config = mock(CMNotificationServlet.Config.class);
        Mockito.lenient().when(config.clientSecret()).thenReturn("p8e-Ox4ZjY6R5WJTFgrx5nsRB9YKKX_oxTAI");
        Mockito.lenient().when(config.organizationId()).thenReturn("093D0BC1606B79A70A495CB4@AdobeOrg");
        Mockito.lenient().when(config.technicalAccountId()).thenReturn("E5FD10ED60F6B0790A495C1C@techacct.adobe.com");
        Mockito.lenient().when(config.authServer()).thenReturn("ims-na1.adobelogin.com");
        Mockito.lenient().when(config.apiKey()).thenReturn("8fac8496de26412ca38ccdd807373556");
        Mockito.lenient().when(config.privateKeyPath()).thenReturn("/META-INF/keys/private.key");
        cmNotificationServlet.activate(config);
    }

    @Test
    void getErrorWhenCMNotificationChallengeEmpty() throws IOException {
        Assert.assertNotNull(cmNotificationServlet);
        cmNotificationServlet.doGet(mockSlingRequest, mockSlingResponse);
        assertEquals(400, mockSlingResponse.getStatus());
    }

    @Test
    void doPostTest() throws IOException {
        Map<String, String> headers = new HashMap<>();
        byte[] bytes =
                "{\"event_id\":\"276e2c5f-6cab-4ea6-a390-5d2d834bb666\",\"event\":{\"@id\":\"urn:oeid:cloudmanager:5fa33c32-19e5-43fe-9fd0-5b1293dd8e8c\",\"@type\":\"https://ns.adobe.com/experience/cloudmanager/event/started\",\"activitystreams:published\":\"2021-12-29T20:23:52.427Z\",\"activitystreams:to\":{\"@type\":\"xdmImsOrg\",\"xdmImsOrg:id\":\"093D0BC1606B79A70A495CB4@AdobeOrg\"},\"activitystreams:actor\":{\"@type\":\"xdmImsUser\",\"xdmImsUser:id\":\"62FD7F0F60A3EF250A495E92@dish.com\"},\"activitystreams:object\":{\"@id\":\"https://cloudmanager.adobe.io/api/program/33669/pipeline/2350399/execution/911696\",\"@type\":\"https://ns.adobe.com/experience/cloudmanager/pipeline-execution\"},\"xdmEventEnvelope:objectType\":\"https://ns.adobe.com/experience/cloudmanager/pipeline-execution\"}}".getBytes(StandardCharsets.UTF_8);
                mockSlingRequest.setContent(bytes);
        mockSlingRequest.addHeader("x-adobe-signature", "nTiYydskwBiMDpJISZAo69aJggNOflbK/CiUayfzCwI=");
        mockSlingRequest.addHeader(Constants.USER_AGENT, Constants.ADOBE_IO_USER_AGENT);
        mockSlingRequest.addHeader(Constants.X_ADOBE_PROVIDER, Constants.CLOUD_MANAGER);
        cmNotificationServlet.doPost(mockSlingRequest, mockSlingResponse);
        assertEquals("Request Received", mockSlingResponse.getOutputAsString());
    }

    //@Test
    void doGetChallengeTest() throws IOException {
        boolean thrown = false;
        try {
            SlingHttpServletRequest slingHttpServletRequest = mock(SlingHttpServletRequest.class);
            when(slingHttpServletRequest.getRequestURI()).thenReturn("http://localhost:4502/name !");
            cmNotificationServlet.doGet(slingHttpServletRequest, mockSlingResponse);
        } catch (RuntimeException e) {
            thrown = true;
        }
        assertFalse(thrown);
    }

    //@Test
    void doExceptionTest() throws IOException {
        byte[] bytes =
                "{\"event_id\":\"276e2c5f-6cab-4ea6-a390-5d2d834bb666\",\"event\":{\"@id\":\"urn:oeid:cloudmanager:5fa33c32-19e5-43fe-9fd0-5b1293dd8e8c\",\"@type\":\"https://ns.adobe.com/experience/cloudmanager/event/started\",\"activitystreams:published\":\"2021-12-29T20:23:52.427Z\",\"activitystreams:to\":{\"@type\":\"xdmImsOrg\",\"xdmImsOrg:id\":\"093D0BC1606B79A70A495CB4@AdobeOrg\"},\"activitystreams:actor\":{\"@type\":\"xdmImsUser\",\"xdmImsUser:id\":\"62FD7F0F60A3EF250A495E92@dish.com\"},\"activitystreams:object\":{\"@id\":\"https://cloudmanager.adobe.io/api/program/33669/pipeline/2350399/execution/91169 6\",\"@type\":\"https://ns.adobe.com/experience/cloudmanager/pipeline-execution\"},\"xdmEventEnvelope:objectType\":\"https://ns.adobe.com/experience/cloudmanager/pipeline-execution\"}}".getBytes(StandardCharsets.UTF_8);
        mockSlingRequest.setContent(bytes);
        mockSlingRequest.addHeader("x-adobe-signature", "MAJV9o658l27h8hlmYlTViR9TydnuNeQ9rZqcZcWDj4A=");
        cmNotificationServlet.doPost(mockSlingRequest, mockSlingResponse);
        assertEquals("Request Received", mockSlingResponse.getOutputAsString());
    }
}
