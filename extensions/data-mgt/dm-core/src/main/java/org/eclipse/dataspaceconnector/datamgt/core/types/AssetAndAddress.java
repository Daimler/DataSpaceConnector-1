package org.eclipse.dataspaceconnector.datamgt.core.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.transfer.DataAddress;

@JsonDeserialize(builder = AssetAndAddress.Builder.class)
public class AssetAndAddress {

    @JsonProperty("asset")
    private final Asset asset;
    @JsonProperty("address")
    private final DataAddress address;

    public AssetAndAddress(Asset asset, DataAddress address) {
        this.asset = asset;
        this.address = address;
    }

    public Asset getAsset() {
        return asset;
    }

    public DataAddress getAddress() {
        return address;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {

        private Asset asset = null;
        private DataAddress dataAddress = null;

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
        }

        public Builder asset(Asset asset) {
            this.asset = asset;
            return this;
        }

        public Builder address(DataAddress address) {
            this.dataAddress = address;
            return this;
        }

        public AssetAndAddress build() {
            return new AssetAndAddress(asset, dataAddress);
        }

    }
}
