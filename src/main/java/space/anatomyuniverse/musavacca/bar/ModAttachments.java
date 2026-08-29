package space.anatomyuniverse.musavacca.bar;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.bar.balance.BalanceData;
import space.anatomyuniverse.musavacca.bar.hunger.BonusHungerData;

import java.util.function.Supplier;

public final class ModAttachments {
    private ModAttachments() {
    }

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MusaCore.MOD_ID);

    public static final Supplier<AttachmentType<BonusHungerData>> BONUS_HUNGER =
            ATTACHMENT_TYPES.register("bonus_hunger", () ->
                    AttachmentType.serializable(BonusHungerData::new)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<BalanceData>> BALANCE =
            ATTACHMENT_TYPES.register("balance", () ->
                    AttachmentType.serializable(BalanceData::new)
                            .copyOnDeath()
                            .build()
            );

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }
}
