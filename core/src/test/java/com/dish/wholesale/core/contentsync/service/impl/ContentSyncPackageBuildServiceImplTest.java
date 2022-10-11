package com.dish.wholesale.core.contentsync.service.impl;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.jackrabbit.vault.fs.api.WorkspaceFilter;
import org.apache.jackrabbit.vault.packaging.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ContentSyncPackageBuildServiceImplTest {
    private static final String mockUuidString = "e2e99730-7d0e-4fbe-9676-22fe1f3412c8";

    UUID mockUuid = UUID.fromString(mockUuidString);
    Set<String> mockPaths = new HashSet<>(Arrays.asList(
            "1", "2", "3"
    ));

    @Mock
    JcrPackageManager mockPkgManager;
    @Mock
    JcrPackage mockPkg;
    @Mock
    JcrPackageDefinition mockPkgDefinition;
    @Mock
    WorkspaceFilter mockWsFilter;
    @Mock
    PackageId mockPkgId;
    @Mock
    Session mockSession;
    @Mock
    ResourceResolver mockRr;
    @Mock
    ResourceResolverFactory mockRrf;
    @Mock
    Packaging mockPackaging;

    @Test
    public void buildPackage() throws RepositoryException, PackageException, IOException, LoginException {
        when(mockRrf.getServiceResourceResolver(any())).thenReturn(mockRr);
        when(mockRr.adaptTo(Session.class)).thenReturn(mockSession);
        when(mockPackaging.getPackageManager(mockSession)).thenReturn(mockPkgManager);

        ContentSyncPackageBuildServiceImpl testSubject = spy(new ContentSyncPackageBuildServiceImpl(
                mockPackaging, mockRrf
        ));
        doNothing().when(testSubject).buildPackageInternal(any(), any(), any());

        testSubject.buildPackage(mockPaths, mockUuid);

        verify(mockRrf).getServiceResourceResolver(any());
        verify(mockRr).adaptTo(Session.class);
        verify(mockPackaging).getPackageManager(mockSession);
        verify(testSubject).buildPackageInternal(mockPaths, mockUuid, mockPkgManager);
        verify(mockRr).commit();
        verify(mockRr).close();
    }

    @Test
    public void buildPackageInternal() throws RepositoryException, IOException, PackageException {
        ContentSyncPackageBuildServiceImpl testSubject = spy(new ContentSyncPackageBuildServiceImpl());
        doReturn(false).when(testSubject).packageExists(any(), any());
        doNothing().when(testSubject).createPackage(any(), any(), any());
        doNothing().when(testSubject).assemblePackage(any(), any());

        testSubject.buildPackageInternal(mockPaths, mockUuid, mockPkgManager);

        verify(testSubject).packageExists(mockUuid, mockPkgManager);
        verify(testSubject).createPackage(mockPaths, mockUuid, mockPkgManager);
        verify(testSubject).assemblePackage(mockUuid, mockPkgManager);
    }

    @Test
    public void buildPackageInternal_packageExistsTrue() throws RepositoryException, IOException, PackageException {
        ContentSyncPackageBuildServiceImpl testSubject = spy(new ContentSyncPackageBuildServiceImpl());
        doReturn(true).when(testSubject).packageExists(any(), any());

        try {
            testSubject.buildPackageInternal(mockPaths, mockUuid, mockPkgManager);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }

        verify(testSubject).packageExists(mockUuid, mockPkgManager);
    }

    @Test
    public void packageExists() throws RepositoryException {
        when(mockPkgManager.open(any(PackageId.class))).thenReturn(mockPkg);
        ContentSyncPackageBuildServiceImpl testSubject = spy(new ContentSyncPackageBuildServiceImpl());
        doReturn(mockPkgId).when(testSubject).getId(any());

        boolean actual = testSubject.packageExists(mockUuid, mockPkgManager);

        verify(testSubject).getId(any());
        verify(mockPkgManager).open(any(PackageId.class));
        verify(mockPkg).close();
        assertTrue(actual);
    }

    @Test
    public void packageExists_nullReturn() throws RepositoryException {
        ContentSyncPackageBuildServiceImpl testSubject = spy(new ContentSyncPackageBuildServiceImpl());
        doReturn(mockPkgId).when(testSubject).getId(any());

        boolean actual = testSubject.packageExists(mockUuid, mockPkgManager);

        verify(testSubject).getId(any());
        verify(mockPkgManager).open(any(PackageId.class));
        assertFalse(actual);
    }

    // TODO test when pkgDefinition is null
    @Test
    public void createPackage() throws IOException, RepositoryException {
        when(mockPkgManager.create(any(String.class), any())).thenReturn(mockPkg);
        when(mockPkg.getDefinition()).thenReturn(mockPkgDefinition);

        ContentSyncPackageBuildServiceImpl testSubject = spy(new ContentSyncPackageBuildServiceImpl());
        doReturn(mockWsFilter).when(testSubject).createWorkspaceFilter(any());

        testSubject.createPackage(mockPaths, mockUuid, mockPkgManager);

        verify(mockPkgManager).create(any(String.class), any());
        verify(testSubject).createWorkspaceFilter(mockPaths);
        verify(mockPkgDefinition).setFilter(mockWsFilter, true);
    }

    @Test
    public void createPackage_throws() throws IOException, RepositoryException {
        when(mockPkgManager.create(any(String.class), any())).thenReturn(mockPkg);

        ContentSyncPackageBuildServiceImpl testSubject = spy(new ContentSyncPackageBuildServiceImpl());
        //doReturn(mockWsFilter).when(testSubject).createWorkspaceFilter(any());

        try {
            testSubject.createPackage(mockPaths, mockUuid, mockPkgManager);
            fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void assemblePackage() throws RepositoryException, PackageException, IOException {
        when(mockPkgManager.open(any(PackageId.class))).thenReturn(mockPkg);

        ContentSyncPackageBuildServiceImpl testSubject = new ContentSyncPackageBuildServiceImpl();
        testSubject.assemblePackage(mockUuid, mockPkgManager);

        verify(mockPkgManager).open(any(PackageId.class));
        verify(mockPkgManager).assemble(any(), any());
        verify(mockPkg).close();
    }

    @Test
    public void assemblePackage_throws() throws RepositoryException, PackageException, IOException {
        ContentSyncPackageBuildServiceImpl testSubject = new ContentSyncPackageBuildServiceImpl();

        try {
            testSubject.assemblePackage(mockUuid, mockPkgManager);
            fail();
        } catch (NullPointerException e) {
            // expected
        }
    }

    @Test
    public void getId() {
        PackageId actual = new ContentSyncPackageBuildServiceImpl()
                .getId(mockUuid);

        assertEquals(
                new PackageId("content-sync-tool", mockUuidString, (String) null),
                actual);
    }

    @Test
    public void createWorkspaceFilter() {
        WorkspaceFilter actual = new ContentSyncPackageBuildServiceImpl()
                .createWorkspaceFilter(mockPaths);

        for (String mockPath : mockPaths) {
            assertTrue(actual.contains(mockPath));
        }
    }
}
