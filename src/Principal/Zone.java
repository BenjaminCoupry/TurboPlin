package Principal;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Zone {
    private Location centre;
    private int rayon;
    private World w;
    private Random r;
    private List<Material> pasdeFrontiere;
    public Zone(Player p, int rayon, Random r)
    {
        Material[] pasdeFrontiere_ = new Material[]{Material.WATER,Material.LAVA};
        pasdeFrontiere = Arrays.asList(pasdeFrontiere_);
        this.rayon = rayon;
        centre = p.getLocation().getBlock().getLocation();
        w = p.getWorld();
        this.r = r;
    }
    public boolean dansZone(Player p)
    {
        Location lp = p.getLocation().getBlock().getLocation();
        return Math.abs(lp.getX()-centre.getX())<rayon && Math.abs(lp.getZ()-centre.getZ())<rayon;
    }
    private List<Block> getFrontiere()
    {
        List<Block> ret = new ArrayList<>();
        ret.add(w.getHighestBlockAt(centre.getBlockX(),centre.getBlockZ()).getRelative(0,1,0));
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
                    int ir;
                    if(l==0)
                    {
                        ir = i;
                    }
                    else
                    {
                        ir = j;
                        j=i;
                    }
                    ret.add(w.getHighestBlockAt(ir+centre.getBlockX(),j+centre.getBlockZ()).getRelative(0,1,0));
                }
            }
        }
        return ret;
    }
    public void tracerFrontiere()
    {
        int k=0;
        for(Block b : getFrontiere())
        {
            if(frontierePossible(b.getRelative(0,-1,0))) {
                if(k==0) {
                    b.setType(Material.CAMPFIRE);
                }
                else
                {
                    b.setType(Material.COBBLESTONE_WALL);
                    if(r.nextDouble()<0.08) {
                        b.getRelative(0, 1, 0).setType(Material.LANTERN);
                    }
                }

            }
            k++;
        }
    }
    public boolean frontierePossible(Block b)
    {
        if(!pasdeFrontiere.contains(b.getType()))
        {
            if(!b.isPassable()) {
                if (!(b.getState() instanceof Bisected)) {
                    if (!((b.getState().getBlockData()) instanceof Leaves)) {
                        BoundingBox bb = b.getBoundingBox();
                        if (bb.getVolume() == 1) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
