package org.eclipse.dataspaceconnector.ids.transform;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.ProhibitionBuilder;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.policy.model.Prohibition;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ProhibitionToProhibitionTransformer implements IdsTypeTransformer<Prohibition, de.fraunhofer.iais.eis.Prohibition> {

    @Override
    public Class<Prohibition> getInputType() {
        return Prohibition.class;
    }

    @Override
    public Class<de.fraunhofer.iais.eis.Prohibition> getOutputType() {
        return de.fraunhofer.iais.eis.Prohibition.class;
    }

    @Override
    public @Nullable de.fraunhofer.iais.eis.Prohibition transform(Prohibition object, TransformerContext context) {
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        var action = context.transform(object.getAction(), Action.class);
        var assinger = context.transform(object.getAssigner(), URI.class);
        var assignee = context.transform(object.getAssignee(), URI.class);
        var target = context.transform(object.getTarget(), URI.class);
        var constraints = new ArrayList<Constraint>();
        for (var edcConstraint : object.getConstraints()) {
            var idsConstraint = context.transform(edcConstraint, Constraint.class);
            constraints.add(idsConstraint);
        }

        var idsId = IdsId.Builder.newInstance().value(object.hashCode()).type(IdsType.PROHIBITION).build();
        var id = context.transform(idsId, URI.class);
        var prohibitionBuilder = new ProhibitionBuilder(id);

        prohibitionBuilder._action_(new ArrayList<>(Collections.singletonList(action)));
        prohibitionBuilder._assigner_(new ArrayList<>(Collections.singletonList(assinger)));
        prohibitionBuilder._assignee_(new ArrayList<>(Collections.singletonList(assignee)));
        prohibitionBuilder._target_(target);
        prohibitionBuilder._constraint_(constraints);

        return prohibitionBuilder.build();
    }
}
