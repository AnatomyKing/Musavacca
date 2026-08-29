package space.anatomyuniverse.musavacca.basuke.summon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
//? if >=1.21.2
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.item.ModItems;

import java.util.List;

public final class BasukeSealed {

    private BasukeSealed() {
    }

    public static boolean trySealFromVocoTable(
            Level level,
            BlockPos pos
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        BlockState state =
                serverLevel.getBlockState(pos);

        if (!(state.getBlock() instanceof VocoTableBlock)) {
            return false;
        }

        if (
                !state.getValue(
                        VocoTableBlock.ROTARY_DIALERS
                )
        ) {
            return false;
        }

        if (
                !(serverLevel.getBlockEntity(pos)
                        instanceof VocoTableBlockEntity tableBe)
        ) {
            return false;
        }

        if (!matchesSealingRecipe(state, tableBe)) {
            return false;
        }

        List<Basuke> boundBasukes =
                findPresentBoundBasukes(
                        serverLevel,
                        pos
                );

        if (boundBasukes.isEmpty()) {
            return false;
        }

        completeSealing(
                serverLevel,
                pos,
                tableBe,
                boundBasukes
        );

        return true;
    }

    private static boolean matchesSealingRecipe(
            @NotNull BlockState state,
            @NotNull VocoTableBlockEntity tableBe
    ) {
        ItemStack displayed =
                tableBe.getDisplayedItem();

        return displayed.is(
                ModItems.INACTIVE_VOCO_CALLER.get()
        )
                && allReceptorsCharged(state)
                && allNormalCandleCornersLit(tableBe);
    }

    private static boolean allReceptorsCharged(
            @NotNull BlockState state
    ) {
        for (
                BooleanProperty property
                : VocoTableBlock.RECEPTOR_LIGHTS
        ) {
            if (!state.getValue(property)) {
                return false;
            }
        }

        return true;
    }

    private static boolean allNormalCandleCornersLit(
            @NotNull VocoTableBlockEntity tableBe
    ) {
        for (
                ReceptorPosition receptor
                : ReceptorPosition.values()
        ) {
            if (
                    !tableBe.isCandleLit(receptor)
                            || tableBe.getCornerHexColor(receptor)
                            != VocoTableBlockEntity.UNSET_HEX_COLOR
            ) {
                return false;
            }
        }

        return true;
    }

    private static List<Basuke> findPresentBoundBasukes(
            ServerLevel level,
            BlockPos tablePos
    ) {
        AABB searchArea =
                new AABB(
                        tablePos.getX() - 0.5D,
                        tablePos.getY(),
                        tablePos.getZ() - 0.5D,
                        tablePos.getX() + 1.5D,
                        tablePos.getY() + 3.0D,
                        tablePos.getZ() + 1.5D
                );

        return level.getEntitiesOfClass(
                Basuke.class,
                searchArea,
                basuke ->
                        basuke.isAlive()
                                && basuke.isBoundToVocoTable()
                                && tablePos.equals(
                                basuke.getVocoTablePos()
                        )
        );
    }

    private static void completeSealing(
            ServerLevel level,
            BlockPos pos,
            VocoTableBlockEntity tableBe,
            List<Basuke> boundBasukes
    ) {
        tableBe.setDisplayedItem(
                new ItemStack(
                        ModItems.BANANA_PHONE.get()
                ),
                1
        );

        tableBe.extinguishAllCandlesForSummon();

        BlockState newState =
                level.getBlockState(pos);

        for (
                ReceptorPosition receptor
                : ReceptorPosition.values()
        ) {
            newState =
                    newState
                            .setValue(
                                    VocoTableBlock.lightProperty(
                                            receptor
                                    ),
                                    false
                            )
                            .setValue(
                                    VocoTableBlock.portalProperty(
                                            receptor
                                    ),
                                    false
                            );

            VocoTeleportLogic.syncEndpoint(
                    level,
                    pos,
                    receptor,
                    false,
                    VocoReceptorLogic.UNSET_HEX_COLOR
            );
        }

        newState =
                newState.setValue(
                        VocoTableBlock.ROTARY_DIALERS,
                        false
                );

        level.setBlock(
                pos,
                newState,
                VocoReceptorLogic.UPDATE_FLAGS
        );

        spawnSealingLightning(level, pos);
        tableBe.deactivateBasukeFromRotaryDialers(level);

        for (Basuke basuke : boundBasukes) {
            if (basuke.isAlive()) {
                basuke.discard();
            }
        }
    }

    private static void spawnSealingLightning(
            ServerLevel level,
            BlockPos pos
    ) {
        //? if <1.21.2 {
        /*LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        *///?} else {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(
                level,
                EntitySpawnReason.TRIGGERED
        );
        //?}

        if (lightning == null) {
            return;
        }

        //? if >=1.21.5 {
        lightning.snapTo(
                pos.getX() + 0.5D,
                pos.getY() + 1.05D,
                pos.getZ() + 0.5D
        );
        //?} else {
        /*lightning.moveTo(
                pos.getX() + 0.5D,
                pos.getY() + 1.05D,
                pos.getZ() + 0.5D
        );
        *///?}

        lightning.setVisualOnly(true);
        level.addFreshEntity(lightning);
    }
}
