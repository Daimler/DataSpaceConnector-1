package org.eclipse.dataspaceconnector.ids.transform;

import de.fraunhofer.iais.eis.PermissionBuilder;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.policy.model.Permission;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class PermissionToPermissionTransformer implements IdsTypeTransformer<Permission, de.fraunhofer.iais.eis.Permission> {

    @Override
    public Class<Permission> getInputType() {
        return Permission.class;
    }

    @Override
    public Class<de.fraunhofer.iais.eis.Permission> getOutputType() {
        return de.fraunhofer.iais.eis.Permission.class;
    }

    @Override
    public @Nullable de.fraunhofer.iais.eis.Permission transform(Permission object, TransformerContext context) {
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        var idsConstraints = new ArrayList<de.fraunhofer.iais.eis.Constraint>();
        for (var edcConstraint : object.getConstraints()) {
            var idsConstraint = context.transform(edcConstraint, de.fraunhofer.iais.eis.Constraint.class);
            idsConstraints.add(idsConstraint);
        }

        var target = context.transform(object.getTarget(), URI.class);
        var assigner = context.transform(object.getAssigner(), URI.class);
        var assignee = context.transform(object.getAssignee(), URI.class);

        var duty = context.transform(object.getDuty(), de.fraunhofer.iais.eis.Duty.class);
        var action = context.transform(object.getAction(), de.fraunhofer.iais.eis.Action.class);

        var idsId = IdsId.Builder.newInstance().value(object.hashCode()).type(IdsType.PERMISSION).build();
        var id = context.transform(idsId, URI.class);
        var permissionBuilder = new PermissionBuilder(id);

        permissionBuilder._constraint_(idsConstraints);
        permissionBuilder._target_(target);
        permissionBuilder._assigner_(new ArrayList<>(Collections.singletonList(assigner)));
        permissionBuilder._assignee_(new ArrayList<>(Collections.singletonList(assignee)));
        permissionBuilder._action_(new ArrayList<>(Collections.singletonList(action)));

        if (duty != null) {
            context.reportProblem("Undefined Transformation: EDC-Duty to IDS Pre-/Post-Duty");
        }
        //        permissionBuilder._postDuty_()
        //        permissionBuilder._preDuty_()

        return permissionBuilder.build();
    }
}
