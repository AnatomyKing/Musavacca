package space.anatomyuniverse.musavacca.teleport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
//? if >=1.21.5 {
import net.minecraft.world.level.saveddata.SavedDataType;
//?} else {
/*import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
*///?}
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.portal.PearlPortalFrame;

public final class HexTeleportDirectory extends SavedData {
    public static final String STORAGE_ID = "musavacca_unified_teleports";
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(text -> {
        try {
            return DataResult.success(UUID.fromString(text));
        } catch (IllegalArgumentException error) {
            return DataResult.error(() -> "Invalid UUID: " + text);
        }
    }, UUID::toString);

    public enum Kind {
        PEARL_PORTAL("pearl_portal"), VOCO_TABLE_RECEPTOR_CORNER("voco_table_receptor_corner"), VOCO_POST_RECEPTOR_CORNER("voco_post_receptor_corner"),
        PORTAL_DOOR("portal_door"), PORTAL_TRAPDOOR("portal_trapdoor"), VOCO_CALLER("voco_caller");

        private final String name;

        Kind(String name) { this.name = name; }

        public String serializedName() { return this.name; }

        public boolean isPortal() { return this == PEARL_PORTAL; }

        public boolean isVoco() { return this == VOCO_TABLE_RECEPTOR_CORNER || this == VOCO_POST_RECEPTOR_CORNER; }

        public boolean isDoor() { return this == PORTAL_DOOR || this == PORTAL_TRAPDOOR; }

        public boolean canQueueForAddress() { return this.isVoco(); }

        public static Kind fromSerializedName(String name) {
            for (Kind kind : values()) if (kind.name.equals(name)) return kind;
            throw new IllegalArgumentException("Unknown endpoint kind " + name);
        }
    }

    private static final Codec<Kind> KIND_CODEC = Codec.STRING.comapFlatMap(name -> {
        try {
            return DataResult.success(Kind.fromSerializedName(name));
        } catch (IllegalArgumentException error) {
            return DataResult.error(error::getMessage);
        }
    }, Kind::serializedName);

    public enum Result {
        REGISTERED, UPDATED, WAITING_FOR_SECOND_PORTAL, LINKED_TO_EXISTING_PORTAL, WAITING_FOR_SECOND_DOOR, LINKED_TO_EXISTING_DOOR,
        WAITING_FOR_SECOND_TRAPDOOR, LINKED_TO_EXISTING_TRAPDOOR, ALREADY_REGISTERED, HEX_OCCUPIED, INVALID_OWNER;

        public boolean success() {
            return this != HEX_OCCUPIED && this != INVALID_OWNER;
        }
    }

    public record EndpointKey(UUID deviceId, int slotId) {}

    public record Endpoint(UUID deviceId, int slotId, Kind kind, int hexColor, String dimension, BlockPos ownerPos, boolean pending) {
        public static final Codec<Endpoint> CODEC = RecordCodecBuilder.create(i -> i.group(
                UUID_CODEC.fieldOf("device").forGetter(Endpoint::deviceId),
                Codec.INT.fieldOf("slot").forGetter(Endpoint::slotId),
                KIND_CODEC.fieldOf("kind").forGetter(Endpoint::kind),
                Codec.INT.fieldOf("hex").forGetter(Endpoint::hexColor),
                Codec.STRING.fieldOf("dimension").forGetter(Endpoint::dimension),
                BlockPos.CODEC.fieldOf("pos").forGetter(Endpoint::ownerPos),
                Codec.BOOL.optionalFieldOf("pending", false).forGetter(Endpoint::pending)
        ).apply(i, Endpoint::new));

        public EndpointKey key() { return new EndpointKey(this.deviceId, this.slotId); }

        public UUID endpointId() {
            if (this.slotId == 0) return this.deviceId;
            return UUID.nameUUIDFromBytes((this.deviceId + ":" + this.slotId).getBytes(StandardCharsets.UTF_8));
        }

        public String ownerKey() { return HexTeleportDirectory.ownerKey(this.kind, this.deviceId, this.slotId); }

        public boolean hasValidOwner() { return this.deviceId != null && (this.kind == Kind.VOCO_CALLER || !this.dimension.isEmpty()); }

        //? if >=1.21.11 {
        /*public Identifier dimensionId() { return this.dimension.isEmpty() ? null : Identifier.parse(this.dimension); }

        *///?} else {
        public ResourceLocation dimensionId() { return this.dimension.isEmpty() ? null : ResourceLocation.parse(this.dimension); }

        //?}
        public Endpoint moved(String newDimension, BlockPos newPos) {
            return new Endpoint(this.deviceId, this.slotId, this.kind, this.hexColor, newDimension, newPos.immutable(), this.pending);
        }

        public Endpoint queued(boolean value) {
            return new Endpoint(this.deviceId, this.slotId, this.kind, this.hexColor, this.dimension, this.ownerPos, value);
        }
    }

    public record DoorEndpoint(UUID deviceId, Kind kind, int hexColor, String dimension, BlockPos ownerPos) {
        public String ownerKey() { return HexTeleportDirectory.ownerKey(this.kind, this.deviceId, 0); }

        //? if >=1.21.11 {
        /*public Identifier dimensionId() { return Identifier.parse(this.dimension); }

        *///?} else {
        public ResourceLocation dimensionId() { return ResourceLocation.parse(this.dimension); }

        //?}
        public static DoorEndpoint of(Endpoint endpoint) {
            return new DoorEndpoint(endpoint.deviceId(), endpoint.kind(), endpoint.hexColor(), endpoint.dimension(), endpoint.ownerPos());
        }
    }

    public record PhoneRegistration(int hexColor, UUID ownerUuid) {}

    public record VocoRegistration(Result result, Endpoint removedActiveEndpoint) {}

    public static final Codec<HexTeleportDirectory> CODEC = RecordCodecBuilder.create(i -> i.group(
            Endpoint.CODEC.listOf().fieldOf("claims").forGetter(d -> d.savedClaims())
    ).apply(i, HexTeleportDirectory::new));
    //? if >=26.1 {
    /*public static final SavedDataType<HexTeleportDirectory> TYPE = new SavedDataType<>(Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, STORAGE_ID),
            HexTeleportDirectory::new, CODEC);
    *///?} else if >=1.21.5 <26.1 {
    public static final SavedDataType<HexTeleportDirectory> TYPE = new SavedDataType<>(STORAGE_ID, HexTeleportDirectory::new, CODEC);
    //?} else {
    /*private static final SavedData.Factory<HexTeleportDirectory> FACTORY = new SavedData.Factory<>(HexTeleportDirectory::new, HexTeleportDirectory::load);
    *///?}

    private final Map<EndpointKey, Endpoint> claims = new LinkedHashMap<>();
    private final Map<UUID, EndpointKey> keysByEndpointId = new HashMap<>();
    private final Map<UUID, ArrayList<EndpointKey>> keysByDevice = new HashMap<>();
    private final Map<Integer, ArrayList<EndpointKey>> activeByHex = new HashMap<>();
    private final Map<Integer, ArrayList<EndpointKey>> pendingByHex = new HashMap<>();

    public HexTeleportDirectory() {}

    private HexTeleportDirectory(List<Endpoint> loaded) {
        for (Endpoint endpoint : loaded) {
            if (endpoint == null || !endpoint.hasValidOwner() || this.claims.containsKey(endpoint.key())) continue;
            if (endpoint.kind() == Kind.VOCO_CALLER && endpoint.slotId() != endpoint.hexColor()) continue;
            if (!endpoint.pending() && !this.canClaim(endpoint, endpoint.key())) continue;
            if (endpoint.pending() && !endpoint.kind().canQueueForAddress()) continue;
            this.putRaw(endpoint);
        }
    }

    private List<Endpoint> savedClaims() { return List.copyOf(this.claims.values()); }

    //? if <1.21.5 {
    /*private static HexTeleportDirectory load(CompoundTag tag, HolderLookup.Provider registries) {
        return CODEC.parse(NbtOps.INSTANCE, tag).result().orElseThrow(() -> new IllegalStateException("Invalid Musavacca teleport directory"));
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        Tag saved = CODEC.encodeStart(NbtOps.INSTANCE, this).result().orElseThrow(() -> new IllegalStateException("Cannot save Musavacca teleport directory"));
        if (saved instanceof CompoundTag compound) return compound;
        throw new IllegalStateException("Teleport directory codec returned an invalid tag");
    }
    *///?}

    public static HexTeleportDirectory get(MinecraftServer server) {
        //? if >=1.21.5 {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
        //?} else {
        /*return server.overworld().getDataStorage().computeIfAbsent(FACTORY, STORAGE_ID);
        *///?}
    }

    public static int normalizeHex(int hexColor) { return hexColor & 0xFFFFFF; }

    public static String toHex(int hexColor) { return String.format(Locale.ROOT, "%06X", normalizeHex(hexColor)); }

    public static String ownerKey(Kind kind, UUID deviceId, int slot) {
        return kind.serializedName() + "|" + deviceId + "|" + slot;
    }

    public static String doorOwnerKey(UUID deviceId) { return ownerKey(Kind.PORTAL_DOOR, deviceId, 0); }

    public static String trapdoorOwnerKey(UUID deviceId) { return ownerKey(Kind.PORTAL_TRAPDOOR, deviceId, 0); }

    public static String vocoTableReceptorCornerOwnerKey(UUID deviceId, ReceptorPosition receptor) {
        return ownerKey(Kind.VOCO_TABLE_RECEPTOR_CORNER, deviceId, receptor.id());
    }

    public static String vocoPostReceptorCornerOwnerKey(UUID deviceId) {
        return ownerKey(Kind.VOCO_POST_RECEPTOR_CORNER, deviceId, 0);
    }

    private static EndpointKey parseOwner(String ownerKey) {
        if (ownerKey == null) return null;
        String[] parts = ownerKey.split("\\|", -1);
        if (parts.length != 3) return null;
        try {
            Kind.fromSerializedName(parts[0]);
            return new EndpointKey(UUID.fromString(parts[1]), Integer.parseInt(parts[2]));
        } catch (IllegalArgumentException error) {
            return null;
        }
    }

    private Endpoint byOwner(String ownerKey) {
        EndpointKey key = parseOwner(ownerKey);
        Endpoint endpoint = key == null ? null : this.claims.get(key);
        return endpoint != null && endpoint.ownerKey().equals(ownerKey) ? endpoint : null;
    }

    private List<Endpoint> byHex(int hex) {
        ArrayList<EndpointKey> keys = this.activeByHex.get(normalizeHex(hex));
        if (keys == null) return List.of();
        ArrayList<Endpoint> result = new ArrayList<>(keys.size());
        for (EndpointKey key : keys) {
            Endpoint endpoint = this.claims.get(key);
            if (endpoint != null) result.add(endpoint);
        }
        return result;
    }

    private void putRaw(Endpoint endpoint) {
        this.claims.put(endpoint.key(), endpoint);
        this.keysByEndpointId.put(endpoint.endpointId(), endpoint.key());
        this.keysByDevice.computeIfAbsent(endpoint.deviceId(), ignored -> new ArrayList<>()).add(endpoint.key());
        (endpoint.pending() ? this.pendingByHex : this.activeByHex).computeIfAbsent(endpoint.hexColor(), ignored -> new ArrayList<>()).add(endpoint.key());
    }

    private Endpoint removeRaw(EndpointKey key) {
        Endpoint old = this.claims.remove(key);
        if (old == null) return null;
        this.keysByEndpointId.remove(old.endpointId());
        ArrayList<EndpointKey> deviceKeys = this.keysByDevice.get(old.deviceId());
        if (deviceKeys != null) {
            deviceKeys.remove(key);
            if (deviceKeys.isEmpty()) this.keysByDevice.remove(old.deviceId());
        }
        Map<Integer, ArrayList<EndpointKey>> index = old.pending() ? this.pendingByHex : this.activeByHex;
        ArrayList<EndpointKey> keys = index.get(old.hexColor());
        if (keys != null) {
            keys.remove(key);
            if (keys.isEmpty()) index.remove(old.hexColor());
        }
        return old;
    }

    private void replace(Endpoint endpoint) {
        Endpoint old = this.claims.get(endpoint.key());
        if (endpoint.equals(old)) return;
        if (old != null) this.removeRaw(old.key());
        this.putRaw(endpoint);
        this.setDirty();
    }

    private boolean canClaim(Endpoint candidate, EndpointKey self) {
        ArrayList<EndpointKey> keys = this.activeByHex.get(candidate.hexColor());
        if (keys == null) return true;
        int peers = 0;
        for (EndpointKey key : keys) {
            if (key.equals(self)) continue;
            Endpoint other = this.claims.get(key);
            if (other == null) continue;
            if (candidate.kind().isPortal() && other.kind().isPortal() || candidate.kind().isDoor() && other.kind() == candidate.kind()) {
                peers++;
                continue;
            }
            return false;
        }
        return (candidate.kind().isPortal() || candidate.kind().isDoor()) ? peers < 2 : peers == 0;
    }

    public boolean isHexReserved(int hex) {
        ArrayList<EndpointKey> keys = this.activeByHex.get(normalizeHex(hex));
        return keys != null && !keys.isEmpty();
    }

    //? if >=1.21.11 {
    /*public VocoRegistration registerOrQueueVocoEndpoint(UUID deviceId, Kind kind, int hex, Identifier dimension, BlockPos pos,
    *///?} else {
    public VocoRegistration registerOrQueueVocoEndpoint(UUID deviceId, Kind kind, int hex, ResourceLocation dimension, BlockPos pos,
    //?}
            int slot) {
        if (deviceId == null || kind == null || !kind.isVoco() || dimension == null || pos == null) {
            return new VocoRegistration(Result.INVALID_OWNER, null);
        }
        int normalized = normalizeHex(hex);
        Endpoint candidate = new Endpoint(deviceId, kind == Kind.VOCO_POST_RECEPTOR_CORNER ? 0 : slot, kind, normalized, dimension.toString(), pos.immutable(),
                false);
        Endpoint previous = this.claims.get(candidate.key());
        if (previous != null && previous.kind() != kind) return new VocoRegistration(Result.INVALID_OWNER, null);
        boolean occupied = !this.canClaim(candidate, candidate.key());
        Endpoint saved = candidate.queued(occupied);
        this.replace(saved);
        Endpoint removedActive = previous != null && !previous.pending() && previous.hexColor() != normalized ? previous : null;
        return new VocoRegistration(occupied ? Result.HEX_OCCUPIED : previous == null ? Result.REGISTERED : Result.UPDATED, removedActive);
    }

    public Result checkPortalEndpoint(UUID portalId, int hexColor) {
        if (portalId == null) return Result.INVALID_OWNER;
        int others = 0;
        ArrayList<EndpointKey> keys = this.activeByHex.get(normalizeHex(hexColor));
        if (keys != null) for (EndpointKey key : keys) {
            Endpoint endpoint = this.claims.get(key);
            if (endpoint == null) continue;
            if (endpoint.kind() != Kind.PEARL_PORTAL) return Result.HEX_OCCUPIED;
            if (!endpoint.deviceId().equals(portalId)) others++;
        }
        if (others >= 2) return Result.HEX_OCCUPIED;
        return others == 0 ? Result.WAITING_FOR_SECOND_PORTAL : Result.LINKED_TO_EXISTING_PORTAL;
    }

    //? if >=1.21.11 {
    /*public Result registerPortalEndpoint(UUID portalId, int hexColor, Identifier dimension, PearlPortalFrame.Shape shape) {
    *///?} else {
    public Result registerPortalEndpoint(UUID portalId, int hexColor, ResourceLocation dimension, PearlPortalFrame.Shape shape) {
    //?}
        if (portalId == null || dimension == null || shape == null) return Result.INVALID_OWNER;
        Result check = this.checkPortalEndpoint(portalId, hexColor);
        if (!check.success()) return check;
        this.replace(new Endpoint(portalId, 0, Kind.PEARL_PORTAL, normalizeHex(hexColor), dimension.toString(), shape.minCorner().immutable(), false));
        return check;
    }

    //? if >=1.21.11 {
    /*public Result registerDoorEndpoint(UUID deviceId, int hex, Identifier dimension, BlockPos pos) {
    *///?} else {
    public Result registerDoorEndpoint(UUID deviceId, int hex, ResourceLocation dimension, BlockPos pos) {
    //?}
        return this.registerPhysicalDoor(deviceId, hex, dimension == null ? null : dimension.toString(), pos, Kind.PORTAL_DOOR);
    }

    //? if >=1.21.11 {
    /*public Result registerTrapdoorEndpoint(UUID deviceId, int hex, Identifier dimension, BlockPos pos) {
    *///?} else {
    public Result registerTrapdoorEndpoint(UUID deviceId, int hex, ResourceLocation dimension, BlockPos pos) {
    //?}
        return this.registerPhysicalDoor(deviceId, hex, dimension == null ? null : dimension.toString(), pos, Kind.PORTAL_TRAPDOOR);
    }

    private Result registerPhysicalDoor(UUID deviceId, int hex, String dimension, BlockPos pos, Kind kind) {
        if (deviceId == null || dimension == null || pos == null) return Result.INVALID_OWNER;
        Endpoint candidate = new Endpoint(deviceId, 0, kind, normalizeHex(hex), dimension, pos.immutable(), false);
        Endpoint previous = this.claims.get(candidate.key());
        if (previous != null && previous.kind() != kind) return Result.INVALID_OWNER;
        if (!this.canClaim(candidate, candidate.key())) return Result.HEX_OCCUPIED;
        int peers = 0;
        ArrayList<EndpointKey> keys = this.activeByHex.get(candidate.hexColor());
        if (keys != null) for (EndpointKey key : keys) {
            Endpoint other = this.claims.get(key);
            if (other != null && other.kind() == kind && !key.equals(candidate.key())) peers++;
        }
        Result result = kind == Kind.PORTAL_DOOR ? (peers == 0 ? Result.WAITING_FOR_SECOND_DOOR : Result.LINKED_TO_EXISTING_DOOR)
                : (peers == 0 ? Result.WAITING_FOR_SECOND_TRAPDOOR : Result.LINKED_TO_EXISTING_TRAPDOOR);
        this.replace(candidate);
        return result;
    }

    public Result registerPhone(int hex, UUID playerId) {
        if (playerId == null) return Result.INVALID_OWNER;
        int normalized = normalizeHex(hex);
        Endpoint phone = new Endpoint(playerId, normalized, Kind.VOCO_CALLER, normalized, "", BlockPos.ZERO, false);
        if (this.claims.containsKey(phone.key())) return Result.ALREADY_REGISTERED;
        if (!this.canClaim(phone, phone.key())) return Result.HEX_OCCUPIED;
        this.replace(phone);
        return Result.REGISTERED;
    }

    public Optional<PhoneRegistration> getPhoneRegistrationByHex(int hex) {
        ArrayList<EndpointKey> keys = this.activeByHex.get(normalizeHex(hex));
        if (keys == null) return Optional.empty();
        for (EndpointKey key : keys) {
            Endpoint endpoint = this.claims.get(key);
            if (endpoint != null && endpoint.kind() == Kind.VOCO_CALLER) return Optional.of(new PhoneRegistration(endpoint.hexColor(), endpoint.deviceId()));
        }
        return Optional.empty();
    }

    public Optional<PhoneRegistration> removePhoneRegistration(int hex, UUID playerId) {
        if (playerId == null) return Optional.empty();
        EndpointKey key = new EndpointKey(playerId, normalizeHex(hex));
        Endpoint old = this.claims.get(key);
        if (old == null || old.kind() != Kind.VOCO_CALLER || old.hexColor() != normalizeHex(hex)) return Optional.empty();
        this.removeRaw(key);
        this.setDirty();
        return Optional.of(new PhoneRegistration(old.hexColor(), old.deviceId()));
    }

    public Optional<Endpoint> getPhysicalDevice(UUID deviceId) {
        ArrayList<EndpointKey> keys = this.keysByDevice.get(deviceId);
        if (keys == null) return Optional.empty();
        for (EndpointKey key : keys) {
            Endpoint endpoint = this.claims.get(key);
            if (endpoint != null && endpoint.kind() != Kind.VOCO_CALLER) return Optional.of(endpoint);
        }
        return Optional.empty();
    }

    public Optional<Endpoint> getEndpoint(UUID endpointId) {
        EndpointKey key = this.keysByEndpointId.get(endpointId);
        Endpoint endpoint = key == null ? null : this.claims.get(key);
        return endpoint != null && !endpoint.pending() && !endpoint.kind().isDoor() && endpoint.kind() != Kind.VOCO_CALLER ? Optional.of(endpoint)
                : Optional.empty();
    }

    public Optional<Endpoint> getFirstPendingEndpointByHex(int hex) {
        ArrayList<EndpointKey> keys = this.pendingByHex.get(normalizeHex(hex));
        if (keys == null) return Optional.empty();
        for (EndpointKey key : keys) {
            Endpoint endpoint = this.claims.get(key);
            if (endpoint != null && endpoint.kind().isVoco()) return Optional.of(endpoint);
        }
        return Optional.empty();
    }

    public List<Endpoint> getEndpointsByHex(int hex) {
        return this.byHex(hex).stream().filter(e -> e.kind().isVoco() || e.kind().isPortal()).sorted(Comparator.comparingInt(e -> switch (e.kind()) {
        case VOCO_POST_RECEPTOR_CORNER -> 0;
        case VOCO_TABLE_RECEPTOR_CORNER -> 1;
        default -> 2;
        })).toList();
    }

    public Optional<Endpoint> getLinkedPortalEndpoint(UUID portalId) {
        Endpoint source = this.claims.get(new EndpointKey(portalId, 0));
        if (source == null || !source.kind().isPortal()) return Optional.empty();
        ArrayList<EndpointKey> keys = this.activeByHex.get(source.hexColor());
        if (keys == null) return Optional.empty();
        for (EndpointKey key : keys) {
            Endpoint endpoint = this.claims.get(key);
            if (endpoint != null && endpoint.kind().isPortal() && !endpoint.deviceId().equals(portalId)) return Optional.of(endpoint);
        }
        return Optional.empty();
    }

    public Optional<DoorEndpoint> getDoorEndpointByOwner(String ownerKey) {
        Endpoint source = this.byOwner(ownerKey);
        return source != null && source.kind() == Kind.PORTAL_DOOR ? Optional.of(DoorEndpoint.of(source)) : Optional.empty();
    }

    public Optional<DoorEndpoint> getTrapdoorEndpointByOwner(String ownerKey) {
        Endpoint source = this.byOwner(ownerKey);
        return source != null && source.kind() == Kind.PORTAL_TRAPDOOR ? Optional.of(DoorEndpoint.of(source)) : Optional.empty();
    }

    private Optional<DoorEndpoint> linkedDoor(String ownerKey, Kind kind) {
        Endpoint source = this.byOwner(ownerKey);
        if (source == null || source.kind() != kind) return Optional.empty();
        ArrayList<EndpointKey> keys = this.activeByHex.get(source.hexColor());
        if (keys == null) return Optional.empty();
        for (EndpointKey key : keys) {
            Endpoint endpoint = this.claims.get(key);
            if (endpoint != null && endpoint.kind() == kind && !key.equals(source.key())) return Optional.of(DoorEndpoint.of(endpoint));
        }
        return Optional.empty();
    }

    public Optional<DoorEndpoint> getLinkedDoorEndpoint(String sourceOwnerKey) {
        return this.linkedDoor(sourceOwnerKey, Kind.PORTAL_DOOR);
    }

    public Optional<DoorEndpoint> getLinkedTrapdoorEndpoint(String sourceOwnerKey) {
        return this.linkedDoor(sourceOwnerKey, Kind.PORTAL_TRAPDOOR);
    }

    public Optional<Endpoint> removeEndpoint(UUID endpointId) {
        Endpoint old = this.getEndpoint(endpointId).orElse(null);
        if (old == null) return Optional.empty();
        this.removeRaw(old.key());
        this.setDirty();
        return Optional.of(old);
    }

    public Optional<Endpoint> removeOwner(String ownerKey) {
        Endpoint old = this.byOwner(ownerKey);
        if (old == null || !old.kind().isVoco()) return Optional.empty();
        this.removeRaw(old.key());
        this.setDirty();
        return old.pending() ? Optional.empty() : Optional.of(old);
    }

    public Optional<DoorEndpoint> removeDoorOwner(String ownerKey) {
        Endpoint old = this.byOwner(ownerKey);
        if (old == null || !old.kind().isDoor()) return Optional.empty();
        this.removeRaw(old.key());
        this.setDirty();
        return Optional.of(DoorEndpoint.of(old));
    }

    public Optional<Endpoint> promotePendingForHex(int hex) {
        if (this.isHexReserved(hex)) return Optional.empty();
        Endpoint first = this.getFirstPendingEndpointByHex(hex).orElse(null);
        if (first == null) return Optional.empty();
        Endpoint promoted = first.queued(false);
        this.replace(promoted);
        return Optional.of(promoted);
    }

}
