package space.anatomyuniverse.musavacca.mixin;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class OptionalCompatMixinPlugin implements IMixinConfigPlugin {
    private static final String COMPAT = ".compat.";

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        int start = mixinClassName.indexOf(COMPAT);

        if (start < 0) {
            return true;
        }

        start += COMPAT.length();

        int end = mixinClassName.indexOf('.', start);

        return end < 0
                || LoadingModList.get().getModFileById(mixinClassName.substring(start, end)) != null;
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo
    ) {}

    @Override
    public void postApply(
            String targetClassName,
            ClassNode targetClass,
            String mixinClassName,
            IMixinInfo mixinInfo
    ) {}
}