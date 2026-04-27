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
                .setTitle(Text.literal("Aztrix Prime Studio - PvP Mode").formatted(Formatting.RED, Formatting.BOLD))
                .setSavingRunnable(() -> AzAutoTotemConfig.save(cfg));

            ConfigEntryBuilder eb = builder.entryBuilder();
            ConfigCategory combat = builder.getOrCreateCategory(Text.literal("🛡️ Combate & Survival"));

            combat.addEntry(eb.startBooleanToggle(Text.literal("Auto-Tótem (Solo Inventario)"), cfg.autoTotem)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Ignora la hotbar al reponer tótems."))
                .setSaveConsumer(v -> cfg.autoTotem = v)
                .build());

            combat.addEntry(eb.startBooleanToggle(Text.literal("Auto-Escudo Backup"), cfg.autoShieldBackup)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Si no quedan tótems, equipa un escudo automáticamente."))
                .setSaveConsumer(v -> cfg.autoShieldBackup = v)
                .build());

            combat.addEntry(eb.startFloatField(Text.literal("Umbral de Vida"), cfg.totemHealthThreshold)
                .setDefaultValue(18.0f)
                .setMin(0.0f).setMax(20.0f)
                .setSaveConsumer(v -> cfg.totemHealthThreshold = v)
                .build());

            ConfigCategory tools = builder.getOrCreateCategory(Text.literal("⛏️ Herramientas"));
            tools.addEntry(eb.startBooleanToggle(Text.literal("Auto-Tool"), cfg.switchOnBlock)
                .setSaveConsumer(v -> cfg.switchOnBlock = v).build());
            tools.addEntry(eb.startBooleanToggle(Text.literal("Auto-Weapon"), cfg.switchOnEntity)
                .setSaveConsumer(v -> cfg.switchOnEntity = v).build());

            return builder.build();
        };
    }
}
