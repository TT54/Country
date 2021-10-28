package fr.tt54.country.listener;

import fr.tt54.country.Main;
import fr.tt54.country.manager.ClaimManager;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.objects.country.Claim;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.permissions.ClaimPermission;
import fr.tt54.country.objects.permissions.CountryPermission;
import fr.tt54.country.utils.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.InventoryHolder;

public class ClaimListener implements Listener {

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        if (ClaimManager.isInClaimedChunk(event.getBlock().getLocation())) {
            Claim claim = ClaimManager.getClaim(event.getBlock().getLocation().getChunk());
            if (ClaimManager.hasPermission(event.getPlayer(), claim, ClaimPermission.BUILD, CountryPermission.BUILD))
                return;
            event.setCancelled(true);
            event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (ClaimManager.isInClaimedChunk(event.getClickedBlock().getLocation())) {
                Claim claim = ClaimManager.getClaim(event.getClickedBlock().getLocation().getChunk());

                if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1]) < 13) {
                    if (event.getClickedBlock().getType() == Material.STONE_BUTTON ||
                            event.getClickedBlock().getType() == MaterialUtils.getMaterial(MaterialUtils.BUTTON) ||
                            event.getClickedBlock().getType() == Material.LEVER) {
                        if (ClaimManager.hasPermission(event.getPlayer(), claim, ClaimPermission.USE_BUTTON, CountryPermission.USE_BUTTON))
                            return;
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
                    } else if (event.getClickedBlock().getType() == Material.DARK_OAK_DOOR ||
                            event.getClickedBlock().getType() == Material.ACACIA_DOOR ||
                            event.getClickedBlock().getType() == Material.BIRCH_DOOR ||
                            event.getClickedBlock().getType() == Material.JUNGLE_DOOR ||
                            event.getClickedBlock().getType() == Material.SPRUCE_DOOR ||
                            event.getClickedBlock().getType() == Material.getMaterial("ACACIA_FENCE_GATE") ||
                            event.getClickedBlock().getType() == Material.getMaterial("BIRCH_FENCE_GATE") ||
                            event.getClickedBlock().getType() == Material.getMaterial("JUNGLE_FENCE_GATE") ||
                            event.getClickedBlock().getType() == Material.getMaterial("DARK_OAK_FENCE_GATE") ||
                            event.getClickedBlock().getType() == Material.getMaterial("SPRUCE_FENCE_GATE") ||
                            event.getClickedBlock().getType() == MaterialUtils.getMaterial(MaterialUtils.FENCE_GATE) ||
                            event.getClickedBlock().getType() == MaterialUtils.getMaterial(MaterialUtils.TRAPDOOR) ||
                            event.getClickedBlock().getType() == MaterialUtils.getMaterial(MaterialUtils.WOODEN_DOOR)) {
                        if (ClaimManager.hasPermission(event.getPlayer(), claim, ClaimPermission.OPEN_DOOR, CountryPermission.OPEN_DOOR))
                            return;
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
                    } else if (event.getItem() != null &&
                            (event.getItem().getType() == Material.FLINT_AND_STEEL ||
                                    event.getItem().getType() == Material.LAVA_BUCKET ||
                                    event.getItem().getType() == Material.WATER_BUCKET ||
                                    event.getItem().getType() == MaterialUtils.getMaterial(MaterialUtils.FIREBALL))) {
                        if (ClaimManager.hasPermission(event.getPlayer(), claim, ClaimPermission.INTERACT, CountryPermission.INTERACT))
                            return;
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
                    } else if (event.getClickedBlock().getState() instanceof InventoryHolder) {
                        if (ClaimManager.hasPermission(event.getPlayer(), claim, ClaimPermission.OPEN_CHEST, CountryPermission.OPEN_CHEST))
                            return;
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
                    }
                } else {
                    if (event.getClickedBlock().getType() == Material.STONE_BUTTON ||
                            event.getClickedBlock().getType() == Material.getMaterial("BIRCH_BUTTON") ||
                            event.getClickedBlock().getType() == Material.getMaterial("ACACIA_BUTTON") ||
                            event.getClickedBlock().getType() == Material.getMaterial("CRIMSON_BUTTON") ||
                            event.getClickedBlock().getType() == Material.getMaterial("DARK_OAK_BUTTON") ||
                            event.getClickedBlock().getType() == Material.getMaterial("JUNGLE_BUTTON") ||
                            event.getClickedBlock().getType() == Material.getMaterial("POLISHED_BLACKSTONE_BUTTON") ||
                            event.getClickedBlock().getType() == Material.getMaterial("SPRUCE_BUTTON") ||
                            event.getClickedBlock().getType() == Material.getMaterial("WARPED_BUTTON") ||
                            event.getClickedBlock().getType() == MaterialUtils.getMaterial(MaterialUtils.BUTTON) ||
                            event.getClickedBlock().getType() == Material.LEVER) {
                        if (ClaimManager.hasPermission(event.getPlayer(), claim, ClaimPermission.USE_BUTTON, CountryPermission.USE_BUTTON))
                            return;
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
                    } else if (event.getClickedBlock().getType() == Material.DARK_OAK_DOOR ||
                            event.getClickedBlock().getType() == Material.ACACIA_DOOR ||
                            event.getClickedBlock().getType() == Material.BIRCH_DOOR ||
                            event.getClickedBlock().getType() == Material.JUNGLE_DOOR ||
                            event.getClickedBlock().getType() == Material.SPRUCE_DOOR ||
                            event.getClickedBlock().getType() == Material.getMaterial("ACACIA_FENCE_GATE") ||
                            event.getClickedBlock().getType() == Material.getMaterial("BIRCH_FENCE_GATE") ||
                            event.getClickedBlock().getType() == Material.getMaterial("JUNGLE_FENCE_GATE") ||
                            event.getClickedBlock().getType() == Material.getMaterial("DARK_OAK_FENCE_GATE") ||
                            event.getClickedBlock().getType() == Material.getMaterial("SPRUCE_FENCE_GATE") ||
                            event.getClickedBlock().getType() == Material.getMaterial("CRIMSON_FENCE_GATE") ||
                            event.getClickedBlock().getType() == Material.getMaterial("WARPED_FENCE_GATE") ||
                            event.getClickedBlock().getType() == MaterialUtils.getMaterial(MaterialUtils.TRAPDOOR) ||
                            event.getClickedBlock().getType() == Material.getMaterial("CRIMSON_DOOR") ||
                            event.getClickedBlock().getType() == Material.getMaterial("WARPED_DOOR") ||
                            event.getClickedBlock().getType() == Material.getMaterial("ACACIA_TRAPDOOR") ||
                            event.getClickedBlock().getType() == Material.getMaterial("BIRCH_TRAPDOOR") ||
                            event.getClickedBlock().getType() == Material.getMaterial("CRIMSON_TRAPDOOR") ||
                            event.getClickedBlock().getType() == Material.getMaterial("DARK_OAK_TRAPDOOR") ||
                            event.getClickedBlock().getType() == Material.getMaterial("JUNGLE_TRAPDOOR") ||
                            event.getClickedBlock().getType() == Material.getMaterial("SPRUCE_TRAPDOOR") ||
                            event.getClickedBlock().getType() == Material.getMaterial("WARPED_TRAPDOOR") ||
                            event.getClickedBlock().getType() == MaterialUtils.getMaterial(MaterialUtils.FENCE_GATE) ||
                            event.getClickedBlock().getType() == MaterialUtils.getMaterial(MaterialUtils.WOODEN_DOOR)) {
                        if (ClaimManager.hasPermission(event.getPlayer(), claim, ClaimPermission.OPEN_DOOR, CountryPermission.OPEN_DOOR))
                            return;
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
                    } else if (event.getItem() != null &&
                            (event.getItem().getType() == Material.FLINT_AND_STEEL ||
                                    event.getItem().getType() == Material.LAVA_BUCKET ||
                                    event.getItem().getType() == Material.WATER_BUCKET ||

                                    event.getItem().getType() == MaterialUtils.getMaterial(MaterialUtils.FIREBALL))) {
                        if (ClaimManager.hasPermission(event.getPlayer(), claim, ClaimPermission.INTERACT, CountryPermission.INTERACT))
                            return;
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
                    } else if (event.getClickedBlock().getState() instanceof InventoryHolder) {
                        if (ClaimManager.hasPermission(event.getPlayer(), claim, ClaimPermission.OPEN_CHEST, CountryPermission.OPEN_CHEST))
                            return;
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPlace(BlockPlaceEvent event) {
        if (ClaimManager.isInClaimedChunk(event.getBlock().getLocation())) {
            Claim claim = ClaimManager.getClaim(event.getBlock().getLocation().getChunk());
            if (ClaimManager.hasPermission(event.getPlayer(), claim, ClaimPermission.BUILD, CountryPermission.BUILD))
                return;
            event.setCancelled(true);
            event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
        }
    }

    /*@EventHandler
    public void onPlayerOpenInventory(InventoryOpenEvent event) {
        if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1]) > 8) {
            Inventory inv = event.getInventory();
            if (inv != null && inv.getLocation() != null) {
                if (ClaimManager.isInClaimedChunk(inv.getLocation())) {
                    Claim claim = ClaimManager.getClaim(inv.getLocation().getChunk());

                    if (ClaimManager.hasPermission((Player) event.getPlayer(), claim, ClaimPermission.OPEN_CHEST, CountryPermission.OPEN_CHEST))
                        return;

                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Main.getMessages().getMessage("cantdoinclaim", "%country%", claim.getOwner().getName()));
                }
            }
        }
    }*/

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (Main.getInstance().getConfig().getBoolean("claimmessage")) {
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
                Main.getInstance().log(event.getPlayer().getName() + " left " + claimFrom.getOwner().getName() + "'s claim");
                return;
            }

            if (claimFrom == null && claimTo != null) {
                event.getPlayer().sendMessage(Main.getMessages().getMessage("joinclaim", "%country%", claimTo.getOwner().getName()));
                Main.getInstance().log(event.getPlayer().getName() + " entered " + claimTo.getOwner().getName() + "'s claim");
                return;
            }

            if (claimFrom.getOwner() != claimTo.getOwner()) {
                event.getPlayer().sendMessage(Main.getMessages().getMessage("moveclaim", "%countryFrom%", claimFrom.getOwner().getName(), "%countryTo%", claimTo.getOwner().getName()));
                Main.getInstance().log(event.getPlayer().getName() + " left " + claimFrom.getOwner().getName() + "'s claim and joined " + claimTo.getOwner().getName() + "'s claim");
            }
        }
    }
}
