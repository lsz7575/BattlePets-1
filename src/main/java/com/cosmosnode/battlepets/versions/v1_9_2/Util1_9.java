package com.cosmosnode.battlepets.versions.v1_9_2;

import net.minecraft.server.v1_9_R2.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class Util1_9 {
    public static boolean fromItemStack(ItemStack item) {
        if (item == null)
            return true;
        if (item.getType() != Material.MONSTER_EGG)
            return true;
        net.minecraft.server.v1_9_R2.ItemStack stack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tagCompound = stack.getTag();
        if (tagCompound != null) {
            @SuppressWarnings("deprecation")
            EntityType type = EntityType.fromName(tagCompound.getCompound("EntityTag").getString("id"));
            if (type != null) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
