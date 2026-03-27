// file: src/main/java/space/anatomyuniverse/musavacca/data/models/item/SpawnEggItems.java
package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

//? if <=1.21.3 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
 *///?}

//? if >=1.21.4 {
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.item.BlockModelWrapper;
//?}

//? if =1.21.4 {
/*import net.minecraft.client.color.item.Constant;

import java.util.List;
*///?}

public final class SpawnEggItems {
    private SpawnEggItems() {}

    public static final int DEFAULT_PRIMARY_COLOR = 0xFFFFFF;
    public static final int DEFAULT_SECONDARY_COLOR = 0xFFFFFF;

    public record Entry(ItemLike item, int primaryColor, int secondaryColor) {
        public Entry {
            primaryColor &= 0xFFFFFF;
            secondaryColor &= 0xFFFFFF;
        }
    }

    public static Entry of(ItemLike item, int primaryColor, int secondaryColor) {
        return new Entry(item, primaryColor, secondaryColor);
    }

    public static Entry of(ItemLike item) {
        return new Entry(item, DEFAULT_PRIMARY_COLOR, DEFAULT_SECONDARY_COLOR);
    }

    public static Entry find(Entry[] entries, ItemLike item) {
        if (item == null) {
            return new Entry(null, DEFAULT_PRIMARY_COLOR, DEFAULT_SECONDARY_COLOR);
        }

        Item target = item.asItem();

        if (entries != null) {
            for (Entry entry : entries) {
                if (entry != null && entry.item() != null && entry.item().asItem() == target) {
                    return entry;
                }
            }
        }

        return new Entry(item, DEFAULT_PRIMARY_COLOR, DEFAULT_SECONDARY_COLOR);
    }

    public static int primaryColor(Entry[] entries, ItemLike item) {
        return find(entries, item).primaryColor();
    }

    public static int secondaryColor(Entry[] entries, ItemLike item) {
        return find(entries, item).secondaryColor();
    }

    // =========================================================
    // <= 1.21.3
    // Old spawn egg template model.
    // Actual colors are still consumed by SpawnEggItem constructor.
    // =========================================================
    //? if <=1.21.3 {
    /*public static void generate(ItemModelProvider itemModels, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null || entry.item() == null) continue;

            itemModels.withExistingParent(
                    ModelUtil.pathOf(entry.item()),
                    itemModels.mcLoc("item/template_spawn_egg")
            );
        }
    }
    *///?}

    // =========================================================
    // >= 1.21.4
    // Single method, Stonecutter splits logic inside the body:
    // =1.21.4   -> template_spawn_egg + tint constants
    // >=1.21.5  -> flat item model + plain client item
    // =========================================================
    //? if >=1.21.4 {
    private static final ResourceLocation TEMPLATE_SPAWN_EGG_MODEL =
            ResourceLocation.fromNamespaceAndPath("minecraft", "item/template_spawn_egg");

    public static void generate(ItemModelGenerators items, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null || entry.item() == null) continue;

            //? if =1.21.4 {
            /*items.itemModelOutput.accept(
                    entry.item().asItem(),
                    new BlockModelWrapper.Unbaked(
                            TEMPLATE_SPAWN_EGG_MODEL,
                            List.of(
                                    new Constant(entry.primaryColor()),
                                    new Constant(entry.secondaryColor())
                            )
                    )
            );
            *///?} else {
            ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(
                    ModelUtil.idOf(entry.item()).getNamespace(),
                    "item/" + ModelUtil.pathOf(entry.item())
            );

            ModelTemplates.FLAT_ITEM.create(
                    modelId,
                    new TextureMapping()
                            .put(TextureSlot.LAYER0, ModelUtil.itemTex(entry.item())),
                    items.modelOutput
            );

            items.itemModelOutput.accept(
                    entry.item().asItem(),
                    new BlockModelWrapper.Unbaked(
                            modelId,
                            java.util.List.of()
                    )
            );
            //?}
        }
    }
    //?}
}