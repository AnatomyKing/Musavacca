// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/block/custom/logic/VocoTableCandleLogic.java
package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;

public final class VocoTableCandleLogic {
    private static final int MAX_PEARL_FLAME_PARTICLES_PER_ANIMATE_TICK = 6;
    private static final float PEARL_FLAME_SPAWN_CHANCE = 0.90F;

    private VocoTableCandleLogic() {}

    public static void addCandleToSlot(
            ItemStack stack,
            Level level,
            BlockPos pos,
            Player player,
            Block candleBlock,
            ReceptorPosition receptor
    ) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)
                || !tableBe.addCandle(receptor, candleBlock)) {
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

    public static void lightCandleSlot(
            ItemStack stack,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            VocoTableBlockEntity tableBe,
            ReceptorPosition receptor
    ) {
        if (!tableBe.lightPearlCandle(receptor, FlintAndPearlItem.getStoredHexOrDefault(stack))) {
            return;
        }

        playFlintAndSteelEffects(level, player, pos);
        VocoReceptorLogic.damageItem(stack, player, hand);
        syncPortalStateFromCandles(level, pos, receptor);
    }

    public static void lightVanillaCandleSlot(
            ItemStack stack,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            VocoTableBlockEntity tableBe,
            ReceptorPosition receptor
    ) {
        if (!stack.is(Items.FLINT_AND_STEEL) || !tableBe.lightVanillaCandle(receptor)) {
            return;
        }

        playFlintAndSteelEffects(level, player, pos);
        VocoReceptorLogic.damageItem(stack, player, hand);
        syncPortalStateFromCandles(level, pos, receptor);
    }

    public static void syncPortalStateFromCandles(Level level, BlockPos pos, ReceptorPosition receptor) {
        if (level == null || level.isClientSide()) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        BlockEntity be = level.getBlockEntity(pos);

        if (!(state.getBlock() instanceof VocoTableBlock)
                || !(be instanceof VocoTableBlockEntity tableBe)) {
            return;
        }

        BooleanProperty litProperty = VocoTableBlock.lightProperty(receptor);
        BooleanProperty portalProperty = VocoTableBlock.portalProperty(receptor);

        Sync sync = syncEndpoint(level, pos, receptor, state.getValue(litProperty), tableBe);
        boolean wasPortal = state.getValue(portalProperty);

        if (wasPortal != sync.portal()) {
            if (sync.portal()) {
                VocoReceptorLogic.playPortalAppearSound(level, pos);
            } else {
                VocoReceptorLogic.playPortalDisappearSound(level, pos);
            }

            level.setBlock(
                    pos,
                    state.setValue(portalProperty, sync.portal()),
                    VocoReceptorLogic.UPDATE_FLAGS
            );
        }

        if (sync.portal()) {
            tableBe.activatePortal(receptor);
        } else {
            tableBe.refreshLatestHexFromLitCandles();
        }
    }

    private static Sync syncEndpoint(
            Level level,
            BlockPos pos,
            ReceptorPosition receptor,
            boolean receptorLit,
            VocoTableBlockEntity tableBe
    ) {
        if (!receptorLit || !tableBe.isPearlCandleLit(receptor)) {
            removeEndpoint(level, pos, receptor);
            return Sync.INACTIVE;
        }

        int hexColor = tableBe.getPortalHexColorOrUnset(receptor);
        if (hexColor == VocoReceptorLogic.UNSET_HEX_COLOR) {
            removeEndpoint(level, pos, receptor);
            return Sync.INACTIVE;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return Sync.INACTIVE;
        }

        VocoTeleportLogic.SyncResult result = VocoTeleportLogic.syncEndpointDetailed(
                serverLevel,
                pos,
                receptor,
                true,
                hexColor
        );

        return switch (result) {
            case ACTIVE -> Sync.ACTIVE;
            case QUEUED -> Sync.QUEUED;
            case INACTIVE -> {
                removeEndpoint(level, pos, receptor);
                yield Sync.INACTIVE;
            }
        };
    }

    private static void removeEndpoint(Level level, BlockPos pos, ReceptorPosition receptor) {
        if (level instanceof ServerLevel serverLevel) {
            VocoTeleportLogic.syncEndpointDetailed(
                    serverLevel,
                    pos,
                    receptor,
                    false,
                    VocoReceptorLogic.UNSET_HEX_COLOR
            );
        }
    }

    public static boolean breakLookedAtCandle(Level level, BlockPos pos, Player player) {
        ReceptorPosition receptor = detectLookedAtCandle(level, pos, player);
        return receptor != null && breakOneCandle(level, pos, player, receptor);
    }

    private static boolean breakOneCandle(
            Level level,
            BlockPos pos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)
                || !tableBe.hasCandle(receptor)) {
            return false;
        }

        Block candleBlock = tableBe.getCandleBlock(receptor);
        int candleCountBefore = tableBe.getCandleCount(receptor);
        ItemStack removed = tableBe.removeOneCandle(receptor);

        if (removed.isEmpty()) {
            return false;
        }

        Vec3 dropPos = VocoTableCandleVoxelShapes
                .dropPosition(receptor, candleCountBefore)
                .add(pos.getX(), pos.getY(), pos.getZ());

        ItemEntity item = new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z, removed);
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

    public static void animateTick(Level level, BlockPos pos, RandomSource random) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return;
        }

        int spreadTicks = particleSpreadTicks(countLitCandleParticlePositions(tableBe));
        if (spreadTicks <= 0) {
            return;
        }

        long gameTime = level.getGameTime();
        int spawnedPearlFlames = 0;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (!tableBe.isCandleLit(receptor)) {
                continue;
            }

            Vec3[] offsets = VocoTableCandleVoxelShapes.particleOffsets(
                    receptor,
                    tableBe.getCandleCount(receptor)
            );

            boolean pearlLit = tableBe.isPearlCandleLit(receptor);
            int hexColor = tableBe.getCandleHexColorOrFallback(receptor);

            for (int candleIndex = 0; candleIndex < offsets.length; candleIndex++) {
                if (!isScheduledFlameTick(gameTime, receptor, candleIndex, spreadTicks)) {
                    continue;
                }

                Vec3 particlePos = offsets[candleIndex].add(pos.getX(), pos.getY(), pos.getZ());

                if (!pearlLit) {
                    CandleParticleEffects.spawnVanilla(level, random, particlePos);
                    continue;
                }

                if (spawnedPearlFlames >= MAX_PEARL_FLAME_PARTICLES_PER_ANIMATE_TICK) {
                    return;
                }

                if (random.nextFloat() <= PEARL_FLAME_SPAWN_CHANCE) {
                    CandleParticleEffects.spawnPearlTableStyle(level, random, particlePos, hexColor);
                    spawnedPearlFlames++;
                }
            }
        }
    }

    public static void extinguishCandleSlot(
            Level level,
            BlockPos pos,
            Player player,
            ReceptorPosition receptor
    ) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)
                || !tableBe.isCandleLit(receptor)) {
            return;
        }

        for (Vec3 offset : VocoTableCandleVoxelShapes.particleOffsets(receptor, tableBe.getCandleCount(receptor))) {
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

        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

        syncPortalStateFromCandles(level, pos, receptor);
    }

    @Nullable
    public static ReceptorPosition detectExistingCandleHit(Level level, BlockPos pos, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return null;
        }

        Vec3 local = VocoHitboxes.local16(pos, hit);

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (tableBe.hasCandle(receptor)
                    && VocoTableCandleHitboxes.hitBox(receptor, tableBe.getCandleCount(receptor)).contains(local)) {
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

        Vec3 localStart = VocoHitboxes.local16(pos, player.getEyePosition());
        Vec3 localEnd = VocoHitboxes.local16(
                pos,
                player.getEyePosition().add(player.getLookAngle().scale(player.blockInteractionRange()))
        );

        ReceptorPosition bestReceptor = null;
        double bestDistance = Double.POSITIVE_INFINITY;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (!tableBe.hasCandle(receptor)) {
                continue;
            }

            double distance = VocoTableCandleHitboxes.rayHitDistance(
                    localStart,
                    localEnd,
                    VocoTableCandleHitboxes.hitBox(receptor, tableBe.getCandleCount(receptor))
            );

            if (!Double.isNaN(distance) && distance < bestDistance) {
                bestDistance = distance;
                bestReceptor = receptor;
            }
        }

        return bestReceptor;
    }

    private static void playFlintAndSteelEffects(Level level, Player player, BlockPos pos) {
        level.playSound(
                null,
                pos,
                SoundEvents.FLINTANDSTEEL_USE,
                SoundSource.BLOCKS,
                1.0F,
                level.getRandom().nextFloat() * 0.4F + 0.8F
        );

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    private static int countLitCandleParticlePositions(VocoTableBlockEntity tableBe) {
        int total = 0;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (tableBe.isCandleLit(receptor)) {
                total += VocoTableCandleVoxelShapes
                        .particleOffsets(receptor, tableBe.getCandleCount(receptor))
                        .length;
            }
        }

        return total;
    }

    private static int particleSpreadTicks(int totalLitCandlePositions) {
        if (totalLitCandlePositions <= 0) return 0;
        if (totalLitCandlePositions <= 4) return 1;
        if (totalLitCandlePositions <= 8) return 2;
        return 4;
    }

    private static boolean isScheduledFlameTick(
            long gameTime,
            ReceptorPosition receptor,
            int candleIndex,
            int spreadTicks
    ) {
        return spreadTicks <= 1
                || Math.floorMod(gameTime + receptor.id() * 7 + candleIndex * 3, spreadTicks) == 0;
    }

    private record Sync(boolean portal) {
        private static final Sync ACTIVE = new Sync(true);
        private static final Sync QUEUED = new Sync(false);
        private static final Sync INACTIVE = new Sync(false);
    }
}