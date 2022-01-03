package org.eclipse.dataspaceconnector.dataloader.cli.reader.json;

import org.eclipse.dataspaceconnector.dataloader.cli.logger.Logger;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.ConstraintHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.ContractDescriptionHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.SelectorHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.constraints.EventHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.constraints.GeoLocationHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.contractDescription.selectors.PropertySelectorHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.dataDescription.AddressHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.dataDescription.DataDescriptionHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.dataDescription.addresses.LocalAddressHandler;

import java.util.Arrays;
import java.util.List;

public class JsonReaderFactory {

    public JsonReader create(Logger logger) {
        List<ConstraintHandler> constraintHandlers = Arrays.asList(
                new EventHandler(),
                new GeoLocationHandler()
        );
        List<SelectorHandler> selectorHandlers = Arrays.asList(
                new PropertySelectorHandler()
        );
        List<AddressHandler> addressHandlers = Arrays.asList(
                new LocalAddressHandler()
        );
        List<DocumentNodeHandler> documentNodeHandlers = Arrays.asList(
                new DataDescriptionHandler(addressHandlers),
                new ContractDescriptionHandler(constraintHandlers, selectorHandlers)
        );

        return new JsonReader(logger, documentNodeHandlers);
    }

}
