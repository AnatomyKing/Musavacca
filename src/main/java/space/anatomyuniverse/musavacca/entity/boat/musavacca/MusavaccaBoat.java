package space.anatomyuniverse.musavacca.entity.boat.musavacca;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.vehicle.Raft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import space.anatomyuniverse.musavacca.item.ModItems;

public final class MusavaccaBoat extends Raft {

    public static final float HITBOX_WIDTH =
            22.0F / 16.0F;

    public static final float HITBOX_HEIGHT =
            14.0F / 16.0F;

    public static final int MAX_PASSENGERS = 3;

    private static final double DRIVER_SEAT_Z =
            0.0D;

    private static final double BACK_SEAT_Z =
            11.0D / 16.0D;

    private static final double FRONT_SEAT_Z =
            -11.0D / 16.0D;

    public MusavaccaBoat(
            EntityType<? extends MusavaccaBoat> entityType,
            Level level
    ) {
        super(
                entityType,
                level,
                () -> ModItems.MUSAVACCA_BOAT.get()
        );
    }

    @Override
    protected double rideHeight(
            EntityDimensions dimensions
    ) {
        return dimensions.height();
    }

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
            case 1 -> BACK_SEAT_Z;
            case 2 -> FRONT_SEAT_Z;
            default -> DRIVER_SEAT_Z;
        };

        return new Vec3(
                0.0D,
                this.rideHeight(dimensions),
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
}