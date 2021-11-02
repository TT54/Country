package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.InventoryManager;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Relations;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdRelationPermission extends SubCommand {


    public CmdRelationPermission() {
        super("relationpermission", new String[]{"relperm", "rperm", "rpermission"}, "Edit relations permissions");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }
        Player player = (Player) sender;

        if (!CountryManager.hasCountry(player)) {
            player.sendMessage(Main.getMessages().getMessage("nocountry"));
            return false;
        }
        if (!Permission.hasCountryPermission(player, CountryPermission.EDIT_PERMISSIONS)) {
            player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " <relation>"));
            return false;
        }

        Country country = CountryManager.getPlayerCountry(player);
        if (!Relations.exist(args[0])) {
            player.sendMessage(Main.getMessages().getMessage("relationdoesntexist", "%relation%", args[0]));
            return false;
        }
        Relations relation = Relations.getRelation(args[0]);
        InventoryManager.openRelationPermissionInventory(player, country, relation);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (CountryManager.hasCountry(player)) {
                if (args.length == 1) {
                    return Arrays.stream(Relations.values()).map(Relations::name).filter(s -> s.toUpperCase().startsWith(args[0].toUpperCase())).collect(Collectors.toList());
                }
            }
        }
        return null;
    }
}
