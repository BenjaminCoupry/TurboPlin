package Principal;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
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
        Recettes.ajouterRecettes(this);
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
        Material mange = event.getItem().getType();
        PlayerSuperData psd = superdatas.get(event.getPlayer().getName());
        psd.Manger(mange);
    }

    public void createBoard(Player p)
    {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard sb = manager.getNewScoreboard();
        Objective obj = sb.registerNewObjective("stats","dummy","Statistiques");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score variteAlim = obj.getScore(ChatColor.GOLD+"Equilibre Alimentaire :");
        variteAlim.setScore(100);
        Score soif = obj.getScore(ChatColor.BLUE+"Eau :");
        soif.setScore(100);
        Score temp = obj.getScore(ChatColor.RED+"Temperature :");
        temp.setScore(20);
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

    @EventHandler
    public void onFireArrowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();

            ItemStack arrow = getArrowStack(p);

            if (arrow != null && Recettes.isNuke(arrow)) {
                this.getServer().getLogger().info("NUKE");
                e.getProjectile().setGlowing(true);
                e.getProjectile().setFireTicks(20*30);
                e.getProjectile().setVelocity(e.getProjectile().getVelocity().multiply(4));
                p.playEffect(p.getLocation(), Effect.WITHER_SHOOT,null);
                e.getProjectile().setCustomNameVisible(true);
                e.getProjectile().setCustomName("!NUKE!");
            }
        }
    }

    ItemStack getArrowStack(Player player) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.getType() == Material.ARROW) {
                return stack;
            }
        }
        return null;
    }


    public void callCommande(String command)
    {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, command);
    }


    @EventHandler
    public void onArrowHit(ProjectileHitEvent event)
    {

        Entity p = event.getEntity();
        this.getServer().getLogger().info(p.getCustomName().toString());
        if(p.getCustomName().contains( "!NUKE!") && p.isGlowing()) {
            this.getServer().getLogger().info("NUKE HIT");
            callCommande("weather thunder 20000");
            p.getWorld().setTime(12540);
            callCommande("say la pluie toxique va tomber...");
            p.getWorld().strikeLightning(p.getLocation());
            p.getWorld().createExplosion(event.getHitBlock().getLocation(),40,true,true);
            p.remove();

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
        PlayerSuperData sd = superdatas.get(p.getName());
        sd.updateSoif();
        sd.updateTemperature();
        sd.appliquerEffetTemperature(sd.getTemperature());
        sd.appliquerEffetsPluie(r);
        sd.updateVarieteAlimentaire();
        sd.appliquerEffetsNutrition();
        scoreboardUpdate(sd);
    }
    private void scoreboardUpdate(PlayerSuperData ps)
    {
        Player p = ps.p;

        Score varieteAlim = p.getScoreboard().getObjective("stats").getScore(ChatColor.GOLD+ "Equilibre Alimentaire :");
        Score soif = p.getScoreboard().getObjective("stats").getScore(ChatColor.BLUE+"Eau :");
        Score temp = p.getScoreboard().getObjective("stats").getScore(ChatColor.RED+"Temperature :");



        varieteAlim.setScore((int)(ps.getVarieteAlimentaire()*100.0));
        soif.setScore((int)(ps.getEau()));
        temp.setScore((int)ps.getTemperature());
    }

}
