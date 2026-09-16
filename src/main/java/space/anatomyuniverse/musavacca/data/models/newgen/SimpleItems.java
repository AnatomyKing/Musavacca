package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SimpleItems {
    private SimpleItems() {}

    public enum Style {
        FLAT,
        HANDHELD
    }

    enum Mode {
        GENERATED,
        EXISTING
    }

    public record PhysicalLayer(ResourceLocation texture, Tints.Tint tint) {
        public PhysicalLayer {
            Objects.requireNonNull(texture, "texture");
            Objects.requireNonNull(tint, "tint");
        }
    }

    public static final class Model {
        private final Mode mode;
        private ResourceLocation existingModel;
        private Style style;
        private boolean folder;
        private boolean inferSingleTexture;
        private List<String> textureTokens;
        private Tints.Tint tint = Tints.none();
        private final Map<Integer, Tints.Tint> layerTints = new LinkedHashMap<>();

        private Model(Mode mode) {
            this.mode = Objects.requireNonNull(mode, "mode");
        }

        private Model(Model other) {
            mode = other.mode;
            existingModel = other.existingModel;
            style = other.style;
            folder = other.folder;
            inferSingleTexture = other.inferSingleTexture;
            textureTokens = other.textureTokens == null ? null : List.copyOf(other.textureTokens);
            tint = other.tint;
            layerTints.putAll(other.layerTints);
        }

        public static Model generated() {
            return new Model(Mode.GENERATED);
        }

        public static Model existing(String modelId) {
            if (modelId == null || modelId.isBlank()) {
                throw new IllegalArgumentException("item model id must not be blank");
            }

            Model model = new Model(Mode.EXISTING);
            model.existingModel = ResourceLocation.parse(modelId);
            return model;
        }

        public static Model existing(ResourceLocation modelId) {
            Model model = new Model(Mode.EXISTING);
            model.existingModel = Objects.requireNonNull(modelId, "modelId");
            return model;
        }

        public Model flat() {
            requireGenerated("flat()");
            style = Style.FLAT;
            return this;
        }

        public Model handheld() {
            requireGenerated("handheld()");
            style = Style.HANDHELD;
            return this;
        }

        public Model folder() {
            requireGenerated("folder()");
            folder = true;
            return this;
        }

        public Model texture() {
            requireGenerated("texture()");
            inferSingleTexture = true;
            textureTokens = List.of();
            return this;
        }

        public Model texture(String texture) {
            return textures(texture);
        }

        public Model textures(String... textures) {
            requireGenerated("textures(...)");

            if (textures == null || textures.length == 0) {
                throw new IllegalArgumentException("item textures must not be empty");
            }

            List<String> copy = new ArrayList<>(textures.length);

            for (String texture : textures) {
                if (texture == null || texture.isBlank()) {
                    throw new IllegalArgumentException("item texture must not be blank");
                }

                copy.add(texture);
            }

            inferSingleTexture = false;
            textureTokens = List.copyOf(copy);
            return this;
        }

        public Model tint(Tints.Tint tint) {
            this.tint = Objects.requireNonNull(tint, "tint");
            return this;
        }

        public Model layerTint(int layer, Tints.Tint tint) {
            requireGenerated("layerTint(...)");

            if (layer < 0) {
                throw new IllegalArgumentException("layer must be >= 0");
            }

            layerTints.put(layer, Objects.requireNonNull(tint, "tint"));
            return this;
        }

        Mode mode() {
            return mode;
        }

        public boolean isGenerated() {
            return mode == Mode.GENERATED;
        }

        public boolean isExisting() {
            return mode == Mode.EXISTING;
        }

        public ResourceLocation existingModel() {
            return existingModel;
        }

        public Style style() {
            return style;
        }

        public boolean folderMode() {
            return folder;
        }

        public Tints.Tint tint() {
            return tint;
        }

        public Map<Integer, Tints.Tint> layerTints() {
            return Collections.unmodifiableMap(layerTints);
        }

        public Tints.Tint tintForLogicalLayer(int layer) {
            return layerTints.getOrDefault(layer, tint);
        }

        public List<ResourceLocation> logicalTextures(ItemLike item) {
            Objects.requireNonNull(item, "item");

            if (!isGenerated()) {
                return List.of();
            }

            if (textureTokens == null) {
                throw new IllegalStateException(
                        "No texture configured for item " + ModelLocations.itemId(item)
                                + ". Call .texture() or .textures(...)."
                );
            }

            if (inferSingleTexture) {
                return List.of(folder ? TextureTokens.itemFolder(item) : TextureTokens.item(item));
            }

            return textureTokens.stream()
                    .map(token -> resolveTexture(item, token))
                    .toList();
        }

        public List<PhysicalLayer> physicalLayers(ItemLike item) {
            validate(item);

            if (isExisting()) {
                return physicalTintList(tint).stream()
                        .map(layerTint -> new PhysicalLayer(TextureTokens.item(item), layerTint))
                        .toList();
            }

            List<ResourceLocation> logical = logicalTextures(item);
            List<PhysicalLayer> physical = new ArrayList<>();

            for (int logicalLayer = 0; logicalLayer < logical.size(); logicalLayer++) {
                ResourceLocation base = logical.get(logicalLayer);
                Tints.Tint layerTint = tintForLogicalLayer(logicalLayer);

                List<Tints.GeneratedLayer> generatedLayers = Tints.generatedLayers(layerTint);

                for (Tints.GeneratedLayer generated : generatedLayers) {
                    ResourceLocation texture = generatedLayers.size() == 1 && generated.sourceLayer() == 0
                            ? base
                            : TextureTokens.itemLayer(base, generated.sourceLayer());

                    physical.add(new PhysicalLayer(
                            texture,
                            layerTint.physicalLayer(generated)
                    ));
                }
            }

            return List.copyOf(physical);
        }

        public List<Tints.Tint> existingTints() {
            if (!isExisting()) {
                throw new IllegalStateException("existingTints() requires an existing item model");
            }

            return physicalTintList(tint);
        }

        public boolean hasAnyTint(ItemLike item) {
            if (isExisting()) {
                return existingTints().stream().anyMatch(Tints.Tint::tinted);
            }

            return physicalLayers(item).stream()
                    .map(PhysicalLayer::tint)
                    .anyMatch(Tints.Tint::tinted);
        }

        public Model copy() {
            return new Model(this);
        }

        public void validate(ItemLike item) {
            Objects.requireNonNull(item, "item");

            if (isExisting()) {
                if (existingModel == null) {
                    throw new IllegalStateException("Existing item model is missing its model id");
                }

                if (style != null || folder || textureTokens != null || !layerTints.isEmpty()) {
                    throw new IllegalStateException(
                            "Existing item models cannot use generated style/folder/texture/layerTint options"
                    );
                }

                return;
            }

            if (style == null) {
                throw new IllegalStateException(
                        "Generated item " + ModelLocations.itemId(item) + " requires .flat() or .handheld()"
                );
            }

            if (textureTokens == null) {
                throw new IllegalStateException(
                        "Generated item " + ModelLocations.itemId(item) + " requires .texture() or .textures(...)"
                );
            }

            int logicalCount = inferSingleTexture ? 1 : textureTokens.size();

            for (Integer layer : layerTints.keySet()) {
                if (layer >= logicalCount) {
                    throw new IllegalStateException(
                            "Logical tint layer " + layer + " is outside the " + logicalCount
                                    + " texture layers for " + ModelLocations.itemId(item)
                    );
                }
            }
        }

        private ResourceLocation resolveTexture(ItemLike item, String token) {
            if (!folder || token.indexOf(':') >= 0 || token.startsWith("item/")) {
                return TextureTokens.resolveItem(item, token);
            }

            ResourceLocation itemId = ModelLocations.itemId(item);
            String path = token.startsWith("_") ? itemId.getPath() + token : token;

            return ResourceLocation.fromNamespaceAndPath(
                    itemId.getNamespace(),
                    "item/" + itemId.getPath() + "/" + path
            );
        }

        private void requireGenerated(String operation) {
            if (!isGenerated()) {
                throw new IllegalStateException(operation + " requires SimpleItems.Model.generated()");
            }
        }

        private static List<Tints.Tint> physicalTintList(Tints.Tint tint) {
            return Tints.generatedLayers(tint).stream()
                    .map(tint::physicalLayer)
                    .toList();
        }
    }

    public static final class Entry {
        private final ItemLike item;
        private final Model model;

        private Entry(Builder builder) {
            item = builder.item;
            model = Objects.requireNonNull(builder.model, "model").copy();
            model.validate(item);
        }

        public static Builder builder(ItemLike item) {
            return new Builder(item);
        }

        public ItemLike item() {
            return item;
        }

        public Model model() {
            return model;
        }
    }

    public static final class Builder {
        private final ItemLike item;
        private Model model;

        private Builder(ItemLike item) {
            this.item = Objects.requireNonNull(item, "item");
        }

        public Builder generated() {
            select(Model.generated());
            return this;
        }

        public Builder model(String modelId) {
            select(Model.existing(modelId));
            return this;
        }

        public Builder model(ResourceLocation modelId) {
            select(Model.existing(modelId));
            return this;
        }

        public Builder model(Model model) {
            select(Objects.requireNonNull(model, "model").copy());
            return this;
        }

        public Builder flat() {
            requireModel().flat();
            return this;
        }

        public Builder handheld() {
            requireModel().handheld();
            return this;
        }

        public Builder folder() {
            requireModel().folder();
            return this;
        }

        public Builder texture() {
            requireModel().texture();
            return this;
        }

        public Builder texture(String texture) {
            requireModel().texture(texture);
            return this;
        }

        public Builder textures(String... textures) {
            requireModel().textures(textures);
            return this;
        }

        public Builder tint(Tints.Tint tint) {
            requireModel().tint(tint);
            return this;
        }

        public Builder layerTint(int layer, Tints.Tint tint) {
            requireModel().layerTint(layer, tint);
            return this;
        }

        public Entry build() {
            if (model == null) {
                throw new IllegalStateException(
                        "Choose .generated() or .model(...) for " + ModelLocations.itemId(item)
                );
            }

            return new Entry(this);
        }

        private void select(Model requested) {
            if (model != null) {
                throw new IllegalStateException("Item model source has already been selected");
            }

            model = requested;
        }

        private Model requireModel() {
            if (model == null) {
                throw new IllegalStateException("Call .generated() or .model(...) first");
            }

            return model;
        }
    }

    public static int legacyTintColor(Entry entry, ItemStack stack, int tintIndex) {
        Objects.requireNonNull(entry, "entry");
        return legacyTintColor(entry.item(), entry.model(), stack, tintIndex);
    }

    public static int legacyTintColor(ItemLike item, Model model, ItemStack stack, int tintIndex) {
        if (tintIndex < 0) {
            return Tints.NO_TINT;
        }

        List<Tints.Tint> tints = model.isExisting()
                ? model.existingTints()
                : model.physicalLayers(item).stream().map(PhysicalLayer::tint).toList();

        return tintIndex < tints.size()
                ? tints.get(tintIndex).itemColor(stack)
                : Tints.NO_TINT;
    }

}

