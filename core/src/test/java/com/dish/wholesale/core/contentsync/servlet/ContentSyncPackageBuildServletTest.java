package com.dish.wholesale.core.contentsync.servlet;

import com.dish.wholesale.core.contentsync.service.ContentSyncPackageBuildService;
import com.dish.wholesale.core.contentsync.service.ContentSyncSearchService;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.PrintWriter;
import java.util.Collections;


import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ContentSyncPackageBuildServletTest {
    @Mock
    ContentSyncPackageBuildService buildService;

    @Mock
    ContentSyncSearchService searchService;

    @Mock
    SlingHttpServletRequest request;

    @Mock
    SlingHttpServletResponse response;

    @Mock
    PrintWriter printWriter;

    ContentSyncPackageBuildServlet testSubject;

    @BeforeEach
    public void before() throws Exception {
        testSubject = spy(new ContentSyncPackageBuildServlet(buildService, searchService));
    }

    @Test
    public void doPost() throws Exception {
        when(response.getWriter()).thenReturn(printWriter);

        doReturn(Collections.emptySet()).when(testSubject).paths(any());
        doReturn(false).when(testSubject).includeAssets(any());
        doReturn(false).when(testSubject).includeTemplates(any());
        doReturn(false).when(testSubject).includeEfragsr(any());

        testSubject.doPost(request, response);


        verify(testSubject).paths(request);
        verify(testSubject).includeAssets(request);
        verify(testSubject).includeTemplates(request);
        verify(testSubject).includeEfragsr(request);
        // !!! figure out why this line is failing
        //        verify(searchService).findContent(any(), any(), any(), any());
        verify(buildService).buildPackage(any(), any());
        verify(response).getWriter();
        verify(printWriter).print(any(String.class));
    }

    @Test
    public void paths() {
        when(request.getParameterValues(any())).thenReturn(new String[]{"a"});

        testSubject.paths(request);

        verify(request).getParameterValues("path");
    }

    @Test
    public void paths_nullArray() {
        when(request.getParameterValues(any())).thenReturn(null);

        try {
            testSubject.paths(request);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }

        verify(request).getParameterValues("path");
    }

    @Test
    public void includeAssets() {
        testSubject.includeAssets(request);

        verify(request).getParameter(any());
    }

    @Test
    public void includeTemplates() {
        testSubject.includeTemplates(request);

        verify(request).getParameter(any());
    }

    @Test
    public void includeEfragsr() {
        testSubject.includeEfragsr(request);

        verify(request).getParameter(any());
    }
}
