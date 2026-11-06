package space.anatomyuniverse.musavacca.client;

//? if <1.21.2 {
/*import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import org.jetbrains.annotations.Nullable;
import space.anatomyuniverse.musavacca.item.custom.PearlIgniter;
*///?}

public final class PearlIgniterModels {
    private PearlIgniterModels() {}
    //? if <1.21.2 {
    /*private static final ModelResourceLocation MODEL = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath("musavacca", "item/flint_and_pearl"));

    public static void registerAdditional(ModelEvent.RegisterAdditional event) {
        event.register(MODEL);
    }

    public static void modifyModels(ModelEvent.ModifyBakingResult event) {
        var key = new ModelResourceLocation(ResourceLocation.withDefaultNamespace("flint_and_steel"), "inventory");
        BakedModel vanilla = event.getModels().get(key);
        BakedModel pearl = event.getModels().get(MODEL);
        if (vanilla == null || pearl == null) return;
        event.getModels().put(key, new BakedModelWrapper<BakedModel>(vanilla) {
            private final ItemOverrides overrides = new ItemOverrides() {
                @Override
                public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
                    BakedModel selected = PearlIgniter.is(stack) ? pearl : vanilla;
                    BakedModel resolved = selected.getOverrides().resolve(selected, stack, level, entity, seed);
                    return resolved == null ? selected : resolved;
                }
            };

            @Override
            public ItemOverrides getOverrides() { return overrides; }
        });
    }
    *///?}
}
