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

        // 1) Blocks -> block.anynology.<id> = Human Name (skip if overridden)
        for (Block b : BuiltInRegistries.BLOCK) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
            if (id == null || !modid.equals(id.getNamespace())) continue;

            String path = id.getPath();
            String key = "block." + modid + "." + path;

            if (OVERRIDES.containsKey(key)) continue; // avoid duplicates; overrides win
            add(key, humanize(path));
        }

        // 2) Items (non-BlockItems) -> item.anynology.<id> = Human Name (skip if overridden)
        for (Item it : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(it);
            if (id == null || !modid.equals(id.getNamespace())) continue;
            if (it instanceof BlockItem) continue;

            String path = id.getPath();
            String key = "item." + modid + "." + path;

            if (OVERRIDES.containsKey(key)) continue; // avoid duplicates; overrides win
            add(key, humanize(path));
        }

        // 3) Creative tab name: only add a default if NOT overridden
        String tabKey = "itemGroup." + modid + ".musavacca_tab";
        if (!OVERRIDES.containsKey(tabKey)) {
            add(tabKey, "Musavacca");
        }

        // 4) Apply overrides last (safe: we skipped any auto-generated keys that collide)
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
