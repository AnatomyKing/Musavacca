package space.anatomyuniverse.musavacca.item;

import net.minecraft.resources.ResourceKey;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;

import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
*///?} else if >=1.21.4 <1.21.11 {
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
//?} else {
/*import net.minecraft.resources.ResourceLocation;
*///?}

import space.anatomyuniverse.musavacca.MusaCore;

public final class CustomHelmetArmorTrims {
    //? if >=1.21.11 {
    /*public static final Identifier TRIM_CARRIER_ID =
            Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, "custom_helmet_trim_carrier");
    *///?} else {
    public static final ResourceLocation TRIM_CARRIER_ID =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "custom_helmet_trim_carrier");
    //?}

    //? if >=1.21.4 {
    public static final ResourceKey<EquipmentAsset> TRIM_CARRIER_ASSET =
            ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    TRIM_CARRIER_ID
            );

    //?}
    private CustomHelmetArmorTrims() {
    }
}
