package space.anatomyuniverse.musavacca.item.custom.potassium;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.entity.projectile.PotassiumProjectile;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class PotassiumThrow {
    public static final int COOLDOWN = 80;
    public static final int RECOVERY_TIME = 300;
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MusaCore.MOD_ID);
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MusaCore.MOD_ID);
    public static final DeferredHolder<EntityType<?>, EntityType<PotassiumProjectile>> PROJECTILE = ENTITIES.register("potassium_sword_projectile",
            () -> EntityType.Builder.<PotassiumProjectile>of(PotassiumProjectile::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(8).updateInterval(1).fireImmune().noSave().noSummon()
                    //? if >=1.21.11 {
                    /*.build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "potassium_sword_projectile"))));
                    *///?} else if >=1.21.2 {
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "potassium_sword_projectile"))));
                    //?} else {
                    /*.build(MusaCore.MOD_ID + ":potassium_sword_projectile"));
                    *///?}
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<StoredTool>> STORED_TOOL = COMPONENTS.register("stored_potassium_sword",
            () -> DataComponentType.<StoredTool>builder().persistent(StoredTool.CODEC).networkSynchronized(StoredTool.STREAM_CODEC).build());

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        COMPONENTS.register(bus);
        NeoForge.EVENT_BUS.addListener(PotassiumThrow::onPlayerTick);
        PotassiumCombat.register();
    }

    public static boolean throwTool(Player player, InteractionHand hand, ItemStack stack) {
        if (!(player instanceof ServerPlayer serverPlayer) || !(player.level() instanceof ServerLevel level)) return true;
        int slot = hand == InteractionHand.OFF_HAND ? Inventory.SLOT_OFFHAND : selectedSlot(player);
        long deadline = level.getGameTime() + RECOVERY_TIME;
        PotassiumProjectile projectile = new PotassiumProjectile(PROJECTILE.get(), level);
        projectile.launch(serverPlayer, stack.copy(), slot, deadline);
        if (!level.addFreshEntity(projectile)) return false;
        ItemStack placeholder = new ItemStack(ModItems.POTASSIUM_THROWABLE_TOOL.get());
        placeholder.set(DataComponents.ITEM_NAME, Component.literal("Thrown ").append(stack.getHoverName()));
        placeholder.set(STORED_TOOL.get(), new StoredTool(stack.copy(), deadline));
        player.getInventory().setItem(slot, placeholder);
        player.getInventory().setChanged();
        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 0.7F, 1.1F);
        return true;
    }

    static int selectedSlot(Player player) {
        //? if >=1.21.5 {
        return player.getInventory().getSelectedSlot();
        //?} else {
        /*return player.getInventory().selected;
        *///?}
    }

    public static StoredTool getStored(ServerPlayer player, int slot, long deadline) {
        ItemStack placeholder = player.getInventory().getItem(slot);
        if (!placeholder.is(ModItems.POTASSIUM_THROWABLE_TOOL.get())) return null;
        StoredTool stored = placeholder.get(STORED_TOOL.get());
        return stored != null && stored.deadline() == deadline && !stored.tool().isEmpty() ? stored : null;
    }

    public static void restore(ServerPlayer player, int slot, long deadline) {
        Inventory inventory = player.getInventory();
        StoredTool stored = getStored(player, slot, deadline);
        if (stored == null) return;
        ItemStack tool = stored.tool().copy();
        inventory.setItem(slot, tool);
        inventory.setChanged();
        //? if >=1.21.2 {
        player.getCooldowns().addCooldown(tool, COOLDOWN);
        //?} else {
        /*player.getCooldowns().addCooldown(tool.getItem(), COOLDOWN);
        *///?}
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            StoredTool stored = inventory.getItem(slot).get(STORED_TOOL.get());
            if (stored != null && player.level().getGameTime() >= stored.deadline()) restore(player, slot, stored.deadline());
        }
    }

    public record StoredTool(ItemStack tool, long deadline) {
        public static final Codec<StoredTool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemStack.OPTIONAL_CODEC.fieldOf("sword").forGetter(StoredTool::tool),
                Codec.LONG.fieldOf("deadline").forGetter(StoredTool::deadline)
        ).apply(instance, StoredTool::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, StoredTool> STREAM_CODEC = StreamCodec.composite(
                ItemStack.OPTIONAL_STREAM_CODEC, StoredTool::tool,
                ByteBufCodecs.VAR_LONG, StoredTool::deadline, StoredTool::new);
    }

    private PotassiumThrow() {}
}
