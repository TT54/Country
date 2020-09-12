package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.InviteManager;
import fr.tt54.country.objects.country.Country;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CmdJoin extends SubCommand {

    public CmdJoin() {
        super("join", new String[]{}, "Join a country", "country.join");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " <country>"));
            return false;
        }
        if (!CountryManager.existCountry(args[0])) {
            player.sendMessage(Main.getMessages().getMessage("countrynotexist", "%country%", args[0]));
            return false;
        }
        Country country = CountryManager.getCountry(args[0]);
        if (CountryManager.hasCountry(player)) {
            player.sendMessage(Main.getMessages().getMessage("alreadyincountry", "%country%", CountryManager.getCountryName(player)));
            return false;
        }
        if (country == null) {
            player.sendMessage(Main.getMessages().getMessage("countrynotexist", "%country%", args[0]));
            return false;
        }
        if (!country.isOpened() && !InviteManager.isInvited(player, country.getUuid())) {
            player.sendMessage(Main.getMessages().getMessage("notinvited", "%country%", args[0]));
            return false;
        }
        CountryManager.joinCountry(player, country.getUuid());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        List<String> message = new ArrayList<>();
        if (args.length == 0) {
            message.addAll(CountryManager.countriesMap.values().stream().map(Country::getName).filter(name -> name.startsWith(args[0])).collect(Collectors.toList()));
        }
        return message;
    }
}
