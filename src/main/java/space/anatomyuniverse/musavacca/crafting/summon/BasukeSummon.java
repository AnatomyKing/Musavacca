// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/crafting/summon/BasukeSummon.java
package space.anatomyuniverse.musavacca.crafting.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class BasukeSummon {
    private BasukeSummon() {}

    public static boolean trySummonFromVocoTable(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        BlockState state = serverLevel.getBlockState(pos);
        if (!(state.getBlock() instanceof VocoTableBlock)) {
            return false;
        }

        if (state.getValue(VocoTableBlock.ROTARY_DIALERS)) {
            return false;
        }

        if (!(serverLevel.getBlockEntity(pos) instanceof VocoTableBlockEntity tableBe)) {
            return false;
        }

        if (!matchesSummonRecipe(state, tableBe)) {
            return false;
        }

        completeSummon(serverLevel, pos, state, tableBe);
        return true;
    }

    private static boolean matchesSummonRecipe(
            @NotNull BlockState state,
            @NotNull VocoTableBlockEntity tableBe
    ) {
        ItemStack displayed = tableBe.getDisplayedItem();

        return displayed.is(ModItems.BIG_BANANA_PEARL.get())
                && allReceptorsCharged(state)
                && allCandleCornersLit(tableBe);
    }

    private static boolean allReceptorsCharged(@NotNull BlockState state) {
        for (BooleanProperty property : VocoTableBlock.RECEPTOR_LIGHTS) {
            if (!state.getValue(property)) {
                return false;
            }
        }

        return true;
    }

    private static boolean allCandleCornersLit(@NotNull VocoTableBlockEntity tableBe) {
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (!tableBe.isCandleLit(receptor)) {
                return false;
            }
        }

        return true;
    }

    private static void completeSummon(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            VocoTableBlockEntity tableBe
    ) {
        tableBe.removeDisplayedItem();
        tableBe.extinguishAllCandlesForSummon();

        BlockState newState = state;

        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            newState = newState
                    .setValue(VocoTableBlock.lightProperty(receptor), false)
                    .setValue(VocoTableBlock.portalProperty(receptor), false);

            VocoTeleportLogic.syncEndpoint(
                    level,
                    pos,
                    receptor,
                    false,
                    VocoReceptorLogic.UNSET_HEX_COLOR
            );
        }

        newState = newState.setValue(VocoTableBlock.ROTARY_DIALERS, true);

        level.setBlock(
                pos,
                newState,
                VocoReceptorLogic.UPDATE_FLAGS
        );

        spawnSummonLightning(level, pos);
        tableBe.activateBasukeFromRotaryDialers(level);
    }

    private static void spawnSummonLightning(ServerLevel level, BlockPos pos) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (lightning == null) {
            return;
        }

        lightning.snapTo(
                pos.getX() + 0.5D,
                pos.getY() + 1.05D,
                pos.getZ() + 0.5D
        );

        /*
         * Visual-only keeps the ritual dramatic without burning nearby blocks
         * or damaging entities. Remove this line if you want real lightning.
         */
        lightning.setVisualOnly(true);

        level.addFreshEntity(lightning);
    }
}