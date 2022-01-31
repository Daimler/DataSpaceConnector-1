package org.eclipse.dataspaceconnector.s4.fakes;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;

@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DataController {

    private final Monitor monitor;

    public DataController(Monitor monitor) {
        this.monitor = monitor;
    }

    @POST
    @Path("/data")
    public Response receiveData(String data) {

        monitor.info(String.format("Received data: %s", data));

        return Response.ok().build();
    }
}
