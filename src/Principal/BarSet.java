package Principal;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class BarSet {
    private final Main plugin;
    Bar barTemp;
    Bar barSoif;
    Bar barAlim;
    Bar barFatigue;

    public BarSet(Main plugin, Player p) {
        for (Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
            BossBar b = it.next();
            if(b.getPlayers().contains(p))
            {
                b.removePlayer(p);
            }
        }
        this.plugin = plugin;
        barTemp = new Bar(plugin);
        barSoif = new Bar(plugin);
        barAlim = new Bar(plugin);
        barFatigue = new Bar(plugin);
        barTemp.createTempBar();
        barSoif.createWaterBar();
        barAlim.createAlimBar();
        barFatigue.createFatigueBar();
        barTemp.addPlayer(p);
        barSoif.addPlayer(p);
        barAlim.addPlayer(p);
        barFatigue.addPlayer(p);
    }
    public void update(PlayerSuperData ps)
    {
        barAlim.updateValue(ps.getVarieteAlimentaire());
        barSoif.updateValue(ps.getEau());
        barTemp.updateValue(ps.getTemperature()+50);
        barFatigue.updateValue(ps.getFatigue());
        barTemp.getBar().setColor(colorFromTemp(ps));
        barSoif.getBar().setColor(colorFromSoif(ps));
        barAlim.getBar().setColor(colorFromEquilibreAlim(ps));
        barFatigue.getBar().setColor(colorFromFatigue(ps));
    }

    public BarColor colorFromTemp(PlayerSuperData ps)
    {
        if(ps.estGlacial())
        {
            return BarColor.WHITE;
        }
        if(ps.estFroid())
        {
            return BarColor.BLUE;
        }
        if(ps.estBouillant())
        {
            return BarColor.RED;
        }
        if(ps.estChaud())
        {
            return BarColor.YELLOW;
        }
        return BarColor.GREEN;
    }
    public BarColor colorFromSoif(PlayerSuperData ps)
    {
        if(ps.aSoif())
        {
            return BarColor.RED;
        }
        return BarColor.BLUE;
    }
    public BarColor colorFromEquilibreAlim(PlayerSuperData ps)
    {
        if(ps.estCarence())
        {
            return BarColor.RED;
        }
        if(ps.estBienAlimente())
        {
            return BarColor.BLUE;
        }
        return BarColor.GREEN;
    }
    public BarColor colorFromFatigue(PlayerSuperData ps)
    {
        if(ps.estEpuise())
        {
            return BarColor.RED;
        }
        if(ps.estFatigue())
        {
            return BarColor.YELLOW;
        }

        return BarColor.GREEN;
    }

    public void del()
    {
        barTemp.del();
        barAlim.del();
        barSoif.del();
        barFatigue.del();
    }
    public void del(Player p)
    {
        barTemp.getBar().removePlayer(p);
        barTemp.getBar().removePlayer(p);
        barTemp.getBar().removePlayer(p);
    }

}
