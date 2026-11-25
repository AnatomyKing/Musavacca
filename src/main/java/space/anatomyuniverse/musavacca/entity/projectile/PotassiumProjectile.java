package space.anatomyuniverse.musavacca.entity.projectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumCombat;
import space.anatomyuniverse.musavacca.item.custom.potassium.PotassiumThrow;

public final class PotassiumProjectile extends Projectile implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> ITEM =
            SynchedEntityData.defineId(PotassiumProjectile.class, EntityDataSerializers.ITEM_STACK);
    private int slot;
    private long deadline;
    private int returnTimer = 8;

    public PotassiumProjectile(EntityType<? extends PotassiumProjectile> type, Level level) {
        super(type, level);
        noPhysics = true;
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ITEM, ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItem() {
        return entityData.get(ITEM);
    }

    public void launch(ServerPlayer owner, ItemStack tool, int slot, long deadline) {
        this.slot = slot;
        this.deadline = deadline;
        setOwner(owner);
        entityData.set(ITEM, tool);
        setPos(owner.position().add(0, owner.getBbHeight() * 0.5, 0));
        shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, 1.75F, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(level() instanceof ServerLevel level)) {
            setPos(position().add(getDeltaMovement()));
            return;
        }
        if (!(getOwner() instanceof ServerPlayer owner) || !owner.isAlive() || owner.level() != level || level.getGameTime() >= deadline) {
            discard();
            return;
        }
        if (PotassiumThrow.getStored(owner, slot, deadline) == null) {
            discard();
            return;
        }
        Vec3 start = position();
        Vec3 movement = getDeltaMovement();
        Vec3 end = start.add(movement);
        var block = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        boolean blocked = block.getType() == HitResult.Type.BLOCK;
        if (blocked) end = block.getLocation();
        for (LivingEntity target :
                level.getEntitiesOfClass(
                        LivingEntity.class,
                        getBoundingBox().expandTowards(movement).inflate(0.5),
                        entity -> entity != owner && entity.isAlive() && entity.isPickable())) {
            var box = target.getBoundingBox().inflate(0.25);
            if (!box.contains(start) && box.clip(start, end).isEmpty()) continue;
            if (PotassiumCombat.attack(owner, target, slot, deadline)) returnTimer += 2;
            var stored = PotassiumThrow.getStored(owner, slot, deadline);
            if (stored == null || !owner.isAlive() || owner.level() != level) {
                discard();
                return;
            }
            entityData.set(ITEM, stored.tool().copy());
        }
        setPos(blocked ? end.subtract(movement.normalize().scale(0.05)) : end);
        Vec3 home = owner.position().add(0, owner.getBbHeight() * 0.6, 0).subtract(position());
        if (blocked) {
            returnTimer = 0;
            setDeltaMovement(home.normalize().scale(0.75));
        }
        if (--returnTimer <= 0) {
            if (distanceTo(owner) < 3.0F) {
                PotassiumThrow.restore(owner, slot, deadline);
                level.playSound(
                        null, owner.getX(), owner.getY(), owner.getZ(), SoundEvents.TRIDENT_RETURN, SoundSource.PLAYERS, 0.6F, 1.1F);
                discard();
                return;
            }
            Vec3 current = getDeltaMovement();
            double speed = Mth.clamp(current.length() * 3, 0.5, 2.0);
            setDeltaMovement(current.lerp(home.normalize().scale(speed), 0.1));
        }
    }
}
