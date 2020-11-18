package Principal;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Factions {
    private Map<String, List<String>> factions;
    private Map<String, Zone> bases;
    private static final int rayonBase = 30;
    private Random r;
    public Factions(Random r)
    {
        factions = new HashMap<>();
        bases = new HashMap<>();
        this.r = r;
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
                Zone z = new Zone(p,rayonBase,r);
                bases.put(f,z);
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
        if(f!=null)
        {
            return f == getNomFactionLocalisationJoueur(p);
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
        if(fl!=null)
        {
            return fp != fl;
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
                if(cible != fp) {
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

    public void spreadFactions(World w, Server s)
    {
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
                    if(p.getName() == m)
                    {
                        p.teleport(lf);
                    }
                }
            }
        }
    }
}
