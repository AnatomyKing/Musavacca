package space.anatomyuniverse.musavacca.effect;

import net.minecraft.core.registries.Registries;
//? if >=1.21.11 {
/*import net.minecraft.resources.Identifier;
*///?} else {
import net.minecraft.resources.ResourceLocation;
//?}
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModMobEffectTags {
    public static final TagKey<MobEffect> BYPASSES_COW_BLESSING = create("bypasses_cow_blessing");

    private ModMobEffectTags() {}

    private static TagKey<MobEffect> create(String name) {
        //? if >=1.21.11 {
        /*return TagKey.create(Registries.MOB_EFFECT, Identifier.fromNamespaceAndPath(MusaCore.MOD_ID, name));
        *///?} else {
        return TagKey.create(Registries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(MusaCore.MOD_ID, name));
        //?}
    }
}
