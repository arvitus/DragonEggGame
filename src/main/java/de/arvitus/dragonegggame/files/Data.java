package de.arvitus.dragonegggame.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import de.arvitus.dragonegggame.api.DragonEggAPI;
import de.arvitus.dragonegggame.api.DragonEggAPI.PositionType;
import de.arvitus.dragonegggame.utils.Utils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.UUID;

import static de.arvitus.dragonegggame.DragonEggGame.*;

public class Data {
    public transient @Nullable World world;
    @SerializedName("world")
    public @NotNull String worldId = "minecraft:overworld";
    @SerializedName("position")
    public @NotNull Vec3d position = new Vec3d(0, 0, 0);
    @SerializedName("entity_uuid")
    public @Nullable UUID entityUUID;
    @SerializedName("player_uuid")
    public @Nullable UUID playerUUID;
    @SerializedName("type")
    public @NotNull DragonEggAPI.PositionType type = PositionType.NONE;
    @SerializedName("randomized_position")
    private BlockPos randomizedPosition;

    public static Data load() {
        File dataFile = CONFIG_DIR.resolve("data.json").toFile();
        try (Reader reader = new FileReader(dataFile)) {
            Data data = new Gson().fromJson(reader, Data.class);
            if (data != null) return data;
        } catch (FileNotFoundException ignored) {
            LOGGER.debug("data.json not found, using default values");
        } catch (JsonIOException | IOException e) {
            LOGGER.warn("could not load saved data, using default values");
        } catch (JsonSyntaxException e) {
            LOGGER.warn("saved data is invalid: {}, using default values", e.getMessage());
        }
        return new Data();
    }

    public BlockPos getRandomizedPosition() {
        if (this.randomizedPosition == null) {
            this.randomizedPosition = Utils.randomizePosition(this.getBlockPos(), CONFIG.searchRadius);
        }
        return this.randomizedPosition;
    }

    public BlockPos getBlockPos() {
        return BlockPos.ofFloored(this.position);
    }

    public void clearRandomizedPosition() {
        this.randomizedPosition = null;
    }

    public void save() {
        File dataFile = CONFIG_DIR.resolve("data.json").toFile();
        try (Writer writer = new FileWriter(dataFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(this, writer);
        } catch (JsonIOException | IOException e) {
            LOGGER.warn("could not save data: {}", e.getMessage());
        }
    }
}
