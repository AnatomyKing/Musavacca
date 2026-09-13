package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import space.anatomyuniverse.musavacca.data.models.ModelUtil;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import space.anatomyuniverse.musavacca.tint.HexColorItemTintSource;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;
//?}

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TallCrossBlockGenerator {
    private TallCrossBlockGenerator() {}

    public static void generate(
            //? if <1.21.4 {
            /*BlockStateProvider blocks,
            ItemModelProvider items,
            *///?} else {
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            //?}
            Collection<TallCrossBlocks.Entry> entries
    ) {
        if (entries == null) {
            return;
        }

        for (TallCrossBlocks.Entry entry : entries) {
            if (entry == null) {
                continue;
            }

            entry.validate();

            CrossBlockGenerator.generate(
                    blocks,
                    items,
                    List.of(translate(entry))
            );

            if (!entry.noItem()) {
                //? if <1.21.4 {
                /*generateLegacyItem(blocks, items, entry);
                *///?} else {
                generateItem(blocks, items, entry);
                //?}
            }
        }
    }

    private static CrossBlocks.Entry translate(TallCrossBlocks.Entry entry) {
        CrossBlocks.Entry.Builder builder = CrossBlocks.Entry.builder(entry.block());

        if (entry.mode() == TallCrossBlocks.Mode.MODELS) {
            List<CrossBlocks.Model> models = new ArrayList<>();

            for (TallCrossBlocks.Model model : entry.models()) {
                models.add(toModel(model, model.source().upper(), DoubleBlockHalf.UPPER));
                models.add(toModel(model, model.source().lower(), DoubleBlockHalf.LOWER));
            }

            builder.models(models.toArray(CrossBlocks.Model[]::new));
        } else {
            List<CrossBlocks.Part> parts = new ArrayList<>();

            for (TallCrossBlocks.Part part : entry.parts()) {
                parts.add(toPart(part, part.source().upper(), DoubleBlockHalf.UPPER));
                parts.add(toPart(part, part.source().lower(), DoubleBlockHalf.LOWER));
            }

            builder.multipart(parts.toArray(CrossBlocks.Part[]::new));
        }

        return builder
                .rotations(entry.rotations())
                .tint(entry.tint())
                .noItem()
                .build();
    }

    private static CrossBlocks.Model toModel(
            TallCrossBlocks.Model source,
            Models.Source model,
            DoubleBlockHalf half
    ) {
        CrossBlocks.Model result = CrossBlocks.Model.when(
                model,
                source.conditions().and(DoublePlantBlock.HALF, half)
        );

        if (source.rotationX() != 0) {
            result.rotateX(source.rotationX());
        }

        if (source.rotationY() != 0) {
            result.rotateY(source.rotationY());
        }

        if (source.tint() != null) {
            result.tint(source.tint());
        }

        return result.variants(source.variants());
    }

    private static CrossBlocks.Part toPart(
            TallCrossBlocks.Part source,
            Models.Source model,
            DoubleBlockHalf half
    ) {
        CrossBlocks.Part result = CrossBlocks.Part.when(
                model,
                source.conditions().and(DoublePlantBlock.HALF, half)
        );

        if (source.rotationX() != 0) {
            result.rotateX(source.rotationX());
        }

        if (source.rotationY() != 0) {
            result.rotateY(source.rotationY());
        }

        if (source.tint() != null) {
            result.tint(source.tint());
        }

        return result.variants(source.variants());
    }

    //? if <1.21.4 {
    /*private static void generateLegacyItem(
            BlockStateProvider blocks,
            ItemModelProvider items,
            TallCrossBlocks.Entry entry
    ) {
        if (entry.itemModel() != null) {
            ModelFile model = blocks.models().getExistingFile(
                    ResourceLocation.parse(entry.itemModel())
            );

            blocks.simpleBlockItem(entry.block(), model);
            return;
        }

        Models.Source source = defaultItemSource(entry);

        if (source instanceof CrossModels.Generated generated) {
            items.singleTexture(
                    ModelUtil.pathOf(entry.block()),
                    ResourceLocation.withDefaultNamespace("item/generated"),
                    "layer0",
                    generated.textures().texture()
            );
            return;
        }

        if (source instanceof Models.Existing existing) {
            blocks.simpleBlockItem(
                    entry.block(),
                    blocks.models().getExistingFile(existing.model())
            );
            return;
        }

        throw unsupportedItemSource(source);
    }
    *///?} else {
    private static void generateItem(
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            TallCrossBlocks.Entry entry
    ) {
        ResourceLocation model;

        if (entry.itemModel() != null) {
            model = ResourceLocation.parse(entry.itemModel());
        } else {
            Models.Source source = defaultItemSource(entry);

            if (source instanceof CrossModels.Generated generated) {
                model = ModelLocationUtils.getModelLocation(entry.block().asItem());

                ModelTemplates.FLAT_ITEM.create(
                        model,
                        TextureMapping.layer0(generated.textures().texture()),
                        blocks.modelOutput
                );
            } else if (source instanceof Models.Existing existing) {
                model = existing.model();
            } else {
                throw unsupportedItemSource(source);
            }
        }

        Tints.Tint tint = entry.itemTint();

        if (!tint.tinted()) {
            blocks.registerSimpleItemModel(entry.block(), model);
            return;
        }

        ItemTintSource source = switch (tint.kind()) {
            case NONE ->
                    throw new IllegalStateException("NONE reached tinted item generation");

            case CONSTANT ->
                    new Constant(TintColorUtil.rgb(((Tints.Constant) tint).rgb()));

            case BIOME_FOLIAGE ->
                    new Constant(TintColorUtil.defaultFoliageItemTint());

            case HEX_COLOR,
                 PEARL_FIRE ->
                    HexColorItemTintSource.INSTANCE;
        };

        items.itemModelOutput.accept(
                entry.block().asItem(),
                new BlockModelWrapper.Unbaked(model, List.of(source))
        );
    }
    //?}

    private static Models.Source defaultItemSource(TallCrossBlocks.Entry entry) {
        if (entry.mode() == TallCrossBlocks.Mode.MODELS) {
            return entry.models().get(0).source().upper();
        }

        return defaultMultipartItemPart(entry).source().upper();
    }

    private static TallCrossBlocks.Part defaultMultipartItemPart(TallCrossBlocks.Entry entry) {
        for (TallCrossBlocks.Part part : entry.parts()) {
            if (part.conditions().isAlways()) {
                return part;
            }
        }

        throw new IllegalStateException(
                "Multipart tall cross block "
                        + ModelUtil.idOf(entry.block())
                        + " has no unconditional base part for its default item model. "
                        + "Add Part.always(...) or explicitly use .item(...)."
        );
    }

    private static IllegalStateException unsupportedItemSource(Models.Source source) {
        return new IllegalStateException(
                "TallCrossBlockGenerator cannot infer an item model from "
                        + source
                        + ". Use .item(...) or .noItem()."
        );
    }
}
