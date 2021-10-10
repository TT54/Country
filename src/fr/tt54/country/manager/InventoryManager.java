package fr.tt54.country.manager;

import fr.tt54.country.objects.country.Country;
import fr.tt54.country.objects.country.Rank;
import fr.tt54.country.objects.permissions.CountryPermission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryManager {

    public static void openPermissionInventory(Player player, Country country, Rank rank) {
        Inventory inv = Bukkit.createInventory(null, 6 * 9, "§2" + rank.getName() + "'s §2permissions");

        if (Integer.parseInt(Bukkit.getServer().getVersion().split("\\.")[1]) < 13) {
            List<CountryPermission> perms = Arrays.asList(CountryPermission.values());
            for (int i = 0; i < perms.size(); i++) {
                ItemStack item;
                if (rank.hasPermission(perms.get(i))) {
                    item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
                } else {
                    item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
                }
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(((rank.hasPermission(perms.get(i))) ? "§2" : "§4") + perms.get(i).name());

                List<String> lore = new ArrayList<>();
                String description = perms.get(i).getDescription();

                if (description.length() > 30) {
                    int lastchar = -1;
                    for (int j = 0; j < description.length(); j++) {
                        if ((description.charAt(j) == ' ' && j - lastchar > 30) || j + 1 == description.length()) {
                            lore.add("§7" + description.substring(lastchar + 1, j));
                            lastchar = j;
                        }
                    }
                } else {
                    lore.add("§7" + description);
                }

                meta.setLore(lore);
                item.setItemMeta(meta);
                inv.setItem(i * 2, item);
            }
            ItemStack saveButton = new ItemStack(Material.PAPER);
            ItemMeta meta = saveButton.getItemMeta();
            meta.setDisplayName("§6Save and quit");
            saveButton.setItemMeta(meta);
            inv.setItem(53, saveButton);

            ItemStack quitButton = new ItemStack(Material.BARRIER);
            meta = quitButton.getItemMeta();
            meta.setDisplayName("§4Quit without saving");
            quitButton.setItemMeta(meta);
            inv.setItem(5 * 9, quitButton);
        } else {
            //TODO
        }

        player.openInventory(inv);
    }


}
