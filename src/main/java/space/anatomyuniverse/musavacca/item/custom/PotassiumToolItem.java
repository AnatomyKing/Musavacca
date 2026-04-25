// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/item/custom/PotassiumToolItem.java
package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PotassiumToolItem extends Item {
    private static final int EAT_DURABILITY_DAMAGE = 8;

    public PotassiumToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (isLookingAtBlock(level, player)) {
            return InteractionResult.PASS;
        }

        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);

        if (!level.isClientSide() && food != null && consumable != null) {
            food.onConsume(level, entity, stack, consumable);

            if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
                stack.hurtAndBreak(EAT_DURABILITY_DAMAGE, entity, entity.getUsedItemHand());
            }
        }

        return stack;
    }

    private static boolean isLookingAtBlock(Level level, Player player) {
        double reach = player.blockInteractionRange();

        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getViewVector(1.0F).scale(reach));

        BlockHitResult hitResult = level.clip(
                new ClipContext(
                        start,
                        end,
                        ClipContext.Block.OUTLINE,
                        ClipContext.Fluid.NONE,
                        player
                )
        );

        return hitResult.getType() == HitResult.Type.BLOCK;
    }
}