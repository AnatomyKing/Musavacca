package space.anatomyuniverse.musavacca.mixin.client;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import org.spongepowered.asm.mixin.Mixin;

//? if >=1.21.2 {
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
//?}

@Mixin(ClientBundleTooltip.class)
public interface ClientBundleTooltipAccessor {

    //? if >=1.21.2 {
    @Accessor("BUNDLE_EMPTY_DESCRIPTION")
    static Component musavacca$getEmptyDescription() {
        throw new AssertionError();
    }

    @Mutable
    @Accessor("BUNDLE_EMPTY_DESCRIPTION")
    static void musavacca$setEmptyDescription(
            Component description
    ) {
        throw new AssertionError();
    }
    //?}
}

