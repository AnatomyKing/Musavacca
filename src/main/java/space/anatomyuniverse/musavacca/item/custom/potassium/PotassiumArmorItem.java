package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
//? if >=1.21.2 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
//?}
//? if <1.21.2 {
/*import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import space.anatomyuniverse.musavacca.MusaCore;
*///?}

public final class PotassiumArmorItem
        //? if >=1.21.2 {
        extends PotassiumItem
        //?} else {
        /*extends ArmorItem
        *///?}
{
    //? if <1.21.2 {
    /*private static final ResourceLocation HUMANOID_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/equipment/humanoid/potassium.png");
    private static final ResourceLocation LEGGINGS_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, "textures/entity/equipment/humanoid_leggings/potassium.png");

    *///?}
    //? if >=1.21.2 {
    public PotassiumArmorItem(Properties properties) {
    //?} else {
    /*public PotassiumArmorItem(Holder<ArmorMaterial> material, Type type, Item.Properties properties) {
    *///?}
        //? if >=1.21.2 {
        super(properties);
        //?} else {
        /*super(material, type, properties.durability(type.getDurability(35)));
        *///?}
    }

    @Override
    //? if >=1.21.2 {
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) return PotassiumItem.startEating(player, hand);
        ItemStack stack = player.getItemInHand(hand);
        var equippable = stack.get(DataComponents.EQUIPPABLE);
        return equippable != null && equippable.swappable() ? equippable.swapWithEquipmentSlot(stack, player) : InteractionResult.PASS;
    }
    //?} else {
    /*public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return player.isShiftKeyDown() ? PotassiumItem.startEating(player, hand) : super.use(level, player, hand);
    }
    *///?}

    //? if <1.21.2 {
    /*@Override
    public ResourceLocation getArmorTexture(
            ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return innerModel ? LEGGINGS_TEXTURE : HUMANOID_TEXTURE;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return PotassiumItem.finishEating(stack, level, entity);
    }
    *///?}
}
