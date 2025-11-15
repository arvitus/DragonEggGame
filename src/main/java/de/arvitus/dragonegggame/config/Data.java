package de.arvitus.dragonegggame.config;

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
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.*;
import java.util.UUID;

import static de.arvitus.dragonegggame.DragonEggGame.*;

public class Data {
    public transient @Nullable World world;
    @SerializedName("world")
    public @NotNull String worldId = "minecraft:overworld";
    @SerializedName("entity_uuid")
    public @Nullable UUID entityUUID;
    @SerializedName("player_uuid")
    public @Nullable UUID playerUUID;
    public @NotNull DragonEggAPI.PositionType type = PositionType.NONE;
    @SerializedName("type")
    public @Nullable DragonEggAPI.PositionType type;
    @SerializedName("position")
    private @Nullable Vector3f _position;
    @SerializedName("randomized_position")
    private @Nullable Vector3i _randomizedPosition;

    public static Data load() {
        Data data = new Data();
        File dataFile = CONFIG_DIR.resolve("data.json").toFile();
        try (Reader reader = new FileReader(dataFile)) {
            data = new Gson().fromJson(reader, Data.class);
        } catch (FileNotFoundException ignored) {
            LOGGER.debug("data.json not found, using default values");
        } catch (JsonIOException | IOException e) {
            LOGGER.warn("could not load saved data, using default values");
        } catch (JsonSyntaxException e) {
            LOGGER.warn("saved data is invalid: {}, using default values", e.getMessage());
        }
        return data;
    }

    public @NotNull BlockPos getRandomizedPosition() {
        if (this._randomizedPosition == null) {
            BlockPos randPos = Utils.randomizePosition(this.getBlockPos(), CONFIG.searchRadius);
            this._randomizedPosition = new Vector3i(randPos.getX(), randPos.getY(), randPos.getZ());
        }
        return new BlockPos(
            this._randomizedPosition.x,
            this._randomizedPosition.y,
            this._randomizedPosition.z
        );
    }

    public @NotNull BlockPos getBlockPos() {
        return BlockPos.ofFloored(getPosition());
    }

    public @NotNull Vec3d getPosition() {
        return this._position == null ? Vec3d.ZERO : new Vec3d(this._position);
    }

    public void setPosition(@NotNull Vec3d position) {
        this._position = position.toVector3f();
    }

    public void clearRandomizedPosition() {
        this._randomizedPosition = null;
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
