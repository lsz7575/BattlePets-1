package com.cosmosnode.battlepets.utils;


import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class WorldguardZones {

    private WorldGuardPlugin wg;
    private Plugin plugin;
    private RegionContainer container;
    private RegionQuery query;
    private boolean defaultspawning;
    private  List<String> exceptions;

    public WorldguardZones(Plugin wg, Plugin plugin) {
        this.wg = (WorldGuardPlugin) wg;
        this.plugin = plugin;
        container = this.wg.getRegionContainer();
        query = container.createQuery();
        defaultspawning = plugin.getConfig().getBoolean("WorldGuard.EnableSpawningByDefault");
        exceptions = plugin.getConfig().getStringList("WorldGuard.ExceptionRegions");
    }

    public boolean isAllowed(Entity p) {
        ApplicableRegionSet set = query.getApplicableRegions(p.getLocation());

        for (ProtectedRegion region : set.getRegions()) {
            for (String s : exceptions) {
                if (region.getId().equalsIgnoreCase(s))
                    return !defaultspawning;
            }
        }

        return defaultspawning;
    }
}
