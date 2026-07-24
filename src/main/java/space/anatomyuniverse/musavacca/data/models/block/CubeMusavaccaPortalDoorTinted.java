package space.anatomyuniverse.musavacca.data.models.block;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.block.custom.MusavaccaPortalDoorBlock;
import space.anatomyuniverse.musavacca.tint.PearlFireTintProfiles;
import space.anatomyuniverse.musavacca.tint.ProfileHexColorItemTintSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.model.Variant;
//?}

public final class CubeMusavaccaPortalDoorTinted {
    private CubeMusavaccaPortalDoorTinted() {}

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
            if (half == DoubleBlockHalf.LOWER) {
                if (hinge == DoorHingeSide.LEFT) {
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

            if (hinge == DoorHingeSide.LEFT) {
                return ResourceLocation.parse(
                        open
                                ? this.topLeftOpen
                                : this.topLeft
                );
            }

            return ResourceLocation.parse(
                    open
                            ? this.topRightOpen
                            : this.topRight
            );
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
             * portal only exists in the upper half.
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
             * portal occupies both block positions.
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

    public record Models(
            DoorModels baseModels,
            DoorModels litModels,
            PortalModels portalModels,
            String itemModel,
            PearlFireTintProfiles.Profile tintProfile
    ) {
        public Models {
            if (baseModels == null) {
                throw new IllegalArgumentException(
                        "baseModels must not be null"
                );
            }

            if (litModels == null) {
                throw new IllegalArgumentException(
                        "litModels must not be null"
                );
            }

            if (portalModels == null) {
                throw new IllegalArgumentException(
                        "portalModels must not be null"
                );
            }

            if (
                    itemModel == null
                            || itemModel.isBlank()
            ) {
                throw new IllegalArgumentException(
                        "itemModel must not be blank"
                );
            }

            if (tintProfile == null) {
                throw new IllegalArgumentException(
                        "tintProfile must not be null"
                );
            }
        }

        public ResourceLocation itemModelLocation() {
            return ResourceLocation.parse(
                    this.itemModel
            );
        }
    }

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
                (block, stateModels) -> {
                    if (
                            !(block instanceof
                                    MusavaccaPortalDoorBlock)
                                    || stateModels == null
                    ) {
                        return;
                    }

                    generateBlockState(
                            blocks,
                            block,
                            stateModels
                    );

                    generateItemModel(
                            items,
                            block,
                            stateModels
                    );
                }
        );
    }

    private static void generateBlockState(
            BlockModelGenerators blocks,
            Block block,
            Models models
    ) {
        MultiPartGenerator multi =
                MultiPartGenerator.multiPart(block);

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

                        multi = addPart(
                                multi,
                                models
                                        .baseModels()
                                        .model(
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

                        multi = addPart(
                                multi,
                                models
                                        .litModels()
                                        .model(
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

                        ResourceLocation portalModel =
                                models
                                        .portalModels()
                                        .model(
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

        blocks.blockStateOutput.accept(multi);
    }

    private static void generateItemModel(
            ItemModelGenerators items,
            Block block,
            Models models
    ) {
        items.itemModelOutput.accept(
                block.asItem(),
                new BlockModelWrapper.Unbaked(
                        models.itemModelLocation(),
                        createItemTintSources(
                                models.tintProfile()
                        )
                )
        );
    }

    private static List<ItemTintSource>
    createItemTintSources(
            PearlFireTintProfiles.Profile profile
    ) {
        List<ItemTintSource> tintSources =
                new ArrayList<>(
                        profile.layerCount()
                );

        for (
                int layerIndex = 0;
                layerIndex < profile.layerCount();
                ++layerIndex
        ) {
            tintSources.add(
                    ProfileHexColorItemTintSource.of(
                            layerIndex,
                            profile,
                            false
                    )
            );
        }

        return List.copyOf(tintSources);
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
                BlockModelGenerators
                        .condition()
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

        if (yRotation != 0) {
            variant = variant.with(
                    VariantProperties.Y_ROT,
                    rotation(yRotation)
            );
        }

        return variant;
    }

    private static VariantProperties.Rotation rotation(
            int degrees
    ) {
        return switch (
                Math.floorMod(degrees, 360)
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
                new Variant(model);

        Quadrant rotation =
                quadrant(yRotation);

        return rotation == Quadrant.R0
                ? variant
                : variant.withYRot(rotation);
    }

    private static Quadrant quadrant(
            int degrees
    ) {
        return switch (
                Math.floorMod(degrees, 360)
                ) {
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> Quadrant.R0;
        };
    }
    //?}

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