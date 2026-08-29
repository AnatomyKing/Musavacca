package space.anatomyuniverse.musavacca.entity.boat.musavacca;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;

//? if <1.21.2 {
/*import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
*///?} else {
import net.minecraft.world.entity.vehicle.Raft;
 //?}

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class MusavaccaBoat extends
        //? if <1.21.2 {
        /*Boat
        *///?} else {
        Raft
         //?}
{

    public static final float HITBOX_WIDTH =
            22.0F / 16.0F;

    public static final float HITBOX_HEIGHT =
            14.0F / 16.0F;

    public static final int MAX_PASSENGERS = 3;

    private static final double SEAT_SPACING_Z =
            11.0D / 16.0D;

    /*
     * Passenger order:
     *
     * 0 = driver       -> front
     * 1 = passenger 1  -> middle
     * 2 = passenger 2  -> back
     */
    private static final double DRIVER_SEAT_Z =
            SEAT_SPACING_Z;

    private static final double PASSENGER_ONE_SEAT_Z =
            0.0D;

    private static final double PASSENGER_TWO_SEAT_Z =
            -SEAT_SPACING_Z;

    public MusavaccaBoat(
            EntityType<? extends MusavaccaBoat> entityType,
            Level level
    ) {
        //? if <1.21.2 {
        /*super(entityType, level);
        *///?} else {
        super(
                entityType,
                level,
                () -> ModItems.MUSAVACCA_BOAT.get()
        );
        //?}
    }

    //? if >=1.21.2 {
    @Override
    protected double rideHeight(
            EntityDimensions dimensions
    ) {
        return dimensions.height();
    }
    //?}

    @Override
    protected int getMaxPassengers() {
        return MAX_PASSENGERS;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(
            Entity passenger,
            EntityDimensions dimensions,
            float scaleFactor
    ) {
        int passengerIndex =
                this.getPassengers().indexOf(passenger);

        double seatZ = switch (passengerIndex) {
            case 0 -> DRIVER_SEAT_Z;
            case 1 -> PASSENGER_ONE_SEAT_Z;
            case 2 -> PASSENGER_TWO_SEAT_Z;
            default -> DRIVER_SEAT_Z;
        };

        return new Vec3(
                0.0D,
                //? if <1.21.2 {
                /*dimensions.height(),
                *///?} else {
                this.rideHeight(dimensions),
                 //?}
                seatZ * scaleFactor
        ).yRot(
                -this.getYRot() * Mth.DEG_TO_RAD
        );
    }

    @Override
    protected void positionRider(
            Entity passenger,
            Entity.MoveFunction moveFunction
    ) {
        super.positionRider(
                passenger,
                moveFunction
        );

        if (passenger instanceof Animal animal) {
            float sidewaysRotation =
                    (passenger.getId() & 1) == 0
                            ? 90.0F
                            : -90.0F;

            animal.setYBodyRot(
                    Mth.wrapDegrees(
                            this.getYRot()
                                    + sidewaysRotation
                    )
            );

            animal.setYHeadRot(
                    Mth.wrapDegrees(
                            passenger.getYRot()
                                    + sidewaysRotation
                    )
            );
        }
    }

    //? if <1.21.2 {
    /*@Override
    public Item getDropItem() {
        return ModItems.MUSAVACCA_BOAT.get();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(
                ModItems.MUSAVACCA_BOAT.get()
        );
    }
    *///?}
}