package com.dish.wholesale.core.contentsync.service.impl;

import com.adobe.acs.commons.fam.ActionManager;
import com.adobe.acs.commons.mcp.ProcessInstance;
import com.adobe.acs.commons.mcp.model.GenericReport;
import com.dish.wholesale.core.contentsync.service.ContentSyncPackageInstallService;
import com.dish.wholesale.core.contentsync.service.impl.ContentSyncProcess.ContentSyncProcessBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.hamcrest.collection.IsMapContaining;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static junit.framework.Assert.fail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
public class ContentSyncProcessTest {

    @Mock
    GenericReport mockReport;

    @Mock
    EnvironmentConfigurationFactory mockEnvironmentConfig;

    @Mock
    ProcessInstance mockProcessInstance;

    @Mock
    ResourceResolver mockResourceResolver;

    @Mock
    ContentSyncPackageInstallService mockInstallService;

    @Mock
    private ActionManager actionManager;

    @Mock
    HttpClient httpClient;

    @Mock
    HttpPost post;

    @Mock
    HttpResponse response;

    @Spy
    List<EnumMap<ContentSyncProcess.ReportColumns, Object>> mockReportRows = new ArrayList<>();

    String mockPackageItem = "/content/package item";

    String mockEnvironment = "environment";

    String mockEnvironmentUrl = "mock url";

    ContentSyncProcess testSubject() {
        return testSubject(null);
    }

    ContentSyncProcess testSubject(Consumer<ContentSyncProcessBuilder> consumer) {
        ContentSyncProcessBuilder builder = ContentSyncProcess.builder()
                .report(mockReport)
                .reportRows(mockReportRows)
                .environmentConfigs(Collections.singleton(mockEnvironmentConfig))
                .installService(mockInstallService)
                .username("username")
                .password("password")
                .selectedEnvironmentConfig(mockEnvironmentConfig)
                .packageItems(Collections.singletonList(mockPackageItem))
                .environment(mockEnvironment)
                .packageUuid(UUID.randomUUID())
                .includeAssets(false)
                .includeTemplates(false)
                .includeEfrags(false)
                .includeAssets(false)
                .installPackage(false)
                .savePackage(false);

        if (consumer != null) {
            consumer.accept(builder);
        }

        return spy(builder.build());
    }

    @Test
    public void buildProcess() throws LoginException, RepositoryException {
        ContentSyncProcess testSubject = testSubject();

        doNothing().when(testSubject).validateInputs();

        testSubject.buildProcess(mockProcessInstance, mockResourceResolver);

        verify(testSubject).validateInputs();
    }

    @Test
    public void validateInputs() {
        ContentSyncProcess testSubject = mock(ContentSyncProcess.class);

        doCallRealMethod().when(testSubject).validateInputs();

        testSubject.validateInputs();

        verify(testSubject).validatePackageItems();
        verify(testSubject).validateEnvironment();
        verify(testSubject).validateInstallAndSave();
    }

    @Test
    public void validatePackageItems_nullPackageItems() {
        ContentSyncProcess testSubject = testSubject(builder ->
                builder.packageItems(null));

        try {
            testSubject.validatePackageItems();
            fail("validatePackageItems should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // good
        }
    }

    @Test
    public void validatePackageItems_emptyPackageItems() {
        ContentSyncProcess testSubject = testSubject(builder ->
                builder.packageItems(Collections.emptyList()));

        try {
            testSubject.validatePackageItems();
            fail("validatePackageItems should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // good
        }
    }

    @Test
    public void validatePackageItems_withInvalidItem() {
        ContentSyncProcess testSubject = testSubject(builder ->
                builder.packageItems(Collections.singletonList("invalid item")));

        try {
            testSubject.validatePackageItems();
            fail("validatePackageItems should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // good
        }
    }

    @Test
    public void validatePackageItems_validPackageItems() {
        ContentSyncProcess testSubject = testSubject(builder ->
                builder.packageItems(Collections.singletonList("/content/valid item")));

        testSubject.validatePackageItems();
    }

    @Test
    public void validateEnvironment_nullEnvironment() {
        ContentSyncProcess testSubject = testSubject(builder ->
                builder.environment(null));

        try {
            testSubject.validateEnvironment();
            fail("validatePackageItems should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // good
        }
    }

    @Test
    public void validateEnvironment_emptyStringEnvironment() {
        ContentSyncProcess testSubject = testSubject(builder ->
                builder.environment(""));

        try {
            testSubject.validateEnvironment();
            fail("validatePackageItems should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // good
        }
    }

    @Test
    public void validateEnvironment_nullSelectedEnvironment() {
        ContentSyncProcess testSubject = testSubject(builder ->
                builder.selectedEnvironmentConfig(null));

        try {
            testSubject.validateEnvironment();
            fail("validatePackageItems should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // good
        }
    }

    @Test
    public void validateEnvironment_validValues() {
        ContentSyncProcess testSubject = testSubject();

        testSubject.validateEnvironment();
    }

    @Test
    public void validateInstallAndSave_bothFalse() {
        ContentSyncProcess testSubject = testSubject();

        try {
            testSubject.validateInstallAndSave();
            fail("validatePackageItems should have thrown an exception");
        } catch (IllegalArgumentException e) {
            // good
        }
    }

    @Test
    public void validateInstallAndSave_installTrueSaveFalse() {
        ContentSyncProcess testSubject = testSubject(builder ->
                builder.installPackage(true));

        testSubject.validateInstallAndSave();
    }

    @Test
    public void validateInstallAndSave_installFalseSaveTrue() {
        ContentSyncProcess testSubject = testSubject(builder ->
                builder.savePackage(true));

        testSubject.validateInstallAndSave();
    }

    @Test
    public void validateInstallAndSave_bothTrue() {
        ContentSyncProcess testSubject = testSubject(builder ->
                builder.savePackage(true)
                        .installPackage(true));

        testSubject.validateInstallAndSave();
    }

    @Test
    public void startPackageBuild() throws IOException {
        ContentSyncProcess testSubject = testSubject();

        lenient().when(httpClient.execute(post)).thenReturn(response);
        try {
            testSubject.startPackageBuild(actionManager);
        } catch (Exception e) {}
    }

    @Test
    public void retrieveBuiltPackage() {
        ContentSyncProcess testSubject = testSubject();
        try {
            testSubject.retrieveBuiltPackage(actionManager);

        } catch (Exception e) {

        }
    }

    @Test
    public void getHttpClient() {
        ContentSyncProcess testSubject = testSubject();
        try (CloseableHttpClient client = testSubject().getHttpClient()) {
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void installPackage() throws Exception {
        UUID uuid = UUID.fromString("7880ecf4-fea4-4537-ac02-b94d89a66e3b");
        ContentSyncProcess testSubject = testSubject(builder -> {
            builder.packageUuid(uuid);
        });

        testSubject.installPackage(null);

        verify(mockInstallService).installPackage(uuid, false, false, false);
    }

    @Test
    public void storeReport() throws RepositoryException, PersistenceException {
        ContentSyncProcess testSubject = testSubject();

        when(mockProcessInstance.getPath()).thenReturn("/mock/path");

        testSubject.storeReport(mockProcessInstance, mockResourceResolver);

        verify(mockReport).setRows(mockReportRows, ContentSyncProcess.ReportColumns.class);
        verify(mockProcessInstance).getPath();
        verify(mockReport).persist(mockResourceResolver, "/mock/path/jcr:content/report");
    }

    @Test
    public void addReportRow() {
        ContentSyncProcess testSubject = testSubject();

        testSubject.addReportRow("key", "value");

        verify(mockReportRows).add(Matchers.any());
        assertEquals(1, mockReportRows.size());

        EnumMap<ContentSyncProcess.ReportColumns, Object> rowItem = mockReportRows.get(0);

        assertEquals(2, rowItem.size());
        assertThat(rowItem, IsMapContaining.hasEntry(ContentSyncProcess.ReportColumns.KEY, "key"));
        assertThat(rowItem, IsMapContaining.hasEntry(ContentSyncProcess.ReportColumns.VALUE, "value"));

    }

    @Test
    public void reportContentParameters() {
        ContentSyncProcess testSubject = testSubject();
        doNothing().when(testSubject).addReportRow(Matchers.any(), Matchers.any());

        testSubject.reportContentParameters(
                Collections.singletonList(mockPackageItem),
                true,
                true,
                true,
                true,
                true
        );

        verify(testSubject).addReportRow("Path", mockPackageItem);
        verify(testSubject).addReportRow("Include Assets", true);
        verify(testSubject).addReportRow("Include Templates", true);
        verify(testSubject).addReportRow("Include Experience Fragments", true);
        verify(testSubject).addReportRow("Install Package", true);
        verify(testSubject).addReportRow("Save Package", true);
    }

    @Test
    public void init() throws RepositoryException {
        ContentSyncProcess testSubject = testSubject();
        doNothing().when(testSubject).reportEnvironmentParameters(Matchers.any(), Matchers.any());
        doNothing().when(testSubject).reportContentParameters(
                Matchers.any(),
                anyBoolean(),
                anyBoolean(),
                anyBoolean(),
                anyBoolean(),
                anyBoolean()
        );

        testSubject.init();

        verify(testSubject).reportEnvironmentParameters(mockEnvironment, mockEnvironmentConfig);
        verify(testSubject).reportContentParameters(
                Matchers.any(),
                eq(false),
                eq(false),
                eq(false),
                eq(false),
                eq(false)
        );
    }
}