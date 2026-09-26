package space.anatomyuniverse.musavacca.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.effect.MobEffect;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.effect.ModMobEffectTags;
import space.anatomyuniverse.musavacca.effect.ModMobEffects;

import java.util.concurrent.CompletableFuture;

//? if <1.21.4 {
/*import net.neoforged.neoforge.common.data.ExistingFileHelper;
*///?}

public final class ModMobEffectTagsProvider extends TagsProvider<MobEffect> {
    //? if <1.21.4 {
    /*public ModMobEffectTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup,
                                    ExistingFileHelper existingFileHelper) {
        super(output, Registries.MOB_EFFECT, lookup, MusaCore.MOD_ID, existingFileHelper);
    }
    *///?} else {
    public ModMobEffectTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, Registries.MOB_EFFECT, lookup, MusaCore.MOD_ID);
    }
    //?}

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        getOrCreateRawBuilder(ModMobEffectTags.BYPASSES_COW_BLESSING)
                .addElement(ModMobEffects.PLAYER_STATUS.getId());
    }
}
