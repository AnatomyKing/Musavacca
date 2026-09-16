package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.NewModelSets;
import space.anatomyuniverse.musavacca.tint.ArmorTrimItemTintSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class NewgenItemCatalog {
    private NewgenItemCatalog() {}

    public record Binding(ItemLike item, SimpleItems.Model model) {}

    public static List<Binding> tintBindings() {
        List<Binding> result = new ArrayList<>();

        for (SimpleItems.Entry entry : NewModelSets.simpleItems()) {
            addIfTinted(result, entry.item(), entry.model());
        }

        for (DoorBlocks.Entry door : NewModelSets.doorBlocks()) {
            for (SimpleItems.Entry entry : door.items()) {
                addIfTinted(result, entry.item(), entry.model());
            }
        }

        addModelFamilyItems(result, NewModelSets.simpleBlocks());
        addModelFamilyItems(result, NewModelSets.crossBlocks());
        addModelFamilyItems(result, NewModelSets.tallCrossBlocks());
        addModelFamilyItems(result, NewModelSets.portalBlocks());

        addExplicitStateFamilyItems(result, NewModelSets.trapdoorBlocks());
        addExplicitStateFamilyItems(result, NewModelSets.stairBlocks());
        addExplicitStateFamilyItems(result, NewModelSets.slabBlocks());
        addExplicitStateFamilyItems(result, NewModelSets.fenceBlocks());
        addExplicitStateFamilyItems(result, NewModelSets.fenceGateBlocks());
        addExplicitStateFamilyItems(result, NewModelSets.pressurePlateBlocks());
        addExplicitStateFamilyItems(result, NewModelSets.buttonBlocks());
        addExplicitStateFamilyItems(result, NewModelSets.wallBlocks());

        for (FireBlocks.Entry entry : NewModelSets.fireBlocks()) {
            if (entry.itemMode() == FamilyItemMode.CUSTOM) {
                addIfTinted(result, entry.block().asItem(), entry.item());
            } else if (entry.itemMode() == FamilyItemMode.DEFAULT && entry.tint().tinted()) {
                addIfTinted(
                        result,
                        entry.block().asItem(),
                        SimpleItems.Model.existing("minecraft:item/generated").tint(entry.tint())
                );
            }
        }

        for (DecorationBlocks.Entry entry : NewModelSets.decorationBlocks()) {
            if (entry.itemMode() == DecorationBlocks.ItemMode.CUSTOM) {
                addIfTinted(result, entry.block().asItem(), entry.itemModel());
            } else if (entry.itemMode() == DecorationBlocks.ItemMode.PLACEMENT) {
                DecorationBlocks.Model rule = entry.itemRule();
                Tints.Tint tint = Tints.effective(entry.tint(), rule.tint());
                if (tint.tinted()) {
                    addIfTinted(
                            result,
                            entry.block().asItem(),
                            SimpleItems.Model.existing("minecraft:item/generated").tint(tint)
                    );
                }
            }
        }

        return List.copyOf(result);
    }

    //? if >=1.21.4 {
    public static List<Tints.ItemTintType> itemTintTypes() {
        Map<net.minecraft.resources.ResourceLocation, Tints.ItemTintType> types = new LinkedHashMap<>();

        for (Binding binding : tintBindings()) {
            List<Tints.Tint> tints = binding.model().isExisting()
                    ? binding.model().existingTints()
                    : binding.model().physicalLayers(binding.item()).stream()
                    .map(SimpleItems.PhysicalLayer::tint)
                    .toList();

            for (Tints.Tint tint : tints) {
                Tints.ItemTintType type = tint.itemTintType();
                if (type == null) {
                    continue;
                }

                Tints.ItemTintType previous = types.putIfAbsent(type.id(), type);
                if (previous != null && previous.codec() != type.codec()) {
                    throw new IllegalStateException("Different item tint codecs use " + type.id());
                }
            }
        }

        boolean usesArmorTrimTint = NewModelSets.armorItems().stream()
                .anyMatch(entry -> entry.inventory().trims());

        if (usesArmorTrimTint) {
            Tints.ItemTintType type = ArmorTrimItemTintSource.itemTintType();
            types.putIfAbsent(type.id(), type);
        }

        return List.copyOf(types.values());
    }
    //?}

    private static void addModelFamilyItems(
            List<Binding> result,
            List<? extends BlockFamily.ModelFamilyEntry<?, ?>> entries
    ) {
        for (BlockFamily.ModelFamilyEntry<?, ?> entry : entries) {
            if (entry.familyItemMode() == FamilyItemMode.NONE) continue;

            if (entry.familyItemMode() == FamilyItemMode.CUSTOM) {
                addIfTinted(result, entry.block().asItem(), entry.item());
                continue;
            }

            List<? extends BlockFamily.ModelRule<?, ?>> rules = entry.mode() == ModelMode.MODELS
                    ? entry.models()
                    : entry.parts();

            BlockFamily.ModelRule<?, ?> first = entry.mode() == ModelMode.MODELS
                    ? rules.get(0)
                    : rules.stream()
                    .filter(rule -> rule.conditions().isAlways())
                    .findFirst()
                    .orElse(null);

            if (first == null) continue;

            Tints.Tint tint = Tints.effective(entry.tint(), first.tint());
            if (tint.tinted()) {
                addIfTinted(
                        result,
                        entry.block().asItem(),
                        SimpleItems.Model.existing("minecraft:item/generated").tint(tint)
                );
            }
        }
    }

    private static void addExplicitStateFamilyItems(
            List<Binding> result,
            List<? extends BlockFamily.StateFamilyEntry<?>> entries
    ) {
        for (BlockFamily.StateFamilyEntry<?> entry : entries) {
            if (entry.familyItemMode() != FamilyItemMode.CUSTOM) continue;
            addIfTinted(result, entry.block().asItem(), entry.item());
        }
    }

    private static void addIfTinted(
            List<Binding> result,
            ItemLike item,
            SimpleItems.Model model
    ) {
        if (item == null || model == null || !model.hasAnyTint(item)) return;

        for (Binding existing : result) {
            if (existing.item().asItem() == item.asItem()) {
                throw new IllegalStateException(
                        "NewGen declares legacy item tint handling more than once for "
                                + ModelLocations.itemId(item)
                );
            }
        }

        result.add(new Binding(item, model));
    }
}

