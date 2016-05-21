package com.cosmosnode.battlepets.versions.v1_9_2;

import com.cosmosnode.battlepets.BattlePets;
import com.cosmosnode.battlepets.MobStats;
import net.minecraft.server.v1_9_R2.EntityEndermite;
import net.minecraft.server.v1_9_R2.World;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class CustomPet extends EntityEndermite {
    private ArmorStand stand;
    private MobStats stats;

    public CustomPet(World arg0) {
        super(arg0);
    }

    public CustomPet(World world, String type, Location loc, Player p) {
        super(world);
        stats = BattlePets.statsai.get(type.toLowerCase());
        stand = (ArmorStand) EntityTypes.spawnEntity(
            EntityTypes.createEntity("armorstand", world),
            loc.add(stats.offX, stats.offY - 1.28, stats.offZ)).getBukkitEntity();
        stand.setHelmet(stats.texture);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMetadata("Owner", new FixedMetadataValue(BattlePets.plugin, p.getUniqueId()));
    }

    public void update() {
        stand.teleport(this.getBukkitEntity().getLocation().add(stats.offX, stats.offY - 1.28, stats.offZ));
    }

    public void removeall() {
        stand.remove();
    }

    public void updatename() {
        stand.setCustomName(this.getBukkitEntity().getCustomName());
        stand.setCustomNameVisible(true);
    }
}