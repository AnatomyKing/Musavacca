package space.anatomyuniverse.musavacca.block.custom.logic;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.teleport.HexTeleportResolver;
//? if >=26.2 {
/*import net.minecraft.world.entity.EntityTypes;
*///?} else {
import net.minecraft.world.entity.EntityType;
//?}
//? if >=1.21.6 {
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
//?}

public final class VocoDustRide extends Display.BlockDisplay {
    private static final Map<ServerPlayer, VocoDustRide> RIDES = new IdentityHashMap<>();
    public static final float TRAIL_EYE_HEIGHT = 0.70F;
    private static final int ARRIVAL_TICKS = 4;
    private final ServerLevel world;
    private final ServerPlayer rider;
    private final VocoDustRoute.Node source;
    private final ArrayList<VocoDustRoute.Node> path = new ArrayList<>();
    private final Set<VocoDustRoute.Node> visited = new HashSet<>();
    private final VocoDustReturn departure;
    private final VocoDustInputs controls;
    private final int startTick;
    private final Vec3 entry;
    private final double eyeDrop;
    private VocoDustRoute.Node pending;
    private VocoDustRoute.Node destination;
    private int arrivalTick;
    private boolean reversing;
    private boolean onStep;

    private VocoDustRide(ServerLevel level, VocoDustRoute.Node source, VocoDustReturn departure, VocoDustInputs controls, ServerPlayer player) {
        //? if >=26.2 {
        /*super(EntityTypes.BLOCK_DISPLAY, level);
        *///?} else {
        super(EntityType.BLOCK_DISPLAY, level);
        //?}
        this.world = level;
        this.rider = player;
        this.source = source;
        this.path.add(source);
        this.visited.add(source);
        this.departure = departure;
        this.controls = controls;
        this.startTick = player.tickCount;
        this.entry = boardingPoint(player, nodePosition(source).add(0, TRAIL_EYE_HEIGHT, 0));
        this.eyeDrop = player.getEyeHeight() - TRAIL_EYE_HEIGHT;
        CompoundTag settings = new CompoundTag();
        settings.putInt(TAG_POS_ROT_INTERPOLATION_DURATION, 1);
        //? if >=1.21.6 {
        readAdditionalSaveData(TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), settings));
        //?} else {
        /*readAdditionalSaveData(settings);
        *///?}
        setUUID(cameraId(player));
        setPos(departure.pos());
        setYRot(departure.yaw());
        setXRot(departure.pitch());
    }

    private static Vec3 boardingPoint(ServerPlayer player, Vec3 cap) {
        Vec3 start = player.getEyePosition();
        var hit = player.level().clip(new ClipContext(start, cap, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double travel = Math.max(0, start.distanceTo(hit.getLocation()) - 0.10);
        return player.position().add(cap.subtract(start).normalize().scale(travel));
    }

    private Vec3 nodePosition(VocoDustRoute.Node node) {
        BlockPos pos = node.pos();
        if (!node.isReceptor()) return Vec3.atBottomCenterOf(pos);
        double x = 0.5D;
        double z = 0.5D;
        if (world.getBlockState(pos).getBlock() instanceof VocoTableBlock) {
            x = VocoDustRoute.dx(node.receptor()) > 0 ? 0.8125D : 0.1875D;
            z = VocoDustRoute.dz(node.receptor()) > 0 ? 0.8125D : 0.1875D;
        }
        return new Vec3(pos.getX() + x, pos.getY() + 0.875D - TRAIL_EYE_HEIGHT, pos.getZ() + z);
    }

    @Override
    public boolean broadcastToPlayer(ServerPlayer viewer) {
        return viewer == rider && super.broadcastToPlayer(viewer);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    private static UUID cameraId(Player player) {
        return UUID.nameUUIDFromBytes(("musavacca:voco_dust_camera/" + player.getStringUUID()).getBytes(StandardCharsets.UTF_8));
    }

    public static boolean isCamera(Entity entity, Player player) {
        return entity instanceof BlockDisplay && entity.getUUID().equals(cameraId(player));
    }

    public static boolean active(ServerPlayer player) {
        return RIDES.containsKey(player);
    }

    public static void start(ServerPlayer player, ServerLevel level, BlockPos pos, ReceptorPosition receptor) {
        if (player instanceof FakePlayer || active(player) || player.isSpectator() || player.isPassenger() || player.isSleeping() || !player.isAlive()) return;
        var source = new VocoDustRoute.Node(pos, receptor);
        if (VocoDustRoute.neighbors(level, source).isEmpty()) return;
        var controls = VocoDustInputs.install(player);
        if (controls == null) return;
        var departure = VocoDustReturn.capture(player, level);
        var camera = new VocoDustRide(level, source, departure, controls, player);
        HexTeleportResolver.keepChunkLoaded(level, pos);
        if (!level.addFreshEntity(camera)) return;
        RIDES.put(player, camera);
        departure.save(player);
        controls.setRiding(true);
        if (!player.setGameMode(GameType.SPECTATOR) || !player.isSpectator()) {
            departure.restoreMode(player, true);
            camera.release();
            return;
        }
        player.closeContainer();
        player.stopUsingItem();
        player.setShiftKeyDown(false);
        player.setPortalCooldown();
        player.setCamera(camera);
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        VocoDustRide ride = RIDES.get(player);
        if (ride != null) ride.advance();
    }

    private void advance() {
        if (rider.level() != world || !rider.isAlive() || !rider.isSpectator() || rider.getCamera() != this
                || isRemoved()) {
            finish(false);
            return;
        }
        int age = rider.tickCount - startTick;
        if (age < 1) return;
        if (age == 1) {
            setPos(entry);
            return;
        }
        if (age <= 3) {
            setPose(Pose.CROUCHING);
            setPos(entry.add(0, eyeDrop, 0).lerp(nodePosition(source), age == 2 ? 0.5D : 1.0D));
            return;
        }
        if (destination != null) {
            if (!VocoDustRoute.isLit(world, destination)) destination = null;
            else {
                if (rider.tickCount - arrivalTick >= ARRIVAL_TICKS) finish(true);
                return;
            }
        }
        if (pending == null) {
            pending = chooseNext();
            reversing = pending == null;
            if (reversing) {
                if (path.size() == 1) {
                    finish(false);
                    return;
                }
                pending = path.get(path.size() - 2);
            }
        }
        VocoDustRoute.Node current = path.getLast();
        BlockPos from = current.pos();
        BlockPos to = pending.pos();
        if (!onStep && ((from.getX() >> 4) != (to.getX() >> 4) || (from.getZ() >> 4) != (to.getZ() >> 4))) {
            HexTeleportResolver.keepChunkLoaded(world, to);
        }
        if (!onStep && !current.isReceptor() && !pending.isReceptor() && from.getY() != to.getY()) {
            setPos((from.getX() + (double) to.getX() + 1) * 0.5, Math.max(from.getY(), to.getY()) + 0.52 - TRAIL_EYE_HEIGHT,
                    (from.getZ() + (double) to.getZ() + 1) * 0.5);
            onStep = true;
            return;
        }
        setPos(nodePosition(pending));
        if (reversing) path.removeLast();
        else {
            path.add(pending);
            visited.add(pending);
            if (pending.isReceptor()) {
                destination = pending;
                arrivalTick = rider.tickCount;
            }
        }
        pending = null;
        reversing = false;
        onStep = false;
    }

    private VocoDustRoute.Node chooseNext() {
        VocoDustRoute.Node current = path.getLast();
        VocoDustRoute.Node previous = path.size() > 1 ? path.get(path.size() - 2) : null;
        VocoDustRoute.Node best = null;
        int bestScore = Integer.MIN_VALUE;
        for (VocoDustRoute.Node candidate : VocoDustRoute.neighbors(world, current)) {
            if (visited.contains(candidate)) continue;
            int score = candidate.isReceptor() ? 4 : 0;
            if (previous != null) {
                score += (current.pos().getX() - previous.pos().getX()) * (candidate.pos().getX() - current.pos().getX());
                score += (current.pos().getZ() - previous.pos().getZ()) * (candidate.pos().getZ() - current.pos().getZ());
            }
            if (score <= bestScore) continue;
            best = candidate;
            bestScore = score;
        }
        return best;
    }

    public static void cancel(ServerPlayer player) {
        VocoDustRide ride = RIDES.get(player);
        if (ride != null) ride.finish(false);
    }

    private void finish(boolean arrival) {
        if (rider.isAlive() && rider.level() instanceof ServerLevel current) {
            if (arrival && current == world && destination != null && VocoDustRoute.isLit(world, destination)) {
                var target = VocoTeleportLogic.getEndpointTarget(world, destination.pos(), destination.receptor());
                if (!HexTeleportResolver.teleportToVocoExit(rider, world, target)) {
                    destination = null;
                    return;
                }
            } else if (current == world || rider.isSpectator()) departure.moveHome(rider, current);
        }
        if (!rider.isRemoved() && rider.getCamera() != rider) rider.setCamera(rider);
        rider.setDeltaMovement(Vec3.ZERO);
        rider.fallDistance = 0;
        departure.restoreMode(rider, false);
        release();
        if (!rider.isRemoved() && rider.level() instanceof ServerLevel level) level.getChunkSource().move(rider);
    }

    private void release() {
        RIDES.remove(rider, this);
        controls.setRiding(false);
        VocoDustReturn.clear(rider);
        discard();
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) cancel(player);
    }

    public static void onClone(PlayerEvent.Clone event) {
        if (!(event.getOriginal() instanceof ServerPlayer previous) || !(event.getEntity() instanceof ServerPlayer player)) return;
        VocoDustRide ride = RIDES.get(previous);
        if (ride == null) return;
        ride.release();
        ride.departure.restoreMode(player, false);
        VocoDustReturn.clear(player);
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        for (VocoDustRide ride : List.copyOf(RIDES.values())) ride.finish(false);
    }
}
