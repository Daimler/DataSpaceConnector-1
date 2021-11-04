package org.eclipse.dataspaceconnector.ids.transform;

import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Prohibition;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.policy.model.Policy;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

public class PolicyToContractOfferTransformer implements IdsTypeTransformer<Policy, ContractOffer> {

    public PolicyToContractOfferTransformer() {
    }

    @Override
    public Class<Policy> getInputType() {
        return Policy.class;
    }

    @Override
    public Class<ContractOffer> getOutputType() {
        return ContractOffer.class;
    }

    @Override
    public @Nullable ContractOffer transform(Policy object, TransformerContext context) {
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        var idsPermissions = new ArrayList<Permission>();
        var idsProhibitions = new ArrayList<Prohibition>();
        var idsObligations = new ArrayList<Duty>();

        for (var edcPermission : object.getPermissions()) {
            var idsPermission = context.transform(edcPermission, Permission.class);
            idsPermissions.add(idsPermission);
        }

        for (var edcProhibition : object.getProhibitions()) {
            var idsProhibition = context.transform(edcProhibition, Prohibition.class);
            idsProhibitions.add(idsProhibition);
        }

        for (var edcObligation : object.getObligations()) {
            var idsObligation = context.transform(edcObligation, Duty.class);
            idsObligations.add(idsObligation);
        }

        var provider = context.transform(object.getAssigner(), URI.class);

        ContractOfferBuilder contractOfferBuilder = new ContractOfferBuilder(); // TODO Is there are way to not autogenerate the id?

        contractOfferBuilder._obligation_(idsObligations);
        contractOfferBuilder._prohibition_(idsProhibitions);
        contractOfferBuilder._permission_(idsPermissions);
        contractOfferBuilder._provider_(provider);

        return contractOfferBuilder.build();
    }
}
