package Principal;


import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.Serializable;
import java.util.*;

public class Factions implements Serializable {
    private Map<String, List<String>> factions;
    private Map<String, Zone> bases;
    private List<Zone> lootZones;
    private static final int rayonBase = 30;
    private long temps;
    public Factions()
    {
        factions = new HashMap<>();
        bases = new HashMap<>();
        lootZones = new ArrayList<>();
        temps =0;
    }
    public void ajouterJoueur(String faction, Player p)
    {
        supprimerJoueur(p);
        if(!factions.containsKey(faction))
        {
            List<String> nf = new ArrayList<>();
            nf.add(p.getName());
            factions.put(faction,nf);
        }
        else
        {
            factions.get(faction).add(p.getName());
        }
    }
    public void supprimerJoueur(Player p)
    {
        for(List<String> membres : factions.values())
        {
            if(membres.contains(p.getName()))
            {
                membres.remove(p.getName());
            }
        }
    }
    public void supprimerFaction(String f)
    {
        if(factions.containsKey(f))
        {
            factions.remove(f);
        }
        if(bases.containsKey(f))
        {
            bases.remove(f);
        }
    }
    public String getStringFactions()
    {
        String s = "Factions : "+'\n';
        for(String f : factions.keySet())
        {
            List<String> membres = factions.get(f);
            s+="    "+f+"\n";
            for(String m : membres)
            {
                s+="        "+m+'\n';
            }
        }
        return s;
    }
    public String factionDe(Player p)
    {
        for(String f : factions.keySet())
        {
            List<String> membres = factions.get(f);
            if(membres.contains(p.getName()))
            {
                return f;
            }
        }
        return null;
    }
    public Zone baseDe(Player p)
    {
        String fact = factionDe(p);
        if(fact != null)
        {
            if(bases.containsKey(fact))
            {
                return bases.get(fact);
            }
        }
        return null;
    }
    public Zone creerBase(Player p)
    {
        String f = factionDe(p);
        if(f!= null)
        {
            if(baseDe(p) == null)
            {
                Zone z = new Zone(p.getLocation(),rayonBase);
                bases.put(f,z);
                p.setBedSpawnLocation(p.getLocation(),true);
                donnerMarqueurFaction(p);
                return z;
            }
        }
        return null;
    }
    public String getNomFactionLocalisationJoueur(Player p)
    {
        for(String f : bases.keySet())
        {
            Zone z = bases.get(f);
            if(z.dansZone(p))
            {
                return f;
            }
        }
        return null;
    }

    public void donnerMarqueurFaction(Player p)
    {
        ItemStack marqueur = getMarqueurFaction(p);
        if(marqueur != null) {
            p.getWorld().dropItemNaturally(p.getLocation(), marqueur);
        }
    }

    public boolean testerPossesionMarqueurFaction(Player p, String faction)
    {
        for(ItemStack mh : p.getInventory().getContents()) {
            if (mh != null) {
                Main.callCommande("say "+mh.toString());
                if (mh.hasItemMeta()) {
                    ItemMeta im = mh.getItemMeta();
                    if (im.hasLore()) {
                        List<String> lore = im.getLore();
                        if (lore.size() > 0) {
                            if(lore.get(0).contains(faction)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private ItemStack getMarqueurFaction(Player p)
    {
        String faction = factionDe(p);
        if(faction != null)
        {
            ItemStack marque = new ItemStack(Material.COMPASS);
            List<String> Lore = new ArrayList<String>();
            Lore.add(faction);
            ItemMeta m = marque.getItemMeta();
            m.setLore(Lore);
            m.setDisplayName(ChatColor.RED+"Faction " + factionDe(p));
            marque.setItemMeta(m);
            return marque;
        }
        else
        {
            return null;
        }
    }

    public boolean estDansSaBase(Player p)
    {
        String f = factionDe(p);
        if(f!=null && p.getLocation().getWorld().getEnvironment().equals(World.Environment.NORMAL))
        {
            return f.equals(getNomFactionLocalisationJoueur(p));
        }
        else
        {
            return false;
        }
    }
    public boolean estDansBaseEnnemie(Player p)
    {
        String fp = factionDe(p);
        String fl = getNomFactionLocalisationJoueur(p);
        if(fl!=null && p.getLocation().getWorld().getEnvironment().equals(World.Environment.NORMAL))
        {
            return !fp.equals(fl);
        }
        else
        {
            return false;
        }
    }

    public boolean destructionDeFactionPar(Player p)
    {
        String fp = factionDe(p);
        if(estDansSaBase(p))
        {
            for(String cible : factions.keySet()) {
                if(!cible.equals(fp)) {
                    if (testerPossesionMarqueurFaction(p, cible)) {
                        Main.callCommande("say "+ChatColor.RED + cible + " a été détruite par " + p.getName());
                        supprimerFaction(cible);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void spreadFactions(World w, Main main)
    {
        Random r = main.r;
        Server s = main.getServer();
        List<Player> joueurs = (List<Player>) s.getOnlinePlayers();
        Location centre = w.getWorldBorder().getCenter();
        double Rayon = w.getWorldBorder().getSize()/2.0;
        for(String f : factions.keySet())
        {
            List<String> membres = factions.get(f);
            double x = (r.nextDouble()*2.0-1.0)*Rayon+centre.getX();
            double z = (r.nextDouble()*2.0-1.0)*Rayon+centre.getZ();
            Location lf = w.getHighestBlockAt((int)x,(int)z).getLocation().add(0,2,0);
            for(String m : membres)
            {
                for(Player p : joueurs)
                {
                    s.getLogger().info(p.getName()+" "+m);
                    if(p.getName().equals(m))
                    {
                        s.getLogger().info("ok "+p.getName()+" "+m);
                        donnerStuffBase(p,main);
                        p.teleport(lf);
                    }
                }
            }
        }
        startTimer(main);
    }

    public void forcerBases(Main main)
    {
        Random r = new Random();
        Server s = main.getServer();
        List<Player> joueurs = (List<Player>) s.getOnlinePlayers();
        for(String f : factions.keySet())
        {
            List<String> membres = factions.get(f);
            if(membres.size() != 0)
            {
                String m = membres.get(0);
                for(Player p : joueurs)
                {
                    if(p.getName().equals(m))
                    {
                        Zone z = creerBase(p);
                        z.tracerFrontiere(p.getWorld(), r);
                        Main.callCommande("say "+p.getName()+" de la faction "+factionDe(p)+" a créé sa base");
                        break;
                    }
                }
            }
        }
    }

    public void creerLootZone( Main main)
    {
        Random r = main.r;
        Server s = main.getServer();
        List<Player> joueurs = (List<Player>) s.getOnlinePlayers();
        if(joueurs.size()>0) {
            if( joueurs.get(0).getLocation().getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
                World w = joueurs.get(0).getWorld();
                Location centre = w.getWorldBorder().getCenter();
                double Rayon = w.getWorldBorder().getSize() / 2.0;
                double x = (r.nextDouble()*2.0-1.0)*Rayon+centre.getX();
                double z = (r.nextDouble()*2.0-1.0)*Rayon+centre.getZ();
                Main.callCommande("say Un trésor secret est apparu en "+x+" "+z);
                Location lf = w.getHighestBlockAt((int)x,(int)z).getLocation().add(0,2,0);
                Zone zone = new Zone(lf,5);
                zone.tracerFrontiere(w,r);
                lootZones.add(zone);
            }
        }
    }


    public void looter(Player p)
    {
        if(p.getGameMode() == GameMode.SURVIVAL) {
            Zone looted = null;
            for (Zone z : lootZones) {
                if (z.dansZone(p)) {
                    looted = z;
                    Main.callCommande("say Un trésor a été trouvé en " + z.getCentreX() + " " + z.getCentreZ());
                    donnerLoot(p);
                    break;
                }
            }
            if (looted != null) {
                lootZones.remove(looted);
            }
        }
    }

    public void donnerLoot(Player p)
    {
        Random r = new Random();
        if(r.nextDouble()<0.9)
        {
            donnerLootBien(p);
        }
        else
        {
            donnerLootMauvais(p);
        }
    }
    public void donnerLootBien(Player p)
    {

        Random r = new Random();
        ItemStack is = new ItemStack(Material.DIAMOND);
        p.getWorld().dropItemNaturally(p.getLocation(),is);
        if(r.nextDouble()<0.5)
        {
            is = new ItemStack(Material.BLAZE_ROD);
            is.setAmount(2);
            p.getWorld().dropItemNaturally(p.getLocation(),is);
        }
        if(r.nextDouble()<0.5)
        {
            is = new ItemStack(Material.NETHER_WART);
            is.setAmount(6);
            p.getWorld().dropItemNaturally(p.getLocation(),is);
        }
        if(r.nextDouble()<0.5)
        {
            is = new ItemStack(Material.SPLASH_POTION);
            PotionMeta pm = (PotionMeta)is.getItemMeta();
            pm.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION,60*20,3),true);
            is.setItemMeta(pm);
            p.getWorld().dropItemNaturally(p.getLocation(),is);
            if(r.nextDouble()<0.5)
            {
                is = new ItemStack(Material.DIAMOND_CHESTPLATE);
                p.getWorld().dropItemNaturally(p.getLocation(),is);
                if(r.nextDouble()<0.5)
                {
                    is = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
                    p.getWorld().dropItemNaturally(p.getLocation(),is);
                    if(r.nextDouble()<0.5)
                    {
                        is = new ItemStack(Material.NETHERITE_BLOCK);
                        is.setAmount(r.nextInt(3)+1);
                        p.getWorld().dropItemNaturally(p.getLocation(),is);
                    }
                }
                is = new ItemStack(Material.ANVIL);
                p.getWorld().dropItemNaturally(p.getLocation(),is);
                if(r.nextDouble()<0.5)
                {
                    is = new ItemStack(Material.GOLDEN_APPLE);
                    p.getWorld().dropItemNaturally(p.getLocation(),is);
                    if(r.nextDouble()<0.5)
                    {
                        is = new ItemStack(Material.NETHER_STAR);
                        p.getWorld().dropItemNaturally(p.getLocation(),is);
                    }
                }
                is = new ItemStack(Material.NETHER_WART);
                p.getWorld().dropItemNaturally(p.getLocation(),is);
                if(r.nextDouble()<0.5)
                {
                    is = new ItemStack(Material.BELL);
                    p.getWorld().dropItemNaturally(p.getLocation(),is);
                    if(r.nextDouble()<0.5)
                    {
                        is = new ItemStack(Material.BLAZE_POWDER);
                        p.getWorld().dropItemNaturally(p.getLocation(),is);
                    }
                }
            }
        }
        if(r.nextDouble()<0.5)
        {
            is = new ItemStack(Material.POTION);
            PotionMeta pm = (PotionMeta)is.getItemMeta();
            pm.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION,10*20,3),true);
            is.setItemMeta(pm);
            p.getWorld().dropItemNaturally(p.getLocation(),is);
            if(r.nextDouble()<0.5)
            {
                is = new ItemStack(Material.APPLE);
                p.getWorld().dropItemNaturally(p.getLocation(),is);
                if(r.nextDouble()<0.5)
                {
                    is = new ItemStack(Material.SPLASH_POTION);
                    PotionMeta pm2 = (PotionMeta)is.getItemMeta();
                    pm2.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION,120*20,2),true);
                    is.setItemMeta(pm2);
                    p.getWorld().dropItemNaturally(p.getLocation(),is);
                    if(r.nextDouble()<0.5)
                    {
                        is = new ItemStack(Material.TNT);
                        is.setAmount(r.nextInt(3)+1);
                        p.getWorld().dropItemNaturally(p.getLocation(),is);
                    }
                }
            }
            if(r.nextDouble()<0.5)
            {
                is = new ItemStack(Material.BLAZE_ROD);
                p.getWorld().dropItemNaturally(p.getLocation(),is);
                if(r.nextDouble()<0.5)
                {
                    is = new ItemStack(Material.SPLASH_POTION);
                    PotionMeta pm2 = (PotionMeta)is.getItemMeta();
                    pm.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS,120*20,2),true);
                    is.setItemMeta(pm2);
                    p.getWorld().dropItemNaturally(p.getLocation(),is);
                    if(r.nextDouble()<0.5)
                    {
                        is = new ItemStack(Material.DIAMOND_BLOCK);
                        is.setAmount(r.nextInt(3)+1);
                        p.getWorld().dropItemNaturally(p.getLocation(),is);
                    }
                }
            }
        }
    }
    public void donnerLootMauvais(Player p)
    {
        Random r = new Random();
        ItemStack is = new ItemStack(Material.CHAIN);
        is.setAmount(64);
        p.getWorld().dropItemNaturally(p.getLocation(),is);
        if(r.nextDouble()<0.5)
        {
            is = new ItemStack(Material.ACACIA_SIGN);
            is.setAmount(16);
            p.getWorld().dropItemNaturally(p.getLocation(),is);
        }
        if(r.nextDouble()<0.5)
        {
            is = new ItemStack(Material.WOODEN_SHOVEL);
            is.setAmount(3);
            p.getWorld().dropItemNaturally(p.getLocation(),is);
        }
        if(r.nextDouble()<0.5)
        {
            is = new ItemStack(Material.WOODEN_SWORD);
            is.setAmount(3);
            p.getWorld().dropItemNaturally(p.getLocation(),is);
        }
        if(r.nextDouble()<0.5)
        {
            is = new ItemStack(Material.LIME_STAINED_GLASS);
            is.setAmount(1);
            p.getWorld().dropItemNaturally(p.getLocation(),is);
        }
        if(r.nextDouble()<0.5)
        {
            is = new ItemStack(Material.BAMBOO);
            is.setAmount(1);
            p.getWorld().dropItemNaturally(p.getLocation(),is);
        }
    }

    public void startTimer(Main main)
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
            @Override
            public void run() {
                ecoulerTemps(main);
            }
        },0,20);
    }
    public void donnerStuffBase(Player p, Main m)
    {
        ItemStack is = new ItemStack(Material.GLASS_BOTTLE);
        ItemStack is2 = new ItemStack(Material.APPLE);
        m.playerReset(p);
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,60*20,10));
        p.setGameMode(GameMode.SURVIVAL);
        p.getInventory().clear();
        p.getInventory().setItem(EquipmentSlot.HAND,is);
        p.getInventory().setItem(EquipmentSlot.OFF_HAND,is2);
    }

    public void ecoulerTemps(Main m)
    {
        temps += 1;
        if(temps <= 10*60)
        {
            //Debut de partie
            if(temps%10==0)
            {
                Main.callCommande("say "+ChatColor.RED+"Il vous reste "+(10*60-temps) + " secondes pour créer " +
                        "vôtre base ! (/set_base)");
            }
            if(temps == 10*60)
            {
                m.immunise = false;
                forcerBases(m);
            }
        }
        else
        {
            if(temps%(10*60) == 0)
            {
                Main.callCommande("say "+ChatColor.RED+"Vous jouez depuis "+(temps/60) + " minutes");
            }
            if((temps+120)%(40*60) == 0)
            {
                Main.callCommande("say "+ChatColor.RED+"Un terrible évènement est à prévoir ...");
            }
            if(temps%(40*60) == 0)
            {
                EventsTemporels.superVagueZombie(m);
            }
            if((temps+30*60+5*60)%(40*60) == 0)
            {
                Main.callCommande("say "+ChatColor.RED+"Un coffre va apparaître ...");
            }
            if((temps+30*60)%(40*60) == 0)
            {
                creerLootZone(m);
            }
        }
    }
}
