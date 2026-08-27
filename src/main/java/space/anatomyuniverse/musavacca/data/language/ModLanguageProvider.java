package space.anatomyuniverse.musavacca.data.language;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import space.anatomyuniverse.musavacca.MusaCore;
import space.anatomyuniverse.musavacca.item.ModSmithingTemplates;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ModLanguageProvider extends LanguageProvider {

    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        // Creative tab display name
        OVERRIDES.put("itemGroup." + MusaCore.MOD_ID + ".musavacca_tab", "Musavacca");

        // Examples (uncomment if needed):
        // OVERRIDES.put("item.anynology.purpish_anytomithium_ingot", "Purplish Anytomithium Ingot");
        // OVERRIDES.put("block.anynology.some_block", "Some Block");
        OVERRIDES.put(
                "block.musavacca.hex_block",
                "Lopha Flower"
        );
        OVERRIDES.put(
                "item.musavacca.banana_phone.empty.description",
                "Can hold one valid SIM card"
        );
    }

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, MusaCore.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        final String modid = MusaCore.MOD_ID;
        final Set<String> generatedKeys = new HashSet<>();

        for (Block b : BuiltInRegistries.BLOCK) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
            if (id == null || !modid.equals(id.getNamespace())) continue;

            addGenerated(generatedKeys, b.getDescriptionId(), humanize(id.getPath()));
        }

        for (Item it : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(it);
            if (id == null || !modid.equals(id.getNamespace())) continue;

            addGenerated(generatedKeys, it.getDescriptionId(), humanize(id.getPath()));
        }

        for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
            ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            if (id == null || !modid.equals(id.getNamespace())) continue;

            addGenerated(generatedKeys, effect.getDescriptionId(), humanize(id.getPath()));
        }

        addSmithingTemplateTranslations(generatedKeys);

        String tabKey = "itemGroup." + modid + ".musavacca_tab";
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

    /** "lobby_wallpaper_plinth" -> "Lobby Wallpaper Plinth" (with acronym touch-ups). */
    private static String humanize(String registryPath) {
        String[] parts = registryPath.toLowerCase(Locale.ROOT).split("[_\\-]+");
        StringBuilder out = new StringBuilder(parts.length * 6);

        for (String p : parts) {
            if (p.isEmpty()) continue;

            out.append(Character.toUpperCase(p.charAt(0)))
                    .append(p.length() > 1 ? p.substring(1) : "")
                    .append(' ');
        }

        String s = out.toString().trim();

        return s.replace("Tnt", "TNT")
                .replace("Tv", "TV")
                .replace("Gps", "GPS");
    }
}