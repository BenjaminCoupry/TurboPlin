package Principal;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Factions {
    private Map<String, List<String>> factions;
    private Map<String, Zone> bases;
    private static final int rayonBase = 30;
    public Factions()
    {
        factions = new HashMap<>();
        bases = new HashMap<>();
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
                Zone z = new Zone(p,rayonBase);
                bases.put(f,z);
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
}
