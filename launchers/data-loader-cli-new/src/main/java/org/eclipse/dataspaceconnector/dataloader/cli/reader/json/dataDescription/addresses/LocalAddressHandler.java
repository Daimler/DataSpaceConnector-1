package org.eclipse.dataspaceconnector.dataloader.cli.reader.json.dataDescription.addresses;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.dataspaceconnector.dataloader.cli.exceptions.reader.ReaderException;
import org.eclipse.dataspaceconnector.dataloader.cli.reader.json.dataDescription.AddressHandler;
import org.eclipse.dataspaceconnector.dataloader.cli.types.addresses.Address;
import org.eclipse.dataspaceconnector.dataloader.cli.types.addresses.LocalAddress;
import org.eclipse.dataspaceconnector.dataloader.cli.util.JsonUtil;

import java.nio.file.Paths;

public class LocalAddressHandler implements AddressHandler {

    private static final String NODE = "file";

    private static final String PATH_FIELD = "path";

    @Override
    public String getNodeType() {
        return NODE;
    }

    @Override
    public Address handle(JsonNode node) throws ReaderException {
        String path = JsonUtil.getTextValue(node, PATH_FIELD);

        return new LocalAddress(Paths.get(path));
    }
}
