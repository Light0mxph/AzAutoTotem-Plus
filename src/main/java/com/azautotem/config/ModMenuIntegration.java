package com.azautotem.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            AzAutoTotemConfig cfg = AzAutoTotemConfig.load();
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Aztrix Prime Studio - AzAutoTotem+").formatted(Formatting.AQUA, Formatting.BOLD))
                .setSavingRunnable(() -> AzAutoTotemConfig.save(cfg));

            ConfigEntryBuilder eb = builder.entryBuilder();

            // --- ⚙️ GENERAL ---
            ConfigCategory general = builder.getOrCreateCategory(Text.literal("⚙️ General"));
            general.addEntry(eb.startBooleanToggle(Text.literal("Mod Activado (Master Switch)"), cfg.enabled)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Apaga o enciende TODO el mod (Auto-Totem, Tools, etc)."))
                .setSaveConsumer(v -> cfg.enabled = v).build());

            // --- 🛡️ SUPERVIVENCIA ---
            ConfigCategory combat = builder.getOrCreateCategory(Text.literal("🛡️ Supervivencia"));
            combat.addEntry(eb.startBooleanToggle(Text.literal("Auto-Tótem"), cfg.autoTotem)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Si se desactiva, el mod no te pondrá tótems en la mano."))
                .setSaveConsumer(v -> cfg.autoTotem = v).build());

            combat.addEntry(eb.startBooleanToggle(Text.literal("Auto-Escudo Backup"), cfg.autoShieldBackup)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Equipa un escudo automáticamente si no hay tótems."))
                .setSaveConsumer(v -> cfg.autoShieldBackup = v).build());

            combat.addEntry(eb.startFloatField(Text.literal("Umbral de Vida"), cfg.totemHealthThreshold)
                .setDefaultValue(18.0f).setMin(0.0f).setMax(20.0f)
                .setTooltip(Text.literal("Punto de salud donde el mod actúa (20 = Siempre activo)."))
                .setSaveConsumer(v -> cfg.totemHealthThreshold = v).build());

            // --- ⛏️ HERRAMIENTAS ---
            ConfigCategory tools = builder.getOrCreateCategory(Text.literal("⛏️ Herramientas"));
            tools.addEntry(eb.startBooleanToggle(Text.literal("Auto-Tool (Bloques)"), cfg.switchOnBlock)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Cambia al pico/hacha óptimo al mirar un bloque."))
                .setSaveConsumer(v -> cfg.switchOnBlock = v).build());
                
            tools.addEntry(eb.startBooleanToggle(Text.literal("Auto-Regresar Slot"), cfg.autoRevert)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Vuelve al slot original después de picar un bloque."))
                .setSaveConsumer(v -> cfg.autoRevert = v).build());
                
            tools.addEntry(eb.startIntSlider(Text.literal("Retraso de Regreso (Ticks)"), cfg.revertDelayTicks, 1, 40)
                .setDefaultValue(5)
                .setTooltip(Text.literal("Tiempo de espera antes de devolver la herramienta (20 ticks = 1s)."))
                .setSaveConsumer(v -> cfg.revertDelayTicks = v).build());

            // --- 👻 GHOST PROTOCOL ---
            ConfigCategory ghost = builder.getOrCreateCategory(Text.literal("👻 Ghost Protocol"));
            ghost.addEntry(eb.startBooleanToggle(Text.literal("Modo Sigilo"), cfg.stealthMode)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Añade latencia aleatoria para evadir detección Anti-Cheat."))
                .setSaveConsumer(v -> cfg.stealthMode = v).build());
                
            ghost.addEntry(eb.startIntSlider(Text.literal("Máximo Jitter (Ticks)"), cfg.maxJitterTicks, 0, 10)
                .setDefaultValue(2)
                .setTooltip(Text.literal("Nivel de aleatoriedad en el cambio de slots."))
                .setSaveConsumer(v -> cfg.maxJitterTicks = v).build());

            return builder.build();
        };
    }
}
