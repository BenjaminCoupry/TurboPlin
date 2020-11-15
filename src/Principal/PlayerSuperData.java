package Principal;

import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
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
        double tequip = 0;
        if(p.getEquipment().getBoots() != null && p.getEquipment().getBoots().getType() == Material.LEATHER_BOOTS)
        {
            tequip += 3;
        }
        if(p.getEquipment().getLeggings()!= null && p.getEquipment().getLeggings().getType() == Material.LEATHER_LEGGINGS)
        {
            tequip += 6;
        }
        if(p.getEquipment().getChestplate()!= null && p.getEquipment().getChestplate().getType() == Material.LEATHER_CHESTPLATE)
        {
            tequip += 10;
        }
        if(p.getEquipment().getHelmet() != null && p.getEquipment().getHelmet().getType() == Material.LEATHER_HELMET)
        {
            tequip += 5;
        }
        return temperatureBiome+tempSol+tempProfondeur+tequip;
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
