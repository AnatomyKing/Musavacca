package space.anatomyuniverse.musavacca.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;

//? if >=1.21.5 {
import net.minecraft.core.component.DataComponentGetter;
//?}
import net.minecraft.server.level.ServerLevel;
import space.anatomyuniverse.musavacca.trapdoor.MusavaccaTrapdoorTeleportNetwork;
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

import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalTrapdoorBlock;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;

public final class MusavaccaPortalTrapdoorBlockEntity
        extends BlockEntity {

    private static final String TAG_HEX_COLOR =
            "hex_color";

    public static final int UNSET_HEX_COLOR =
            -1;

    private int hexColor =
            UNSET_HEX_COLOR;

    public MusavaccaPortalTrapdoorBlockEntity(
            BlockPos pos,
            BlockState state
    ) {
        super(
                ModBlockEntities
                        .MUSAVACCA_TRAPDOOR_BLOCK_ENTITY
                        .get(),
                pos,
                state
        );
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public boolean hasHexColor() {
        return this.hexColor
                != UNSET_HEX_COLOR;
    }

    public void setHexColor(int hexColor) {
        int resolved =
                hexColor == UNSET_HEX_COLOR
                        ? UNSET_HEX_COLOR
                        : normalizeHex(hexColor);

        if (this.hexColor == resolved) {
            return;
        }

        this.hexColor = resolved;

        this.setChanged();
        this.syncTrapdoor(true);
    }

    public void clearHexColor() {
        this.setHexColor(
                UNSET_HEX_COLOR
        );
    }

    private static int normalizeHex(
            int hexColor
    ) {
        return hexColor & 0xFFFFFF;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        this.syncTrapdoor(false);
    }

    public void cleanupBeforeRemoval() {
        if (
                this.level
                        instanceof ServerLevel serverLevel
        ) {
            MusavaccaTrapdoorTeleportNetwork
                    .removeTrapdoor(
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

    private void syncTrapdoor(
            boolean playPortalSound
    ) {
        Level level =
                this.getLevel();

        if (level == null) {
            return;
        }

        if (!level.isClientSide()) {
            MusavaccaPortalTrapdoorBlock
                    .synchronizePearlState(
                            level,
                            this.getBlockPos(),
                            playPortalSound
                    );
            return;
        }

        this.rerenderClientTrapdoor();
    }

    private void rerenderClientTrapdoor() {
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
                        TAG_HEX_COLOR,
                        UNSET_HEX_COLOR
                );

        this.hexColor =
                loaded == UNSET_HEX_COLOR
                        ? UNSET_HEX_COLOR
                        : normalizeHex(loaded);
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
                    TAG_HEX_COLOR,
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
                        TAG_HEX_COLOR,
                        UNSET_HEX_COLOR
                );

        this.hexColor =
                loaded == UNSET_HEX_COLOR
                        ? UNSET_HEX_COLOR
                        : normalizeHex(loaded);
    }

    @Override
    protected void saveAdditional(
            ValueOutput output
    ) {
        super.saveAdditional(output);

        if (this.hasHexColor()) {
            output.putInt(
                    TAG_HEX_COLOR,
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
                        ? UNSET_HEX_COLOR
                        : normalizeHex(savedHex);
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
                        ? UNSET_HEX_COLOR
                        : normalizeHex(savedHex);
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

        this.rerenderClientTrapdoor();
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

        this.rerenderClientTrapdoor();
    }
    *///?} else {
    @Override
    public void handleUpdateTag(
            ValueInput input
    ) {
        super.handleUpdateTag(input);
        this.rerenderClientTrapdoor();
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

        this.rerenderClientTrapdoor();
    }
    //?}
}
