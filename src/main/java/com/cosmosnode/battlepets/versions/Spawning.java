package com.cosmosnode.battlepets.versions;

import com.cosmosnode.battlepets.BattlePets;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;

public interface Spawning {
    LivingEntity SpawnCreature(PlayerInteractEvent event, BattlePets plugin);

    void update(LivingEntity pet, BattlePets plugin);

    void setTarget(LivingEntity pet, LivingEntity target);

    void returnPet(LivingEntity pet);

    void nameUpdate(LivingEntity pet);

    void load();
}
