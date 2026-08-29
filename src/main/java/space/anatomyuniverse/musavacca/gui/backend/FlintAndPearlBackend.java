package space.anatomyuniverse.musavacca.gui.backend;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import space.anatomyuniverse.musavacca.component.ModDataComponents;
import space.anatomyuniverse.musavacca.item.custom.FlintAndPearlItem;
import space.anatomyuniverse.musavacca.tint.TintColorUtil;

public final class FlintAndPearlBackend {
    private final InteractionHand hand;

    private int hexColor;

    public FlintAndPearlBackend(InteractionHand hand, int hexColor) {
        this.hand = hand;
        this.hexColor = normalizeHexColor(hexColor);
    }

    public InteractionHand getHand() {
        return this.hand;
    }

    public int getHexColor() {
        return this.hexColor;
    }

    public void setHexColor(int hexColor) {
        this.hexColor = normalizeHexColor(hexColor);
    }

    public boolean applyHexColor(Player player, int hexColor) {
        ItemStack stack = player.getItemInHand(this.hand);

        if (!(stack.getItem() instanceof FlintAndPearlItem)) {
            return false;
        }

        int color = normalizeHexColor(hexColor);

        stack.set(ModDataComponents.HEX_COLOR.get(), color);
        this.hexColor = color;

        return true;
    }

    public static int getDefaultHexColor() {
        return normalizeHexColor(FlintAndPearlItem.DEFAULT_HEX_COLOR);
    }

    private static int normalizeHexColor(int hexColor) {
        return TintColorUtil.rgb(hexColor);
    }
}
