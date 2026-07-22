// file: C:/mods/Musavacca/src/main/java/space/anatomyuniverse/musavacca/data/models/block/CubeMusavaccaPortalDoor.java
package space.anatomyuniverse.musavacca.data.models.block;

import com.mojang.math.Quadrant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;

import java.util.Map;

public final class CubeMusavaccaPortalDoor {
    private CubeMusavaccaPortalDoor() {}

    public record DoorModels(
            String bottomLeft,
            String bottomLeftOpen,
            String bottomRight,
            String bottomRightOpen,
            String topLeft,
            String topLeftOpen,
            String topRight,
            String topRightOpen
    ) {
        public ResourceLocation model(
                DoubleBlockHalf half,
                DoorHingeSide hinge,
                boolean open
        ) {
            String modelPath;

            if (half == DoubleBlockHalf.LOWER) {
                if (hinge == DoorHingeSide.LEFT) {
                    modelPath = open
                            ? this.bottomLeftOpen
                            : this.bottomLeft;
                } else {
                    modelPath = open
                            ? this.bottomRightOpen
                            : this.bottomRight;
                }
            } else {
                if (hinge == DoorHingeSide.LEFT) {
                    modelPath = open
                            ? this.topLeftOpen
                            : this.topLeft;
                } else {
                    modelPath = open
                            ? this.topRightOpen
                            : this.topRight;
                }
            }

            return ResourceLocation.parse(modelPath);
        }
    }

    public record PortalModels(
            String bottomLeftOpen,
            String bottomRightOpen,
            String doorPortalTopLeft,
            String doorPortalTopRight,
            String topLeftOpen,
            String topRightOpen
    ) {
        @Nullable
        public ResourceLocation model(
                DoubleBlockHalf half,
                DoorHingeSide hinge,
                boolean open
        ) {
            /*
             * Closed door:
             * only render the portal embedded in the upper door half.
             */
            if (!open) {
                if (half == DoubleBlockHalf.LOWER) {
                    return null;
                }

                return ResourceLocation.parse(
                        hinge == DoorHingeSide.LEFT
                                ? this.doorPortalTopLeft
                                : this.doorPortalTopRight
                );
            }

            /*
             * Open door:
             * render the stationary portal in both block spaces.
             */
            if (half == DoubleBlockHalf.LOWER) {
                return ResourceLocation.parse(
                        hinge == DoorHingeSide.LEFT
                                ? this.bottomLeftOpen
                                : this.bottomRightOpen
                );
            }

            return ResourceLocation.parse(
                    hinge == DoorHingeSide.LEFT
                            ? this.topLeftOpen
                            : this.topRightOpen
            );
        }
    }

    /**
     * Lit and portal models are complete existing model files.
     *
     * They already contain their own parents and texture mappings.
     * This helper only generates the multipart blockstate.
     *
     * The normal bottom/top texture references remain available here
     * for the shared Musavacca Door setup, but this helper does not
     * generate child models from them anymore.
     */
    public record Models(
            DoorModels baseModels,
            DoorModels litModels,
            PortalModels portalModels,
            String bottomTexture,
            String topTexture,
            String itemModel
    ) {
        public ResourceLocation bottomTextureLocation() {
            return ResourceLocation.parse(this.bottomTexture);
        }

        public ResourceLocation topTextureLocation() {
            return ResourceLocation.parse(this.topTexture);
        }

        public ResourceLocation itemModelLocation() {
            return ResourceLocation.parse(this.itemModel);
        }
    }

    public static void generate(
            BlockModelGenerators gen,
            Map<Block, Models> models
    ) {
        if (models == null || models.isEmpty()) {
            return;
        }

        models.forEach((block, stateModels) -> {
            if (!(block instanceof MusavaccaPortalDoorBlock)
                    || stateModels == null) {
                return;
            }

            MultiPartGenerator multi =
                    MultiPartGenerator.multiPart(block);

            for (Direction facing : horizontalDirections()) {
                for (DoubleBlockHalf half : DoubleBlockHalf.values()) {
                    for (DoorHingeSide hinge : DoorHingeSide.values()) {
                        for (boolean open : new boolean[] {false, true}) {
                            int yRotation =
                                    yRotation(
                                            facing,
                                            hinge,
                                            open
                                    );

                            /*
                             * Always-visible normal Musavacca Door model.
                             */
                            multi = addPart(
                                    multi,
                                    stateModels.baseModels().model(
                                            half,
                                            hinge,
                                            open
                                    ),
                                    facing,
                                    half,
                                    hinge,
                                    open,
                                    null,
                                    yRotation
                            );

                            /*
                             * Lit overlay.
                             *
                             * The referenced model already contains its
                             * own #bottom or #top texture mapping.
                             */
                            multi = addPart(
                                    multi,
                                    stateModels.litModels().model(
                                            half,
                                            hinge,
                                            open
                                    ),
                                    facing,
                                    half,
                                    hinge,
                                    open,
                                    MusavaccaPortalDoorBlock.LIT,
                                    yRotation
                            );

                            /*
                             * Portal overlay.
                             *
                             * The referenced model already contains its
                             * portal_0 through portal_14 texture mappings.
                             */
                            ResourceLocation portalModel =
                                    stateModels.portalModels().model(
                                            half,
                                            hinge,
                                            open
                                    );

                            if (portalModel != null) {
                                multi = addPart(
                                        multi,
                                        portalModel,
                                        facing,
                                        half,
                                        hinge,
                                        open,
                                        MusavaccaPortalDoorBlock.PORTAL,
                                        yRotation
                                );
                            }
                        }
                    }
                }
            }

            gen.blockStateOutput.accept(multi);

            /*
             * Reuse the ordinary Musavacca Door inventory model.
             */
            gen.registerSimpleItemModel(
                    block,
                    stateModels.itemModelLocation()
            );
        });
    }

    private static MultiPartGenerator addPart(
            MultiPartGenerator multi,
            ResourceLocation model,
            Direction facing,
            DoubleBlockHalf half,
            DoorHingeSide hinge,
            boolean open,
            @Nullable BooleanProperty requiredTrue,
            int yRotation
    ) {
        var condition =
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

        if (requiredTrue != null) {
            condition.term(
                    requiredTrue,
                    true
            );
        }

        return multi.with(
                condition,
                BlockModelGenerators.variant(
                        variant(
                                model,
                                yRotation
                        )
                )
        );
    }

    /**
     * Matches the rotations used by vanilla door models.
     */
    private static int yRotation(
            Direction facing,
            DoorHingeSide hinge,
            boolean open
    ) {
        int closedRotation =
                switch (facing) {
                    case EAST -> 0;
                    case SOUTH -> 90;
                    case WEST -> 180;
                    case NORTH -> 270;
                    default -> 0;
                };

        if (!open) {
            return closedRotation;
        }

        return Math.floorMod(
                closedRotation
                        + (
                        hinge == DoorHingeSide.LEFT
                                ? 90
                                : -90
                ),
                360
        );
    }

    private static Variant variant(
            ResourceLocation model,
            int yRotation
    ) {
        Variant variant =
                new Variant(model);

        Quadrant rotation =
                quadrant(yRotation);

        return rotation == Quadrant.R0
                ? variant
                : variant.withYRot(rotation);
    }

    private static Quadrant quadrant(
            int yRotation
    ) {
        return switch (
                Math.floorMod(yRotation, 360)
                ) {
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> Quadrant.R0;
        };
    }

    private static Direction[] horizontalDirections() {
        return new Direction[] {
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH,
                Direction.WEST
        };
    }
}