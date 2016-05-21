package com.cosmosnode.battlepets.versions.v1_9_2;

import com.cosmosnode.battlepets.BattlePets;
import net.minecraft.server.v1_9_R2.EntityEndermite;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class ArmorStandPlus extends EntityEndermite {
    private ArmorStand stand;

    public ArmorStandPlus(World world) {
        super(world);
    }

    public ArmorStandPlus(World world, String blocks, Location loc, Player player) {
        super(world);
        stand = (ArmorStand) EntityTypes.spawnEntity(EntityTypes.createEntity("armorstand", world), loc.add(0, -1.28, 0)).getBukkitEntity();
        String[] opt = blocks.split(":");
        Material block = Material.valueOf(opt[0]);
        if (block == Material.SKULL_ITEM) {
            ItemStack sk = new ItemStack(block, 1, (short) 3);
            SkullMeta meta = (SkullMeta) sk.getItemMeta();
            meta.setOwner(opt[1]);
            sk.setItemMeta(meta);
            stand.setHelmet(sk);
        } else {
            Short data = opt.length == 2 ? Short.parseShort(opt[1]) : (short) 0;
            stand.setHelmet(new ItemStack(block, 1, data));
        }
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMetadata("Owner", new FixedMetadataValue(BattlePets.plugin, player.getUniqueId()));
    }

    public void update() {
        stand.teleport(this.getBukkitEntity().getLocation().add(0, -1.28, 0));
    }

    public void removeall() {
        stand.remove();
    }

    public void updatename() {
        stand.setCustomName(this.getBukkitEntity().getCustomName());
        stand.setCustomNameVisible(true);
    }
}
