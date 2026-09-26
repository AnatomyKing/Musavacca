package space.anatomyuniverse.musavacca.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class PlayerStatusEffect extends MobEffect {

    public PlayerStatusEffect() {
        super(
                MobEffectCategory.NEUTRAL,
                0x6FA8FF
        );
    }
}
