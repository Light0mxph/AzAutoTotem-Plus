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
                .setTitle(Text.literal("AzAutoTotem+ Control Center").formatted(Formatting.GOLD, Formatting.BOLD))
                .setSavingRunnable(() -> AzAutoTotemConfig.save(cfg));

            ConfigEntryBuilder eb = builder.entryBuilder();

            // --- ESTADO GLOBAL ---
            ConfigCategory main = builder.getOrCreateCategory(Text.literal("Principal"));
            main.addEntry(eb.startBooleanToggle(Text.literal("Activar Módulo"), cfg.enabled)
                .setDefaultValue(true).setSaveConsumer(v -> cfg.enabled = v).build());

            // --- SUPERVIVENCIA (PVP) ---
            ConfigCategory pvp = builder.getOrCreateCategory(Text.literal("🛡️ Supervivencia"));
            pvp.addEntry(eb.startBooleanToggle(Text.literal("Auto-Tótem Inteligente"), cfg.autoTotem)
                .setTooltip(Text.literal("Equipa tótems del inventario principal a la mano secundaria."))
                .setSaveConsumer(v -> cfg.autoTotem = v).build());

            pvp.addEntry(eb.startIntSlider(Text.literal("Velocidad de Intercambio"), cfg.swapSpeed, 1, 5)
                .setTooltip(Text.literal("1 = Instantáneo (Riesgo de Desync), 5 = Lento (Seguro)."))
                .setSaveConsumer(v -> cfg.swapSpeed = v).build());

            pvp.addEntry(eb.startFloatField(Text.literal("Salud de Activación"), cfg.totemHealthThreshold)
                .setTooltip(Text.literal("Se activa cuando tu vida baja de este valor."))
                .setSaveConsumer(v -> cfg.totemHealthThreshold = v).build());

            // --- RENDIMIENTO (MINERÍA) ---
            ConfigCategory utility = builder.getOrCreateCategory(Text.literal("⛏️ Utilidad"));
            utility.addEntry(eb.startBooleanToggle(Text.literal("Auto-Tool Automático"), cfg.switchOnBlock)
                .setSaveConsumer(v -> cfg.switchOnBlock = v).build());
            
            utility.addEntry(eb.startIntSlider(Text.literal("Retraso de Regreso"), cfg.revertDelayTicks, 1, 10)
                .setSaveConsumer(v -> cfg.revertDelayTicks = v).build());

            // --- SEGURIDAD ---
            ConfigCategory safety = builder.getOrCreateCategory(Text.literal("👻 Ghost Protocol"));
            safety.addEntry(eb.startBooleanToggle(Text.literal("Modo Indetectable"), cfg.stealthMode)
                .setTooltip(Text.literal("Simula latencia humana para evitar Bans."))
                .setSaveConsumer(v -> cfg.stealthMode = v).build());

            return builder.build();
        };
    }
}
