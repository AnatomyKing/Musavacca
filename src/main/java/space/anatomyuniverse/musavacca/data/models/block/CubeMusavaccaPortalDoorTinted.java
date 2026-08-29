package space.anatomyuniverse.musavacca.data.models.block;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
*///?} else {
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.BlockModelWrapper;
//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Condition;
import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.model.Variant;
//?}
//?}
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
//? if >=1.21.4 {
import space.anatomyuniverse.musavacca.tint.HexColorItemTintSource;
import space.anatomyuniverse.musavacca.tint.ProfileHexColorItemTintSource;
//?}

import java.util.Map;
//? if >=1.21.4 {
import java.util.List;
//?}

public final class CubeMusavaccaPortalDoorTinted {

    private CubeMusavaccaPortalDoorTinted() {}

    private record DoorModels(
            ResourceLocation bottomLeft,
            ResourceLocation bottomLeftOpen,
            ResourceLocation bottomRight,
            ResourceLocation bottomRightOpen,
            ResourceLocation topLeft,
            ResourceLocation topLeftOpen,
            ResourceLocation topRight,
            ResourceLocation topRightOpen
    ) {
        public ResourceLocation model(
                DoubleBlockHalf half,
                DoorHingeSide hinge,
                boolean open
        ) {
            if (
                    half
                            == DoubleBlockHalf.LOWER
            ) {
                if (
                        hinge
                                == DoorHingeSide.LEFT
                ) {
                    return open
                            ? this.bottomLeftOpen
                            : this.bottomLeft;
                }

                return open
                        ? this.bottomRightOpen
                        : this.bottomRight;
            }

            if (
                    hinge
                            == DoorHingeSide.LEFT
            ) {
                return open
                        ? this.topLeftOpen
                        : this.topLeft;
            }

            return open
                    ? this.topRightOpen
                    : this.topRight;
        }
    }

    /*
     * Normal charged Banana Pearl knob.
     *
     * Used by:
     *
     * LIT=true
     * LIT_PORTAL=false
     * PORTAL=false
     *
     * AND:
     *
     * LIT=true
     * LIT_PORTAL=true
     * PORTAL=true
     *
     * In other words, once an imbued door becomes an active portal,
     * the normal lit knob replaces the tinted lit_portal knob.
     */
    public record LitModels(
            String bottomLeft,
            String bottomLeftOpen,
            String bottomRight,
            String bottomRightOpen
    ) {
        public LitModels {
            requireModel(
                    bottomLeft,
                    "bottomLeft"
            );

            requireModel(
                    bottomLeftOpen,
                    "bottomLeftOpen"
            );

            requireModel(
                    bottomRight,
                    "bottomRight"
            );

            requireModel(
                    bottomRightOpen,
                    "bottomRightOpen"
            );
        }

        @Nullable
        public ResourceLocation model(
                DoubleBlockHalf half,
                DoorHingeSide hinge,
                boolean open
        ) {
            if (
                    half
                            != DoubleBlockHalf.LOWER
            ) {
                return null;
            }

            if (
                    hinge
                            == DoorHingeSide.LEFT
            ) {
                return ResourceLocation.parse(
                        open
                                ? this.bottomLeftOpen
                                : this.bottomLeft
                );
            }

            return ResourceLocation.parse(
                    open
                            ? this.bottomRightOpen
                            : this.bottomRight
            );
        }
    }

    /*
     * Hex-imbued knob.
     *
     * This model is tinted using the stored HEX_COLOR.
     *
     * It is ONLY visible while:
     *
     * LIT=false
     * LIT_PORTAL=true
     * PORTAL=false
     *
     * Once a Banana Pearl is inserted:
     *
     * LIT=true
     * LIT_PORTAL=true
     * PORTAL=true
     *
     * and this model disappears, being replaced by LitModels.
     */
    public record LitPortalModels(
            String bottomLeft,
            String bottomLeftOpen,
            String bottomRight,
            String bottomRightOpen
    ) {
        public LitPortalModels {
            requireModel(
                    bottomLeft,
                    "bottomLeft"
            );

            requireModel(
                    bottomLeftOpen,
                    "bottomLeftOpen"
            );

            requireModel(
                    bottomRight,
                    "bottomRight"
            );

            requireModel(
                    bottomRightOpen,
                    "bottomRightOpen"
            );
        }

        @Nullable
        public ResourceLocation model(
                DoubleBlockHalf half,
                DoorHingeSide hinge,
                boolean open
        ) {
            if (
                    half
                            != DoubleBlockHalf.LOWER
            ) {
                return null;
            }

            if (
                    hinge
                            == DoorHingeSide.LEFT
            ) {
                return ResourceLocation.parse(
                        open
                                ? this.bottomLeftOpen
                                : this.bottomLeft
                );
            }

            return ResourceLocation.parse(
                    open
                            ? this.bottomRightOpen
                            : this.bottomRight
            );
        }
    }

    /*
     * Actual portal surface.
     *
     * Only visible while PORTAL=true.
     */
    public record PortalModels(
            String bottomLeftOpen,
            String bottomRightOpen,
            String doorPortalTopLeft,
            String doorPortalTopRight,
            String topLeftOpen,
            String topRightOpen
    ) {
        public PortalModels {
            requireModel(
                    bottomLeftOpen,
                    "bottomLeftOpen"
            );

            requireModel(
                    bottomRightOpen,
                    "bottomRightOpen"
            );

            requireModel(
                    doorPortalTopLeft,
                    "doorPortalTopLeft"
            );

            requireModel(
                    doorPortalTopRight,
                    "doorPortalTopRight"
            );

            requireModel(
                    topLeftOpen,
                    "topLeftOpen"
            );

            requireModel(
                    topRightOpen,
                    "topRightOpen"
            );
        }

        @Nullable
        public ResourceLocation model(
                DoubleBlockHalf half,
                DoorHingeSide hinge,
                boolean open
        ) {
            if (!open) {
                if (
                        half
                                == DoubleBlockHalf.LOWER
                ) {
                    return null;
                }

                return ResourceLocation.parse(
                        hinge
                                == DoorHingeSide.LEFT
                                ? this.doorPortalTopLeft
                                : this.doorPortalTopRight
                );
            }

            if (
                    half
                            == DoubleBlockHalf.LOWER
            ) {
                return ResourceLocation.parse(
                        hinge
                                == DoorHingeSide.LEFT
                                ? this.bottomLeftOpen
                                : this.bottomRightOpen
                );
            }

            return ResourceLocation.parse(
                    hinge
                            == DoorHingeSide.LEFT
                            ? this.topLeftOpen
                            : this.topRightOpen
            );
        }
    }

    public record Models(
            ItemLike doorItem,
            ItemLike chargedDoorItem,
            ItemLike imbuedDoorItem,
            LitModels litModels,
            LitPortalModels litPortalModels,
            PortalModels portalModels,
            PearlFireTintProfiles.Profile tintProfile
    ) {
        public Models {
            if (
                    doorItem
                            == null
            ) {
                throw new IllegalArgumentException(
                        "doorItem must not be null"
                );
            }

            if (
                    chargedDoorItem
                            == null
            ) {
                throw new IllegalArgumentException(
                        "chargedDoorItem must not be null"
                );
            }

            if (
                    imbuedDoorItem
                            == null
            ) {
                throw new IllegalArgumentException(
                        "imbuedDoorItem must not be null"
                );
            }

            if (
                    litModels
                            == null
            ) {
                throw new IllegalArgumentException(
                        "litModels must not be null"
                );
            }

            if (
                    litPortalModels
                            == null
            ) {
                throw new IllegalArgumentException(
                        "litPortalModels must not be null"
                );
            }

            if (
                    portalModels
                            == null
            ) {
                throw new IllegalArgumentException(
                        "portalModels must not be null"
                );
            }

            if (
                    tintProfile
                            == null
            ) {
                throw new IllegalArgumentException(
                        "tintProfile must not be null"
                );
            }
        }
    }

    //? if <1.21.4 {
    /*public static void generate(
            BlockStateProvider blocks,
            ItemModelProvider items,
            Map<Block, Models> models
    ) {
        if (models == null || models.isEmpty()) {
            return;
        }

        models.forEach((block, modelsForBlock) -> {
            if (!(block instanceof MusavaccaPortalDoorBlock)
                    || modelsForBlock == null) {
                return;
            }

            DoorModels baseModels = generateLegacyBaseDoorModels(
                    blocks,
                    block
            );

            generateLegacyBlockState(
                    blocks,
                    block,
                    baseModels,
                    modelsForBlock
            );

            generateLegacyItemModels(
                    items,
                    block,
                    modelsForBlock
            );
        });
    }

    private static DoorModels generateLegacyBaseDoorModels(
            BlockStateProvider blocks,
            Block block
    ) {
        ResourceLocation id = blockId(block);
        ResourceLocation bottom = ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "block/" + id.getPath() + "_bottom"
        );
        ResourceLocation top = ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "block/" + id.getPath() + "_top"
        );

        ResourceLocation bottomLeft = legacyDoorModel(
                blocks, id, "_bottom_left", "door_bottom_left", bottom, top
        );
        ResourceLocation bottomLeftOpen = legacyDoorModel(
                blocks, id, "_bottom_left_open", "door_bottom_left_open", bottom, top
        );
        ResourceLocation bottomRight = legacyDoorModel(
                blocks, id, "_bottom_right", "door_bottom_right", bottom, top
        );
        ResourceLocation bottomRightOpen = legacyDoorModel(
                blocks, id, "_bottom_right_open", "door_bottom_right_open", bottom, top
        );
        ResourceLocation topLeft = legacyDoorModel(
                blocks, id, "_top_left", "door_top_left", bottom, top
        );
        ResourceLocation topLeftOpen = legacyDoorModel(
                blocks, id, "_top_left_open", "door_top_left_open", bottom, top
        );
        ResourceLocation topRight = legacyDoorModel(
                blocks, id, "_top_right", "door_top_right", bottom, top
        );
        ResourceLocation topRightOpen = legacyDoorModel(
                blocks, id, "_top_right_open", "door_top_right_open", bottom, top
        );

        return new DoorModels(
                bottomLeft,
                bottomLeftOpen,
                bottomRight,
                bottomRightOpen,
                topLeft,
                topLeftOpen,
                topRight,
                topRightOpen
        );
    }

    private static ResourceLocation legacyDoorModel(
            BlockStateProvider blocks,
            ResourceLocation blockId,
            String suffix,
            String parent,
            ResourceLocation bottom,
            ResourceLocation top
    ) {
        blocks.models()
                .withExistingParent(
                        blockId.getPath() + suffix,
                        ResourceLocation.withDefaultNamespace("block/" + parent)
                )
                .texture("bottom", bottom)
                .texture("top", top);

        return ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "block/" + blockId.getPath() + suffix
        );
    }

    private static void generateLegacyBlockState(
            BlockStateProvider blocks,
            Block block,
            DoorModels baseModels,
            Models models
    ) {
        MultiPartBlockStateBuilder multi = blocks.getMultipartBuilder(block);

        for (Direction facing : horizontalDirections()) {
            for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
                for (DoorHingeSide hinge : DoorHingeSide.values()) {
                    for (boolean open : new boolean[]{false, true}) {
                        int rotation = legacyDoorYRotation(facing, hinge, open);

                        addLegacyPart(
                                multi,
                                baseModels.model(half, hinge, open),
                                facing,
                                half,
                                hinge,
                                open,
                                null,
                                null,
                                rotation
                        );

                        ResourceLocation lit = models.litModels().model(
                                half,
                                hinge,
                                open
                        );

                        if (lit != null) {
                            addLegacyPart(
                                    multi, lit, facing, half, hinge, open,
                                    MusavaccaPortalDoorBlock.LIT,
                                    MusavaccaPortalDoorBlock.LIT_PORTAL,
                                    rotation
                            );
                            addLegacyPart(
                                    multi, lit, facing, half, hinge, open,
                                    MusavaccaPortalDoorBlock.PORTAL,
                                    null,
                                    rotation
                            );
                        }

                        ResourceLocation litPortal = models.litPortalModels().model(
                                half,
                                hinge,
                                open
                        );

                        if (litPortal != null) {
                            addLegacyPart(
                                    multi, litPortal, facing, half, hinge, open,
                                    MusavaccaPortalDoorBlock.LIT_PORTAL,
                                    MusavaccaPortalDoorBlock.PORTAL,
                                    rotation
                            );
                        }

                        ResourceLocation portal = models.portalModels().model(
                                half,
                                hinge,
                                open
                        );

                        if (portal != null) {
                            addLegacyPart(
                                    multi, portal, facing, half, hinge, open,
                                    MusavaccaPortalDoorBlock.PORTAL,
                                    null,
                                    rotation
                            );
                        }
                    }
                }
            }
        }
    }

    private static void addLegacyPart(
            MultiPartBlockStateBuilder multi,
            ResourceLocation model,
            Direction facing,
            DoubleBlockHalf half,
            DoorHingeSide hinge,
            boolean open,
            @Nullable BooleanProperty requiredTrue,
            @Nullable BooleanProperty requiredFalse,
            int yRotation
    ) {
        var part = multi.part()
                .modelFile(new ModelFile.UncheckedModelFile(model))
                .rotationY(yRotation)
                .addModel()
                .condition(MusavaccaPortalDoorBlock.FACING, facing)
                .condition(MusavaccaPortalDoorBlock.HALF, half)
                .condition(MusavaccaPortalDoorBlock.HINGE, hinge)
                .condition(MusavaccaPortalDoorBlock.OPEN, open);

        if (requiredTrue != null) {
            part.condition(requiredTrue, true);
        }
        if (requiredFalse != null) {
            part.condition(requiredFalse, false);
        }

        part.end();
    }

    private static int legacyDoorYRotation(
            Direction facing,
            DoorHingeSide hinge,
            boolean open
    ) {
        int closed = switch (facing) {
            case EAST -> 0;
            case SOUTH -> 90;
            case WEST -> 180;
            case NORTH -> 270;
            default -> 0;
        };

        return open
                ? Math.floorMod(
                        closed + (hinge == DoorHingeSide.LEFT ? 90 : -90),
                        360
                )
                : closed;
    }

    private static void generateLegacyItemModels(
            ItemModelProvider items,
            Block block,
            Models models
    ) {
        ResourceLocation blockId = blockId(block);
        ResourceLocation base = legacyItemTexture(blockId, "");
        ResourceLocation knob = legacyItemTexture(blockId, "_knob");
        ResourceLocation portal = legacyItemTexture(blockId, "_portal");

        legacyLayeredItem(items, models.doorItem(), base);
        legacyLayeredItem(items, models.chargedDoorItem(), base, knob);
        legacyLayeredItem(items, models.imbuedDoorItem(), base, portal);
    }

    private static void legacyLayeredItem(
            ItemModelProvider items,
            ItemLike item,
            ResourceLocation... textures
    ) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item.asItem());
        var model = items.getBuilder(itemId.getPath())
                .parent(new ModelFile.UncheckedModelFile(
                        ResourceLocation.withDefaultNamespace("item/generated")
                ));

        for (int layer = 0; layer < textures.length; ++layer) {
            model.texture("layer" + layer, textures[layer]);
        }
    }

    private static ResourceLocation legacyItemTexture(
            ResourceLocation blockId,
            String suffix
    ) {
        return ResourceLocation.fromNamespaceAndPath(
                blockId.getNamespace(),
                "item/" + blockId.getPath() + suffix
        );
    }
    *///?} else {
    public static void generate(
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            Map<Block, Models> models
    ) {
        if (
                models == null
                        || models.isEmpty()
        ) {
            return;
        }

        models.forEach(
                (block, modelsForBlock) -> {
                    if (
                            !(block
                                    instanceof MusavaccaPortalDoorBlock)
                                    || modelsForBlock
                                    == null
                    ) {
                        return;
                    }

                    DoorModels baseModels =
                            generateBaseDoorModels(
                                    blocks,
                                    block
                            );

                    generateBlockState(
                            blocks,
                            block,
                            baseModels,
                            modelsForBlock
                    );

                    generateItemModels(
                            blocks,
                            items,
                            block,
                            modelsForBlock
                    );
                }
        );
    }

    private static DoorModels generateBaseDoorModels(
            BlockModelGenerators blocks,
            Block block
    ) {
        TextureMapping textures =
                TextureMapping.door(
                        block
                );

        ResourceLocation bottomLeft =
                ModelTemplates.DOOR_BOTTOM_LEFT
                        .create(
                                block,
                                textures,
                                blocks.modelOutput
                        );

        ResourceLocation bottomLeftOpen =
                ModelTemplates.DOOR_BOTTOM_LEFT_OPEN
                        .create(
                                block,
                                textures,
                                blocks.modelOutput
                        );

        ResourceLocation bottomRight =
                ModelTemplates.DOOR_BOTTOM_RIGHT
                        .create(
                                block,
                                textures,
                                blocks.modelOutput
                        );

        ResourceLocation bottomRightOpen =
                ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN
                        .create(
                                block,
                                textures,
                                blocks.modelOutput
                        );

        ResourceLocation topLeft =
                ModelTemplates.DOOR_TOP_LEFT
                        .create(
                                block,
                                textures,
                                blocks.modelOutput
                        );

        ResourceLocation topLeftOpen =
                ModelTemplates.DOOR_TOP_LEFT_OPEN
                        .create(
                                block,
                                textures,
                                blocks.modelOutput
                        );

        ResourceLocation topRight =
                ModelTemplates.DOOR_TOP_RIGHT
                        .create(
                                block,
                                textures,
                                blocks.modelOutput
                        );

        ResourceLocation topRightOpen =
                ModelTemplates.DOOR_TOP_RIGHT_OPEN
                        .create(
                                block,
                                textures,
                                blocks.modelOutput
                        );

        return new DoorModels(
                bottomLeft,
                bottomLeftOpen,
                bottomRight,
                bottomRightOpen,
                topLeft,
                topLeftOpen,
                topRight,
                topRightOpen
        );
    }

    private static void generateBlockState(
            BlockModelGenerators blocks,
            Block block,
            DoorModels baseModels,
            Models models
    ) {
        MultiPartGenerator multi =
                MultiPartGenerator.multiPart(
                        block
                );

        for (
                Direction facing
                : horizontalDirections()
        ) {
            for (
                    DoubleBlockHalf half
                    : DoubleBlockHalf.values()
            ) {
                for (
                        DoorHingeSide hinge
                        : DoorHingeSide.values()
                ) {
                    for (
                            boolean open
                            : new boolean[] {
                            false,
                            true
                    }
                    ) {
                        int yRotation =
                                yRotation(
                                        facing,
                                        hinge,
                                        open
                                );

                        /*
                         * Base wooden door model.
                         *
                         * Always present.
                         */
                        multi = addPart(
                                multi,
                                baseModels.model(
                                        half,
                                        hinge,
                                        open
                                ),
                                facing,
                                half,
                                hinge,
                                open,
                                null,
                                null,
                                yRotation
                        );

                        ResourceLocation litModel =
                                models
                                        .litModels()
                                        .model(
                                                half,
                                                hinge,
                                                open
                                        );

                        if (
                                litModel
                                        != null
                        ) {
                            /*
                             * NORMAL CHARGED DOOR
                             *
                             * LIT=true
                             * LIT_PORTAL=false
                             *
                             * Shows:
                             *
                             * lit_knob_door_*
                             */
                            multi = addPart(
                                    multi,
                                    litModel,
                                    facing,
                                    half,
                                    hinge,
                                    open,
                                    MusavaccaPortalDoorBlock.LIT,
                                    MusavaccaPortalDoorBlock.LIT_PORTAL,
                                    yRotation
                            );

                            /*
                             * ACTIVE IMBUED PORTAL DOOR
                             *
                             * LIT=true
                             * LIT_PORTAL=true
                             * PORTAL=true
                             *
                             * IMPORTANT:
                             *
                             * Once the portal activates, the normal
                             * lit knob is rendered again.
                             *
                             * The tinted lit_portal knob disappears.
                             */
                            multi = addPart(
                                    multi,
                                    litModel,
                                    facing,
                                    half,
                                    hinge,
                                    open,
                                    MusavaccaPortalDoorBlock.PORTAL,
                                    null,
                                    yRotation
                            );
                        }

                        ResourceLocation litPortalModel =
                                models
                                        .litPortalModels()
                                        .model(
                                                half,
                                                hinge,
                                                open
                                        );

                        if (
                                litPortalModel
                                        != null
                        ) {
                            /*
                             * IMBUED BUT NOT CHARGED
                             *
                             * LIT_PORTAL=true
                             * PORTAL=false
                             *
                             * Shows the tinted:
                             *
                             * lit_portal_door_*
                             *
                             * PORTAL=false is explicitly required,
                             * so these models disappear as soon as
                             * the Banana Pearl activates the portal.
                             */
                            multi = addPart(
                                    multi,
                                    litPortalModel,
                                    facing,
                                    half,
                                    hinge,
                                    open,
                                    MusavaccaPortalDoorBlock.LIT_PORTAL,
                                    MusavaccaPortalDoorBlock.PORTAL,
                                    yRotation
                            );
                        }

                        ResourceLocation portalModel =
                                models
                                        .portalModels()
                                        .model(
                                                half,
                                                hinge,
                                                open
                                        );

                        if (
                                portalModel
                                        != null
                        ) {
                            /*
                             * Actual portal surface.
                             *
                             * Only exists while PORTAL=true.
                             */
                            multi = addPart(
                                    multi,
                                    portalModel,
                                    facing,
                                    half,
                                    hinge,
                                    open,
                                    MusavaccaPortalDoorBlock.PORTAL,
                                    null,
                                    yRotation
                            );
                        }
                    }
                }
            }
        }

        blocks.blockStateOutput.accept(
                multi
        );
    }

    private static void generateItemModels(
            BlockModelGenerators blocks,
            ItemModelGenerators items,
            Block block,
            Models models
    ) {
        ResourceLocation blockId =
                blockId(
                        block
                );

        ResourceLocation baseTexture =
                itemTexture(
                        blockId,
                        ""
                );

        ResourceLocation knobTexture =
                itemTexture(
                        blockId,
                        "_knob"
                );

        ResourceLocation portalTexture =
                itemTexture(
                        blockId,
                        "_portal"
                );

        /*
         * MUSAVACCA_DOOR
         *
         * layer0:
         * musavacca_door
         */
        ResourceLocation doorModel =
                ModelTemplates.FLAT_ITEM
                        .create(
                                itemModelLocation(
                                        models.doorItem()
                                ),
                                TextureMapping.layer0(
                                        baseTexture
                                ),
                                blocks.modelOutput
                        );

        /*
         * MUSAVACCA_CHARGED_DOOR
         *
         * layer0:
         * musavacca_door
         *
         * layer1:
         * musavacca_door_knob
         */
        ResourceLocation chargedDoorModel =
                ModelTemplates.TWO_LAYERED_ITEM
                        .create(
                                itemModelLocation(
                                        models.chargedDoorItem()
                                ),
                                TextureMapping.layered(
                                        baseTexture,
                                        knobTexture
                                ),
                                blocks.modelOutput
                        );

        /*
         * MUSAVACCA_IMBUED_DOOR
         *
         * layer0:
         * musavacca_door
         *
         * layer1:
         * musavacca_door_portal
         *
         * The second layer receives HEX_COLOR tinting.
         *
         * There is deliberately NO knob layer.
         */
        ResourceLocation imbuedDoorModel =
                ModelTemplates.TWO_LAYERED_ITEM
                        .create(
                                itemModelLocation(
                                        models.imbuedDoorItem()
                                ),
                                TextureMapping.layered(
                                        baseTexture,
                                        portalTexture
                                ),
                                blocks.modelOutput
                        );

        ItemTintSource noTint =
                ProfileHexColorItemTintSource
                        .noTint(
                                models.tintProfile(),
                                false
                        );

        items.itemModelOutput.accept(
                models
                        .doorItem()
                        .asItem(),
                new BlockModelWrapper.Unbaked(
                        doorModel,
                        List.of(
                                noTint
                        )
                )
        );

        items.itemModelOutput.accept(
                models
                        .chargedDoorItem()
                        .asItem(),
                new BlockModelWrapper.Unbaked(
                        chargedDoorModel,
                        List.of(
                                noTint,
                                noTint
                        )
                )
        );

        items.itemModelOutput.accept(
                models
                        .imbuedDoorItem()
                        .asItem(),
                new BlockModelWrapper.Unbaked(
                        imbuedDoorModel,
                        List.of(
                                noTint,
                                HexColorItemTintSource.INSTANCE
                        )
                )
        );
    }

    private static ResourceLocation itemTexture(
            ResourceLocation blockId,
            String suffix
    ) {
        return ResourceLocation
                .fromNamespaceAndPath(
                        blockId.getNamespace(),
                        "item/"
                                + blockId.getPath()
                                + suffix
                );
    }

    private static ResourceLocation itemModelLocation(
            ItemLike item
    ) {
        ResourceLocation itemId =
                BuiltInRegistries.ITEM
                        .getKey(
                                item.asItem()
                        );

        if (
                itemId
                        == null
        ) {
            throw new IllegalStateException(
                    "Cannot generate a model for an "
                            + "unregistered item"
            );
        }

        return ResourceLocation
                .fromNamespaceAndPath(
                        itemId.getNamespace(),
                        "item/"
                                + itemId.getPath()
                );
    }

    /*
     * requiredTrue:
     *
     * If supplied, this property must be true.
     *
     *
     * requiredFalse:
     *
     * If supplied, this property must be false.
     *
     *
     * This allows exact multipart states such as:
     *
     * LIT=true
     * LIT_PORTAL=false
     *
     * or:
     *
     * LIT_PORTAL=true
     * PORTAL=false
     */
    private static MultiPartGenerator addPart(
            MultiPartGenerator multi,
            ResourceLocation model,
            Direction facing,
            DoubleBlockHalf half,
            DoorHingeSide hinge,
            boolean open,
            @Nullable BooleanProperty requiredTrue,
            @Nullable BooleanProperty requiredFalse,
            int yRotation
    ) {
        var condition =
                //? if <1.21.5
                //Condition.condition()
                //? if >=1.21.5
                BlockModelGenerators.condition()
                        .term(
                                MusavaccaPortalDoorBlock.FACING,
                                facing
                        )
                        .term(
                                MusavaccaPortalDoorBlock.HALF,
                                half
                        )
                        .term(
                                MusavaccaPortalDoorBlock.HINGE,
                                hinge
                        )
                        .term(
                                MusavaccaPortalDoorBlock.OPEN,
                                open
                        );

        if (
                requiredTrue
                        != null
        ) {
            condition.term(
                    requiredTrue,
                    true
            );
        }

        if (
                requiredFalse
                        != null
        ) {
            condition.term(
                    requiredFalse,
                    false
            );
        }

        return multi.with(
                condition,

                //? if <1.21.5 {
                /*variant(
                        model,
                        yRotation
                )
                *///?} else {
                BlockModelGenerators.variant(
                        variant(
                                model,
                                yRotation
                        )
                )
                //?}
        );
    }

    private static int yRotation(
            Direction facing,
            DoorHingeSide hinge,
            boolean open
    ) {
        int closedRotation =
                switch (facing) {
                    case EAST ->
                            0;

                    case SOUTH ->
                            90;

                    case WEST ->
                            180;

                    case NORTH ->
                            270;

                    default ->
                            0;
                };

        if (!open) {
            return closedRotation;
        }

        return Math.floorMod(
                closedRotation
                        + (
                        hinge
                                == DoorHingeSide.LEFT
                                ? 90
                                : -90
                ),
                360
        );
    }

    //? if <1.21.5 {
    /*private static Variant variant(
            ResourceLocation model,
            int yRotation
    ) {
        Variant variant =
                Variant.variant()
                        .with(
                                VariantProperties.MODEL,
                                model
                        );

        if (
                yRotation
                        != 0
        ) {
            variant = variant.with(
                    VariantProperties.Y_ROT,
                    rotation(
                            yRotation
                    )
            );
        }

        return variant;
    }

    private static VariantProperties.Rotation rotation(
            int degrees
    ) {
        return switch (
                Math.floorMod(
                        degrees,
                        360
                )
        ) {
            case 90 ->
                    VariantProperties.Rotation.R90;

            case 180 ->
                    VariantProperties.Rotation.R180;

            case 270 ->
                    VariantProperties.Rotation.R270;

            default ->
                    VariantProperties.Rotation.R0;
        };
    }
    *///?} else {
    private static Variant variant(
            ResourceLocation model,
            int yRotation
    ) {
        Variant variant =
                new Variant(
                        model
                );

        Quadrant rotation =
                quadrant(
                        yRotation
                );

        return rotation
                == Quadrant.R0
                ? variant
                : variant.withYRot(
                rotation
        );
    }

    private static Quadrant quadrant(
            int degrees
    ) {
        return switch (
                Math.floorMod(
                        degrees,
                        360
                )
                ) {
            case 90 ->
                    Quadrant.R90;

            case 180 ->
                    Quadrant.R180;

            case 270 ->
                    Quadrant.R270;

            default ->
                    Quadrant.R0;
        };
    }
    //?}

    //?}

    private static ResourceLocation blockId(
            Block block
    ) {
        ResourceLocation blockId =
                BuiltInRegistries.BLOCK
                        .getKey(
                                block
                        );

        if (
                blockId
                        == null
        ) {
            throw new IllegalStateException(
                    "Cannot generate models for an "
                            + "unregistered block"
            );
        }

        return blockId;
    }

    private static void requireModel(
            String model,
            String name
    ) {
        if (
                model == null
                        || model.isBlank()
        ) {
            throw new IllegalArgumentException(
                    name
                            + " must not be blank"
            );
        }
    }

    private static Direction[]
    horizontalDirections() {
        return new Direction[] {
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST
        };
    }
}
