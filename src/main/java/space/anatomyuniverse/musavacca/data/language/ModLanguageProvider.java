package space.anatomyuniverse.musavacca.data.language;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.item.ModSmithingTemplates;

public final class ModLanguageProvider extends LanguageProvider {
    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();
    static {
        OVERRIDES.put("itemGroup." + MusaCore.MOD_ID + ".musavacca_tab", "Musavacca");
        OVERRIDES.put("block.musavacca.hex_block", "Lopha Flower");
        OVERRIDES.put("item.musavacca.musavacca_pup", "Musavacca Pup");
        OVERRIDES.put("item.musavacca.flint_and_pearl", "Flint and Pearl");
        OVERRIDES.put("item.musavacca.banana_phone.empty.description", "Can hold one valid SIM card");
    }

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, MusaCore.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        final String modId = MusaCore.MOD_ID;
        final Set<String> generatedKeys = new HashSet<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            var id = BuiltInRegistries.BLOCK.getKey(block);
            if (id == null || !modId.equals(id.getNamespace())) continue;
            addGenerated(generatedKeys, block.getDescriptionId(), humanize(id.getPath()));
        }
        for (Item item : BuiltInRegistries.ITEM) {
            var id = BuiltInRegistries.ITEM.getKey(item);
            if (id == null || !modId.equals(id.getNamespace())) continue;
            addGenerated(generatedKeys, item.getDescriptionId(), humanize(id.getPath()));
        }
        for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
            var id = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            if (id == null || !modId.equals(id.getNamespace())) continue;
            addGenerated(generatedKeys, effect.getDescriptionId(), humanize(id.getPath()));
        }
        addSmithingTemplateTranslations(generatedKeys);
        String tabKey = "itemGroup." + modId + ".musavacca_tab";
        addGenerated(generatedKeys, tabKey, "Musavacca");
        OVERRIDES.forEach(this::add);
    }

    private void addSmithingTemplateTranslations(Set<String> generatedKeys) {
        for (ModSmithingTemplates.Entry entry : ModSmithingTemplates.ALL) {
            addGenerated(generatedKeys, entry.upgradeKey(), entry.upgrade());
            addGenerated(generatedKeys, entry.appliesToKey(), entry.appliesTo());
            addGenerated(generatedKeys, entry.ingredientsKey(), entry.ingredients());
            addGenerated(generatedKeys, entry.baseSlotDescriptionKey(), entry.baseSlotDescription());
            addGenerated(generatedKeys, entry.additionsSlotDescriptionKey(), entry.additionsSlotDescription());
        }
    }

    private void addGenerated(Set<String> generatedKeys, String key, String value) {
        if (key == null || key.isBlank()) return;
        if (value == null || value.isBlank()) return;
        if (OVERRIDES.containsKey(key)) return;
        if (!generatedKeys.add(key)) return;
        add(key, value);
    }

    private static String humanize(String registryPath) {
        String[] parts = registryPath.toLowerCase(Locale.ROOT).split("[_\\-]+");
        StringBuilder name = new StringBuilder(parts.length * 6);
        for (String part : parts) {
            if (part.isEmpty()) continue;
            name.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
        }
        return name.toString().trim().replace("Tnt", "TNT").replace("Tv", "TV").replace("Gps", "GPS");
    }
}
