package org.eclipse.dataspaceconnector.contract;

import org.eclipse.dataspaceconnector.spi.asset.AssetIndex;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferFramework;
import org.eclipse.dataspaceconnector.spi.contract.ContractOfferService;
import org.eclipse.dataspaceconnector.spi.system.ServiceExtensionContext;

public class ContractOfferServiceFactory {

    public static ContractOfferService createContractOfferService(final ServiceExtensionContext serviceExtensionContext) {
        /*
         * Construct an AssetIndexLocator for finding several AssetIndexes provided via extensions
         */
        final AssetIndexLocator assetIndexLocator = new AssetIndexLocator(serviceExtensionContext);
        /*
         * Add the default asset index delegating calls to extensions
         */
        final AssetIndex assetIndex = new CompositeAssetIndex(
                assetIndexLocator,
                serviceExtensionContext.getMonitor());
        /*
         * Construct a ContractOfferFrameworkLocator for finding several ContractOfferFrameworks provided via extensions
         */
        final ContractOfferFrameworkLocator compositeContractOfferFramework = new ContractOfferFrameworkLocator(
                serviceExtensionContext
        );
        /*
         * There is always one default contract offer framework instance
         * delegating calls to those provided by custom extensions.
         */
        final ContractOfferFramework contractOfferFramework = new CompositeContractOfferFramework(
                compositeContractOfferFramework,
                serviceExtensionContext.getMonitor());
        /*
         * Contract offer service calculates contract offers using a variety of contract offer frameworks
         * ad the given asset index.
         */
        return new ContractOfferServiceImpl(
                contractOfferFramework,
                assetIndex
        );
    }
}
