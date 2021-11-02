package fr.tt54.country.listener;

import fr.tt54.country.Main;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.RelationManager;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Rank;
import fr.tt54.country.objects.country.Relations;
import fr.tt54.country.objects.permissions.ClaimPermission;
import fr.tt54.country.objects.permissions.CountryPermission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().isEmpty()) {
            if (event.getView().getTitle().contains("'s §2permissions")) {
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                    if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1]) < 13) {

                        if (event.getCurrentItem().getType() == Material.getMaterial("STAINED_GLASS_PANE")) {
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
                            Country country = CountryManager.getPlayerCountry((Player) event.getWhoClicked());
                            if (country != null) {
                                Rank rank = CountryManager.getPlayerCountry((Player) event.getWhoClicked()).getRank(event.getView().getTitle().split("'")[0].substring(2));
                                for (ItemStack is : event.getClickedInventory().getContents()) {
                                    if (is != null && is.getType() == Material.getMaterial("STAINED_GLASS_PANE") && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                                        String name = is.getItemMeta().getDisplayName().substring(2);
                                        CountryPermission permission = CountryPermission.getPermission(name);
                                        if (is.getDurability() == 5 && !rank.hasPermission(permission)) {
                                            rank.addPermission(permission);
                                        } else if (is.getDurability() == 14 && rank.hasPermission(permission)) {
                                            rank.removePermission(permission);
                                        }
                                    }
                                }
                                CountryManager.saveCountry(country);
                                Main.getInstance().log(event.getWhoClicked().getName() + " edited " + country.getName() + "'s permissions");
                                event.getWhoClicked().sendMessage(Main.getMessages().getMessage("permissionsedited", "%rank%", rank.getName()));
                            }
                            event.getWhoClicked().closeInventory();
                        }
                    } else {
                        if (event.getCurrentItem().getType().name().contains("STAINED_GLASS_PANE")) {
                            if (event.getCurrentItem().getType() == Material.getMaterial("LIME_STAINED_GLASS_PANE")) {
                                event.getCurrentItem().setType(Material.getMaterial("RED_STAINED_GLASS_PANE"));
                            } else {
                                event.getCurrentItem().setType(Material.getMaterial("LIME_STAINED_GLASS_PANE"));
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
                            Country country = CountryManager.getPlayerCountry((Player) event.getWhoClicked());
                            if (country != null) {
                                Rank rank = CountryManager.getPlayerCountry((Player) event.getWhoClicked()).getRank(event.getView().getTitle().split("'")[0].substring(2));
                                for (ItemStack is : event.getClickedInventory().getContents()) {
                                    if (is != null && is.getType().name().contains("STAINED_GLASS_PANE") && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                                        String name = is.getItemMeta().getDisplayName().substring(2);
                                        CountryPermission permission = CountryPermission.getPermission(name);
                                        if (is.getType() == Material.getMaterial("LIME_STAINED_GLASS_PANE") && !rank.hasPermission(permission)) {
                                            rank.addPermission(permission);
                                        } else if (is.getType() == Material.getMaterial("RED_STAINED_GLASS_PANE") && rank.hasPermission(permission)) {
                                            rank.removePermission(permission);
                                        }
                                    }
                                }
                                CountryManager.saveCountry(country);
                                Main.getInstance().log(event.getWhoClicked().getName() + " edited " + country.getName() + "'s permissions");
                                event.getWhoClicked().sendMessage(Main.getMessages().getMessage("permissionsedited", "%rank%", rank.getName()));
                            }
                            event.getWhoClicked().closeInventory();
                        }

                    }
                    event.setCancelled(true);
                }
            } else if (event.getView().getTitle().contains("'s §2claim permissions")) {
                if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                    Country country = CountryManager.getPlayerCountry((Player) event.getWhoClicked());
                    if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1]) < 13) {
                        if (event.getCurrentItem().getType() == Material.getMaterial("STAINED_GLASS_PANE")) {
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
                            if (country != null) {
                                Relations relation = Relations.getRelation(event.getView().getTitle().split("'")[0].substring(2));
                                for (ItemStack is : event.getClickedInventory().getContents()) {
                                    if (is != null && is.getType() == Material.getMaterial("STAINED_GLASS_PANE") && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                                        String name = is.getItemMeta().getDisplayName().substring(2);
                                        ClaimPermission permission = ClaimPermission.getPermission(name);
                                        if (is.getDurability() == 5 && !country.hasRelationPermission(relation, permission)) {
                                            country.addRelationPermission(relation, permission);
                                        } else if (is.getDurability() == 14 && country.hasRelationPermission(relation, permission)) {
                                            country.removeRelationPermission(relation, permission);
                                        }
                                    }
                                }
                                RelationManager.saveRelations(country);
                                Main.getInstance().log(event.getWhoClicked().getName() + " edited " + country.getName() + "'s permissions");
                                event.getWhoClicked().sendMessage(Main.getMessages().getMessage("permissionsedited", "%rank%", relation.name().toLowerCase()));
                            }
                            event.getWhoClicked().closeInventory();
                        }
                    } else {
                        if (event.getCurrentItem().getType().name().contains("STAINED_GLASS_PANE")) {
                            if (event.getCurrentItem().getType() == Material.getMaterial("LIME_STAINED_GLASS_PANE")) {
                                event.getCurrentItem().setType(Material.getMaterial("RED_STAINED_GLASS_PANE"));
                            } else {
                                event.getCurrentItem().setType(Material.getMaterial("LIME_STAINED_GLASS_PANE"));
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
                            if (country != null) {
                                Relations relation = Relations.getRelation(event.getView().getTitle().split("'")[0].substring(2));
                                for (ItemStack is : event.getClickedInventory().getContents()) {
                                    if (is != null && is.getType().name().contains("STAINED_GLASS_PANE") && is.hasItemMeta() && is.getItemMeta().hasDisplayName()) {
                                        String name = is.getItemMeta().getDisplayName().substring(2);
                                        ClaimPermission permission = ClaimPermission.getPermission(name);
                                        if (is.getType() == Material.getMaterial("LIME_STAINED_GLASS_PANE") && !country.hasRelationPermission(relation, permission)) {
                                            country.addRelationPermission(relation, permission);
                                        } else if (is.getType() == Material.getMaterial("RED_STAINED_GLASS_PANE") && country.hasRelationPermission(relation, permission)) {
                                            country.removeRelationPermission(relation, permission);
                                        }
                                    }
                                }
                                RelationManager.saveRelations(country);
                                Main.getInstance().log(event.getWhoClicked().getName() + " edited " + country.getName() + "'s permissions");
                                event.getWhoClicked().sendMessage(Main.getMessages().getMessage("permissionsedited", "%rank%", relation.name().toLowerCase()));
                            }
                            event.getWhoClicked().closeInventory();
                        }

                    }
                    event.setCancelled(true);
                }
            }
        }
    }

}
