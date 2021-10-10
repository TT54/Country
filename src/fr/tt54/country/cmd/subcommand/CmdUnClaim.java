package fr.tt54.country.cmd.subcommand;

import fr.tt54.country.Main;
import fr.tt54.country.cmd.SubCommand;
import fr.tt54.country.manager.ClaimManager;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdUnClaim extends SubCommand {
    public CmdUnClaim() {
        super("unclaim", new String[]{}, "Unclaim a chunk");
    }

    @Override
    public boolean onExecute(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }
        Player player = (Player) sender;
        if (args.length != 0) {
            player.sendMessage(Main.getMessages().getBadUsageMessage("/country " + command));
            return false;
        }
        if (!CountryManager.hasCountry(player)) {
            player.sendMessage(Main.getMessages().getMessage("nocountry"));
            return false;
        }
        if (!Permission.hasCountryPermission(player, CountryPermission.CLAIM)) {
            player.sendMessage(Main.getMessages().getMessage("notcountrypermission"));
            return false;
        }

        if (ClaimManager.isInClaimedChunk(player.getLocation())) {
            if (ClaimManager.getClaimCountry(player.getLocation().getChunk()) != CountryManager.getPlayerCountry(player)) {
                player.sendMessage(Main.getMessages().getMessage("notyourclaim"));
                return false;
            }

            ClaimManager.unclaimChunk(CountryManager.getPlayerCountry(player), player.getLocation());
            player.sendMessage(Main.getMessages().getMessage("unclaimed"));
        } else {
            player.sendMessage(Main.getMessages().getMessage("notinclaim"));
            return false;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, SubCommand subCommand, String command, String[] args) {
        return null;
    }
}
