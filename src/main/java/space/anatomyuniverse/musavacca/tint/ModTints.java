package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import space.anatomyuniverse.musavacca.data.models.newgen.NewgenBlockTintCatalog;
import space.anatomyuniverse.musavacca.data.models.newgen.Tints;
//? if >=26.1 {
/*import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.Set;
*///?} else {
import net.minecraft.world.level.BlockAndTintGetter;
//?}
//? if <1.21.4 {
/*import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import space.anatomyuniverse.musavacca.data.models.NewModelSets;
import space.anatomyuniverse.musavacca.data.models.newgen.ArmorItems;
import space.anatomyuniverse.musavacca.data.models.newgen.NewgenItemCatalog;
*///?}

public final class ModTints {
    private ModTints() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(ModTints::registerBlockColorHandlers);
        //? if >=1.21.4 {
        modBus.addListener(ModTints::registerItemTintSources);
        //?} else {
        /*modBus.addListener(ModTints::registerItemColorHandlers);
        modBus.addListener(ModTints::registerLegacyTrimProperties);
        *///?}
    }

    //? if >=26.1 {
    /*public static void registerBlockColorHandlers(RegisterColorHandlersEvent.BlockTintSources event) {
        for (var binding : NewgenBlockTintCatalog.bindings()) {
            int maximum = binding.rules().stream().mapToInt(rule -> rule.tint().tintIndex()).max().orElse(-1);
            var sources = new ArrayList<BlockTintSource>();
            var properties = Set.copyOf(binding.block().getStateDefinition().getProperties());
            for (int i = 0; i <= maximum; i++) {
                int index = i;
                sources.add(new BlockTintSource() {
                    @Override
                    public int color(BlockState state) {
                        return blockTint(binding, state, null, null, index);
                    }

                    @Override
                    public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                        return blockTint(binding, state, level, pos, index);
                    }

                    @Override
                    public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                        return blockTint(binding, state, level, pos, index);
                    }

                    @Override
                    public Set<Property<?>> relevantProperties() {
                        return properties;
                    }
                });
            }
            event.register(sources, binding.block());
        }
    }
    *///?} else {
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        for (var binding : NewgenBlockTintCatalog.bindings()) {
            event.register((state, level, pos, index) -> blockTint(binding, state, level, pos, index), binding.block());
        }
    }
    //?}

    private static int blockTint(NewgenBlockTintCatalog.Binding binding, BlockState state, BlockAndTintGetter level, BlockPos pos, int index) {
        Tints.Tint tint = binding.resolve(state, index);
        return tint == null ? Tints.NO_TINT : tint.blockColor(state, level, pos, index);
    }

    //? if >=1.21.4 {
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(ArmorTrimItemTintSource.TINT_ID, ArmorTrimItemTintSource.MAP_CODEC);
        event.register(Tints.Hex.TINT_ID, Tints.Hex.MAP_CODEC);
    }
    //?} else {
    /*private static void registerLegacyTrimProperties(FMLClientSetupEvent event) {
        ArmorTrimItemTintSource.registerLegacyItemProperties(event, NewModelSets.armorItems());
    }

    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        for (var binding : NewgenItemCatalog.tintBindings()) {
            event.register(binding::color, binding.item().asItem());
        }
        for (ArmorItems.Entry entry : NewModelSets.armorItems()) {
            if (!entry.inventory().trims()) continue;
            for (ItemLike item : entry.items().all()) {
                event.register((stack, index) -> index == 1 ? ArmorTrimItemTintSource.color(stack) : Tints.NO_TINT, item.asItem());
            }
        }
    }
    *///?}
}
