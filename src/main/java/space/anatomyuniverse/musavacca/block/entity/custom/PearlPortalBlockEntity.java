package space.anatomyuniverse.musavacca.block.entity.custom;
//? if <1.21.5 {
/*import net.minecraft.nbt.Tag;
*///?}

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;

//? if >=1.21.5 {
import net.minecraft.core.component.DataComponentGetter;
//?}
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

//? if >=1.21.6 {
import net.minecraft.world.level.storage.ValueInput;

import net.minecraft.world.level.storage.ValueOutput;
//?}
import space.anatomyuniverse.musavacca.block.custom.PearlPortalBlock;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints.HexSource;
import space.anatomyuniverse.musavacca.tint.MusavaccaTints;
import space.anatomyuniverse.musavacca.portal.PearlPortalDestroyer;
import space.anatomyuniverse.musavacca.portal.PearlPortalFrame;
import space.anatomyuniverse.musavacca.portal.PearlPortalNetwork;

import java.util.UUID;

public class PearlPortalBlockEntity extends BlockEntity implements HexSource {
    private static final String TAG_PORTAL_ID = "portal_id";
    private static final String TAG_ORIGIN_X = "origin_x";
    private static final String TAG_ORIGIN_Y = "origin_y";
    private static final String TAG_ORIGIN_Z = "origin_z";
    private static final String TAG_AXIS = "axis";
    private static final String TAG_FRONT_DIRECTION = "front_direction";
    private static final String TAG_UP_DIRECTION = "up_direction";
    private static final String TAG_WIDTH = "width";
    private static final String TAG_HEIGHT = "height";
    private static final String TAG_EXIT_ANCHOR_X = "exit_anchor_x";
    private static final String TAG_EXIT_ANCHOR_Y = "exit_anchor_y";
    private static final String TAG_EXIT_ANCHOR_Z = "exit_anchor_z";

    private UUID portalId = UUID.randomUUID();
    private int hexColor = MusavaccaTints.DEFAULT_TINT;

    private BlockPos originPos = null;
    private Direction.Axis axis = Direction.Axis.X;
    private Direction frontDirection = Direction.SOUTH;
    private Direction upDirection = Direction.UP;
    private int width = PearlPortalFrame.MIN_WIDTH;
    private int height = PearlPortalFrame.MIN_HEIGHT;
    private BlockPos exitAnchorPos = null;

    public PearlPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEARL_PORTAL_BLOCK_ENTITY.get(), pos, state);
    }

    public void initializePortal(UUID portalId, int hexColor, PearlPortalFrame.Shape shape) {
        this.portalId = portalId;
        this.hexColor = MusavaccaTints.resolve(hexColor);
        this.originPos = shape.minCorner().immutable();
        this.axis = PearlPortalFrame.normalizeAxis(shape.axis());
        this.frontDirection = PearlPortalFrame.normalizeFrontDirection(this.axis, shape.frontDirection());
        this.upDirection = PearlPortalFrame.normalizeUpDirection(this.axis, this.frontDirection, shape.upDirection());
        this.width = clampWidth(shape.width());
        this.height = clampHeight(shape.height());
        this.exitAnchorPos = shape.exitAnchorPos().immutable();

        this.setChangedAndSync();
        PearlPortalNetwork.registerPortalBlock(this);
    }

    public boolean hasPortalData() {
        return this.portalId != null
                && this.originPos != null
                && this.width >= PearlPortalFrame.MIN_WIDTH
                && this.height >= PearlPortalFrame.MIN_HEIGHT
                && PearlPortalFrame.isValidFrontDirection(this.getPortalAxis(), this.getPortalFrontDirection())
                && PearlPortalFrame.isValidUpDirection(
                this.getPortalAxis(),
                this.getPortalFrontDirection(),
                this.getPortalUpDirection()
        );
    }

    public boolean isPortalBlockEntityForPortalBlock() {
        return this.getBlockState().getBlock() instanceof PearlPortalBlock;
    }

    public boolean isValidPortalTile() {
        return this.hasPortalData() && this.isPortalBlockEntityForPortalBlock();
    }

    public boolean isOriginBlock() {
        return this.hasPortalData() && this.getBlockPos().equals(this.getOriginPos());
    }

    public UUID getPortalId() {
        return this.portalId;
    }

    @Override
    public int getHexColor() {
        return this.hexColor;
    }

    @Override
    public boolean hasHexColor() {
        return this.isValidPortalTile();
    }

    public BlockPos getOriginPos() {
        return this.originPos == null ? this.getBlockPos() : this.originPos;
    }

    public Direction.Axis getPortalAxis() {
        return PearlPortalFrame.normalizeAxis(this.axis);
    }

    public Direction getPortalFrontDirection() {
        return PearlPortalFrame.normalizeFrontDirection(this.getPortalAxis(), this.frontDirection);
    }

    public Direction getPortalUpDirection() {
        return PearlPortalFrame.normalizeUpDirection(
                this.getPortalAxis(),
                this.getPortalFrontDirection(),
                this.upDirection
        );
    }

    public int getPortalWidth() {
        return this.width;
    }

    public int getPortalHeight() {
        return this.height;
    }

    public BlockPos getExitAnchorPos() {
        return this.exitAnchorPos == null
                ? this.getPortalShape().exitAnchorPos()
                : this.exitAnchorPos;
    }

    public PearlPortalFrame.Shape getPortalShape() {
        return new PearlPortalFrame.Shape(
                this.getPortalAxis(),
                this.getOriginPos(),
                this.getPortalWidth(),
                this.getPortalHeight(),
                this.getPortalFrontDirection(),
                this.getPortalUpDirection(),
                this.exitAnchorPos
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (this.isValidPortalTile()) {
            PearlPortalNetwork.registerPortalBlock(this);
        }
    }

    @Override
    public void setRemoved() {
        PearlPortalNetwork.unregisterPortalBlock(this);
        super.setRemoved();
    }

    public void cleanupBeforeRemoval() {
        if (this.level instanceof ServerLevel serverLevel
                && !PearlPortalDestroyer.isDestroyingPortal()
                && this.isValidPortalTile()) {
            PearlPortalDestroyer.destroyPortalFromAnyTile(serverLevel, this.getBlockPos(), this.portalId);
        }
    }

    //? if >=1.21.5 {
    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        this.cleanupBeforeRemoval();
        super.preRemoveSideEffects(pos, state);
    }

    //?}
    private void setChangedAndSync() {
        this.setChanged();

        Level level = this.getLevel();
        if (level == null) {
            return;
        }

        if (level.isClientSide()) {
            this.rerenderClientNow();
            return;
        }

        this.sendBlockUpdate(false);
    }

    private void rerenderClientNow() {
        Level level = this.getLevel();
        if (level == null || !level.isClientSide()) {
            return;
        }

        this.sendBlockUpdate(true);

        if (this.isValidPortalTile()) {
            PearlPortalNetwork.registerPortalBlock(this);
        }
    }

    private void sendBlockUpdate(boolean clientOnly) {
        Level level = this.getLevel();
        if (level == null) {
            return;
        }

        if (clientOnly && !level.isClientSide()) {
            return;
        }

        BlockPos pos = this.getBlockPos();
        BlockState state = this.getBlockState();

        level.sendBlockUpdated(
                pos,
                state,
                state,
                Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE
        );
    }

    @Override
    //? if >=1.21.6 {
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    //?} else {
    /*protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
    *///?}

        //? if >=1.21.6 {
        this.portalId = readUuid(input, TAG_PORTAL_ID, UUID.randomUUID());
        this.hexColor = MusavaccaTints.stored(input.getIntOr(MusavaccaTints.HEX_COLOR_KEY, MusavaccaTints.DEFAULT_TINT));
        this.originPos = readBlockPos(input, TAG_ORIGIN_X, TAG_ORIGIN_Y, TAG_ORIGIN_Z, this.getBlockPos());
        this.axis = axisFromString(input.getStringOr(TAG_AXIS, "x"));
        //?} else if >=1.21.5 <1.21.6 {
        /*this.portalId = readUuid(tag, TAG_PORTAL_ID, UUID.randomUUID());
        this.hexColor = MusavaccaTints.stored(tag.getIntOr(MusavaccaTints.HEX_COLOR_KEY, MusavaccaTints.DEFAULT_TINT));
        this.originPos = readBlockPos(tag, TAG_ORIGIN_X, TAG_ORIGIN_Y, TAG_ORIGIN_Z, this.getBlockPos());
        this.axis = axisFromString(tag.getStringOr(TAG_AXIS, "x"));
        *///?} else {
        /*this.portalId = readUuid(tag, TAG_PORTAL_ID, UUID.randomUUID());
        this.hexColor = MusavaccaTints.stored((tag.contains(MusavaccaTints.HEX_COLOR_KEY, Tag.TAG_ANY_NUMERIC) ? tag.getInt(MusavaccaTints.HEX_COLOR_KEY) : MusavaccaTints.DEFAULT_TINT));
        this.originPos = readBlockPos(tag, TAG_ORIGIN_X, TAG_ORIGIN_Y, TAG_ORIGIN_Z, this.getBlockPos());
        this.axis = axisFromString((tag.contains(TAG_AXIS, Tag.TAG_STRING) ? tag.getString(TAG_AXIS) : "x"));
        *///?}
        this.frontDirection = PearlPortalFrame.normalizeFrontDirection(
                this.axis,
                //? if >=1.21.6 {
                directionFromString(input.getStringOr(TAG_FRONT_DIRECTION, ""))
                //?} else if >=1.21.5 <1.21.6 {
                /*directionFromString(tag.getStringOr(TAG_FRONT_DIRECTION, ""))
                *///?} else {
                /*directionFromString((tag.contains(TAG_FRONT_DIRECTION, Tag.TAG_STRING) ? tag.getString(TAG_FRONT_DIRECTION) : ""))
                *///?}
        );
        this.upDirection = PearlPortalFrame.normalizeUpDirection(
                this.axis,
                this.frontDirection,
        //? if >=1.21.6 {
                directionFromString(input.getStringOr(TAG_UP_DIRECTION, ""))
        );
        this.width = clampWidth(input.getIntOr(TAG_WIDTH, PearlPortalFrame.MIN_WIDTH));
        this.height = clampHeight(input.getIntOr(TAG_HEIGHT, PearlPortalFrame.MIN_HEIGHT));
        //?} else if >=1.21.5 <1.21.6 {
                /*directionFromString(tag.getStringOr(TAG_UP_DIRECTION, ""))
        );
        this.width = clampWidth(tag.getIntOr(TAG_WIDTH, PearlPortalFrame.MIN_WIDTH));
        this.height = clampHeight(tag.getIntOr(TAG_HEIGHT, PearlPortalFrame.MIN_HEIGHT));
        *///?} else {
                /*directionFromString((tag.contains(TAG_UP_DIRECTION, Tag.TAG_STRING) ? tag.getString(TAG_UP_DIRECTION) : ""))
        );
        this.width = clampWidth((tag.contains(TAG_WIDTH, Tag.TAG_ANY_NUMERIC) ? tag.getInt(TAG_WIDTH) : PearlPortalFrame.MIN_WIDTH));
        this.height = clampHeight((tag.contains(TAG_HEIGHT, Tag.TAG_ANY_NUMERIC) ? tag.getInt(TAG_HEIGHT) : PearlPortalFrame.MIN_HEIGHT));
        *///?}

        PearlPortalFrame.Shape fallbackShape = new PearlPortalFrame.Shape(
                this.axis,
                this.getOriginPos(),
                this.width,
                this.height,
                this.frontDirection,
                this.upDirection,
                null
        );

        this.exitAnchorPos = readBlockPos(
                //? if >=1.21.6 {
                input,
                //?} else {
                /*tag,
                *///?}
                TAG_EXIT_ANCHOR_X,
                TAG_EXIT_ANCHOR_Y,
                TAG_EXIT_ANCHOR_Z,
                fallbackShape.exitAnchorPos()
        );
    }

    @Override
    //? if >=1.21.6 {
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    //?} else {
    /*protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
    *///?}

        //? if >=1.21.6 {
        output.putString(TAG_PORTAL_ID, this.portalId.toString());
        output.putInt(MusavaccaTints.HEX_COLOR_KEY, this.hexColor);
        //?} else {
        /*tag.putString(TAG_PORTAL_ID, this.portalId.toString());
        tag.putInt(MusavaccaTints.HEX_COLOR_KEY, this.hexColor);
        *///?}

        BlockPos origin = this.getOriginPos();
        //? if >=1.21.6 {
        output.putInt(TAG_ORIGIN_X, origin.getX());
        output.putInt(TAG_ORIGIN_Y, origin.getY());
        output.putInt(TAG_ORIGIN_Z, origin.getZ());
        //?} else {
        /*tag.putInt(TAG_ORIGIN_X, origin.getX());
        tag.putInt(TAG_ORIGIN_Y, origin.getY());
        tag.putInt(TAG_ORIGIN_Z, origin.getZ());
        *///?}

        //? if >=1.21.6 {
        output.putString(TAG_AXIS, axisToString(this.getPortalAxis()));
        output.putString(TAG_FRONT_DIRECTION, directionToString(this.getPortalFrontDirection()));
        output.putString(TAG_UP_DIRECTION, directionToString(this.getPortalUpDirection()));
        output.putInt(TAG_WIDTH, this.width);
        output.putInt(TAG_HEIGHT, this.height);
        //?} else {
        /*tag.putString(TAG_AXIS, axisToString(this.getPortalAxis()));
        tag.putString(TAG_FRONT_DIRECTION, directionToString(this.getPortalFrontDirection()));
        tag.putString(TAG_UP_DIRECTION, directionToString(this.getPortalUpDirection()));
        tag.putInt(TAG_WIDTH, this.width);
        tag.putInt(TAG_HEIGHT, this.height);
        *///?}

        BlockPos anchor = this.getExitAnchorPos();
        //? if >=1.21.6 {
        output.putInt(TAG_EXIT_ANCHOR_X, anchor.getX());
        output.putInt(TAG_EXIT_ANCHOR_Y, anchor.getY());
        output.putInt(TAG_EXIT_ANCHOR_Z, anchor.getZ());
        //?} else {
        /*tag.putInt(TAG_EXIT_ANCHOR_X, anchor.getX());
        tag.putInt(TAG_EXIT_ANCHOR_Y, anchor.getY());
        tag.putInt(TAG_EXIT_ANCHOR_Z, anchor.getZ());
        *///?}
    }

    @Override
    //? if >=1.21.5 {
    protected void applyImplicitComponents(DataComponentGetter input) {
    //?} else {
    /*protected void applyImplicitComponents(DataComponentInput input) {
    *///?}
        super.applyImplicitComponents(input);

        Integer savedHex = input.get(ModDataComponents.HEX_COLOR.get());
        if (savedHex != null) {
            this.hexColor = MusavaccaTints.stored(savedHex);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(ModDataComponents.HEX_COLOR.get(), this.hexColor);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    //? if >=1.21.6 {
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
    //?} else {
    /*public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        super.handleUpdateTag(tag, provider);
    *///?}
        this.rerenderClientNow();
    }

    @Override
    //? if >=1.21.6 {
    public void onDataPacket(Connection connection, ValueInput input) {
        super.onDataPacket(connection, input);
    //?} else {
    /*public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        super.onDataPacket(connection, packet, provider);
    *///?}
        this.rerenderClientNow();
    }

    private static BlockPos readBlockPos(
            //? if >=1.21.6 {
            ValueInput input,
            //?} else {
            /*CompoundTag tag,
            *///?}
            String xTag,
            String yTag,
            String zTag,
            BlockPos fallback
    ) {
        return new BlockPos(
                //? if >=1.21.6 {
                input.getIntOr(xTag, fallback.getX()),
                input.getIntOr(yTag, fallback.getY()),
                input.getIntOr(zTag, fallback.getZ())
                //?} else if >=1.21.5 <1.21.6 {
                /*tag.getIntOr(xTag, fallback.getX()),
                tag.getIntOr(yTag, fallback.getY()),
                tag.getIntOr(zTag, fallback.getZ())
                *///?} else {
                /*(tag.contains(xTag, Tag.TAG_ANY_NUMERIC) ? tag.getInt(xTag) : fallback.getX()),
                (tag.contains(yTag, Tag.TAG_ANY_NUMERIC) ? tag.getInt(yTag) : fallback.getY()),
                (tag.contains(zTag, Tag.TAG_ANY_NUMERIC) ? tag.getInt(zTag) : fallback.getZ())
                *///?}
        );
    }

    //? if >=1.21.6 {
    private static UUID readUuid(ValueInput input, String key, UUID fallback) {
        String text = input.getStringOr(key, "");
    //?} else if >=1.21.5 <1.21.6 {
    /*private static UUID readUuid(CompoundTag tag, String key, UUID fallback) {
        String text = tag.getStringOr(key, "");
    *///?} else {
    /*private static UUID readUuid(CompoundTag tag, String key, UUID fallback) {
        String text = (tag.contains(key, Tag.TAG_STRING) ? tag.getString(key) : "");
    *///?}
        if (text.isEmpty()) {
            return fallback;
        }

        try {
            return UUID.fromString(text);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    private static Direction.Axis axisFromString(String text) {
        return switch ((text == null ? "" : text).toLowerCase()) {
            case "y" -> Direction.Axis.Y;
            case "z" -> Direction.Axis.Z;
            default -> Direction.Axis.X;
        };
    }

    private static Direction directionFromString(String text) {
        return switch ((text == null ? "" : text).toLowerCase()) {
            case "north" -> Direction.NORTH;
            case "south" -> Direction.SOUTH;
            case "west" -> Direction.WEST;
            case "east" -> Direction.EAST;
            case "up" -> Direction.UP;
            case "down" -> Direction.DOWN;
            default -> null;
        };
    }

    private static String directionToString(Direction direction) {
        return switch (direction) {
            case NORTH -> "north";
            case SOUTH -> "south";
            case WEST -> "west";
            case EAST -> "east";
            case UP -> "up";
            case DOWN -> "down";
        };
    }

    private static String axisToString(Direction.Axis axis) {
        return switch (axis) {
            case X -> "x";
            case Y -> "y";
            case Z -> "z";
        };
    }

    private static int clampWidth(int value) {
        return clamp(value, PearlPortalFrame.MIN_WIDTH, PearlPortalFrame.MAX_WIDTH);
    }

    private static int clampHeight(int value) {
        return clamp(value, PearlPortalFrame.MIN_HEIGHT, PearlPortalFrame.MAX_HEIGHT);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
