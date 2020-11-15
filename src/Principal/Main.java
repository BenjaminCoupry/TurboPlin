package Principal;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;

public class Main extends JavaPlugin implements Listener {



    //Materiaux
    List<Material> tntOnly;
    Random r;
    Map<String,PlayerSuperData> superdatas;
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
        r = new Random();
        superdatas = new HashMap<>();
        Material[] matArr = {Material.STONE_BRICK_WALL,Material.STONE_BRICK_SLAB,Material.STONE_BRICK_STAIRS,Material.STONE_BRICKS};
        tntOnly = Arrays.asList(matArr);
        this.getServer().getPluginManager().registerEvents(this,this);
        playersSetup();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                playersUpdate();
            }
        },0,5);
    }

    @EventHandler
    public void onPlayerConnexion(PlayerJoinEvent event)
    {
        Player p = event.getPlayer();
        if(!superdatas.containsKey(p.getName()))
        {
            superdatas.put(p.getName(),new PlayerSuperData(p));
        }
        createBoard(p);

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
    @EventHandler
    public void onManger(PlayerItemConsumeEvent event)
    {

    }

    public void createBoard(Player p)
    {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard sb = manager.getNewScoreboard();
        Objective obj = sb.registerNewObjective("stats","dummy","Statistiques");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score variteAlim = obj.getScore("Equilibre Alimentaire :");
        variteAlim.setScore(1);
        Score soif = obj.getScore("Eau :");
        soif.setScore(1);
        Score temp = obj.getScore("Temperature :");
        temp.setScore(1);
        p.setScoreboard(sb);
    }


    @EventHandler
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Zombie) {
            if (event.getEntity() instanceof Player) {
                // Joueur tapé par zombie
                Player p = (Player) event.getEntity();
                Zombie z = (Zombie) event.getDamager();
                PotionEffect ep = new PotionEffect(PotionEffectType.POISON, 7 * 20, 2);
                PotionEffect eh = new PotionEffect(PotionEffectType.HUNGER, 30 * 20, 2);
                PotionEffect ej = new PotionEffect(PotionEffectType.JUMP, 4 * 20, 2);
                PotionEffect el = new PotionEffect(PotionEffectType.LEVITATION, 2 * 20, 2);
                ep.apply(p);eh.apply(p);ej.apply(p);el.apply(p);
                if (p.getHealth() - event.getDamage() <= 0.5) {
                    //Joueur Meurt
                    Zombie zp = (Zombie)z.getWorld().spawnEntity(p.getLocation(),EntityType.ZOMBIE);
                    zp.getEquipment().setArmorContents(p.getEquipment().getArmorContents().clone());
                    p.getEquipment().clear();
                    zp.setCustomName("(Z)"+p.getName());
                    zp.setCustomNameVisible(true);
                }
            }
        }
        if (event.getDamager() instanceof Player) {
            if (event.getEntity() instanceof Zombie) {
                //Zombie tapé par Joueur
                Player p = (Player) event.getDamager();
                Zombie z = (Zombie) event.getEntity();
                PotionEffect eb = new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 1);
                eb.apply(p);
                if (z.getHealth() - event.getDamage() <= 0.5) {
                    //Zombie Meurt
                    if(r.nextDouble()<0.2) {
                        z.getWorld().createExplosion(z.getLocation(), r.nextFloat()*5,true,true);
                        z.getWorld().strikeLightning(z.getLocation());
                    }
                }
            }
        }
    }

    private void playersUpdate()
    {
        List<Player> lp = (List<Player>)this.getServer().getOnlinePlayers();
        for (Player p: lp) {
            playerUpdate(p);
        }

    }
    private void playersSetup()
    {
        List<Player> lp = (List<Player>)this.getServer().getOnlinePlayers();
        for (Player p: lp) {
            superdatas.put(p.getName(),new PlayerSuperData(p));
            createBoard(p);
        }

    }
    private void playerUpdate(Player p)
    {
        scoreboardUpdate(superdatas.get(p.getName()));
    }
    private void scoreboardUpdate(PlayerSuperData ps)
    {
        Player p = ps.p;

        int temperatureBiome = (int)(100.0*p.getWorld().getTemperature(p.getLocation().getBlockX(),p.getLocation().getBlockY(),p.getLocation().getBlockZ()));

        Score varieteAlim = p.getScoreboard().getObjective("stats").getScore("Equilibre Alimentaire :");
        Score soif = p.getScoreboard().getObjective("stats").getScore("Eau :");
        Score temp = p.getScoreboard().getObjective("stats").getScore("Temperature :");

        varieteAlim.setScore(10);
        soif.setScore(10);
        temp.setScore(temperatureBiome);
    }
}
