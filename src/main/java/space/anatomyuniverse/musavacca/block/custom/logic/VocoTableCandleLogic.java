package space.anatomyuniverse.musavacca.block.custom.logic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.item.custom.PearlIgniter;
import space.anatomyuniverse.musavacca.basuke.summon.BasukeSummon;
import space.anatomyuniverse.musavacca.particle.tinted.OriginalCandleParticles;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public final class VocoTableCandleLogic {
    private VocoTableCandleLogic() {}

    public static InteractionResult place(Level level, BlockPos pos, ReceptorPosition corner, Player player, InteractionHand hand,
                                          ItemStack stack, BlockItem item, BlockHitResult hit) {
        if (player.isSpectator() || !player.getAbilities().mayBuild || !(level.getBlockEntity(pos) instanceof VocoTableBlockEntity table)) {
            return InteractionResult.FAIL;
        }
        var current = table.candle(corner);
        VocoTableCandle next;
        if (current == null) {
            Vec3 offset = VocoTableCandleVoxelShapes.renderTranslation(corner);
            Vec3 localHit = new Vec3(hit.getLocation().x - offset.x, pos.getY() + 1, hit.getLocation().z - offset.z);
            var top = new BlockHitResult(localHit, Direction.UP, pos, false);
            next = VocoTableCandle.place(item, new BlockPlaceContext(player, hand, stack, top));
        } else {
            next = current.addOne(stack);
        }
        if (next == null) return InteractionResult.FAIL;
        if (!level.isClientSide()) {
            table.setCandle(corner, next);
            if (!player.getAbilities().instabuild) stack.consume(1, player);
            SoundType sound = next.state().getSoundType();
            level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult ignite(Level level, BlockPos pos, ReceptorPosition corner, Player player, InteractionHand hand, ItemStack stack) {
        boolean pearl = PearlIgniter.is(stack);
        if (!pearl && !stack.is(Items.FLINT_AND_STEEL)) return InteractionResult.PASS;
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity table)) return InteractionResult.PASS;
        var candle = table.candle(corner);
        if (candle == null) return InteractionResult.PASS;
        if (candle.lit() || player.isSpectator() || !player.getAbilities().mayBuild) return InteractionResult.FAIL;
        if (!level.isClientSide()) {
            int hex = pearl ? PearlIgniter.getStoredHexOrDefault(stack) : MusavaccaTints.NO_TINT;
            table.setCandle(corner, candle.withFlame(true, hex));
            playFlintAndSteelEffects(level, player, pos);
            VocoReceptorLogic.damageItem(stack, player, hand);
            BasukeSummon.trySummonFromVocoTable(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    public static void syncPortalStateFromCandles(Level level, BlockPos pos, ReceptorPosition receptor) {
        if (level == null || level.isClientSide()) return;
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
            level.setBlock(pos, state.setValue(portalProperty, sync.portal()), VocoReceptorLogic.UPDATE_FLAGS);
            PearlSlotIgnition.playPortalStateChangeSound(level, pos, wasPortal, sync.portal());
        }
        if (sync.portal()) {
            tableBe.activatePortal(receptor);
        } else {
            tableBe.refreshLatestHexFromLitCandles();
        }
    }

    private static Sync syncEndpoint(Level level, BlockPos pos, ReceptorPosition receptor, boolean receptorLit, VocoTableBlockEntity tableBe) {
        if (!receptorLit || !tableBe.isPearlCandleLit(receptor)) {
            removeEndpoint(level, pos, receptor);
            return Sync.INACTIVE;
        }
        int hexColor = tableBe.getPortalHexColorOrNoTint(receptor);
        if (hexColor == MusavaccaTints.NO_TINT) {
            removeEndpoint(level, pos, receptor);
            return Sync.INACTIVE;
        }
        if (!(level instanceof ServerLevel serverLevel)) return Sync.INACTIVE;
        VocoTeleportLogic.SyncResult result = VocoTeleportLogic.syncEndpointDetailed(serverLevel, pos, receptor, true, hexColor);
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
            VocoTeleportLogic.syncEndpointDetailed(serverLevel, pos, receptor, false, MusavaccaTints.NO_TINT);
        }
    }

    public static boolean breakLookedAtCandle(Level level, BlockPos pos, Player player) {
        if (player.isSpectator() || !player.getAbilities().mayBuild || !(level.getBlockEntity(pos) instanceof VocoTableBlockEntity table)) return false;
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(player.blockInteractionRange()));
        var context = CollisionContext.of(player);
        var hit = level.getBlockState(pos).getShape(level, pos, context).clip(eye, end, pos);
        if (hit == null) return false;
        var corner = detectExistingCandleHit(level, pos, hit, player);
        if (corner == null) return false;
        var candle = table.candle(corner);
        Vec3 drop = VocoTableCandleVoxelShapes.dropPosition(level, pos, corner, candle);
        table.setCandle(corner, candle.removeOne());
        if (!player.getAbilities().instabuild) {
            ItemEntity item = new ItemEntity(level, drop.x, drop.y, drop.z, candle.item().copyWithCount(1));
            item.setDefaultPickUpDelay();
            level.addFreshEntity(item);
        }
        SoundType sound = candle.state().getSoundType();
        level.playSound(null, drop.x, drop.y, drop.z, sound.getBreakSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F,
                sound.getPitch() * 0.8F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        return true;
    }

    public static void animateTick(Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide() || !(level.getBlockEntity(pos) instanceof VocoTableBlockEntity table)) return;
        var ground = pos.above();
        for (var corner : ReceptorPosition.values()) {
            var candle = table.candle(corner);
            if (candle != null && candle.lit()) {
                OriginalCandleParticles.animateMounted(candle.state(), level, ground, random, candle.hex(), VocoTableCandleVoxelShapes.renderTranslation(corner));
            }
        }
    }

    public static void extinguishCandleSlot(Level level, BlockPos pos, Player player, ReceptorPosition corner) {
        if (!(level instanceof ServerLevel server) || !(level.getBlockEntity(pos) instanceof VocoTableBlockEntity table)) return;
        var candle = table.candle(corner);
        if (candle == null || !candle.lit()) return;
        var outline = VocoTableCandleVoxelShapes.shape(candle, level, pos, corner, CollisionContext.empty(), false);
        table.setCandle(corner, candle.withFlame(false, MusavaccaTints.NO_TINT));
        if (!outline.isEmpty()) {
            var box = outline.bounds();
            var center = box.getCenter();
            server.sendParticles(ParticleTypes.SMOKE, pos.getX() + center.x, pos.getY() + box.maxY, pos.getZ() + center.z, 1, 0, 0, 0, 0);
        }
        level.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    @Nullable
    public static ReceptorPosition detectExistingCandleHit(Level level, BlockPos pos, BlockHitResult hit, Player player) {
        if (!(level.getBlockEntity(pos) instanceof VocoTableBlockEntity table)) return null;
        Vec3 local = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        var context = CollisionContext.of(player);
        ReceptorPosition closest = null;
        double best = Double.POSITIVE_INFINITY;
        for (var corner : ReceptorPosition.values()) {
            var candle = table.candle(corner);
            if (candle == null) continue;
            var shape = VocoTableCandleVoxelShapes.shape(candle, level, pos, corner, context, false);
            if (shape.isEmpty() || shape.closestPointTo(local).orElseThrow().distanceToSqr(local) > 1.0E-8D) continue;
            double distance = shape.bounds().getCenter().distanceToSqr(local);
            if (distance < best) {
                best = distance;
                closest = corner;
            }
        }
        return closest;
    }

    private static void playFlintAndSteelEffects(Level level, Player player, BlockPos pos) {
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    private record Sync(boolean portal) {
        private static final Sync ACTIVE = new Sync(true);
        private static final Sync QUEUED = new Sync(false);
        private static final Sync INACTIVE = new Sync(false);
    }
}
