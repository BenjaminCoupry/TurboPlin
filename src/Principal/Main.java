package Principal;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    //Materiaux
    List<Material> tntOnly;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return super.onCommand(sender, command, label, args);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.getServer().getLogger().info("TurboStart");
        Material[] matArr = {Material.STONE_BRICK_WALL,Material.STONE_BRICK_SLAB,Material.STONE_BRICK_STAIRS,Material.STONE_BRICKS};
        tntOnly = Arrays.asList(matArr);
        this.getServer().getPluginManager().registerEvents(this,this);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event)
    {
        Player p = (Player) event.getPlayer();
        Block b = event.getBlock();
        if(p.getGameMode() == GameMode.SURVIVAL && tntOnly.contains(b.getType()))
        {
            event.setCancelled(true);
        }

    }
}
