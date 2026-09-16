package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import space.anatomyuniverse.musavacca.data.models.newgen.NewgenBlockTintCatalog;
import space.anatomyuniverse.musavacca.data.models.newgen.NewgenItemCatalog;
import space.anatomyuniverse.musavacca.data.models.newgen.SimpleItems;
import space.anatomyuniverse.musavacca.data.models.newgen.Tints;

//? if <1.21.4 {
/*import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import space.anatomyuniverse.musavacca.data.models.NewModelSets;
import space.anatomyuniverse.musavacca.data.models.newgen.ArmorItems;
*///?}

public final class ModTints {
    private ModTints() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModTints::registerBlockColorHandlers);

        //? if <1.21.4 {
        /*modBus.addListener(ModTints::registerItemColorHandlers);
        modBus.addListener(ModTints::registerLegacyTrimProperties);
        *///?} else {
        modBus.addListener(ModTints::registerItemTintSources);
        //?}
    }

    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        if (NewgenBlockTintCatalog.bindings().isEmpty()) {
            return;
        }

        event.register(ModTints::blockTint, NewgenBlockTintCatalog.blocks());
    }

    private static int blockTint(
            BlockState state,
            BlockAndTintGetter level,
            BlockPos pos,
            int tintIndex
    ) {
        NewgenBlockTintCatalog.Binding binding = NewgenBlockTintCatalog.binding(state.getBlock());
        if (binding == null) {
            return Tints.NO_TINT;
        }

        Tints.Tint tint = binding.resolve(state, tintIndex);
        if (tint == null) {
            return Tints.NO_TINT;
        }

        int color = tint.blockColor(state, level, pos, tintIndex);
        if (color != Tints.NO_TINT) {
            if (tint instanceof MusavaccaTints.PearlFire) {
                PearlPlacementColorMemory.clear(pos);
            }
            return color;
        }

        if (tint instanceof MusavaccaTints.PearlFire pearlFire) {
            Integer rememberedColor = PearlPlacementColorMemory.get(pos);
            if (rememberedColor != null) {
                return pearlFire.blockColor(rememberedColor, tintIndex);
            }
        }

        return Tints.NO_TINT;
    }

    //? if <1.21.4 {
    /*private static void registerLegacyTrimProperties(FMLClientSetupEvent event) {
        ArmorTrimItemTintSource.registerLegacyItemProperties(event, NewModelSets.armorItems());
    }

    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        for (NewgenItemCatalog.Binding binding : NewgenItemCatalog.tintBindings()) {
            event.register(
                    (stack, tintIndex) -> SimpleItems.legacyTintColor(
                            binding.item(),
                            binding.model(),
                            stack,
                            tintIndex
                    ),
                    binding.item().asItem()
            );
        }

        for (ArmorItems.Entry entry : NewModelSets.armorItems()) {
            registerLegacyArmorTint(event, entry.helmet());
            registerLegacyArmorTint(event, entry.chestplate());
            registerLegacyArmorTint(event, entry.leggings());
            registerLegacyArmorTint(event, entry.boots());
        }
    }

    private static void registerLegacyArmorTint(RegisterColorHandlersEvent.Item event, ItemLike item) {
        if (item == null) {
            return;
        }

        event.register(
                (stack, tintIndex) -> tintIndex == 1
                        ? ArmorTrimItemTintSource.color(stack)
                        : Tints.NO_TINT,
                item.asItem()
        );
    }
    *///?} else {
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        for (Tints.ItemTintType type : NewgenItemCatalog.itemTintTypes()) {
            event.register(type.id(), type.codec());
        }
    }
    //?}
}
