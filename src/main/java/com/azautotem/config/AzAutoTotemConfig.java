package com.azautotem.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class AzAutoTotemConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("azautotem_plus.json");

    public boolean enabled = true;
    public boolean autoTotem = true;
    public boolean autoShieldBackup = true;
    public float totemHealthThreshold = 20.0f; 
    public int swapSpeed = 2; // Nuevo: Control de velocidad (1 = Max velocidad)
    public boolean stealthMode = false;
    public int maxJitterTicks = 1;
    public boolean autoRevert = true;
    public boolean switchOnBlock = true;
    public int revertDelayTicks = 3;
    public List<String> blacklistedBlocks = List.of("minecraft:bedrock");

    public static AzAutoTotemConfig load() {
        File f = PATH.toFile();
        if (!f.exists()) { save(new AzAutoTotemConfig()); return new AzAutoTotemConfig(); }
        try (FileReader r = new FileReader(f)) { return GSON.fromJson(r, AzAutoTotemConfig.class); } catch (Exception e) { return new AzAutoTotemConfig(); }
    }

    public static void save(AzAutoTotemConfig c) {
        try (FileWriter w = new FileWriter(PATH.toFile())) { GSON.toJson(c, w); } catch (IOException e) {}
    }
}
