package Principal;

import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerSuperData {
    static private int TailleMenu = 5;
    Player p;
    List<Material> Menu;

    public double getEau() {
        return eau;
    }

    double eau;
    public PlayerSuperData(Player p)
    {
        this.p = p;
        Menu = new ArrayList<>();
        eau = 100.0;
    }
    public double getTemperature()
    {
        double temperatureBiome = (100.0*(p.getWorld().getTemperature(p.getLocation().getBlockX(),p.getLocation().getBlockY(),p.getLocation().getBlockZ())-0.6))-10.0;
        double tempSol = getIntensiteSoleil();
        double tempProfondeur = 15-(25*(p.getLocation().getBlockY()-45)/205.0);
        double tequip = getTempEquipement();
        return temperatureBiome+tempSol+tempProfondeur+tequip;
    }
    public double getTempEquipement()
    {
        EntityEquipment e= p.getEquipment();

        double tequip = getTempTorches(e);
        tequip += getTempBottes(e);
        tequip += getTempLeggins(e);
        tequip += getTempChestplate(e);
        tequip += getTempHelmet(e);
        return tequip;
    }

    public double getTempTorches(EntityEquipment e)
    {
        double tequip = 0;
        if(e.getItemInMainHand() != null && e.getItemInMainHand().getType() == Material.TORCH)
        {
            tequip += 5;
        }
        if(e.getItemInOffHand() != null && e.getItemInOffHand().getType() == Material.TORCH)
        {
            tequip += 5;
        }
        return tequip;
    }

    public boolean hasLoreChaleur(ItemStack is)
    {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore())
        {
            return (is.getItemMeta().getLore().get(0).contains("Chaud"));

        }
        else
        {
            return false;
        }
    }
    public boolean hasLoreFroid(ItemStack is)
    {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasLore())
        {
            return (is.getItemMeta().getLore().get(0).contains("Froid"));

        }
        else
        {
            return false;
        }
    }
    public boolean isLeather(ItemStack is)
    {
        if(is!=null)
        {
            Material mat = is.getType();
            return mat==Material.LEATHER_BOOTS || mat == Material.LEATHER_CHESTPLATE || mat == Material.LEATHER_LEGGINGS
                    ||mat==Material.LEATHER_HELMET;
        }
        else
        {
            return false;
        }
    }

    public double getTempBottes(EntityEquipment e)
    {
        ItemStack is = e.getBoots();
        double tequip = 2;
        if(isLeather(is))
        {
            tequip += 2;
        }
        if(hasLoreFroid(is))
        {
            tequip -= 4;
        }
        if(hasLoreChaleur(is))
        {
            tequip += 4;
        }

        return tequip;
    }

    public double getTempLeggins(EntityEquipment e)
    {
        ItemStack is = e.getLeggings();
        double tequip = 3;
        if(isLeather(is))
        {
            tequip += 3;
        }
        if(hasLoreFroid(is))
        {
            tequip -= 6;
        }
        if(hasLoreChaleur(is))
        {
            tequip += 6;
        }

        return tequip;
    }

    public double getTempChestplate(EntityEquipment e)
    {
        ItemStack is = e.getChestplate();
        double tequip = 4;
        if(isLeather(is))
        {
            tequip += 4;
        }
        if(hasLoreFroid(is))
        {
            tequip -= 8;
        }
        if(hasLoreChaleur(is))
        {
            tequip += 8;
        }

        return tequip;
    }

    public double getTempHelmet(EntityEquipment e)
    {
        ItemStack is = e.getHelmet();
        double tequip = 3;
        if(isLeather(is))
        {
            tequip += 3;
        }
        if(hasLoreFroid(is))
        {
            tequip -= 6;
        }
        if(hasLoreChaleur(is))
        {
            tequip += 6;
        }

        return tequip;
    }

    public boolean estExposeAuCiel()
    {
        int maxY = p.getWorld().getHighestBlockAt(p.getLocation()).getY();
        int playerY = p.getLocation().getBlockY();
        return playerY>=maxY;
    }
    public boolean estSousPluie()
    {
        return estExposeAuCiel() && (p.getWorld().hasStorm() || p.getWorld().isThundering());
    }
    public double getIntensiteSoleil()
    {
        double temps = p.getWorld().getTime();
        if(temps>12000)
        {
            temps =12000;
        }
        double k = (double)temps/12000.0;
        double I = (1.0-Math.cos(k*2.0*Math.PI))/2.0;
        if(p.getWorld().hasStorm() || p.getWorld().isThundering())
        {
            I = I/2.0;
        }
        if(!estExposeAuCiel()) {
            I=I/2.0;
        }
        return I*30;
    }
    public void Manger(Material i)
    {
        Menu.add(i);
        if(i == Material.POTION) {
            eau = Math.min(50+eau,100.0);
        }
        else {
            if (Menu.size() > TailleMenu) {
                Menu.remove(0);
            }
        }
    }

    public double getVarieteAlimentaire()
    {
        List<Material> alimUniques = new ArrayList<>();
        for (Material m :Menu) {
            if(!alimUniques.contains(m))
            {
                alimUniques.add(m);
            }
        }
        return alimUniques.size()/(double)TailleMenu;
    }

    public void updateSoif()
    {
        double delta = Math.max(0.025,getTemperature()*0.025/9);
        eau = Math.max(0,eau - delta);
    }
}
