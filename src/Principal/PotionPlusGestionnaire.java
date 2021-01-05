package Principal;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Collection;
//TODO
//Penser a rajouter les nouvelles recettes
public class PotionPlusGestionnaire {
    static String getTypePotion(PotionMeta pm)
    {
        //Definir le type des nouvelles potions
        String name = pm.getDisplayName();
        String lore = pm.getLore().get(0);
        return "";
    }
    static void appliquer(String typePotion, Player p)
    {
        //Appliquer les effets
        if(typePotion.equals(""))
        {

        }
        else
        {

        }
    }
    static void gestSplash(PotionSplashEvent pse)
    {
        Collection<LivingEntity> cibles = pse.getAffectedEntities();
        ThrownPotion tp  = pse.getPotion();
        ItemStack is = tp.getItem();
        if(is.getType() == Material.POTION)
        {
            PotionMeta pm = (PotionMeta)is.getItemMeta();
            String typepot = getTypePotion(pm);
            for(LivingEntity le: cibles )
            {
                if(le instanceof Player) {
                    appliquer(typepot, (Player)le);
                }
            }

        }
    }
    static void gestConso(PlayerItemConsumeEvent pice)
    {
        ItemStack is = pice.getItem();
        if(is.getType() == Material.POTION)
        {
            PotionMeta pm = (PotionMeta)is.getItemMeta();
            Player p = pice.getPlayer();
            appliquer(getTypePotion(pm),p);
        }
    }
}
