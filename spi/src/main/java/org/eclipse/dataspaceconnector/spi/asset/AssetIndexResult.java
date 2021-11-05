package org.eclipse.dataspaceconnector.spi.asset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.dataspaceconnector.spi.pagination.Cursor;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class AssetIndexResult implements Iterable<Asset> {
    @NotNull
    private final AssetSelectorExpression expression;
    @NotNull
    private final List<Asset> assets;
    @Nullable
    private final Cursor cursor;

    public AssetIndexResult(
            @NotNull AssetSelectorExpression expression,
            @NotNull List<Asset> assets,
            @Nullable Cursor cursor) {
        this.expression = Objects.requireNonNull(expression);
        this.assets = Objects.requireNonNull(assets);
        this.cursor = cursor;
    }

    public @NotNull AssetSelectorExpression getExpression() {
        return expression;
    }

    public @Nullable Cursor getCursor() {
        return cursor;
    }

    @NotNull
    @Override
    public Iterator<Asset> iterator() {
        return assets.iterator();
    }

    @JsonPOJOBuilder
    public static class Builder {
        private List<Asset> assets;
        private Cursor cursor;
        private AssetSelectorExpression expression;

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        private Builder() {
            assets = new ArrayList<>();
        }

        public Builder assets(List<Asset> assets) {
            this.assets = assets;
            return this;
        }

        public Builder cursor(final Cursor cursor) {
            this.cursor = cursor;
            return this;
        }


        public Builder expression(AssetSelectorExpression expression) {
            this.expression = expression;
            return this;
        }

        public AssetIndexResult build() {
            return new AssetIndexResult(
                    expression,
                    assets,
                    cursor);
        }
    }
}
