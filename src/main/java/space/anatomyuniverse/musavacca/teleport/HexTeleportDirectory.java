// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/teleport/HexTeleportDirectory.java
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
            int width,
            int height
    ) {
        public static final PortalData EMPTY = new PortalData("x", "south", 2, 3);

        public static final Codec<PortalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("axis", "x").forGetter(PortalData::axis),
                Codec.STRING.optionalFieldOf("front", "south").forGetter(PortalData::front),
                Codec.INT.optionalFieldOf("width", PearlPortalFrame.MIN_WIDTH).forGetter(PortalData::width),
                Codec.INT.optionalFieldOf("height", PearlPortalFrame.MIN_HEIGHT).forGetter(PortalData::height)
        ).apply(instance, PortalData::new));

        public static PortalData of(PearlPortalFrame.Shape shape) {
            return new PortalData(
                    shape.axis() == Direction.Axis.Z ? "z" : "x",
                    directionToString(shape.frontDirection()),
                    shape.width(),
                    shape.height()
            );
        }

        public Direction.Axis axisValue() {
            return "z".equalsIgnoreCase(this.axis) ? Direction.Axis.Z : Direction.Axis.X;
        }

        public Direction frontDirectionValue() {
            return directionFromString(this.front);
        }

        public PearlPortalFrame.Shape toPortalShape(BlockPos origin) {
            Direction.Axis normalizedAxis = PearlPortalFrame.normalizeAxis(this.axisValue());

            return new PearlPortalFrame.Shape(
                    normalizedAxis,
                    origin,
                    clamp(this.width, PearlPortalFrame.MIN_WIDTH, PearlPortalFrame.MAX_WIDTH),
                    clamp(this.height, PearlPortalFrame.MIN_HEIGHT, PearlPortalFrame.MAX_HEIGHT),
                    PearlPortalFrame.normalizeFrontDirection(normalizedAxis, this.frontDirectionValue())
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
                Codec.FLOAT.optionalFieldOf("yaw", 0.0F).forGetter(Endpoint::yaw),
                Codec.FLOAT.optionalFieldOf("pitch", 0.0F).forGetter(Endpoint::pitch),
                Codec.BOOL.optionalFieldOf("custom_target", false).forGetter(Endpoint::customTarget),
                Codec.INT.optionalFieldOf("slot_id", 0).forGetter(Endpoint::slotId),
                PortalData.CODEC.optionalFieldOf("portal", PortalData.EMPTY).forGetter(Endpoint::portalData)
        ).apply(instance, Endpoint::new));

        public Endpoint normalized() {
            UUID safeId = this.endpointId == null ? UUID.randomUUID() : this.endpointId;
            Kind safeKind = this.kind == null ? Kind.VOCO_POST_RECEPTOR_CORNER : this.kind;
            String safeOwnerKey = normalizeOwnerKey(this.ownerKey);
            BlockPos safeOwnerPos = this.ownerPos == null ? BlockPos.ZERO : this.ownerPos.immutable();
            Target safeTarget = this.target == null
                    ? new Target(safeOwnerPos.getX() + 0.5D, safeOwnerPos.getY(), safeOwnerPos.getZ() + 0.5D)
                    : this.target;
            PortalData safePortalData = this.portalData == null ? PortalData.EMPTY : this.portalData;

            return new Endpoint(
                    safeId,
                    safeOwnerKey,
                    safeKind,
                    normalizeHex(this.hexColor),
                    this.dimensionId,
                    safeOwnerPos,
                    safeTarget,
                    clampFloat(this.yaw, -180.0F, 180.0F),
                    clampFloat(this.pitch, -90.0F, 90.0F),
                    this.customTarget,
                    clamp(this.slotId, 0, ReceptorPosition.COUNT - 1),
                    safePortalData
            );
        }

        public boolean hasValidOwner() {
            return this.kind == Kind.PEARL_PORTAL || !normalizeOwnerKey(this.ownerKey).isEmpty();
        }

        public PearlPortalFrame.Shape portalShape() {
            return this.portalData.toPortalShape(this.ownerPos);
        }
    }

    public static final Codec<HexTeleportDirectory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Endpoint.CODEC.listOf().optionalFieldOf("endpoints", List.of()).forGetter(data -> data.endpoints)
    ).apply(instance, HexTeleportDirectory::new));

    public static final SavedDataType<HexTeleportDirectory> TYPE =
            new SavedDataType<>(STORAGE_ID, HexTeleportDirectory::new, CODEC);

    private final ArrayList<Endpoint> endpoints;

    private transient HashMap<UUID, Endpoint> endpointById;
    private transient HashMap<String, UUID> endpointIdByOwnerKey;
    private transient HashMap<Integer, ArrayList<UUID>> endpointIdsByHex;

    public HexTeleportDirectory() {
        this.endpoints = new ArrayList<>();
        this.rebuildIndex();
    }

    private HexTeleportDirectory(List<Endpoint> endpoints) {
        this.endpoints = new ArrayList<>();

        for (Endpoint endpoint : endpoints) {
            if (endpoint != null) {
                this.endpoints.add(endpoint.normalized());
            }
        }

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

        int hex = normalizeHex(hexColor);
        UUID ownEndpointId = this.endpointIdByOwnerKey.get(ownerKey);

        for (Endpoint endpoint : this.endpointsByHex(hex)) {
            if (ownEndpointId != null && endpoint.endpointId.equals(ownEndpointId)) {
                continue;
            }

            return Result.HEX_OCCUPIED;
        }

        return ownEndpointId == null ? Result.REGISTERED : Result.UPDATED;
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
        ownerKey = normalizeOwnerKey(ownerKey);
        if (ownerKey.isEmpty() || kind == null || !kind.isVoco()) {
            return Result.INVALID_OWNER;
        }

        int hex = normalizeHex(hexColor);
        UUID existingId = this.endpointIdByOwnerKey.get(ownerKey);

        for (Endpoint endpoint : this.endpointsByHex(hex)) {
            if (existingId != null && endpoint.endpointId.equals(existingId)) {
                continue;
            }

            return Result.HEX_OCCUPIED;
        }

        UUID endpointId = existingId == null ? UUID.randomUUID() : existingId;
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

        if (existingId != null) {
            this.removeEndpointNoDirty(existingId);
        }

        this.addEndpointNoDirty(endpoint);
        this.setDirty();

        return existingId == null ? Result.REGISTERED : Result.UPDATED;
    }

    public Result checkPortalEndpoint(UUID portalId, int hexColor) {
        if (portalId == null) {
            return Result.INVALID_OWNER;
        }

        int hex = normalizeHex(hexColor);
        int otherPortals = 0;

        for (Endpoint endpoint : this.endpointsByHex(hex)) {
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
        Result checkBefore = this.checkPortalEndpoint(portalId, hexColor);
        if (!checkBefore.success()) {
            return checkBefore;
        }

        Endpoint endpoint = new Endpoint(
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
        ).normalized();

        this.removeEndpointNoDirty(portalId);
        this.addEndpointNoDirty(endpoint);
        this.setDirty();

        return this.checkPortalEndpoint(portalId, hexColor);
    }

    public Optional<Endpoint> getEndpoint(UUID endpointId) {
        return Optional.ofNullable(this.endpointById.get(endpointId));
    }

    public Optional<Endpoint> getEndpointByOwner(String ownerKey) {
        UUID endpointId = this.endpointIdByOwnerKey.get(normalizeOwnerKey(ownerKey));
        return endpointId == null ? Optional.empty() : this.getEndpoint(endpointId);
    }

    public List<Endpoint> getEndpointsByHex(int hexColor) {
        ArrayList<Endpoint> result = new ArrayList<>(this.endpointsByHex(normalizeHex(hexColor)));

        result.sort(Comparator.comparingInt(endpoint -> switch (endpoint.kind()) {
            case VOCO_POST_RECEPTOR_CORNER -> 0;
            case VOCO_TABLE_RECEPTOR_CORNER -> 1;
            case PEARL_PORTAL -> 2;
        }));

        return result;
    }

    public Optional<Endpoint> getLinkedPortalEndpoint(UUID sourcePortalId) {
        Endpoint source = this.endpointById.get(sourcePortalId);
        if (source == null || source.kind != Kind.PEARL_PORTAL) {
            return Optional.empty();
        }

        for (Endpoint endpoint : this.endpointsByHex(source.hexColor)) {
            if (endpoint.kind == Kind.PEARL_PORTAL && !endpoint.endpointId.equals(sourcePortalId)) {
                return Optional.of(endpoint);
            }
        }

        return Optional.empty();
    }

    public boolean isHexFullyOccupiedByPortals(int hexColor) {
        int portals = 0;

        for (Endpoint endpoint : this.endpointsByHex(normalizeHex(hexColor))) {
            if (endpoint.kind == Kind.PEARL_PORTAL) {
                portals++;
            } else {
                return true;
            }
        }

        return portals >= 2;
    }

    public boolean isHexWaitingForSecondPortal(int hexColor) {
        int portals = 0;

        for (Endpoint endpoint : this.endpointsByHex(normalizeHex(hexColor))) {
            if (endpoint.kind == Kind.PEARL_PORTAL) {
                portals++;
            }
        }

        return portals == 1;
    }

    public void removeEndpoint(UUID endpointId) {
        if (this.removeEndpointNoDirty(endpointId)) {
            this.setDirty();
        }
    }

    public void removeOwner(String ownerKey) {
        UUID endpointId = this.endpointIdByOwnerKey.get(normalizeOwnerKey(ownerKey));
        if (endpointId != null) {
            this.removeEndpoint(endpointId);
        }
    }

    private List<Endpoint> endpointsByHex(int hexColor) {
        ArrayList<UUID> ids = this.endpointIdsByHex.get(normalizeHex(hexColor));
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        ArrayList<Endpoint> result = new ArrayList<>();

        for (UUID id : ids) {
            Endpoint endpoint = this.endpointById.get(id);
            if (endpoint != null) {
                result.add(endpoint);
            }
        }

        return result;
    }

    private void rebuildIndex() {
        this.endpointById = new HashMap<>();
        this.endpointIdByOwnerKey = new HashMap<>();
        this.endpointIdsByHex = new HashMap<>();

        ArrayList<Endpoint> clean = new ArrayList<>();

        for (Endpoint rawEndpoint : this.endpoints) {
            if (rawEndpoint == null) {
                continue;
            }

            Endpoint endpoint = rawEndpoint.normalized();

            if (endpoint.dimensionId == null || !endpoint.hasValidOwner()) {
                continue;
            }

            if (!this.canIndexEndpoint(endpoint)) {
                continue;
            }

            this.addEndpointToIndex(endpoint);
            clean.add(endpoint);
        }

        this.endpoints.clear();
        this.endpoints.addAll(clean);
    }

    private boolean canIndexEndpoint(Endpoint endpoint) {
        if (this.endpointById.containsKey(endpoint.endpointId)) {
            return false;
        }

        if (!endpoint.ownerKey.isEmpty() && this.endpointIdByOwnerKey.containsKey(endpoint.ownerKey)) {
            return false;
        }

        List<Endpoint> existingAtHex = this.endpointsByHex(endpoint.hexColor);

        if (endpoint.kind != Kind.PEARL_PORTAL) {
            return existingAtHex.isEmpty();
        }

        int existingPortals = 0;

        for (Endpoint existing : existingAtHex) {
            if (existing.kind != Kind.PEARL_PORTAL) {
                return false;
            }

            existingPortals++;
        }

        return existingPortals < 2;
    }

    private void addEndpointNoDirty(Endpoint endpoint) {
        endpoint = endpoint.normalized();
        this.endpoints.add(endpoint);
        this.addEndpointToIndex(endpoint);
    }

    private void addEndpointToIndex(Endpoint endpoint) {
        this.endpointById.put(endpoint.endpointId, endpoint);

        if (!endpoint.ownerKey.isEmpty()) {
            this.endpointIdByOwnerKey.put(endpoint.ownerKey, endpoint.endpointId);
        }

        this.endpointIdsByHex
                .computeIfAbsent(endpoint.hexColor, ignored -> new ArrayList<>())
                .add(endpoint.endpointId);
    }

    private boolean removeEndpointNoDirty(UUID endpointId) {
        Endpoint old = this.endpointById.remove(endpointId);
        if (old == null) {
            return false;
        }

        if (!old.ownerKey.isEmpty()) {
            this.endpointIdByOwnerKey.remove(old.ownerKey);
        }

        ArrayList<UUID> ids = this.endpointIdsByHex.get(old.hexColor);
        if (ids != null) {
            ids.remove(endpointId);

            if (ids.isEmpty()) {
                this.endpointIdsByHex.remove(old.hexColor);
            }
        }

        this.endpoints.removeIf(endpoint -> endpoint.endpointId.equals(endpointId));
        return true;
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

    private static float clampFloat(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static Direction directionFromString(String text) {
        return switch ((text == null ? "" : text).toLowerCase(Locale.ROOT)) {
            case "north" -> Direction.NORTH;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            case "east" -> Direction.EAST;
            default -> Direction.SOUTH;
        };
    }

    private static String directionToString(Direction direction) {
        return switch (direction) {
            case NORTH -> "north";
            case SOUTH -> "south";
            case WEST -> "west";
            case EAST -> "east";
            default -> "south";
        };
    }
}