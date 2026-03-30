import React, { useEffect, useMemo, useState } from 'react';
import {
    ItemModelGlProvider,
    ItemModelDisplayer
} from '@iskallia-dev/item-model-renderer';

const MISSING_TEXTURE =
    'data:image/svg+xml;charset=utf-8,' +
    encodeURIComponent(`
    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16" shape-rendering="crispEdges">
      <rect width="16" height="16" fill="#000"/>
      <rect x="0" y="0" width="8" height="8" fill="#ff00ff"/>
      <rect x="8" y="8" width="8" height="8" fill="#ff00ff"/>
    </svg>
  `);

function resolveTextureReference(ref, textures) {
    let current = ref;
    const seen = new Set();

    while (typeof current === 'string' && current.startsWith('#')) {
        if (seen.has(current)) return null;
        seen.add(current);
        current = textures[current.slice(1)];
    }

    return typeof current === 'string' ? current : null;
}

export default function ModelPreviewApp() {
    const [item, setItem] = useState(null);

    useEffect(() => {
        setItem(window.__PREVIEW_ITEM__ ?? null);
    }, []);

    useEffect(() => {
        if (!item) return;

        const timer = setInterval(() => {
            const img = document.querySelector('.preview-image');
            const card = document.getElementById('card');

            if (img && img.complete && img.naturalWidth > 0 && card) {
                card.classList.add('ready');
                clearInterval(timer);
            }
        }, 50);

        return () => clearInterval(timer);
    }, [item]);

    const resolveTextureUrl = useMemo(() => {
        return (resourceLocation) => {
            if (!item) return MISSING_TEXTURE;

            const terminal = resolveTextureReference(
                resourceLocation,
                item.model?.textures ?? {}
            );

            if (!terminal) return MISSING_TEXTURE;
            return item.textureDataUrls?.[terminal] ?? MISSING_TEXTURE;
        };
    }, [item]);

    async function resolveMcmeta() {
        return null;
    }

    if (!item) {
        return (
            <div id="card" className="ready">
                <div className="empty">No preview item provided.</div>
            </div>
        );
    }

    return (
        <ItemModelGlProvider
            resolveTextureUrl={resolveTextureUrl}
            resolveMcmeta={resolveMcmeta}
            itemModelTransform="gui"
            zoomFactor={0.9}
            individualRenderWait={35}
        >
            <div id="card">
                <div className="meta">
                    <h1>{item.modelId}</h1>
                    <p>{item.sourcePath}</p>
                </div>

                <div className="viewer">
                    <ItemModelDisplayer
                        itemId={item.modelId}
                        itemModel={item.model}
                        renderedSize={520}
                        className="preview-image"
                    />
                </div>
            </div>
        </ItemModelGlProvider>
    );
}