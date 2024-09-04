/*
 * This file is part of BMUtils, licensed under the MPL2 License (MPL).
 * Please keep tabs on https://github.com/TechnicJelle/BMUtils for updates.
 *
 * Copyright (c) TechnicJelle <https://technicjelle.com>
 * Copyright (c) contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.technicjelle.BMUtils;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

/**
 * Utility functions for copying stuff to BlueMap
 */
public class BMCopy {
    private BMCopy() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Copies any stream to the BlueMap asset folder.<br>
     * If the resource is a script or style, it will be registered with BlueMap.<br>
     * <b>This function should be called directly inside {@link BlueMapAPI#onEnable(Consumer)}, not in a separate
     * thread.</b>
     *
     * @param blueMapAPI The BlueMapAPI instance
     * @param in         The input stream to copy from
     * @param toAsset    The asset to copy to, relative to BlueMap's asset folder (<code>bluemap/web/assets</code>)
     * @param overwrite  Whether to overwrite the asset if it already exists
     * @throws IOException If the resource could not be copied
     */
    public static void streamToWebApp(
        final @NotNull BlueMapAPI blueMapAPI,
        final @NotNull InputStream in,
        final @NotNull String toAsset,
        final boolean overwrite
    ) throws IOException {
        final Path toPath = blueMapAPI.getWebApp().getWebRoot().resolve("assets").resolve(toAsset);

        //Register script or style
        final String assetPath = "assets/" + toAsset;
        if (toAsset.endsWith(".js")) blueMapAPI.getWebApp().registerScript(assetPath);
        if (toAsset.endsWith(".css")) blueMapAPI.getWebApp().registerStyle(assetPath);

        //Copy stream
        if (Files.exists(toPath) && !overwrite) return;
        Files.createDirectories(toPath.getParent());
        try (
            final OutputStream out = Files.newOutputStream(
                toPath,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            )
        ) {
            in.transferTo(out);
        }
    }

    /**
     * Copies any stream to the BlueMap asset folder of a specific map.<br>
     * Do not use this method for copying scripts or styles, as those need to be installed in the webapp.<br>
     *
     * @param map       The map to copy the stream to
     * @param in        The input stream to copy from
     * @param toAsset   The map's asset to copy to, relative to the map's asset folder (<code>bluemap/web/maps/{map}
     *                  /assets/</code>)
     * @param overwrite Whether to overwrite the asset if it already exists
     * @throws IOException If the resource could not be copied
     */
    public static void streamToMap(
        final @NotNull BlueMapMap map,
        final @NotNull InputStream in,
        final @NotNull String toAsset,
        final boolean overwrite
    ) throws IOException {
        //Copy stream
        if (map.getAssetStorage().assetExists(toAsset) && !overwrite) return;
        try (
            final OutputStream out = map.getAssetStorage().writeAsset(toAsset)
        ) {
            in.transferTo(out);
        }
    }

    /**
     * Copies any file to the BlueMap asset folder.<br>
     * If the resource is a script or style, it will be registered with BlueMap.<br>
     * <b>This function should be called directly inside {@link BlueMapAPI#onEnable(Consumer)}, not in a separate
     * thread.</b>
     *
     * @param blueMapAPI The BlueMapAPI instance
     * @param from       The file to copy
     * @param toAsset    The asset to copy to, relative to BlueMap's asset folder (<code>bluemap/web/assets</code>)
     * @param overwrite  Whether to overwrite the asset if it already exists
     * @throws IOException If the resource could not be found or copied
     */
    public static void fileToWebApp(
        final @NotNull BlueMapAPI blueMapAPI,
        final @NotNull Path from,
        final @NotNull String toAsset,
        final boolean overwrite
    ) throws IOException {
        try (
            final InputStream in = Files.newInputStream(from)
        ) {
            streamToWebApp(blueMapAPI, in, toAsset, overwrite);
        }
    }

    /**
     * Copies any file to the BlueMap asset folder of a specific map.<br>
     * Do not use this method for copying scripts or styles, as those need to be installed in the webapp.<br>
     *
     * @param map       The map to copy the file to
     * @param from      The file to copy
     * @param toAsset   The map's asset to copy to, relative to the map's asset folder (<code>bluemap/web/maps/{map}
     *                  /assets/</code>)
     * @param overwrite Whether to overwrite the asset if it already exists
     * @throws IOException If the resource could not be found or copied
     */
    public static void fileToMap(
        final @NotNull BlueMapMap map,
        final @NotNull Path from,
        final @NotNull String toAsset,
        final boolean overwrite
    ) throws IOException {
        try (
            final InputStream in = Files.newInputStream(from)
        ) {
            streamToMap(map, in, toAsset, overwrite);
        }
    }

    /**
     * Copies a resource from the jar to the BlueMap asset folder.<br>
     * If the resource is a script or style, it will be registered with BlueMap.<br>
     * <b>This function should be called directly inside {@link BlueMapAPI#onEnable(Consumer)}, not in a separate
     * thread.</b>
     *
     * @param blueMapAPI   The BlueMapAPI instance
     * @param classLoader  The class loader to get the resource from the correct jar
     * @param fromResource The resource to copy from the jar
     * @param toAsset      The asset to copy to, relative to BlueMap's asset folder (<code>bluemap/web/assets</code>)
     * @param overwrite    Whether to overwrite the asset if it already exists
     * @throws IOException If the resource could not be found or copied
     */
    public static void jarResourceToWebApp(
        final @NotNull BlueMapAPI blueMapAPI,
        final @NotNull ClassLoader classLoader,
        final @NotNull String fromResource,
        final @NotNull String toAsset,
        final boolean overwrite
    ) throws IOException {
        try (
            final InputStream in = classLoader.getResourceAsStream(fromResource)
        ) {
            if (in == null) throw new IOException("Resource not found: " + fromResource);
            streamToWebApp(blueMapAPI, in, toAsset, overwrite);
        }
    }

    /**
     * Copies a resource from the jar to the BlueMap asset folder of a specific map.<br>
     * Do not use this method for copying scripts or styles, as those need to be installed in the webapp.<br>
     *
     * @param map          The map to copy the resource to
     * @param classLoader  The class loader to get the resource from the correct jar
     * @param fromResource The resource to copy from the jar
     * @param toAsset      The asset to copy to, relative to the map's asset folder (<code>bluemap/web/maps/{map}
     *                     /assets/</code>)
     * @param overwrite    Whether to overwrite the asset if it already exists
     * @throws IOException If the resource could not be found or copied
     */
    public static void jarResourceToMap(
        final @NotNull BlueMapMap map,
        final @NotNull ClassLoader classLoader,
        final @NotNull String fromResource,
        final @NotNull String toAsset,
        final boolean overwrite
    ) throws IOException {
        try (
            final InputStream in = classLoader.getResourceAsStream(fromResource)
        ) {
            if (in == null) throw new IOException("Resource not found: " + fromResource);
            streamToMap(map, in, toAsset, overwrite);
        }
    }
}
