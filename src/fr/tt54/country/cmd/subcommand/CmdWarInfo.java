package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.WarManager;
import fr.tt54.country.objects.War;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Relations;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CmdWarInfo extends SubCommand {

    public CmdWarInfo() {
        super("warinfo", new String[]{"warsinfo", "wi"}, "Get informations about a war");
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
        War war = WarManager.getActualWarBetween(country, target);

        player.sendMessage("");
        player.sendMessage("§9---- War Info : §l" + war.getCountry1().getName() + " §9VS §l" + war.getCountry2().getName() + "§9 ----");
        player.sendMessage("§3" + war.getCountry1().getName() + "'s kills : §b" + war.getCountry1Kills());
        player.sendMessage("§3" + war.getCountry2().getName() + "'s kills : §b" + war.getCountry2Kills());
        long time = System.currentTimeMillis() - war.getTimeBegin();
        long day = TimeUnit.MILLISECONDS.toDays(time);
        long hours = TimeUnit.MILLISECONDS.toHours(time) - (day * 24);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - (hours * 60);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - (minutes * 60);
        player.sendMessage("§3Duration : §b"
                + ((day != 0) ? day + " day(s), " : "")
                + ((hours != 0) ? hours + " hour(s), " : "")
                + ((minutes != 0) ? minutes + " minutes(s), " : "")
                + ((seconds != 0) ? seconds + " seconds(s), " : ""));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (CountryManager.hasCountry(player)) {
                    Country country = CountryManager.getPlayerCountry(player);
                    return country.getRelations(Relations.IN_WAR).stream().map(uuid -> CountryManager.getCountry(uuid).getName()).filter(s -> s.toUpperCase().startsWith(args[0].toUpperCase())).collect(Collectors.toList());
                }
            }
        }
        return null;
    }
}
