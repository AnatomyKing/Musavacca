// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/hex/PearlHexNetworkDirectory.java
package space.anatomyuniverse.hex;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import space.anatomyuniverse.musavacca.portal.PearlPortalDirectory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class PearlHexNetworkDirectory extends SavedData {
    public static final String STORAGE_ID = "pearl_hex_network_directory";

    private static final Codec<PearlHexNetwork.OwnerKind> OWNER_KIND_CODEC = Codec.STRING.xmap(
            PearlHexNetwork.OwnerKind::fromSerializedName,
            PearlHexNetwork.OwnerKind::serializedName
    );

    public record Claim(
            String ownerKey,
            PearlHexNetwork.OwnerKind ownerKind,
            int hexColor
    ) {
        public Claim normalized() {
            return new Claim(
                    normalizeOwnerKey(ownerKey),
                    ownerKind == null ? PearlHexNetwork.OwnerKind.VOCO_TABLE_CANDLE : ownerKind,
                    normalizeHex(hexColor)
            );
        }

        public boolean hasValidOwner() {
            return !normalizeOwnerKey(this.ownerKey).isEmpty();
        }
    }

    private static final Codec<Claim> CLAIM_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("owner_key").forGetter(Claim::ownerKey),
            OWNER_KIND_CODEC.fieldOf("owner_kind").forGetter(Claim::ownerKind),
            Codec.INT.fieldOf("hex_color").forGetter(Claim::hexColor)
    ).apply(instance, Claim::new));

    public static final Codec<PearlHexNetworkDirectory> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CLAIM_CODEC.listOf().optionalFieldOf("claims", List.of()).forGetter(data -> data.claims)
    ).apply(instance, PearlHexNetworkDirectory::new));

    public static final SavedDataType<PearlHexNetworkDirectory> TYPE =
            new SavedDataType<>(STORAGE_ID, PearlHexNetworkDirectory::new, CODEC);

    private final ArrayList<Claim> claims;

    private transient HashMap<String, Claim> claimByOwnerKey;
    private transient HashMap<Integer, String> ownerKeyByHex;

    public PearlHexNetworkDirectory() {
        this.claims = new ArrayList<>();
        this.rebuildIndex();
    }

    private PearlHexNetworkDirectory(List<Claim> claims) {
        this.claims = new ArrayList<>();

        for (Claim claim : claims) {
            if (claim == null) {
                continue;
            }

            Claim normalized = claim.normalized();
            if (normalized.hasValidOwner()) {
                this.claims.add(normalized);
            }
        }

        this.rebuildIndex();
    }

    public static PearlHexNetworkDirectory get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }

    public PearlHexNetwork.ClaimResult reserveVocoHex(
            ServerLevel level,
            String ownerKey,
            PearlHexNetwork.OwnerKind ownerKind,
            int hexColor
    ) {
        ownerKey = normalizeOwnerKey(ownerKey);
        if (ownerKey.isEmpty()) {
            return PearlHexNetwork.ClaimResult.INVALID_OWNER;
        }

        int hex = normalizeHex(hexColor);

        Claim existingClaim = this.claimByOwnerKey.get(ownerKey);
        if (existingClaim != null && normalizeHex(existingClaim.hexColor()) == hex) {
            return PearlHexNetwork.ClaimResult.ALREADY_OWNED;
        }

        if (this.isPortalHexOccupiedForVoco(level.getServer(), hex)) {
            return PearlHexNetwork.ClaimResult.HEX_OCCUPIED_BY_PORTAL;
        }

        String otherOwner = this.ownerKeyByHex.get(hex);
        if (otherOwner != null && !otherOwner.equals(ownerKey)) {
            return PearlHexNetwork.ClaimResult.HEX_OCCUPIED_BY_VOCO;
        }

        this.removeOwnerClaimNoDirty(ownerKey);

        Claim claim = new Claim(ownerKey, ownerKind, hex).normalized();
        this.claims.add(claim);
        this.claimByOwnerKey.put(ownerKey, claim);
        this.ownerKeyByHex.put(hex, ownerKey);

        this.setDirty();
        return PearlHexNetwork.ClaimResult.RESERVED;
    }

    public PearlHexNetwork.ClaimResult checkVocoHex(
            ServerLevel level,
            String ownerKey,
            int hexColor
    ) {
        ownerKey = normalizeOwnerKey(ownerKey);
        if (ownerKey.isEmpty()) {
            return PearlHexNetwork.ClaimResult.INVALID_OWNER;
        }

        int hex = normalizeHex(hexColor);

        Claim existingClaim = this.claimByOwnerKey.get(ownerKey);
        if (existingClaim != null && normalizeHex(existingClaim.hexColor()) == hex) {
            return PearlHexNetwork.ClaimResult.ALREADY_OWNED;
        }

        if (this.isPortalHexOccupiedForVoco(level.getServer(), hex)) {
            return PearlHexNetwork.ClaimResult.HEX_OCCUPIED_BY_PORTAL;
        }

        String otherOwner = this.ownerKeyByHex.get(hex);
        if (otherOwner != null && !otherOwner.equals(ownerKey)) {
            return PearlHexNetwork.ClaimResult.HEX_OCCUPIED_BY_VOCO;
        }

        return PearlHexNetwork.ClaimResult.RESERVED;
    }

    public void release(String ownerKey) {
        ownerKey = normalizeOwnerKey(ownerKey);
        if (ownerKey.isEmpty()) {
            return;
        }

        boolean changed = this.removeOwnerClaimNoDirty(ownerKey);

        if (changed) {
            this.setDirty();
        }
    }

    public Optional<Integer> getClaimedHex(String ownerKey) {
        Claim claim = this.claimByOwnerKey.get(normalizeOwnerKey(ownerKey));
        return claim == null ? Optional.empty() : Optional.of(normalizeHex(claim.hexColor()));
    }

    public boolean isHexClaimedByVoco(int hexColor) {
        return this.ownerKeyByHex.containsKey(normalizeHex(hexColor));
    }

    public boolean isHexClaimedByOwner(String ownerKey, int hexColor) {
        Claim claim = this.claimByOwnerKey.get(normalizeOwnerKey(ownerKey));
        return claim != null && normalizeHex(claim.hexColor()) == normalizeHex(hexColor);
    }

    public boolean canCreatePortalWithHex(ServerLevel level, int hexColor) {
        int hex = normalizeHex(hexColor);

        if (this.isHexClaimedByVoco(hex)) {
            return false;
        }

        PearlPortalDirectory portalDirectory = PearlPortalDirectory.get(level.getServer());

        /*
         * Important:
         * - Fully linked portal hex = blocked.
         * - Waiting portal hex = allowed, because this is how the second portal links.
         */
        return !portalDirectory.isHexFullyLinked(hex);
    }

    public boolean isHexOccupiedForVoco(ServerLevel level, int hexColor, String ownerKey) {
        return !this.checkVocoHex(level, ownerKey, hexColor).success();
    }

    public int claimCount() {
        return this.claims.size();
    }

    private boolean isPortalHexOccupiedForVoco(MinecraftServer server, int hexColor) {
        PearlPortalDirectory portalDirectory = PearlPortalDirectory.get(server);
        int hex = normalizeHex(hexColor);

        return portalDirectory.isHexWaiting(hex) || portalDirectory.isHexFullyLinked(hex);
    }

    private boolean removeOwnerClaimNoDirty(String ownerKey) {
        final String normalizedOwnerKey = normalizeOwnerKey(ownerKey);

        Claim old = this.claimByOwnerKey.remove(normalizedOwnerKey);
        if (old == null) {
            return false;
        }

        this.ownerKeyByHex.remove(normalizeHex(old.hexColor()));

        return this.claims.removeIf(claim ->
                claim.ownerKey().equals(normalizedOwnerKey)
        );
    }

    private void rebuildIndex() {
        this.claimByOwnerKey = new HashMap<>();
        this.ownerKeyByHex = new HashMap<>();

        ArrayList<Claim> cleanClaims = new ArrayList<>();

        for (Claim rawClaim : this.claims) {
            if (rawClaim == null) {
                continue;
            }

            Claim claim = rawClaim.normalized();
            if (!claim.hasValidOwner()) {
                continue;
            }

            String ownerKey = claim.ownerKey();
            int hex = normalizeHex(claim.hexColor());

            /*
             * If corrupted/duplicate saved data exists:
             * - keep the first valid owner claim
             * - keep the first valid hex claim
             * This avoids one bad duplicate poisoning the runtime maps.
             */
            if (this.claimByOwnerKey.containsKey(ownerKey)) {
                continue;
            }

            if (this.ownerKeyByHex.containsKey(hex)) {
                continue;
            }

            this.claimByOwnerKey.put(ownerKey, claim);
            this.ownerKeyByHex.put(hex, ownerKey);
            cleanClaims.add(claim);
        }

        this.claims.clear();
        this.claims.addAll(cleanClaims);
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
}