
package space.anatomyuniverse.musavacca;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import space.anatomyuniverse.musavacca.block.ModBlocks;
import space.anatomyuniverse.musavacca.block.custom.PearlFireBlock;
import space.anatomyuniverse.musavacca.block.entity.ModBlockEntities;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.data.ModDataGenerators;
import space.anatomyuniverse.musavacca.entity.ModEntities;
import space.anatomyuniverse.musavacca.entity.ModEntityRenderers;
import space.anatomyuniverse.musavacca.item.ModCreativeTabs;
import space.anatomyuniverse.musavacca.item.ModItems;
import space.anatomyuniverse.musavacca.menu.ModMenus;
import space.anatomyuniverse.musavacca.particle.ModParticleProviders;
import space.anatomyuniverse.musavacca.particle.ModParticleTypes;
import space.anatomyuniverse.musavacca.render.MusaRenderLayers;
import space.anatomyuniverse.musavacca.screen.ModMenuScreens;
import space.anatomyuniverse.musavacca.tint.ModTints;

//? if <1.21.9 {
import net.neoforged.fml.loading.FMLLoader;
//?} else {
/*import net.neoforged.fml.loading.FMLEnvironment;
 *///?}

@Mod(MusaCore.MOD_ID)
public final class MusaCore {
    public static final String MOD_ID = "musavacca";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String VERSION = /*$ mod_version*/ "0.0.1";
    public static final String MINECRAFT = /*$ minecraft*/ "1.21.8";

    public MusaCore(IEventBus modBus, ModContainer container) {
        ModItems.register(modBus);
        ModBlocks.register(modBus);
        ModCreativeTabs.register(modBus);
        ModDataComponents.register(modBus);
        ModBlockEntities.register(modBus);
        ModMenus.register(modBus);
        ModParticleTypes.register(modBus);
        ModEntities.register(modBus);

        modBus.addListener(this::commonSetup);
        modBus.addListener(ModDataGenerators::gatherData);

        //? if <1.21.9 {
        if (FMLLoader.getDist() == Dist.CLIENT) {
            ModTints.register(modBus);
            modBus.addListener(MusaRenderLayers::onModifyBakingResult);
            modBus.addListener(ModMenuScreens::register);
            modBus.addListener(ModEntityRenderers::registerRenderers);
            modBus.addListener(ModEntityRenderers::registerLayerDefinitions);
            ModParticleProviders.register(modBus);
        }
        //?} else {
        /*if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ModTints.register(modBus);
            modBus.addListener(MusaRenderLayers::onModifyBakingResult);
            modBus.addListener(ModMenuScreens::register);
            modBus.addListener(ModEntityRenderers::registerRenderers);
            modBus.addListener(ModEntityRenderers::registerLayerDefinitions);
            ModParticleProviders.register(modBus);
        }
        *///?}
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(PearlFireBlock::bootStrap);
    }
}