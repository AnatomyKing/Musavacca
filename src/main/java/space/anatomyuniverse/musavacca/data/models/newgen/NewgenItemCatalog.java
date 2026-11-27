package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockState;
import space.anatomyuniverse.musavacca.data.models.NewModelSets;
import space.anatomyuniverse.musavacca.data.models.newgen.BlockFamily.FamilyItemMode;
import space.anatomyuniverse.musavacca.data.models.newgen.BlockFamily.ModelMode;

import java.util.ArrayList;
import java.util.List;

public final class NewgenItemCatalog {
    private NewgenItemCatalog() {}

    public record Binding(ItemLike item, List<Tints.Tint> tints) {
        public int color(ItemStack stack, int index) {
            return index >= 0 && index < tints.size() ? tints.get(index).itemColor(stack) : Tints.NO_TINT;
        }
    }

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
                addIfTinted(result, entry.block().asItem(), SimpleItems.Model.existing("minecraft:item/generated").tint(entry.tint()));
            }
        }
        for (DecorationBlocks.Entry entry : NewModelSets.decorationBlocks()) {
            if (entry.itemMode() == DecorationBlocks.ItemMode.CUSTOM) {
                addIfTinted(result, entry.block().asItem(), entry.itemModel());
            } else if (entry.itemMode() == DecorationBlocks.ItemMode.PLACEMENT) {
                DecorationBlocks.Model rule = entry.itemRule();
                Tints.Tint tint = Tints.effective(entry.tint(), rule.tint());
                if (tint.tinted()) {
                    addIfTinted(result, entry.block().asItem(), SimpleItems.Model.existing("minecraft:item/generated").tint(tint));
                }
            }
        }
        return List.copyOf(result);
    }

    private static void addModelFamilyItems(List<Binding> result, List<? extends BlockFamily.ModelFamilyEntry<?, ?>> entries) {
        for (BlockFamily.ModelFamilyEntry<?, ?> entry : entries) {
            if (entry.familyItemMode() == FamilyItemMode.NONE) continue;
            if (entry.familyItemMode() == FamilyItemMode.CUSTOM) {
                addIfTinted(result, entry.block().asItem(), entry.item());
                continue;
            }
            List<? extends BlockFamily.ModelRule<?, ?>> rules = entry.mode() == ModelMode.MODELS ? entry.models() : entry.parts();
            BlockFamily.ModelRule<?, ?> first =
                    entry.mode() == ModelMode.MODELS
                            ? rules.get(0)
                            : rules.stream().filter(rule -> rule.conditions().isAlways()).findFirst().orElse(null);
            if (first == null) continue;
            Tints.Tint tint = Tints.effective(entry.tint(), first.tint());
            if (tint.tinted()) {
                addIfTinted(result, entry.block().asItem(), SimpleItems.Model.existing("minecraft:item/generated").tint(tint));
            }
        }
    }

    private static void addExplicitStateFamilyItems(List<Binding> result, List<? extends BlockFamily.StateFamilyEntry<?>> entries) {
        for (BlockFamily.StateFamilyEntry<?> entry : entries) {
            if (entry.familyItemMode() != FamilyItemMode.CUSTOM) continue;
            addIfTinted(result, entry.block().asItem(), entry.item());
        }
    }

    private static List<Tints.Tint> itemTints(ItemLike item, SimpleItems.Model model) {
        if (model.isExisting()) return List.of(model.tint());
        if (model.isGenerated()) return model.textureTints(model.textures(ModelLocations.itemId(item)).size());
        List<Tints.Tint> tints = new ArrayList<>();
        if (model.isMultipart()) {
            for (SimpleBlocks.Part part : model.parts()) {
                tints.add(Tints.effective(Tints.none(), part.tint()));
            }
        } else if (model.isBlockState() && item.asItem() instanceof BlockItem blockItem) {
            BlockState state = SimpleItems.blockState(item);
            var entry = blockDefinition(blockItem);
            if (entry != null) {
                List<? extends BlockFamily.ModelRule<?, ?>> rules = entry.mode() == ModelMode.MODELS ? entry.models() : entry.parts();
                for (var part : rules) {
                    if (part.conditions().matches(state)) tints.add(Tints.effective(entry.tint(), part.tint()));
                }
            }
        }
        var tinted = tints.stream().filter(Tints.Tint::tinted).distinct().toList();
        if (tinted.size() > 1) throw new IllegalStateException("Legacy composite item has incompatible tint palettes: " + item);
        return tinted;
    }

    private static BlockFamily.ModelFamilyEntry<?, ?> blockDefinition(BlockItem item) {
        for (List<? extends BlockFamily.ModelFamilyEntry<?, ?>> entries : List.<List<? extends BlockFamily.ModelFamilyEntry<?, ?>>>of(
                NewModelSets.simpleBlocks(), NewModelSets.crossBlocks(), NewModelSets.tallCrossBlocks(), NewModelSets.portalBlocks())) {
            for (var entry : entries) if (entry.block() == item.getBlock()) return entry;
        }
        return null;
    }

    private static void addIfTinted(List<Binding> result, ItemLike item, SimpleItems.Model model) {
        if (item == null || model == null) return;
        var tints = itemTints(item, model);
        if (tints.stream().noneMatch(Tints.Tint::tinted)) return;
        for (Binding existing : result) {
            if (existing.item().asItem() == item.asItem()) {
                throw new IllegalStateException(
                        "NewGen declares legacy item tint handling more than once for " + ModelLocations.itemId(item));
            }
        }
        result.add(new Binding(item, tints));
    }
}
