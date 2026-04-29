// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/portal/PearlPortalDirectory.java
package space.anatomyuniverse.musavacca.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class PearlPortalDirectory extends SavedData {
    public static final String STORAGE_ID = "pearl_portal_directory";

    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    private static final Codec<Direction.Axis> AXIS_CODEC = Codec.STRING.xmap(
            value -> "z".equalsIgnoreCase(value) ? Direction.Axis.Z : Direction.Axis.X,
            axis -> axis == Direction.Axis.Z ? "z" : "x"
    );

    public enum LinkResult {
        WAITING_FOR_SECOND_PORTAL,
        LINKED_TO_EXISTING_PORTAL,
        HEX_ALREADY_USED,
        ALREADY_LINKED
    }

    public record Endpoint(
            UUID portalId,
            ResourceLocation dimensionId,
            int originX,
            int originY,
            int originZ,
            Direction.Axis axis,
            int width,
            int height,
            int hexColor
    ) {
        public Endpoint normalized() {
            return new Endpoint(
                    portalId,
                    dimensionId,
                    originX,
                    originY,
                    originZ,
                    axis == Direction.Axis.Z ? Direction.Axis.Z : Direction.Axis.X,
                    clamp(width, PearlPortalFrame.MIN_WIDTH, PearlPortalFrame.MAX_WIDTH),
                    clamp(height, PearlPortalFrame.MIN_HEIGHT, PearlPortalFrame.MAX_HEIGHT),
                    hexColor & 0xFFFFFF
            );
        }

        public BlockPos originPos() {
            return new BlockPos(originX, originY, originZ);
        }

        public PearlPortalFrame.Shape shape() {
            Endpoint endpoint = this.normalized();

            return new PearlPortalFrame.Shape(
                    endpoint.axis,
                    endpoint.originPos(),
                    endpoint.width,
                    endpoint.height
            );
        }
    }

    public record WaitingEntry(int hexColor, UUID portalId) {
        public WaitingEntry normalized() {
            return new WaitingEntry(hexColor & 0xFFFFFF, portalId);
        }
    }

    public record LinkEntry(int hexColor, UUID firstPortalId, UUID secondPortalId) {
        public LinkEntry normalized() {
            return new LinkEntry(hexColor & 0xFFFFFF, firstPortalId, secondPortalId);
        }

        public boolean contains(UUID portalId) {
            return firstPortalId.equals(portalId) || secondPortalId.equals(portalId);
        }

        public UUID other(UUID portalId) {
            if (firstPortalId.equals(portalId)) return secondPortalId;
            if (secondPortalId.equals(portalId)) return firstPortalId;
            return null;
        }
    }

    private static final Codec<Endpoint> ENDPOINT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUID_CODEC.fieldOf("portal_id").forGetter(Endpoint::portalId),
            ResourceLocation.CODEC.fieldOf("dimension").forGetter(Endpoint::dimensionId),
            Codec.INT.fieldOf("origin_x").forGetter(Endpoint::originX),
            Codec.INT.fieldOf("origin_y").forGetter(Endpoint::originY),
            Codec.INT.fieldOf("origin_z").forGetter(Endpoint::originZ),
            AXIS_CODEC.fieldOf("axis").forGetter(Endpoint::axis),
            Codec.INT.fieldOf("width").forGetter(Endpoint::width),
            Codec.INT.fieldOf("height").forGetter(Endpoint::height),
            Codec.INT.fieldOf("hex_color").forGetter(Endpoint::hexColor)
    ).apply(instance, Endpoint::new));

    private static final Codec<WaitingEntry> WAITING_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("hex_color").forGetter(WaitingEntry::hexColor),
            UUID_CODEC.fieldOf("portal_id").forGetter(WaitingEntry::portalId)
    ).apply(instance, WaitingEntry::new));

    private static final Codec<LinkEntry> LINK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("hex_color").forGetter(LinkEntry::hexColor),
            UUID_CODEC.fieldOf("first").forGetter(LinkEntry::firstPortalId),
            UUID_CODEC.fieldOf("second").forGetter(LinkEntry::secondPortalId)
    ).apply(instance, LinkEntry::new));

    public static final Codec<PearlPortalDirectory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ENDPOINT_CODEC.listOf().optionalFieldOf("endpoints", List.of()).forGetter(data -> data.endpoints),
            WAITING_CODEC.listOf().optionalFieldOf("waiting", List.of()).forGetter(data -> data.waitingEntries),
            LINK_CODEC.listOf().optionalFieldOf("links", List.of()).forGetter(data -> data.linkEntries)
    ).apply(instance, PearlPortalDirectory::new));

    public static final SavedDataType<PearlPortalDirectory> TYPE =
            new SavedDataType<>(STORAGE_ID, PearlPortalDirectory::new, CODEC);

    private final ArrayList<Endpoint> endpoints;
    private final ArrayList<WaitingEntry> waitingEntries;
    private final ArrayList<LinkEntry> linkEntries;

    private transient HashMap<UUID, Integer> endpointIndexByPortalId;
    private transient HashMap<Integer, UUID> waitingPortalByHex;
    private transient HashMap<Integer, LinkEntry> linkByHex;
    private transient HashMap<UUID, UUID> linkedPortalByPortalId;
    private transient HashMap<UUID, Integer> linkedHexByPortalId;

    public PearlPortalDirectory() {
        this.endpoints = new ArrayList<>();
        this.waitingEntries = new ArrayList<>();
        this.linkEntries = new ArrayList<>();
        this.rebuildIndex();
    }

    private PearlPortalDirectory(List<Endpoint> endpoints, List<WaitingEntry> waitingEntries, List<LinkEntry> linkEntries) {
        this.endpoints = new ArrayList<>();
        for (Endpoint endpoint : endpoints) {
            this.endpoints.add(endpoint.normalized());
        }

        this.waitingEntries = new ArrayList<>();
        for (WaitingEntry waitingEntry : waitingEntries) {
            this.waitingEntries.add(waitingEntry.normalized());
        }

        this.linkEntries = new ArrayList<>();
        for (LinkEntry linkEntry : linkEntries) {
            this.linkEntries.add(linkEntry.normalized());
        }

        this.rebuildIndex();
    }

    public static PearlPortalDirectory get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    private void rebuildIndex() {
        this.endpointIndexByPortalId = new HashMap<>();
        this.waitingPortalByHex = new HashMap<>();
        this.linkByHex = new HashMap<>();
        this.linkedPortalByPortalId = new HashMap<>();
        this.linkedHexByPortalId = new HashMap<>();

        for (int i = 0; i < this.endpoints.size(); i++) {
            this.endpointIndexByPortalId.put(this.endpoints.get(i).portalId(), i);
        }

        for (WaitingEntry waitingEntry : this.waitingEntries) {
            int hex = normalizeHex(waitingEntry.hexColor());
            this.waitingPortalByHex.put(hex, waitingEntry.portalId());
        }

        for (LinkEntry linkEntry : this.linkEntries) {
            int hex = normalizeHex(linkEntry.hexColor());

            this.linkByHex.put(hex, linkEntry);
            this.linkedPortalByPortalId.put(linkEntry.firstPortalId(), linkEntry.secondPortalId());
            this.linkedPortalByPortalId.put(linkEntry.secondPortalId(), linkEntry.firstPortalId());
            this.linkedHexByPortalId.put(linkEntry.firstPortalId(), hex);
            this.linkedHexByPortalId.put(linkEntry.secondPortalId(), hex);

            this.waitingPortalByHex.remove(hex);
        }
    }

    public Optional<Endpoint> getEndpoint(UUID portalId) {
        Integer index = this.endpointIndexByPortalId.get(portalId);
        return index == null ? Optional.empty() : Optional.of(this.endpoints.get(index));
    }

    public Optional<UUID> getLinkedPortalId(UUID sourcePortalId) {
        return Optional.ofNullable(this.linkedPortalByPortalId.get(sourcePortalId));
    }

    public Optional<Integer> getLinkedHex(UUID portalId) {
        return Optional.ofNullable(this.linkedHexByPortalId.get(portalId));
    }

    public boolean isHexFullyLinked(int hexColor) {
        return this.linkByHex.containsKey(normalizeHex(hexColor));
    }

    public void upsertEndpoint(UUID portalId, ResourceLocation dimensionId, PearlPortalFrame.Shape shape, int hexColor) {
        Endpoint endpoint = new Endpoint(
                portalId,
                dimensionId,
                shape.minCorner().getX(),
                shape.minCorner().getY(),
                shape.minCorner().getZ(),
                shape.axis(),
                shape.width(),
                shape.height(),
                normalizeHex(hexColor)
        ).normalized();

        Integer index = this.endpointIndexByPortalId.get(portalId);
        if (index == null) {
            this.endpoints.add(endpoint);
            this.endpointIndexByPortalId.put(portalId, this.endpoints.size() - 1);
            this.setDirty();
            return;
        }

        Endpoint old = this.endpoints.get(index);
        if (!old.equals(endpoint)) {
            this.endpoints.set(index, endpoint);
            this.setDirty();
        }
    }

    public LinkResult linkOrWait(UUID portalId, int hexColor) {
        int hex = normalizeHex(hexColor);

        LinkEntry existingLink = this.linkByHex.get(hex);
        if (existingLink != null) {
            return existingLink.contains(portalId)
                    ? LinkResult.ALREADY_LINKED
                    : LinkResult.HEX_ALREADY_USED;
        }

        UUID waitingPortal = this.waitingPortalByHex.get(hex);
        if (waitingPortal != null && !waitingPortal.equals(portalId)) {
            if (!this.endpointIndexByPortalId.containsKey(waitingPortal)) {
                this.removeWaiting(hex);
                this.addWaiting(hex, portalId);
                return LinkResult.WAITING_FOR_SECOND_PORTAL;
            }

            this.removeWaiting(hex);

            LinkEntry link = new LinkEntry(hex, waitingPortal, portalId).normalized();
            this.linkEntries.add(link);

            this.linkByHex.put(hex, link);
            this.linkedPortalByPortalId.put(waitingPortal, portalId);
            this.linkedPortalByPortalId.put(portalId, waitingPortal);
            this.linkedHexByPortalId.put(waitingPortal, hex);
            this.linkedHexByPortalId.put(portalId, hex);

            this.setDirty();
            return LinkResult.LINKED_TO_EXISTING_PORTAL;
        }

        this.addWaiting(hex, portalId);
        return LinkResult.WAITING_FOR_SECOND_PORTAL;
    }

    public void removePortal(UUID portalId) {
        if (portalId == null) return;

        boolean changed = false;

        Integer endpointIndexObject = this.endpointIndexByPortalId.remove(portalId);
        if (endpointIndexObject != null) {
            int removedIndex = endpointIndexObject;
            int lastIndex = this.endpoints.size() - 1;

            if (removedIndex != lastIndex) {
                Endpoint last = this.endpoints.get(lastIndex);
                this.endpoints.set(removedIndex, last);
                this.endpointIndexByPortalId.put(last.portalId(), removedIndex);
            }

            this.endpoints.remove(lastIndex);
            changed = true;
        }

        if (this.waitingEntries.removeIf(entry -> entry.portalId().equals(portalId))) {
            changed = true;
        }

        Iterator<LinkEntry> iterator = this.linkEntries.iterator();
        while (iterator.hasNext()) {
            LinkEntry link = iterator.next();
            if (!link.contains(portalId)) continue;

            iterator.remove();
            changed = true;

            int hex = normalizeHex(link.hexColor());
            UUID other = link.other(portalId);

            if (other != null && this.endpointIndexByPortalId.containsKey(other)) {
                this.waitingEntries.add(new WaitingEntry(hex, other));
            }
        }

        this.rebuildIndex();

        if (changed) {
            this.setDirty();
        }
    }

    private void addWaiting(int hexColor, UUID portalId) {
        int hex = normalizeHex(hexColor);
        this.removeWaiting(hex);

        this.waitingEntries.add(new WaitingEntry(hex, portalId));
        this.waitingPortalByHex.put(hex, portalId);

        this.setDirty();
    }

    private void removeWaiting(int hexColor) {
        int hex = normalizeHex(hexColor);

        boolean removed = this.waitingEntries.removeIf(entry -> normalizeHex(entry.hexColor()) == hex);
        UUID removedPortal = this.waitingPortalByHex.remove(hex);

        if (removed || removedPortal != null) {
            this.setDirty();
        }
    }

    private static int normalizeHex(int hexColor) {
        return hexColor & 0xFFFFFF;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}