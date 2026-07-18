package space.anatomyuniverse.musavacca.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import space.anatomyuniverse.musavacca.MusaCore;

public final class CustomHelmetArmorTrims {
    public static final TagKey<Item> CUSTOM_HEAD_HELMETS = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(
                    MusaCore.MOD_ID,
                    "custom_head_helmets"
            )
    );

    public static final ResourceLocation TRIM_CARRIER_ID =
            ResourceLocation.fromNamespaceAndPath(
                    MusaCore.MOD_ID,
                    "custom_helmet_trim_carrier"
            );

    public static final ResourceKey<EquipmentAsset> TRIM_CARRIER_ASSET =
            ResourceKey.create(
                    EquipmentAssets.ROOT_ID,
                    TRIM_CARRIER_ID
            );

    private CustomHelmetArmorTrims() {
    }
}