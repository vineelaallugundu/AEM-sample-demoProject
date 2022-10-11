package com.dish.wholesale.core.contentsync.service.impl;

import com.day.cq.wcm.api.reference.Reference;
import com.day.cq.wcm.api.reference.ReferenceProvider;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ContentSyncSearchServiceImplTest {

    @Mock
    ResourceResolverFactory resourceResolverFactory;

    @Mock
    ReferenceProvider efragReferenceProvider;

    @Mock
    ReferenceProvider assetReferenceProvider;

    @Mock
    ReferenceProvider templateReferenceProvider;

    @Mock
    ReferenceProvider contentPolicyReferenceProvider;

    @Mock
    ResourceResolver resourceResolver;

    ContentSyncSearchServiceImpl testSubject;

    @BeforeEach
    public void before() {
        testSubject = spy(
                new ContentSyncSearchServiceImpl(
                        resourceResolverFactory,
                        efragReferenceProvider,
                        assetReferenceProvider,
                        templateReferenceProvider,
                        contentPolicyReferenceProvider));
    }

    @Test
    public void findContent() throws Exception {
        Set<String> paths = Collections.singleton("a");

        when(resourceResolverFactory.getServiceResourceResolver(any())).thenReturn(resourceResolver);

        doReturn("a").when(testSubject).addJcrContentToPage(any());
        doReturn(paths).when(testSubject).filterPaths(any());
        doReturn(null).when(testSubject).findContentInternal(any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean());

        testSubject.findContent(paths, false, false, false);

        verify(testSubject).addJcrContentToPage(any());
        verify(resourceResolverFactory).getServiceResourceResolver(any());
        verify(resourceResolver).close();
        verify(testSubject).findContentInternal(eq(resourceResolver), any(), any(), eq(false), eq(false), eq(false));
        verify(testSubject).filterPaths(any());
    }

    @Test
    public void addJcrContentToPage_notNeeded() {
        doReturn(false).when(testSubject).isPage(any());

        String actual = testSubject.addJcrContentToPage("/a");

        assertEquals("/a", actual);
    }

    @Test
    public void addJcrContentToPage_needed() {
        doReturn(true).when(testSubject).isPage(any());

        String actual = testSubject.addJcrContentToPage("/a");

        assertEquals("/a/jcr:content", actual);
    }

    @Test
    public void addJcrContentIfNotThere_notThere() {
        String actual = testSubject.addJcrContentIfNotThere("/not-there");

        assertEquals("/not-there/jcr:content", actual);
    }

    @Test
    public void addJcrContentIfNotThere_there() {
        String actual = testSubject.addJcrContentIfNotThere("/there/jcr:content");

        assertEquals("/there/jcr:content", actual);
    }

    @Test
    public void isPage_notContent() {
        boolean actual = testSubject.isPage("/is-not");

        assertFalse(actual);
    }

    @Test
    public void isPage_isEfrag() {
        boolean actual = testSubject.isPage("/content/experience-fragments/is-not");

        assertFalse(actual);
    }

    @Test
    public void isPage_isAsset() {
        boolean actual = testSubject.isPage("/content/dam/is-not");

        assertFalse(actual);
    }

    @Test
    public void isPage_is() {
        boolean actual = testSubject.isPage("/content/is");

        assertTrue(actual);
    }

    @Test
    public void filterPaths() {
        Collection<String> paths = Arrays.asList("/a", "/b", "/c", "/d");

        doReturn(true).when(testSubject).isValidPath(any());

        testSubject.filterPaths(paths);

        verify(testSubject, times(paths.size())).isValidPath(any());
    }

    @Test
    public void isValidPath() {
        doAnswer(invocation -> invocation.getArguments()[0].equals("efrag"))
                .when(testSubject).isEfrag(any());

        doAnswer(invocation -> invocation.getArguments()[0].equals("asset"))
                .when(testSubject).isAsset(any());

        doAnswer(invocation -> invocation.getArguments()[0].equals("template"))
                .when(testSubject).isTemplate(any());

        doAnswer(invocation -> invocation.getArguments()[0].equals("policy"))
                .when(testSubject).isPolicy(any());

        doAnswer(invocation -> invocation.getArguments()[0].equals("page"))
                .when(testSubject).isPage(any());

        // Exercising each of the parts of the or-chain
        assertTrue(testSubject.isValidPath("efrag"));
        assertTrue(testSubject.isValidPath("asset"));
        assertTrue(testSubject.isValidPath("template"));
        assertTrue(testSubject.isValidPath("policy"));
        assertTrue(testSubject.isValidPath("page"));
        assertFalse(testSubject.isValidPath("anything else"));
    }

    @Test
    public void findContentInternal_emptySearchPaths() {
        Set<String> searchPaths = Collections.emptySet();
        Set<String> combined = spy(new HashSet<>());

        Set<String> actual = testSubject.findContentInternal(
                resourceResolver,
                searchPaths,
                combined,
                false,
                false,
                false);

        verify(testSubject, only()).findContentInternal(any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
        verify(combined, never()).addAll(any());

        assertEquals(0, actual.size());
    }

    @Test
    public void findContentInternal_noRefs() {
        Set<String> searchPaths = Collections.singleton("/a");
        Set<String> combined = spy(new HashSet<>());
        testSubject.findContentInternal(
                resourceResolver,
                searchPaths,
                combined,
                true,
                true,
                true);

        verify(testSubject, times(2)).findContentInternal(any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean());
        verify(testSubject).findEfrags(any(), any());
        verify(testSubject).findTemplates(any(), any());
        verify(testSubject).findPolicies(any(), any());
        verify(testSubject).findAssets(any(), any());
    }

    @Test
    public void findReferences() {
        Set<String> paths = Collections.singleton("/a");
        Resource resource = mock(Resource.class);
        Resource childResource = mock(Resource.class);
        Reference reference = mock(Reference.class);

        when(resourceResolver.getResource(any())).thenReturn(resource);
        when(resource.getPath()).thenReturn("/a/jcr:content");
        when(efragReferenceProvider.findReferences(any())).thenReturn(Collections.singletonList(reference));
        when(reference.getResource()).thenReturn(resource);

        doReturn(childResource).when(testSubject).getJcrContent(any());

        Stream<String> actual = testSubject.findReferences(paths, resourceResolver, efragReferenceProvider);
        List<String> actualList = actual.collect(Collectors.toList());

        verify(resourceResolver).getResource("/a");
        verify(testSubject).getJcrContent(resource);
        verify(efragReferenceProvider).findReferences(childResource);
        verify(reference).getResource();
        verify(resource).getPath();
        assertEquals(1, actualList.size());
    }

    @Test
    public void getJcrContent_hasJcrContent() {
        Resource resource = mock(Resource.class);


        verify(resource, never()).getChild(any());
    }

    @Test
    public void getJcrContent_startsWithConf() {
        Resource resource = mock(Resource.class);

        when(resource.getName()).thenReturn("not-jcr-content");
        when(resource.getPath()).thenReturn("/conf");

        testSubject.getJcrContent(resource);

        verify(resource, never()).getChild(any());
    }

    @Test
    public void getJcrContent_noJcrContent() {
        Resource resource = mock(Resource.class);

        when(resource.getName()).thenReturn("not-jcr-content");
        when(resource.getPath()).thenReturn("/not-conf");

        testSubject.getJcrContent(resource);

        verify(resource).getChild("jcr:content");
    }

    @Test
    public void findEfrags() {
        Set<String> paths = Collections.singleton("a");
        doReturn(Stream.of("b")).when(testSubject).findReferences(any(), any(), any());
        doReturn(true).when(testSubject).isEfrag(any());

        testSubject.findEfrags(paths, resourceResolver);

        verify(testSubject).findReferences(paths, resourceResolver, efragReferenceProvider);
        verify(testSubject).isEfrag("b");
    }

    @Test
    public void isEfrag_isNot() {
        boolean actual = testSubject.isEfrag("/is-not");

        assertFalse(actual);
    }

    @Test
    public void isEfrag_is() {
        boolean actual = testSubject.isEfrag("/content/experience-fragments/is");

        assertTrue(actual);
    }

    @Test
    public void findAssetReferences() {
        Set<String> paths = Collections.singleton("a");
        doReturn(Stream.of("b")).when(testSubject).findReferences(any(), any(), any());
        doReturn(true).when(testSubject).isAsset(any());

        testSubject.findAssets(paths, resourceResolver);

        verify(testSubject).findReferences(paths, resourceResolver, assetReferenceProvider);
        verify(testSubject).isAsset("b");
    }

    @Test
    public void isAsset_isNot() {
        boolean actual = testSubject.isAsset("/is-not");

        assertFalse(actual);
    }

    @Test
    public void isAsset_is() {
        boolean actual = testSubject.isAsset("/content/dam/is");

        assertTrue(actual);
    }

    @Test
    public void findTemplates() {
        Set<String> paths = Collections.singleton("a");
        doReturn(Stream.of("b")).when(testSubject).findReferences(any(), any(), any());
        doReturn(true).when(testSubject).isTemplate(any());

        testSubject.findTemplates(paths, resourceResolver);

        verify(testSubject).findReferences(paths, resourceResolver, templateReferenceProvider);
        verify(testSubject).isTemplate("b");
    }

    @Test
    public void isTemplate_isNotConf() {
        boolean actual = testSubject.isTemplate("/is-not");

        assertFalse(actual);
    }

    @Test
    public void isTemplate_isConfButNotTemplate() {
        boolean actual = testSubject.isTemplate("/conf/is-not");

        assertFalse(actual);
    }

    @Test
    public void isTemplate_is() {
        boolean actual = testSubject.isTemplate("/conf/settings/wcm/templates/is");

        assertTrue(actual);
    }

    @Test
    public void findPolicies() {
        Set<String> paths = Collections.singleton("a");
        doReturn(Stream.of("b")).when(testSubject).findReferences(any(), any(), any());
        doReturn(true).when(testSubject).isPolicy(any());

        testSubject.findPolicies(paths, resourceResolver);

        verify(testSubject).findReferences(paths, resourceResolver, contentPolicyReferenceProvider);
        verify(testSubject).isPolicy("b");
    }

    @Test
    public void isPolicy_isNotConf() {
        boolean actual = testSubject.isPolicy("/is-not");

        assertFalse(actual);
    }

    @Test
    public void isPolicy_isConfButNotPolicy() {
        boolean actual = testSubject.isPolicy("/conf/is-not");

        assertFalse(actual);
    }

    @Test
    public void isPolicy_is() {
        boolean actual = testSubject.isPolicy("/conf/settings/wcm/policies/is");

        assertTrue(actual);
    }
}
