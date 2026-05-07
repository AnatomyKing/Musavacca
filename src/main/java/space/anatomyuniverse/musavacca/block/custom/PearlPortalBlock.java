package space.anatomyuniverse.musavacca.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.entity.custom.PearlPortalBlockEntity;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticles;
import space.anatomyuniverse.musavacca.portal.PearlPortalDestroyer;
import space.anatomyuniverse.musavacca.portal.PearlPortalFrame;
import space.anatomyuniverse.musavacca.portal.PearlPortalNetwork;
import space.anatomyuniverse.musavacca.portal.PearlPortalResolver;
import space.anatomyuniverse.musavacca.portal.PearlPortalTransform;

import java.util.Map;
import java.util.Set;

public class PearlPortalBlock extends Block implements Portal, EntityBlock {
    public static final MapCodec<PearlPortalBlock> CODEC = simpleCodec(PearlPortalBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    private static final Map<Direction.Axis, VoxelShape> SHAPES =
            Shapes.rotateHorizontalAxis(Block.column(4.0D, 16.0D, 0.0D, 16.0D));

    public PearlPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    public EnumProperty<Direction.Axis> getAxisProperty() {
        return AXIS;
    }

    @Override
    public MapCodec<PearlPortalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PearlPortalBlockEntity(pos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(AXIS));
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess scheduledTickAccess,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            RandomSource random
    ) {
        Direction.Axis portalAxis = state.getValue(AXIS);
        Direction.Axis changedAxis = direction.getAxis();

        boolean changedAlongOtherHorizontalAxis = portalAxis != changedAxis && changedAxis.isHorizontal();

        if (!changedAlongOtherHorizontalAxis
                && !neighborState.is(this)
                && PearlPortalFrame.findExistingShape(level, pos, portalAxis).isEmpty()) {
            if (level instanceof ServerLevel serverLevel) {
                if (serverLevel.getBlockState(pos).is(this)) {
                    serverLevel.destroyBlock(pos, false);
                }

                return state;
            }

            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public int getPortalTransitionTime(ServerLevel level, Entity entity) {
        return 0;
    }

    @Nullable
    @Override
    public TeleportTransition getPortalDestination(ServerLevel currentLevel, Entity entity, BlockPos entryPos) {
        PearlPortalResolver.ResolvedPortal sourcePortal = findSourcePortal(currentLevel, entryPos);
        if (sourcePortal == null) {
            return null;
        }

        PearlPortalResolver.ResolvedPortal targetPortal = PearlPortalResolver
                .resolveLinkedPortal(currentLevel, sourcePortal.portalId())
                .orElse(null);

        if (targetPortal == null) {
            return null;
        }

        ServerLevel targetLevel = targetPortal.level();
        PearlPortalTransform.Result transform = PearlPortalTransform.calculate(entity, sourcePortal, targetPortal);

        EntityDimensions dimensions = entity.getDimensions(entity.getPose());
        Vec3 safePos = PortalShape.findCollisionFreePosition(transform.position(), targetLevel, entity, dimensions);
        BlockPos safeBlockPos = BlockPos.containing(safePos);

        TeleportTransition.PostTeleportTransition postTeleport =
                TeleportTransition.PLAY_PORTAL_SOUND.then(teleportedEntity -> {
                    teleportedEntity.setPortalCooldown();
                    teleportedEntity.setDeltaMovement(transform.deltaMovement());
                    PearlPortalResolver.keepDestinationAlive(teleportedEntity, safeBlockPos);
                });

        return new TeleportTransition(
                targetLevel,
                safePos,
                transform.deltaMovement(),
                transform.yRot(),
                transform.xRot(),
                Set.of(),
                postTeleport
        );
    }

    @Nullable
    private static PearlPortalResolver.ResolvedPortal findSourcePortal(ServerLevel level, BlockPos entryPos) {
        if (level.getBlockEntity(entryPos) instanceof PearlPortalBlockEntity portalBlockEntity
                && portalBlockEntity.isValidPortalTile()) {
            PearlPortalNetwork.registerPortalBlock(portalBlockEntity);
            return fromBlockEntity(level, portalBlockEntity);
        }

        PearlPortalNetwork.LoadedPortal loadedPortal = PearlPortalNetwork
                .getLoadedPortalAt(level, entryPos)
                .orElse(null);

        if (loadedPortal != null) {
            return fromLoadedPortal(loadedPortal);
        }

        BlockState state = level.getBlockState(entryPos);
        if (!(state.getBlock() instanceof PearlPortalBlock)) {
            return null;
        }

        Direction.Axis axis = state.getValue(AXIS);
        PearlPortalFrame.Shape shape = PearlPortalFrame.findExistingShape(level, entryPos, axis).orElse(null);
        if (shape == null) {
            return null;
        }

        PearlPortalBlockEntity foundTile = findAnyValidPortalTile(level, shape);
        if (foundTile == null) {
            return null;
        }

        PearlPortalNetwork.registerPortalBlock(foundTile);
        return fromBlockEntity(level, foundTile);
    }

    @Nullable
    private static PearlPortalBlockEntity findAnyValidPortalTile(ServerLevel level, PearlPortalFrame.Shape shape) {
        PearlPortalBlockEntity[] found = {null};

        shape.forEachInteriorBlock(pos -> {
            if (found[0] != null) {
                return;
            }

            if (level.getBlockEntity(pos) instanceof PearlPortalBlockEntity portalBlockEntity
                    && portalBlockEntity.isValidPortalTile()) {
                found[0] = portalBlockEntity;
            }
        });

        return found[0];
    }

    private static PearlPortalResolver.ResolvedPortal fromBlockEntity(
            ServerLevel level,
            PearlPortalBlockEntity portalBlockEntity
    ) {
        return new PearlPortalResolver.ResolvedPortal(
                portalBlockEntity.getPortalId(),
                level,
                portalBlockEntity.getPortalShape(),
                portalBlockEntity.getHexColor()
        );
    }

    private static PearlPortalResolver.ResolvedPortal fromLoadedPortal(PearlPortalNetwork.LoadedPortal loadedPortal) {
        return new PearlPortalResolver.ResolvedPortal(
                loadedPortal.portalId(),
                loadedPortal.level(),
                loadedPortal.shape(),
                loadedPortal.hexColor()
        );
    }

    @Override
    public Portal.Transition getLocalTransition() {
        return Portal.Transition.CONFUSION;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.5F,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }

        if (!(level.getBlockEntity(pos) instanceof PearlPortalBlockEntity pearlPortalBe)
                || !pearlPortalBe.isValidPortalTile()) {
            return;
        }

        if (random.nextFloat() > 0.35F) {
            return;
        }

        int hexColor = pearlPortalBe.getHexColor();

        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() + random.nextDouble();
        double z = pos.getZ() + random.nextDouble();

        double dx = (random.nextFloat() - 0.5D) * 0.5D;
        double dy = (random.nextFloat() - 0.5D) * 0.5D;
        double dz = (random.nextFloat() - 0.5D) * 0.5D;

        int side = random.nextInt(2) * 2 - 1;

        if (!level.getBlockState(pos.west()).is(this) && !level.getBlockState(pos.east()).is(this)) {
            x = pos.getX() + 0.5D + 0.25D * side;
            dx = random.nextFloat() * 2.0F * side;
        } else {
            z = pos.getZ() + 0.5D + 0.25D * side;
            dz = random.nextFloat() * 2.0F * side;
        }

        ProfileTintParticles.spawnRandomVariant(
                level,
                random,
                hexColor,
                x,
                y,
                z,
                dx,
                dy,
                dz,
                ModParticleTypes.PEARL_G_TINTED.get(),
                ModParticleTypes.PEARL_2_TINTED.get(),
                ModParticleTypes.PEARL_C_TINTED.get(),
                ModParticleTypes.PEARL_H_TINTED.get()
        );
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData) {
        return ItemStack.EMPTY;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return switch (rotation) {
            case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
                case X -> state.setValue(AXIS, Direction.Axis.Z);
                case Z -> state.setValue(AXIS, Direction.Axis.X);
                default -> state;
            };
            default -> state;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }
}