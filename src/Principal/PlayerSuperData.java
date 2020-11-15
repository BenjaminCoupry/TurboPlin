package Principal;

import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerSuperData {
    Player p;
    public PlayerSuperData(Player p)
    {
        this.p = p;
    }
    public double getTemperature()
    {
        double temperatureBiome = (100.0*p.getWorld().getTemperature(p.getLocation().getBlockX(),p.getLocation().getBlockY(),p.getLocation().getBlockZ()))-80.0;
        double tempSol = getIntensiteSoleil();
        double tempProfondeur = 50.0-(100*p.getLocation().getBlockY()/200.0);
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
}
