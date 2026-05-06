package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public class VocoTableBlock extends Block implements EntityBlock {

    public static final BooleanProperty LIT_NORTH_EAST = BooleanProperty.create("lit_north_east");
    public static final BooleanProperty LIT_NORTH_WEST = BooleanProperty.create("lit_north_west");
    public static final BooleanProperty LIT_SOUTH_EAST = BooleanProperty.create("lit_south_east");
    public static final BooleanProperty LIT_SOUTH_WEST = BooleanProperty.create("lit_south_west");

    private static final BooleanProperty[] RECEPTOR_LIGHTS = {
            LIT_NORTH_EAST,
            LIT_NORTH_WEST,
            LIT_SOUTH_EAST,
            LIT_SOUTH_WEST
    };

    private static final int STATE_UPDATE_FLAGS = Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE;

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 5.0D, 4.0D, 5.0D),
            Block.box(0.0D, 0.0D, 11.0D, 5.0D, 4.0D, 16.0D),
            Block.box(11.0D, 0.0D, 11.0D, 16.0D, 4.0D, 16.0D),
            Block.box(11.0D, 0.0D, 0.0D, 16.0D, 4.0D, 5.0D),

            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D),

            Block.box(10.0D, 12.0D, 0.0D, 16.0D, 16.0D, 6.0D),
            Block.box(0.0D, 12.0D, 0.0D, 6.0D, 16.0D, 6.0D),
            Block.box(10.0D, 12.0D, 10.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 12.0D, 10.0D, 6.0D, 16.0D, 16.0D),

            Block.box(6.0D, 10.0D, -1.0D, 10.0D, 13.0D, 2.0D),
            Block.box(14.0D, 10.0D, 6.0D, 17.0D, 13.0D, 10.0D),
            Block.box(6.0D, 10.0D, 14.0D, 10.0D, 13.0D, 17.0D),
            Block.box(-1.0D, 10.0D, 6.0D, 2.0D, 13.0D, 10.0D)
    );

    private static final HitBox[] HIT_BOXES = {
            new HitBox(HitPart.RECEPTOR_NORTH_EAST, 10.0D, 12.0D, 0.0D, 16.0D, 16.0D, 6.0D),
            new HitBox(HitPart.RECEPTOR_NORTH_WEST, 0.0D, 12.0D, 0.0D, 6.0D, 16.0D, 6.0D),
            new HitBox(HitPart.RECEPTOR_SOUTH_EAST, 10.0D, 12.0D, 10.0D, 16.0D, 16.0D, 16.0D),
            new HitBox(HitPart.RECEPTOR_SOUTH_WEST, 0.0D, 12.0D, 10.0D, 6.0D, 16.0D, 16.0D),

            new HitBox(HitPart.DIALER_NORTH, 6.0D, 10.0D, -1.0D, 10.0D, 13.0D, 2.0D),
            new HitBox(HitPart.DIALER_EAST, 14.0D, 10.0D, 6.0D, 17.0D, 13.0D, 10.0D),
            new HitBox(HitPart.DIALER_SOUTH, 6.0D, 10.0D, 14.0D, 10.0D, 13.0D, 17.0D),
            new HitBox(HitPart.DIALER_WEST, -1.0D, 10.0D, 6.0D, 2.0D, 13.0D, 10.0D)
    };

    public VocoTableBlock(Properties properties) {
        super(properties);

        BlockState state = this.stateDefinition.any();
        for (BooleanProperty property : RECEPTOR_LIGHTS) {
            state = state.setValue(property, false);
        }

        this.registerDefaultState(state);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VocoTableBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(RECEPTOR_LIGHTS);
    }

    public static boolean hasAnyReceptorLit(BlockState state) {
        for (BooleanProperty property : RECEPTOR_LIGHTS) {
            if (state.getValue(property)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (handleSpecialHit(state, level, pos, player, hit)) {
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            removeDisplayedItem(level, pos, player);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (handleSpecialHit(state, level, pos, player, hit)) {
            return InteractionResult.SUCCESS;
        }

        if (stack.isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        return insertDisplayedItem(stack, level, pos, player)
                ? InteractionResult.SUCCESS
                : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private static boolean handleSpecialHit(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        HitPart part = detectHitPart(pos, hit);
        if (part == HitPart.NONE) {
            return false;
        }

        if (level.isClientSide()) {
            player.displayClientMessage(Component.literal(part.message), false);
            return true;
        }

        if (part.lightProperty != null) {
            toggleReceptorLight(state, level, pos, part.lightProperty);
        } else if (part.togglesBasuke) {
            toggleBasuke(level, pos);
        }

        return true;
    }

    private static void toggleReceptorLight(
            BlockState state,
            Level level,
            BlockPos pos,
            BooleanProperty property
    ) {
        level.setBlock(
                pos,
                state.setValue(property, !state.getValue(property)),
                STATE_UPDATE_FLAGS
        );
    }

    private static void toggleBasuke(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof VocoTableBlockEntity tableBe) {
            tableBe.toggleBasuke(serverLevel);
        }
    }

    private static boolean removeDisplayedItem(Level level, BlockPos pos, Player player) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof VocoTableBlockEntity tableBe) || !tableBe.hasDisplayedItem()) {
            return false;
        }

        ItemStack removed = tableBe.removeDisplayedItem();
        if (!player.addItem(removed)) {
            player.drop(removed, false);
        }

        return true;
    }

    private static boolean insertDisplayedItem(
            ItemStack stack,
            Level level,
            BlockPos pos,
            Player player
    ) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof VocoTableBlockEntity tableBe) || tableBe.hasDisplayedItem()) {
            return false;
        }

        ItemStack inserted = stack.copy();
        inserted.setCount(1);
        tableBe.setDisplayedItem(inserted);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return true;
    }

    private static HitPart detectHitPart(BlockPos pos, BlockHitResult hit) {
        Vec3 location = hit.getLocation();

        double x = (location.x - pos.getX()) * 16.0D;
        double y = (location.y - pos.getY()) * 16.0D;
        double z = (location.z - pos.getZ()) * 16.0D;

        for (HitBox box : HIT_BOXES) {
            if (box.contains(x, y, z)) {
                return box.part;
            }
        }

        return HitPart.NONE;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    private record HitBox(
            HitPart part,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        private boolean contains(double x, double y, double z) {
            return x >= this.minX && x <= this.maxX
                    && y >= this.minY && y <= this.maxY
                    && z >= this.minZ && z <= this.maxZ;
        }
    }

    private enum HitPart {
        NONE(null, null, false),

        RECEPTOR_NORTH_EAST("Toggled receptor: north-east corner", LIT_NORTH_EAST, false),
        RECEPTOR_NORTH_WEST("Toggled receptor: north-west corner", LIT_NORTH_WEST, false),
        RECEPTOR_SOUTH_EAST("Toggled receptor: south-east corner", LIT_SOUTH_EAST, false),
        RECEPTOR_SOUTH_WEST("Toggled receptor: south-west corner", LIT_SOUTH_WEST, false),

        DIALER_NORTH("Hit dialer: north", null, true),
        DIALER_EAST("Hit dialer: east", null, true),
        DIALER_SOUTH("Hit dialer: south", null, true),
        DIALER_WEST("Hit dialer: west", null, true);

        private final String message;
        private final BooleanProperty lightProperty;
        private final boolean togglesBasuke;

        HitPart(
                @Nullable String message,
                @Nullable BooleanProperty lightProperty,
                boolean togglesBasuke
        ) {
            this.message = message;
            this.lightProperty = lightProperty;
            this.togglesBasuke = togglesBasuke;
        }
    }
}