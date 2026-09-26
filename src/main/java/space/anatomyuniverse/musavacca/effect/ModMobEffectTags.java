package space.anatomyuniverse.musavacca.effect;

import net.minecraft.core.registries.Registries;
//? if <1.21.11 {
import net.minecraft.resources.ResourceLocation;
//?} else {
/*import net.minecraft.resources.Identifier;
*///?}
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import space.anatomyuniverse.musavacca.MusaCore;

public final class ModMobEffectTags {
    public static final TagKey<MobEffect> BYPASSES_COW_BLESSING = create("bypasses_cow_blessing");

    private ModMobEffectTags() {}

    private static TagKey<MobEffect> create(String name) {
        return TagKey.create(Registries.MOB_EFFECT,
                /*? if <1.21.11 {*/ ResourceLocation
                /*?} else {*//*Identifier*//*?}*/.fromNamespaceAndPath(MusaCore.MOD_ID, name));
    }
}
