package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.EconomyManager;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CmdBank extends SubCommand {

    public CmdBank() {
        super("bank", new String[0], "Manage your country's bank");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " deposit|view|withdraw"));
            return false;
        }

        if (!CountryManager.hasCountry(player)) {
            player.sendMessage(Main.getMessages().getMessage("nocountry"));
            return false;
        }
        Country country = CountryManager.getPlayerCountry(player);

        switch (args[0]) {
            case "deposit":
                if (!Permission.hasCountryPermission(player, CountryPermission.BANK_DEPOSIT)) {
                    player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
                    return false;
                }

                if (args.length != 2) {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " deposit <amount>"));
                    return false;
                }

                try {
                    double amount = Math.abs(Double.parseDouble(args[1]));
                    if (country.getMoney() + amount > country.getMaxMoney()) {
                        sender.sendMessage(Main.getMessages().getMessage("countrybankfull", "%money%", Main.getEconomy().format(amount)));
                        return false;
                    }

                    if (Main.getEconomy().has(player, amount)) {
                        EconomyManager.playerDeposit(player, country, amount);
                        sender.sendMessage(Main.getMessages().getMessage("moneydeposit", "%money%", Main.getEconomy().format(amount)));
                        return true;
                    } else {
                        sender.sendMessage(Main.getMessages().getMessage("donthavemoney", "%money%", Main.getEconomy().format(amount)));
                        return false;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " deposit <amount>"));
                    return false;
                }

            case "withdraw":
                if (!Permission.hasCountryPermission(player, CountryPermission.BANK_WITHDRAW)) {
                    player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
                    return false;
                }

                if (args.length != 2) {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " deposit <amount>"));
                    return false;
                }

                try {
                    double amount = Math.abs(Double.parseDouble(args[1]));
                    if (country.hasMoney(amount)) {
                        EconomyManager.playerWithdraw(player, country, amount);
                        sender.sendMessage(Main.getMessages().getMessage("moneywithdraw", "%money%", Main.getEconomy().format(amount)));
                        return true;
                    } else {
                        sender.sendMessage(Main.getMessages().getMessage("countrydoesnthavemoney", "%money%", Main.getEconomy().format(amount)));
                        return false;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " deposit <amount>"));
                    return false;
                }

            case "view":
                if (args.length != 1) {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " view"));
                    return false;
                }

                sender.sendMessage("§9§l---- " + country.getName() + "'s bank ----");
                sender.sendMessage("§9Current money : §b" + Main.getEconomy().format(country.getMoney()) + "/" + Main.getEconomy().format(country.getMaxMoney()));

                return true;

            default:
                sender.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command + " deposit|view|withdraw"));
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (args.length == 1) {
            return Stream.of("deposit", "view", "withdraw").filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        }
        return null;
    }
}
