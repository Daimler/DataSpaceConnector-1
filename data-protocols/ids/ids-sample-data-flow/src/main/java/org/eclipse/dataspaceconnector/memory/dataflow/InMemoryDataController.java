package org.eclipse.dataspaceconnector.memory.dataflow;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.dataspaceconnector.spi.asset.AssetIndexLoader;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataAddress;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Path("/data")
public class InMemoryDataController {
    private final InMemoryDataStore inMemoryDataStore;
    private final Monitor monitor;
    private final AssetIndexLoader assetIndexLoader;

    public InMemoryDataController(@NotNull Monitor monitor, @NotNull InMemoryDataStore inMemoryDataStore, @NotNull AssetIndexLoader assetIndexLoader) {
        this.inMemoryDataStore = Objects.requireNonNull(inMemoryDataStore);
        this.monitor = Objects.requireNonNull(monitor);
        this.assetIndexLoader = Objects.requireNonNull(assetIndexLoader);
    }

    @POST
    @Path("/{key}")
    @Consumes({ MediaType.MULTIPART_FORM_DATA })
    public Response upload(@PathParam("key") String key, @FormDataParam("file") InputStream is) {

        try (var buffer = new ByteArrayOutputStream()) {
            int nRead;
            byte[] data = new byte[4096];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            inMemoryDataStore.save(key, buffer.toByteArray());

            Asset asset = Asset.Builder.newInstance().id(key).build();
            DataAddress dataAddress = DataAddress.Builder.newInstance()
                    .type(InMemoryDataFlowSchema.TYPE)
                    .property(InMemoryDataFlowSchema.ATTRIBUTE_KEY, key)
                    .build();

            assetIndexLoader.insert(asset, dataAddress);

        } catch (IOException e) {
            monitor.severe(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.status(Response.Status.OK).build();
    }
}
