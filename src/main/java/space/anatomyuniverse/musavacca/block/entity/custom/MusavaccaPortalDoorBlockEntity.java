package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;

//? if >=1.21.5 {
import net.minecraft.core.component.DataComponentGetter;
//?}
import net.minecraft.server.level.ServerLevel;
import space.anatomyuniverse.musavacca.door.MusavaccaDoorTeleportNetwork;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
//?}

import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints.HexSource;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;

public final class MusavaccaPortalDoorBlockEntity
        extends BlockEntity implements HexSource {

    private int hexColor =
            MusavaccaTints.NO_TINT;

    public MusavaccaPortalDoorBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                ModBlockEntities
                        .MUSAVACCA_DOOR_BLOCK_ENTITY
                        .get(),
                pos,
                state
        );
    }

    @Override
    public int getHexColor() {
        return this.hexColor;
    }

    public boolean hasHexColor() {
        return this.hexColor
                != MusavaccaTints.NO_TINT;
    }

    public void setHexColor(int hexColor) {
        int resolved =
                hexColor == MusavaccaTints.NO_TINT
                        ? MusavaccaTints.NO_TINT
                        : MusavaccaTints.resolve(hexColor);

        if (this.hexColor == resolved) {
            return;
        }

        this.hexColor = resolved;

        this.setChanged();
        this.syncDoor(true);
    }

    public void clearHexColor() {
        this.setHexColor(
                MusavaccaTints.NO_TINT
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();

        this.syncDoor(false);
    }

    public void cleanupBeforeRemoval() {
        if (
                this.level
                        instanceof ServerLevel serverLevel
        ) {
            MusavaccaDoorTeleportNetwork
                    .removeDoor(
                            serverLevel,
                            this.getBlockPos()
                    );
        }
    }

    //? if >=1.21.5 {
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.cleanupBeforeRemoval();
        super.preRemoveSideEffects(
                pos,
                state
        );
    }
    //?}

    private void syncDoor(
            boolean playPortalSound
    ) {
        Level level =
                this.getLevel();

        if (level == null) {
            return;
        }

        if (!level.isClientSide()) {
            MusavaccaPortalDoorBlock
                    .synchronizePearlState(
                            level,
                            this.getBlockPos(),
                            playPortalSound
                    );
            return;
        }

        this.rerenderClientDoor();
    }

    private void rerenderClientDoor() {
        Level level =
                this.getLevel();

        if (
                level == null
                        || !level.isClientSide()
        ) {
            return;
        }

        rerenderPosition(
                level,
                this.getBlockPos()
        );

        rerenderPosition(
                level,
                this.getBlockPos().above()
        );
    }

    private void rerenderPosition(
            Level level,
            BlockPos pos
    ) {
        BlockState targetState =
                level.getBlockState(pos);

        if (
                targetState.getBlock()
                        != this.getBlockState().getBlock()
        ) {
            return;
        }

        level.sendBlockUpdated(
                pos,
                targetState,
                targetState,
                Block.UPDATE_CLIENTS
                        | Block.UPDATE_IMMEDIATE
        );
    }

    private static int readIntOr(
            CompoundTag tag,
            String key,
            int fallback
    ) {
        //? if <1.21.5 {
        /*return tag.contains(key)
                ? tag.getInt(key)
                : fallback;
        *///?} else {
        return tag.getIntOr(
                key,
                fallback
        );
        //?}
    }

    //? if <1.21.6 {
    /*@Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(
                tag,
                registries
        );

        int loaded =
                readIntOr(
                        tag,
                        MusavaccaTints.HEX_COLOR_KEY,
                        MusavaccaTints.NO_TINT
                );

        this.hexColor =
                loaded == MusavaccaTints.NO_TINT
                        ? MusavaccaTints.NO_TINT
                        : MusavaccaTints.stored(loaded);
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(
                tag,
                registries
        );

        if (this.hasHexColor()) {
            tag.putInt(
                    MusavaccaTints.HEX_COLOR_KEY,
                    this.hexColor
            );
        }
    }
    *///?} else {
    @Override
    protected void loadAdditional(
            ValueInput input
    ) {
        super.loadAdditional(input);

        int loaded =
                input.getIntOr(
                        MusavaccaTints.HEX_COLOR_KEY,
                        MusavaccaTints.NO_TINT
                );

        this.hexColor =
                loaded == MusavaccaTints.NO_TINT
                        ? MusavaccaTints.NO_TINT
                        : MusavaccaTints.stored(loaded);
    }

    @Override
    protected void saveAdditional(
            ValueOutput output
    ) {
        super.saveAdditional(output);

        if (this.hasHexColor()) {
            output.putInt(
                    MusavaccaTints.HEX_COLOR_KEY,
                    this.hexColor
            );
        }
    }
    //?}

    //? if <1.21.5 {
    /*@Override
    protected void applyImplicitComponents(
            DataComponentInput input
    ) {
        super.applyImplicitComponents(input);

        Integer savedHex =
                input.get(
                        ModDataComponents
                                .HEX_COLOR
                                .get()
                );

        this.hexColor =
                savedHex == null
                        ? MusavaccaTints.NO_TINT
                        : MusavaccaTints.stored(savedHex);
    }
    *///?} else {
    @Override
    protected void applyImplicitComponents(
            DataComponentGetter input
    ) {
        super.applyImplicitComponents(input);

        Integer savedHex =
                input.get(
                        ModDataComponents
                                .HEX_COLOR
                                .get()
                );

        this.hexColor =
                savedHex == null
                        ? MusavaccaTints.NO_TINT
                        : MusavaccaTints.stored(savedHex);
    }
    //?}

    @Override
    protected void collectImplicitComponents(
            DataComponentMap.Builder components
    ) {
        super.collectImplicitComponents(components);

        if (this.hasHexColor()) {
            components.set(
                    ModDataComponents
                            .HEX_COLOR
                            .get(),
                    this.hexColor
            );
        }
    }

    @Override
    public CompoundTag getUpdateTag(
            HolderLookup.Provider provider
    ) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public Packet<ClientGamePacketListener>
    getUpdatePacket() {
        return ClientboundBlockEntityDataPacket
                .create(this);
    }

    //? if <1.21.6 {
    /*@Override
    public void handleUpdateTag(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.handleUpdateTag(
                tag,
                registries
        );

        this.rerenderClientDoor();
    }

    @Override
    public void onDataPacket(
            Connection connection,
            ClientboundBlockEntityDataPacket packet,
            HolderLookup.Provider registries
    ) {
        super.onDataPacket(
                connection,
                packet,
                registries
        );

        this.rerenderClientDoor();
    }
    *///?} else {
    @Override
    public void handleUpdateTag(
            ValueInput input
    ) {
        super.handleUpdateTag(input);
        this.rerenderClientDoor();
    }

    @Override
    public void onDataPacket(
            Connection connection,
            ValueInput input
    ) {
        super.onDataPacket(
                connection,
                input
        );

        this.rerenderClientDoor();
    }
    //?}
}

