// file: src/main/java/space/anatomyuniverse/musavacca/data/models/item/FlatItems.java
package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
*///?} else {
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.world.item.Item;
import space.anatomyuniverse.musavacca.tint.HexColorItemTintSource;

import java.util.List;
//?}

public final class FlatItems {
    private FlatItems() {}

    /**
     * One flat generated item with one runtime HEX_COLOR tint.
     *
     * <p>This deliberately has no PearlFire profile and no layered-item tint
     * abstraction: one model, one texture, one tint source.</p>
     */
    public record HexTinted(
            ItemLike item,
            String modelStem,
            String textureStem
    ) {
        public HexTinted {
            if (item == null) {
                throw new IllegalArgumentException("item must not be null");
            }
            if (modelStem == null || modelStem.isBlank()) {
                throw new IllegalArgumentException("modelStem must not be blank");
            }
            if (textureStem == null || textureStem.isBlank()) {
                throw new IllegalArgumentException("textureStem must not be blank");
            }
        }

        public ResourceLocation itemId() {
            return ModelUtil.idOf(item);
        }

        public ResourceLocation modelId() {
            ResourceLocation id = itemId();
            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/" + modelStem
            );
        }

        public ResourceLocation texture() {
            ResourceLocation id = itemId();
            return ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    "item/" + textureStem
            );
        }
    }

    public static HexTinted hexTintedRoot(ItemLike item) {
        String stem = ModelUtil.pathOf(item);
        return new HexTinted(item, stem, stem);
    }

    /** item/example/example.png -> item/example model */
    public static HexTinted hexTintedFolder(ItemLike item) {
        String stem = ModelUtil.pathOf(item);
        return new HexTinted(item, stem, stem + "/" + stem);
    }

    //? if <1.21.4 {
    /*/^* Flat generated item (parent: minecraft:item/generated). ^/
    public static void generate(ItemModelProvider itemModels, ItemLike... items) {
        for (ItemLike it : items) {
            itemModels.basicItem(it.asItem());
        }
    }

    public static void generateHexTinted(
            ItemModelProvider itemModels,
            HexTinted... entries
    ) {
        if (entries == null) {
            return;
        }

        for (HexTinted entry : entries) {
            if (entry == null) {
                continue;
            }

            itemModels.withExistingParent(
                            entry.modelStem(),
                            itemModels.mcLoc("item/generated")
                    )
                    .texture("layer0", entry.texture());
        }
    }
    *///?} else {
    /** Flat generated item for 1.21.4+ via ItemModelGenerators. */
    public static void generate(ItemModelGenerators gen, ItemLike... items) {
        for (ItemLike it : items) {
            Item item = it.asItem();
            gen.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        }
    }

    public static void generateHexTinted(
            ItemModelGenerators gen,
            HexTinted... entries
    ) {
        if (entries == null) {
            return;
        }

        for (HexTinted entry : entries) {
            if (entry == null) {
                continue;
            }

            ResourceLocation model = ModelTemplates.FLAT_ITEM.create(
                    entry.modelId(),
                    TextureMapping.layer0(entry.texture()),
                    gen.modelOutput
            );

            gen.itemModelOutput.accept(
                    entry.item().asItem(),
                    new BlockModelWrapper.Unbaked(
                            model,
                            List.of(HexColorItemTintSource.INSTANCE)
                    )
            );
        }
    }
    //?}
}
