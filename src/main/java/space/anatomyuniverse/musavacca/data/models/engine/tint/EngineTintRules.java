package space.anatomyuniverse.musavacca.data.models.engine.tint;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import space.anatomyuniverse.musavacca.data.models.ModelSets;
import space.anatomyuniverse.musavacca.data.models.block.AgeBlocks;
import space.anatomyuniverse.musavacca.data.models.block.ColumnBlocks;
import space.anatomyuniverse.musavacca.data.models.block.CrossBlocks;
import space.anatomyuniverse.musavacca.data.models.block.FireBlocks;
import space.anatomyuniverse.musavacca.data.models.block.PortalBlocks;
import space.anatomyuniverse.musavacca.data.models.block.SimpleBlocks;
import space.anatomyuniverse.musavacca.data.models.engine.item.EngineItemEntry;
import space.anatomyuniverse.musavacca.data.models.item.ArmorItems;
import space.anatomyuniverse.musavacca.data.models.item.SimpleItems;
import space.anatomyuniverse.musavacca.data.models.unified.BlockTintRule;
import space.anatomyuniverse.musavacca.data.models.unified.BlockTintRules;
import space.anatomyuniverse.musavacca.data.models.unified.PearlTint;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Runtime tint-rule collector for the model engine.
 *
 * ModelSets is the only place that decides which block/item uses which tint.
 * The tint package does not get edited when a new item/block/profile is added.
 */
public final class EngineTintRules {
    private static VanillaItemTintRule[] vanillaItemTintRules;

    private EngineTintRules() {}

    public static BlockTintRule[] blockTintRules() {
        List<BlockTintRule> rules = new ArrayList<>();

        collectSimple(rules, ModelSets.simpleBlocks());
        collectCross(rules, ModelSets.crossBlocks());
        collectAge(rules, ModelSets.ageBlocks());
        collectColumn(rules, ModelSets.columnBlocks());
        collectFire(rules, ModelSets.fireBlocks());
        collectPortal(rules, ModelSets.portalBlocks());

        return rules.toArray(BlockTintRule[]::new);
    }

    public static List<Integer> customModelDataColors(ItemStack stack, Map<String, Integer> namedColors) {
        if (stack == null || stack.isEmpty()) {
            return List.of();
        }

        for (VanillaItemTintRule rule : vanillaItemTintRules()) {
            if (rule.matches(stack)) {
                return rule.customModelDataColors(stack, namedColors);
            }
        }

        return List.of();
    }

    public static int legacyItemTint(ItemStack stack, int tintIndex) {
        if (stack == null || stack.isEmpty()) {
            return TintColorUtil.NO_TINT;
        }

        for (VanillaItemTintRule rule : vanillaItemTintRules()) {
            int color = rule.legacyItemTint(stack, tintIndex);
            if (color != TintColorUtil.NO_TINT) {
                return color;
            }
        }

        return TintColorUtil.NO_TINT;
    }

    public static Item[] legacyItemTintItems() {
        List<Item> result = new ArrayList<>();

        for (VanillaItemTintRule rule : vanillaItemTintRules()) {
            if (rule.enabled() && !result.contains(rule.item())) {
                result.add(rule.item());
            }
        }

        return result.toArray(Item[]::new);
    }

    private static VanillaItemTintRule[] vanillaItemTintRules() {
        if (vanillaItemTintRules == null) {
            List<VanillaItemTintRule> rules = new ArrayList<>();
            collectSimpleItems(rules, ModelSets.simpleItems());
            collectArmorItems(rules, ModelSets.armorItems());
            vanillaItemTintRules = rules.toArray(VanillaItemTintRule[]::new);
        }

        return vanillaItemTintRules.clone();
    }

    private static void collectSimpleItems(List<VanillaItemTintRule> rules, SimpleItems.Entry[] entries) {
        if (entries == null) return;

        for (SimpleItems.Entry entry : entries) {
            if (entry == null) continue;
            collectItemEntry(rules, entry.data());
        }
    }

    private static void collectArmorItems(List<VanillaItemTintRule> rules, ArmorItems.Entry[] entries) {
        if (entries == null) return;

        for (ArmorItems.Entry entry : entries) {
            if (entry == null || !(entry.itemTint() instanceof PearlTint pearlTint)) continue;

            addItemRule(rules, entry.helmet(), pearlTint);
            addItemRule(rules, entry.chestplate(), pearlTint);
            addItemRule(rules, entry.leggings(), pearlTint);
            addItemRule(rules, entry.boots(), pearlTint);
        }
    }

    private static void collectItemEntry(List<VanillaItemTintRule> rules, EngineItemEntry entry) {
        if (entry == null || !(entry.itemTint() instanceof PearlTint pearlTint)) {
            return;
        }

        addItemRule(rules, entry.item(), pearlTint);
    }

    private static void addItemRule(List<VanillaItemTintRule> rules, ItemLike itemLike, PearlTint pearlTint) {
        if (rules == null || itemLike == null || pearlTint == null || !pearlTint.hasItemSources()) {
            return;
        }

        Item item = itemLike.asItem();
        for (VanillaItemTintRule rule : rules) {
            if (rule.item() == item) {
                return;
            }
        }

        rules.add(new VanillaItemTintRule(item, pearlTint));
    }

    private static void collectSimple(List<BlockTintRule> rules, SimpleBlocks.Entry[] entries) {
        if (entries == null) return;
        for (SimpleBlocks.Entry entry : entries) {
            if (entry != null) BlockTintRules.addAll(rules, entry.data().blockTintRules());
        }
    }

    private static void collectCross(List<BlockTintRule> rules, CrossBlocks.Entry[] entries) {
        if (entries == null) return;
        for (CrossBlocks.Entry entry : entries) {
            if (entry != null) BlockTintRules.addAll(rules, entry.data().blockTintRules());
        }
    }

    private static void collectAge(List<BlockTintRule> rules, AgeBlocks.Entry[] entries) {
        if (entries == null) return;
        for (AgeBlocks.Entry entry : entries) {
            if (entry != null) BlockTintRules.addAll(rules, entry.data().blockTintRules());
        }
    }

    private static void collectColumn(List<BlockTintRule> rules, ColumnBlocks.Entry[] entries) {
        if (entries == null) return;
        for (ColumnBlocks.Entry entry : entries) {
            if (entry != null) BlockTintRules.addAll(rules, entry.data().blockTintRules());
        }
    }

    private static void collectFire(List<BlockTintRule> rules, FireBlocks.Entry[] entries) {
        if (entries == null) return;
        for (FireBlocks.Entry entry : entries) {
            if (entry != null) BlockTintRules.addAll(rules, entry.blockTintRules());
        }
    }

    private static void collectPortal(List<BlockTintRule> rules, PortalBlocks.Entry[] entries) {
        if (entries == null) return;
        for (PortalBlocks.Entry entry : entries) {
            if (entry != null) BlockTintRules.addAll(rules, entry.blockTintRules());
        }
    }
}
