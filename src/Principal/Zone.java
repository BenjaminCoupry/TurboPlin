package Principal;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Zone {
    private Location centre;
    private int rayon;
    private World w;
    public Zone(Player p, int rayon)
    {
        this.rayon = rayon;
        centre = p.getLocation().getBlock().getLocation();
        w = p.getWorld();
    }
    public boolean dansZone(Player p)
    {
        Location lp = p.getLocation().getBlock().getLocation();
        return Math.abs(lp.getX()-centre.getX())<rayon && Math.abs(lp.getZ()-centre.getZ())<rayon;
    }
    private List<Block> getFrontiere()
    {
        List<Block> ret = new ArrayList<>();
        for(int i=-rayon;i<=rayon;i++)
        {
            int j;
            for(int k=0;k<=1;k++)
            {
                if(k==0)
                {
                    j=rayon;
                }
                else
                {
                    j=-rayon;
                }
                for(int l=0;l<=1;l++)
                {
                    if(l==0)
                    {
                        int tmp = i;
                        i=j;
                        j=tmp;
                    }
                    ret.add(w.getHighestBlockAt(i,j).getRelative(0,1,0));
                }
            }
        }
        return ret;
    }
    public void tracerFrontiere()
    {
        for(Block b : getFrontiere())
        {
            b.setType(Material.DEAD_BUSH);
        }
    }

}
