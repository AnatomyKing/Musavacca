package space.anatomyuniverse.musavacca.basuke.summon;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
//? if >=1.21.2 {
import net.minecraft.world.entity.EntitySpawnReason;
//?}
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import space.anatomyuniverse.musavacca.block.custom.VocoTableBlock;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic.ReceptorPosition;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoReceptorLogic;
import space.anatomyuniverse.musavacca.block.custom.logic.VocoTeleportLogic;
import space.anatomyuniverse.musavacca.block.entity.custom.VocoTableBlockEntity;
import space.anatomyuniverse.musavacca.entity.mob.basuke.Basuke;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public final class BasukeSummon {
    private BasukeSummon() {}

    public static boolean trySummonFromVocoTable(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) return false;
        BlockState state = serverLevel.getBlockState(pos);
        if (!(state.getBlock() instanceof VocoTableBlock)) return false;
        if (!(serverLevel.getBlockEntity(pos) instanceof VocoTableBlockEntity table)) return false;
        boolean sealing = state.getValue(VocoTableBlock.ROTARY_DIALERS);
        if (!table.getDisplayedItem().is(sealing ? ModItems.INACTIVE_VOCO_CALLER.get() : ModItems.BIG_BANANA_PEARL.get())) return false;
        if (!allReceptorsCharged(state) || !allNormalCandleCornersLit(table)) return false;
        List<Basuke> boundBasukes = sealing ? findPresentBoundBasukes(serverLevel, pos) : List.of();
        if (sealing && boundBasukes.isEmpty()) return false;
        completeRitual(serverLevel, pos, table, sealing, boundBasukes);
        return true;
    }

    private static boolean allReceptorsCharged(BlockState state) {
        for (BooleanProperty property : VocoTableBlock.RECEPTOR_LIGHTS) {
            if (!state.getValue(property)) return false;
        }
        return true;
    }

    private static boolean allNormalCandleCornersLit(VocoTableBlockEntity table) {
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            if (!table.isCandleLit(receptor) || table.getCornerHexColor(receptor) != MusavaccaTints.NO_TINT) return false;
        }
        return true;
    }

    private static List<Basuke> findPresentBoundBasukes(ServerLevel level, BlockPos tablePos) {
        AABB searchArea = new AABB(tablePos.getX() - 0.5D, tablePos.getY(), tablePos.getZ() - 0.5D, tablePos.getX() + 1.5D, tablePos.getY() + 3.0D,
                tablePos.getZ() + 1.5D);
        return level.getEntitiesOfClass(Basuke.class, searchArea,
                basuke -> basuke.isAlive() && basuke.isBoundToVocoTable() && tablePos.equals(basuke.getVocoTablePos()));
    }

    private static void completeRitual(ServerLevel level, BlockPos pos, VocoTableBlockEntity table, boolean sealing, List<Basuke> boundBasukes) {
        if (sealing) table.setDisplayedItem(new ItemStack(ModItems.BANANA_PHONE.get()), 1);
        else table.removeDisplayedItems(1);
        table.extinguishAllCandlesForSummon();
        BlockState newState = level.getBlockState(pos);
        for (ReceptorPosition receptor : ReceptorPosition.values()) {
            newState = newState.setValue(VocoTableBlock.lightProperty(receptor), false).setValue(VocoTableBlock.portalProperty(receptor), false);
            VocoTeleportLogic.syncEndpoint(level, pos, receptor, false, MusavaccaTints.NO_TINT);
        }
        level.setBlock(pos, newState.setValue(VocoTableBlock.ROTARY_DIALERS, !sealing), VocoReceptorLogic.UPDATE_FLAGS);
        spawnLightning(level, pos);
        if (sealing) {
            table.deactivateBasukeFromRotaryDialers(level);
            for (Basuke basuke : boundBasukes) {
                if (basuke.isAlive()) basuke.discard();
            }
        } else {
            table.activateBasukeFromRotaryDialers(level);
        }
    }

    private static void spawnLightning(ServerLevel level, BlockPos pos) {
        //? if >=26.2 {
        /*LightningBolt lightning = net.minecraft.world.entity.EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        *///?} else if >=1.21.2 <26.2 {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        //?} else {
        /*LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        *///?}
        if (lightning == null) return;
        //? if >=1.21.5 {
        lightning.snapTo(pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D);
        //?} else {
        /*lightning.moveTo(pos.getX() + 0.5D, pos.getY() + 1.05D, pos.getZ() + 0.5D);
        *///?}
        lightning.setVisualOnly(true);
        level.addFreshEntity(lightning);
    }
}
