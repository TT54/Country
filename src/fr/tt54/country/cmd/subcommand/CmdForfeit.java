package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.WarManager;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class CmdForfeit extends SubCommand {

    public CmdForfeit() {
        super("forfeit", new String[]{}, "Forfeit a war with another country");
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
        Country country = CountryManager.getPlayerCountry(player);

        if (!Permission.hasCountryPermission(player, CountryPermission.MANAGE_RELATIONS)) {
            player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
            return false;
        }

        if (args.length != 1) {
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " <country>"));
            return false;
        }

        if (!CountryManager.existCountry(args[0])) {
            player.sendMessage(Main.getMessages().getMessage("countrynotexist", "%country%", args[0]));
            return false;
        }
        Country target = CountryManager.getCountry(args[0]);

        if (target == country) {
            player.sendMessage(Main.getMessages().getMessage("relationsyourself"));
            return false;
        }

        if (!WarManager.areInWar(country, target)) {
            player.sendMessage(Main.getMessages().getMessage("notinwar", "%country%", target.getName()));
            return false;
        }

        WarManager.onWarWin(target, WarManager.getActualWarBetween(country, target));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (args.length == 1) {
            return CountryManager.countriesMap.values().stream().map(Country::getName).filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        return null;
    }

}
