// file: src/main/java/space/anatomyuniverse/musavacca/component/ModDataComponents.java
package space.anatomyuniverse.musavacca.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.vococaller.VocoCallerPhonebook;

import java.util.function.Supplier;

public final class ModDataComponents {
    private ModDataComponents() {}

    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(
                    Registries.DATA_COMPONENT_TYPE,
                    MusaCore.MOD_ID
            );

    public static final Supplier<DataComponentType<Integer>> HEX_COLOR =
            DATA_COMPONENT_TYPES.registerComponentType(
                    "hex_color",
                    builder -> builder
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
            );

    /*
     * Recent + Saved belong to the physical SIM.
     *
     * CODEC        -> persistent ItemStack/world saving
     * STREAM_CODEC -> compact, real ItemStack synchronization
     *
     * Do NOT use StreamCodec.unit(...) here: a unit codec only accepts the
     * exact unit value and will reject a non-empty phonebook during slot sync.
     */
    public static final Supplier<DataComponentType<VocoCallerPhonebook>>
            VOCO_CALLER_PHONEBOOK =
            DATA_COMPONENT_TYPES.registerComponentType(
                    "voco_caller_phonebook",
                    builder -> builder
                            .persistent(VocoCallerPhonebook.CODEC)
                            .networkSynchronized(
                                    VocoCallerPhonebook.STREAM_CODEC
                            )
            );

    public static void register(IEventBus modBus) {
        DATA_COMPONENT_TYPES.register(modBus);
    }
}
