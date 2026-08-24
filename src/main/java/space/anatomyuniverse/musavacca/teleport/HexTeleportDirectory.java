// file: src/main/java/space/anatomyuniverse/musavacca/teleport/HexTeleportDirectory.java
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

        public boolean canQueueForAddress() {
            return this.isVoco();
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
        WAITING_FOR_SECOND_DOOR,
        LINKED_TO_EXISTING_DOOR,
        WAITING_FOR_SECOND_TRAPDOOR,
        LINKED_TO_EXISTING_TRAPDOOR,
        ALREADY_REGISTERED,
        HEX_OCCUPIED,
        INVALID_OWNER;

        public boolean success() {
            return this == REGISTERED
                    || this == UPDATED
                    || this == WAITING_FOR_SECOND_PORTAL
                    || this == LINKED_TO_EXISTING_PORTAL
                    || this == WAITING_FOR_SECOND_DOOR
                    || this == LINKED_TO_EXISTING_DOOR
                    || this == WAITING_FOR_SECOND_TRAPDOOR
                    || this == LINKED_TO_EXISTING_TRAPDOOR
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

    public record DoorEndpoint(
            String ownerKey,
            int hexColor,
            ResourceLocation dimensionId,
            BlockPos ownerPos
    ) {
        public static final Codec<DoorEndpoint> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("owner_key").forGetter(DoorEndpoint::ownerKey),
                Codec.INT.fieldOf("hex_color").forGetter(DoorEndpoint::hexColor),
                ResourceLocation.CODEC.fieldOf("dimension").forGetter(DoorEndpoint::dimensionId),
                BlockPos.CODEC.fieldOf("owner_pos").forGetter(DoorEndpoint::ownerPos)
        ).apply(instance, DoorEndpoint::new));

        public DoorEndpoint normalized() {
            return new DoorEndpoint(
                    normalizeOwnerKey(this.ownerKey),
                    normalizeHex(this.hexColor),
                    this.dimensionId,
                    this.ownerPos == null ? BlockPos.ZERO : this.ownerPos.immutable()
            );
        }

        public boolean hasValidOwner() {
            return this.dimensionId != null
                    && !normalizeOwnerKey(this.ownerKey).isEmpty();
        }
    }

    public record PhoneRegistration(
            int hexColor,
            UUID ownerUuid
    ) {
        public static final Codec<PhoneRegistration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("hex_color").forGetter(PhoneRegistration::hexColor),
                UUID_CODEC.fieldOf("owner_uuid").forGetter(PhoneRegistration::ownerUuid)
        ).apply(instance, PhoneRegistration::new));

        public PhoneRegistration normalized() {
            return new PhoneRegistration(
                    normalizeHex(this.hexColor),
                    this.ownerUuid
            );
        }
    }

    public record VocoRegistration(Result result, Endpoint removedActiveEndpoint) {}

    /*
     * Clean directory layout.
     *
     * pending_endpoints:
     *     address-queued endpoint claims, currently used by Voco.
     *
     * pending_door_endpoints:
     *     doors that ALREADY RESERVE their address and wait for door #2.
     */
    public static final Codec<HexTeleportDirectory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Endpoint.CODEC.listOf().fieldOf("endpoints").forGetter(data -> data.active.entries),
            Endpoint.CODEC.listOf().fieldOf("pending_endpoints").forGetter(data -> data.pending.entries),
            DoorEndpoint.CODEC.listOf().fieldOf("door_endpoints").forGetter(data -> data.doors.entries),
            DoorEndpoint.CODEC.listOf().fieldOf("pending_door_endpoints").forGetter(data -> data.pendingDoors.entries),
            PhoneRegistration.CODEC.listOf().optionalFieldOf("phone_registrations", List.of()).forGetter(data -> data.phoneRegistrations)
    ).apply(instance, HexTeleportDirectory::new));

    public static final SavedDataType<HexTeleportDirectory> TYPE =
            new SavedDataType<>(STORAGE_ID, HexTeleportDirectory::new, CODEC);

    /*
     * Indexed stores keep normal operations off full-list scans.
     *
     * active:
     *     Voco endpoints + Pearl portals.
     *
     * pending:
     *     claims waiting for a currently reserved address.
     *
     * doors:
     *     linked Musavacca door pairs.
     *
     * pendingDoors:
     *     one Musavacca door waiting for its matching second door.
     */
    private final EndpointStore active = new EndpointStore();
    private final EndpointStore pending = new EndpointStore();
    private final DoorEndpointStore doors = new DoorEndpointStore();
    private final DoorEndpointStore pendingDoors = new DoorEndpointStore();
    private final ArrayList<PhoneRegistration> phoneRegistrations = new ArrayList<>();

    public HexTeleportDirectory() {}

    private HexTeleportDirectory(
            List<Endpoint> endpoints,
            List<Endpoint> pendingEndpoints,
            List<DoorEndpoint> doorEndpoints,
            List<DoorEndpoint> pendingDoorEndpoints,
            List<PhoneRegistration> phoneRegistrations
    ) {
        this.active.entries.addAll(endpoints);
        this.pending.entries.addAll(pendingEndpoints);
        this.doors.entries.addAll(doorEndpoints);
        this.pendingDoors.entries.addAll(pendingDoorEndpoints);

        if (phoneRegistrations != null) {
            for (PhoneRegistration registration : phoneRegistrations) {
                if (registration != null
                        && registration.ownerUuid() != null) {
                    this.phoneRegistrations.add(
                            registration.normalized()
                    );
                }
            }
        }

        this.rebuildIndex();
        this.normalizeLoadedPhoneRegistrations();
    }

    public static HexTeleportDirectory get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    /*
     * True when an address is currently owned/reserved by:
     *
     * - one Voco endpoint
     * - one waiting Pearl portal
     * - one linked Pearl portal pair
     * - one waiting Musavacca door/trapdoor
     * - one linked Musavacca door/trapdoor pair
     */
    public boolean isHexReserved(int hexColor) {
        int hex = normalizeHex(hexColor);

        return this.active.hasHex(hex)
                || this.isDoorHexReserved(hex)
                || this.hasPhoneHex(hex);
    }

    private boolean isHexReservedByOther(int hexColor, Endpoint self) {
        int hex = normalizeHex(hexColor);

        if (this.isDoorHexReserved(hex) || this.hasPhoneHex(hex)) {
            return true;
        }

        for (Endpoint endpoint : this.active.byHex(hex)) {
            if (self == null || !endpoint.endpointId.equals(self.endpointId)) {
                return true;
            }
        }

        return false;
    }

    public Result checkVocoEndpoint(String ownerKey, int hexColor) {
        ownerKey = normalizeOwnerKey(ownerKey);

        if (ownerKey.isEmpty()) {
            return Result.INVALID_OWNER;
        }

        Endpoint self = this.active.byOwner(ownerKey).orElse(null);

        return this.isHexReservedByOther(hexColor, self)
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

        boolean occupiedByOther = this.isHexReservedByOther(hex, existingActive);

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

        Endpoint removedActive = existingActive == null
                ? null
                : this.active.remove(existingActive.endpointId);

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

    public Result checkPortalEndpoint(UUID portalId, int hexColor) {
        if (portalId == null) {
            return Result.INVALID_OWNER;
        }

        int hex = normalizeHex(hexColor);

        if (this.isDoorHexReserved(hex) || this.hasPhoneHex(hex)) {
            return Result.HEX_OCCUPIED;
        }

        int otherPortals = 0;

        for (Endpoint endpoint : this.active.byHex(hex)) {
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

        return otherPortals == 1
                ? Result.LINKED_TO_EXISTING_PORTAL
                : Result.WAITING_FOR_SECOND_PORTAL;
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

    public Result registerDoorEndpoint(
            String ownerKey,
            int hexColor,
            ResourceLocation dimensionId,
            BlockPos ownerPos
    ) {
        ownerKey = normalizeOwnerKey(ownerKey);

        if (ownerKey.isEmpty()
                || dimensionId == null
                || ownerPos == null) {
            return Result.INVALID_OWNER;
        }

        int hex = normalizeHex(hexColor);

        DoorEndpoint existingActive = this.doors.byOwner(ownerKey).orElse(null);
        DoorEndpoint existingPending = this.pendingDoors.byOwner(ownerKey).orElse(null);

        if (matchesDoorEndpoint(existingActive, hex, dimensionId, ownerPos)) {
            return Result.LINKED_TO_EXISTING_DOOR;
        }

        if (matchesDoorEndpoint(existingPending, hex, dimensionId, ownerPos)) {
            return Result.WAITING_FOR_SECOND_DOOR;
        }

        /*
         * A changed door releases its previous claim first.
         *
         * If it was linked, removeDoorOwner() demotes its surviving
         * partner to pending, so the old hex stays reserved.
         */
        if (existingActive != null || existingPending != null) {
            this.removeDoorOwner(ownerKey);
        }

        /*
         * Voco/Pearl ownership blocks door ownership.
         */
        if (this.active.hasHex(hex)
                || this.hasPhoneHex(hex)
                || this.doors.countByHex(hex) >= 2) {
            return Result.HEX_OCCUPIED;
        }

        DoorEndpoint candidate = new DoorEndpoint(
                ownerKey,
                hex,
                dimensionId,
                ownerPos.immutable()
        ).normalized();

        DoorEndpoint partner = this.pendingDoors.firstByHex(hex).orElse(null);

        if (
                partner != null
                        && !isDoorOwnerKey(partner.ownerKey())
        ) {
            return Result.HEX_OCCUPIED;
        }

        if (partner != null && !partner.ownerKey.equals(ownerKey)) {
            this.pendingDoors.removeOwner(partner.ownerKey);
            this.doors.add(partner);
            this.doors.add(candidate);
            this.setDirty();
            return Result.LINKED_TO_EXISTING_DOOR;
        }

        /*
         * Door #1 is pending-for-pair but already reserves the address.
         */
        this.pendingDoors.add(candidate);
        this.setDirty();
        return Result.WAITING_FOR_SECOND_DOOR;
    }

    /*
     * Trapdoors intentionally reuse the persisted DoorEndpoint store.
     *
     * The endpoint payload is identical (owner key, hex, dimension, position),
     * so sharing the store avoids a saved-data schema migration. Door and
     * trapdoor endpoints still NEVER pair with each other: the owner-key
     * prefix below keeps each hinged portal family isolated.
     */
    public Result registerTrapdoorEndpoint(
            String ownerKey,
            int hexColor,
            ResourceLocation dimensionId,
            BlockPos ownerPos
    ) {
        ownerKey = normalizeOwnerKey(ownerKey);

        if (ownerKey.isEmpty()
                || dimensionId == null
                || ownerPos == null
                || !isTrapdoorOwnerKey(ownerKey)) {
            return Result.INVALID_OWNER;
        }

        int hex = normalizeHex(hexColor);

        DoorEndpoint existingActive = this.doors.byOwner(ownerKey).orElse(null);
        DoorEndpoint existingPending = this.pendingDoors.byOwner(ownerKey).orElse(null);

        if (matchesDoorEndpoint(existingActive, hex, dimensionId, ownerPos)) {
            return Result.LINKED_TO_EXISTING_TRAPDOOR;
        }

        if (matchesDoorEndpoint(existingPending, hex, dimensionId, ownerPos)) {
            return Result.WAITING_FOR_SECOND_TRAPDOOR;
        }

        if (existingActive != null || existingPending != null) {
            this.removeDoorOwner(ownerKey);
        }

        if (this.active.hasHex(hex)
                || this.hasPhoneHex(hex)
                || this.doors.countByHex(hex) >= 2) {
            return Result.HEX_OCCUPIED;
        }

        DoorEndpoint candidate = new DoorEndpoint(
                ownerKey,
                hex,
                dimensionId,
                ownerPos.immutable()
        ).normalized();

        DoorEndpoint partner = this.pendingDoors.firstByHex(hex).orElse(null);

        if (
                partner != null
                        && !isTrapdoorOwnerKey(partner.ownerKey())
        ) {
            return Result.HEX_OCCUPIED;
        }

        if (partner != null && !partner.ownerKey.equals(ownerKey)) {
            this.pendingDoors.removeOwner(partner.ownerKey);
            this.doors.add(partner);
            this.doors.add(candidate);
            this.setDirty();
            return Result.LINKED_TO_EXISTING_TRAPDOOR;
        }

        this.pendingDoors.add(candidate);
        this.setDirty();
        return Result.WAITING_FOR_SECOND_TRAPDOOR;
    }

    public Result registerPhone(
            int hexColor,
            UUID ownerUuid
    ) {
        if (ownerUuid == null) {
            return Result.INVALID_OWNER;
        }

        int hex = normalizeHex(hexColor);

        if (this.isHexReserved(hex)) {
            return Result.HEX_OCCUPIED;
        }

        this.phoneRegistrations.add(
                new PhoneRegistration(
                        hex,
                        ownerUuid
                )
        );

        this.setDirty();
        return Result.REGISTERED;
    }

    public Optional<PhoneRegistration> getPhoneRegistrationByHex(
            int hexColor
    ) {
        int hex = normalizeHex(hexColor);

        for (PhoneRegistration registration : this.phoneRegistrations) {
            if (registration.hexColor() == hex) {
                return Optional.of(registration);
            }
        }

        return Optional.empty();
    }

    public Optional<PhoneRegistration> removePhoneRegistration(
            int hexColor,
            UUID ownerUuid
    ) {
        int hex = normalizeHex(hexColor);

        for (int index = 0; index < this.phoneRegistrations.size(); index++) {
            PhoneRegistration registration =
                    this.phoneRegistrations.get(index);

            if (registration.hexColor() == hex
                    && registration.ownerUuid().equals(ownerUuid)) {
                this.phoneRegistrations.remove(index);
                this.setDirty();
                return Optional.of(registration);
            }
        }

        return Optional.empty();
    }

    private boolean hasPhoneHex(int hexColor) {
        return this.getPhoneRegistrationByHex(hexColor).isPresent();
    }

    private void normalizeLoadedPhoneRegistrations() {
        ArrayList<PhoneRegistration> raw =
                new ArrayList<>(
                        this.phoneRegistrations
                );

        this.phoneRegistrations.clear();

        for (PhoneRegistration registration : raw) {
            if (!this.active.hasHex(registration.hexColor())
                    && !this.isDoorHexReserved(registration.hexColor())
                    && !this.hasPhoneHex(registration.hexColor())) {
                this.phoneRegistrations.add(registration);
            }
        }
    }

    public Optional<Endpoint> getEndpoint(UUID endpointId) {
        return this.active.byId(endpointId);
    }

    public Optional<Endpoint> getEndpointByOwner(String ownerKey) {
        return this.active.byOwner(ownerKey);
    }

    public Optional<Endpoint> getFirstPendingEndpointByHex(int hexColor) {
        return this.pending.firstByHex(normalizeHex(hexColor));
    }

    public List<Endpoint> getEndpointsByHex(int hexColor) {
        ArrayList<Endpoint> result = new ArrayList<>(
                this.active.byHex(normalizeHex(hexColor))
        );

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
            if (endpoint.kind == Kind.PEARL_PORTAL
                    && !endpoint.endpointId.equals(sourcePortalId)) {
                return Optional.of(endpoint);
            }
        }

        return Optional.empty();
    }

    public Optional<DoorEndpoint> getDoorEndpointByOwner(String ownerKey) {
        DoorEndpoint activeDoor = this.doors.byOwner(ownerKey).orElse(null);

        if (activeDoor != null) {
            return Optional.of(activeDoor);
        }

        return this.pendingDoors.byOwner(ownerKey);
    }

    public Optional<DoorEndpoint> getLinkedDoorEndpoint(String sourceOwnerKey) {
        DoorEndpoint source = this.doors.byOwner(sourceOwnerKey).orElse(null);

        if (
                source == null
                        || !isDoorOwnerKey(source.ownerKey())
        ) {
            return Optional.empty();
        }

        for (DoorEndpoint endpoint : this.doors.byHex(source.hexColor)) {
            if (
                    !endpoint.ownerKey.equals(source.ownerKey)
                            && isDoorOwnerKey(endpoint.ownerKey())
            ) {
                return Optional.of(endpoint);
            }
        }

        return Optional.empty();
    }

    public Optional<DoorEndpoint> getTrapdoorEndpointByOwner(String ownerKey) {
        DoorEndpoint endpoint = this.doors.byOwner(ownerKey).orElse(null);

        if (endpoint == null) {
            endpoint = this.pendingDoors.byOwner(ownerKey).orElse(null);
        }

        return endpoint != null
                && isTrapdoorOwnerKey(endpoint.ownerKey())
                ? Optional.of(endpoint)
                : Optional.empty();
    }

    public Optional<DoorEndpoint> getLinkedTrapdoorEndpoint(String sourceOwnerKey) {
        DoorEndpoint source = this.doors.byOwner(sourceOwnerKey).orElse(null);

        if (
                source == null
                        || !isTrapdoorOwnerKey(source.ownerKey())
        ) {
            return Optional.empty();
        }

        for (DoorEndpoint endpoint : this.doors.byHex(source.hexColor)) {
            if (
                    !endpoint.ownerKey.equals(source.ownerKey)
                            && isTrapdoorOwnerKey(endpoint.ownerKey())
            ) {
                return Optional.of(endpoint);
            }
        }

        return Optional.empty();
    }

    public Optional<DoorEndpoint> getFirstPendingDoorEndpointByHex(int hexColor) {
        return this.pendingDoors.firstByHex(normalizeHex(hexColor));
    }

    public boolean isDoorHexReserved(int hexColor) {
        int hex = normalizeHex(hexColor);

        return this.doors.hasHex(hex)
                || this.pendingDoors.hasHex(hex);
    }

    public boolean isDoorWaitingForSecond(int hexColor) {
        return this.pendingDoors.hasHex(hexColor);
    }

    public boolean isHexFullyOccupiedByPortals(int hexColor) {
        int hex = normalizeHex(hexColor);

        if (this.isDoorHexReserved(hex) || this.hasPhoneHex(hex)) {
            return true;
        }

        int portals = 0;

        for (Endpoint endpoint : this.active.byHex(hex)) {
            if (!endpoint.kind.isPortal()) {
                return true;
            }

            portals++;
        }

        return portals >= 2;
    }

    public boolean isHexWaitingForSecondPortal(int hexColor) {
        int hex = normalizeHex(hexColor);

        if (this.isDoorHexReserved(hex) || this.hasPhoneHex(hex)) {
            return false;
        }

        int portals = 0;

        for (Endpoint endpoint : this.active.byHex(hex)) {
            if (endpoint.kind.isPortal()) {
                portals++;
            }
        }

        return portals == 1;
    }

    public Optional<Endpoint> removeEndpoint(UUID endpointId) {
        Endpoint removedActive = this.active.remove(endpointId);
        Endpoint removedPending = removedActive == null
                ? this.pending.remove(endpointId)
                : null;

        if (removedActive != null || removedPending != null) {
            this.setDirty();
        }

        /*
         * Only active endpoints reserve an address.
         * The shared address network only needs the active return value.
         */
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

    public Optional<DoorEndpoint> removeDoorOwner(String ownerKey) {
        ownerKey = normalizeOwnerKey(ownerKey);

        DoorEndpoint removedActive = this.doors.removeOwner(ownerKey);
        DoorEndpoint removedPending = this.pendingDoors.removeOwner(ownerKey);

        DoorEndpoint removed = removedActive != null
                ? removedActive
                : removedPending;

        /*
         * Linked A <-> B:
         *
         * remove A
         *     ↓
         * B becomes pending-for-pair
         *     ↓
         * address stays reserved.
         */
        if (removedActive != null) {
            this.demoteRemainingDoorAtHex(removedActive.hexColor);
        }

        if (removed != null) {
            this.setDirty();
        }

        return Optional.ofNullable(removed);
    }

    public Optional<DoorEndpoint> removeDoorEndpoint(DoorEndpoint expected) {
        if (expected == null) {
            return Optional.empty();
        }

        DoorEndpoint normalized = expected.normalized();
        DoorEndpoint current = this.getDoorEndpointByOwner(normalized.ownerKey).orElse(null);

        if (current == null || !current.equals(normalized)) {
            return Optional.empty();
        }

        return this.removeDoorOwner(normalized.ownerKey);
    }

    public boolean removePendingEndpoint(UUID endpointId) {
        Endpoint removed = this.pending.remove(endpointId);

        if (removed != null) {
            this.setDirty();
        }

        return removed != null;
    }

    private void demoteRemainingDoorAtHex(int hexColor) {
        List<DoorEndpoint> remaining = this.doors.byHex(hexColor);

        if (remaining.size() != 1) {
            return;
        }

        DoorEndpoint partner = remaining.get(0);

        this.doors.removeOwner(partner.ownerKey);
        this.pendingDoors.removeOwner(partner.ownerKey);
        this.pendingDoors.add(partner);
    }

    private static boolean matchesDoorEndpoint(
            DoorEndpoint endpoint,
            int hexColor,
            ResourceLocation dimensionId,
            BlockPos ownerPos
    ) {
        return endpoint != null
                && endpoint.hexColor == normalizeHex(hexColor)
                && endpoint.dimensionId.equals(dimensionId)
                && endpoint.ownerPos.equals(ownerPos);
    }

    private void rebuildIndex() {
        this.active.rebuild(this::canIndexActive);
        this.doors.rebuild(this::canIndexActiveDoor);

        this.normalizeLoadedDoorPairs();

        this.pendingDoors.rebuild(this::canIndexPendingDoor);
        this.pending.rebuild(this::canIndexPending);
    }

    private void normalizeLoadedDoorPairs() {
        ArrayList<Integer> hexes = new ArrayList<>();

        for (DoorEndpoint endpoint : this.doors.entries) {
            if (!hexes.contains(endpoint.hexColor)) {
                hexes.add(endpoint.hexColor);
            }
        }

        for (int hex : hexes) {
            List<DoorEndpoint> endpoints = this.doors.byHex(hex);

            if (endpoints.size() != 1) {
                continue;
            }

            DoorEndpoint endpoint = endpoints.get(0);

            this.doors.removeOwner(endpoint.ownerKey);
            this.pendingDoors.entries.add(endpoint);
        }
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
            return existingAtHex.isEmpty()
                    && !this.isDoorHexReserved(endpoint.hexColor);
        }

        if (this.isDoorHexReserved(endpoint.hexColor)) {
            return false;
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

    private boolean canIndexActiveDoor(DoorEndpoint endpoint) {
        if (!endpoint.hasValidOwner()
                || this.doors.hasOwner(endpoint.ownerKey)
                || this.active.hasHex(endpoint.hexColor)) {
            return false;
        }

        List<DoorEndpoint> sameHex = this.doors.byHex(endpoint.hexColor);

        if (sameHex.size() >= 2) {
            return false;
        }

        if (!sameHex.isEmpty()) {
            boolean endpointTrapdoor = isTrapdoorOwnerKey(endpoint.ownerKey());
            boolean existingTrapdoor = isTrapdoorOwnerKey(sameHex.get(0).ownerKey());

            if (endpointTrapdoor != existingTrapdoor) {
                return false;
            }
        }

        return true;
    }

    private boolean canIndexPendingDoor(DoorEndpoint endpoint) {
        return endpoint.hasValidOwner()
                && !this.active.hasHex(endpoint.hexColor)
                && !this.doors.hasHex(endpoint.hexColor)
                && !this.pendingDoors.hasHex(endpoint.hexColor)
                && !this.doors.hasOwner(endpoint.ownerKey)
                && !this.pendingDoors.hasOwner(endpoint.ownerKey);
    }

    private boolean canIndexPending(Endpoint endpoint) {
        return endpoint.dimensionId != null
                && endpoint.kind.canQueueForAddress()
                && endpoint.hasValidOwner()
                && this.isHexReserved(endpoint.hexColor)
                && !this.active.hasOwner(endpoint.ownerKey)
                && !this.pending.hasId(endpoint.endpointId)
                && !this.pending.hasOwner(endpoint.ownerKey);
    }

    public static String vocoTableReceptorCornerOwnerKey(
            ResourceLocation dimensionId,
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        return ownerKey(
                Kind.VOCO_TABLE_RECEPTOR_CORNER,
                dimensionId,
                pos,
                receptor.id()
        );
    }

    public static String vocoPostReceptorCornerOwnerKey(
            ResourceLocation dimensionId,
            BlockPos pos
    ) {
        return ownerKey(
                Kind.VOCO_POST_RECEPTOR_CORNER,
                dimensionId,
                pos,
                0
        );
    }

    public static String doorOwnerKey(
            ResourceLocation dimensionId,
            BlockPos pos
    ) {
        return "musavacca_door"
                + "|"
                + dimensionId
                + "|"
                + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static String trapdoorOwnerKey(
            ResourceLocation dimensionId,
            BlockPos pos
    ) {
        return "musavacca_trapdoor"
                + "|"
                + dimensionId
                + "|"
                + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    private static boolean isDoorOwnerKey(
            String ownerKey
    ) {
        return normalizeOwnerKey(ownerKey)
                .startsWith(
                        "musavacca_door|"
                );
    }

    private static boolean isTrapdoorOwnerKey(
            String ownerKey
    ) {
        return normalizeOwnerKey(ownerKey)
                .startsWith(
                        "musavacca_trapdoor|"
                );
    }

    public static String ownerKey(
            Kind kind,
            ResourceLocation dimensionId,
            BlockPos pos,
            int slotId
    ) {
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

            return endpointId == null
                    ? null
                    : this.remove(endpointId);
        }

        private Optional<Endpoint> byId(UUID endpointId) {
            return Optional.ofNullable(this.byId.get(endpointId));
        }

        private Optional<Endpoint> byOwner(String ownerKey) {
            UUID endpointId = this.idByOwner.get(normalizeOwnerKey(ownerKey));

            return endpointId == null
                    ? Optional.empty()
                    : this.byId(endpointId);
        }

        private List<Endpoint> byHex(int hexColor) {
            ArrayList<UUID> ids = this.idsByHex.get(normalizeHex(hexColor));

            if (ids == null || ids.isEmpty()) {
                return List.of();
            }

            ArrayList<Endpoint> result = new ArrayList<>(ids.size());

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

        private boolean hasHex(int hexColor) {
            ArrayList<UUID> ids = this.idsByHex.get(normalizeHex(hexColor));
            return ids != null && !ids.isEmpty();
        }

        private boolean hasId(UUID endpointId) {
            return this.byId.containsKey(endpointId);
        }

        private boolean hasOwner(String ownerKey) {
            return this.idByOwner.containsKey(normalizeOwnerKey(ownerKey));
        }
    }

    private static final class DoorEndpointStore {
        private final ArrayList<DoorEndpoint> entries = new ArrayList<>();
        private final HashMap<String, DoorEndpoint> byOwner = new HashMap<>();
        private final HashMap<Integer, ArrayList<String>> ownersByHex = new HashMap<>();

        private void rebuild(Predicate<DoorEndpoint> validator) {
            ArrayList<DoorEndpoint> raw = new ArrayList<>(this.entries);
            this.clear();

            for (DoorEndpoint endpoint : raw) {
                if (endpoint == null) {
                    continue;
                }

                DoorEndpoint normalized = endpoint.normalized();

                if (validator.test(normalized)) {
                    this.add(normalized);
                }
            }
        }

        private void clear() {
            this.entries.clear();
            this.byOwner.clear();
            this.ownersByHex.clear();
        }

        private void add(DoorEndpoint endpoint) {
            endpoint = endpoint.normalized();

            this.entries.add(endpoint);
            this.byOwner.put(endpoint.ownerKey, endpoint);

            this.ownersByHex
                    .computeIfAbsent(endpoint.hexColor, ignored -> new ArrayList<>())
                    .add(endpoint.ownerKey);
        }

        private DoorEndpoint removeOwner(String ownerKey) {
            ownerKey = normalizeOwnerKey(ownerKey);

            DoorEndpoint endpoint = this.byOwner.remove(ownerKey);

            if (endpoint == null) {
                return null;
            }

            ArrayList<String> owners = this.ownersByHex.get(endpoint.hexColor);

            if (owners != null) {
                owners.remove(ownerKey);

                if (owners.isEmpty()) {
                    this.ownersByHex.remove(endpoint.hexColor);
                }
            }

            String finalOwnerKey = ownerKey;
            this.entries.removeIf(entry -> entry.ownerKey.equals(finalOwnerKey));

            return endpoint;
        }

        private Optional<DoorEndpoint> byOwner(String ownerKey) {
            return Optional.ofNullable(
                    this.byOwner.get(
                            normalizeOwnerKey(ownerKey)
                    )
            );
        }

        private List<DoorEndpoint> byHex(int hexColor) {
            ArrayList<String> owners = this.ownersByHex.get(normalizeHex(hexColor));

            if (owners == null || owners.isEmpty()) {
                return List.of();
            }

            ArrayList<DoorEndpoint> result = new ArrayList<>(owners.size());

            for (String ownerKey : owners) {
                DoorEndpoint endpoint = this.byOwner.get(ownerKey);

                if (endpoint != null) {
                    result.add(endpoint);
                }
            }

            return result;
        }

        private Optional<DoorEndpoint> firstByHex(int hexColor) {
            List<DoorEndpoint> endpoints = this.byHex(hexColor);

            return endpoints.isEmpty()
                    ? Optional.empty()
                    : Optional.of(endpoints.get(0));
        }

        private boolean hasHex(int hexColor) {
            ArrayList<String> owners = this.ownersByHex.get(normalizeHex(hexColor));
            return owners != null && !owners.isEmpty();
        }

        private int countByHex(int hexColor) {
            ArrayList<String> owners = this.ownersByHex.get(normalizeHex(hexColor));
            return owners == null ? 0 : owners.size();
        }

        private boolean hasOwner(String ownerKey) {
            return this.byOwner.containsKey(
                    normalizeOwnerKey(ownerKey)
            );
        }
    }
}
