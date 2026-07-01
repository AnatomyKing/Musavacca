package space.anatomyuniverse.musavacca.data.models.engine.core;

import net.minecraft.resources.ResourceLocation;

//? if <1.21.5 {
/*import net.minecraft.client.data.models.blockstates.Variant;
import net.minecraft.client.data.models.blockstates.VariantProperties;
*///?} else {
import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantMutator;
//?}

public final class EngineVariants {
    private EngineVariants() {}

    public static Variant variant(ResourceLocation modelId, int xDeg, int yDeg) {
        //? if <1.21.5 {
        /*Variant variant = Variant.variant().with(VariantProperties.MODEL, modelId);
        if (xDeg != 0) variant = variant.with(VariantProperties.X_ROT, rot(xDeg));
        if (yDeg != 0) variant = variant.with(VariantProperties.Y_ROT, rot(yDeg));
        return variant;
        *///?} else {
        Variant variant = new Variant(modelId);
        if (xDeg != 0) variant = variant.with(VariantMutator.X_ROT.withValue(quadrant(xDeg)));
        if (yDeg != 0) variant = variant.with(VariantMutator.Y_ROT.withValue(quadrant(yDeg)));
        return variant;
        //?}
    }

    //? if <1.21.5 {
    /*private static VariantProperties.Rotation rot(int deg) {
        return switch (Math.floorMod(deg, 360)) {
            case 0 -> VariantProperties.Rotation.R0;
            case 90 -> VariantProperties.Rotation.R90;
            case 180 -> VariantProperties.Rotation.R180;
            case 270 -> VariantProperties.Rotation.R270;
            default -> throw new IllegalArgumentException("Unsupported rotation: " + deg);
        };
    }
    *///?} else {
    private static Quadrant quadrant(int deg) {
        return switch (Math.floorMod(deg, 360)) {
            case 0 -> Quadrant.R0;
            case 90 -> Quadrant.R90;
            case 180 -> Quadrant.R180;
            case 270 -> Quadrant.R270;
            default -> throw new IllegalArgumentException("Unsupported rotation: " + deg);
        };
    }
    //?}
}
