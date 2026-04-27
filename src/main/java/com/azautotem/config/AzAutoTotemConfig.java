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
    private static long lastTime = 0;

    public boolean enabled = true;
    public boolean autoTotem = true;
    public boolean autoShieldBackup = true; // NUEVO: Equipar escudo si no hay tótems
    public float totemHealthThreshold = 18.0f; 
    public boolean stealthMode = true;
    public int maxJitterTicks = 2;
    public boolean searchFullInventory = true;
    public boolean autoRevert = true;
    public boolean switchOnBlock = true;
    public boolean switchOnEntity = true;
    public int revertDelayTicks = 5;
    public List<String> blacklistedBlocks = List.of("minecraft:bedrock");
    public List<String> blacklistedEntities = List.of("minecraft:villager");

    public static AzAutoTotemConfig load() {
        File f = PATH.toFile();
        if (!f.exists()) { save(new AzAutoTotemConfig()); return new AzAutoTotemConfig(); }
        try (FileReader r = new FileReader(f)) {
            lastTime = f.lastModified();
            return GSON.fromJson(r, AzAutoTotemConfig.class);
        } catch (Exception e) { return new AzAutoTotemConfig(); }
    }

    public static void save(AzAutoTotemConfig c) {
        try (FileWriter w = new FileWriter(PATH.toFile())) { GSON.toJson(c, w); } catch (IOException e) {}
    }

    public static boolean needsReload() { return PATH.toFile().lastModified() > lastTime; }
}
