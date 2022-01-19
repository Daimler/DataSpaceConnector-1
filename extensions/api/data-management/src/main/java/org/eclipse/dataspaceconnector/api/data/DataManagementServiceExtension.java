package org.eclipse.dataspaceconnector.api.data;

import jakarta.ws.rs.container.ContainerRequestContext;
import org.eclipse.dataspaceconnector.api.data.controller.DataManagementController;
import org.eclipse.dataspaceconnector.api.data.filter.HttpApiKeyAuthContainerRequestFilter;
import org.eclipse.dataspaceconnector.api.data.settings.CommonsConnectionPoolConfigFactory;
import org.eclipse.dataspaceconnector.api.data.settings.ConnectionFactoryConfigFactory;
import org.eclipse.dataspaceconnector.clients.postgresql.PostgresqlClient;
import org.eclipse.dataspaceconnector.clients.postgresql.PostgresqlClientImpl;
import org.eclipse.dataspaceconnector.clients.postgresql.asset.Repository;
import org.eclipse.dataspaceconnector.clients.postgresql.asset.RepositoryImpl;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.ConnectionFactory;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.ConnectionFactoryConfig;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.ConnectionFactoryImpl;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.pool.ConnectionPool;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.pool.commons.CommonsConnectionPool;
import org.eclipse.dataspaceconnector.clients.postgresql.connection.pool.commons.CommonsConnectionPoolConfig;
import org.eclipse.dataspaceconnector.spi.EdcSetting;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.protocol.web.WebService;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Set;
import java.util.function.Predicate;

public class DataManagementServiceExtension implements ServiceExtension {

    @EdcSetting
    public static final String EDC_API_DATA_AUTH_APIKEY_KEY = "edc.api.data.auth.apikey.key";
    public static final String EDC_API_DATA_AUTH_APIKEY_KEY_DEFAULT = "X-Api-Key";

    @EdcSetting
    public static final String EDC_API_DATA_AUTH_APIKEY_VALUE = "edc.api.data.auth.apikey.value";

    private Monitor monitor;

    @Override
    public String name() {
        return "EDC Data Loader API";
    }

    @Override
    public Set<String> requires() {
        return Set.of("edc:webservice");
    }

    @Override
    public void initialize(ServiceExtensionContext serviceExtensionContext) {
        monitor = serviceExtensionContext.getMonitor();
        WebService webService = serviceExtensionContext.getService(WebService.class);

        Repository repository = createRepository(serviceExtensionContext);
        Monitor monitor = serviceExtensionContext.getMonitor();
        webService.registerController(new DataManagementController(repository, monitor));

        HttpApiKeyAuthContainerRequestFilter httpApiKeyAuthContainerRequestFilter = new HttpApiKeyAuthContainerRequestFilter(
                resolveApiKeyHeaderName(serviceExtensionContext),
                resolveApiKeyHeaderValue(serviceExtensionContext),
                AuthenticationContainerRequestContextPredicate.INSTANCE);

        webService.registerController(httpApiKeyAuthContainerRequestFilter);
    }

    private String resolveApiKeyHeaderName(@NotNull ServiceExtensionContext context) {
        String key = context.getSetting(EDC_API_DATA_AUTH_APIKEY_KEY, null);
        if (key == null) {
            key = EDC_API_DATA_AUTH_APIKEY_KEY_DEFAULT;
            monitor.warning(String.format("Settings: No setting found for key '%s'. Using default value '%s'", EDC_API_DATA_AUTH_APIKEY_KEY, EDC_API_DATA_AUTH_APIKEY_KEY_DEFAULT));
        }
        return key;
    }

    private String resolveApiKeyHeaderValue(@NotNull ServiceExtensionContext context) {
        String value = context.getSetting(EDC_API_DATA_AUTH_APIKEY_VALUE, null);
        if (value == null) {
            value = generateRandomString();
            monitor.warning(String.format("Settings: No setting found for key '%s'. Using random value '%s'", EDC_API_DATA_AUTH_APIKEY_VALUE, value));
        }
        return value;
    }


    private enum AuthenticationContainerRequestContextPredicate implements Predicate<ContainerRequestContext> {
        INSTANCE;

        @Override
        public boolean test(ContainerRequestContext containerRequestContext) {
            String path = containerRequestContext.getUriInfo().getPath();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }

            return path.startsWith("/data");
        }
    }

    /*
     * Produces twelve characters long sequence in the ascii range of '!' (dec 33) to '~' (dec 126).
     *
     * @return sequence
     */
    private static String generateRandomString() {
        StringBuilder stringBuilder = new SecureRandom().ints('!', ((int) '~' + 1))
                .limit(12).collect(
                        StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append);
        return stringBuilder.toString();
    }

    @NotNull
    private Repository createRepository(ServiceExtensionContext context) {
        ConnectionFactoryConfigFactory connectionFactoryConfigFactory = new ConnectionFactoryConfigFactory(context);
        ConnectionFactoryConfig connectionFactoryConfig = connectionFactoryConfigFactory.create();
        ConnectionFactory connectionFactory = new ConnectionFactoryImpl(connectionFactoryConfig);
        CommonsConnectionPoolConfigFactory commonsConnectionPoolConfigFactory = new CommonsConnectionPoolConfigFactory(context);
        CommonsConnectionPoolConfig commonsConnectionPoolConfig = commonsConnectionPoolConfigFactory.create();
        ConnectionPool connectionPool = new CommonsConnectionPool(connectionFactory, commonsConnectionPoolConfig);
        PostgresqlClient postgresqlClient = new PostgresqlClientImpl(connectionPool);
        return new RepositoryImpl(postgresqlClient);
    }
}
