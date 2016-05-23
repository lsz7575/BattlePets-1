package com.cosmosnode.battlepets.comamnds;

import com.cosmosnode.battlepets.BattlePets;
import com.cosmosnode.battlepets.utils.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by me on 5/22/16.
 */
public class CommandReload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.hasPermission("battlepets.reload")) {
            BattlePets.plugin.reloadConfig();
            ((BattlePets) BattlePets.plugin).reload();
            sender.sendMessage(Language.getMessage("reloaded"));
        } else {
            sender.sendMessage(Language.getMessage("no_permission"));
        }
        return false;
    }
}
