package space.anatomyuniverse.musavacca.data.models.item;

import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.engine.item.EngineItemEntry;
import space.anatomyuniverse.musavacca.data.models.engine.item.EngineItemModels;
import space.anatomyuniverse.musavacca.data.models.unified.BiomeTint;
import space.anatomyuniverse.musavacca.data.models.unified.HexColorTint;
import space.anatomyuniverse.musavacca.data.models.unified.ItemTint;
import space.anatomyuniverse.musavacca.data.models.unified.PearlTint;

import java.util.Arrays;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
 *///?} else {
import net.minecraft.client.data.models.ItemModelGenerators;
//?}

public final class SimpleItems {
    private SimpleItems() {}

    public record Entry(EngineItemEntry data) {
        public static Entry flat(ItemLike item) {
            return new Entry(EngineItemEntry.flat(item));
        }

        public static Entry handheld(ItemLike item) {
            return new Entry(EngineItemEntry.handheld(item));
        }

        public static Entry model(ItemLike item, String modelId) {
            return new Entry(EngineItemEntry.model(item, modelId));
        }

        public Entry textures(String baseTextureStem, String tintedTextureStem) {
            return new Entry(data.textures(baseTextureStem, tintedTextureStem));
        }

        public Entry itemTint(ItemTint itemTint) {
            return new Entry(data.itemTint(itemTint));
        }

        public Entry biomeTint(BiomeTint biomeTint) {
            return new Entry(data.itemTint(biomeTint));
        }

        public Entry hexColorTint(HexColorTint hexColorTint) {
            return new Entry(data.itemTint(hexColorTint));
        }

        public Entry pearlTint(PearlTint pearlTint) {
            return new Entry(data.pearlTint(pearlTint));
        }
    }

    public static void generate(
            //? if <1.21.4 {
            /*ItemModelProvider items,
             *///?} else {
            ItemModelGenerators items,
            //?}
            Entry... entries
    ) {
        if (entries == null) return;

        EngineItemModels.generate(
                items,
                Arrays.stream(entries)
                        .filter(entry -> entry != null)
                        .map(Entry::data)
                        .toArray(EngineItemEntry[]::new)
        );
    }

    public static void generateFlat(
            //? if <1.21.4 {
            /*ItemModelProvider items,
             *///?} else {
            ItemModelGenerators items,
            //?}
            ItemLike... flatItems
    ) {
        if (flatItems == null) return;

        generate(
                items,
                Arrays.stream(flatItems)
                        .filter(item -> item != null)
                        .map(Entry::flat)
                        .toArray(Entry[]::new)
        );
    }

    public static void generateHandheld(
            //? if <1.21.4 {
            /*ItemModelProvider items,
             *///?} else {
            ItemModelGenerators items,
            //?}
            ItemLike... handheldItems
    ) {
        if (handheldItems == null) return;

        generate(
                items,
                Arrays.stream(handheldItems)
                        .filter(item -> item != null)
                        .map(Entry::handheld)
                        .toArray(Entry[]::new)
        );
    }
}
