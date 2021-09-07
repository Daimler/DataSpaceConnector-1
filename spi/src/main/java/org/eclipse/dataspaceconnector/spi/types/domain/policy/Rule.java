package org.eclipse.dataspaceconnector.spi.types.domain.policy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A Rule object consists of an {@link Action} which use is obliged by a
 * set of {@link Constraint}s.
 */
public class Rule {
    private Action action;
    private List<Constraint> constraints;

    public Action getAction() {
        return action;
    }

    public List<Constraint> getConstraints() {
        return Optional.ofNullable(constraints)
                .map(Collections::unmodifiableList)
                .orElseGet(Collections::emptyList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Action action;
        private List<Constraint> constraints;

        private Builder() {
        }

        public Builder action(final Action action) {
            this.action = action;
            return this;
        }

        public Builder constraints(final List<Constraint> constraints) {
            this.constraints = constraints;
            return this;
        }

        public Rule build() {
            final Rule rule = new Rule();
            rule.action = this.action;
            rule.constraints = this.constraints;
            return rule;
        }
    }
}
