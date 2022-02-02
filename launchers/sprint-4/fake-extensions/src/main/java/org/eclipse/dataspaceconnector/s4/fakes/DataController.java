package org.eclipse.dataspaceconnector.s4.fakes;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

@Consumes({MediaType.TEXT_PLAIN})
@Path("/fake")
public class DataController {

    private final Monitor monitor;

    public DataController(Monitor monitor) {
        this.monitor = monitor;
    }

    @POST
    @Path("/data")
    public Response receiveData(String data) {

        monitor.info(String.format("Received (fake) data: %s", data));

        // TODO Update Transfer Process State

        return Response.ok().build();
    }
}
