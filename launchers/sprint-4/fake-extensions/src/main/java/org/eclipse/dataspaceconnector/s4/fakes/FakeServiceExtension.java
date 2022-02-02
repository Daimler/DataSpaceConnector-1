package org.eclipse.dataspaceconnector.s4.fakes;

import okhttp3.OkHttpClient;
import org.eclipse.dataspaceconnector.dataloading.AssetLoader;
import org.eclipse.dataspaceconnector.dataloading.ContractDefinitionLoader;
import org.eclipse.dataspaceconnector.spi.WebService;
import org.eclipse.dataspaceconnector.spi.asset.DataAddressResolver;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.Requires;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;
import org.eclipse.dataspaceconnector.spi.transfer.flow.DataFlowManager;

@Requires({ WebService.class, OkHttpClient.class, DataFlowManager.class, AssetLoader.class, ContractDefinitionLoader.class })
public class FakeServiceExtension implements ServiceExtension {

    @Override
    public void initialize(ServiceExtensionContext context) {
        Monitor monitor = context.getMonitor();

        WebService webService = context.getService(WebService.class);
        webService.registerController(new DataController(monitor));

        OkHttpClient okHttpClient = context.getService(OkHttpClient.class);
        DataAddressResolver dataAddressResolver = context.getService(DataAddressResolver.class);
        FakeDataFlowController flowController = new FakeDataFlowController(okHttpClient, dataAddressResolver);

        DataFlowManager dataFlowManager = context.getService(DataFlowManager.class);
        dataFlowManager.register(flowController);

        AssetLoader assetLoader = context.getService(AssetLoader.class);
        ContractDefinitionLoader contractDefinitionLoader = context.getService(ContractDefinitionLoader.class);
        webService.registerController(new MgmtController(assetLoader, contractDefinitionLoader));
    }
}
