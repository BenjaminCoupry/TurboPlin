package Principal;

import net.minecraft.server.v1_16_R2.WorldGenFeatureOceanRuin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Campfire;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerSuperData implements Serializable {

    public void setP(Player p) {
        this.p = p;
    }

    static private double inertieThermique = 0.97;
    static private double regainEnergie = 0.5;
    static private double regainEnergieManger = 20;
    static private int TailleMenu = 7;
    static private int RayonTemperature = 2;
    transient Player p;
    String[] statuts;
    double varieteAlimentaire;
    double temperature;
    List<Material> Menu;
    double eau;
    long cooldown;
    double fatigue;
    private double lastExhaustion;


    public PlayerSuperData(Player p)
    {
        cooldown =System.currentTimeMillis();
        this.p = p;
        Menu = new ArrayList<>();
        temperature =20;
        eau = 100.0;
        fatigue = 0;
        lastExhaustion =0;
        varieteAlimentaire = 0;
        statuts = new String[4];
        statuts[0]="";
        statuts[1]="";
        statuts[2]="";
        statuts[3]="";
    }



    //Calculateurs
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

    public double calcVarieteAlimentaire()
    {
        List<Material> alimUniques = new ArrayList<>();
        for (Material m :Menu) {
            if(!alimUniques.contains(m))
            {
                alimUniques.add(m);
            }
        }
        return 100.0*alimUniques.size()/(double)TailleMenu;
    }


    //Updates
    public void updateTemperature()
    {
        temperature = (inertieThermique*temperature + (1.0-inertieThermique)*calcTemperature());
    }

    public void updateVarieteAlimentaire()
    {
        varieteAlimentaire = calcVarieteAlimentaire();
    }

    public void updateSoif()
    {
        double delta = Math.max(0.025,getTemperature()*0.025/9);
        eau = Math.max(0,eau - delta);
    }

    public void updateFatigue()
    {

        double delta = 0;
        if(p.isSprinting())
        {
            delta =1.6*regainEnergie;
        }
        if(p.isSneaking())
        {
            delta -= regainEnergie;
        }
        double deltaReel = delta -regainEnergie;
        fatigue = Math.min(Math.max(0,fatigue+deltaReel),100.0);
    }


    //Getters
    public double getFatigue()
    {
        return fatigue;
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

    public void setFatigue(double fatigue) {
        this.fatigue = fatigue;
    }

    public double getEau() {
        return eau;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getVarieteAlimentaire() {
        return varieteAlimentaire;
    }

    public Player getP() {
        return p;
    }

    public String getStatusString()
    {
        return "Temperature : ("+temperature+") "+statuts[0]+'\n'
                +"Nourriture : ("+varieteAlimentaire+") "+statuts[1]+'\n'
                +"Abri : "+statuts[2]+'\n'
                +"Eau : ("+eau+") "+statuts[3];
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
        if(!estExposeAuCiel()) {
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




    //Effets
    public void appliquerEffetTemperature()
    {
        statuts[0] = "OK";
        if(estChaud())
        {
           //Chaud
            statuts[0] = "Chaud";
            PotionEffect p1 = new PotionEffect(PotionEffectType.SLOW,2*20,0);
            PotionEffect p2 = new PotionEffect(PotionEffectType.SLOW_DIGGING,2*20,0);
            appEffet(p1);appEffet(p2);
            if(estBouillant())
            {
                //Bouillant
                statuts[0] = "Bouillant";

                PotionEffect p3 = new PotionEffect(PotionEffectType.CONFUSION,30*20,2);
                PotionEffect p4 = new PotionEffect(PotionEffectType.POISON,5*20,1);
                PotionEffect p5 = new PotionEffect(PotionEffectType.WEAKNESS,2*20,1);
                appEffet(p3);appEffet(p4);appEffet(p5);
            }
        }
        if(estFroid())
        {
            //Froid
            statuts[0] = "Froid";
            PotionEffect p1 = new PotionEffect(PotionEffectType.HUNGER,40*20,0);
            PotionEffect p2 = new PotionEffect(PotionEffectType.SLOW,2*20,1);
            appEffet(p1);
            appEffet(p2);
            if(estGlacial()) {
                //Glacial
                statuts[0] = "Glacial";
                PotionEffect p3 = new PotionEffect(PotionEffectType.BLINDNESS,15*20,2);
                PotionEffect p4 = new PotionEffect(PotionEffectType.SLOW_DIGGING,2*20,1);
                PotionEffect p5 = new PotionEffect(PotionEffectType.POISON,5*20,1);
                appEffet(p3);
                appEffet(p4);
                appEffet(p5);
            }
        }

    }

    public void appliquerEffetsNutrition()
    {
        statuts[1] = "OK";
        if(estCarence())
        {
            //Mal nourri
            statuts[1] = "Mal Nourri";
            PotionEffect p1 = new PotionEffect(PotionEffectType.SLOW_DIGGING,2*20,0);
            appEffet(p1);
        }
        if(estBienAlimente())
        {
            //Bien Nourri
            statuts[1] = "Bien Nourri";
            PotionEffect p1 = new PotionEffect(PotionEffectType.FAST_DIGGING,2*20,0);
            PotionEffect p2 = new PotionEffect(PotionEffectType.INCREASE_DAMAGE,2*20,1);
            PotionEffect p3 = new PotionEffect(PotionEffectType.ABSORPTION,80*20,1);
            appEffet(p1);appEffet(p2);appEffet(p3);
        }
    }

    public void appliquerEffetsSoif()
    {
        statuts[3]="OK";
        if(aSoif())
        {
            statuts[3]="Assoifé";
            PotionEffect p1 = new PotionEffect(PotionEffectType.WEAKNESS,2*20,0);
            PotionEffect p2 = new PotionEffect(PotionEffectType.BLINDNESS,15*20,0);
            appEffet(p1);appEffet(p2);
        }
        if(eau<1)
        {
            statuts[3]="Mourrant";
            PotionEffect p1 = new PotionEffect(PotionEffectType.HARM,2*20,1);
            appEffet(p1);
        }
    }

    public void appliquerEffetsFatigue()
    {
        if(estFatigue())
        {
            PotionEffect p1 = new PotionEffect(PotionEffectType.SLOW,4*20,1);
            appEffet(p1);
            if(estEpuise())
            {
                PotionEffect p2 = new PotionEffect(PotionEffectType.BLINDNESS,13*20,1);
                appEffet(p2);
            }
        }
    }

    public void appliquerEffetsPluie(Random r)
    {
        if(estSousPluie() && p.getWorld().getTemperature(p.getLocation().getBlockX(),p.getLocation().getBlockY(),p.getLocation().getBlockZ())<1) {
            ItemStack casque = p.getEquipment().getHelmet();
            if(casque != null && casque.getType() == Material.CHAINMAIL_HELMET && casque.getItemMeta().hasLore()) {

                if(r.nextDouble()<1.0/15.0) {
                    Damageable c = ((Damageable) casque.getItemMeta());
                    int D_next = c.getDamage() + 1;
                    c.setDamage(D_next);
                    casque.setItemMeta((ItemMeta) c);
                    if(Material.CHAINMAIL_HELMET.getMaxDurability()<D_next)
                    {
                        p.getEquipment().setHelmet(null);
                    }
                }

            }
            else {
                PotionEffect tox = new PotionEffect(PotionEffectType.CONFUSION, 15 * 20, 4);
                PotionEffect acide = new PotionEffect(PotionEffectType.POISON, 2 * 20, 0);
                appEffet(tox);appEffet(acide);
            }
        }
    }



    //Utils
    public boolean estEpuise()
    {
        return fatigue>85;
    }
    public boolean estFatigue()
    {
        return fatigue>60;
    }
    public boolean estGlacial()
    {
        return (temperature<-10);
    }
    public boolean estFroid()
    {
        return (temperature<5);
    }
    public boolean estChaud()
    {
        return temperature>29;
    }
    public boolean estBouillant()
    {
        return temperature>40;
    }
    public boolean estCarence()
    {
        return varieteAlimentaire<30;
    }
    public boolean estBienAlimente()
    {
        return varieteAlimentaire>70;
    }

    public boolean aSoif()
    {
        return eau<15;
    }

    public void appEffet(PotionEffect e)
    {
        PotionEffectType t= e.getType();
        if(p.hasPotionEffect(t))
        {
            PotionEffect present = p.getPotionEffect(t);
            if(present.getDuration()<10) {
                //p.removePotionEffect(t);
                e.apply(p);
            }
        }
        else
        {
            e.apply(p);
        }
    }

    public boolean estExposeAuCiel()
    {
        int maxY = p.getWorld().getHighestBlockAt(p.getLocation()).getY();
        int playerY = p.getLocation().getBlockY();
        if(playerY>=maxY) {
            statuts[2] = "Exposé";
            return true;
        }else
        {
            statuts[2] = "Abrité";
            return false;
        }
    }

    public boolean estSousPluie()
    {
        return p.getLocation().getWorld().getEnvironment().equals(World.Environment.NORMAL) && estExposeAuCiel() && (p.getWorld().hasStorm() || p.getWorld().isThundering());
    }

    public void Manger(Material i)
    {

        fatigue = Math.max(0,fatigue-30);
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




}
