import { promises as fs } from 'node:fs';
import path from 'node:path';
import { execFileSync } from 'node:child_process';
import { chromium } from 'playwright';

const astroRoot = process.cwd();
const repoRoot = path.resolve(astroRoot, '..');

const PREVIEW_URL =
    process.env.PREVIEW_URL || 'http://127.0.0.1:4321/model-preview';
const DISCORD_WEBHOOK = process.env.DISCORD_WEBHOOK || '';
const MAX_PREVIEWS = 10;

const MODEL_PATH_REGEX =
    /^src\/main\/resources\/assets\/[^/]+\/models\/item\/.+\.json$/;

const MANUAL_MODEL_PATHS = (process.env.MODEL_PATHS || '')
    .split('|')
    .map((value) => value.trim())
    .filter(Boolean);

function toPosix(filePath) {
    return filePath.replace(/\\/g, '/');
}

async function getGitHubDiffRange() {
    const eventPath = process.env.GITHUB_EVENT_PATH;
    if (!eventPath) return null;

    try {
        const payload = JSON.parse(await fs.readFile(eventPath, 'utf8'));
        const before = payload?.before;
        const after = payload?.after;

        if (
            typeof before === 'string' &&
            typeof after === 'string' &&
            before &&
            after &&
            !/^0+$/.test(before)
        ) {
            return [before, after];
        }
    } catch {
        // fall through to local fallback
    }

    return null;
}

async function getChangedModelPaths() {
    if (MANUAL_MODEL_PATHS.length > 0) {
        return [...new Set(
            MANUAL_MODEL_PATHS
                .map(toPosix)
                .filter((filePath) => MODEL_PATH_REGEX.test(filePath))
        )].slice(0, MAX_PREVIEWS);
    }

    const range = await getGitHubDiffRange();
    const gitArgs = range
        ? ['diff', '--name-only', '--diff-filter=AM', range[0], range[1]]
        : ['diff', '--name-only', '--diff-filter=AM', 'HEAD~1', 'HEAD'];

    try {
        const stdout = execFileSync('git', gitArgs, {
            cwd: repoRoot,
            encoding: 'utf8'
        });

        return [...new Set(
            stdout
                .split(/\r?\n/)
                .map((line) => toPosix(line.trim()))
                .filter((line) => MODEL_PATH_REGEX.test(line))
        )].slice(0, MAX_PREVIEWS);
    } catch (error) {
        console.warn('Failed to detect changed model paths from git diff.');
        console.warn(error?.message || error);
        return [];
    }
}

function modelIdFromPath(modelPath) {
    const match = modelPath.match(
        /^src\/main\/resources\/assets\/([^/]+)\/models\/item\/(.+)\.json$/
    );

    if (!match) {
        return modelPath;
    }

    return `${match[1]}:${match[2]}`;
}

function splitResourceLocation(resourceLocation) {
    const parts = resourceLocation.split(':', 2);

    if (parts.length === 2) {
        return {
            namespace: parts[0] || 'minecraft',
            path: parts[1] || ''
        };
    }

    return {
        namespace: 'minecraft',
        path: resourceLocation
    };
}

function textureFilePathFromResourceLocation(resourceLocation) {
    const { namespace, path: resourcePath } = splitResourceLocation(resourceLocation);

    let texturePath = resourcePath;
    if (texturePath.startsWith('textures/')) {
        texturePath = texturePath.slice('textures/'.length);
    }

    return path.join(
        repoRoot,
        'src',
        'main',
        'resources',
        'assets',
        namespace,
        'textures',
        `${texturePath}.png`
    );
}

function getTerminalTextureRefs(model) {
    const values = Object.values(model?.textures ?? {});

    return [...new Set(
        values.filter(
            (value) =>
                typeof value === 'string' &&
                value.trim() &&
                !value.startsWith('#')
        )
    )];
}

async function fileToDataUrl(filePath) {
    const buffer = await fs.readFile(filePath);
    return `data:image/png;base64,${buffer.toString('base64')}`;
}

async function buildPreviewItem(modelPath) {
    const fullPath = path.join(repoRoot, ...modelPath.split('/'));
    const raw = await fs.readFile(fullPath, 'utf8');
    const model = JSON.parse(raw);

    if (!Array.isArray(model.elements) || model.elements.length === 0) {
        console.log(`Skipping ${modelPath} because it has no direct elements.`);
        return null;
    }

    const textureDataUrls = {};

    for (const ref of getTerminalTextureRefs(model)) {
        if (ref.startsWith('data:')) {
            textureDataUrls[ref] = ref;
            continue;
        }

        try {
            textureDataUrls[ref] = await fileToDataUrl(
                textureFilePathFromResourceLocation(ref)
            );
        } catch {
            console.warn(`Missing texture for ${modelPath}: ${ref}`);
        }
    }

    return {
        modelId: modelIdFromPath(modelPath),
        sourcePath: modelPath,
        model,
        textureDataUrls
    };
}

async function waitForServer(url, timeoutMs = 30000) {
    const started = Date.now();

    while (Date.now() - started < timeoutMs) {
        try {
            const response = await fetch(url);
            if (response.ok) return;
        } catch {
            // wait and retry
        }

        await new Promise((resolve) => setTimeout(resolve, 500));
    }

    throw new Error(`Astro dev server did not become ready in time: ${url}`);
}

function safeFileName(value) {
    return value.replace(/[^a-zA-Z0-9._-]+/g, '_');
}

async function sendToDiscord(modelId, sourcePath, fileName, buffer) {
    if (!DISCORD_WEBHOOK) {
        console.log('DISCORD_WEBHOOK is not set, skipping Discord send.');
        return;
    }

    const payload = {
        content: `Minecraft model preview: \`${modelId}\``,
        embeds: [
            {
                title: modelId,
                description: sourcePath,
                image: {
                    url: `attachment://${fileName}`
                }
            }
        ]
    };

    const form = new FormData();
    form.append('payload_json', JSON.stringify(payload));
    form.append('files[0]', new Blob([buffer], { type: 'image/png' }), fileName);

    const response = await fetch(DISCORD_WEBHOOK, {
        method: 'POST',
        body: form
    });

    if (!response.ok) {
        throw new Error(
            `Discord webhook failed: ${response.status} ${await response.text()}`
        );
    }
}

async function main() {
    const modelPaths = await getChangedModelPaths();

    if (!modelPaths.length) {
        console.log('No changed item model JSON files found.');
        console.log(
            'For local testing, set MODEL_PATHS to one or more repo-relative model paths separated by |'
        );
        return;
    }

    const artifactsDir = path.join(astroRoot, 'artifacts');
    await fs.rm(artifactsDir, { recursive: true, force: true });
    await fs.mkdir(artifactsDir, { recursive: true });

    await waitForServer(PREVIEW_URL);

    const browser = await chromium.launch({ headless: true });

    try {
        for (const modelPath of modelPaths) {
            const previewItem = await buildPreviewItem(modelPath);
            if (!previewItem) continue;

            const page = await browser.newPage({
                viewport: { width: 900, height: 900 },
                deviceScaleFactor: 2
            });

            page.on('console', (msg) => {
                console.log(`[browser:${msg.type()}] ${msg.text()}`);
            });

            page.on('pageerror', (err) => {
                console.log(`[browser:error] ${err.message}`);
            });

            await page.addInitScript((value) => {
                window.__PREVIEW_ITEM__ = value;
            }, previewItem);

            await page.goto(PREVIEW_URL, { waitUntil: 'domcontentloaded' });
            await page.waitForSelector('#card', { timeout: 120000 });
            await page.waitForTimeout(3500);

            const outputFileName = `${safeFileName(previewItem.modelId)}.png`;
            const outputPath = path.join(artifactsDir, outputFileName);

            await page.locator('#card').screenshot({
                path: outputPath,
                animations: 'disabled'
            });

            const imageBuffer = await fs.readFile(outputPath);

            await sendToDiscord(
                previewItem.modelId,
                previewItem.sourcePath,
                outputFileName,
                imageBuffer
            );

            await page.close();
            console.log(`Done: ${previewItem.modelId}`);
        }
    } finally {
        await browser.close();
    }
}

main().catch((error) => {
    console.error(error);
    process.exit(1);
});