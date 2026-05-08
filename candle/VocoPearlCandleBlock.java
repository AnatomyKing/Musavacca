// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/VocoPearlCandleBlock.java
package space.anatomyuniverse.musavacca.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoInteractLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoPearlCandleBlockEntity;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticles;

public class VocoPearlCandleBlock extends Block implements EntityBlock {
    public static final EnumProperty<Corner> CORNER = EnumProperty.create("corner", Corner.class);
    public static final IntegerProperty CANDLES = IntegerProperty.create("candles", 1, 4);
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty PORTAL = BooleanProperty.create("portal");

    private static final int UPDATE_FLAGS = Block.UPDATE_ALL | Block.UPDATE_IMMEDIATE;

    /*
     * Local pixel flame positions for your NORTH_EAST ground-level Voco Mensa candle models.
     *
     * These came from the old table-relative positions:
     * y 21 -> y 5
     * y 23 -> y 7
     * y 24 -> y 8
     *
     * Other corners are rotated around the hidden candle block center.
     */
    private static final Vec3[][] FLAME_PIXELS_BY_CANDLE_COUNT = {
            new Vec3[0],
            {
                    new Vec3(13.0D, 7.0D, 3.0D)
            },
            {
                    new Vec3(11.0D, 7.0D, 3.0D),
                    new Vec3(15.0D, 8.0D, 2.0D)
            },
            {
                    new Vec3(13.0D, 5.0D, 5.0D),
                    new Vec3(11.0D, 7.0D, 3.0D),
                    new Vec3(14.0D, 8.0D, 2.0D)
            },
            {
                    new Vec3(12.0D, 5.0D, 4.0D),
                    new Vec3(15.0D, 7.0D, 4.0D),
                    new Vec3(11.0D, 7.0D, 1.0D),
                    new Vec3(14.0D, 8.0D, 1.0D)
            }
    };

    public VocoPearlCandleBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(CORNER, Corner.NORTH_EAST)
                        .setValue(CANDLES, 1)
                        .setValue(LIT, false)
                        .setValue(PORTAL, false)
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VocoPearlCandleBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CORNER, CANDLES, LIT, PORTAL);
    }

    public static BlockPos candlePosFor(BlockPos tablePos, ReceptorPosition receptor) {
        return switch (receptor) {
            case NORTH_EAST -> tablePos.above().north().east();
            case NORTH_WEST -> tablePos.above().north().west();
            case SOUTH_EAST -> tablePos.above().south().east();
            case SOUTH_WEST -> tablePos.above().south().west();
        };
    }

    public static boolean isPortalActiveForTable(Level level, BlockPos tablePos, ReceptorPosition receptor) {
        BlockPos candlePos = candlePosFor(tablePos, receptor);
        BlockState candleState = level.getBlockState(candlePos);

        return candleState.getBlock() instanceof VocoPearlCandleBlock
                && candleState.getValue(CORNER).receptor() == receptor
                && candleState.getValue(PORTAL);
    }

    public static void refreshPortalState(Level level, BlockPos tablePos, ReceptorPosition receptor) {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockPos candlePos = candlePosFor(tablePos, receptor);
        BlockState candleState = level.getBlockState(candlePos);

        if (!(candleState.getBlock() instanceof VocoPearlCandleBlock)) {
            return;
        }

        boolean tableReceptorLit = false;
        BlockState tableState = level.getBlockState(tablePos);

        if (tableState.getBlock() instanceof VocoTableBlock) {
            tableReceptorLit = tableState.getValue(VocoTableBlock.lightProperty(receptor));
        }

        boolean shouldPortal = tableReceptorLit && candleState.getValue(LIT);

        if (candleState.getValue(PORTAL) != shouldPortal) {
            level.setBlock(candlePos, candleState.setValue(PORTAL, shouldPortal), UPDATE_FLAGS);
        }
    }

    public static boolean tryPlaceOrAddFromTable(
            ItemStack stack,
            Level level,
            BlockPos tablePos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!(stack.getItem() instanceof BlockItem blockItem)
                || !(blockItem.getBlock() instanceof CandleBlock)
                || blockItem.getBlock() instanceof PearlCandleBlock) {
            return false;
        }

        Block candleBlock = blockItem.getBlock();
        BlockPos candlePos = candlePosFor(tablePos, receptor);
        BlockState stateAtCandlePos = level.getBlockState(candlePos);

        if (stateAtCandlePos.getBlock() instanceof VocoPearlCandleBlock) {
            return tryAddCandleToExisting(stack, level, candlePos, player, candleBlock);
        }

        if (!stateAtCandlePos.canBeReplaced()) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("There is no space for a Voco candle here."), false);
            }

            return true;
        }

        if (!level.isClientSide()) {
            BlockState newState = ModBlocks.VOCO_PEARL_CANDLE.get()
                    .defaultBlockState()
                    .setValue(CORNER, Corner.fromReceptor(receptor))
                    .setValue(CANDLES, 1)
                    .setValue(LIT, false)
                    .setValue(PORTAL, false);

            level.setBlock(candlePos, newState, UPDATE_FLAGS);

            if (level.getBlockEntity(candlePos) instanceof VocoPearlCandleBlockEntity candleBe) {
                candleBe.setTablePos(tablePos);
                candleBe.setCandleBlock(candleBlock);
                candleBe.setHexColor(FlintAndPearlItem.DEFAULT_HEX_COLOR);
            }

            playCandlePlaceSound(level, candlePos, candleBlock);

            if (!player.getAbilities().instabuild) {
                stack.consume(1, player);
            }

            level.gameEvent(player, GameEvent.BLOCK_CHANGE, candlePos);
        }

        return true;
    }

    public static boolean tryIgniteFromTable(
            ItemStack stack,
            Level level,
            BlockPos tablePos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!(stack.getItem() instanceof FlintAndPearlItem)) {
            return false;
        }

        BlockPos candlePos = candlePosFor(tablePos, receptor);
        BlockState candleState = level.getBlockState(candlePos);

        if (!(candleState.getBlock() instanceof VocoPearlCandleBlock)) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("This receptor needs Voco candles first."), false);
            }

            return true;
        }

        if (!level.isClientSide()) {
            ignite(
                    level,
                    candlePos,
                    candleState,
                    tablePos,
                    receptor,
                    getHexColorFromFlintAndPearl(stack),
                    player
            );
        }

        return true;
    }

    private static boolean tryAddCandleToExisting(
            ItemStack stack,
            Level level,
            BlockPos candlePos,
            Player player,
            Block candleBlock
    ) {
        BlockState state = level.getBlockState(candlePos);
        int currentCount = state.getValue(CANDLES);

        if (currentCount >= 4) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("This receptor already has four candles."), false);
            }

            return true;
        }

        if (level.getBlockEntity(candlePos) instanceof VocoPearlCandleBlockEntity candleBe
                && !candleBe.isSameCandleBlock(candleBlock)) {
            if (level.isClientSide()) {
                player.displayClientMessage(Component.literal("These candles must match the existing candle color."), false);
            }

            return true;
        }

        if (!level.isClientSide()) {
            BlockState newState = state.setValue(CANDLES, currentCount + 1);
            level.setBlock(candlePos, newState, UPDATE_FLAGS);

            if (level.getBlockEntity(candlePos) instanceof VocoPearlCandleBlockEntity candleBe) {
                candleBe.setCandleBlock(candleBlock);
            }

            playCandlePlaceSound(level, candlePos, candleBlock);

            if (!player.getAbilities().instabuild) {
                stack.consume(1, player);
            }

            level.gameEvent(player, GameEvent.BLOCK_CHANGE, candlePos);
        }

        return true;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (state.getValue(LIT)) {
            if (!level.isClientSide()) {
                extinguish(level, pos, state, player);
            }

            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            removeOneCandle(level, pos, state, player);
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
        if (stack.is(Items.SHEARS)) {
            if (!level.isClientSide()) {
                removeOneCandle(level, pos, state, player);
            }

            return InteractionResult.SUCCESS;
        }

        if (stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof CandleBlock
                && !(blockItem.getBlock() instanceof PearlCandleBlock)) {
            return tryAddCandleToExisting(stack, level, pos, player, blockItem.getBlock())
                    ? InteractionResult.SUCCESS
                    : InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (stack.getItem() instanceof FlintAndPearlItem) {
            if (!level.isClientSide()) {
                BlockPos tablePos = getTablePos(level, pos, state);
                ReceptorPosition receptor = state.getValue(CORNER).receptor();

                ignite(
                        level,
                        pos,
                        state,
                        tablePos,
                        receptor,
                        getHexColorFromFlintAndPearl(stack),
                        player
                );
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private static void ignite(
            Level level,
            BlockPos candlePos,
            BlockState candleState,
            BlockPos tablePos,
            ReceptorPosition receptor,
            int hexColor,
            Player player
    ) {
        if (level.getBlockEntity(candlePos) instanceof VocoPearlCandleBlockEntity candleBe) {
            candleBe.setTablePos(tablePos);
            candleBe.setHexColor(hexColor);
        }

        BlockState newState = candleState.setValue(LIT, true);
        level.setBlock(candlePos, newState, UPDATE_FLAGS);

        refreshPortalState(level, tablePos, receptor);

        level.playSound(
                null,
                candlePos,
                SoundEvents.FLINTANDSTEEL_USE,
                SoundSource.BLOCKS,
                1.0F,
                level.random.nextFloat() * 0.4F + 0.8F
        );

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, candlePos);
    }

    private static void extinguish(Level level, BlockPos pos, BlockState state, Player player) {
        BlockState newState = state
                .setValue(LIT, false)
                .setValue(PORTAL, false);

        level.setBlock(pos, newState, UPDATE_FLAGS);

        level.playSound(
                null,
                pos,
                SoundEvents.CANDLE_EXTINGUISH,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    private static void removeOneCandle(Level level, BlockPos pos, BlockState state, Player player) {
        int currentCount = state.getValue(CANDLES);
        Block candleBlock = getStoredCandleBlock(level, pos);

        ItemEntity item = new ItemEntity(
                level,
                pos.getX() + 0.5D,
                pos.getY() + 0.35D,
                pos.getZ() + 0.5D,
                new ItemStack(candleBlock.asItem())
        );

        item.setDeltaMovement(0.0D, 0.12D, 0.0D);
        level.addFreshEntity(item);

        level.playSound(
                null,
                pos,
                candleBlock.defaultBlockState().getSoundType().getBreakSound(),
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );

        if (currentCount <= 1) {
            level.removeBlock(pos, false);
        } else {
            level.setBlock(pos, state.setValue(CANDLES, currentCount - 1), UPDATE_FLAGS);
        }

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    private static Block getStoredCandleBlock(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof VocoPearlCandleBlockEntity candleBe) {
            return candleBe.getCandleBlock();
        }

        return net.minecraft.world.level.block.Blocks.CANDLE;
    }

    private static BlockPos getTablePos(Level level, BlockPos candlePos, BlockState candleState) {
        if (level.getBlockEntity(candlePos) instanceof VocoPearlCandleBlockEntity candleBe
                && candleBe.hasTablePos()) {
            return candleBe.getTablePos();
        }

        Corner corner = candleState.getValue(CORNER);

        return switch (corner) {
            case NORTH_EAST -> candlePos.below().south().west();
            case NORTH_WEST -> candlePos.below().south().east();
            case SOUTH_EAST -> candlePos.below().north().west();
            case SOUTH_WEST -> candlePos.below().north().east();
        };
    }

    private static int getHexColorFromFlintAndPearl(ItemStack stack) {
        Integer savedHex = stack.get(ModDataComponents.HEX_COLOR.get());
        return savedHex == null
                ? FlintAndPearlItem.DEFAULT_HEX_COLOR
                : savedHex & 0xFFFFFF;
    }

    private static void playCandlePlaceSound(Level level, BlockPos pos, Block candleBlock) {
        SoundType soundType = candleBlock.defaultBlockState().getSoundType();

        level.playSound(
                null,
                pos,
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }

        int hexColor = FlintAndPearlItem.DEFAULT_HEX_COLOR;

        if (level.getBlockEntity(pos) instanceof VocoPearlCandleBlockEntity candleBe) {
            hexColor = candleBe.getHexColor();
        }

        Corner corner = state.getValue(CORNER);
        int candleCount = state.getValue(CANDLES);

        for (Vec3 flamePixel : FLAME_PIXELS_BY_CANDLE_COUNT[candleCount]) {
            Vec3 flamePos = toWorldParticlePos(pos, corner, flamePixel);
            addPearlCandleParticlesAndSound(level, flamePos, random, hexColor);
        }
    }

    private static void addPearlCandleParticlesAndSound(
            Level level,
            Vec3 particlePos,
            RandomSource random,
            int hexColor
    ) {
        float roll = random.nextFloat();

        if (roll < 0.30F) {
            level.addParticle(
                    ParticleTypes.SMOKE,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    0.0D,
                    0.0D,
                    0.0D
            );

            if (roll < 0.17F) {
                level.playLocalSound(
                        particlePos.x + 0.5D,
                        particlePos.y + 0.5D,
                        particlePos.z + 0.5D,
                        SoundEvents.CANDLE_AMBIENT,
                        SoundSource.BLOCKS,
                        1.0F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
        }

        ProfileTintParticles.spawn(
                level,
                random,
                ModParticleTypes.PEARL_FLAME.get(),
                hexColor,
                particlePos.x,
                particlePos.y,
                particlePos.z,
                0.0D,
                0.0D,
                0.0D
        );
    }

    private static Vec3 toWorldParticlePos(BlockPos pos, Corner corner, Vec3 northEastPixel) {
        Vec3 rotated = rotatePixelAroundBlockCenter(northEastPixel, corner);

        return new Vec3(
                pos.getX() + (rotated.x / 16.0D),
                pos.getY() + (rotated.y / 16.0D),
                pos.getZ() + (rotated.z / 16.0D)
        );
    }

    private static Vec3 rotatePixelAroundBlockCenter(Vec3 pixel, Corner corner) {
        double x = pixel.x;
        double y = pixel.y;
        double z = pixel.z;

        return switch (corner) {
            case NORTH_EAST -> new Vec3(x, y, z);
            case SOUTH_EAST -> new Vec3(16.0D - z, y, x);
            case SOUTH_WEST -> new Vec3(16.0D - x, y, 16.0D - z);
            case NORTH_WEST -> new Vec3(z, y, 16.0D - x);
        };
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return candleSelectionShape(state.getValue(CORNER), state.getValue(CANDLES));
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
    }

    private static VoxelShape candleSelectionShape(Corner corner, int count) {
        CandleBox box = northEastCandleBox(count);

        Vec3 a = rotatePixelAroundBlockCenter(new Vec3(box.minX, box.minY, box.minZ), corner);
        Vec3 b = rotatePixelAroundBlockCenter(new Vec3(box.maxX, box.maxY, box.maxZ), corner);

        double minX = Math.min(a.x, b.x);
        double minY = Math.min(a.y, b.y);
        double minZ = Math.min(a.z, b.z);

        double maxX = Math.max(a.x, b.x);
        double maxY = Math.max(a.y, b.y);
        double maxZ = Math.max(a.z, b.z);

        return Block.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static CandleBox northEastCandleBox(int candles) {
        return switch (candles) {
            case 1 -> new CandleBox(12.0D, 0.0D, 2.0D, 14.0D, 8.0D, 4.0D);
            case 2 -> new CandleBox(10.0D, 0.0D, 1.0D, 16.0D, 8.0D, 4.0D);
            case 3 -> new CandleBox(10.0D, 0.0D, 1.0D, 15.0D, 8.0D, 6.0D);
            case 4 -> new CandleBox(10.0D, 0.0D, 0.0D, 16.0D, 8.0D, 5.0D);
            default -> new CandleBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        };
    }

    @Override
    protected ItemStack getCloneItemStack(
            LevelReader level,
            BlockPos pos,
            BlockState state,
            boolean includeData
    ) {
        Block block = net.minecraft.world.level.block.Blocks.CANDLE;

        if (level.getBlockEntity(pos) instanceof VocoPearlCandleBlockEntity candleBe) {
            block = candleBe.getCandleBlock();
        }

        return new ItemStack(block.asItem());
    }

    public static int lightLevel(BlockState state) {
        return state.getValue(LIT) ? Math.min(15, state.getValue(CANDLES) * 3) : 0;
    }

    private record CandleBox(
            double minX,
            double minY,
            double minZ,
            double maxX,
            double maxY,
            double maxZ
    ) {}

    public enum Corner implements StringRepresentable {
        NORTH_EAST("north_east", ReceptorPosition.NORTH_EAST, 0),
        SOUTH_EAST("south_east", ReceptorPosition.SOUTH_EAST, 90),
        SOUTH_WEST("south_west", ReceptorPosition.SOUTH_WEST, 180),
        NORTH_WEST("north_west", ReceptorPosition.NORTH_WEST, 270);

        private final String name;
        private final ReceptorPosition receptor;
        private final int yRotation;

        Corner(String name, ReceptorPosition receptor, int yRotation) {
            this.name = name;
            this.receptor = receptor;
            this.yRotation = yRotation;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public ReceptorPosition receptor() {
            return this.receptor;
        }

        public int yRotation() {
            return this.yRotation;
        }

        public static Corner fromReceptor(ReceptorPosition receptor) {
            return switch (receptor) {
                case NORTH_EAST -> NORTH_EAST;
                case SOUTH_EAST -> SOUTH_EAST;
                case SOUTH_WEST -> SOUTH_WEST;
                case NORTH_WEST -> NORTH_WEST;
            };
        }
    }
}