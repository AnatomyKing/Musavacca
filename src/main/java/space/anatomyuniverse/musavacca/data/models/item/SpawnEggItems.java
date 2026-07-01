package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineIds;
import space.anatomyuniverse.musavacca.data.models.engine.item.EngineItemEntry;
import space.anatomyuniverse.musavacca.data.models.engine.item.EngineItemModels;
import space.anatomyuniverse.musavacca.data.models.unified.ItemTint;

//? if <=1.21.3 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
*///?}

//? if >=1.21.4 {
import net.minecraft.client.data.models.ItemModelGenerators;
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

    public record Entry(ItemLike item, int primaryColor, int secondaryColor, ItemTint itemTint) {
        public Entry {
            primaryColor &= 0xFFFFFF;
            secondaryColor &= 0xFFFFFF;
        }

        public Entry itemTint(ItemTint itemTint) {
            return new Entry(item, primaryColor, secondaryColor, itemTint);
        }
    }

    public static Entry of(ItemLike item, int primaryColor, int secondaryColor) {
        return new Entry(item, primaryColor, secondaryColor, null);
    }

    public static Entry of(ItemLike item) {
        return new Entry(item, DEFAULT_PRIMARY_COLOR, DEFAULT_SECONDARY_COLOR, null);
    }

    //? if <=1.21.3 {
    /*public static void generate(ItemModelProvider itemModels, Entry... entries) {
        if (entries == null) return;
        for (Entry entry : entries) {
            if (entry == null || entry.item() == null) continue;
            itemModels.withExistingParent(EngineIds.itemId(entry.item()).getPath(), itemModels.mcLoc("item/template_spawn_egg"));
        }
    }
    *///?}

    //? if >=1.21.4 {
    private static final ResourceLocation TEMPLATE_SPAWN_EGG_MODEL = ResourceLocation.fromNamespaceAndPath("minecraft", "item/template_spawn_egg");

    public static void generate(ItemModelGenerators items, Entry... entries) {
        if (entries == null) return;

        for (Entry entry : entries) {
            if (entry == null || entry.item() == null) continue;

            //? if =1.21.4 {
            /*items.itemModelOutput.accept(entry.item().asItem(), new BlockModelWrapper.Unbaked(TEMPLATE_SPAWN_EGG_MODEL, List.of(new Constant(entry.primaryColor()), new Constant(entry.secondaryColor()))));
            *///?} else {
            if (entry.itemTint() != null) {
                EngineItemModels.generate(items, EngineItemEntry.flat(entry.item()).itemTint(entry.itemTint()));
            } else {
                ResourceLocation modelId = EngineIds.itemModel(entry.item());
                items.itemModelOutput.accept(entry.item().asItem(), new BlockModelWrapper.Unbaked(modelId, java.util.List.of()));
            }
            //?}
        }
    }
    //?}
}
