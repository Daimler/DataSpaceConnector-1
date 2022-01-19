package org.eclipse.dataspaceconnector.api.data.controller;

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
import org.eclipse.dataspaceconnector.api.data.types.AssetAndAddress;
import org.eclipse.dataspaceconnector.clients.postgresql.asset.Repository;
import org.eclipse.dataspaceconnector.spi.asset.Criterion;
import org.eclipse.dataspaceconnector.spi.monitor.Monitor;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractDefinition;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataAddress;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Path("/data")
public class DataManagementController {

    // TODO As soon as other storage systems support CRUD operations, too, remove and update repository.
    private final Repository repository;
    private final Monitor monitor;

    public DataManagementController(Repository repository, Monitor monitor) {
        this.repository = repository;
        this.monitor = monitor;
    }

    @PUT
    @Path("/asset")
    public Response createAssetAndAddress(AssetAndAddress assetAndAddress) {
        try {
            repository.create(assetAndAddress.getAsset(), assetAndAddress.getAddress());
            return Response.ok().build();
        } catch (SQLException e) {
            monitor.debug("Dataloader - Create Asset and Address", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/asset")
    public Response getAssets(@Context UriInfo ui) {
        try {
            List<Criterion> criteria = getCriteriaFromUri(ui);
            List<Asset> assets = repository.queryAssets(criteria);
            return Response.ok(assets).build();
        } catch (SQLException e) {
            monitor.debug("Dataloader - Get Assets", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/asset")
    public Response updateAsset(Asset asset) {
        try {
            repository.update(asset);
            return Response.ok().build();
        } catch (SQLException e) {
            monitor.debug("Dataloader - Update Asset", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/asset/{id}")
    public Response deleteAsset(@PathParam("id") String id) {
        try {
            repository.deleteAsset(id);
            return Response.ok().build();
        } catch (SQLException e) {
            monitor.debug("DataLoader - Delete Asset", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/address")
    public Response getAddresses(@Context UriInfo ui) {
        try {
            List<Criterion> criteria = getCriteriaFromUri(ui);
            List<DataAddress> addresses = repository.queryAddress(criteria);
            return Response.ok(addresses).build();
        } catch (SQLException e) {
            monitor.debug("DataLoader - Get Address", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/address")
    public Response updateAddress(AssetAndAddress assetAndAddress) {
        try {
            Asset asset = assetAndAddress.getAsset();
            DataAddress address = assetAndAddress.getAddress();
            repository.update(asset, address);
            return Response.ok().build();
        } catch (SQLException e) {
            monitor.debug("DataLoader - Update Address", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/contract-definition")
    public Response getContractDefinition() {
        try {
            List<ContractDefinition> definitions = repository.queryAllContractDefinitions();
            return Response.ok(definitions).build();
        } catch (SQLException e) {
            monitor.debug("DataLoader - Get Contract Definitions", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/contract-definition")
    public Response createContractDefinition(ContractDefinition contractDefinition) {
        try {
            repository.create(contractDefinition);
            return Response.ok().build();
        } catch (SQLException e) {
            monitor.debug("DataLoader - Create Contract Definition", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Path("/contract-definition")
    public Response updateContractDefinition(ContractDefinition contractDefinition) {
        try {
            repository.update(contractDefinition);
            return Response.ok().build();
        } catch (SQLException e) {
            monitor.debug("DataLoader - Update Contract Definition", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/contract-definition/{id}")
    public Response deleteContractDefinition(@PathParam("id") String id) {
        try {
            repository.deleteContractDefinition(id);
            return Response.ok().build();
        } catch (SQLException e) {
            monitor.debug("DataLoader - Delete Contract Definition", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private List<Criterion> getCriteriaFromUri(@Context UriInfo ui) {
        List<Criterion> criteria = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : ui.getQueryParameters().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().stream().findFirst().orElse(null);

            criteria.add(new Criterion(key, "=", value));
        }
        return criteria;
    }
}
