package com.cosmosnode.battlepets.events;

import com.cosmosnode.battlepets.BattlePets;
import com.cosmosnode.battlepets.MobStats;
import com.cosmosnode.battlepets.utils.Language;
import com.cosmosnode.battlepets.utils.Shop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;

public class InventoryEvents implements Listener {
    BattlePets plugin;

    public InventoryEvents(BattlePets plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void clickmenu(InventoryClickEvent event) {
        String invname = ChatColor.stripColor(event.getInventory().getName()).toLowerCase();

        if (event.getCurrentItem() == null) return;

        ItemStack item = event.getCurrentItem();

        if (event.getCurrentItem().getType() == Material.AIR) return;
        if (!item.hasItemMeta()) return;
        if (!item.getItemMeta().hasDisplayName()) return;

        String petmenu = ChatColor.stripColor(Language.getMessage("pet_stats", true));
        String skillsmenu = ChatColor.stripColor(Language.getMessage("menu_skillpoints", true));

        if (invname.equalsIgnoreCase(petmenu)) {
            event.setCancelled(true);

            LivingEntity pet = BattlePets.pets.get(( event.getWhoClicked()).getUniqueId());

            if (pet == null) ( event.getWhoClicked()).closeInventory();

            else
                mainmenu(event);
            return;
        } else if (invname.equalsIgnoreCase(skillsmenu)) {
            event.setCancelled(true);

            LivingEntity pet = BattlePets.pets.get(( event.getWhoClicked()).getUniqueId());

            if (pet == null) event.getWhoClicked().closeInventory();
            else
                skillmenu(event);

            return;
        } else if (event.getClickedInventory() instanceof HorseInventory) {
            if (BattlePets.pets.containsKey(( event.getWhoClicked()).getUniqueId())) {
                LivingEntity pet = BattlePets.pets.get(( event.getWhoClicked()).getUniqueId());
                if (pet instanceof Horse) {
                    if (pet.getPassenger() == ( event.getWhoClicked())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    void mainmenu(InventoryClickEvent event) {
        String choose = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
        String returnas, petshop, skillpoints, ride, battle;

        returnas = ChatColor.stripColor(Language.getMessage("egg_return", true));
        petshop = ChatColor.stripColor(Language.getMessage("pet_shop", true));
        skillpoints = ChatColor.stripColor(Language.getMessage("menu_skillpoints", true));
        ride = ChatColor.stripColor(Language.getMessage("menu_ride", true));
        battle = ChatColor.stripColor(Language.getMessage("menu_battle", true));

        if (event.getSlot() == 0) {
            if (event.getWhoClicked().hasPermission("battlepets.name")) {
                PlayerEvents.namechanging.add(event.getWhoClicked().getUniqueId());
                event.getWhoClicked().closeInventory();
                event.getWhoClicked().sendMessage(Language.getMessage("rename"));
            }
        }

        if (choose.equalsIgnoreCase(returnas)) {
            BattlePets.return_pet((Player) event.getWhoClicked());
            event.getWhoClicked().closeInventory();
            return;
        }

        if (choose.equalsIgnoreCase(petshop)) {
            Shop.openshop((Player) event.getWhoClicked(), "main_menu");
            return;
        }

        if (choose.equalsIgnoreCase(skillpoints)) {
            BattlePets.skillpointsmenu((Player) event.getWhoClicked());
            return;
        }

        if (choose.equalsIgnoreCase(ride)) {
            BattlePets.pets.get(event.getWhoClicked().getUniqueId()).setPassenger(event.getWhoClicked());
            return;
        }

        if (choose.equalsIgnoreCase(battle)) {
            event.getWhoClicked().closeInventory();
            if (!PlayerEvents.battles.containsKey(event.getWhoClicked().getUniqueId())) {
                PlayerEvents.battles.put(event.getWhoClicked().getUniqueId(), null);
                event.getWhoClicked().sendMessage(Language.getMessage("battle_request"));
            } else {
                PlayerEvents.battles.remove(event.getWhoClicked().getUniqueId());
                event.getWhoClicked().sendMessage(Language.getMessage("battle_request_stop"));
            }
            return;
        }

    }

    void skillmenu(InventoryClickEvent event) {
        String click = event.getAction().toString();
        if (!click.equalsIgnoreCase("pickup_all") && !click.equalsIgnoreCase("pickup_half")) return;
        int slot = event.getRawSlot();
        if (slot == 8) {
            BattlePets.openmenu((Player) event.getWhoClicked(), BattlePets.pets.get(event.getWhoClicked().getUniqueId()));
        }
        if (event.getClickedInventory().getSize() == 9) return;
        int currentpoints = Integer.valueOf(event.getClickedInventory().getItem(0).getItemMeta().getLore().get(0).substring(
                event.getClickedInventory().getItem(0).getItemMeta().getLore().get(0).lastIndexOf(":") + 2));
        switch (slot) {
            case 11:
            case 12:
            case 13:
            case 14:
                LivingEntity pet = BattlePets.pets.get(event.getWhoClicked().getUniqueId());
                String type = pet.getMetadata("Type").get(0).asString().toLowerCase();
                String st = "";

                if (type.contains("baby")) {
                    st += "baby-";
                }

                st += pet.getType().toString().toLowerCase();

                if (st.equalsIgnoreCase("endermite")) {
                    st = "block";
                }

                MobStats stats = BattlePets.statsai.get(st);
                String lore = event.getClickedInventory().getItem(slot - 9).getItemMeta().getLore().get(0);
                int viso = Integer.parseInt(lore.substring(lore.lastIndexOf(":") + 2));
                String name = event.getClickedInventory().getItem(slot).getItemMeta().getDisplayName();
                int amount = Integer.parseInt(name.substring(name.lastIndexOf("+")));

                if (click.equalsIgnoreCase("pickup_all") && currentpoints > 0 && viso + amount < stats.maxes.get(slot)) {
                    amount++;
                    String points = event.getClickedInventory().getItem(0).getItemMeta().getLore().get(0);
                    points = points.substring(0, points.lastIndexOf(":") + 2) + (currentpoints - 1);
                    ItemStack temp = event.getClickedInventory().getItem(0);
                    ItemMeta tempmeta = temp.getItemMeta();
                    tempmeta.setLore(Arrays.asList(points));
                    temp.setItemMeta(tempmeta);
                    event.getClickedInventory().setItem(0, temp);
                } else if (amount > 0 && click.equalsIgnoreCase("pickup_half")) {
                    amount--;
                    String points = event.getClickedInventory().getItem(0).getItemMeta().getLore().get(0);
                    points = points.substring(0, points.lastIndexOf(":") + 2) + (currentpoints + 1);
                    ItemStack temp = event.getClickedInventory().getItem(0);
                    ItemMeta tempmeta = temp.getItemMeta();
                    tempmeta.setLore(Arrays.asList(points));
                    temp.setItemMeta(tempmeta);
                    event.getClickedInventory().setItem(0, temp);
                }

                name = name.substring(0, name.lastIndexOf("+") + 1);
                name += amount;
                ItemStack temp = event.getClickedInventory().getItem(slot);
                ItemMeta tempmeta = temp.getItemMeta();
                tempmeta.setDisplayName(name);
                temp.setItemMeta(tempmeta);
                event.getClickedInventory().setItem(slot, temp);
                ((Player) event.getWhoClicked()).updateInventory();
                break;
        }
        if (slot == 17) {
            //save
            LivingEntity pet = BattlePets.pets.get(event.getWhoClicked().getUniqueId());
            Inventory inv = event.getClickedInventory();
            int plusvitality = 0;
            int plusstrength = 0;
            int plusdefense = 0;
            int plusdexterity = 0;

            plusvitality = Integer.parseInt(inv.getItem(11).getItemMeta().getDisplayName().substring(inv.getItem(11).getItemMeta().getDisplayName().lastIndexOf("+")));
            plusstrength = Integer.parseInt(inv.getItem(12).getItemMeta().getDisplayName().substring(inv.getItem(12).getItemMeta().getDisplayName().lastIndexOf("+")));
            plusdefense = Integer.parseInt(inv.getItem(13).getItemMeta().getDisplayName().substring(inv.getItem(13).getItemMeta().getDisplayName().lastIndexOf("+")));
            plusdexterity = Integer.parseInt(inv.getItem(14).getItemMeta().getDisplayName().substring(inv.getItem(14).getItemMeta().getDisplayName().lastIndexOf("+")));

            BattlePets pl = (BattlePets) BattlePets.plugin;

            pet.setMetadata("Vitality", new FixedMetadataValue(pl, pet.getMetadata("Vitality").get(0).asInt() + plusvitality));
            pet.setMetadata("Strength", new FixedMetadataValue(pl, pet.getMetadata("Strength").get(0).asInt() + plusstrength));
            pet.setMetadata("Defense", new FixedMetadataValue(pl, pet.getMetadata("Defense").get(0).asInt() + plusdefense));
            pet.setMetadata("Dexterity", new FixedMetadataValue(pl, pet.getMetadata("Dexterity").get(0).asInt() + plusdexterity));
            pet.setMetadata("Points", new FixedMetadataValue(pl, currentpoints));

            BattlePets.spawning.update(pet, pl);

            event.getWhoClicked().sendMessage(Language.getMessage("skills_saved"));
            event.getWhoClicked().closeInventory();
        }
    }
}
