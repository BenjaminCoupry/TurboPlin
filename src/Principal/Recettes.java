package Principal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Recettes {
    public static ShapedRecipe getRChainmailHelmet(Plugin p)
    {

        ItemStack item = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED+""+ChatColor.BOLD+"Casque Anti Acide");
        List<String> Lore = new ArrayList<>();
        Lore.add("");
        Lore.add(ChatColor.GOLD+""+ChatColor.ITALIC+"Protege de la pluie");
        meta.setLore(Lore);
        item.setItemMeta(meta);
        NamespacedKey key = new NamespacedKey(p,"Casque_Anti_Acide");
        ShapedRecipe recipe = new ShapedRecipe(key,item);
        recipe.shape("GDG","GIG","GLG");
        recipe.setIngredient('G',Material.GOLD_INGOT);
        recipe.setIngredient('D',Material.DIAMOND_HELMET);
        recipe.setIngredient('I',Material.IRON_HELMET);
        recipe.setIngredient('L',Material.LEATHER_HELMET);
        return recipe;
    }
    public static ShapedRecipe getNukeRecipe(Plugin p)
    {
        ItemStack item =getNuke();
        NamespacedKey key = new NamespacedKey(p,"Nuke");
        ShapedRecipe recipe = new ShapedRecipe(key,item);
        recipe.shape("DDD","DAD","DTD");
        recipe.setIngredient('D',Material.DIAMOND_BLOCK);
        recipe.setIngredient('A',Material.GHAST_TEAR);
        recipe.setIngredient('T',Material.TNT_MINECART);

        return recipe;
    }


    public static void ajouterRecettes(Plugin p)
    {
        Bukkit.addRecipe(getRChainmailHelmet(p));
        Bukkit.addRecipe(getNukeRecipe(p));
    }

    private static ItemStack getNuke()
    {
        ItemStack nuke = new ItemStack(Material.ARROW);
        ItemMeta meta = nuke.getItemMeta();
        meta.setDisplayName(ChatColor.RED+""+ChatColor.BOLD+"Nuke !");
        List<String> Lore = new ArrayList<>();
        Lore.add("");
        Lore.add(ChatColor.GOLD+""+ChatColor.ITALIC+"DESTRUCTION");
        meta.setLore(Lore);
        nuke.setItemMeta(meta);
        return nuke;

    }
    public static boolean isNuke(ItemStack I)
    {
        if(I.getType() == Material.ARROW)
        {
            if(I.getItemMeta().getDisplayName().contains("Nuke !"))
            {
                if(I.getItemMeta().hasLore())
                {
                    return true;
                }
            }
        }
        return false;
    }
}
