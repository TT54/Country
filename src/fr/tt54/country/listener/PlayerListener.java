package fr.tt54.country.listener;

import fr.tt54.country.Main;
import fr.tt54.country.manager.CountryManager;
import fr.tt54.country.manager.WarManager;
import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Relations;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        if (Main.getInstance().getConfig().getBoolean("enablechat")) {
            String format = Main.getInstance().getConfig().getString("chatformat");
            if (format == null) {
                format = "&2[%country_name%] %player% : &f%message%";
            }
            event.getRecipients().clear();
            format = this.playerSendMessage(event.getPlayer(), new ArrayList<>(Bukkit.getOnlinePlayers()), format, event.getMessage());
            event.setFormat(format);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player entity = (Player) event.getEntity();

            if (CountryManager.hasCountry(damager) && CountryManager.hasCountry(entity)) {
                Country dam = CountryManager.getPlayerCountry(damager);
                Country ent = CountryManager.getPlayerCountry(entity);
                if (ent == dam || dam.getRelationWith(ent.getUuid()) == Relations.ALLY || dam.getRelationWith(ent.getUuid()) == Relations.ENTENTE) {
                    event.getDamager().sendMessage(Main.getMessages().getMessage("hitfriends"));
                    event.setCancelled(true);
                } else if (entity.getHealth() - event.getFinalDamage() <= 0) {
                    if (WarManager.areInWar(dam, ent)) {
                        Main.getInstance().log(dam.getName() + " make a kill against " + ent.getName() + " during their war");
                        WarManager.addKillForWar(dam, ent);
                    }
                }
            }
        }
    }

    private String playerSendMessage(Player player, List<Player> players, String format, String message) {
        String send = format.replace("&", "§").replace("%country_name%", CountryManager.getCountryName(player)).replace("%player%", player.getDisplayName()).replace("%message%", message);
        for (Player player1 : players) {
            String color = "";
            if (CountryManager.getPlayerCountry(player1) != null && CountryManager.getPlayerCountry(player) != null) {
                color = (CountryManager.getPlayerCountry(player1).equals(CountryManager.getPlayerCountry(player))) ? "§2" : "";
            }
            player1.sendMessage(send.replace("%relation_color%", color));
        }
        return send.replace("%country_name%", CountryManager.getCountryName(player)).replace("%relation_color%", "");
    }
}
