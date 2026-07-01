package space.anatomyuniverse.musavacca.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;

import java.util.Map;
import java.util.function.Supplier;

public final class ModDataComponents {
    private ModDataComponents() {}

    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MusaCore.MOD_ID);

    /**
     * Musavacca source-of-truth color component.
     *
     * Keep this custom because blocks/items can have named color slots.
     * Rendering is now mirrored into vanilla components by HexColorComponent:
     * - minecraft:dyed_color
     * - minecraft:custom_model_data.colors
     */
    public static final Supplier<DataComponentType<Map<String, Integer>>> HEX_COLOR =
            DATA_COMPONENT_TYPES.registerComponentType(
                    "hex_color",
                    builder -> builder
                            .persistent(HexColorComponent.CODEC)
                            .networkSynchronized(ByteBufCodecs.fromCodec(HexColorComponent.CODEC))
            );

    public static void register(IEventBus modBus) {
        DATA_COMPONENT_TYPES.register(modBus);
    }
}
