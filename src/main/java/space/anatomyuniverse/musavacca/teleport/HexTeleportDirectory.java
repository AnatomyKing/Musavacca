package space.anatomyuniverse.musavacca.teleport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.portal.PearlPortalFrame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public final class HexTeleportDirectory extends SavedData {
    public static final String STORAGE_ID = "musavacca_hex_teleport_directory";

    private static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(
            text -> {
                try {
                    return DataResult.success(UUID.fromString(text));
                } catch (IllegalArgumentException exception) {
                    return DataResult.error(() -> "Invalid UUID: " + text);
                }
            },
            UUID::toString
    );

    private static final Codec<Kind> KIND_CODEC = Codec.STRING.xmap(
            Kind::fromSerializedName,
            Kind::serializedName
    );

    public enum Kind {
        PEARL_PORTAL("pearl_portal"),
        VOCO_TABLE_RECEPTOR_CORNER("voco_table_receptor_corner"),
        VOCO_POST_RECEPTOR_CORNER("voco_post_receptor_corner");

        private final String serializedName;

        Kind(String serializedName) {
            this.serializedName = serializedName;
        }

        public String serializedName() {
            return this.serializedName;
        }

        public boolean isPortal() {
            return this == PEARL_PORTAL;
        }

        public boolean isVoco() {
            return this == VOCO_TABLE_RECEPTOR_CORNER
                    || this == VOCO_POST_RECEPTOR_CORNER;
        }

        public static Kind fromSerializedName(String name) {
            String normalized = name == null ? "" : name.toLowerCase(Locale.ROOT);

            for (Kind kind : values()) {
                if (kind.serializedName.equals(normalized)) {
                    return kind;
                }
            }

            return VOCO_POST_RECEPTOR_CORNER;
        }
    }

    public enum Result {
        REGISTERED,
        UPDATED,
        WAITING_FOR_SECOND_PORTAL,
        LINKED_TO_EXISTING_PORTAL,
        ALREADY_REGISTERED,
        HEX_OCCUPIED,
        INVALID_OWNER;

        public boolean success() {
            return this == REGISTERED
                    || this == UPDATED
                    || this == WAITING_FOR_SECOND_PORTAL
                    || this == LINKED_TO_EXISTING_PORTAL
                    || this == ALREADY_REGISTERED;
        }
    }

    public record Target(double x, double y, double z) {
        public static final Codec<Target> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("x").forGetter(Target::x),
                Codec.DOUBLE.fieldOf("y").forGetter(Target::y),
                Codec.DOUBLE.fieldOf("z").forGetter(Target::z)
        ).apply(instance, Target::new));

        public static Target of(Vec3 vec3) {
            return new Target(vec3.x, vec3.y, vec3.z);
        }

        public Vec3 vec3() {
            return new Vec3(this.x, this.y, this.z);
        }
    }

    public record PortalData(
            String axis,
            String front,
            String up,
            int width,
            int height,
            int exitAnchorX,
            int exitAnchorY,
            int exitAnchorZ
    ) {
        public static final PortalData EMPTY = new PortalData(
                "x",
                "south",
                "up",
                PearlPortalFrame.MIN_WIDTH,
                PearlPortalFrame.MIN_HEIGHT,
                0,
                0,
                0
        );

        public static final Codec<PortalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("axis").forGetter(PortalData::axis),
                Codec.STRING.fieldOf("front").forGetter(PortalData::front),
                Codec.STRING.fieldOf("up").forGetter(PortalData::up),
                Codec.INT.fieldOf("width").forGetter(PortalData::width),
                Codec.INT.fieldOf("height").forGetter(PortalData::height),
                Codec.INT.fieldOf("exit_anchor_x").forGetter(PortalData::exitAnchorX),
                Codec.INT.fieldOf("exit_anchor_y").forGetter(PortalData::exitAnchorY),
                Codec.INT.fieldOf("exit_anchor_z").forGetter(PortalData::exitAnchorZ)
        ).apply(instance, PortalData::new));

        public static PortalData of(PearlPortalFrame.Shape shape) {
            BlockPos anchor = shape.exitAnchorPos();

            return new PortalData(
                    axisToString(shape.axis()),
                    directionToString(shape.frontDirection()),
                    directionToString(shape.upDirection()),
                    shape.width(),
                    shape.height(),
                    anchor.getX(),
                    anchor.getY(),
                    anchor.getZ()
            );
        }

        public PearlPortalFrame.Shape toPortalShape(BlockPos origin) {
            Direction.Axis axis = axisFromString(this.axis);
            Direction.Axis safeAxis = PearlPortalFrame.normalizeAxis(axis);

            Direction front = PearlPortalFrame.normalizeFrontDirection(
                    safeAxis,
                    directionFromString(this.front)
            );

            Direction up = PearlPortalFrame.normalizeUpDirection(
                    safeAxis,
                    front,
                    directionFromString(this.up)
            );

            return new PearlPortalFrame.Shape(
                    safeAxis,
                    origin,
                    clamp(this.width, PearlPortalFrame.MIN_WIDTH, PearlPortalFrame.MAX_WIDTH),
                    clamp(this.height, PearlPortalFrame.MIN_HEIGHT, PearlPortalFrame.MAX_HEIGHT),
                    front,
                    up,
                    new BlockPos(this.exitAnchorX, this.exitAnchorY, this.exitAnchorZ)
            );
        }
    }

    public record Endpoint(
            UUID endpointId,
            String ownerKey,
            Kind kind,
            int hexColor,
            ResourceLocation dimensionId,
            BlockPos ownerPos,
            Target target,
            float yaw,
            float pitch,
            boolean customTarget,
            int slotId,
            PortalData portalData
    ) {
        public static final Codec<Endpoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUID_CODEC.fieldOf("endpoint_id").forGetter(Endpoint::endpointId),
                Codec.STRING.fieldOf("owner_key").forGetter(Endpoint::ownerKey),
                KIND_CODEC.fieldOf("kind").forGetter(Endpoint::kind),
                Codec.INT.fieldOf("hex_color").forGetter(Endpoint::hexColor),
                ResourceLocation.CODEC.fieldOf("dimension").forGetter(Endpoint::dimensionId),
                BlockPos.CODEC.fieldOf("owner_pos").forGetter(Endpoint::ownerPos),
                Target.CODEC.fieldOf("target").forGetter(Endpoint::target),
                Codec.FLOAT.fieldOf("yaw").forGetter(Endpoint::yaw),
                Codec.FLOAT.fieldOf("pitch").forGetter(Endpoint::pitch),
                Codec.BOOL.fieldOf("custom_target").forGetter(Endpoint::customTarget),
                Codec.INT.fieldOf("slot_id").forGetter(Endpoint::slotId),
                PortalData.CODEC.fieldOf("portal").forGetter(Endpoint::portalData)
        ).apply(instance, Endpoint::new));

        public Endpoint normalized() {
            BlockPos safeOwnerPos = this.ownerPos == null ? BlockPos.ZERO : this.ownerPos.immutable();
            Kind safeKind = this.kind == null ? Kind.VOCO_POST_RECEPTOR_CORNER : this.kind;

            return new Endpoint(
                    this.endpointId == null ? UUID.randomUUID() : this.endpointId,
                    normalizeOwnerKey(this.ownerKey),
                    safeKind,
                    normalizeHex(this.hexColor),
                    this.dimensionId,
                    safeOwnerPos,
                    this.target == null
                            ? new Target(safeOwnerPos.getX() + 0.5D, safeOwnerPos.getY(), safeOwnerPos.getZ() + 0.5D)
                            : this.target,
                    clampFloat(this.yaw, -180.0F, 180.0F),
                    clampFloat(this.pitch, -90.0F, 90.0F),
                    this.customTarget,
                    clamp(this.slotId, 0, ReceptorPosition.COUNT - 1),
                    this.portalData == null ? PortalData.EMPTY : this.portalData
            );
        }

        public boolean hasValidOwner() {
            return this.kind == Kind.PEARL_PORTAL || !normalizeOwnerKey(this.ownerKey).isEmpty();
        }

        public PearlPortalFrame.Shape portalShape() {
            return this.portalData.toPortalShape(this.ownerPos);
        }
    }

    public record VocoRegistration(Result result, Endpoint removedActiveEndpoint) {}

    public static final Codec<HexTeleportDirectory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Endpoint.CODEC.listOf().fieldOf("endpoints").forGetter(data -> data.active.entries),
            Endpoint.CODEC.listOf().fieldOf("pending_voco_endpoints").forGetter(data -> data.pending.entries)
    ).apply(instance, HexTeleportDirectory::new));

    public static final SavedDataType<HexTeleportDirectory> TYPE =
            new SavedDataType<>(STORAGE_ID, HexTeleportDirectory::new, CODEC);

    private final EndpointStore active = new EndpointStore();
    private final EndpointStore pending = new EndpointStore();

    public HexTeleportDirectory() {}

    private HexTeleportDirectory(List<Endpoint> endpoints, List<Endpoint> pendingVocoEndpoints) {
        this.active.entries.addAll(endpoints);
        this.pending.entries.addAll(pendingVocoEndpoints);
        this.rebuildIndex();
    }

    public static HexTeleportDirectory get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public Result checkVocoEndpoint(String ownerKey, int hexColor) {
        ownerKey = normalizeOwnerKey(ownerKey);
        if (ownerKey.isEmpty()) {
            return Result.INVALID_OWNER;
        }

        Endpoint self = this.active.byOwner(ownerKey).orElse(null);
        return this.isHexOccupiedByOther(normalizeHex(hexColor), self)
                ? Result.HEX_OCCUPIED
                : self == null ? Result.REGISTERED : Result.UPDATED;
    }

    public Result registerVocoEndpoint(
            String ownerKey,
            Kind kind,
            int hexColor,
            ResourceLocation dimensionId,
            BlockPos ownerPos,
            Vec3 targetPos,
            float yaw,
            float pitch,
            boolean customTarget,
            int slotId
    ) {
        return this.registerOrQueueVocoEndpoint(
                ownerKey,
                kind,
                hexColor,
                dimensionId,
                ownerPos,
                targetPos,
                yaw,
                pitch,
                customTarget,
                slotId
        ).result();
    }

    public VocoRegistration registerOrQueueVocoEndpoint(
            String ownerKey,
            Kind kind,
            int hexColor,
            ResourceLocation dimensionId,
            BlockPos ownerPos,
            Vec3 targetPos,
            float yaw,
            float pitch,
            boolean customTarget,
            int slotId
    ) {
        ownerKey = normalizeOwnerKey(ownerKey);

        if (ownerKey.isEmpty()
                || kind == null
                || !kind.isVoco()
                || dimensionId == null
                || ownerPos == null
                || targetPos == null) {
            return new VocoRegistration(Result.INVALID_OWNER, null);
        }

        int hex = normalizeHex(hexColor);
        Endpoint existingActive = this.active.byOwner(ownerKey).orElse(null);
        Endpoint existingPending = this.pending.byOwner(ownerKey).orElse(null);
        boolean occupiedByOther = this.isHexOccupiedByOther(hex, existingActive);

        UUID endpointId = existingActive != null
                ? existingActive.endpointId
                : existingPending != null
                ? existingPending.endpointId
                : UUID.randomUUID();

        Endpoint endpoint = new Endpoint(
                endpointId,
                ownerKey,
                kind,
                hex,
                dimensionId,
                ownerPos.immutable(),
                Target.of(targetPos),
                yaw,
                pitch,
                customTarget,
                slotId,
                PortalData.EMPTY
        ).normalized();

        Endpoint removedActive = existingActive == null ? null : this.active.remove(existingActive.endpointId);

        if (existingPending != null) {
            this.pending.remove(existingPending.endpointId);
        }

        if (occupiedByOther) {
            this.pending.add(endpoint);
            this.setDirty();
            return new VocoRegistration(Result.HEX_OCCUPIED, removedActive);
        }

        this.active.add(endpoint);
        this.setDirty();

        return new VocoRegistration(
                existingActive == null ? Result.REGISTERED : Result.UPDATED,
                removedActive
        );
    }

    private boolean isHexOccupiedByOther(int hexColor, Endpoint self) {
        for (Endpoint endpoint : this.active.byHex(hexColor)) {
            if (self == null || !endpoint.endpointId.equals(self.endpointId)) {
                return true;
            }
        }

        return false;
    }

    public Result checkPortalEndpoint(UUID portalId, int hexColor) {
        if (portalId == null) {
            return Result.INVALID_OWNER;
        }

        int otherPortals = 0;

        for (Endpoint endpoint : this.active.byHex(normalizeHex(hexColor))) {
            if (endpoint.endpointId.equals(portalId)) {
                continue;
            }

            if (!endpoint.kind.isPortal()) {
                return Result.HEX_OCCUPIED;
            }

            otherPortals++;
        }

        if (otherPortals >= 2) {
            return Result.HEX_OCCUPIED;
        }

        return otherPortals == 1 ? Result.LINKED_TO_EXISTING_PORTAL : Result.WAITING_FOR_SECOND_PORTAL;
    }

    public Result registerPortalEndpoint(
            UUID portalId,
            int hexColor,
            ResourceLocation dimensionId,
            PearlPortalFrame.Shape shape
    ) {
        if (portalId == null || dimensionId == null || shape == null) {
            return Result.INVALID_OWNER;
        }

        Result checkBefore = this.checkPortalEndpoint(portalId, hexColor);
        if (!checkBefore.success()) {
            return checkBefore;
        }

        this.active.remove(portalId);
        this.active.add(new Endpoint(
                portalId,
                portalOwnerKey(portalId),
                Kind.PEARL_PORTAL,
                normalizeHex(hexColor),
                dimensionId,
                shape.minCorner().immutable(),
                Target.of(shape.center()),
                0.0F,
                0.0F,
                false,
                0,
                PortalData.of(shape)
        ).normalized());

        this.setDirty();
        return this.checkPortalEndpoint(portalId, hexColor);
    }

    public Optional<Endpoint> getEndpoint(UUID endpointId) {
        return this.active.byId(endpointId);
    }

    public Optional<Endpoint> getEndpointByOwner(String ownerKey) {
        return this.active.byOwner(ownerKey);
    }

    public Optional<Endpoint> getFirstPendingVocoEndpointByHex(int hexColor) {
        return this.pending.firstByHex(normalizeHex(hexColor));
    }

    public List<Endpoint> getEndpointsByHex(int hexColor) {
        ArrayList<Endpoint> result = new ArrayList<>(this.active.byHex(normalizeHex(hexColor)));

        result.sort(Comparator.comparingInt(endpoint -> switch (endpoint.kind()) {
            case VOCO_POST_RECEPTOR_CORNER -> 0;
            case VOCO_TABLE_RECEPTOR_CORNER -> 1;
            case PEARL_PORTAL -> 2;
        }));

        return result;
    }

    public Optional<Endpoint> getLinkedPortalEndpoint(UUID sourcePortalId) {
        Endpoint source = this.active.byId(sourcePortalId).orElse(null);
        if (source == null || source.kind != Kind.PEARL_PORTAL) {
            return Optional.empty();
        }

        for (Endpoint endpoint : this.active.byHex(source.hexColor)) {
            if (endpoint.kind == Kind.PEARL_PORTAL && !endpoint.endpointId.equals(sourcePortalId)) {
                return Optional.of(endpoint);
            }
        }

        return Optional.empty();
    }

    public boolean isHexFullyOccupiedByPortals(int hexColor) {
        int portals = 0;

        for (Endpoint endpoint : this.active.byHex(normalizeHex(hexColor))) {
            if (!endpoint.kind.isPortal()) {
                return true;
            }

            portals++;
        }

        return portals >= 2;
    }

    public boolean isHexWaitingForSecondPortal(int hexColor) {
        int portals = 0;

        for (Endpoint endpoint : this.active.byHex(normalizeHex(hexColor))) {
            if (endpoint.kind.isPortal()) {
                portals++;
            }
        }

        return portals == 1;
    }

    public Optional<Endpoint> removeEndpoint(UUID endpointId) {
        Endpoint removedActive = this.active.remove(endpointId);
        Endpoint removedPending = removedActive == null ? this.pending.remove(endpointId) : null;

        if (removedActive != null || removedPending != null) {
            this.setDirty();
        }

        return Optional.ofNullable(removedActive);
    }

    public Optional<Endpoint> removeOwner(String ownerKey) {
        Endpoint removedActive = this.active.removeOwner(ownerKey);
        Endpoint removedPending = this.pending.removeOwner(ownerKey);

        if (removedActive != null || removedPending != null) {
            this.setDirty();
        }

        return Optional.ofNullable(removedActive);
    }

    public boolean removePendingEndpoint(UUID endpointId) {
        Endpoint removed = this.pending.remove(endpointId);

        if (removed != null) {
            this.setDirty();
        }

        return removed != null;
    }

    private void rebuildIndex() {
        this.active.rebuild(this::canIndexActive);
        this.pending.rebuild(this::canIndexPending);
    }

    private boolean canIndexActive(Endpoint endpoint) {
        if (endpoint.dimensionId == null
                || !endpoint.hasValidOwner()
                || this.active.hasId(endpoint.endpointId)
                || !endpoint.ownerKey.isEmpty() && this.active.hasOwner(endpoint.ownerKey)) {
            return false;
        }

        List<Endpoint> existingAtHex = this.active.byHex(endpoint.hexColor);

        if (!endpoint.kind.isPortal()) {
            return existingAtHex.isEmpty();
        }

        int portals = 0;

        for (Endpoint existing : existingAtHex) {
            if (!existing.kind.isPortal()) {
                return false;
            }

            portals++;
        }

        return portals < 2;
    }

    private boolean canIndexPending(Endpoint endpoint) {
        return endpoint.dimensionId != null
                && endpoint.kind.isVoco()
                && endpoint.hasValidOwner()
                && !this.active.byHex(endpoint.hexColor).isEmpty()
                && !this.active.hasOwner(endpoint.ownerKey)
                && !this.pending.hasId(endpoint.endpointId)
                && !this.pending.hasOwner(endpoint.ownerKey);
    }

    public static String vocoTableReceptorCornerOwnerKey(
            ResourceLocation dimensionId,
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        return ownerKey(Kind.VOCO_TABLE_RECEPTOR_CORNER, dimensionId, pos, receptor.id());
    }

    public static String vocoPostReceptorCornerOwnerKey(ResourceLocation dimensionId, BlockPos pos) {
        return ownerKey(Kind.VOCO_POST_RECEPTOR_CORNER, dimensionId, pos, 0);
    }

    public static String ownerKey(Kind kind, ResourceLocation dimensionId, BlockPos pos, int slotId) {
        return kind.serializedName()
                + "|"
                + dimensionId
                + "|"
                + pos.getX() + "," + pos.getY() + "," + pos.getZ()
                + "|"
                + slotId;
    }

    public static String portalOwnerKey(UUID portalId) {
        return "pearl_portal|" + portalId;
    }

    public static int normalizeHex(int hexColor) {
        return hexColor & 0xFFFFFF;
    }

    public static String toHex(int hexColor) {
        return String.format(Locale.ROOT, "%06X", normalizeHex(hexColor));
    }

    private static String normalizeOwnerKey(String ownerKey) {
        return ownerKey == null ? "" : ownerKey.trim();
    }

    private static Direction.Axis axisFromString(String text) {
        return switch ((text == null ? "" : text).toLowerCase(Locale.ROOT)) {
            case "y" -> Direction.Axis.Y;
            case "z" -> Direction.Axis.Z;
            default -> Direction.Axis.X;
        };
    }

    private static String axisToString(Direction.Axis axis) {
        return switch (PearlPortalFrame.normalizeAxis(axis)) {
            case X -> "x";
            case Y -> "y";
            case Z -> "z";
        };
    }

    private static Direction directionFromString(String text) {
        return switch ((text == null ? "" : text).toLowerCase(Locale.ROOT)) {
            case "north" -> Direction.NORTH;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            case "east" -> Direction.EAST;
            case "up" -> Direction.UP;
            case "down" -> Direction.DOWN;
            default -> null;
        };
    }

    private static String directionToString(Direction direction) {
        if (direction == null) {
            return "south";
        }

        return switch (direction) {
            case NORTH -> "north";
            case SOUTH -> "south";
            case WEST -> "west";
            case EAST -> "east";
            case UP -> "up";
            case DOWN -> "down";
        };
    }

    private static float clampFloat(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class EndpointStore {
        private final ArrayList<Endpoint> entries = new ArrayList<>();
        private final HashMap<UUID, Endpoint> byId = new HashMap<>();
        private final HashMap<String, UUID> idByOwner = new HashMap<>();
        private final HashMap<Integer, ArrayList<UUID>> idsByHex = new HashMap<>();

        private void rebuild(Predicate<Endpoint> validator) {
            ArrayList<Endpoint> raw = new ArrayList<>(this.entries);
            this.clear();

            for (Endpoint endpoint : raw) {
                if (endpoint == null) {
                    continue;
                }

                Endpoint normalized = endpoint.normalized();
                if (validator.test(normalized)) {
                    this.add(normalized);
                }
            }
        }

        private void clear() {
            this.entries.clear();
            this.byId.clear();
            this.idByOwner.clear();
            this.idsByHex.clear();
        }

        private void add(Endpoint endpoint) {
            endpoint = endpoint.normalized();

            this.entries.add(endpoint);
            this.byId.put(endpoint.endpointId, endpoint);

            if (!endpoint.ownerKey.isEmpty()) {
                this.idByOwner.put(endpoint.ownerKey, endpoint.endpointId);
            }

            this.idsByHex
                    .computeIfAbsent(endpoint.hexColor, ignored -> new ArrayList<>())
                    .add(endpoint.endpointId);
        }

        private Endpoint remove(UUID endpointId) {
            Endpoint endpoint = this.byId.remove(endpointId);
            if (endpoint == null) {
                return null;
            }

            if (!endpoint.ownerKey.isEmpty()) {
                this.idByOwner.remove(endpoint.ownerKey);
            }

            ArrayList<UUID> ids = this.idsByHex.get(endpoint.hexColor);
            if (ids != null) {
                ids.remove(endpointId);

                if (ids.isEmpty()) {
                    this.idsByHex.remove(endpoint.hexColor);
                }
            }

            this.entries.removeIf(entry -> entry.endpointId.equals(endpointId));
            return endpoint;
        }

        private Endpoint removeOwner(String ownerKey) {
            UUID endpointId = this.idByOwner.get(normalizeOwnerKey(ownerKey));
            return endpointId == null ? null : this.remove(endpointId);
        }

        private Optional<Endpoint> byId(UUID endpointId) {
            return Optional.ofNullable(this.byId.get(endpointId));
        }

        private Optional<Endpoint> byOwner(String ownerKey) {
            UUID endpointId = this.idByOwner.get(normalizeOwnerKey(ownerKey));
            return endpointId == null ? Optional.empty() : this.byId(endpointId);
        }

        private List<Endpoint> byHex(int hexColor) {
            ArrayList<UUID> ids = this.idsByHex.get(normalizeHex(hexColor));
            if (ids == null || ids.isEmpty()) {
                return List.of();
            }

            ArrayList<Endpoint> result = new ArrayList<>();

            for (UUID id : ids) {
                Endpoint endpoint = this.byId.get(id);
                if (endpoint != null) {
                    result.add(endpoint);
                }
            }

            return result;
        }

        private Optional<Endpoint> firstByHex(int hexColor) {
            ArrayList<UUID> ids = this.idsByHex.get(normalizeHex(hexColor));
            if (ids == null || ids.isEmpty()) {
                return Optional.empty();
            }

            for (UUID id : ids) {
                Endpoint endpoint = this.byId.get(id);
                if (endpoint != null) {
                    return Optional.of(endpoint);
                }
            }

            return Optional.empty();
        }

        private boolean hasId(UUID endpointId) {
            return this.byId.containsKey(endpointId);
        }

        private boolean hasOwner(String ownerKey) {
            return this.idByOwner.containsKey(normalizeOwnerKey(ownerKey));
        }
    }
}

