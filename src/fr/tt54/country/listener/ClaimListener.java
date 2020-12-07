package fr.tt54.country.listener;

import fr.tt54.country.manager.ClaimManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ClaimListener implements Listener {

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        if (ClaimManager.isInClaimedChunk(event.getBlock().getLocation())) {
            event.getPlayer().sendMessage("§4COUCOU !");
            //TODO Empêcher les events
        }
    }

}
