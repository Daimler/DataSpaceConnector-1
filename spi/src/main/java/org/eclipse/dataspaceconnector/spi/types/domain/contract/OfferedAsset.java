package org.eclipse.dataspaceconnector.spi.types.domain.contract;

import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.policy.UsagePolicy;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OfferedAsset {
    private UsagePolicy usagePolicy;
    private Asset asset;

    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    public UsagePolicy getUsagePolicy() {
        return usagePolicy;
    }

    @NotNull
    public Asset getAsset() {
        return asset;
    }

    public static final class Builder {
        private UsagePolicy usagePolicy;
        private Asset asset;

        private Builder() {
        }

        public Builder usagePolicy(final UsagePolicy usagePolicy) {
            this.usagePolicy = usagePolicy;
            return this;
        }

        public Builder asset(final Asset asset) {
            this.asset = asset;
            return this;
        }

        public OfferedAsset build() {
            final OfferedAsset offeredAsset = new OfferedAsset();
            offeredAsset.asset = Objects.requireNonNull(this.asset);
            offeredAsset.usagePolicy = Objects.requireNonNull(this.usagePolicy);
            return offeredAsset;
        }
    }
}
