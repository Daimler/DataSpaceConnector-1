package org.eclipse.dataspaceconnector.demo.contracts;

import org.eclipse.dataspaceconnector.spi.contract.ContractOfferFramework;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtension;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

public class DemoContractExtension implements ServiceExtension {
    private Monitor monitor;

    @Override
    public void initialize(final ServiceExtensionContext context) {
        monitor = context.getMonitor();

        final PublicContractOfferFramework contractOfferFramework = new PublicContractOfferFramework();

        context.registerService(ContractOfferFramework.class, contractOfferFramework);

        monitor.info("Initialized Demo Contract Framework extension");
    }

    @Override
    public void start() {
        monitor.info("Started Demo Contract Framework extension");
    }

    @Override
    public void shutdown() {
        monitor.info("Shutdown Demo Contract Framework extension");
    }
}
