package space.anatomyuniverse.musavacca.block.custom.logic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
//? if <1.21.6 {
/*import net.minecraft.nbt.CompoundTag;
*///?}
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.core.BlockPos;
import java.util.Set;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
//? if >=1.21.6 {
import net.minecraft.world.entity.player.Abilities;
//?}
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public record VocoDustReturn(GameType mode, ResourceKey<Level> dimension, Vec3 pos, float yaw, float pitch,
        //? if >=1.21.6 {
        Abilities.Packed abilities) {
        //?} else {
        /*CompoundTag abilities) {
        *///?}
    private static final String KEY = "musavacca:voco_dust_return";
    private static final Codec<VocoDustReturn> CODEC = RecordCodecBuilder.create(i -> i.group(
            GameType.CODEC.fieldOf("mode").forGetter(VocoDustReturn::mode),
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(VocoDustReturn::dimension),
            Vec3.CODEC.fieldOf("position").forGetter(VocoDustReturn::pos),
            Codec.FLOAT.fieldOf("yaw").forGetter(VocoDustReturn::yaw),
            Codec.FLOAT.fieldOf("pitch").forGetter(VocoDustReturn::pitch),
            //? if >=1.21.6 {
            Abilities.Packed.CODEC.fieldOf("abilities").forGetter(VocoDustReturn::abilities)
            //?} else {
            /*CompoundTag.CODEC.fieldOf("abilities").forGetter(VocoDustReturn::abilities)
            *///?}
    ).apply(i, VocoDustReturn::new));

    static VocoDustReturn capture(ServerPlayer player, ServerLevel level) {
        //? if >=1.21.6 {
        var abilities = player.getAbilities().pack();
        //?} else {
        /*CompoundTag abilities = new CompoundTag();
        player.getAbilities().addSaveData(abilities);
        *///?}
        return new VocoDustReturn(player.gameMode.getGameModeForPlayer(), level.dimension(), player.position(),
                player.getYRot(), player.getXRot(), abilities);
    }

    void save(ServerPlayer player) {
        player.getPersistentData().put(KEY, CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
    }

    static void clear(ServerPlayer player) {
        player.getPersistentData().remove(KEY);
    }

    void moveHome(ServerPlayer player, ServerLevel current) {
        ServerLevel home = current.getServer().getLevel(dimension);
        if (home == null) return;
        var body = player.getDimensions(Pose.STANDING);
        Vec3 safe = PortalShape.findCollisionFreePosition(pos, home, player, body);
        if (!home.noCollision(player, body.makeBoundingBox(safe))) {
            safe = Vec3.atBottomCenterOf(home.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(pos)));
        }
        if (current == home) {
            player.connection.teleport(safe.x, safe.y, safe.z, yaw, pitch);
        } else {
            //? if >=1.21.2 {
            player.teleportTo(home, safe.x, safe.y, safe.z, Set.of(), yaw, pitch, true);
            //?} else {
            /*player.teleportTo(home, safe.x, safe.y, safe.z, Set.of(), yaw, pitch);
            *///?}
        }
    }

    private static void changeMode(ServerPlayer player, GameType mode) {
        if (player.gameMode.getGameModeForPlayer() == mode) return;
        player.setGameMode(mode);
        if (player.gameMode.getGameModeForPlayer() != mode) {
            player.gameMode.changeGameModeForPlayer(mode);
            player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, mode.getId()));
        }
    }

    void restoreMode(ServerPlayer player, boolean rollback) {
        if (!rollback && !player.isSpectator()) return;
        changeMode(player, mode);
        //? if >=1.21.6 {
        player.getAbilities().apply(abilities);
        //?} else {
        /*player.getAbilities().loadSaveData(abilities);
        *///?}
        player.onUpdateAbilities();
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)) return;
        VocoDustInputs.install(player);
        Tag saved = player.getPersistentData().get(KEY);
        if (saved == null) return;
        VocoDustReturn departure = CODEC.parse(NbtOps.INSTANCE, saved).result().orElse(null);
        if (departure != null) {
            departure.moveHome(player, level);
            departure.restoreMode(player, false);
        } else if (player.isSpectator()) {
            changeMode(player, level.getServer().getDefaultGameType());
        }
        clear(player);
    }
}
