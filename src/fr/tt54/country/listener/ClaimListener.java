package fr.tt54.country.listener;

import fr.tt54.country.Main;
import fr.tt54.country.manager.ClaimManager;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.Claim;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.permissions.CountryPermission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ClaimListener implements Listener {

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        if (ClaimManager.isInClaimedChunk(event.getBlock().getLocation())) {
            Claim claim = ClaimManager.getClaim(event.getBlock().getLocation().getChunk());
            Country country = CountryManager.getPlayerCountry(event.getPlayer());
            if (country == claim.getOwner()) {
                if (CountryManager.getRank(event.getPlayer()).hasPermission(CountryPermission.BUILD))
                    return;
            }
            event.setCancelled(true);
            event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
        }
    }

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event) {
        if (ClaimManager.isInClaimedChunk(event.getBlock().getLocation())) {
            Claim claim = ClaimManager.getClaim(event.getBlock().getLocation().getChunk());
            Country country = CountryManager.getPlayerCountry(event.getPlayer());
            if (country == claim.getOwner()) {
                if (CountryManager.getRank(event.getPlayer()).hasPermission(CountryPermission.BUILD))
                    return;
            }
            event.setCancelled(true);
            event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Country country = CountryManager.getPlayerCountry(event.getPlayer());
        Claim claimTo = null;
        Claim claimFrom = null;

        if (ClaimManager.isInClaimedChunk(event.getTo())) {
            claimTo = ClaimManager.getClaim(event.getTo().getChunk());
        }
        if (ClaimManager.isInClaimedChunk(event.getFrom())) {
            claimFrom = ClaimManager.getClaim(event.getFrom().getChunk());
        }

        if (claimFrom == null && claimTo == null) {
            return;
        }

        if (claimFrom != null && claimTo == null) {
            event.getPlayer().sendMessage(Main.getMessages().getMessage("leaveclaim", "%country%", claimFrom.getOwner().getName()));
            return;
        }

        if (claimFrom == null && claimTo != null) {
            event.getPlayer().sendMessage(Main.getMessages().getMessage("joinclaim", "%country%", claimTo.getOwner().getName()));
            return;
        }

        if (claimFrom.getOwner() != claimTo.getOwner()) {
            event.getPlayer().sendMessage(Main.getMessages().getMessage("moveclaim", "%countryFrom%", claimFrom.getOwner().getName(), "%countryTo%", claimTo.getOwner().getName()));
        }
    }

}
