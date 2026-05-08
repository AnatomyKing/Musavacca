// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTableLogic.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.PearlCandleBlock;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoSharedBetweenTableAndReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticles;
import space.anatomyuniverse.musavacca.teleport.HexTeleportDirectory;

public final class VocoTableLogic {
    public static final BooleanProperty LIT_NORTH_EAST = BooleanProperty.create("lit_north_east");
    public static final BooleanProperty LIT_NORTH_WEST = BooleanProperty.create("lit_north_west");
    public static final BooleanProperty LIT_SOUTH_EAST = BooleanProperty.create("lit_south_east");
    public static final BooleanProperty LIT_SOUTH_WEST = BooleanProperty.create("lit_south_west");

    public static final BooleanProperty PORTAL_NORTH_EAST = BooleanProperty.create("portal_north_east");
    public static final BooleanProperty PORTAL_NORTH_WEST = BooleanProperty.create("portal_north_west");
    public static final BooleanProperty PORTAL_SOUTH_EAST = BooleanProperty.create("portal_south_east");
    public static final BooleanProperty PORTAL_SOUTH_WEST = BooleanProperty.create("portal_south_west");

    public static final BooleanProperty[] RECEPTOR_LIGHTS = {
            LIT_NORTH_EAST,
            LIT_NORTH_WEST,
            LIT_SOUTH_EAST,
            LIT_SOUTH_WEST
    };

    public static final BooleanProperty[] RECEPTOR_PORTALS = {
            PORTAL_NORTH_EAST,
            PORTAL_NORTH_WEST,
            PORTAL_SOUTH_EAST,
            PORTAL_SOUTH_WEST
    };

    public static final VoxelShape BASE_SHAPE = Shapes.or(
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

    private static final int SHAPE_CACHE_SIZE = 5 * 5 * 5 * 5;
    private static final VoxelShape[] SHAPE_CACHE = new VoxelShape[SHAPE_CACHE_SIZE];

    private static final int MAX_PEARL_FLAME_PARTICLES_PER_ANIMATE_TICK = 6;
    private static final float PEARL_FLAME_SPAWN_CHANCE = 0.90F;
    private static final float SMOKE_PARTICLE_CHANCE = 0.14F;
    private static final float AMBIENT_SOUND_CHANCE = 0.025F;

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

    private VocoTableLogic() {}

    public static BlockState defaultState(BlockState state) {
        for (BooleanProperty property : RECEPTOR_LIGHTS) {
            state = state.setValue(property, false);
        }

        for (BooleanProperty property : RECEPTOR_PORTALS) {
            state = state.setValue(property, false);
        }

        return state;
    }

    public static void addProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RECEPTOR_LIGHTS);
        builder.add(RECEPTOR_PORTALS);
    }

    public static boolean hasAnyReceptorLit(BlockState state) {
        for (BooleanProperty property : RECEPTOR_LIGHTS) {
            if (state.getValue(property)) {
                return true;
            }
        }

        return false;
    }

    public static BooleanProperty lightProperty(ReceptorPosition receptor) {
        return switch (receptor) {
            case NORTH_EAST -> LIT_NORTH_EAST;
            case NORTH_WEST -> LIT_NORTH_WEST;
            case SOUTH_EAST -> LIT_SOUTH_EAST;
            case SOUTH_WEST -> LIT_SOUTH_WEST;
        };
    }

    public static BooleanProperty portalProperty(ReceptorPosition receptor) {
        return switch (receptor) {
            case NORTH_EAST -> PORTAL_NORTH_EAST;
            case NORTH_WEST -> PORTAL_NORTH_WEST;
            case SOUTH_EAST -> PORTAL_SOUTH_EAST;
            case SOUTH_WEST -> PORTAL_SOUTH_WEST;
        };
    }

    public static InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        ReceptorPosition candleHit = detectExistingCandleHit(level, pos, hit);
        HitPart part = detectHitPart(pos, hit);

        if (player.isShiftKeyDown()) {
            ReceptorPosition receptor = candleHit != null
                    ? candleHit
                    : part.receptor;

            if (receptor != null
                    && VocoSharedBetweenTableAndReceptorLogic.tryOpenSliderMenu(level, pos, player, receptor)) {
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.SUCCESS;
        }

        if (candleHit != null) {
            if (!level.isClientSide()) {
                extinguishCandleSlot(level, pos, player, candleHit);
            }

            return InteractionResult.SUCCESS;
        }

        if (part.togglesBasuke) {
            toggleBasuke(level, pos);
            return InteractionResult.SUCCESS;
        }

        if (part.isReceptor()) {
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            removeDisplayedItem(level, pos, player);
        }

        return InteractionResult.SUCCESS;
    }

    public static InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        ReceptorPosition candleHit = detectExistingCandleHit(level, pos, hit);
        HitPart part = detectHitPart(pos, hit);

        if (player.isShiftKeyDown()) {
            ReceptorPosition receptor = candleHit != null
                    ? candleHit
                    : part.receptor;

            if (receptor != null
                    && VocoSharedBetweenTableAndReceptorLogic.tryOpenSliderMenu(level, pos, player, receptor)) {
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.SUCCESS;
        }

        if (candleHit != null) {
            return useCandleSlotItem(stack, level, pos, player, hand, candleHit);
        }

        Block candleBlock = candleBlockFromStack(stack);
        if (candleBlock != null && part.isReceptor()) {
            if (!level.isClientSide()) {
                addCandleToSlot(stack, level, pos, player, candleBlock, part.receptor);
            }

            return InteractionResult.SUCCESS;
        }

        if (part.isReceptor()) {
            return useReceptorCornerItem(stack, state, level, pos, player, hand, part.receptor);
        }

        if (part.togglesBasuke) {
            toggleBasuke(level, pos);
            return InteractionResult.SUCCESS;
        }

        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
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

    private static InteractionResult useCandleSlotItem(
            ItemStack stack,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ReceptorPosition receptor
    ) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return InteractionResult.SUCCESS;
        }

        if (tableBe.isCandleLit(receptor)) {
            Block candleBlock = candleBlockFromStack(stack);

            if (candleBlock != null) {
                if (!level.isClientSide()) {
                    addCandleToSlot(stack, level, pos, player, candleBlock, receptor);
                }

                return InteractionResult.SUCCESS;
            }

            if (stack.getItem() instanceof FlintAndPearlItem) {
                return InteractionResult.SUCCESS;
            }

            if (!level.isClientSide()) {
                extinguishCandleSlot(level, pos, player, receptor);
            }

            return InteractionResult.SUCCESS;
        }

        Block candleBlock = candleBlockFromStack(stack);
        if (candleBlock != null) {
            if (!level.isClientSide()) {
                addCandleToSlot(stack, level, pos, player, candleBlock, receptor);
            }

            return InteractionResult.SUCCESS;
        }

        if (stack.getItem() instanceof FlintAndPearlItem) {
            if (!tableBe.hasCandle(receptor)) {
                return InteractionResult.SUCCESS;
            }

            if (!level.isClientSide()) {
                lightCandleSlot(stack, level, pos, player, hand, tableBe, receptor);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult useReceptorCornerItem(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ReceptorPosition receptor
    ) {
        BooleanProperty litProperty = lightProperty(receptor);
        BooleanProperty portalProperty = portalProperty(receptor);

        if (stack.is(ModItems.BANANA_PEARL.get())) {
            if (state.getValue(litProperty)) {
                return InteractionResult.SUCCESS;
            }

            if (!level.isClientSide()) {
                VocoSharedBetweenTableAndReceptorLogic.lightReceptorWithPearl(
                        stack,
                        state,
                        level,
                        pos,
                        player,
                        litProperty
                );

                syncPortalStateFromCandles(level, pos, receptor);
            }

            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.SHEARS)) {
            if (!state.getValue(litProperty)) {
                return InteractionResult.SUCCESS;
            }

            if (!level.isClientSide()) {
                VocoSharedBetweenTableAndReceptorLogic.depleteReceptorPearl(
                        stack,
                        state,
                        level,
                        pos,
                        player,
                        hand,
                        litProperty,
                        portalProperty,
                        receptor
                );

                syncPortalStateFromCandles(level, pos, receptor);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.SUCCESS;
    }

    private static void addCandleToSlot(
            ItemStack stack,
            Level level,
            BlockPos pos,
            Player player,
            Block candleBlock,
            ReceptorPosition receptor
    ) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return;
        }

        if (!tableBe.addCandle(receptor, candleBlock)) {
            return;
        }

        SoundType soundType = candleBlock.defaultBlockState().getSoundType();

        level.playSound(
                null,
                pos,
                soundType.getPlaceSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );

        if (!player.getAbilities().instabuild) {
            stack.consume(1, player);
        }

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        syncPortalStateFromCandles(level, pos, receptor);
    }

    private static void lightCandleSlot(
            ItemStack stack,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            VocoTableBlockEntity tableBe,
            ReceptorPosition receptor
    ) {
        int hexColor = FlintAndPearlItem.getStoredHexOrDefault(stack);

        if (!tableBe.lightCandle(receptor, hexColor)) {
            return;
        }

        level.playSound(
                null,
                pos,
                SoundEvents.FLINTANDSTEEL_USE,
                SoundSource.BLOCKS,
                1.0F,
                level.getRandom().nextFloat() * 0.4F + 0.8F
        );

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        VocoSharedBetweenTableAndReceptorLogic.damageItem(stack, player, hand);
        syncPortalStateFromCandles(level, pos, receptor);
    }

    private static boolean canUseVocoHex(
            Level level,
            BlockPos pos,
            VocoTableBlockEntity tableBe,
            ReceptorPosition receptor,
            int hexColor,
            Player player
    ) {
        int normalized = HexTeleportDirectory.normalizeHex(hexColor);

        if (tableBe.hasOtherLitCandleWithHex(receptor, normalized)) {
            sendHexOccupiedMessage(player, normalized, HexTeleportDirectory.Result.HEX_OCCUPIED);
            return false;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return true;
        }

        String ownerKey = HexTeleportDirectory.vocoTableOwnerKey(
                serverLevel.dimension().location(),
                pos,
                receptor
        );

        HexTeleportDirectory.Result result = HexTeleportDirectory
                .get(serverLevel.getServer())
                .checkVocoEndpoint(ownerKey, normalized);

        if (!result.success()) {
            sendHexOccupiedMessage(player, normalized, result);
            return false;
        }

        return true;
    }

    public static void syncPortalStateFromCandles(Level level, BlockPos pos, ReceptorPosition receptor) {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof VocoTableBlock)) {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof VocoTableBlockEntity tableBe)) {
            return;
        }

        BooleanProperty litProperty = lightProperty(receptor);
        BooleanProperty portalProperty = portalProperty(receptor);

        boolean shouldBePortal = state.getValue(litProperty) && tableBe.isCandleLit(receptor);

        if (shouldBePortal && level instanceof ServerLevel serverLevel) {
            int hexColor = tableBe.getPortalHexColorOrUnset(receptor);

            if (hexColor == VocoSharedBetweenTableAndReceptorLogic.UNSET_HEX_COLOR) {
                shouldBePortal = false;
            } else {
                shouldBePortal = VocoTeleportLogic.syncEndpoint(
                        serverLevel,
                        pos,
                        receptor,
                        true,
                        hexColor
                );

                if (!shouldBePortal) {
                    VocoTeleportLogic.syncEndpoint(
                            serverLevel,
                            pos,
                            receptor,
                            false,
                            VocoSharedBetweenTableAndReceptorLogic.UNSET_HEX_COLOR
                    );
                }
            }
        }

        if (!shouldBePortal && level instanceof ServerLevel serverLevel) {
            VocoTeleportLogic.syncEndpoint(
                    serverLevel,
                    pos,
                    receptor,
                    false,
                    VocoSharedBetweenTableAndReceptorLogic.UNSET_HEX_COLOR
            );
        }

        boolean wasPortal = state.getValue(portalProperty);

        if (wasPortal != shouldBePortal) {
            if (!wasPortal && shouldBePortal) {
                VocoSharedBetweenTableAndReceptorLogic.playPortalAppearSound(level, pos);
            }

            level.setBlock(
                    pos,
                    state.setValue(portalProperty, shouldBePortal),
                    VocoSharedBetweenTableAndReceptorLogic.UPDATE_FLAGS
            );
        }

        if (shouldBePortal) {
            tableBe.activatePortal(receptor);
        } else {
            tableBe.refreshLatestHexFromLitCandles();
        }
    }

    public static boolean breakLookedAtCandle(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player
    ) {
        ReceptorPosition receptor = detectLookedAtCandle(level, pos, player);
        if (receptor == null) {
            return false;
        }

        return breakOneCandle(level, pos, player, receptor);
    }

    private static boolean breakOneCandle(
            Level level,
            BlockPos pos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return false;
        }

        if (!tableBe.hasCandle(receptor)) {
            return false;
        }

        Block candleBlock = tableBe.getCandleBlock(receptor);
        int candleCountBefore = tableBe.getCandleCount(receptor);

        ItemStack removed = tableBe.removeOneCandle(receptor);
        if (removed.isEmpty()) {
            return false;
        }

        Vec3 dropPos = VocoTableCandleGeometry
                .dropPosition(receptor, candleCountBefore)
                .add(pos.getX(), pos.getY(), pos.getZ());

        ItemEntity item = new ItemEntity(
                level,
                dropPos.x,
                dropPos.y,
                dropPos.z,
                removed
        );

        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);

        SoundType soundType = candleBlock == null
                ? SoundType.CANDLE
                : candleBlock.defaultBlockState().getSoundType();

        level.playSound(
                null,
                dropPos.x,
                dropPos.y,
                dropPos.z,
                soundType.getBreakSound(),
                SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        syncPortalStateFromCandles(level, pos, receptor);

        return true;
    }

    @Nullable
    private static Block candleBlockFromStack(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }

        Block block = blockItem.getBlock();

        if (block instanceof PearlCandleBlock pearlCandleBlock) {
            block = pearlCandleBlock.getVanillaCandleBlock();
        }

        return block instanceof CandleBlock ? block : null;
    }

    private static void toggleBasuke(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }

        if (level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe) {
            tableBe.toggleBasuke(serverLevel);
        }
    }

    private static boolean removeDisplayedItem(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)
                || !tableBe.hasDisplayedItem()) {
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
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)
                || tableBe.hasDisplayedItem()) {
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

    public static void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return;
        }

        int totalLitCandlePositions = countLitCandleParticlePositions(tableBe);
        if (totalLitCandlePositions <= 0) {
            return;
        }

        int spreadTicks = particleSpreadTicks(totalLitCandlePositions);
        long gameTime = level.getGameTime();

        int spawnedPearlFlames = 0;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (!tableBe.isCandleLit(receptor)) {
                continue;
            }

            Vec3[] offsets = VocoTableCandleGeometry.particleOffsets(
                    receptor,
                    tableBe.getCandleCount(receptor)
            );

            if (offsets.length == 0) {
                continue;
            }

            int hexColor = tableBe.getCandleHexColorOrFallback(receptor);

            for (int candleIndex = 0; candleIndex < offsets.length; candleIndex++) {
                if (spawnedPearlFlames >= MAX_PEARL_FLAME_PARTICLES_PER_ANIMATE_TICK) {
                    return;
                }

                if (!isScheduledFlameTick(gameTime, receptor, candleIndex, spreadTicks)) {
                    continue;
                }

                if (random.nextFloat() > PEARL_FLAME_SPAWN_CHANCE) {
                    continue;
                }

                Vec3 particlePos = offsets[candleIndex].add(
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                );

                addPearlCandleParticlesAndSound(
                        level,
                        particlePos,
                        random,
                        hexColor
                );

                spawnedPearlFlames++;
            }
        }
    }

    private static void addPearlCandleParticlesAndSound(
            Level level,
            Vec3 particlePos,
            RandomSource random,
            int hexColor
    ) {
        if (random.nextFloat() < SMOKE_PARTICLE_CHANCE) {
            level.addParticle(
                    ParticleTypes.SMOKE,
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    0.0D,
                    0.0D,
                    0.0D
            );
        }

        if (random.nextFloat() < AMBIENT_SOUND_CHANCE) {
            level.playLocalSound(
                    particlePos.x,
                    particlePos.y,
                    particlePos.z,
                    SoundEvents.CANDLE_AMBIENT,
                    SoundSource.BLOCKS,
                    0.55F + random.nextFloat() * 0.35F,
                    0.65F + random.nextFloat() * 0.45F,
                    false
            );
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

    private static void extinguishCandleSlot(
            Level level,
            BlockPos pos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return;
        }

        if (!tableBe.isCandleLit(receptor)) {
            return;
        }

        for (Vec3 offset : VocoTableCandleGeometry.particleOffsets(receptor, tableBe.getCandleCount(receptor))) {
            level.addParticle(
                    ParticleTypes.SMOKE,
                    pos.getX() + offset.x,
                    pos.getY() + offset.y,
                    pos.getZ() + offset.z,
                    0.0D,
                    0.1D,
                    0.0D
            );
        }

        tableBe.extinguishCandle(receptor);

        level.playSound(
                null,
                pos,
                SoundEvents.CANDLE_EXTINGUISH,
                SoundSource.BLOCKS,
                1.0F,
                1.0F
        );

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        syncPortalStateFromCandles(level, pos, receptor);
    }

    private static void sendHexOccupiedMessage(
            Player player,
            int hexColor,
            HexTeleportDirectory.Result result
    ) {
        String reason = switch (result) {
            case HEX_OCCUPIED -> "already occupied";
            case INVALID_OWNER -> "invalid";
            case REGISTERED, UPDATED, WAITING_FOR_SECOND_PORTAL, LINKED_TO_EXISTING_PORTAL, ALREADY_REGISTERED -> "available";
        };

        player.displayClientMessage(
                Component.literal(
                        "Pearl hex #" + HexTeleportDirectory.toHex(hexColor) + " is " + reason + "."
                ),
                true
        );
    }

    private static int countLitCandleParticlePositions(VocoTableBlockEntity tableBe) {
        int total = 0;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (!tableBe.isCandleLit(receptor)) {
                continue;
            }

            total += VocoTableCandleGeometry
                    .particleOffsets(receptor, tableBe.getCandleCount(receptor))
                    .length;
        }

        return total;
    }

    private static int particleSpreadTicks(int totalLitCandlePositions) {
        if (totalLitCandlePositions <= 4) {
            return 1;
        }

        if (totalLitCandlePositions <= 8) {
            return 2;
        }

        return 4;
    }

    private static boolean isScheduledFlameTick(
            long gameTime,
            ReceptorPosition receptor,
            int candleIndex,
            int spreadTicks
    ) {
        if (spreadTicks <= 1) {
            return true;
        }

        int offset = receptor.id() * 7 + candleIndex * 3;
        return Math.floorMod(gameTime + offset, spreadTicks) == 0;
    }

    public static VoxelShape shape(BlockGetter level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return BASE_SHAPE;
        }

        int northEast = shapeCount(tableBe, ReceptorPosition.NORTH_EAST);
        int northWest = shapeCount(tableBe, ReceptorPosition.NORTH_WEST);
        int southEast = shapeCount(tableBe, ReceptorPosition.SOUTH_EAST);
        int southWest = shapeCount(tableBe, ReceptorPosition.SOUTH_WEST);

        int key = shapeKey(northEast, northWest, southEast, southWest);

        VoxelShape cached = SHAPE_CACHE[key];
        if (cached != null) {
            return cached;
        }

        VoxelShape shape = BASE_SHAPE;

        if (northEast > 0) {
            shape = Shapes.or(shape, VocoTableCandleGeometry.shape(ReceptorPosition.NORTH_EAST, northEast));
        }

        if (northWest > 0) {
            shape = Shapes.or(shape, VocoTableCandleGeometry.shape(ReceptorPosition.NORTH_WEST, northWest));
        }

        if (southEast > 0) {
            shape = Shapes.or(shape, VocoTableCandleGeometry.shape(ReceptorPosition.SOUTH_EAST, southEast));
        }

        if (southWest > 0) {
            shape = Shapes.or(shape, VocoTableCandleGeometry.shape(ReceptorPosition.SOUTH_WEST, southWest));
        }

        SHAPE_CACHE[key] = shape;
        return shape;
    }

    private static int shapeCount(VocoTableBlockEntity tableBe, ReceptorPosition receptor) {
        if (!tableBe.hasCandle(receptor)) {
            return 0;
        }

        return Math.max(1, Math.min(4, tableBe.getCandleCount(receptor)));
    }

    private static int shapeKey(int northEast, int northWest, int southEast, int southWest) {
        return northEast
                + northWest * 5
                + southEast * 25
                + southWest * 125;
    }

    @Nullable
    private static ReceptorPosition detectExistingCandleHit(Level level, BlockPos pos, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return null;
        }

        Vec3 location = hit.getLocation();

        double x = (location.x - pos.getX()) * 16.0D;
        double y = (location.y - pos.getY()) * 16.0D;
        double z = (location.z - pos.getZ()) * 16.0D;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (!tableBe.hasCandle(receptor)) {
                continue;
            }

            if (VocoTableCandleGeometry
                    .hitBox(receptor, tableBe.getCandleCount(receptor))
                    .contains(x, y, z)) {
                return receptor;
            }
        }

        return null;
    }

    @Nullable
    private static ReceptorPosition detectLookedAtCandle(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return null;
        }

        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getLookAngle().scale(player.blockInteractionRange()));

        Vec3 localStart = new Vec3(
                (start.x - pos.getX()) * 16.0D,
                (start.y - pos.getY()) * 16.0D,
                (start.z - pos.getZ()) * 16.0D
        );

        Vec3 localEnd = new Vec3(
                (end.x - pos.getX()) * 16.0D,
                (end.y - pos.getY()) * 16.0D,
                (end.z - pos.getZ()) * 16.0D
        );

        ReceptorPosition bestReceptor = null;
        double bestDistance = Double.POSITIVE_INFINITY;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (!tableBe.hasCandle(receptor)) {
                continue;
            }

            double distance = rayHitDistance(
                    localStart,
                    localEnd,
                    VocoTableCandleGeometry.hitBox(receptor, tableBe.getCandleCount(receptor))
            );

            if (!Double.isNaN(distance) && distance < bestDistance) {
                bestDistance = distance;
                bestReceptor = receptor;
            }
        }

        return bestReceptor;
    }

    private static double rayHitDistance(
            Vec3 start,
            Vec3 end,
            VocoTableCandleGeometry.Box box
    ) {
        Vec3 direction = end.subtract(start);

        double tMin = 0.0D;
        double tMax = 1.0D;

        double[] resultX = clipAxis(start.x, direction.x, box.minX(), box.maxX(), tMin, tMax);
        if (resultX == null) return Double.NaN;
        tMin = resultX[0];
        tMax = resultX[1];

        double[] resultY = clipAxis(start.y, direction.y, box.minY(), box.maxY(), tMin, tMax);
        if (resultY == null) return Double.NaN;
        tMin = resultY[0];
        tMax = resultY[1];

        double[] resultZ = clipAxis(start.z, direction.z, box.minZ(), box.maxZ(), tMin, tMax);
        if (resultZ == null) return Double.NaN;

        return resultZ[0];
    }

    @Nullable
    private static double[] clipAxis(
            double start,
            double direction,
            double min,
            double max,
            double tMin,
            double tMax
    ) {
        if (Math.abs(direction) < 1.0E-7D) {
            return start >= min && start <= max
                    ? new double[] {tMin, tMax}
                    : null;
        }

        double inv = 1.0D / direction;
        double t1 = (min - start) * inv;
        double t2 = (max - start) * inv;

        if (t1 > t2) {
            double swap = t1;
            t1 = t2;
            t2 = swap;
        }

        tMin = Math.max(tMin, t1);
        tMax = Math.min(tMax, t2);

        return tMin <= tMax
                ? new double[] {tMin, tMax}
                : null;
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
        NONE(null, false),

        RECEPTOR_NORTH_EAST(ReceptorPosition.NORTH_EAST, false),
        RECEPTOR_NORTH_WEST(ReceptorPosition.NORTH_WEST, false),
        RECEPTOR_SOUTH_EAST(ReceptorPosition.SOUTH_EAST, false),
        RECEPTOR_SOUTH_WEST(ReceptorPosition.SOUTH_WEST, false),

        DIALER_NORTH(null, true),
        DIALER_EAST(null, true),
        DIALER_SOUTH(null, true),
        DIALER_WEST(null, true);

        @Nullable
        private final ReceptorPosition receptor;
        private final boolean togglesBasuke;

        HitPart(@Nullable ReceptorPosition receptor, boolean togglesBasuke) {
            this.receptor = receptor;
            this.togglesBasuke = togglesBasuke;
        }

        private boolean isReceptor() {
            return this.receptor != null;
        }
    }
}