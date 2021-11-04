/*
 *  Copyright (c) 2021 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering - Initial API and Implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.transform;

import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.policy.model.Duty;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class DutyToDutyTransformer implements IdsTypeTransformer<Duty, de.fraunhofer.iais.eis.Duty> {

    @Override
    public Class<Duty> getInputType() {
        return Duty.class;
    }

    @Override
    public Class<de.fraunhofer.iais.eis.Duty> getOutputType() {
        return de.fraunhofer.iais.eis.Duty.class;
    }

    @Override
    public @Nullable de.fraunhofer.iais.eis.Duty transform(Duty object, TransformerContext context) {
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        var idsConstraints = new ArrayList<Constraint>();
        for (var edcConstraint : object.getConstraints()) {
            var idsConstraint = context.transform(edcConstraint, de.fraunhofer.iais.eis.Constraint.class);
            idsConstraints.add(idsConstraint);
        }

        var target = context.transform(object.getTarget(), URI.class);
        var assigner = context.transform(object.getAssigner(), URI.class);
        var assignee = context.transform(object.getAssignee(), URI.class);
        var action = context.transform(object.getAction(), de.fraunhofer.iais.eis.Action.class);

        var idsId = IdsId.Builder.newInstance().value(object.hashCode()).type(IdsType.PERMISSION).build();
        var id = context.transform(idsId, URI.class);
        var dutyBuilder = new DutyBuilder(id);

        dutyBuilder._constraint_(idsConstraints);
        dutyBuilder._target_(target);
        dutyBuilder._assigner_(new ArrayList<>(Collections.singletonList(assigner)));
        dutyBuilder._assignee_(new ArrayList<>(Collections.singletonList(assignee)));
        dutyBuilder._action_(new ArrayList<>(Collections.singletonList(action)));

        de.fraunhofer.iais.eis.Duty duty;
        try {
            duty = dutyBuilder.build();
        } catch (ConstraintViolationException e) {
            context.reportProblem(String.format("Failed to build IDS duty: %s", e.getMessage()));
            duty = null;
        }

        return duty;
    }
}
