package Principal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public class TempCalculator implements Serializable {
    public void setP(Player p) {
        this.p = p;
    }

    Player p;
    static private final int RayonTemperature = 2;

    public TempCalculator(Player p) {
        this.p = p;
    }

    public double calcTemperature()
    {
        double temperatureBiome = getTempBiome();
        double tempSol = getIntensiteSoleil();
        double tempProfondeur = getTempProfondeur();
        double tequip = getTempEquipement();
        double envir = getTempObjEnvir();
        double course = getTempCourrir();
        return temperatureBiome+tempSol+tempProfondeur+tequip+envir+course;
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

    public double getIntensiteSoleil()
    {
        double temps = p.getWorld().getTime();
        temps = (temps + 776)%23992;
        if(temps>13562)
        {
            temps =13562;
        }
        double k = (double)temps/13562.0;
        double I = (1.0-Math.cos(k*2.0*Math.PI))/2.0;
        if(p.getWorld().hasStorm() || p.getWorld().isThundering())
        {
            I = I/2.0;
        }
        if(!PlayerSuperData.estExposeAuCiel(p)) {
            I=I/2.0;
        }
        return I*25;
    }
    //Temperature
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

    public double getTempProfondeur()
    {
        double z= p.getLocation().getBlockY();
        double delta = z-45;
        return delta*(-0.2);
    }

    public double getTempBiome()
    {
        double tmin = -20;
        double tmax = 47;
        double tempbrute= p.getWorld().getTemperature(p.getLocation().getBlockX(),p.getLocation().getBlockY(),
                p.getLocation().getBlockZ());
        double k = (tempbrute+0.5)/2.5;
        double temperatureBiome = tmin+k*(tmax-tmin);
        return temperatureBiome;
    }

    public double getTempCourrir()
    {
        if(p.isSprinting())
        {
            return 5;
        }
        return 0;
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

    public double getTempBottes(EntityEquipment e)
    {
        ItemStack is = e.getBoots();
        double tequip =0;
        if(is != null) {
            tequip += 2;
        }
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
        double tequip =0;
        if(is != null) {
            tequip += 3;
        }
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
        double tequip =0;
        if(is != null) {
            tequip += 4;
        }
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
        double tequip =0;
        if(is != null) {
            tequip += 3;
        }
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

    public double getTempBloc(Block b)
    {
        if(b.getType() == Material.SNOW)
        {
            return -1;
        }
        if(b.getType() == Material.SNOW_BLOCK)
        {
            return -2;
        }
        if(b.getType() == Material.ICE)
        {
            return -3;
        }
        if(b.getType() == Material.PACKED_ICE)
        {
            return -4;
        }
        if(b.getType() == Material.FROSTED_ICE)
        {
            return -4;
        }
        if(b.getType() == Material.WATER)
        {
            return -0.11;
        }
        if(b.getType() == Material.BLUE_ICE)
        {
            return -5;
        }
        if(b.getType() == Material.CAMPFIRE)
        {
            return +10;
        }
        if(b.getType() == Material.FIRE)
        {
            return +13;
        }
        if(b.getType() == Material.TORCH)
        {
            return +2;
        }
        if(b.getType() == Material.LAVA)
        {
            return +20;
        }
        return 0;
    }

    public double getTempObjEnvir()
    {
        double t =0;
        int R = RayonTemperature;
        Location l = p.getLocation();
        for(int i = -R;i<=R;i++)
        {
            for(int j = -R;j<=R;j++)
            {
                for(int k = -R/2;k<=R/2;k++)
                {
                    Location l0 = new Location(p.getWorld(),l.getX()+i,l.getY()+k,l.getZ()+j);
                    Block b = p.getWorld().getBlockAt(l0);
                    t+= getTempBloc(b);
                }
            }
        }
        return t;
    }




}
