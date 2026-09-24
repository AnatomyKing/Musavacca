package space.anatomyuniverse.musavacca.economy;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class EconomyConfig {
    public static final String FILE_NAME = "musavacca-economy.toml";

    private static final ModConfigSpec.IntValue VOCO_TABLE_TELEPORT_COST;
    private static final ModConfigSpec.IntValue MUSAVACCA_DOOR_TELEPORT_COST;
    private static final ModConfigSpec.IntValue MUSAVACCA_TRAPDOOR_TELEPORT_COST;
    private static final ModConfigSpec.IntValue PEARL_PORTAL_TELEPORT_COST;
    private static final ModConfigSpec.IntValue VOCO_CALLER_TELEPORT_COST;

    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Teleport costs paid from the player's balance. A cost of 0 makes that teleport type free.")
                .push("teleportation");

        VOCO_TABLE_TELEPORT_COST = cost(
                builder,
                "voco_table",
                1,
                "Balance cost for teleporting with a Voco Table. Set to 0 to make Voco Table teleportation free."
        );

        MUSAVACCA_DOOR_TELEPORT_COST = cost(
                builder,
                "musavacca_door",
                0,
                "Balance cost for teleporting through a Musavacca Door. Set to 0 to make Musavacca Door teleportation free."
        );

        MUSAVACCA_TRAPDOOR_TELEPORT_COST = cost(
                builder,
                "musavacca_trapdoor",
                0,
                "Balance cost for teleporting through a Musavacca Trapdoor. Set to 0 to make Musavacca Trapdoor teleportation free."
        );

        PEARL_PORTAL_TELEPORT_COST = cost(
                builder,
                "pearl_portal",
                0,
                "Balance cost for teleporting through a Pearl Portal. Set to 0 to make Pearl Portal teleportation free."
        );

        VOCO_CALLER_TELEPORT_COST = cost(
                builder,
                "voco_caller",
                2,
                "Balance cost for teleporting with a Voco Caller. Set to 0 to make Voco Caller teleportation free."
        );

        builder.pop();
        SPEC = builder.build();
    }

    private EconomyConfig() {}

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC, FILE_NAME);
    }

    public static int vocoTableTeleportCost() {
        return VOCO_TABLE_TELEPORT_COST.get();
    }

    public static int musavaccaDoorTeleportCost() {
        return MUSAVACCA_DOOR_TELEPORT_COST.get();
    }

    public static int musavaccaTrapdoorTeleportCost() {
        return MUSAVACCA_TRAPDOOR_TELEPORT_COST.get();
    }

    public static int pearlPortalTeleportCost() {
        return PEARL_PORTAL_TELEPORT_COST.get();
    }

    public static int vocoCallerTeleportCost() {
        return VOCO_CALLER_TELEPORT_COST.get();
    }

    private static ModConfigSpec.IntValue cost(
            ModConfigSpec.Builder builder,
            String key,
            int defaultCost,
            String description
    ) {
        return builder.comment(description)
                .defineInRange(key, defaultCost, 0, Integer.MAX_VALUE);
    }
}
