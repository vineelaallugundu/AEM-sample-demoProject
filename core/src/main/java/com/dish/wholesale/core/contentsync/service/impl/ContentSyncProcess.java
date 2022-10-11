package com.dish.wholesale.core.contentsync.service.impl;

import com.adobe.acs.commons.fam.ActionManager;
import com.adobe.acs.commons.mcp.ProcessDefinition;
import com.adobe.acs.commons.mcp.ProcessInstance;
import com.adobe.acs.commons.mcp.form.*;
import com.adobe.acs.commons.mcp.model.GenericReport;
import com.day.cq.replication.ReplicationException;
import com.dish.wholesale.core.contentsync.EnvironmentSelector;
import com.dish.wholesale.core.contentsync.service.ContentSyncPackageInstallService;
import com.dish.wholesale.core.contentsync.service.EnvironmentConfiguration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.cm.Configuration;

import javax.jcr.RepositoryException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static com.dish.wholesale.core.contentsync.servlet.ContentSyncPackageBuildServlet.PARAM_INCLUDE_ASSETS;
import static com.dish.wholesale.core.contentsync.servlet.ContentSyncPackageBuildServlet.PARAM_INCLUDE_EFRAGS;
import static com.dish.wholesale.core.contentsync.servlet.ContentSyncPackageBuildServlet.PARAM_INCLUDE_TEMPLATES;
import static com.dish.wholesale.core.contentsync.servlet.ContentSyncPackageBuildServlet.PARAM_PATH;
import static com.dish.wholesale.core.contentsync.servlet.ContentSyncPackageBuildServlet.PATH_PACKAGE_BUILD_SERVLET;
import static com.dish.wholesale.core.contentsync.servlet.ContentSyncPackageRetrievalServlet.PARAM_UUID;
import static com.dish.wholesale.core.contentsync.servlet.ContentSyncPackageRetrievalServlet.PATH_PACKAGE_RETRIEVAL_SERVLET;

/**
 * The {@link ProcessDefinition} coordinates the work for the MCP.
 */
@Slf4j
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentSyncProcess extends ProcessDefinition {
    private static final String SAVE_INSTALL_NOTE = "At least one of Save or Install Package must be selected.";
    private static final String[] ALLOWED_PATH_PREFIXES = {
            "/content/", "/conf/"
    };

    /**
     * The list of items to include in the content package. These values will be sent to the content sync servlet. This
     * items in this list must be paths that start with either {@code /content} or {@code /conf}.
     */
    @FormField(name = "Package Items",
            description = "The items that will be included in the package."
                    + " Must be paths below /content or /conf. At least one package item must be provided.",
            hint = "e.g. /content/wholesale")
    private List<String> packageItems;

    /**
     * The environment from which content will be pulled.
     * <p>
     * Note: The options for this value are driven by a set of server-specific configurations of
     * {@link EnvironmentConfiguration} services. The easiest way to configure these is to create run mode specific
     * configurations of {@link EnvironmentConfigurationFactory} services.
     */
    @FormField(name = "Environment",
            description = "The environment from which to pull content.",
            component = EnvironmentSelector.class)
    private String environment;

    /**
     * Whether or not to include assets referenced by the pages in {@link #packageItems}. If this is true, then assets
     * on any page (including efrags) listed in {@link #packageItems} will be added to the package.
     * <p>
     * If {@link #includeEfrags} is also true, then assets referenced by those efrags will also be included.
     */
    @FormField(name = "Include Assets?",
            description = "Whether or not to include assets referenced by the package items.",
            component = CheckboxComponent.class,
            options = {
                    "checked=true"
            })
    private boolean includeAssets;

    /**
     * Whether or not to include templates of the pages (or efrags) of the {@link #packageItems}. If this is true, then
     * the entire template will be included. This means the structure, initial content, and policies configurations
     * (including the referenced policies) will be included in the package..
     */
    @FormField(name = "Include Templates",
            description = "Whether or not to include templates referenced by the package items.",
            component = CheckboxComponent.class,
            options = {
                    "checked=true"
            })
    private boolean includeTemplates;

    /**
     * Whether or not to include efrags referenced by the pages in {@link #packageItems}. If this is trie, then efrags
     * used on any page listed in {@link #packageItems} will be added to the package. This will be recursively applied,
     * so efrags used on other efrags, will also be included.
     * <p>
     * If {@link #includeAssets} is also true, then assets referenced by the efrags within the package will also be
     * included.
     */
    @FormField(name = "Include Experience Fragments?",
            description = "Whether or not to include experience fragments referenced by the package items.",
            component = CheckboxComponent.class,
            options = {
                    "checked=true"
            })
    private boolean includeEfrags;

    /**
     * Whether or not to install the package after pulling it from the remote server. If this is true, then the package
     * will be installed on the author and its associated publishers.
     * <p>
     * Either this or {@link #savePackage} must be true.
     */
    @FormField(name = "Install Package?",
            description = "Whether or not to install the package onto author & publisher(s).",
            hint = SAVE_INSTALL_NOTE,
            component = CheckboxComponent.class,
            options = {
                    "checked=true"
            })
    private boolean installPackage;

    /**
     * Whether or not to save the package after pulling it from the remote server. If this is trie. then the package
     * will be saved on the author server. If this is false, then the package will be deleted from the author server.
     * The package will <b>always</b> be deleted from the publishers.
     * <p>
     * Either this or {@link #installPackage} must be true.
     */
    @FormField(name = "Save Package?",
            description = "Whether or not to save the package. If unchecked, the package is deleted after installing.",
            hint = SAVE_INSTALL_NOTE,
            component = CheckboxComponent.class)
    private boolean savePackage;

    /**
     * The report that records the results of the MCP tool run.
     */
    private final GenericReport report;

    /**
     * The rows of the report.
     */
    private final List<EnumMap<ReportColumns, Object>> reportRows;

    /**
     * The content sync install service, used to install and replicate the packages.
     */
    private final ContentSyncPackageInstallService installService;

    /**
     * The set of configured environments. Used in conjunction with the {@link #environment} selected by the MCP user.
     * -- GETTER --
     * Gets the environment configurations. Testing only.
     *
     * @return the environment config set
     */
    @Getter(AccessLevel.PACKAGE)
    private final Set<EnvironmentConfigurationFactory> environmentConfigs;

    /**
     * The set of configured environments. Used in conjunction with the {@link #environment} selected by the MCP user.
     * -- GETTER --
     * Gets the environment configurations. Testing only.
     *
     * @return the environment config set
     */
    @Getter(AccessLevel.PACKAGE)
    private final Configuration[] configurations;

    /**
     * The selected environment configuration
     */
    private EnvironmentConfigurationFactory selectedEnvironmentConfig = new EnvironmentConfigurationFactory();

    /**
     * The UUID sent from the server, that represents the package being built.
     * -- GETTER --
     * Gets the UUID of the package. Testing only.
     *
     * @return the package UUID
     */
    @Getter(AccessLevel.PACKAGE)
    private UUID packageUuid;

    /**
     * The username for communicating with the remote server.
     */
    private final String username;

    /**
     * The password for communicating with the remote server.
     */
    private final String password;

    /**
     * The columns names for each row of the report. This is a generic set of key/value columns, since the data being
     * saved in the report will be mostly single value
     */
    enum ReportColumns {
        KEY,
        VALUE
    }

    /**
     * Creates an instance of the process. Used by the factory to create an instance of the process.
     *
     * @param environmentConfigs the set of environment configurations
     * @param installService     the install service
     * @param configurations
     */
    @SuppressWarnings({"squid:S2384", "java:S2384"})
    ContentSyncProcess(
            @NotNull Set<EnvironmentConfigurationFactory> environmentConfigs,
            @NotNull ContentSyncPackageInstallService installService,
            @NotNull String username,
            @NotNull String password,
            Configuration[] configurations) {
        this(
                new GenericReport(),
                new ArrayList<>(),
                installService,
                environmentConfigs,
                configurations,
                username,
                password

        );
    }

    @Override
    public void buildProcess(ProcessInstance instance, ResourceResolver rr) throws LoginException, RepositoryException {
        log.info("build process");
        validateInputs();
        instance.defineCriticalAction("Start Package Build on " + environment, rr, this::startPackageBuild);
        instance.defineCriticalAction("Retrieve Package From  " + environment, rr, this::retrieveBuiltPackage);
        instance.defineCriticalAction("Install Package", rr, this::installPackage);
    }

    /**
     * Validates the inputs of the process, to ensure the minimally correct data was provided by the user.
     *
     * @throws IllegalArgumentException when validation of parameters fails.
     */
    void validateInputs() {
        log.debug("Validating MCP inputs...");

        validatePackageItems();
        validateEnvironment();
        validateInstallAndSave();

        log.debug("MCP inputs valid");
    }

    /**
     * Validates the package items, ensuring there is at least one provided and that none of those provided is not a
     * valid item.
     *
     * @throws IllegalArgumentException if packageItems is null/empty or has no valid items
     */
    void validatePackageItems() {
        this.packageItems = Collections.unmodifiableList(ListUtils.emptyIfNull(packageItems));

        if (packageItems.isEmpty()) {
            log.error("No packageItems provided");
            throw new IllegalArgumentException("packageItems is zero length");
        }

        Map<Boolean, List<String>> filteredPaths = packageItems.stream()
                .collect(Collectors.partitioningBy(
                        path -> StringUtils.startsWithAny(path, ALLOWED_PATH_PREFIXES)
                                && !StringUtils.contains(path, ".."),
                        Collectors.toList()
                ));

        this.packageItems = filteredPaths.get(true);
        List<String> invalidPackageItems = filteredPaths.get(false);

        if (invalidPackageItems != null && !invalidPackageItems.isEmpty()) {
            throw new IllegalArgumentException("packageItems contains invalid items " + invalidPackageItems);
        }
    }

    /**
     * Validates that the environment and selected environment are not blank or null (respectively).
     *
     * @throws IllegalArgumentException when environment is blank or selectedEnvironmentConfig is null
     */
    void validateEnvironment() {
        if (StringUtils.isBlank(environment)) {
            log.error("No environment selected");
            throw new IllegalArgumentException("environment is blank, check your services config");
        }

        if (selectedEnvironmentConfig == null) {
            log.error("Invalid environment selected: {}", environment);
            throw new IllegalArgumentException("environmentConfig could not be found with name " + environment);
        }
    }

    /**
     * Validates that at least one of {@link #installPackage} or {@link #savePackage} are true.
     *
     * @throws IllegalArgumentException if both installPackage and savePackage are false
     */
    void validateInstallAndSave() {
        if (!installPackage && !savePackage) {
            log.error("Neither install nor save package selected.");
            throw new IllegalArgumentException("Either installPackage or savePackage must be true");
        }
    }

    /**
     * Request the author server denoted by {@link #environment} to build the package with the given params of:
     * <ul>
     *     <li>{@link #packageItems}</li>
     *     <li>{@link #includeAssets}</li>
     *     <li>{@link #includeTemplates}</li>
     *     <li>{@link #includeEfrags}</li>
     * </ul>
     * <p>
     * Receives the UUID from the environment to be used in further actions.
     *
     * @param actionManager the action manager
     */
    void startPackageBuild(ActionManager actionManager) throws IOException {
        log.info("Beginning build action...");
        log.info("Requesting environment {} build the package", environment);

        try (CloseableHttpClient httpClient = getHttpClient()) {
            List<NameValuePair> params = new ArrayList<>();

            packageItems.stream()
                    .map(path -> new BasicNameValuePair(PARAM_PATH, path))
                    .forEach(params::add);

            params.add(new BasicNameValuePair(PARAM_INCLUDE_ASSETS, Boolean.toString(includeAssets)));
            params.add(new BasicNameValuePair(PARAM_INCLUDE_TEMPLATES, Boolean.toString(includeTemplates)));
            params.add(new BasicNameValuePair(PARAM_INCLUDE_EFRAGS, Boolean.toString(includeEfrags)));

            HttpPost post = new HttpPost(selectedEnvironmentConfig.getEnvUrl() + PATH_PACKAGE_BUILD_SERVLET);
            post.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                if (200 != response.getStatusLine().getStatusCode()) {
                    throw new IOException("Remote servlet returned non-200. Actual status code: "
                            + response.getStatusLine().getStatusCode());
                }

                String uuidString = EntityUtils.toString(response.getEntity());
                this.packageUuid = UUID.fromString(uuidString);

                EntityUtils.consumeQuietly(response.getEntity());
            }
        }

        if (packageUuid == null) {
            throw new IllegalArgumentException("packageUuid is null after making the build call");
        }

        log.info("Package built with UUID {}", packageUuid);
        log.info("Completed build action.");
    }

    /**
     * Retrieves the package from the author server denoted by {@link #environment} with the UUID {@link #packageUuid}
     * and "uploads" it to this server.
     *
     * @param actionManager the action manager
     */
    void retrieveBuiltPackage(ActionManager actionManager)
            throws IOException, LoginException, PackageException, ReplicationException, RepositoryException {
        log.info("Beginning receive action...");
        log.info("Getting package with UUID {} from environment {}", packageUuid, environment);

        try (CloseableHttpClient httpClient = getHttpClient()) {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(PARAM_UUID, packageUuid.toString()));

            HttpPost post = new HttpPost(selectedEnvironmentConfig.getEnvUrl() + PATH_PACKAGE_RETRIEVAL_SERVLET);
            post.setEntity(new UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                if (200 != response.getStatusLine().getStatusCode()) {
                    throw new IOException("Remote servlet returned non-200. Actual status code: "
                            + response.getStatusLine().getStatusCode());
                }

                try (InputStream pkgStream = new BufferedInputStream(response.getEntity().getContent())) {
                    installService.uploadPackage(pkgStream);
                }

                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
        log.info("Package with UUID {} retrieved from environment {}", packageUuid, environment);
    }

    /**
     * Get a new http client
     *
     * @return a new http client
     */
    CloseableHttpClient getHttpClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        credentialsProvider.setCredentials(AuthScope.ANY, credentials);

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5_000) // 5 seconds
                .setConnectionRequestTimeout(5_000) // 5 seconds
                .setSocketTimeout(300_000).build(); // 300 seconds = 5 minutes (should be far less than that)

        HttpClientBuilder builder = HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setDefaultRequestConfig(config)
                .setUserAgent("Apache Http Client sling-tv-content-sync");

        return builder.build();
    }

    /**
     * Installs the package retrieved from the author server denoted by {@link #environment} with the UUID
     * {@link #packageUuid}.
     *
     * @param actionManager the action manager
     */
    void installPackage(ActionManager actionManager)
            throws LoginException, PackageException, ReplicationException, RepositoryException, IOException {
        log.info("Beginning install action...");
        installService.installPackage(packageUuid, installPackage, installPackage, savePackage);
        log.info("Completed install action.");
    }

    @Override
    public void storeReport(ProcessInstance instance, ResourceResolver rr)
            throws RepositoryException, PersistenceException {
        report.setRows(reportRows, ReportColumns.class);
        report.persist(rr, instance.getPath() + "/jcr:content/report");
    }

    /**
     * Adds a row to the report. This is a helper that simplifies adding to the report.
     *
     * @param key   the key column for the row.
     * @param value the value column for the row.
     */
    void addReportRow(String key, Object value) {
        EnumMap<ReportColumns, Object> row = new EnumMap<>(ReportColumns.class);
        row.put(ReportColumns.KEY, key);
        row.put(ReportColumns.VALUE, value);

        reportRows.add(row);
    }

    /**
     * Adds report rows for the environment configuration and selection.
     *
     * @param environment               the environment selected
     * @param selectedEnvironmentConfig the environment config selected
     */
    void reportEnvironmentParameters(
            @NotNull String environment,
            @Nullable EnvironmentConfigurationFactory selectedEnvironmentConfig
    ) {
        addReportRow("Environment", environment);

        if (selectedEnvironmentConfig != null) {
            addReportRow("Environment URL", selectedEnvironmentConfig.getEnvUrl());
        }
    }

    /**
     * Adds report rows for content related parameters.
     *
     * @param packageItems     the package items
     * @param includeAssets    the include assets flag
     * @param includeTemplates the include templates flag
     * @param includeEfrags    the include efrags flag
     * @param installPackage   the install package flag
     * @param savePackage      the save package flag
     */
    void reportContentParameters(
            @NotNull List<String> packageItems,
            boolean includeAssets,
            boolean includeTemplates,
            boolean includeEfrags,
            boolean installPackage,
            boolean savePackage
    ) {
        packageItems.forEach(item -> addReportRow("Path", item));

        addReportRow("Include Assets", includeAssets);
        addReportRow("Include Templates", includeTemplates);
        addReportRow("Include Experience Fragments", includeEfrags);
        addReportRow("Install Package", installPackage);
        addReportRow("Save Package", savePackage);
    }

    @Override
    public void init() throws RepositoryException {
        if (configurations != null) {
            for (Configuration configuration : configurations) {
                if (environment.equalsIgnoreCase(configuration.getProperties().get("getEnvName").toString())) {
                    selectedEnvironmentConfig.setEnvName(configuration.getProperties().get("getEnvName").toString());
                    selectedEnvironmentConfig.setEnvUrl(configuration.getProperties().get("getEnvUrl").toString());
                    break;
                }
            }
        } else {
            log.info("configurations is null");
        }

        reportEnvironmentParameters(environment, selectedEnvironmentConfig);

        packageItems = ListUtils.emptyIfNull(packageItems);

        reportContentParameters(
                packageItems,
                includeAssets,
                includeTemplates,
                includeEfrags,
                installPackage,
                savePackage
        );
    }
}
