package com.azautotem.handler;

import com.azautotem.config.AzAutoTotemConfig;
import com.azautotem.logic.ToolSelector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
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
    private static int actionCooldown = 0;
    private static final Random RNG = new Random();
    private static AzAutoTotemConfig cfg = AzAutoTotemConfig.load();

    public static void handleTick(MinecraftClient client) {
        if (client.player == null || client.world == null) return;
        
        // 🛑 BLOQUEO MAESTRO: Cofres, Shulkers, Chat, Inventario... TODO pausa el mod.
        if (client.currentScreen != null) return; 

        if (++configTimer >= 100) { 
            if (AzAutoTotemConfig.needsReload()) cfg = AzAutoTotemConfig.load(); 
            configTimer = 0; 
        }
        if (!cfg.enabled) return;

        if (actionCooldown > 0) { actionCooldown--; return; }

        int currentSlot = client.player.getInventory().selectedSlot;

        // 🛡️ TÓTEM Y ESCUDO (Prioridad Absoluta)
        if (client.player.getHealth() <= cfg.totemHealthThreshold || client.player.getOffHandStack().isEmpty()) {
            if (!client.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
                int totemSlot = ToolSelector.findTotem(client.player.getInventory());
                
                if (totemSlot != -1) {
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, totemSlot, 40, SlotActionType.SWAP, client.player);
                    actionCooldown = 3;
                    return; 
                } else if (cfg.autoShieldBackup && !client.player.getOffHandStack().isOf(Items.SHIELD)) {
                    int shieldSlot = ToolSelector.findShield(client.player.getInventory());
                    if (shieldSlot != -1) {
                        int syncSlot = shieldSlot < 9 ? shieldSlot + 36 : shieldSlot;
                        client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, syncSlot, 40, SlotActionType.SWAP, client.player);
                        actionCooldown = 3;
                        return;
                    }
                }
            }
        }

        // ⛏️ SELECCIÓN DE HERRAMIENTAS (Solo bloques)
        HitResult hit = client.crosshairTarget;
        int nextTarget = -1;
        if (hit != null && hit.getType() == HitResult.Type.BLOCK && cfg.switchOnBlock) {
            var bh = (BlockHitResult) hit;
            var state = client.world.getBlockState(bh.getBlockPos());
            if (!cfg.blacklistedBlocks.contains(Registries.BLOCK.getId(state.getBlock()).toString())) {
                nextTarget = ToolSelector.findBestTool(client.player.getInventory(), state);
            }
        }

        if (nextTarget != -1 && nextTarget != currentSlot) {
            if (nextTarget != pendingTarget) {
                pendingTarget = nextTarget;
                jitterTimer = cfg.stealthMode ? RNG.nextInt(cfg.maxJitterTicks + 1) : 0;
            }
            if (jitterTimer <= 0) {
                if (originalSlot == -1) originalSlot = currentSlot;
                client.player.getInventory().selectedSlot = nextTarget; 
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
