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
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.particle.tinted.ProfileTintParticles;

public final class VocoTableCandleLogic {
    private static final int MAX_PEARL_FLAME_PARTICLES_PER_ANIMATE_TICK = 6;
    private static final float PEARL_FLAME_SPAWN_CHANCE = 0.90F;
    private static final float SMOKE_PARTICLE_CHANCE = 0.14F;
    private static final float AMBIENT_SOUND_CHANCE = 0.025F;

    private VocoTableCandleLogic() {}

    public static void addCandleToSlot(
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

    public static void lightCandleSlot(
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
        VocoReceptorLogic.damageItem(stack, player, hand);
        syncPortalStateFromCandles(level, pos, receptor);
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

        BooleanProperty litProperty = VocoTableBlock.lightProperty(receptor);
        BooleanProperty portalProperty = VocoTableBlock.portalProperty(receptor);

        boolean shouldBePortal = state.getValue(litProperty) && tableBe.isCandleLit(receptor);

        if (shouldBePortal && level instanceof ServerLevel serverLevel) {
            int hexColor = tableBe.getPortalHexColorOrUnset(receptor);

            if (hexColor == VocoReceptorLogic.UNSET_HEX_COLOR) {
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
                            VocoReceptorLogic.UNSET_HEX_COLOR
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
                    VocoReceptorLogic.UNSET_HEX_COLOR
            );
        }

        boolean wasPortal = state.getValue(portalProperty);

        if (wasPortal != shouldBePortal) {
            if (!wasPortal && shouldBePortal) {
                VocoReceptorLogic.playPortalAppearSound(level, pos);
            } else if (wasPortal) {
                VocoReceptorLogic.playPortalDisappearSound(level, pos);
            }

            level.setBlock(
                    pos,
                    state.setValue(portalProperty, shouldBePortal),
                    VocoReceptorLogic.UPDATE_FLAGS
            );
        }

        if (shouldBePortal) {
            tableBe.activatePortal(receptor);
        } else {
            tableBe.refreshLatestHexFromLitCandles();
        }
    }

    public static boolean breakLookedAtCandle(
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

        Vec3 dropPos = VocoTableCandleVoxelShapes
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

    public static void animateTick(Level level, BlockPos pos, RandomSource random) {
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

            Vec3[] offsets = VocoTableCandleVoxelShapes.particleOffsets(
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

    public static void extinguishCandleSlot(
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

    @Nullable
    public static ReceptorPosition detectExistingCandleHit(Level level, BlockPos pos, BlockHitResult hit) {
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

            if (VocoTableCandleHitboxes
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

    private static int countLitCandleParticlePositions(VocoTableBlockEntity tableBe) {
        int total = 0;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (!tableBe.isCandleLit(receptor)) {
                continue;
            }

            total += VocoTableCandleVoxelShapes
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
}