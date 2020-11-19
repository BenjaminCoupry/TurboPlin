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

    //Recettes
    public static ShapedRecipe getRChainmailHelmetRecipe(Plugin p)
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

    public static ShapedRecipe getEquipTempRecipe(Plugin p,String temp,Material concerne)
    {
        Material modifTemp =getModifTemp(temp);
        ItemStack item = new ItemStack(concerne);
        ItemMeta meta = item.getItemMeta();
        List<String> Lore = new ArrayList<>();
        String couleur = "";
        if(temp.equals("Chaud"))
        {
            couleur=ChatColor.RED+"";
        } else if (temp.equals("Froid"))
        {
            couleur = ChatColor.BLUE+"";
        }
        Lore.add(couleur+ChatColor.ITALIC+temp);
        meta.setLore(Lore);
        item.setItemMeta(meta);
        NamespacedKey key = new NamespacedKey(p,concerne.toString()+temp);
        ShapedRecipe recipe = new ShapedRecipe(key,item);
        recipe.shape("LLL","LEL","LLL");
        recipe.setIngredient('L',modifTemp);
        recipe.setIngredient('E',concerne);

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

    public static ShapedRecipe getPetardRecipe(Plugin p)
    {
        ItemStack item =new ItemStack(Material.FLINT);
        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.YELLOW+"Petard");
        List<String> lore = new ArrayList<>();
        lore.add("Petard");
        im.setLore(lore);
        item.setItemMeta(im);
        NamespacedKey key = new NamespacedKey(p,"petard");
        ShapedRecipe recipe = new ShapedRecipe(key,item);
        recipe.shape("FPF","FPF","DSD");
        recipe.setIngredient('F',Material.FLINT);
        recipe.setIngredient('P',Material.GUNPOWDER);
        recipe.setIngredient('D',Material.FLINT_AND_STEEL);
        recipe.setIngredient('S',Material.BUCKET);

        return recipe;
    }

    //Ajouts
    public static void ajouterRecettesThermiques(Plugin p,Material concerne)
    {
        Bukkit.addRecipe(getEquipTempRecipe(p,"Chaud",concerne));
        Bukkit.addRecipe(getEquipTempRecipe(p,"Froid",concerne));
    }

    public static void ajouterRecettesThermiques(Plugin p)
    {
        ajouterRecettesThermiques(p,Material.LEATHER_HELMET);
        ajouterRecettesThermiques(p,Material.IRON_HELMET);
        ajouterRecettesThermiques(p,Material.DIAMOND_HELMET);
        ajouterRecettesThermiques(p,Material.GOLDEN_HELMET);

        ajouterRecettesThermiques(p,Material.LEATHER_CHESTPLATE);
        ajouterRecettesThermiques(p,Material.IRON_CHESTPLATE);
        ajouterRecettesThermiques(p,Material.DIAMOND_CHESTPLATE);
        ajouterRecettesThermiques(p,Material.GOLDEN_CHESTPLATE);

        ajouterRecettesThermiques(p,Material.LEATHER_LEGGINGS);
        ajouterRecettesThermiques(p,Material.IRON_LEGGINGS);
        ajouterRecettesThermiques(p,Material.DIAMOND_LEGGINGS);
        ajouterRecettesThermiques(p,Material.GOLDEN_LEGGINGS);

        ajouterRecettesThermiques(p,Material.LEATHER_BOOTS);
        ajouterRecettesThermiques(p,Material.IRON_BOOTS);
        ajouterRecettesThermiques(p,Material.DIAMOND_BOOTS);
        ajouterRecettesThermiques(p,Material.GOLDEN_BOOTS);

    }

    public static void ajouterRecettes(Plugin p)
    {
        Bukkit.addRecipe(getRChainmailHelmetRecipe(p));
        Bukkit.addRecipe(getNukeRecipe(p));
        Bukkit.addRecipe(getPetardRecipe(p));
        ajouterRecettesThermiques(p);
    }


    //Utils
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
    public static boolean isPetard(ItemStack I)
    {
        if(I != null) {
            if (I.getType() == Material.FLINT) {
                if (I.getItemMeta().getDisplayName().contains("Petard")) {
                    if (I.getItemMeta().hasLore()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Material getModifTemp(String mod)
    {
        if(mod.equals( "Chaud"))
        {
            return Material.LEATHER;
        }
        else if(mod.equals("Froid"))
        {
            return Material.FEATHER;
        }
        else
        {
            return null;
        }
    }
}
