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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;

public class VocoTableBlock extends Block implements EntityBlock {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 5.0D, 4.0D, 5.0D),
            Block.box(0.0D, 0.0D, 11.0D, 5.0D, 4.0D, 16.0D),
            Block.box(11.0D, 0.0D, 11.0D, 16.0D, 4.0D, 16.0D),
            Block.box(11.0D, 0.0D, 0.0D, 16.0D, 4.0D, 5.0D),

            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 14.0D, 14.0D),

            Block.box(10.0D, 12.0D, 0.0D, 16.0D, 16.0D, 6.0D),
            Block.box(10.0D, 12.0D, 10.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 12.0D, 10.0D, 6.0D, 16.0D, 16.0D),
            Block.box(0.0D, 12.0D, 0.0D, 6.0D, 16.0D, 6.0D),

            Block.box(6.0D, 10.0D, -1.0D, 10.0D, 13.0D, 2.0D),
            Block.box(14.0D, 10.0D, 6.0D, 17.0D, 13.0D, 10.0D),
            Block.box(6.0D, 10.0D, 14.0D, 10.0D, 13.0D, 17.0D),
            Block.box(-1.0D, 10.0D, 6.0D, 2.0D, 13.0D, 10.0D)
    );

    private enum HitPart {
        NONE(null, false),

        RECEPTOR_NORTH_EAST("Hit receptor: north-east corner", false),
        RECEPTOR_SOUTH_EAST("Hit receptor: south-east corner", false),
        RECEPTOR_SOUTH_WEST("Hit receptor: south-west corner", false),
        RECEPTOR_NORTH_WEST("Hit receptor: north-west corner", false),

        DIALER_NORTH("Hit dialer: north", true),
        DIALER_EAST("Hit dialer: east", true),
        DIALER_SOUTH("Hit dialer: south", true),
        DIALER_WEST("Hit dialer: west", true);

        private final String message;
        private final boolean dialer;

        HitPart(String message, boolean dialer) {
            this.message = message;
            this.dialer = dialer;
        }

        public String message() {
            return this.message;
        }

        public boolean isDialer() {
            return this.dialer;
        }

        public boolean isImportant() {
            return this != NONE;
        }
    }

    public VocoTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(LIT, false)
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VocoTableBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    private static InteractionResult passToDefault() {
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private static InteractionResult successResult() {
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> type,
            BlockEntityType<E> checkedType,
            BlockEntityTicker<? super E> ticker
    ) {
        return checkedType == type ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(
                type,
                ModBlockEntities.VOCO_TABLE_BLOCK_ENTITY.get(),
                VocoTableBlockEntity::serverTick
        );
    }

    private static void showMessage(Level level, Player player, String message) {
        if (level.isClientSide()) {
            player.displayClientMessage(Component.literal(message), false);
        }
    }

    private static void syncLitState(BlockState state, Level level, BlockPos pos, VocoTableBlockEntity tableBe) {
        if (level.isClientSide()) {
            return;
        }

        boolean shouldBeLit = tableBe.hasDisplayedItem() || tableBe.isBasukeVisible();

        if (state.getValue(LIT) != shouldBeLit) {
            level.setBlock(pos, state.setValue(LIT, shouldBeLit), Block.UPDATE_ALL);
        } else {
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
        }
    }

    private static void toggleBasuke(BlockState state, Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof VocoTableBlockEntity tableBe) {
            tableBe.toggleBasukeVisible();
            tableBe.updateBasuke(serverLevel);
            syncLitState(state, level, pos, tableBe);
        }
    }

    private static HitPart detectImportantPart(BlockPos pos, BlockHitResult hit) {
        double x = (hit.getLocation().x - pos.getX()) * 16.0D;
        double y = (hit.getLocation().y - pos.getY()) * 16.0D;
        double z = (hit.getLocation().z - pos.getZ()) * 16.0D;

        if (contains(x, y, z, 10.0D, 12.0D, 0.0D, 16.0D, 16.0D, 6.0D)) {
            return HitPart.RECEPTOR_NORTH_EAST;
        }
        if (contains(x, y, z, 10.0D, 12.0D, 10.0D, 16.0D, 16.0D, 16.0D)) {
            return HitPart.RECEPTOR_SOUTH_EAST;
        }
        if (contains(x, y, z, 0.0D, 12.0D, 10.0D, 6.0D, 16.0D, 16.0D)) {
            return HitPart.RECEPTOR_SOUTH_WEST;
        }
        if (contains(x, y, z, 0.0D, 12.0D, 0.0D, 6.0D, 16.0D, 6.0D)) {
            return HitPart.RECEPTOR_NORTH_WEST;
        }

        if (contains(x, y, z, 6.0D, 10.0D, -1.0D, 10.0D, 13.0D, 2.0D)) {
            return HitPart.DIALER_NORTH;
        }
        if (contains(x, y, z, 14.0D, 10.0D, 6.0D, 17.0D, 13.0D, 10.0D)) {
            return HitPart.DIALER_EAST;
        }
        if (contains(x, y, z, 6.0D, 10.0D, 14.0D, 10.0D, 13.0D, 17.0D)) {
            return HitPart.DIALER_SOUTH;
        }
        if (contains(x, y, z, -1.0D, 10.0D, 6.0D, 2.0D, 13.0D, 10.0D)) {
            return HitPart.DIALER_WEST;
        }

        return HitPart.NONE;
    }

    private static boolean contains(
            double x,
            double y,
            double z,
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {
        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        HitPart part = detectImportantPart(pos, hit);

        if (part.isImportant()) {
            showMessage(level, player, part.message());

            if (part.isDialer()) {
                toggleBasuke(state, level, pos);
            }

            return InteractionResult.SUCCESS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof VocoTableBlockEntity tableBe && tableBe.hasDisplayedItem()) {
            ItemStack removed = tableBe.removeDisplayedItem();

            if (!player.addItem(removed)) {
                player.drop(removed, false);
            }

            syncLitState(state, level, pos, tableBe);
            return InteractionResult.SUCCESS;
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
        if (player == null || hand == InteractionHand.OFF_HAND) {
            return passToDefault();
        }

        HitPart part = detectImportantPart(pos, hit);

        if (part.isImportant()) {
            showMessage(level, player, part.message());

            if (part.isDialer()) {
                toggleBasuke(state, level, pos);
            }

            return successResult();
        }

        if (stack.isEmpty()) {
            return passToDefault();
        }

        if (level.isClientSide()) {
            return successResult();
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof VocoTableBlockEntity tableBe)) {
            return passToDefault();
        }

        if (tableBe.hasDisplayedItem()) {
            return passToDefault();
        }

        ItemStack toInsert = stack.copy();
        toInsert.setCount(1);
        tableBe.setDisplayedItem(toInsert);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        syncLitState(state, level, pos, tableBe);
        return successResult();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}