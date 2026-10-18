package space.anatomyuniverse.musavacca.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

//? if >=26.1 {
/*import net.minecraft.world.level.storage.SavedDataStorage;
import org.spongepowered.asm.mixin.gen.Accessor;
*///?}

@Pseudo
@Mixin(
        //? if >=26.1 {
        /*value = SavedDataStorage.class,
        *///?} else {
        targets = "space.anatomyuniverse.musavacca.mixin.DisabledSavedDataStorageTarget",
        //?}
        remap = false
)
public interface SavedDataStorageAccessor {
    //? if >=26.1 {
    /*@Accessor("dataFolder")
    java.nio.file.Path musavacca$dataFolder();
    *///?}
}
