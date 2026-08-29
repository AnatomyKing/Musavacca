package space.anatomyuniverse.musavacca.mixin;

//? if <1.21.2 {
/*import net.minecraft.world.entity.vehicle.Boat;
*///?} else {
import net.minecraft.world.entity.vehicle.AbstractBoat;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import space.anatomyuniverse.musavacca.entity.boat.musavacca.MusavaccaBoat;

@Mixin(
        //? if <1.21.2 {
        /*Boat.class
        *///?} else {
        AbstractBoat.class
        //?}
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
