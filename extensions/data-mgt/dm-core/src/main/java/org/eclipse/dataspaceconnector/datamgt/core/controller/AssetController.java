package org.eclipse.dataspaceconnector.datamgt.core.controller;


import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.dataspaceconnector.datamgt.spi.AssetRepository;
import org.eclipse.dataspaceconnector.datamgt.spi.exceptions.IdentifierAlreadyExistsException;
import org.eclipse.dataspaceconnector.datamgt.spi.exceptions.NotFoundException;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;

import java.util.Collections;
import java.util.List;

import static org.eclipse.dataspaceconnector.datamgt.core.util.CriteriaUtil.getCriteriaFromUri;

@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Path("/data/asset")
public class AssetController {

    private static final String LOG_MSG = "AssetController - %s %s";
    private static final String LOG_DUPLICATION = "AssetController - %s : identifier already exists: %s";
    private static final String LOG_NOT_FOUND = "AssetController - %s : not found (id: %s)";
    private static final String LOG_ERR = "AssetController Error - %s : %s";

    private final AssetRepository repository;
    private final Monitor monitor;

    public AssetController(AssetRepository repository, Monitor monitor) {
        this.repository = repository;
        this.monitor = monitor;
    }

    @POST
    public Response create(Asset asset) {
        try {
            repository.create(asset);
            monitor.debug(String.format(LOG_MSG, "create", String.format("(id: %s)", asset.getId())));
            return Response.status(Response.Status.CREATED).build();
        } catch (IdentifierAlreadyExistsException e) {
            monitor.warning(String.format(LOG_DUPLICATION, "create", e.getIdentifier()));
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            monitor.severe(String.format(LOG_ERR, "create", e.getMessage()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("{id}")
    public Response read(@PathParam("id") String id) {
        try {
            List<Criterion> criteria = Collections.singletonList(new Criterion(Asset.PROPERTY_ID, "=", id));
            List<Asset> assets = repository.queryAssets(criteria);
            if (assets.size() == 0) {
                monitor.warning(String.format(LOG_NOT_FOUND, "read", id));
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            monitor.debug(String.format(LOG_MSG, "read", String.format("(id: %s)", id)));
            return Response.ok(assets.get(0)).build();
        } catch (Exception e) {
            monitor.severe(String.format(LOG_ERR, "read", e.getMessage()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    public Response read(@Context UriInfo ui) {
        try {
            List<Criterion> criteria = getCriteriaFromUri(ui);
            List<Asset> assets = repository.queryAssets(criteria);

            monitor.debug(String.format(LOG_MSG, "read", String.format("(count: %s)", assets.size())));
            return Response.ok(assets).build();
        } catch (Exception e) {
            monitor.severe(String.format(LOG_ERR, "read", e.getMessage()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") String id, Asset asset) {

        if (!asset.getId().equals(id)) {
            monitor.warning(String.format(LOG_ERR, "update", "identifier immutable"));
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        try {
            repository.update(asset);

            monitor.debug(String.format(LOG_MSG, "update", String.format("(id: %s)", id)));
            return Response.ok().build();
        } catch (Exception e) {
            monitor.severe(String.format(LOG_ERR, "update", e.getMessage()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id) {

        try {
            repository.delete(id);

            monitor.debug(String.format(LOG_MSG, "delete", String.format("(id: %s)", id)));
            return Response.ok().build();
        } catch (NotFoundException e) {

            monitor.warning(String.format(LOG_NOT_FOUND, "delete", id));
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            monitor.severe(String.format(LOG_ERR, "delete", e.getMessage()));
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
