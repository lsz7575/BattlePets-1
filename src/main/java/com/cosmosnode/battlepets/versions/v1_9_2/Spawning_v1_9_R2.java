package com.cosmosnode.battlepets.versions.v1_9_2;

import com.cosmosnode.battlepets.BattlePets;
import com.cosmosnode.battlepets.MobStats;
import com.cosmosnode.battlepets.utils.Language;
import com.cosmosnode.battlepets.versions.Spawning;
import com.cosmosnode.battlepets.versions.Util;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Set;

public class Spawning_v1_9_R2 implements Spawning {

    @Override
    @SuppressWarnings("rawtypes")
    public LivingEntity SpawnCreature(PlayerInteractEvent event, BattlePets plugin) {
        ItemStack item = event.getItem();

        List<String> lore = item.getItemMeta().getLore();
        String[] type = lore.get(0).split(" ")[lore.get(0).split(" ").length - 1].split("-");
        String stats = "";
        if (type[0].equalsIgnoreCase("baby"))
            stats += "baby-";

        stats += type[type.length - 1].toLowerCase();

        if (!BattlePets.statsai.containsKey(stats)) {
            event.getPlayer().sendMessage(Language.getMessage("pet_notspawnable"));
            return null;
        }

        if (!event.getPlayer().hasPermission("battlepets.spawn.*") && !event.getPlayer().hasPermission("battlepets.spawn." + stats) && !event.getPlayer().hasPermission("battlepets.spawn." + stats.replace("baby-", "baby_"))) {
            event.getPlayer().sendMessage(Language.getMessage("pet_noperm_spawn"));
            return null;
        }

        MobStats statsai = BattlePets.statsai.get(stats);
        String mobtype;
        int level, points;
        double hp;
        int Vitality, Defense, Strength, Dexterity;
        double xp, xpforlevel;

        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, ChatColor.stripColor(lore.get(i)));
        }

        mobtype = lore.get(0).substring(lore.get(0).indexOf(":") + 2);
        level = Integer.valueOf(lore.get(1).substring(lore.get(1).indexOf(":") + 2));
        xp = Double.valueOf(lore.get(2).substring(lore.get(2).indexOf(":") + 2, lore.get(2).indexOf("/")));
        hp = Double.valueOf(lore.get(3).substring(lore.get(3).indexOf(":") + 2, lore.get(3).indexOf("/")));

        if (hp == 0) {
            plugin.shop.openrevive(event.getPlayer());
            return null;
        }

        points = Integer.valueOf(lore.get(4).substring(lore.get(4).indexOf(":") + 2));
        Vitality = Integer.valueOf(lore.get(5).substring(lore.get(5).indexOf(":") + 2));
        Defense = Integer.valueOf(lore.get(6).substring(lore.get(6).indexOf(":") + 2));
        Strength = Integer.valueOf(lore.get(7).substring(lore.get(7).indexOf(":") + 2));
        Dexterity = Integer.valueOf(lore.get(8).substring(lore.get(8).indexOf(":") + 2));
        xpforlevel = level * statsai.XPForLevel;
        Entity entity = null;
        CraftWorld world = (CraftWorld) event.getClickedBlock().getWorld();

        try {
            if (statsai.custom) {
                entity = com.cosmosnode.battlepets.versions.v1_9_2.EntityTypes.spawnEntity(new CustomPet(world.getHandle(), type[0], event.getClickedBlock().getLocation().add(0, 1, 0), event.getPlayer()), event.getClickedBlock().getLocation().add(0, 1, 0)).getBukkitEntity();
            } else if (type[type.length - 1].equalsIgnoreCase("block")) {
                entity = com.cosmosnode.battlepets.versions.v1_9_2.EntityTypes.spawnEntity(new ArmorStandPlus(world.getHandle(), type[0], event.getClickedBlock().getLocation().add(0, 1, 0), event.getPlayer()), event.getClickedBlock().getLocation().add(0, 1, 0)).getBukkitEntity();
                entity.setMetadata("Block", new FixedMetadataValue(plugin, type[0]));
            } else
                entity = com.cosmosnode.battlepets.versions.v1_9_2.EntityTypes.spawnEntity(com.cosmosnode.battlepets.versions.v1_9_2.EntityTypes.createEntity(type[type.length - 1].toLowerCase(), world.getHandle()), event.getClickedBlock().getLocation().add(0, 1, 0)).getBukkitEntity();
            ((LivingEntity) entity).getEquipment().clear();

            if (entity instanceof Ageable) {
                if (type[0].equalsIgnoreCase("baby"))
                    ((Ageable) entity).setBaby();
                else
                    ((Ageable) entity).setAdult();
                ((Ageable) entity).setAgeLock(true);
            }

            if (entity instanceof Horse) {
                ((Horse) entity).setTamed(true);
                ((Horse) entity).setOwner(event.getPlayer());
                ((Horse) entity).getInventory().setSaddle(new ItemStack(Material.SADDLE));

                if (type[0].equalsIgnoreCase("baby")) {
                    ((Horse) entity).setColor(Color.valueOf(type[1]));
                    ((Horse) entity).setStyle(Style.valueOf(type[2]));
                    ((Horse) entity).setVariant(Variant.valueOf(type[3]));
                } else {
                    ((Horse) entity).setColor(Color.valueOf(type[0]));
                    ((Horse) entity).setStyle(Style.valueOf(type[1]));
                    ((Horse) entity).setVariant(Variant.valueOf(type[2]));
                }
            } else if (entity instanceof Rabbit) {
                if (type[0].equalsIgnoreCase("baby")) {
                    ((Rabbit) entity).setRabbitType(Type.valueOf(type[1]));
                } else {
                    ((Rabbit) entity).setRabbitType(Type.valueOf(type[0]));
                }
            } else if (entity instanceof Sheep) {
                if (type[0].equalsIgnoreCase("baby")) {
                    ((Sheep) entity).setColor(DyeColor.valueOf(type[1]));
                } else {
                    ((Sheep) entity).setColor(DyeColor.valueOf(type[0]));
                }
            } else if (entity instanceof Skeleton) {
                if (type[0].equalsIgnoreCase("wither")) {
                    ((Skeleton) entity).setSkeletonType(SkeletonType.WITHER);
                    ((Skeleton) entity).getEquipment().setItemInMainHand(new ItemStack(Material.STONE_SWORD));
                } else {
                    ((Skeleton) entity).setSkeletonType(SkeletonType.NORMAL);
                    ((Skeleton) entity).getEquipment().setItemInMainHand(new ItemStack(Material.BOW));
                }
            } else if (entity instanceof Villager) {
                if (type[0].equalsIgnoreCase("baby")) {
                    ((Villager) entity).setProfession(Profession.valueOf(type[1]));
                } else {
                    ((Villager) entity).setProfession(Profession.valueOf(type[0]));
                }
            } else if (entity instanceof Zombie) {
                if (type[0].equalsIgnoreCase("baby")) {
                    ((Zombie) entity).setBaby(true);
                    ((Zombie) entity).setVillager(false);
                } else ((Zombie) entity).setBaby(false);
                if (type.length > 1)
                    if (type[0].equalsIgnoreCase("villager") || type[1].equalsIgnoreCase("villager")) {
                        ((Zombie) entity).setVillagerProfession(Profession.FARMER);
                    } else {
                        ((Zombie) entity).setVillagerProfession(Profession.FARMER);
                    }
            } else if (entity instanceof PigZombie) {
                ((PigZombie) entity).getEquipment().setItemInMainHand(new ItemStack(Material.GOLD_SWORD));
            } else if (entity instanceof Slime) {
                ((Slime) entity).setSize(Integer.parseInt(type[0]));
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.getPlayer().sendMessage(Language.getMessage("corrupted_egg"));

            try {
                entity.remove();
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            return null;
        }

        LivingEntity pet = (LivingEntity) entity;
        EntityInsentient tt = (EntityInsentient) ((CraftLivingEntity) pet).getHandle();

        if (tt instanceof WitherPet && type[0].equalsIgnoreCase("baby")) {
            ((WitherPet) tt).setBaby(true);
        }

        pet.setCustomName(Language.display.replace("{name}", item.getItemMeta().getDisplayName()).replace("{level}", level + ""));

        if (tt instanceof ArmorStandPlus)
            ((ArmorStandPlus) tt).updatename();

        if (tt instanceof CustomPet)
            ((CustomPet) tt).updatename();

        tt.setCustomNameVisible(true);
        Set goalB = (Set) Util.getPrivateField("b", PathfinderGoalSelector.class, tt.goalSelector);
        goalB.clear();
        Set goalC = (Set) Util.getPrivateField("c", PathfinderGoalSelector.class, tt.goalSelector);
        goalC.clear();
        Set targetB = (Set) Util.getPrivateField("b", PathfinderGoalSelector.class, tt.targetSelector);
        targetB.clear();
        Set targetC = (Set) Util.getPrivateField("c", PathfinderGoalSelector.class, tt.targetSelector);
        targetC.clear();

        if (tt.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE) == null)
            tt.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);

        tt.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(statsai.Damage + Strength * statsai._Damage);
        pet.setMaxHealth(statsai.HP + Vitality * statsai._HP);

        if (hp < 0) hp = 0;

        pet.setHealth(Math.min(hp, statsai.HP + Vitality * statsai._HP));

        pet.setMetadata("Level", new FixedMetadataValue(plugin, level));
        pet.setMetadata("Points", new FixedMetadataValue(plugin, points));
        pet.setMetadata("Vitality", new FixedMetadataValue(plugin, Vitality));
        pet.setMetadata("Defense", new FixedMetadataValue(plugin, Defense));
        pet.setMetadata("Dexterity", new FixedMetadataValue(plugin, Dexterity));
        pet.setMetadata("Strength", new FixedMetadataValue(plugin, Strength));
        pet.setMetadata("XP", new FixedMetadataValue(plugin, xp));
        pet.setMetadata("XPForLevel", new FixedMetadataValue(plugin, xpforlevel));
        pet.setMetadata("Owner", new FixedMetadataValue(plugin, event.getPlayer().getUniqueId().toString()));
        pet.setMetadata("Name", new FixedMetadataValue(plugin, item.getItemMeta().getDisplayName()));
        pet.setMetadata("Type", new FixedMetadataValue(plugin, mobtype));
        pet.setMetadata("Damage", new FixedMetadataValue(plugin, statsai.Damage + Strength * statsai._Damage));
        pet.setMetadata("Regen", new FixedMetadataValue(plugin, statsai.HPPerSecPercent));
        pet.setMetadata("Speed", new FixedMetadataValue(plugin, statsai.Speed + pet.getMetadata("Dexterity").get(0).asInt() * statsai._Speed));
        tt.goalSelector.a(0, new PathfinderGoalFloat(tt));

        if ((entity instanceof Skeleton && !lore.get(0).contains("WITHER")) || (entity instanceof Wither && !type[0].equalsIgnoreCase("baby")))
            tt.goalSelector.a(4, new PathfinderGoalArrowAttack((IRangedEntity) tt, statsai.Speed + Dexterity * statsai._Speed, 20, 60, 15.0F));
        else
            tt.goalSelector.a(4, new PathFinderTargetAttack(tt, statsai.Speed + Dexterity * statsai._Speed, true));

        tt.goalSelector.a(2, new PathFinderGoalFollow(tt, ((CraftPlayer) event.getPlayer()).getHandle(), plugin.radius1, plugin.radius2, statsai.Speed + Dexterity * statsai._Speed));
        event.getPlayer().getInventory().remove(item);

        return pet;
    }

    @Override
    public void update(LivingEntity pet, BattlePets plugin) {
        EntityLiving pett = ((CraftLivingEntity) pet).getHandle();
        String type = pet.getMetadata("Type").get(0).asString().toLowerCase();
        String typeconf = "";

        if (type.contains("baby"))
            typeconf += "baby-";

        typeconf += pet.getType().toString().toLowerCase();

        if (typeconf.equalsIgnoreCase("endermite"))
            typeconf = "block";

        MobStats statsai = BattlePets.statsai.get(typeconf);
        pett.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(statsai.Damage + pet.getMetadata("Strength").get(0).asInt() * statsai._Damage);
        pet.setMetadata("Damage", new FixedMetadataValue(plugin, statsai.Damage + pet.getMetadata("Strength").get(0).asInt() * statsai._Damage));
        pet.setMetadata("Speed", new FixedMetadataValue(plugin, statsai.Speed + pet.getMetadata("Dexterity").get(0).asInt() * statsai._Speed));
        pet.setMaxHealth(statsai.HP + pet.getMetadata("Vitality").get(0).asInt() * statsai._HP);
        pet.setMetadata("XPForLevel", new FixedMetadataValue(plugin, statsai.XPForLevel * pet.getMetadata("Level").get(0).asInt()));
    }

    @Override
    public void setTarget(LivingEntity pet, LivingEntity target) {
        if ((target instanceof Player) && !BattlePets.PVP) return;

        CraftLivingEntity pet1 = (CraftLivingEntity) pet;
        EntityInsentient pet2 = (EntityInsentient) pet1.getHandle();

        if (target == null) {
            pet2.setGoalTarget(null);
            return;
        }

        pet2.setGoalTarget(((CraftLivingEntity) target).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, true);
    }

    @Override
    public void nameUpdate(LivingEntity pet) {
        if (((CraftLivingEntity) pet).getHandle() instanceof ArmorStandPlus)
            ((ArmorStandPlus) ((CraftLivingEntity) pet).getHandle()).updatename();

        if (((CraftLivingEntity) pet).getHandle() instanceof CustomPet)
            ((CustomPet) ((CraftLivingEntity) pet).getHandle()).updatename();
    }

    @Override
    public void returnPet(LivingEntity pet) {
        if (((CraftLivingEntity) pet).getHandle() instanceof ArmorStandPlus)
            ((ArmorStandPlus) ((CraftLivingEntity) pet).getHandle()).removeall();

        if (((CraftLivingEntity) pet).getHandle() instanceof CustomPet)
            ((CustomPet) ((CraftLivingEntity) pet).getHandle()).removeall();
    }

    @Override
    public void load() {
        com.cosmosnode.battlepets.versions.v1_9_2.EntityTypes.loadMobs();
    }
}
