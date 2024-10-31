package me.lz5509.geyserheads.commands;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import me.lz5509.geyserheads.utils.Heads;
import me.lz5509.geyserheads.utils.Heads.HeadType;
import me.lz5509.geyserheads.utils.Utils;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            Utils.sendUsage(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("load")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (!player.isOp()) {
                    player.sendMessage("You don't have op");
                    return true;
                }
                player.sendMessage("Loading Heads");
            } else sender.sendMessage("Loading Heads");

            if (args.length > 1 && (args[1].equalsIgnoreCase("slimefun") || args[1].equalsIgnoreCase("sf"))) {
                if (Bukkit.getPluginManager().getPlugin("Slimefun") == null || !Bukkit.getPluginManager().getPlugin("Slimefun").isEnabled()) {
                    sender.sendMessage("Not found Slimefun");
                    return true;
                }
                Heads heads = new Heads();
                for (ItemGroup group : Slimefun.getRegistry().getAllItemGroups()) {
                    try {
                        Class<?> Group = group.getClass();
                        int antiLoop = 5;
                        while (Group != ItemGroup.class && antiLoop < 5) {
                            Group = Group.getSuperclass();
                            antiLoop++;
                        }
                        Field field = Group.getDeclaredField("item");
                        field.setAccessible(true);
                        ItemStack item = (ItemStack) field.get(group);
                        heads.add(HeadType.SKIN_HASHES, item);
                    } catch (Exception e) {}
                }
                Slimefun.getRegistry().getAllSlimefunItems().forEach(i -> heads.add(HeadType.SKIN_HASHES, i.getItem()));
                Slimefun.getLocalization().getLanguages().forEach(i -> heads.add(HeadType.SKIN_HASHES, i.getItem()));
                heads.add(HeadType.SKIN_HASHES, Slimefun.getLocalization().getDefaultLanguage().getItem());
                heads.add(HeadType.SKIN_HASHES, "e952d2b3f351a6b0487cc59db31bf5f2641133e5ba0006b18576e996a0293e52");
                heads.save(sender);

            } else if (args[1].equalsIgnoreCase("hand")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Heads heads = new Heads();
                    heads.add(HeadType.SKIN_HASHES, player.getInventory().getItemInMainHand());
                    heads.save(sender);
                    return true;
                } else sender.sendMessage("Hand only for players");

            } else if (args[1].equalsIgnoreCase("head-database") || args[1].equalsIgnoreCase("hdb") || args[1].equalsIgnoreCase("headdatabase")) {
                if (Bukkit.getPluginManager().getPlugin("HeadDatabase") == null || !Bukkit.getPluginManager().getPlugin("HeadDatabase").isEnabled()) {
                    sender.sendMessage("Not found HeadDatabase");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage("Usage: /geyserheads load head-database [HeadDatabase-ID]");
                    return true;
                }

                Heads heads = new Heads();
                HeadDatabaseAPI hdb_api = new HeadDatabaseAPI();
                heads.add(HeadType.PLAYER_PROFILES, hdb_api.getBase64(args[2]));
                heads.save(sender);
            } else Utils.sendUsage(sender);
            return true;
        }
        return true;
    }
}
