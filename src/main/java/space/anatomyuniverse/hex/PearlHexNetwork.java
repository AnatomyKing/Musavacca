// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/hex/PearlHexNetwork.java
package space.anatomyuniverse.hex;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoSharedBetweenTableAndReceptorLogic.ReceptorPosition;

import java.util.Locale;
import java.util.Optional;

public final class PearlHexNetwork {
    private final MinecraftServer server;

    private PearlHexNetwork(MinecraftServer server) {
        this.server = server;
    }

    public enum OwnerKind {
        VOCO_TABLE_CANDLE("voco_table_candle"),
        VOCO_RECEPTOR("voco_receptor");

        private final String serializedName;

        OwnerKind(String serializedName) {
            this.serializedName = serializedName;
        }

        public String serializedName() {
            return this.serializedName;
        }

        public static OwnerKind fromSerializedName(String name) {
            String normalized = name == null
                    ? ""
                    : name.toLowerCase(Locale.ROOT);

            for (OwnerKind kind : values()) {
                if (kind.serializedName.equals(normalized)) {
                    return kind;
                }
            }

            return VOCO_TABLE_CANDLE;
        }
    }

    public enum ClaimResult {
        RESERVED,
        ALREADY_OWNED,
        HEX_OCCUPIED_BY_VOCO,
        HEX_OCCUPIED_BY_PORTAL,
        INVALID_OWNER;

        public boolean success() {
            return this == RESERVED || this == ALREADY_OWNED;
        }
    }

    public static PearlHexNetwork get(MinecraftServer server) {
        return new PearlHexNetwork(server);
    }

    public PearlHexNetworkDirectory directory() {
        return PearlHexNetworkDirectory.get(this.server);
    }

    public ClaimResult reserveVocoHex(
            ServerLevel level,
            String ownerKey,
            OwnerKind ownerKind,
            int hexColor
    ) {
        return this.directory().reserveVocoHex(
                level,
                ownerKey,
                ownerKind,
                hexColor
        );
    }

    public ClaimResult checkVocoHex(
            ServerLevel level,
            String ownerKey,
            int hexColor
    ) {
        return this.directory().checkVocoHex(
                level,
                ownerKey,
                hexColor
        );
    }

    public void release(ServerLevel level, String ownerKey) {
        this.release(ownerKey);
    }

    public void release(String ownerKey) {
        this.directory().release(ownerKey);
    }

    public Optional<Integer> getClaimedHex(String ownerKey) {
        return this.directory().getClaimedHex(ownerKey);
    }

    public boolean isHexClaimedByVoco(int hexColor) {
        return this.directory().isHexClaimedByVoco(hexColor);
    }

    public boolean isHexClaimedByOwner(String ownerKey, int hexColor) {
        return this.directory().isHexClaimedByOwner(ownerKey, hexColor);
    }

    public boolean canCreatePortalWithHex(ServerLevel level, int hexColor) {
        return this.directory().canCreatePortalWithHex(level, hexColor);
    }

    public boolean isHexOccupiedForVoco(ServerLevel level, int hexColor, String ownerKey) {
        return this.directory().isHexOccupiedForVoco(level, hexColor, ownerKey);
    }

    public int claimCount() {
        return this.directory().claimCount();
    }

    public static String vocoTableCandleOwnerKey(
            ServerLevel level,
            BlockPos pos,
            ReceptorPosition receptor
    ) {
        return "voco_table_candle|"
                + level.dimension().location()
                + "|"
                + pos.getX() + "," + pos.getY() + "," + pos.getZ()
                + "|"
                + receptor.id();
    }

    public static String vocoReceptorOwnerKey(ServerLevel level, BlockPos pos) {
        return "voco_receptor|"
                + level.dimension().location()
                + "|"
                + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public static int normalizeHex(int hexColor) {
        return PearlHexNetworkDirectory.normalizeHex(hexColor);
    }

    public static String toHex(int hexColor) {
        return PearlHexNetworkDirectory.toHex(hexColor);
    }
}