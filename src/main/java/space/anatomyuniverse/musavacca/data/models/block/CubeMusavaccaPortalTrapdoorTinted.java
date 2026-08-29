package space.anatomyuniverse.musavacca.data.models.block;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
*///?} else {
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Half;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalTrapdoorBlock;

import java.util.Map;

public final class CubeMusavaccaPortalTrapdoorTinted {

    private CubeMusavaccaPortalTrapdoorTinted() {}

    private record BaseModels(
            ResourceLocation bottom,
            ResourceLocation top,
            ResourceLocation open
    ) {
        public ResourceLocation model(
                Half half,
                boolean open
        ) {
            if (open) {
                return this.open;
            }

            return half == Half.TOP
                    ? this.top
                    : this.bottom;
        }
    }

    public record LitModels(
            String bottom,
            String top,
            String open
    ) {
        public LitModels {
            requireModel(bottom, "bottom");
            requireModel(top, "top");
            requireModel(open, "open");
        }

        public ResourceLocation model(
                Half half,
                boolean open
        ) {
            return ResourceLocation.parse(
                    open
                            ? this.open
                            : half == Half.TOP
                            ? this.top
                            : this.bottom
            );
        }
    }

    public record LitPortalModels(
            String bottom,
            String top,
            String open
    ) {
        public LitPortalModels {
            requireModel(bottom, "bottom");
            requireModel(top, "top");
            requireModel(open, "open");
        }

        public ResourceLocation model(
                Half half,
                boolean open
        ) {
            return ResourceLocation.parse(
                    open
                            ? this.open
                            : half == Half.TOP
                            ? this.top
                            : this.bottom
            );
        }
    }

    /*
     * Full active portal surface.
     *
     * CLOSED:
     *     HALF=BOTTOM -> trapdoor_portal_bottom
     *     HALF=TOP    -> trapdoor_portal_top
     *
     * OPEN:
     *     portal_open
     *
     * The OPEN portal model is special:
     * it is the flat horizontal 2px aperture across the opening.
     *
     * HALF=TOP + OPEN=true uses the existing 180-degree X flip in
     * variant(), moving that flat bottom-authored aperture to the top.
     */
    public record PortalModels(
            String bottom,
            String top,
            String open
    ) {
        public PortalModels {
            requireModel(bottom, "bottom");
            requireModel(top, "top");
            requireModel(open, "open");
        }

        public ResourceLocation model(
                Half half,
                boolean open
        ) {
            return ResourceLocation.parse(
                    open
                            ? this.open
                            : half == Half.TOP
                            ? this.top
                            : this.bottom
            );
        }
    }

    public record Models(
            LitModels litModels,
            LitPortalModels litPortalModels,
            PortalModels portalModels
    ) {
        public Models {
            if (litModels == null) {
                throw new IllegalArgumentException(
                        "litModels must not be null"
                );
            }

            if (litPortalModels == null) {
                throw new IllegalArgumentException(
                        "litPortalModels must not be null"
                );
            }

            if (portalModels == null) {
                throw new IllegalArgumentException(
                        "portalModels must not be null"
                );
            }
        }
    }

    //? if <1.21.4 {
    /*public static void generate(
            BlockStateProvider blocks,
            Map<Block, Models> models
    ) {
        if (models == null || models.isEmpty()) {
            return;
        }

        models.forEach((block, modelsForBlock) -> {
            if (!(block instanceof MusavaccaPortalTrapdoorBlock)
                    || modelsForBlock == null) {
                return;
            }

            BaseModels baseModels = legacyBaseModels(block);
            generateLegacyBlockState(
                    blocks,
                    block,
                    baseModels,
                    modelsForBlock
            );

            blocks.simpleBlockItem(
                    block,
                    new ModelFile.UncheckedModelFile(baseModels.bottom())
            );
        });
    }

    private static BaseModels legacyBaseModels(Block block) {
        ResourceLocation id = blockId(block);
        String basePath = "block/" + id.getPath() + "/" + id.getPath();

        return new BaseModels(
                ResourceLocation.fromNamespaceAndPath(
                        id.getNamespace(),
                        basePath + "_bottom"
                ),
                ResourceLocation.fromNamespaceAndPath(
                        id.getNamespace(),
                        basePath + "_top"
                ),
                ResourceLocation.fromNamespaceAndPath(
                        id.getNamespace(),
                        basePath + "_open"
                )
        );
    }

    private static void generateLegacyBlockState(
            BlockStateProvider blocks,
            Block block,
            BaseModels baseModels,
            Models models
    ) {
        MultiPartBlockStateBuilder multi = blocks.getMultipartBuilder(block);

        for (Direction facing : horizontalDirections()) {
            for (Half half : Half.values()) {
                for (boolean open : new boolean[]{false, true}) {
                    int rotation = legacyTrapdoorYRotation(facing);

                    addLegacyPart(
                            multi,
                            baseModels.model(half, open),
                            facing,
                            half,
                            open,
                            null,
                            null,
                            rotation
                    );

                    addLegacyPart(
                            multi,
                            models.litModels().model(half, open),
                            facing,
                            half,
                            open,
                            MusavaccaPortalTrapdoorBlock.LIT,
                            MusavaccaPortalTrapdoorBlock.LIT_PORTAL,
                            rotation
                    );

                    addLegacyPart(
                            multi,
                            models.litModels().model(half, open),
                            facing,
                            half,
                            open,
                            MusavaccaPortalTrapdoorBlock.PORTAL,
                            null,
                            rotation
                    );

                    addLegacyPart(
                            multi,
                            models.litPortalModels().model(half, open),
                            facing,
                            half,
                            open,
                            MusavaccaPortalTrapdoorBlock.LIT_PORTAL,
                            MusavaccaPortalTrapdoorBlock.PORTAL,
                            rotation
                    );

                    addLegacyPart(
                            multi,
                            models.portalModels().model(half, open),
                            facing,
                            half,
                            open,
                            MusavaccaPortalTrapdoorBlock.PORTAL,
                            null,
                            rotation
                    );
                }
            }
        }
    }

    private static void addLegacyPart(
            MultiPartBlockStateBuilder multi,
            ResourceLocation model,
            Direction facing,
            Half half,
            boolean open,
            @Nullable BooleanProperty requiredTrue,
            @Nullable BooleanProperty requiredFalse,
            int yRotation
    ) {
        boolean flipTopOpen = open && half == Half.TOP;
        int resolvedY = flipTopOpen ? yRotation + 180 : yRotation;

        var part = multi.part()
                .modelFile(new ModelFile.UncheckedModelFile(model))
                .rotationX(flipTopOpen ? 180 : 0)
                .rotationY(Math.floorMod(resolvedY, 360))
                .addModel()
                .condition(MusavaccaPortalTrapdoorBlock.FACING, facing)
                .condition(MusavaccaPortalTrapdoorBlock.HALF, half)
                .condition(MusavaccaPortalTrapdoorBlock.OPEN, open);

        if (requiredTrue != null) {
            part.condition(requiredTrue, true);
        }
        if (requiredFalse != null) {
            part.condition(requiredFalse, false);
        }

        part.end();
    }

    private static int legacyTrapdoorYRotation(Direction facing) {
        return switch (facing) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
    *///?} else {
    public static void generate(
            BlockModelGenerators blocks,
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
                                    instanceof MusavaccaPortalTrapdoorBlock)
                                    || modelsForBlock
                                    == null
                    ) {
                        return;
                    }

                    BaseModels baseModels =
                            baseModels(block);

                    generateBlockState(
                            blocks,
                            block,
                            baseModels,
                            modelsForBlock
                    );

                    /*
                     * Keep the inventory item deliberately simple for this
                     * prototype: render the normal bottom trapdoor model.
                     *
                     * The in-world right-click cycle is what is being tested.
                     */
                    blocks.registerSimpleItemModel(
                            block,
                            baseModels.bottom()
                    );
                }
        );
    }

    private static BaseModels baseModels(
            Block block
    ) {
        ResourceLocation blockId =
                blockId(block);

        String basePath =
                "block/"
                        + blockId.getPath()
                        + "/"
                        + blockId.getPath();

        return new BaseModels(
                ResourceLocation.fromNamespaceAndPath(
                        blockId.getNamespace(),
                        basePath + "_bottom"
                ),
                ResourceLocation.fromNamespaceAndPath(
                        blockId.getNamespace(),
                        basePath + "_top"
                ),
                ResourceLocation.fromNamespaceAndPath(
                        blockId.getNamespace(),
                        basePath + "_open"
                )
        );
    }

    private static void generateBlockState(
            BlockModelGenerators blocks,
            Block block,
            BaseModels baseModels,
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
                    Half half
                    : Half.values()
            ) {
                for (
                        boolean open
                        : new boolean[] {
                        false,
                        true
                }
                ) {
                    int yRotation =
                            yRotation(facing);

                    /*
                     * Base wooden trapdoor.
                     *
                     * Always visible.
                     */
                    multi = addPart(
                            multi,
                            baseModels.model(
                                    half,
                                    open
                            ),
                            facing,
                            half,
                            open,
                            null,
                            null,
                            yRotation
                    );

                    /*
                     * NORMAL CHARGED TRAPDOOR
                     *
                     * LIT=true
                     * LIT_PORTAL=false
                     *
                     * Shows:
                     * lit_knob_trapdoor_*
                     */
                    multi = addPart(
                            multi,
                            models
                                    .litModels()
                                    .model(
                                            half,
                                            open
                                    ),
                            facing,
                            half,
                            open,
                            MusavaccaPortalTrapdoorBlock.LIT,
                            MusavaccaPortalTrapdoorBlock.LIT_PORTAL,
                            yRotation
                    );

                    /*
                     * ACTIVE IMBUED PORTAL TRAPDOOR
                     *
                     * PORTAL=true
                     *
                     * The normal lit knob is visible again.
                     */
                    multi = addPart(
                            multi,
                            models
                                    .litModels()
                                    .model(
                                            half,
                                            open
                                    ),
                            facing,
                            half,
                            open,
                            MusavaccaPortalTrapdoorBlock.PORTAL,
                            null,
                            yRotation
                    );

                    /*
                     * IMBUED BUT NOT ACTIVE
                     *
                     * LIT_PORTAL=true
                     * PORTAL=false
                     *
                     * Shows:
                     * lit_portal_trapdoor_*
                     */
                    multi = addPart(
                            multi,
                            models
                                    .litPortalModels()
                                    .model(
                                            half,
                                            open
                                    ),
                            facing,
                            half,
                            open,
                            MusavaccaPortalTrapdoorBlock.LIT_PORTAL,
                            MusavaccaPortalTrapdoorBlock.PORTAL,
                            yRotation
                    );

                    /*
                     * FULL ACTIVE PORTAL SURFACE
                     *
                     * PORTAL=true
                     *
                     * CLOSED:
                     *
                     * HALF=BOTTOM
                     *   -> trapdoor_portal_bottom
                     *
                     * HALF=TOP
                     *   -> trapdoor_portal_top
                     *
                     * OPEN:
                     *
                     * -> portal_open
                     *
                     * portal_open is the flat 2px horizontal aperture.
                     */
                    multi = addPart(
                            multi,
                            models
                                    .portalModels()
                                    .model(
                                            half,
                                            open
                                    ),
                            facing,
                            half,
                            open,
                            MusavaccaPortalTrapdoorBlock.PORTAL,
                            null,
                            yRotation
                    );
                }
            }
        }

        blocks.blockStateOutput.accept(
                multi
        );
    }

    private static MultiPartGenerator addPart(
            MultiPartGenerator multi,
            ResourceLocation model,
            Direction facing,
            Half half,
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
                                MusavaccaPortalTrapdoorBlock.FACING,
                                facing
                        )
                        .term(
                                MusavaccaPortalTrapdoorBlock.HALF,
                                half
                        )
                        .term(
                                MusavaccaPortalTrapdoorBlock.OPEN,
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
                        half,
                        open,
                        yRotation
                )
                *///?} else {
                BlockModelGenerators.variant(
                        variant(
                                model,
                                half,
                                open,
                                yRotation
                        )
                )
                //?}
        );
    }

    /*
     * Vanilla trapdoors use the same open model for both HALF values.
     *
     * A TOP + OPEN trapdoor is the open model flipped 180 degrees on X.
     * That flip also reverses the horizontal reference direction, so its
     * Y rotation receives another 180 degrees.
     */
    //? if <1.21.5 {
    /*private static Variant variant(
            ResourceLocation model,
            Half half,
            boolean open,
            int yRotation
    ) {
        boolean flipTopOpen =
                open
                        && half == Half.TOP;

        int resolvedYRotation =
                flipTopOpen
                        ? yRotation + 180
                        : yRotation;

        Variant variant =
                Variant.variant()
                        .with(
                                VariantProperties.MODEL,
                                model
                        );

        if (flipTopOpen) {
            variant = variant.with(
                    VariantProperties.X_ROT,
                    VariantProperties.Rotation.R180
            );
        }

        if (
                Math.floorMod(
                        resolvedYRotation,
                        360
                )
                        != 0
        ) {
            variant = variant.with(
                    VariantProperties.Y_ROT,
                    rotation(
                            resolvedYRotation
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
            Half half,
            boolean open,
            int yRotation
    ) {
        boolean flipTopOpen =
                open
                        && half == Half.TOP;

        int resolvedYRotation =
                flipTopOpen
                        ? yRotation + 180
                        : yRotation;

        Variant variant =
                new Variant(model);

        if (flipTopOpen) {
            variant = variant.withXRot(
                    Quadrant.R180
            );
        }

        Quadrant yQuadrant =
                quadrant(
                        resolvedYRotation
                );

        return yQuadrant == Quadrant.R0
                ? variant
                : variant.withYRot(
                yQuadrant
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

    private static int yRotation(
            Direction facing
    ) {
        return switch (facing) {
            case NORTH -> 0;
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }

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
