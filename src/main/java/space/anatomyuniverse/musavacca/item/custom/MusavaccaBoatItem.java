package space.anatomyuniverse.musavacca.item.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.item.BoatItem;

public final class MusavaccaBoatItem extends BoatItem {

    public MusavaccaBoatItem(
            EntityType<? extends AbstractBoat> entityType,
            Properties properties
    ) {
        super(entityType, properties);
    }
}
