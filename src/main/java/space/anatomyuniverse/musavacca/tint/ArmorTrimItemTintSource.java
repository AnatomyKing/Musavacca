package space.anatomyuniverse.musavacca.tint;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.TextColor;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.data.models.newgen.ArmorItems;
import space.anatomyuniverse.musavacca.data.models.newgen.Tints;

import java.util.List;

//? if >=1.21.4 {
import net.minecraft.world.item.equipment.trim.ArmorTrim;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
//?} else if >=1.21.2 <1.21.4 {
/*import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import net.minecraft.world.item.equipment.trim.ArmorTrim;
*///?} else {
/*import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import net.minecraft.world.item.armortrim.ArmorTrim;
*///?}

public final class ArmorTrimItemTintSource

        //? if >=1.21.4 {
        implements ItemTintSource {
        //?} else {
        /*{
        *///?}

    //? if >=1.21.11 {
    /*private static final Identifier TINT_ID =
            Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "armor_trim_color");
    *///?} else {
    private static final ResourceLocation TINT_ID =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "armor_trim_color");
    //?}

    //? if <1.21.4 {
    /*private static final ResourceLocation HAS_TRIM_PROPERTY =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "has_armor_trim");
    *///?}
    public static final ArmorTrimItemTintSource INSTANCE =
            new ArmorTrimItemTintSource();
    //? if >=1.21.4 {

    public static final MapCodec<ArmorTrimItemTintSource> MAP_CODEC =
            MapCodec.unit(INSTANCE);

    private static final Tints.ItemTintType TINT_TYPE =
            new Tints.ItemTintType(TINT_ID, MAP_CODEC);
    //?}

    private ArmorTrimItemTintSource() {}

    public static int color(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return Tints.NO_TINT;
        }

        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) {
            return Tints.NO_TINT;
        }

        TextColor color = trim.material().value().description().getStyle().getColor();
        return color == null
                ? Tints.NO_TINT
                : 0xFF000000 | (color.getValue() & 0xFFFFFF);
    }

    //? if >=1.21.4 {
    public static ItemTintSource itemTintSource() {
        return INSTANCE;
    }
    //?} else {
    /*public static void registerLegacyItemProperties(
            FMLClientSetupEvent event,
            List<ArmorItems.Entry> entries
    ) {
        event.enqueueWork(() -> {
            for (ArmorItems.Entry entry : entries) {
                registerLegacyTrimProperty(entry.helmet());
                registerLegacyTrimProperty(entry.chestplate());
                registerLegacyTrimProperty(entry.leggings());
                registerLegacyTrimProperty(entry.boots());
            }
        });
    }
    *///?}

    //? if >=1.21.4 {
    public static Tints.ItemTintType itemTintType() {
        return TINT_TYPE;
    }
    //?} else {
    /*private static void registerLegacyTrimProperty(ItemLike item) {
        if (item == null) {
            return;
        }

        ItemProperties.register(
                item.asItem(),
                HAS_TRIM_PROPERTY,
                (stack, level, entity, seed) -> stack.has(DataComponents.TRIM) ? 1.0F : 0.0F
        );
    }
    *///?}

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
    //?} else {
    /*public static ResourceLocation legacyTrimPropertyId() {
        return HAS_TRIM_PROPERTY;
    }
    *///?}

}
