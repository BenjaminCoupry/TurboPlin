package Principal;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Zone implements Serializable {
    private int centreX;
    private int centreZ;
    private int rayon;
    private static List<Material> pasdeFrontiere;
    public Zone(Player p, int rayon)
    {

        Material[] pasdeFrontiere_ = new Material[]{Material.WATER,Material.LAVA};
        pasdeFrontiere = Arrays.asList(pasdeFrontiere_);
        this.rayon = rayon;
        centreX = p.getLocation().getBlock().getLocation().getBlockX();
        centreZ = p.getLocation().getBlock().getLocation().getBlockY();
    }
    public boolean dansZone(Player p)
    {
        Location lp = p.getLocation().getBlock().getLocation();
        return Math.abs(lp.getX()-centreX)<rayon && Math.abs(lp.getZ()- centreZ)<rayon;
    }
    private List<Block> getFrontiere(World w)
    {
        List<Block> ret = new ArrayList<>();
        ret.add(w.getHighestBlockAt(centreX, centreZ).getRelative(0,1,0));
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
                    ret.add(w.getHighestBlockAt(ir+centreX,j+ centreZ).getRelative(0,1,0));
                }
            }
        }
        return ret;
    }
    public void tracerFrontiere(World w, Random r)
    {
        int k=0;
        for(Block b : getFrontiere(w))
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
                if (!(b.getState().getBlockData() instanceof Bisected)) {
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
