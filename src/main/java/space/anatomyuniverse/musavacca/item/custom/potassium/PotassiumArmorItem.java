package space.anatomyuniverse.musavacca.item.custom.potassium;

import net.minecraft.world.item.Item;
//? if <1.21.2 {
/*import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import space.anatomyuniverse.musavacca.MusaCore;
*///?}

public final class PotassiumArmorItem
        //? if <1.21.2 {
        /*extends ArmorItem
        *///?} else {
        extends PotassiumItem
        //?}
{
    //? if <1.21.2 {
    /*private static final ResourceLocation HUMANOID_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MusaCore.MOD_ID,
            "textures/entity/equipment/humanoid/potassium.png"
    );
    private static final ResourceLocation LEGGINGS_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            MusaCore.MOD_ID,
            "textures/entity/equipment/humanoid_leggings/potassium.png"
    );

    public PotassiumArmorItem(Holder<ArmorMaterial> material, Type type, Item.Properties properties) {
        super(material, type, properties.durability(type.getDurability(35)));
    }

    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return innerModel ? LEGGINGS_TEXTURE : HUMANOID_TEXTURE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (PotassiumItemBehavior.isLookingAtBlock(player)) return InteractionResultHolder.pass(stack);
        if (!PotassiumItemBehavior.canStartEating(stack, player)) return InteractionResultHolder.fail(stack);
        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        return PotassiumItemBehavior.finishUsingItem(stack, level, entity);
    }
    *///?} else {
    public PotassiumArmorItem(Item.Properties properties) {
        super(properties);
    }
    //?}
}
