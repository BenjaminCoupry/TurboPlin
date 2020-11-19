package Principal;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SecurityDoor {

    //Nommer une pepite (clef)
    //Poser un passeau a 1 bloc de la porte en fer
    //Se tenir sur le meme bloc que la porte
    //faire clique droit avec la pepite sur le panneau
    //La porte est desormais liee a la pepite
    //Click gauche avec la clef pour demonter la porte
    static void actionSec(ItemStack it, Block b, Player p, Action act)
    {
        if(it.getType() == Material.GOLD_NUGGET)
        {
            if(it.hasItemMeta())
            {
                ItemMeta mt= it.getItemMeta();
                if(mt.hasDisplayName())
                {
                    String name = mt.getDisplayName();
                    //Click sur un block avec un golden nugget qui a un nom
                    String hash = Main.getHashst(name);
                    if(b.getState() instanceof Sign)
                    {
                        Sign s = (Sign)(b.getState());
                        s.setLine(0,hash);
                        String locStr = p.getLocation().getBlock().getLocation().toString();
                        String posHash = Main.getHashst(locStr);

                        s.setLine(1,posHash);
                        s.update();
                    }
                    if(b.getType() == Material.IRON_DOOR)
                    {
                        Sign s = getSign(b);
                        if(s!= null)
                        {
                            String actuel = s.getLine(0);
                            if(actuel.equals(hash)) {
                                Door d = (Door) (b.getState().getBlockData());
                                if(act == Action.RIGHT_CLICK_BLOCK) {
                                    if (!d.isPowered()) {
                                        boolean state = d.isOpen();
                                        boolean nextState = !state;
                                        d.setOpen(nextState);
                                        b.setBlockData(d);
                                    }
                                }
                                else if(act == Action.LEFT_CLICK_BLOCK)
                                {
                                    b.setType(Material.AIR);
                                    if(b.getRelative(0,1,0).getType() == Material.IRON_DOOR)
                                    {
                                        b.getRelative(0,1,0).setType(Material.AIR);
                                    }
                                    if(b.getRelative(0,-1,0).getType() == Material.IRON_DOOR)
                                    {
                                        b.getRelative(0,-1,0).setType(Material.AIR);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    static Sign getSign(Block b)
    {
        int R=1;
        Location l = b.getLocation();
        String locStr1 = l.clone().subtract(0,1,0).toString();
        String hashLoc1 = Main.getHashst(locStr1);
        String locStr2 = l.clone().subtract(0,0,0).toString();
        String hashLoc2 = Main.getHashst(locStr2);
        for(int i = -R;i<=R;i++) {
            for (int j = -R; j <= R; j++) {
                for (int k = -R; k <= R; k++) {
                    Location l0 = new Location(b.getWorld(), l.getX() + i, l.getY()+k, l.getZ() + j);
                    Block b_ = b.getWorld().getBlockAt(l0);
                    if (b_.getState() instanceof Sign) {
                        Sign s = (Sign) (b_.getState());
                        if(s.getLine(1).equals(hashLoc1) ||s.getLine(1).equals(hashLoc2)) {
                            return s;
                        }
                    }
                }

            }
        }
        return null;
    }
}
