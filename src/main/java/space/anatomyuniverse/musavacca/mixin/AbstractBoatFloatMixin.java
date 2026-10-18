package space.anatomyuniverse.musavacca.mixin;

//? if >=1.21.11 {
/*import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
*///?} else if >=1.21.2 <1.21.11 {
import net.minecraft.world.entity.vehicle.AbstractBoat;
//?} else {
/*import net.minecraft.world.entity.vehicle.Boat;
*///?}

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import space.anatomyuniverse.musavacca.entity.boat.musavacca.MusavaccaBoat;

@Mixin(

        //? if >=1.21.2 {
        AbstractBoat.class
        //?} else {
        /*Boat.class
        *///?}

)
public abstract class AbstractBoatFloatMixin {

    @ModifyConstant(
            method = "floatBoat()V",
            constant = @Constant(
                    doubleValue = 0.65D
            ),
            require = 1
    )
    private double musavacca$modifyWaterlineFraction(
            double original
    ) {
        return (Object) this instanceof MusavaccaBoat
                ? 0.29D
                : original;
    }
}
