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

## Usage
- Use `runDatagenActive` to run datagen for the **active** version project.
- Use `runDatagenAll` to run datagen for **all** version projects in one go.

## Datagen and Warpers
This project is set up to keep datagen consistent across all supported Minecraft version projects. It also includes specific warpers to help avoid boilerplate.

- **Blocks**
    - [`ModBlocks`](src/main/java/space/anatomyuniverse/musavacca/block/ModBlocks.java) registers all mod blocks and can auto-define item registries for them with an optional blacklist.
    - [`ModBlockEntities`](src/main/java/space/anatomyuniverse/musavacca/block/ModBlocks.java) registers all block entities.

- **Renderers**
    - [`MusaRenderLayers`](src/main/java/space/anatomyuniverse/musavacca/client/render/MusaRenderLayers.java) is where cutout, translucent, and AO properties are assigned for mod blocks.
    - It uses bake-time render-layer wrappers so it can force everything from one place and keep behavior consistent across NeoForge and Minecraft pipeline changes.

- **Tab**
    - [`ModCreativeTabs`](src/main/java/space/anatomyuniverse/musavacca/item/ModCreativeTabs.java) auto-fills and registers the creative tab with all items from our namespace.
    - It's sorted alphabetically with an optional blacklist.

- **Language**
    - [`ModLanguageProvider`](src/main/java/space/anatomyuniverse/musavacca/data/language/ModLanguageProvider.java) is an `en_us` auto-generator that generates `block.*` and `item.*` names for everything in the mod namespace.
    - Supports overrides and avoids duplicate keys.

- **Models / Blockstates**
    - [`ModelSets`](src/main/java/space/anatomyuniverse/musavacca/data/models/ModelSets.java) defines models / blockstates and generates them with helper utilities.
    - Block model helpers live in [`data/models/block/helpers`](src/main/java/space/anatomyuniverse/musavacca/data/models/block).
    - Item model helpers live in [`data/models/item/helpers`](src/main/java/space/anatomyuniverse/musavacca/data/models/item).

- **Recipes**
    - [`RecipeDSL`](src/main/java/space/anatomyuniverse/musavacca/data/recipes/RecipeDSL.java) makes shaped, shapeless, cooking, stonecutting, and smithing generation readable and version-safe.
    - [`ModRecipeProvider`](src/main/java/space/anatomyuniverse/musavacca/data/recipes/ModRecipeProvider.java) is where all the recipes are defined.

- **Tags**
    - [`ModBlockTagsProvider`](src/main/java/space/anatomyuniverse/musavacca/data/tags/ModBlockTagsProvider.java) is where all tags are assigned for mod blocks.

- **Loot**
    - [`ModBlockLootProvider`](src/main/java/space/anatomyuniverse/musavacca/data/loot/ModBlockLootProvider.java) is where all loot tables are defined.

### Shared resources snapshot
IDEs, especially IntelliJ, don’t love multiple modules sharing the same physical content root resource folder,
So we sync shared resources into a build folder to keep each version project clean and consistent.
- `syncSharedResources`in [`build.gradle.kts`](build.gradle.kts) creates a stable snapshot under `build/…/stonecutterSharedResources`.
- Every version project uses that snapshot for:
    - `processResources` packaging
    - datagen `--existing` inputs

###  Links
- [Website Wiki for the Mod COMING SOON](https://anatomy-universe.com)

