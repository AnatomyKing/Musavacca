# Musavacca
This is the official repository of **Musavacca**,
and it's under this [`LICENSE`](LICENSE.txt).

Musavacca is a **NeoForge** mod that adds banana themed economy and teleportation in to Minecraft.

> **Template:** I build this mod using **[Stonecutter](https://stonecutter.kikugie.dev/)**.
> Stonecutter is a great way to manage multi-version projects and I can't recommend it enough! Go check it out if multi-version support is something you're interested in.

## Supported Minecraft versions
These are the versions I support for Musavacca, they are included in my Stonecutter version projects,
- `1.21.1`
- `1.21.3`
- `1.21.4`
- `1.21.5`
- `1.21.8`
- `1.21.10`
- `1.21.11`

## Usage
- Use `runClientActive` to run the client for the **active** version project.
- Use `runServerActive` to run the server for the **active** version project.
- Use `runDatagenActive` to run datagen for the **active** version project.
- Use `runDatagenAll` to run datagen for **all** version projects in one go.

## Datagen and Wrappers
This project is set up to keep datagen consistent across all supported Minecraft version projects. It also includes specific wrappers to avoid repeating boilerplate and to hide most version differences.

[`ModDataGenerators`](src/main/java/space/anatomyuniverse/musavacca/data/ModDataGenerators.java) is the main datagen entry point and handles models, recipes, tags, language, loot, worldgen and other generated resources.

- **Blocks**
  - [`ModBlocks`](src/main/java/space/anatomyuniverse/musavacca/block/ModBlocks.java) registers all mod blocks and can auto-define item registries for them with an optional blacklist.
  - [`ModBlockEntities`](src/main/java/space/anatomyuniverse/musavacca/block/entity/ModBlockEntities.java) registers all block entities.

- **Renderers**
  - [`MusaRenderLayers`](src/main/java/space/anatomyuniverse/musavacca/render/MusaRenderLayers.java) is where cutout, translucent, and AO properties are assigned for mod blocks.
  - It keeps these properties in one place across Minecraft's changing model pipelines.

- **Tab**
  - [`ModCreativeTabs`](src/main/java/space/anatomyuniverse/musavacca/item/ModCreativeTabs.java) auto-fills the creative tab with all items from our namespace.
  - It's sorted alphabetically with an optional blacklist.

- **Language**
  - [`ModLanguageProvider`](src/main/java/space/anatomyuniverse/musavacca/data/language/ModLanguageProvider.java) auto-generates `en_us` names for blocks, items, effects and smithing template text.
  - Supports manual overrides and avoids duplicate keys.

- **Models / Blockstates**
  - [`NewModelSets`](src/main/java/space/anatomyuniverse/musavacca/data/models/NewModelSets.java) is where models and item models are defined.
  - [`Newgen`](src/main/java/space/anatomyuniverse/musavacca/data/models/newgen/Newgen.java) generates the correct output for the current Minecraft version.
  - Supports generated or existing models, blockstate conditions, multipart models, rotations, variants, texture helpers, folders and tints.
  - Includes helpers for simple blocks, plants, doors, trapdoors, stairs, slabs, fences, gates, buttons, pressure plates, portals, fire, decorations, layered items, spawn eggs and armor.
  - Tinting can be declared directly with the model, including foliage, constants, hex colors, Pearl Fire profiles, per layer item tints and multi tints.

- **Recipes**
  - [`RecipeDSL`](src/main/java/space/anatomyuniverse/musavacca/data/recipes/RecipeDSL.java) makes shaped, shapeless, cooking, stonecutting and smithing recipes readable and version-safe.
  - Supports per-ingredient amounts, count conversions, reversible shapeless families and interchangeable stonecutting families.
  - [`ComponentRecipeDSL`](src/main/java/space/anatomyuniverse/musavacca/data/recipes/ComponentRecipeDSL.java) can require or transfer data components, including a specific path inside a component.
  - [`CompatRecipeDSL`](src/main/java/space/anatomyuniverse/musavacca/data/recipes/CompatRecipeDSL.java) handles optional mod recipes and adds the mod-loaded condition automatically.
  - [`ModRecipeProvider`](src/main/java/space/anatomyuniverse/musavacca/data/recipes/ModRecipeProvider.java) is where all recipes are defined.

- **Tags**
  - [`ModBlockTagsProvider`](src/main/java/space/anatomyuniverse/musavacca/data/tags/ModBlockTagsProvider.java) handles block tags.
  - [`ModItemTagsProvider`](src/main/java/space/anatomyuniverse/musavacca/data/tags/ModItemTagsProvider.java) handles item tags.

- **Loot / Worldgen**
  - Datagen covers block loot, mob loot, sniffer digging loot, jungle temple loot, global loot modifiers and the Musavacca tree feature.

- **Compatibility**
  - Optional mod compatibility is configured per version inside `versions/<mc>/gradle.properties`.
  - `compat.mods` lists the compatibility mods for that version, while `runtime_mavens` can add extra development/runtime dependencies when needed.
  - The same properties can also generate optional `neoforge.mods.toml` entries and store Modrinth / CurseForge publishing IDs.

### Shared resources snapshot
IDEs, especially IntelliJ, don’t love multiple modules sharing the same physical content root resource folder,
so we sync shared resources into a build folder to keep each version project clean and consistent. The only downside of this is that you will need quite some Gigabits.
- `syncSharedResources` in [`build.gradle.kts`](build.gradle.kts) creates a stable snapshot under `build/…/stonecutterSharedResources`.
- Every version project uses that snapshot for:
  - `processResources` packaging
  - datagen `--existing` inputs
- Generated resources stay inside `versions/<mc>/src/generated/resources`.

### Links
- [Website Wiki for the Mod COMING SOON](https://anatomy-universe.com)
