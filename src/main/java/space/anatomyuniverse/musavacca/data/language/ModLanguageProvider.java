package space.anatomyuniverse.musavacca.data.language;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import space.anatomyuniverse.musavacca.MusaCore;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


public final class ModLanguageProvider extends LanguageProvider {

    private static final Map<String, String> OVERRIDES = new LinkedHashMap<>();

    static {
        // Creative tab display name
        OVERRIDES.put("itemGroup." + MusaCore.MOD_ID + ".musavacca_tab", "Musavacca");

        // Examples (uncomment if needed):
        // OVERRIDES.put("item.anynology.purpish_anytomithium_ingot", "Purplish Anytomithium Ingot");
        // OVERRIDES.put("block.anynology.some_block", "Some Block");
    }

    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, MusaCore.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        final String modid = MusaCore.MOD_ID;
        final java.util.Set<String> generatedKeys = new java.util.HashSet<>();

        for (Block b : BuiltInRegistries.BLOCK) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
            if (id == null || !modid.equals(id.getNamespace())) continue;

            String key = b.getDescriptionId();
            if (OVERRIDES.containsKey(key) || !generatedKeys.add(key)) continue;

            add(key, humanize(id.getPath()));
        }

        for (Item it : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(it);
            if (id == null || !modid.equals(id.getNamespace())) continue;

            String key = it.getDescriptionId();
            if (OVERRIDES.containsKey(key) || !generatedKeys.add(key)) continue;

            add(key, humanize(id.getPath()));
        }

        String tabKey = "itemGroup." + modid + ".musavacca_tab";
        if (!OVERRIDES.containsKey(tabKey)) {
            add(tabKey, "Musavacca");
        }

        OVERRIDES.forEach(this::add);
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
