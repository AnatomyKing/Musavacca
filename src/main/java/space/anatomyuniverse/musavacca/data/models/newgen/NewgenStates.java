package space.anatomyuniverse.musavacca.data.models.newgen;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

final class NewgenStates {
    private NewgenStates() {}

    record Pose<M>(Conditions.Match when, Function<M, ResourceLocation> model,
                   int x, int y, boolean uvLock) {}

    private static final boolean[] BOOLEANS = {false, true};

    static final List<Pose<SlabModels>> SLABS = slabs();
    static final List<Pose<StairModels>> STAIRS = stairs();
    static final List<Pose<DoorModels>> DOORS = doors();
    static final List<Pose<FenceModels>> FENCES = fences();
    static final List<Pose<FenceGateModels>> FENCE_GATES = fenceGates();
    static final List<Pose<ButtonModels>> BUTTONS = buttons();
    static final List<Pose<WallModels>> WALLS = walls();

    private static List<Pose<SlabModels>> slabs() {
        List<Pose<SlabModels>> poses = new ArrayList<>();
        for (SlabType type : SlabType.values()) {
            poses.add(new Pose<>(Conditions.when(SlabBlock.TYPE, type), m -> m.model(type), 0, 0, false));
        }
        return List.copyOf(poses);
    }

    private static List<Pose<StairModels>> stairs() {
        List<Pose<StairModels>> poses = new ArrayList<>();
        for (Direction facing : ModelDirections.horizontal()) {
            for (Half half : Half.values()) {
                for (StairsShape shape : StairsShape.values()) {
                    int y = (int) facing.getClockWise().toYRot();
                    if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) y += 270;
                    if (shape != StairsShape.STRAIGHT && half == Half.TOP) y += 90;
                    Conditions.Match when = Conditions.when(StairBlock.FACING, facing)
                            .and(StairBlock.HALF, half).and(StairBlock.SHAPE, shape);
                    poses.add(new Pose<>(when, m -> m.model(shape), half == Half.TOP ? 180 : 0,
                            ModelTransforms.quarterTurn(y), true));
                }
            }
        }
        return List.copyOf(poses);
    }

    private static List<Pose<DoorModels>> doors() {
        List<Pose<DoorModels>> poses = new ArrayList<>();
        for (Direction facing : ModelDirections.horizontal()) {
            for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
                for (DoorHingeSide hinge : DoorHingeSide.values()) {
                    for (boolean open : BOOLEANS) {
                        int y = northY(facing) + 270;
                        if (open) y += hinge == DoorHingeSide.LEFT ? 90 : -90;
                        Conditions.Match when = Conditions.when(DoorBlock.FACING, facing)
                                .and(DoorBlock.HALF, half).and(DoorBlock.HINGE, hinge).and(DoorBlock.OPEN, open);
                        poses.add(new Pose<>(when, m -> m.model(half, hinge, open), 0,
                                ModelTransforms.quarterTurn(y), false));
                    }
                }
            }
        }
        return List.copyOf(poses);
    }

    static List<Pose<TrapdoorModels>> trapdoors(TrapdoorBlocks.Orientation orientation) {
        List<Pose<TrapdoorModels>> poses = new ArrayList<>();
        boolean orientable = orientation == TrapdoorBlocks.Orientation.ORIENTABLE;
        for (Direction facing : ModelDirections.horizontal()) {
            for (Half half : Half.values()) {
                for (boolean open : BOOLEANS) {
                    boolean flip = orientable && open && half == Half.TOP;
                    int y = orientable || open ? northY(facing) : 0;
                    if (flip) y += 180;
                    Conditions.Match when = Conditions.when(TrapDoorBlock.FACING, facing)
                            .and(TrapDoorBlock.HALF, half).and(TrapDoorBlock.OPEN, open);
                    poses.add(new Pose<>(when, m -> m.model(half, open), flip ? 180 : 0,
                            ModelTransforms.quarterTurn(y), false));
                }
            }
        }
        return List.copyOf(poses);
    }

    private static List<Pose<FenceModels>> fences() {
        List<Pose<FenceModels>> poses = new ArrayList<>();
        poses.add(new Pose<>(Conditions.always(), FenceModels::post, 0, 0, false));
        for (Direction facing : ModelDirections.horizontal()) {
            BooleanProperty property = switch (facing) {
                case NORTH -> FenceBlock.NORTH;
                case EAST -> FenceBlock.EAST;
                case SOUTH -> FenceBlock.SOUTH;
                case WEST -> FenceBlock.WEST;
                default -> throw new IllegalArgumentException("Not horizontal: " + facing);
            };
            poses.add(new Pose<>(Conditions.when(property, true), FenceModels::side, 0, northY(facing), true));
        }
        return List.copyOf(poses);
    }

    private static List<Pose<FenceGateModels>> fenceGates() {
        List<Pose<FenceGateModels>> poses = new ArrayList<>();
        for (Direction facing : ModelDirections.horizontal()) {
            for (boolean open : BOOLEANS) {
                for (boolean inWall : BOOLEANS) {
                    Conditions.Match when = Conditions.when(FenceGateBlock.FACING, facing)
                            .and(FenceGateBlock.OPEN, open).and(FenceGateBlock.IN_WALL, inWall);
                    poses.add(new Pose<>(when, m -> m.model(open, inWall), 0, (int) facing.toYRot(), true));
                }
            }
        }
        return List.copyOf(poses);
    }

    static List<Pose<PressurePlateModels>> pressurePlates(Block block) {
        List<Pose<PressurePlateModels>> poses = new ArrayList<>();
        if (block.defaultBlockState().hasProperty(PressurePlateBlock.POWERED)) {
            for (boolean powered : BOOLEANS) {
                poses.add(new Pose<>(Conditions.when(PressurePlateBlock.POWERED, powered),
                        m -> m.model(powered), 0, 0, false));
            }
        } else {
            for (int power : BlockStateProperties.POWER.getPossibleValues()) {
                poses.add(new Pose<>(Conditions.when(BlockStateProperties.POWER, power),
                        m -> m.model(power > 0), 0, 0, false));
            }
        }
        return List.copyOf(poses);
    }

    private static List<Pose<ButtonModels>> buttons() {
        List<Pose<ButtonModels>> poses = new ArrayList<>();
        for (Direction facing : ModelDirections.horizontal()) {
            for (AttachFace face : AttachFace.values()) {
                for (boolean powered : BOOLEANS) {
                    int x = switch (face) { case FLOOR -> 0; case WALL -> 90; case CEILING -> 180; };
                    int y = face == AttachFace.CEILING ? (int) facing.toYRot() : northY(facing);
                    Conditions.Match when = Conditions.when(ButtonBlock.FACE, face)
                            .and(ButtonBlock.FACING, facing).and(ButtonBlock.POWERED, powered);
                    poses.add(new Pose<>(when, m -> m.model(powered), x, y, face == AttachFace.WALL));
                }
            }
        }
        return List.copyOf(poses);
    }

    private static List<Pose<WallModels>> walls() {
        List<Pose<WallModels>> poses = new ArrayList<>();
        poses.add(new Pose<>(Conditions.when(BlockStateProperties.UP, true), WallModels::post, 0, 0, false));
        for (Direction facing : ModelDirections.horizontal()) {
            EnumProperty<WallSide> property = switch (facing) {
                case NORTH -> BlockStateProperties.NORTH_WALL;
                case EAST -> BlockStateProperties.EAST_WALL;
                case SOUTH -> BlockStateProperties.SOUTH_WALL;
                case WEST -> BlockStateProperties.WEST_WALL;
                default -> throw new IllegalArgumentException("Not horizontal: " + facing);
            };
            poses.add(new Pose<>(Conditions.when(property, WallSide.LOW), WallModels::lowSide, 0, northY(facing), true));
            poses.add(new Pose<>(Conditions.when(property, WallSide.TALL), WallModels::tallSide, 0, northY(facing), true));
        }
        return List.copyOf(poses);
    }

    static int northY(Direction direction) {
        return switch (direction) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> throw new IllegalArgumentException("Not horizontal: " + direction);
        };
    }
}
