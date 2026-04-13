package space.anatomyuniverse.musavacca.entity.mob.basuke;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.block.ModBlocks;

import java.util.EnumSet;

public class Basuke extends Allay {

    private static final String TAG_VOCO_TABLE_POS = "voco_table_pos";
    private static final long NO_TABLE_POS = Long.MIN_VALUE;

    // Slightly lower than before so Basuke stays a bit closer to the table.
    private static final double AREA_MIN_X = 0.32D;
    private static final double AREA_MAX_X = 0.68D;
    private static final double AREA_MIN_Y = 0.94D;
    private static final double AREA_MAX_Y = 2.36D;
    private static final double AREA_MIN_Z = 0.32D;
    private static final double AREA_MAX_Z = 0.68D;

    private static final double ORBIT_SPEED = 0.045D;
    private static final double CONTAIN_SPEED = 0.055D;
    private static final double CONTAIN_MIN_PUSH = 0.004D;
    private static final double CONTAIN_MAX_PUSH = 0.020D;
    private static final double CONTAIN_DAMPING = 0.70D;

    private BlockPos vocoTablePos;

    public Basuke(EntityType<? extends Basuke> type, Level level) {
        super(type, level);
    }

    public static @NotNull AttributeSupplier.Builder createAttributes() {
        return Allay.createAttributes();
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
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);

        if (this.vocoTablePos != null) {
            output.putLong(TAG_VOCO_TABLE_POS, this.vocoTablePos.asLong());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);

        long packed = input.getLongOr(TAG_VOCO_TABLE_POS, NO_TABLE_POS);
        this.vocoTablePos = packed != NO_TABLE_POS ? BlockPos.of(packed) : null;

        if (this.vocoTablePos != null) {
            this.setPersistenceRequired();
            this.setInvulnerable(true);
        }
    }

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
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
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