package space.anatomyuniverse.musavacca.data.models.newgen;

//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
//? if >=26.1 {
/*import space.anatomyuniverse.musavacca.item.custom.BlockStateItem;
*///?}
import java.util.ArrayList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class SimpleItems {
    private SimpleItems() {}

    static BlockState blockState(ItemLike item) {
        if (!(item.asItem() instanceof BlockItem blockItem))
            throw new IllegalArgumentException(".blockState() requires a BlockItem: " + item);
        BlockItemStateProperties preset;
        //? if >=26.1 {
        /*var holder = blockItem.builtInRegistryHolder();
        if (holder.areComponentsBound()) {
            preset = holder.components().get(DataComponents.BLOCK_STATE);
        } else if (blockItem instanceof BlockStateItem stateItem) {
            preset = stateItem.blockState();
        } else {
            throw new IllegalStateException("Cannot read the block-state preset before components are bound for "
                    + ModelLocations.itemId(item) + ". Use BlockStateItem for .blockState() item definitions on 26.1+.");
        }
        *///?} else {
        preset = blockItem.getDefaultInstance().get(DataComponents.BLOCK_STATE);
        //?}
        BlockState state = blockItem.getBlock().defaultBlockState();
        return preset == null ? state : preset.apply(state);
    }

    public enum Style {
        FLAT,
        HANDHELD
    }
    enum Mode {
        GENERATED,
        EXISTING,
        MULTIPART,
        BLOCK_STATE
    }

    //? if >=1.21.11 {
    /*public record PhysicalLayer(Identifier texture, Tints.Tint tint) {
    *///?} else {
    public record PhysicalLayer(ResourceLocation texture, Tints.Tint tint) {
    //?}
        public PhysicalLayer {
            Objects.requireNonNull(texture, "texture");
            Objects.requireNonNull(tint, "tint");
        }
    }

    public static final class Model {
        private final Mode mode;
        //? if >=1.21.11 {
        /*private Identifier existingModel;
        *///?} else {
        private ResourceLocation existingModel;
        //?}
        private List<SimpleBlocks.Part> parts = List.of();
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
            parts = other.parts;
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
            if (modelId == null || modelId.isBlank()) throw new IllegalArgumentException("item model id must not be blank");
            Model model = new Model(Mode.EXISTING);
            //? if >=1.21.11 {
            /*model.existingModel = Identifier.parse(modelId);
            *///?} else {
            model.existingModel = ResourceLocation.parse(modelId);
            //?}
            return model;
        }

        //? if >=1.21.11 {
        /*public static Model existing(Identifier modelId) {
        *///?} else {
        public static Model existing(ResourceLocation modelId) {
        //?}
            Model model = new Model(Mode.EXISTING);
            model.existingModel = Objects.requireNonNull(modelId, "modelId");
            return model;
        }

        public static Model multipart(SimpleBlocks.Part... parts) {
            if (parts == null || parts.length == 0) throw new IllegalArgumentException("Multipart item needs at least one part");
            Model model = new Model(Mode.MULTIPART);
            model.parts = List.of(parts);
            return model;
        }

        public static Model blockState() {
            return new Model(Mode.BLOCK_STATE);
        }

        public boolean isMultipart() { return mode == Mode.MULTIPART; }
        public boolean isBlockState() { return mode == Mode.BLOCK_STATE; }
        public List<SimpleBlocks.Part> parts() { return parts; }

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
            if (textures == null || textures.length == 0) throw new IllegalArgumentException("item textures must not be empty");
            List<String> copy = new ArrayList<>(textures.length);
            for (String texture : textures) {
                if (texture == null || texture.isBlank()) throw new IllegalArgumentException("item texture must not be blank");
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
            if (layer < 0) throw new IllegalArgumentException("layer must be >= 0");
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

        //? if >=1.21.11 {
        /*public Identifier existingModel() {
        *///?} else {
        public ResourceLocation existingModel() {
        //?}
            return existingModel;
        }

        public Style style() {
            return style;
        }

        public Tints.Tint tint() {
            return tint;
        }

        public Tints.Tint tintForLogicalLayer(int layer) {
            return layerTints.getOrDefault(layer, tint);
        }

        //? if >=1.21.11 {
        /*public List<Identifier> logicalTextures(ItemLike item) { return logicalTextures(ModelLocations.itemId(item)); }

        public List<Identifier> logicalTextures(Identifier id) {
        *///?} else {
        public List<ResourceLocation> logicalTextures(ItemLike item) { return logicalTextures(ModelLocations.itemId(item)); }

        public List<ResourceLocation> logicalTextures(ResourceLocation id) {
        //?}
            if (!isGenerated()) return List.of();
            if (textureTokens == null) throw new IllegalStateException("No texture configured for item " + id);
            if (inferSingleTexture) {
                //? if >=1.21.11 {
                /*return List.of(Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + (folder ? "/" + id.getPath() : "")));
                *///?} else {
                return List.of(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath() + (folder ? "/" + id.getPath() : "")));
                //?}
            }
            return textureTokens.stream().map(token -> resolveTexture(id, token)).toList();
        }

        public List<PhysicalLayer> physicalLayers(ItemLike item) { return physicalLayers(ModelLocations.itemId(item)); }

        //? if >=1.21.11 {
        /*public List<PhysicalLayer> physicalLayers(Identifier id) {
        *///?} else {
        public List<PhysicalLayer> physicalLayers(ResourceLocation id) {
        //?}
            validate(id);
            if (isExisting()) {
                //? if >=1.21.11 {
                /*Identifier texture = Identifier.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
                *///?} else {
                ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/" + id.getPath());
                //?}
                return physicalTintList(tint).stream().map(layerTint -> new PhysicalLayer(texture, layerTint)).toList();
            }
            if (isMultipart() || isBlockState()) return List.of();
            //? if >=1.21.11 {
            /*List<Identifier> logical = logicalTextures(id);
            *///?} else {
            List<ResourceLocation> logical = logicalTextures(id);
            //?}
            List<PhysicalLayer> physical = new ArrayList<>();
            for (int i = 0; i < logical.size(); i++) {
                var base = logical.get(i);
                Tints.Tint layerTint = tintForLogicalLayer(i);
                List<Tints.GeneratedLayer> generatedLayers = Tints.generatedLayers(layerTint);
                for (Tints.GeneratedLayer generated : generatedLayers) {
                    var texture = generatedLayers.size() == 1 && generated.sourceLayer() == 0
                            ? base : TextureTokens.itemLayer(base, generated.sourceLayer());
                    physical.add(new PhysicalLayer(texture, layerTint.physicalLayer(generated)));
                }
            }
            return List.copyOf(physical);
        }

        public List<Tints.Tint> existingTints() {
            if (!isExisting()) throw new IllegalStateException("existingTints() requires an existing item model");
            return physicalTintList(tint);
        }

        public boolean hasAnyTint(ItemLike item) {
            if (isExisting()) return existingTints().stream().anyMatch(Tints.Tint::tinted);
            if (isMultipart() || isBlockState()) return NewgenItemCatalog.declaredItemTints(item, this).stream().anyMatch(Tints.Tint::tinted);
            return physicalLayers(item).stream().map(PhysicalLayer::tint).anyMatch(Tints.Tint::tinted);
        }

        public Model copy() {
            return new Model(this);
        }

        public void validate(ItemLike item) { validate(ModelLocations.itemId(item)); }

        //? if >=1.21.11 {
        /*public void validate(Identifier id) {
        *///?} else {
        public void validate(ResourceLocation id) {
        //?}
            Objects.requireNonNull(id, "id");
            if (isMultipart() || isBlockState()) {
                if (tint.tinted() || !layerTints.isEmpty() || style != null || folder || textureTokens != null)
                    throw new IllegalStateException("Composite items use per-part tinting and cannot use generated item options");
                if (isMultipart()) {
                    for (SimpleBlocks.Part part : parts) {
                        if (part == null || !part.conditions().isAlways())
                            throw new IllegalStateException("Manual multipart items require unconditional parts; use .blockState() for conditional parts");
                        if (part.variants().options().size() != 1)
                            throw new IllegalStateException("Item multipart cannot randomize variants");
                    }
                }
                return;
            }
            if (isExisting()) {
                if (existingModel == null) throw new IllegalStateException("Existing item model is missing its model id");
                if (style != null || folder || textureTokens != null || !layerTints.isEmpty()) {
                    throw new IllegalStateException("Existing item models cannot use generated style/folder/texture/layerTint options");
                }
                return;
            }
            if (style == null) throw new IllegalStateException("Generated item " + id + " requires .flat() or .handheld()");
            if (textureTokens == null) {
                throw new IllegalStateException("Generated item " + id + " requires .texture() or .textures(...)");
            }
            int logicalCount = inferSingleTexture ? 1 : textureTokens.size();
            for (Integer layer : layerTints.keySet()) {
                if (layer >= logicalCount) {
                    throw new IllegalStateException(
                            "Logical tint layer " + layer + " is outside the " + logicalCount
                                    + " texture layers for " + id
                    );
                }
            }
        }

        //? if >=1.21.11 {
        /*private Identifier resolveTexture(Identifier id, String token) {
        *///?} else {
        private ResourceLocation resolveTexture(ResourceLocation id, String token) {
        //?}
            if (!folder || token.indexOf(':') >= 0 || token.startsWith("item/")) return TextureTokens.resolveItem(id, token);
            //? if >=1.21.11 {
            /*Identifier itemId = id;
            *///?} else {
            ResourceLocation itemId = id;
            //?}
            String path = token.startsWith("_") ? itemId.getPath() + token : token;
            //? if >=1.21.11 {
            /*return Identifier.fromNamespaceAndPath(itemId.getNamespace(), "item/" + itemId.getPath() + "/" + path);
            *///?} else {
            return ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(), "item/" + itemId.getPath() + "/" + path);
            //?}
        }

        private void requireGenerated(String operation) {
            if (!isGenerated()) throw new IllegalStateException(operation + " requires SimpleItems.Model.generated()");
        }

        private static List<Tints.Tint> physicalTintList(Tints.Tint tint) {
            return Tints.generatedLayers(tint).stream().map(tint::physicalLayer).toList();
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

        public Builder multipart(SimpleBlocks.Part... parts) {
            select(Model.multipart(parts));
            return this;
        }

        public Builder blockState() {
            select(Model.blockState());
            return this;
        }

        public Builder generated() {
            select(Model.generated());
            return this;
        }

        public Builder model(String modelId) {
            select(Model.existing(modelId));
            return this;
        }

        //? if >=1.21.11 {
        /*public Builder model(Identifier modelId) {
        *///?} else {
        public Builder model(ResourceLocation modelId) {
        //?}
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
            if (model == null) throw new IllegalStateException("Choose .generated(), .model(...), .multipart(...) or .blockState() for " + ModelLocations.itemId(item));
            return new Entry(this);
        }

        private void select(Model requested) {
            if (model != null) throw new IllegalStateException("Item model source has already been selected");
            model = requested;
        }

        private Model requireModel() {
            if (model == null) throw new IllegalStateException("Call .generated(), .model(...), .multipart(...) or .blockState() first");
            return model;
        }
    }

    public static int legacyTintColor(ItemLike item, Model model, ItemStack stack, int tintIndex) {
        if (tintIndex < 0) return Tints.NO_TINT;
        List<Tints.Tint> tints = NewgenItemCatalog.itemTints(item, model);
        return tintIndex < tints.size() ? tints.get(tintIndex).itemColor(stack) : Tints.NO_TINT;
    }
}


