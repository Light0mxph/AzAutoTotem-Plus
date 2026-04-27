package com.azautotem.handler;

import com.azautotem.config.AzAutoTotemConfig;
import com.azautotem.logic.ToolSelector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.SlotActionType;
import java.util.Random;

public class AutoToolHandler {
    private static int originalSlot = -1;
    private static int lastKnownSlot = -1;
    private static int ticks = 0;
    private static int jitterTimer = 0;
    private static int pendingTarget = -1;
    private static int configTimer = 0;
    private static final Random RNG = new Random();
    private static AzAutoTotemConfig cfg = AzAutoTotemConfig.load();

    public static void handleTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        
        if (++configTimer >= 100) { 
            if (AzAutoTotemConfig.needsReload()) cfg = AzAutoTotemConfig.load(); 
            configTimer = 0; 
        }
        if (!cfg.enabled) return;

        int currentSlot = client.player.getInventory().selectedSlot;

        // LÓGICA DE SUPERVIVENCIA AGRESIVA
        if (client.player.getHealth() <= cfg.totemHealthThreshold || client.player.getOffHandStack().isEmpty()) {
            if (!client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                int totemSlot = ToolSelector.findTotem(client.player.getInventory());
                
                if (totemSlot != -1) {
                    // Equipar tótem (siempre desde inventario 9-35)
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, totemSlot, 40, SlotActionType.SWAP, client.player);
                } else if (cfg.autoShieldBackup && !client.player.getOffHandStack().isOf(Items.SHIELD)) {
                    // Si no hay tótems, buscar escudo
                    int shieldSlot = ToolSelector.findShield(client.player.getInventory());
                    if (shieldSlot != -1) {
                        int syncSlot = shieldSlot < 9 ? shieldSlot + 36 : shieldSlot;
                        client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, syncSlot, 40, SlotActionType.SWAP, client.player);
                    }
                }
            }
        }

        HitResult hit = client.crosshairTarget;
        int nextTarget = -1;
        if (hit != null) {
            if (hit.getType() == HitResult.Type.BLOCK && cfg.switchOnBlock) {
                var bh = (BlockHitResult) hit;
                var state = client.world.getBlockState(bh.getBlockPos());
                if (!cfg.blacklistedBlocks.contains(Registries.BLOCK.getId(state.getBlock()).toString())) {
                    nextTarget = ToolSelector.findBestTool(client.player.getInventory(), state, cfg.searchFullInventory);
                }
            } else if (hit.getType() == HitResult.Type.ENTITY && cfg.switchOnEntity) {
                var eh = (EntityHitResult) hit;
                nextTarget = ToolSelector.findBestWeapon(client.player.getInventory(), eh.getEntity(), cfg.searchFullInventory);
            }
        }

        if (nextTarget != -1 && nextTarget != currentSlot) {
            if (nextTarget != pendingTarget) {
                pendingTarget = nextTarget;
                jitterTimer = cfg.stealthMode ? RNG.nextInt(cfg.maxJitterTicks + 1) : 0;
            }
            if (jitterTimer <= 0) {
                if (originalSlot == -1) originalSlot = currentSlot;
                if (nextTarget < 9) client.player.getInventory().selectedSlot = nextTarget;
                else client.interactionManager.pickFromInventory(nextTarget);
                lastKnownSlot = client.player.getInventory().selectedSlot;
                ticks = 0;
                pendingTarget = -1;
            } else { jitterTimer--; }
        } else if (cfg.autoRevert && originalSlot != -1) {
            if (++ticks >= cfg.revertDelayTicks) { 
                client.player.getInventory().selectedSlot = originalSlot; 
                lastKnownSlot = originalSlot;
                originalSlot = -1; 
            }
        } else { lastKnownSlot = currentSlot; }
    }
}
