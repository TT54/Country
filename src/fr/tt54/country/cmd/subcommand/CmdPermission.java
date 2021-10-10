package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.InventoryManager;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Rank;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdPermission extends SubCommand {
    public CmdPermission() {
        super("permission", new String[]{"perm"}, "Edit rank permissions");
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
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " <rank>"));
            return false;
        }

        Country country = CountryManager.getPlayerCountry(player);
        if (country.getRank(args[0]) == null) {
            player.sendMessage(Main.getMessages().getMessage("rankdoesntexist", "%rank%", args[0]));
            return false;
        }
        Rank rank = country.getRank(args[0]);

        InventoryManager.openPermissionInventory(player, country, rank);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        return null;
    }
}
