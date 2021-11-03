package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Rank;
import fr.tt54.country.objects.country.Relations;
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

    protected static void sendInfosMessage(Country country, CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage("§9---- [INFOS] ----");
        sender.sendMessage("§3Name: §b" + country.getName());
        sender.sendMessage("§8UUID: §7" + country.getUuid().toString());
        sender.sendMessage("§3Level: §b" + country.getLevel() + " (" + country.getLevelPoints() + "§3/" + CountryManager.getPointsToNextLevel(country.getLevel()) + ")");
        sender.sendMessage("§3Claims: §b" + country.getChunksClaimed().size() + "§3/" + country.getMaxClaims());
        String allies = country.getRelations(Relations.ALLY).stream().map(uuid -> CountryManager.getCountry(uuid).getName()).collect(Collectors.toList()).toString();
        sender.sendMessage("§5Ally: §d" + allies.substring(1, allies.length() - 1));
        String ententes = country.getRelations(Relations.ENTENTE).stream().map(uuid -> CountryManager.getCountry(uuid).getName()).collect(Collectors.toList()).toString();
        sender.sendMessage("§6Entente: §e" + ententes.substring(1, ententes.length() - 1));
        String wars = country.getRelations(Relations.IN_WAR).stream().map(uuid -> CountryManager.getCountry(uuid).getName()).collect(Collectors.toList()).toString();
        sender.sendMessage("§4Wars: §c" + wars.substring(1, wars.length() - 1));
        List<OfflinePlayer> playersList = new ArrayList<>(country.getMembers().keySet());
        List<String> membersList = new ArrayList<>();
        Collections.sort(playersList, (p1, p2) -> CountryManager.getRank(p2).getPower() - CountryManager.getRank(p1).getPower());
        playersList.forEach(player -> membersList.add(
                ((CountryManager.getRank(player).getPrefix() != null && !CountryManager.getRank(player).getPrefix().isEmpty())
                        ? CountryManager.getRank(player).getPrefix().replace("&", "§") + " "
                        : "")
                        + ((player.isOnline()) ? "§2" : "§c") + player.getName() + "§7"));
        String members = Arrays.toString(membersList.toArray());
        members = members.substring(1, members.length() - 1);
        sender.sendMessage("§3Members: §7" + members);
        sender.sendMessage("§3Ranks: §b" + Arrays.toString(country.getRanks().stream().sorted(Comparator.comparingInt(Rank::getPower)).map(Rank::getName).toArray()));
/*        sender.sendMessage("");
        sender.sendMessage("§2---- [INFOS] ----");*/
    }
}
