package space.anatomyuniverse.musavacca.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DecorationBlock extends Block {
    public static final MapCodec<DecorationBlock> CODEC = simpleCodec(DecorationBlock::new);

    public static final EnumProperty<Placement> PLACEMENT = EnumProperty.create("placement", Placement.class);
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final Direction DEFAULT_FACING = Direction.NORTH;
    private static final int ROTATION_COUNT = 16;
    private static final int HORIZONTAL_COUNT = 4;
    private static final Placement[] PLACEMENTS = Placement.values();

    private final Options options;
    private final VoxelShape[][] shapes;

    public DecorationBlock(Properties properties) {
        this(properties, Options.allRotating());
    }

    public DecorationBlock(Properties properties, Options options) {
        super(properties);

        this.options = options == null ? Options.allRotating() : options.sanitized();
        this.shapes = this.options.shapes().toShapeGrid();

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(PLACEMENT, this.options.firstEnabled())
                        .setValue(ROTATION, 0)
                        .setValue(FACING, DEFAULT_FACING)
        );
    }

    @Override
    protected MapCodec<? extends DecorationBlock> codec() {
        return CODEC;
    }

    public boolean isEnabled(Placement placement) {
        return this.options.isEnabled(placement);
    }

    public Orientation orientation(Placement placement) {
        return this.options.orientation(placement);
    }

    public static ResourceLocation defaultModelLocation(Block block, Placement placement) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);

        return ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "block/" + id.getPath() + placement.modelSuffix()
        );
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        BlockPos pos = context.getClickedPos();

        if (clickedFace.getAxis().isHorizontal() && this.options.side()) {
            return survivalChecked(
                    context,
                    pos,
                    this.defaultBlockState()
                            .setValue(PLACEMENT, Placement.SIDE)
                            .setValue(FACING, clickedFace)
                            .setValue(ROTATION, 0)
            );
        }

        if (clickedFace == Direction.DOWN && this.options.roof()) {
            return survivalChecked(context, pos, stateForPlacement(Placement.ROOF, context, pos));
        }

        Placement placement = this.options.standingPlacement(
                context.getPlayer() != null && context.getPlayer().isShiftKeyDown()
        );

        return placement == null
                ? null
                : survivalChecked(context, pos, stateForPlacement(placement, context, pos));
    }

    @Nullable
    private static BlockState survivalChecked(BlockPlaceContext context, BlockPos pos, BlockState state) {
        return state.canSurvive(context.getLevel(), pos) ? state : null;
    }

    private BlockState stateForPlacement(Placement placement, BlockPlaceContext context, BlockPos pos) {
        return switch (this.options.orientation(placement)) {
            case FIXED -> this.defaultBlockState()
                    .setValue(PLACEMENT, placement)
                    .setValue(ROTATION, 0)
                    .setValue(FACING, DEFAULT_FACING);

            case FACING -> this.defaultBlockState()
                    .setValue(PLACEMENT, placement)
                    .setValue(ROTATION, 0)
                    .setValue(FACING, frontFacesPlayerFacing(context, pos));

            case ROTATION -> {
                int rotation = frontFacesPlayerRotation(context, pos);

                yield this.defaultBlockState()
                        .setValue(PLACEMENT, placement)
                        .setValue(ROTATION, rotation)
                        .setValue(FACING, directionFromRotation(rotation));
            }
        };
    }

    private static int frontFacesPlayerRotation(BlockPlaceContext context, BlockPos pos) {
        Player player = context.getPlayer();

        int rotation;

        if (player == null) {
            rotation = RotationSegment.convertToSegment(context.getRotation());
        } else {
            double dx = player.getX() - (pos.getX() + 0.5D);
            double dz = player.getZ() - (pos.getZ() + 0.5D);

            if ((dx * dx) + (dz * dz) < 1.0E-7D) {
                rotation = RotationSegment.convertToSegment(context.getRotation());
            } else {
                float yawFromBlockToPlayer = (float) Math.toDegrees(Math.atan2(-dx, dz));
                rotation = RotationSegment.convertToSegment(yawFromBlockToPlayer);
            }
        }

        return Math.floorMod(rotation + 8, ROTATION_COUNT);
    }

    private static Direction frontFacesPlayerFacing(BlockPlaceContext context, BlockPos pos) {
        Player player = context.getPlayer();

        if (player == null) {
            return context.getHorizontalDirection().getOpposite();
        }

        double dx = player.getX() - (pos.getX() + 0.5D);
        double dz = player.getZ() - (pos.getZ() + 0.5D);

        if ((dx * dx) + (dz * dz) < 1.0E-7D) {
            return context.getHorizontalDirection().getOpposite();
        }

        if (Math.abs(dx) > Math.abs(dz)) {
            return dx > 0.0D ? Direction.EAST : Direction.WEST;
        }

        return dz > 0.0D ? Direction.SOUTH : Direction.NORTH;
    }

    private static Direction directionFromRotation(int rotation) {
        return switch (Math.floorMod(rotation + 2, ROTATION_COUNT) / 4) {
            case 0 -> Direction.SOUTH;
            case 1 -> Direction.WEST;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.EAST;
            default -> DEFAULT_FACING;
        };
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Placement placement = state.getValue(PLACEMENT);

        if (!this.options.isEnabled(placement)) {
            return false;
        }

        return switch (placement) {
            case FLOOR, SNEAK -> Block.canSupportCenter(level, pos.below(), Direction.UP);
            case ROOF -> sturdy(level, pos.above(), Direction.DOWN);
            case SIDE -> {
                Direction facing = state.getValue(FACING);
                yield sturdy(level, pos.relative(facing.getOpposite()), facing);
            }
        };
    }

    private static boolean sturdy(LevelReader level, BlockPos supportPos, Direction supportFace) {
        return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, supportFace);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        Placement placement = state.getValue(PLACEMENT);

        return switch (this.options.orientation(placement)) {
            case FIXED -> state;

            case FACING -> state.setValue(
                    FACING,
                    rotation.rotate(state.getValue(FACING))
            );

            case ROTATION -> {
                int nextRotation = rotation.rotate(state.getValue(ROTATION), ROTATION_COUNT);

                yield state
                        .setValue(ROTATION, nextRotation)
                        .setValue(FACING, directionFromRotation(nextRotation));
            }
        };
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        Placement placement = state.getValue(PLACEMENT);

        return switch (this.options.orientation(placement)) {
            case FIXED -> state;

            case FACING -> state.rotate(mirror.getRotation(state.getValue(FACING)));

            case ROTATION -> {
                int nextRotation = mirror.mirror(state.getValue(ROTATION), ROTATION_COUNT);

                yield state
                        .setValue(ROTATION, nextRotation)
                        .setValue(FACING, directionFromRotation(nextRotation));
            }
        };
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        Placement placement = state.getValue(PLACEMENT);
        int placementIndex = placement.ordinal();

        if (this.options.usesFacingShape(placement)) {
            return this.shapes[placementIndex][horizontalIndex(state.getValue(FACING))];
        }

        return this.shapes[placementIndex][0];
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return this.getShape(state, level, pos, context);
    }

    //? if <1.21.2 {
    /*@Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.empty();
    }
    *///?} else {
    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return Shapes.empty();
    }
    //?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PLACEMENT, ROTATION, FACING);
    }

    private static int horizontalIndex(Direction direction) {
        return switch (direction) {
            case NORTH -> 0;
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
    }

    private static VoxelShape[] rotateHorizontalShapes(VoxelShape northShape) {
        VoxelShape[] result = new VoxelShape[HORIZONTAL_COUNT];
        result[0] = northShape.optimize();

        for (int i = 1; i < HORIZONTAL_COUNT; i++) {
            result[i] = rotateShapeY90(result[i - 1]);
        }

        return result;
    }

    private static VoxelShape rotateShapeY90(VoxelShape shape) {
        VoxelShape[] result = { Shapes.empty() };

        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> result[0] = Shapes.or(
                result[0],
                Shapes.box(
                        1.0D - maxZ,
                        minY,
                        minX,
                        1.0D - minZ,
                        maxY,
                        maxX
                )
        ));

        return result[0].optimize();
    }

    public enum Placement implements StringRepresentable {
        FLOOR("floor", "_floor"),
        SNEAK("sneak", "_sneak"),
        SIDE("side", "_side"),
        ROOF("roof", "_roof");

        private final String name;
        private final String modelSuffix;

        Placement(String name, String modelSuffix) {
            this.name = name;
            this.modelSuffix = modelSuffix;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public String modelSuffix() {
            return this.modelSuffix;
        }
    }

    public enum Orientation {
        FIXED,
        FACING,
        ROTATION
    }

    public record Options(
            int placementMask,
            int facingMask,
            int rotationMask,
            ShapeSet shapes
    ) {
        public static Options allRotating() {
            return builder()
                    .floorRotating()
                    .sneakRotating()
                    .side()
                    .roofRotating()
                    .build();
        }

        public static Builder builder() {
            return new Builder();
        }

        public Options sanitized() {
            ShapeSet safeShapes = this.shapes == null ? ShapeSet.defaultSet() : this.shapes;

            int safePlacementMask = this.placementMask == 0
                    ? bit(Placement.FLOOR)
                    : this.placementMask;

            int safeRotationMask = this.rotationMask
                    & safePlacementMask
                    & ~bit(Placement.SIDE);

            int safeFacingMask = (this.facingMask | safeRotationMask)
                    & safePlacementMask;

            return new Options(
                    safePlacementMask,
                    safeFacingMask,
                    safeRotationMask,
                    safeShapes
            );
        }

        public boolean floor() {
            return isEnabled(Placement.FLOOR);
        }

        public boolean sneak() {
            return isEnabled(Placement.SNEAK);
        }

        public boolean side() {
            return isEnabled(Placement.SIDE);
        }

        public boolean roof() {
            return isEnabled(Placement.ROOF);
        }

        public boolean isEnabled(Placement placement) {
            return (this.placementMask & bit(placement)) != 0;
        }

        public boolean usesFacingShape(Placement placement) {
            return orientation(placement) != Orientation.FIXED;
        }

        public Orientation orientation(Placement placement) {
            if (placement == Placement.SIDE) {
                return Orientation.FACING;
            }

            if ((this.rotationMask & bit(placement)) != 0) {
                return Orientation.ROTATION;
            }

            if ((this.facingMask & bit(placement)) != 0) {
                return Orientation.FACING;
            }

            return Orientation.FIXED;
        }

        @Nullable
        public Placement standingPlacement(boolean sneaking) {
            if (sneaking && this.sneak()) {
                return Placement.SNEAK;
            }

            if (this.floor()) {
                return Placement.FLOOR;
            }

            if (this.sneak()) {
                return Placement.SNEAK;
            }

            return null;
        }

        public Placement firstEnabled() {
            for (Placement placement : PLACEMENTS) {
                if (isEnabled(placement)) {
                    return placement;
                }
            }

            return Placement.FLOOR;
        }

        private static int bit(Placement placement) {
            return 1 << placement.ordinal();
        }

        public static final class Builder {
            private int placementMask;
            private int facingMask;
            private int rotationMask;
            private ShapeSet shapes = ShapeSet.defaultSet();

            private Builder() {}

            public Builder floor() {
                return enableFixed(Placement.FLOOR);
            }

            public Builder floorFacing() {
                return enableFacing(Placement.FLOOR);
            }

            public Builder floorRotating() {
                return enableRotating(Placement.FLOOR);
            }

            public Builder sneak() {
                return enableFixed(Placement.SNEAK);
            }

            public Builder sneakFacing() {
                return enableFacing(Placement.SNEAK);
            }

            public Builder sneakRotating() {
                return enableRotating(Placement.SNEAK);
            }

            public Builder side() {
                this.placementMask |= bit(Placement.SIDE);
                this.facingMask |= bit(Placement.SIDE);
                return this;
            }

            public Builder roof() {
                return enableFixed(Placement.ROOF);
            }

            public Builder roofFacing() {
                return enableFacing(Placement.ROOF);
            }

            public Builder roofRotating() {
                return enableRotating(Placement.ROOF);
            }

            public Builder shapes(ShapeSet shapes) {
                this.shapes = shapes;
                return this;
            }

            private Builder enableFixed(Placement placement) {
                this.placementMask |= bit(placement);
                this.facingMask &= ~bit(placement);
                this.rotationMask &= ~bit(placement);
                return this;
            }

            private Builder enableFacing(Placement placement) {
                this.placementMask |= bit(placement);
                this.facingMask |= bit(placement);
                this.rotationMask &= ~bit(placement);
                return this;
            }

            private Builder enableRotating(Placement placement) {
                this.placementMask |= bit(placement);
                this.facingMask |= bit(placement);
                this.rotationMask |= bit(placement);
                return this;
            }

            public Options build() {
                return new Options(
                        this.placementMask,
                        this.facingMask,
                        this.rotationMask,
                        this.shapes
                ).sanitized();
            }
        }
    }

    public record ShapeSet(
            VoxelShape floor,
            VoxelShape sneak,
            VoxelShape side,
            VoxelShape roof
    ) {
        private static final VoxelShape DEFAULT_FLOOR = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);
        private static final VoxelShape DEFAULT_SNEAK = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 8.0D, 14.0D);
        private static final VoxelShape DEFAULT_SIDE = Block.box(4.5D, 0.5D, 2.25D, 11.5D, 12.5D, 9.25D);
        private static final VoxelShape DEFAULT_ROOF = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 16.0D, 12.0D);

        public static ShapeSet defaultSet() {
            return new ShapeSet(DEFAULT_FLOOR, DEFAULT_SNEAK, DEFAULT_SIDE, DEFAULT_ROOF);
        }

        public static ShapeSet same(VoxelShape shape) {
            return new ShapeSet(shape, shape, shape, shape);
        }

        public static ShapeSet of(
                VoxelShape floor,
                VoxelShape sneak,
                VoxelShape side,
                VoxelShape roof
        ) {
            return new ShapeSet(floor, sneak, side, roof);
        }

        private VoxelShape[][] toShapeGrid() {
            VoxelShape[][] result = new VoxelShape[PLACEMENTS.length][HORIZONTAL_COUNT];

            result[Placement.FLOOR.ordinal()] = rotateHorizontalShapes(this.floor);
            result[Placement.SNEAK.ordinal()] = rotateHorizontalShapes(this.sneak);
            result[Placement.SIDE.ordinal()] = rotateHorizontalShapes(this.side);
            result[Placement.ROOF.ordinal()] = rotateHorizontalShapes(this.roof);

            return result;
        }
    }
}



