package org.eclipse.dataspaceconnector.spi.types.domain.policy;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UsagePolicy {
    private List<Rule> rules;

    @NotNull
    public List<Rule> getRules() {
        return Optional.ofNullable(rules)
                .map(Collections::unmodifiableList)
                .orElseGet(Collections::emptyList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private List<Rule> rules;

        private Builder() {
        }

        public Builder rules(final List<Rule> rules) {
            this.rules = rules;
            return this;
        }

        public UsagePolicy build() {
            final UsagePolicy usagePolicy = new UsagePolicy();
            usagePolicy.rules = this.rules;
            return usagePolicy;
        }
    }
}
