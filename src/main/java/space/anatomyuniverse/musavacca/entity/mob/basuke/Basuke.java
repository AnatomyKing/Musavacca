package space.anatomyuniverse.musavacca.entity.mob.basuke;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
//? if <1.21.6
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
//? if >=1.21.6
import net.minecraft.world.level.storage.ValueInput;
//? if >=1.21.6
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.basuke.eating.VocoTableEatingAction;
import space.anatomyuniverse.musavacca.basuke.eating.VocoTableEatingLogic;

import java.util.EnumSet;

public class Basuke extends Allay {

    private static final String TAG_VOCO_TABLE_POS = "voco_table_pos";
    private static final long NO_TABLE_POS = Long.MIN_VALUE;

    private static final EntityDataAccessor<Integer> DATA_EATING_TICKS =
            SynchedEntityData.defineId(Basuke.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_EATING_TOTAL_TICKS =
            SynchedEntityData.defineId(Basuke.class, EntityDataSerializers.INT);

    public static final int EATING_CHEW_BEAT_TICKS = 4;
    public static final int MAX_EATING_TICKS = 20 * 60;

    private static final double AREA_MIN_X = 0.32D;
    private static final double AREA_MAX_X = 0.68D;
    private static final double AREA_MIN_Y = 1.42D;
    private static final double AREA_MAX_Y = 2.36D;
    private static final double AREA_MIN_Z = 0.32D;
    private static final double AREA_MAX_Z = 0.68D;

    private static final double ORBIT_SPEED = 0.045D;
    private static final double CONTAIN_SPEED = 0.055D;
    private static final double CONTAIN_MIN_PUSH = 0.004D;
    private static final double CONTAIN_MAX_PUSH = 0.020D;
    private static final double CONTAIN_DAMPING = 0.70D;

    private BlockPos vocoTablePos;
    private int eatingTicks;
    private int eatingTotalTicks;

    public Basuke(EntityType<? extends Basuke> type, Level level) {
        super(type, level);
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Allay.createAttributes();
    }

    @Override
    protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_EATING_TICKS, 0);
        builder.define(DATA_EATING_TOTAL_TICKS, 0);
    }

    public void bindToVocoTable(BlockPos pos) {
        this.vocoTablePos = pos.immutable();
        this.setPersistenceRequired();
        this.setInvulnerable(true);
    }

    public boolean isBoundToVocoTable() {
        return this.vocoTablePos != null;
    }

    public boolean isBoundToTable(BlockPos pos) {
        return this.vocoTablePos != null && this.vocoTablePos.equals(pos);
    }

    public BlockPos getVocoTablePos() {
        return this.vocoTablePos;
    }

    public int getBasukeEatingTicks() {
        return this.entityData.get(DATA_EATING_TICKS);
    }

    public int getBasukeEatingTotalTicks() {
        return this.entityData.get(DATA_EATING_TOTAL_TICKS);
    }

    @Override
    protected InteractionResult mobInteract(
            Player player,
            InteractionHand hand
    ) {
        VocoTableEatingLogic.beforeItemInteraction(
                this,
                player,
                hand
        );

        boolean wasHoldingNothing =
                this.getMainHandItem().isEmpty();

        InteractionResult result =
                super.mobInteract(
                        player,
                        hand
                );

        VocoTableEatingLogic.afterItemInteraction(
                this,
                player,
                wasHoldingNothing
        );

        return result;
    }

    private void setBasukeEatingTicks(int ticks) {
        this.entityData.set(DATA_EATING_TICKS, Mth.clamp(ticks, 0, MAX_EATING_TICKS));
    }

    private void setBasukeEatingTotalTicks(int ticks) {
        this.entityData.set(DATA_EATING_TOTAL_TICKS, Mth.clamp(ticks, 0, MAX_EATING_TICKS));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new OrbitVocoTableGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    public boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (!this.isBoundToVocoTable()) {
            super.checkDespawn();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide() || !this.isBoundToVocoTable()) {
            return;
        }

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!serverLevel.getBlockState(this.vocoTablePos).is(ModBlocks.VOCO_TABLE.get())) {
            this.discard();
            return;
        }

        AABB allowed = this.getAllowedArea();
        Vec3 pos = this.position();

        if (!allowed.contains(pos.x, pos.y, pos.z)) {
            this.confineTo(allowed, pos);
        }

        this.tickHeldItemEating(serverLevel);
    }

    private void tickHeldItemEating(@NotNull ServerLevel level) {
        ItemStack held = this.getMainHandItem();

        if (held.isEmpty()) {
            this.stopEatingHeldItem();
            return;
        }

        VocoTableEatingAction action =
                VocoTableEatingLogic.findActiveAction(
                        this,
                        level,
                        held
                );

        if (action == null) {
            this.stopEatingHeldItem();
            return;
        }

        int actionEatingTime =
                Mth.clamp(
                        action.eatingTimeTicks(),
                        1,
                        MAX_EATING_TICKS
                );

        if (
                this.eatingTicks <= 0
                        || this.eatingTotalTicks
                        != actionEatingTime
        ) {
            this.startEatingHeldItem(
                    actionEatingTime
            );
            return;
        }

        this.eatingTicks--;
        this.setBasukeEatingTicks(
                this.eatingTicks
        );

        int elapsedEatingTicks =
                this.eatingTotalTicks
                        - this.eatingTicks;

        float eatingProgress =
                Mth.clamp(
                        elapsedEatingTicks
                                / (float) Math.max(
                                1,
                                this.eatingTotalTicks
                        ),
                        0.0F,
                        1.0F
                );

        ItemStack particleStack =
                held.copyWithCount(1);

        if (isChewBeat(elapsedEatingTicks)) {
            this.spawnEatingItemParticles(
                    level,
                    particleStack,
                    particleCountForProgress(
                            eatingProgress
                    )
            );

            this.playChewBeatSound(
                    level,
                    elapsedEatingTicks,
                    eatingProgress
            );
        }

        if (this.eatingTicks <= 0) {
            this.finishEatingHeldItem(
                    level,
                    held,
                    particleStack,
                    action
            );
        }
    }

    private void startEatingHeldItem(int totalTicks) {
        this.eatingTotalTicks = Mth.clamp(totalTicks, 1, MAX_EATING_TICKS);
        this.eatingTicks = this.eatingTotalTicks;

        this.setBasukeEatingTotalTicks(this.eatingTotalTicks);
        this.setBasukeEatingTicks(this.eatingTicks);
    }

    private void stopEatingHeldItem() {
        this.eatingTicks = 0;
        this.eatingTotalTicks = 0;

        if (this.getBasukeEatingTicks() != 0) {
            this.setBasukeEatingTicks(0);
        }

        if (this.getBasukeEatingTotalTicks() != 0) {
            this.setBasukeEatingTotalTicks(0);
        }
    }

    private static boolean isChewBeat(int elapsedEatingTicks) {
        return elapsedEatingTicks > 0 && elapsedEatingTicks % EATING_CHEW_BEAT_TICKS == 0;
    }

    private static int particleCountForProgress(float eatingProgress) {
        if (eatingProgress > 0.78F) {
            return 6;
        }

        if (eatingProgress > 0.45F) {
            return 4;
        }

        return 3;
    }

    private void playChewBeatSound(@NotNull ServerLevel level, int elapsedEatingTicks, float eatingProgress) {
        float progress = Mth.clamp(eatingProgress, 0.0F, 1.0F);
        float beatIndex = elapsedEatingTicks / (float) EATING_CHEW_BEAT_TICKS;

        float beatTone = Mth.sin(beatIndex * 1.35F) * 0.025F;
        float volume = Mth.lerp(progress, 0.18F, 0.34F);
        float pitch = Mth.lerp(progress, 1.46F, 1.20F) + beatTone;

        this.playBasukeSound(level, SoundEvents.GENERIC_EAT, volume, pitch);
    }

    private void finishEatingHeldItem(
            @NotNull ServerLevel level,
            @NotNull ItemStack held,
            @NotNull ItemStack particleStack,
            @NotNull VocoTableEatingAction action
    ) {
        boolean completed =
                action.complete(
                        this,
                        level,
                        held
                );

        if (completed) {
            this.spawnEatingItemParticles(
                    level,
                    particleStack,
                    8
            );

            this.playBasukeSound(
                    level,
                    SoundEvents.PLAYER_BURP,
                    0.30F,
                    1.58F
            );
        }

        this.stopEatingHeldItem();
    }

    private void spawnEatingItemParticles(
            @NotNull ServerLevel level,
            @NotNull ItemStack particleStack,
            int count
    ) {
        if (particleStack.isEmpty()) {
            return;
        }

        Vec3 particlePos = this.getEatingParticlePosition();

        level.sendParticles(
                new ItemParticleOption(ParticleTypes.ITEM, particleStack),
                particlePos.x,
                particlePos.y,
                particlePos.z,
                count,
                0.035D,
                0.025D,
                0.035D,
                0.012D
        );
    }

    private Vec3 getEatingParticlePosition() {
        Vec3 forward = this.getLookAngle();
        Vec3 flatForward = new Vec3(forward.x, 0.0D, forward.z);

        if (flatForward.lengthSqr() < 1.0E-6D) {
            flatForward = Vec3.directionFromRotation(0.0F, this.getYRot());
        } else {
            flatForward = flatForward.normalize();
        }

        Vec3 right = new Vec3(-flatForward.z, 0.0D, flatForward.x);

        return new Vec3(
                this.getX(),
                this.getY() + (this.getBbHeight() * 0.66D),
                this.getZ()
        ).add(flatForward.scale(0.16D)).add(right.scale(-0.035D));
    }

    private void playBasukeSound(
            @NotNull ServerLevel level,
            @NotNull SoundEvent sound,
            float volume,
            float pitch
    ) {
        level.playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                sound,
                SoundSource.NEUTRAL,
                volume,
                pitch
        );
    }

    private void playBasukeSound(
            @NotNull ServerLevel level,
            @NotNull Holder<SoundEvent> sound,
            float volume,
            float pitch
    ) {
        level.playSeededSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                sound,
                SoundSource.NEUTRAL,
                volume,
                pitch,
                this.getRandom().nextLong()
        );
    }

    //? if >=1.21.6 {
    @Override
    protected void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);

        if (this.vocoTablePos != null) {
            output.putLong(TAG_VOCO_TABLE_POS, this.vocoTablePos.asLong());
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);

        long packed = input.getLongOr(TAG_VOCO_TABLE_POS, NO_TABLE_POS);
        this.vocoTablePos = packed != NO_TABLE_POS ? BlockPos.of(packed) : null;

        this.stopEatingHeldItem();

        if (this.vocoTablePos != null) {
            this.setPersistenceRequired();
            this.setInvulnerable(true);
        }
    }
    //?} else {
    /*@Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (this.vocoTablePos != null) {
            tag.putLong(TAG_VOCO_TABLE_POS, this.vocoTablePos.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        long packed = getLongOr(tag, TAG_VOCO_TABLE_POS, NO_TABLE_POS);
        this.vocoTablePos = packed != NO_TABLE_POS ? BlockPos.of(packed) : null;

        this.stopEatingHeldItem();

        if (this.vocoTablePos != null) {
            this.setPersistenceRequired();
            this.setInvulnerable(true);
        }
    }
    *///?}

    //? if <1.21.6 {
    /*private static long getLongOr(CompoundTag tag, String key, long fallback) {
        //? if >=1.21.5
        return tag.getLongOr(key, fallback);
        //? if <1.21.5
        //return tag.contains(key) ? tag.getLong(key) : fallback;
    }
    *///?}

    private void confineTo(AABB allowed, Vec3 pos) {
        Vec3 clamped = clampToBox(pos, allowed);
        Vec3 correction = clamped.subtract(pos);

        if (correction.lengthSqr() <= 1.0E-6D) {
            return;
        }

        this.getNavigation().stop();
        this.getMoveControl().setWantedPosition(clamped.x, clamped.y, clamped.z, CONTAIN_SPEED);

        double push = Mth.clamp(
                Math.sqrt(correction.lengthSqr()) * 0.12D,
                CONTAIN_MIN_PUSH,
                CONTAIN_MAX_PUSH
        );

        this.setDeltaMovement(
                this.getDeltaMovement()
                        .scale(CONTAIN_DAMPING)
                        .add(correction.normalize().scale(push))
        );
    }

    private AABB getAllowedArea() {
        BlockPos home = this.vocoTablePos;

        return new AABB(
                home.getX() + AREA_MIN_X,
                home.getY() + AREA_MIN_Y,
                home.getZ() + AREA_MIN_Z,
                home.getX() + AREA_MAX_X,
                home.getY() + AREA_MAX_Y,
                home.getZ() + AREA_MAX_Z
        );
    }

    private Vec3 randomPointInsideAllowedArea() {
        return randomPointInBox(this.getAllowedArea(), 0.01D, 0.05D);
    }

    private static Vec3 clampToBox(Vec3 pos, AABB box) {
        return new Vec3(
                Mth.clamp(pos.x, box.minX, box.maxX),
                Mth.clamp(pos.y, box.minY, box.maxY),
                Mth.clamp(pos.z, box.minZ, box.maxZ)
        );
    }

    private Vec3 randomPointInBox(AABB box, double padXZ, double padY) {
        return new Vec3(
                Mth.lerp(this.random.nextDouble(), box.minX + padXZ, box.maxX - padXZ),
                Mth.lerp(this.random.nextDouble(), box.minY + padY, box.maxY - padY),
                Mth.lerp(this.random.nextDouble(), box.minZ + padXZ, box.maxZ - padXZ)
        );
    }

    private static final class OrbitVocoTableGoal extends Goal {
        private final Basuke basuke;
        private Vec3 targetPos = Vec3.ZERO;
        private int retargetIn;

        private OrbitVocoTableGoal(Basuke basuke) {
            this.basuke = basuke;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return this.basuke.isBoundToVocoTable();
        }

        @Override
        public boolean canContinueToUse() {
            return this.basuke.isBoundToVocoTable();
        }

        @Override
        public void start() {
            this.pickNewTarget();
        }

        @Override
        public void tick() {
            if (!this.basuke.isBoundToVocoTable()) {
                return;
            }

            AABB bounds = this.basuke.getAllowedArea();

            if (!bounds.contains(this.targetPos.x, this.targetPos.y, this.targetPos.z)
                    || this.retargetIn-- <= 0
                    || this.basuke.distanceToSqr(this.targetPos.x, this.targetPos.y, this.targetPos.z) < 0.01D) {
                this.pickNewTarget();
            }

            this.basuke.getMoveControl().setWantedPosition(
                    this.targetPos.x,
                    this.targetPos.y,
                    this.targetPos.z,
                    ORBIT_SPEED
            );
        }

        private void pickNewTarget() {
            this.targetPos = this.basuke.randomPointInsideAllowedArea();
            this.retargetIn = 16 + this.basuke.getRandom().nextInt(16);
        }
    }
}
