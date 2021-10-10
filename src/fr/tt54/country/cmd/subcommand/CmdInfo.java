package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Rank;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class CmdInfo extends SubCommand {
    public CmdInfo() {
        super("info", new String[]{"i"}, "Show country's informations");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (args.length == 0) {
            //TODO Afficher infos du pays du joueur
            if (!(sender instanceof Player)) {
                sender.sendMessage(Main.getMessages().getMessage("notplayer"));
                return false;
            }
            Player player = (Player) sender;
            if (!CountryManager.hasCountry(player)) {
                sender.sendMessage(Main.getMessages().getMessage("nocountry"));
                return false;
            }
            sendInfosMessage(CountryManager.getPlayerCountry(player), player);

            return true;
        } else if (args.length == 1) {
            if (!CountryManager.existCountry(args[0])) {
                sender.sendMessage(Main.getMessages().getMessage("countrynotexist", "%country%", args[0]));
                return false;
            }
            sendInfosMessage(CountryManager.getCountry(args[0]), sender);
            return true;
        }
        sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " [country]"));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (args.length == 1) {
            return CountryManager.countriesMap.values().stream().map(Country::getName).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        return null;
    }

    private static void sendInfosMessage(Country country, CommandSender sender) {
        sender.sendMessage("§2---- [INFOS] ----");
        sender.sendMessage("");
        sender.sendMessage("§aName: §2" + country.getName());
        sender.sendMessage("§aUUID: §2" + country.getUuid().toString());
        sender.sendMessage("§aLevel: §2" + country.getLevel());
        sender.sendMessage("§aClaims: §2" + country.getChunksClaimed().size() + "/" + country.getMaxClaims());
        List<OfflinePlayer> playersList = new ArrayList<>(country.getMembers().keySet());
        List<String> membersList = new ArrayList<>();
        Collections.sort(playersList, (p1, p2) -> CountryManager.getRank(p2).getPower() - CountryManager.getRank(p1).getPower());
        playersList.forEach(player -> membersList.add(
                ((CountryManager.getRank(player).getPrefix() != null && !CountryManager.getRank(player).getPrefix().isEmpty())
                        ? CountryManager.getRank(player).getPrefix().replace("&", "§") + " "
                        : "")
                        + player.getName() + "§2"));
        String members = Arrays.toString(membersList.toArray());
        members = members.substring(1, members.length() - 1);
        sender.sendMessage("§aMembers: §2" + members);
        sender.sendMessage("§aRanks: §2" + Arrays.toString(country.getRanks().stream().sorted(Comparator.comparingInt(Rank::getPower)).map(Rank::getName).toArray()));
        sender.sendMessage("");
        sender.sendMessage("§2---- [INFOS] ----");
    }
}
