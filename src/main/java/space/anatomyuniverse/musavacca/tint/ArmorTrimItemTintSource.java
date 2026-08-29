package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;

//? if <1.21.4 {
/*import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.data.models.ModelSets;
import space.anatomyuniverse.musavacca.data.models.item.CustomArmorSet;
*///?}

//? if <1.21.2 {
/*import net.minecraft.world.item.armortrim.ArmorTrim;
*///?} else {
import net.minecraft.world.item.equipment.trim.ArmorTrim;
//?}

//? if >=1.21.4 {
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
//?}

/**
 * Minimal dynamic color source for the inventory/smithing trim overlay.
 *
 * <p>There is no material list here. The actual ArmorTrim component supplies
 * the material. Its normal display color tints the fixed slot mask. If a
 * third-party material has no display color, white is a safe fallback.</p>
 *
 * <p>This class affects item icons only. Worn trims are rendered separately by
 * Minecraft/NeoForge from the real registry-backed ArmorTrim.</p>
 */
public final class ArmorTrimItemTintSource
        //? if <1.21.4 {
        /*{
         *///?} else {
        implements ItemTintSource {
    //?}

    //? if <1.21.4 {
    /*public static final ResourceLocation HAS_TRIM_PROPERTY =
            ResourceLocation.fromNamespaceAndPath(
                    MusaCore.MOD_ID,
                    "has_armor_trim"
            );
    *///?}

    public static final ArmorTrimItemTintSource INSTANCE =
            new ArmorTrimItemTintSource();

    private static final int DEFAULT_COLOR = 0xFFFFFFFF;

    //? if >=1.21.4 {
    public static final MapCodec<ArmorTrimItemTintSource> MAP_CODEC =
            MapCodec.unit(INSTANCE);
    //?}

    private ArmorTrimItemTintSource() {}

    //? if <1.21.4 {
     
    /*public static void registerLegacyItemProperties(
            FMLClientSetupEvent event
    ) {
        event.enqueueWork(() -> {
            for (CustomArmorSet.Entry entry : ModelSets.customArmorSets()) {
                if (entry == null) {
                    continue;
                }

                registerLegacyTrimProperty(entry.helmet());
                registerLegacyTrimProperty(entry.chestplate());
                registerLegacyTrimProperty(entry.leggings());
                registerLegacyTrimProperty(entry.boots());
            }
        });
    }

    private static void registerLegacyTrimProperty(ItemLike itemLike) {
        if (itemLike == null) {
            return;
        }

        ItemProperties.register(
                itemLike.asItem(),
                HAS_TRIM_PROPERTY,
                (stack, level, entity, seed) ->
                        stack.has(DataComponents.TRIM) ? 1.0F : 0.0F
        );
    }
    *///?}

    public static int color(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return DEFAULT_COLOR;
        }

        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) {
            return DEFAULT_COLOR;
        }

        TextColor color = trim.material()
                .value()
                .description()
                .getStyle()
                .getColor();

        return color == null
                ? DEFAULT_COLOR
                : TintColorUtil.opaqueRgb(color.getValue());
    }

    //? if >=1.21.4 {
    @Override
    public int calculate(
            ItemStack stack,
            @Nullable ClientLevel level,
            @Nullable LivingEntity entity
    ) {
        return color(stack);
    }

    @Override
    public MapCodec<ArmorTrimItemTintSource> type() {
        return MAP_CODEC;
    }
    //?}
}
