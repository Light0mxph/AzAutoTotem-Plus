package com.azautotem.logic;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ToolSelector {
    
    // Tótems: Solo inventario principal (9 a 35)
    public static int findTotem(PlayerInventory inv) {
        for (int i = 9; i < 36; i++) {
            if (inv.getStack(i).isOf(Items.TOTEM_OF_UNDYING)) return i;
        }
        return -1;
    }

    // Escudos: Todo el inventario
    public static int findShield(PlayerInventory inv) {
        for (int i = 0; i < 36; i++) {
            if (inv.getStack(i).isOf(Items.SHIELD)) return i;
        }
        return -1;
    }

    // Herramientas: Solo Hotbar (0 a 8)
    public static int findBestTool(PlayerInventory inv, BlockState state) {
        int best = -1; 
        float max = 1.0f; 

        for (int i = 0; i < 9; i++) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty()) continue;
            float speed = s.getMiningSpeedMultiplier(state);
            if (s.isSuitableFor(state)) speed += 50.0f;

            if (speed > max) { max = speed; best = i; }
        }
        return best;
    }
}
