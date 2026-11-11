package space.anatomyuniverse.musavacca.block.custom.logic;

import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import space.anatomyuniverse.musavacca.block.custom.VocoPostBlock;
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
    private final VocoDustRoute route;
    private final VocoDustReturn departure;
    private final VocoDustInputs controls;
    private final int startTick;
    private final Vec3 entry;
    private final double eyeDrop;
    private int lastDustTick;
    private int next = 1;
    private boolean onStep;

    private VocoDustRide(ServerLevel level, VocoDustRoute route, VocoDustReturn departure, VocoDustInputs controls, ServerPlayer player) {
        //? if >=26.2 {
        /*super(EntityTypes.BLOCK_DISPLAY, level);
        *///?} else {
        super(EntityType.BLOCK_DISPLAY, level);
        //?}
        this.world = level;
        this.rider = player;
        this.route = route;
        this.departure = departure;
        this.controls = controls;
        this.startTick = player.tickCount;
        this.entry = boardingPoint(player, route.nodes().getFirst());
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

    private static Vec3 boardingPoint(ServerPlayer player, BlockPos post) {
        Vec3 start = player.getEyePosition();
        Vec3 cap = Vec3.atBottomCenterOf(post).add(0, 0.875, 0);
        var hit = player.level().clip(new ClipContext(start, cap, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double travel = Math.max(0, start.distanceTo(hit.getLocation()) - 0.10);
        return player.position().add(cap.subtract(start).normalize().scale(travel));
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

    public static void start(ServerPlayer player, ServerLevel level, BlockPos post) {
        if (player instanceof FakePlayer || active(player) || player.isSpectator() || player.isPassenger() || player.isSleeping() || !player.isAlive()) return;
        VocoDustRoute route = VocoDustRoute.find(level, post);
        if (route == null) {
            Component hint = Component.literal("Connect two lit Voco Posts with one unbroken Banana Pearl Dust trail.");
            //? if >=26.1 {
            /*player.sendOverlayMessage(hint);
            *///?} else {
            player.displayClientMessage(hint, true);
            //?}
            return;
        }
        var controls = VocoDustInputs.install(player);
        if (controls == null) return;
        var departure = VocoDustReturn.capture(player, level);
        var camera = new VocoDustRide(level, route, departure, controls, player);
        HexTeleportResolver.keepChunkLoaded(level, post);
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
                || isRemoved() || !route.endpointsReady(world)) {
            finish(false);
            return;
        }
        List<BlockPos> nodes = route.nodes();
        int last = nodes.size() - 2;
        if (!route.intactAt(world, Math.min(next, last))) {
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
            double remaining = age == 2 ? 0.5 : 0.25;
            Vec3 post = Vec3.atBottomCenterOf(nodes.getFirst());
            Vec3 firstDust = Vec3.atBottomCenterOf(nodes.get(1));
            setPos(post.lerp(firstDust, (1 - remaining) * 0.5).add(0, -eyeDrop * remaining, 0));
            return;
        }
        if (next > last) {
            if (rider.tickCount - lastDustTick >= ARRIVAL_TICKS) finish(true);
            return;
        }
        BlockPos from = nodes.get(next - 1);
        BlockPos to = nodes.get(next);
        if (!onStep && ((from.getX() >> 4) != (to.getX() >> 4) || (from.getZ() >> 4) != (to.getZ() >> 4))) {
            HexTeleportResolver.keepChunkLoaded(world, to);
        }
        if (next > 1 && !onStep && from.getY() != to.getY()) {
            setPos((from.getX() + (double) to.getX() + 1) * 0.5, Math.max(from.getY(), to.getY()) + 0.52 - TRAIL_EYE_HEIGHT,
                    (from.getZ() + (double) to.getZ() + 1) * 0.5);
            onStep = true;
            return;
        }
        setPos(Vec3.atBottomCenterOf(to));
        if (next == last) lastDustTick = rider.tickCount;
        onStep = false;
        next++;
    }

    public static void cancel(ServerPlayer player) {
        VocoDustRide ride = RIDES.get(player);
        if (ride != null) ride.finish(false);
    }

    private void finish(boolean arrival) {
        if (rider.isAlive() && rider.level() instanceof ServerLevel current) {
            if (arrival && current == world) {
                BlockPos end = route.nodes().getLast();
                var receptor = VocoPostBlock.receptorPosition(world.getBlockState(end));
                var target = VocoTeleportLogic.getEndpointTarget(world, end, receptor);
                if (!HexTeleportResolver.teleportToVocoExit(rider, world, target)) departure.moveHome(rider, current);
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
