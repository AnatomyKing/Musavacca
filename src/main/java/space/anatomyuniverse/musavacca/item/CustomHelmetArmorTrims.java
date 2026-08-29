package space.anatomyuniverse.musavacca.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
//? if >=1.21.4 {
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
//?}
import space.anatomyuniverse.musavacca.MusaCore;

public final class CustomHelmetArmorTrims {
    public static final ResourceLocation TRIM_CARRIER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MusaCore.MOD_ID,
                    "custom_helmet_trim_carrier"
            );

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


