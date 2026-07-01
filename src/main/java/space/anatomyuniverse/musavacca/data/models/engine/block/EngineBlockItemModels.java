package space.anatomyuniverse.musavacca.data.models.engine.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import space.anatomyuniverse.musavacca.data.models.engine.core.EngineContext;
import space.anatomyuniverse.musavacca.data.models.unified.ItemTint;

import java.util.List;

//? if <1.21.4 {
/*import net.neoforged.neoforge.client.model.generators.ModelFile;
*///?} else {
import net.minecraft.client.renderer.item.BlockModelWrapper;
//?}

public final class EngineBlockItemModels {
    private EngineBlockItemModels() {}

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void generate(EngineContext ctx, Block block, ResourceLocation itemModel, ItemTint itemTint) {
        if (ctx == null || block == null || itemModel == null) return;
        if (EngineBlockItems.shouldSkip(block)) return;
        //? if <1.21.4 {
        /*ModelFile model = ctx.blocks().models().getExistingFile(itemModel);
        ctx.blocks().simpleBlockItem(block, model);
        *///?} else {
        if (itemTint != null) ctx.items().itemModelOutput.accept(block.asItem(), new BlockModelWrapper.Unbaked(itemModel, (List) itemTint.sources()));
        else ctx.blocks().registerSimpleItemModel(block, itemModel);
        //?}
    }
}
