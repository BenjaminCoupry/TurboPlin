package Principal;


import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class InteractionsEntites {
    static final int EXP_KILL = 20;
    static final int EXP_KILL_ZOMBIE = 1;
    public static void Combat(EntityDamageByEntityEvent event)
    {
        Entity attaquant = event.getDamager();
        Entity victime= event.getEntity();
        if (attaquant instanceof Zombie) {
            if (victime instanceof Player) {
                // Joueur tapé par zombie
                JoueurAttaqueParZombie(attaquant,victime,event.getDamage());
            }
        }
        if (attaquant instanceof Player) {
            if (victime instanceof Zombie) {
                ZombieAttaqueParJoueur(victime,attaquant,event.getDamage());
            }
        }
        if (attaquant instanceof Player) {
            if (victime instanceof Player) {
                PvP(attaquant,victime, event.getDamage());
            }
        }

    }
    public static void PvP(Entity attaquant, Entity defenseur, double damage)
    {
        Player pat = (Player) attaquant;
        Player pdef = (Player) defenseur;
        if (pdef.getHealth() - damage <= 0.5) {
            PlayerTuePlayer(pat,pdef);
        }
    }
    public static void PlayerTuePlayer(Player tueur, Player tue)
    {
        Main main = Main.getPlugin();
        main.factions.changerScore(tueur,EXP_KILL);
    }
    public static void ZombieAttaqueParJoueur(Entity Zombie, Entity Joueur, double damage)
    {
        //Zombie tapé par Joueur
        Player p = (Player) Joueur;
        Zombie z = (Zombie) Zombie;
        PotionEffect eb = new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 1);
        eb.apply(p);
        if (z.getHealth() - damage <= 0.5) {
            ZombieTueParJoueur(z,p);
        }
    }
    public static void ZombieTueParJoueur(Zombie z, Player p)
    {
        //Zombie Meurt
        Random r = new Random();
        if(r.nextDouble()<0.2) {
            z.getWorld().createExplosion(z.getLocation(), r.nextFloat()*5,true,true);
            z.getWorld().strikeLightning(z.getLocation());
            Main main = Main.getPlugin();
            main.factions.changerScore(p,EXP_KILL_ZOMBIE);
        }
    }
    public static void JoueurAttaqueParZombie(Entity Zombie, Entity Joueur, double damage)
    {
        //Zombie tapé par Joueur
        Player p = (Player) Joueur;
        Zombie z = (Zombie) Zombie;
        PotionEffect ep = new PotionEffect(PotionEffectType.POISON, 7 * 20, 2);
        PotionEffect eh = new PotionEffect(PotionEffectType.HUNGER, 30 * 20, 2);
        PotionEffect ej = new PotionEffect(PotionEffectType.JUMP, 4 * 20, 2);
        PotionEffect el = new PotionEffect(PotionEffectType.LEVITATION, 2 * 20, 2);
        ep.apply(p);eh.apply(p);ej.apply(p);el.apply(p);
        if (p.getHealth() - damage <= 0.5) {
            //Joueur Meurt
            JoueurTueParZombie(z,p);
        }
    }
    public static void JoueurTueParZombie(Zombie z, Player p)
    {
        Zombie zp = (Zombie)z.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
        if(p.getEquipment().getChestplate()!= null)
        {
            zp.getEquipment().setChestplate(p.getEquipment().getChestplate().clone());
        }
        if(p.getEquipment().getLeggings()!= null)
        {
            zp.getEquipment().setLeggings(p.getEquipment().getLeggings().clone());
        }
        if(p.getEquipment().getBoots()!= null)
        {
            zp.getEquipment().setBoots(p.getEquipment().getBoots().clone());
        }
        if(p.getEquipment().getHelmet()!= null)
        {
            zp.getEquipment().setHelmet(p.getEquipment().getHelmet().clone());
        }
        if(p.getEquipment().getItemInMainHand()!= null)
        {
            zp.getEquipment().setItemInMainHand(p.getEquipment().getItemInMainHand().clone());
        }

        zp.setCustomName("(Z)"+p.getName());
        zp.setCustomNameVisible(true);
    }
}
