package org.eclipse.dataspaceconnector.ids.transform;

import de.fraunhofer.iais.eis.BinaryOperator;
import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.policy.model.Operator;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OperatorToBinaryOperatorTransformer implements IdsTypeTransformer<Operator, BinaryOperator> {
    private static final Map<Operator, BinaryOperator> MAPPING = new HashMap<>() {
        {
            put(Operator.EQ, BinaryOperator.EQUALS);
            put(Operator.GT, BinaryOperator.GT);
            put(Operator.GEQ, BinaryOperator.GTEQ);
            put(Operator.LT, BinaryOperator.LT);
            put(Operator.LEQ, BinaryOperator.LTEQ);
            put(Operator.IN, BinaryOperator.IN);
        }
    };

    @Override
    public Class<Operator> getInputType() {
        return Operator.class;
    }

    @Override
    public Class<BinaryOperator> getOutputType() {
        return BinaryOperator.class;
    }

    @Override
    public @Nullable BinaryOperator transform(Operator object, TransformerContext context) {
        Objects.requireNonNull(context);
        if (object == null) {
            return null;
        }

        BinaryOperator binaryOperator = MAPPING.get(object);
        if (binaryOperator != null) {
            return binaryOperator;
        }

        context.reportProblem(String.format("Can not transform %s to IDS BinaryOperator", object.name()));

        return null;
    }
}
