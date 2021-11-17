package org.eclipse.dataspaceconnector.memory.dataflow;

import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndexLoader;
import org.eclipse.dataspaceconnector.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.protocol.web.WebService;
import org.eclipse.dataspaceconnector.spi.schema.SchemaRegistry;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowManager;

import java.util.Set;

public class InMemoryDataFlowServiceExtension implements ServiceExtension {
    private Monitor monitor;

    @Override
    public Set<String> requires() {
        return Set.of(WebService.FEATURE, SchemaRegistry.FEATURE, DataFlowManager.FEATURE, RemoteMessageDispatcherRegistry.FEATURE);
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        monitor = context.getMonitor();

        var store = new InMemoryDataStore();
        var monitor = context.getMonitor();
        var dataFlowManager = context.getService(DataFlowManager.class);
        var schemaRegistry = context.getService(SchemaRegistry.class);
        schemaRegistry.register(new InMemoryDataFlowSchema());
        var webService = context.getService(WebService.class);
        var assetIndexLoader = context.getService(AssetIndexLoader.class);
        webService.registerController(new InMemoryDataController(monitor, store, assetIndexLoader));
        var okHttpClient = context.getService(OkHttpClient.class);
        var remoteMessageDispatcherRegistry = context.getService(RemoteMessageDispatcherRegistry.class);
        remoteMessageDispatcherRegistry.register(new SampleMessageDispatcher(monitor, okHttpClient));
        dataFlowManager.register(new InMemoryFlowController(monitor, store, remoteMessageDispatcherRegistry));

        monitor.info("Initialized IDS Sample Data Transfer extension");
    }

    @Override
    public void start() {
        monitor.info("Started IDS Sample Data Transfer extension");
    }

    @Override
    public void shutdown() {
        monitor.info("Shutdown IDS Sample Data Transfer extension");
    }
}
