package Principal;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;

import java.util.List;
import java.util.Random;

public class EventsTemporels {




    public static void superVagueZombie(Main m)
    {
        Main.callCommande("say "+ ChatColor.RED+"L'eclipse du mal est en cours...");
        Main.callCommande("time set night");
        Random r = new Random();
        List<Player> joueurs = (List<Player>) m.getServer().getOnlinePlayers();
        for(Player p : joueurs)
        {
            World w =  p.getWorld();
            Location l0 = p.getLocation();
            for(int i=0;i<12;i++)
            {
                double X = l0.getX()+((r.nextDouble()*2.0)-1.0)*10;
                double Z = l0.getZ()+((r.nextDouble()*2.0)-1.0)*10;
                double Y = w.getHighestBlockYAt((int)X,(int)Z);
                Location L1 = new Location(w,X,Y,Z).add(0,2,0);
                Zombie z = (Zombie)w.spawnEntity(L1, EntityType.ZOMBIE);
                AttributeInstance iv =  z.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                iv.setBaseValue(iv.getBaseValue()*1.3);
                z.setCustomName("Eclipse");
                z.setCustomNameVisible(true);

            }
            for(int i=0;i<5;i++)
            {
                double X = l0.getX()+((r.nextDouble()*2.0)-1.0)*10;
                double Z = l0.getZ()+((r.nextDouble()*2.0)-1.0)*10;
                double Y = w.getHighestBlockYAt((int)X,(int)Z);
                Location L1 = new Location(w,X,Y,Z).add(0,2,0);
                Creeper c = (Creeper) w.spawnEntity(L1, EntityType.CREEPER);
                c.setCustomName("Eclipse");
                c.setCustomNameVisible(true);
                c.setPowered(true);
            }
            for(int i=0;i<1;i++)
            {
                double X = l0.getX()+((r.nextDouble()*2.0)-1.0)*10;
                double Z = l0.getZ()+((r.nextDouble()*2.0)-1.0)*10;
                double Y = w.getHighestBlockYAt((int)X,(int)Z);
                Location L1 = new Location(w,X,Y,Z).add(0,10,0);
                Ghast g = (Ghast) w.spawnEntity(L1, EntityType.GHAST);
                g.setCustomName("Eclipse");
                g.setCustomNameVisible(true);
            }
        }
    }
}
