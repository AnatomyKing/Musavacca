package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
//? if <1.21.2 {
/*import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.entity.boat.musavacca.MusavaccaBoat;
import java.util.List;
*///?} else {
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.item.BoatItem;
//?}

public final class MusavaccaBoatItem extends
        //? if <1.21.2 {
        /*Item
        *///?} else {
        BoatItem
        //?}
{

    //? if <1.21.2
    //private final EntityType<? extends MusavaccaBoat> entityType;

    public MusavaccaBoatItem(
            //? if <1.21.2 {
            /*EntityType<? extends MusavaccaBoat> entityType,
            *///?} else {
            EntityType<? extends AbstractBoat> entityType,
            //?}
            Properties properties
    ) {
        //? if <1.21.2 {
        /*super(properties);
        this.entityType = entityType;
        *///?} else {
        super(entityType, properties);
        //?}
    }

    //? if <1.21.2 {
    /*@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

        if (hit.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(stack);
        }

        Vec3 view = player.getViewVector(1.0F);
        List<Entity> nearby = level.getEntities(
                player,
                player.getBoundingBox().expandTowards(view.scale(5.0D)).inflate(1.0D),
                EntitySelector.NO_SPECTATORS.and(Entity::isPickable)
        );

        Vec3 eye = player.getEyePosition();
        for (Entity entity : nearby) {
            if (entity.getBoundingBox().inflate(entity.getPickRadius()).contains(eye)) {
                return InteractionResultHolder.pass(stack);
            }
        }

        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        MusavaccaBoat boat = this.entityType.create(level);
        if (boat == null) {
            return InteractionResultHolder.fail(stack);
        }

        Vec3 location = hit.getLocation();
        boat.setPos(location.x, location.y, location.z);
        boat.setYRot(player.getYRot());

        if (!level.noCollision(boat, boat.getBoundingBox())) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide()) {
            level.addFreshEntity(boat);
            level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.ENTITY_PLACE, location);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
    *///?}
}



