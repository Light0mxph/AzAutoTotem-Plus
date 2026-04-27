package com.azautotem.logic;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EntityTypeTags;

public class ToolSelector {
    
    // Solo busca en el inventario principal (slots 9 a 35), ignorando la hotbar
    public static int findTotem(PlayerInventory inv) {
        for (int i = 9; i < 36; i++) {
            if (inv.getStack(i).isOf(Items.TOTEM_OF_UNDYING)) return i;
        }
        return -1;
    }

    // Busca un escudo en todo el inventario
    public static int findShield(PlayerInventory inv) {
        for (int i = 0; i < 36; i++) {
            if (inv.getStack(i).isOf(Items.SHIELD)) return i;
        }
        return -1;
    }

    public static int findBestTool(PlayerInventory inv, BlockState state, boolean searchFull) {
        int best = -1; float max = 1.0f;
        int limit = searchFull ? 36 : 9;

        for (int i = 0; i < limit; i++) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty()) continue;
            float speed = s.getMiningSpeedMultiplier(state);
            if (s.isSuitableFor(state)) speed += 50.0f;

            if (speed > max) { max = speed; best = i; }
        }
        return best;
    }

    public static int findBestWeapon(PlayerInventory inv, Entity target, boolean searchFull) {
        int best = -1; double maxDmg = -1.0;
        int limit = searchFull ? 36 : 9;
        var registry = inv.player.getWorld().getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        for (int i = 0; i < limit; i++) {
            ItemStack s = inv.getStack(i);
            if (s.isEmpty()) continue;
            final double[] dmg = {1.0};
            s.applyAttributeModifiers(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
                if (attribute.matches(EntityAttributes.GENERIC_ATTACK_DAMAGE)) dmg[0] += modifier.value();
            });
            dmg[0] += EnchantmentHelper.getLevel(registry.getOrThrow(Enchantments.SHARPNESS), s) * 0.5 + 0.5;
            if (target instanceof LivingEntity living) {
                if (living.getType().isIn(EntityTypeTags.UNDEAD)) {
                    dmg[0] += EnchantmentHelper.getLevel(registry.getOrThrow(Enchantments.SMITE), s) * 2.5;
                }
                if (living.getType().isIn(EntityTypeTags.ARTHROPOD)) {
                    dmg[0] += EnchantmentHelper.getLevel(registry.getOrThrow(Enchantments.BANE_OF_ARTHROPODS), s) * 2.5;
                }
            }
            if (dmg[0] > maxDmg) { maxDmg = dmg[0]; best = i; }
        }
        return best;
    }
}
