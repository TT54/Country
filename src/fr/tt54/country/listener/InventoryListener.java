package fr.tt54.country.listener;

import fr.tt54.country.Main;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.country.Rank;
import fr.tt54.country.objects.permissions.CountryPermission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != null && event.getInventory().getName() != null && !event.getInventory().getName().isEmpty()) {
            if (event.getInventory().getName().contains("'s §2permissions")) {
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                    if (event.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
                        if (event.getCurrentItem().getDurability() == 5) {
                            event.getCurrentItem().setDurability((short) 14);
                        } else {
                            event.getCurrentItem().setDurability((short) 5);
                        }
                    } else if (event.getCurrentItem().getType() == Material.BARRIER
                            && event.getCurrentItem().hasItemMeta()
                            && event.getCurrentItem().getItemMeta().hasDisplayName()
                            && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§4Quit without saving")) {
                        event.getWhoClicked().closeInventory();
                    } else if (event.getCurrentItem().getType() == Material.PAPER
                            && event.getCurrentItem().hasItemMeta()
                            && event.getCurrentItem().getItemMeta().hasDisplayName()
                            && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§6Save and quit")) {
                        if (CountryManager.getPlayerCountry((Player) event.getWhoClicked()) != null) {
                            Rank rank = CountryManager.getPlayerCountry((Player) event.getWhoClicked()).getRank(event.getInventory().getName().split("'")[0].substring(2));
                            for (ItemStack is : event.getClickedInventory().getContents()) {
                                if (is != null && is.getType() == Material.STAINED_GLASS_PANE && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                                    String name = is.getItemMeta().getDisplayName().substring(2);
                                    CountryPermission permission = CountryPermission.getPermission(name);
                                    if (is.getDurability() == 5 && !rank.hasPermission(permission)) {
                                        rank.addPermission(permission);
                                    } else if (is.getDurability() == 14 && rank.hasPermission(permission)) {
                                        rank.removePermission(permission);
                                    }
                                }
                            }
                            CountryManager.saveCountry(CountryManager.getPlayerCountry((Player) event.getWhoClicked()));
                            event.getWhoClicked().sendMessage(Main.getMessages().getMessage("permissionsedited", "%rank%", rank.getName()));
                        }
                        event.getWhoClicked().closeInventory();
                    }
                }
                event.setCancelled(true);
            }
        }
    }

}
