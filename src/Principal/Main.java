package Principal;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
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
    Map<String,BarSet> UI;

    //Plugin
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("stat"))
        {
            if(sender instanceof  Player)
            {
                Player p = (Player) sender;
                PlayerSuperData ps = superdatas.get(p.getName());
                sender.sendMessage(ps.getStatusString());
                return true;
            }
        }
        return false;
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
        UI=new HashMap<>();
        Material[] matArr = {Material.STONE_BRICK_WALL,Material.STONE_BRICK_SLAB,Material.STONE_BRICK_STAIRS,Material.STONE_BRICKS,Material.IRON_DOOR};
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


    //Events

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = event.getPlayer();
            long cd = superdatas.get(p.getName()).cooldown;
            if(System.currentTimeMillis()-cd>300) {
                superdatas.get(p.getName()).cooldown = System.currentTimeMillis();
                if (event.hasBlock()) {
                    Block b = event.getClickedBlock();
                    if (p.getInventory().getItemInMainHand() != null) {
                        ItemStack it = p.getInventory().getItemInMainHand();
                        SecurityDoor.actionSec(it, b,p);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerConnexion(PlayerJoinEvent event)
    {
        Player p = event.getPlayer();
        playerSetup(p);

    }
    @EventHandler
    public void onPlayerSpawn(PlayerRespawnEvent event)
    {
        callCommande("say respawn");
        if(superdatas.containsKey(event.getPlayer().getName())) {
            superdatas.remove(event.getPlayer().getName());
        }
        superdatas.put(event.getPlayer().getName(), new PlayerSuperData(event.getPlayer()));
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
                    for(ItemStack is : p.getInventory()) {
                        p.getWorld().dropItemNaturally(p.getLocation(),is);
                    }
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

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event)
    {
        if(event != null) {
            Entity p = event.getEntity();
            if (p!=null && p.isCustomNameVisible()) {
                this.getServer().getLogger().info(p.getCustomName().toString());
                if (p.getCustomName().contains("!NUKE!") && p.isGlowing()) {
                    this.getServer().getLogger().info("NUKE HIT");
                    callCommande("weather thunder 20000");
                    p.getWorld().setTime(12540);
                    callCommande("say la pluie toxique va tomber...");
                    p.getWorld().strikeLightning(p.getLocation());
                    p.getWorld().createExplosion(p.getLocation(), 48, true, true);
                    p.remove();

                }
            }
        }
    }

    //Update et setup
    private void playersSetup()
    {
        List<Player> lp = (List<Player>)this.getServer().getOnlinePlayers();
        for (Player p: lp) {
            playerSetup(p);
        }

    }
    private void playersUpdate()
    {
        List<Player> lp = (List<Player>)this.getServer().getOnlinePlayers();
        for (Player p: lp) {
            playerUpdate(p);
        }

    }

    private void playerSetup(Player p)
    {
        if(superdatas.containsKey(p.getName())) {
            superdatas.get(p.getName()).setP(p);
        }
        else {
            superdatas.put(p.getName(), new PlayerSuperData(p));
        }
        setupAffStats(p);
    }
    private void playerUpdate(Player p)
    {
        PlayerSuperData sd = superdatas.get(p.getName());
        sd.updateSoif();
        sd.updateTemperature();
        sd.updateVarieteAlimentaire();

        if(p.getGameMode() != GameMode.CREATIVE) {
            sd.appliquerEffetTemperature();
            sd.appliquerEffetsPluie(r);
            sd.appliquerEffetsNutrition();
            sd.appliquerEffetsSoif();
        }
        updateAffStats(sd);
    }

    public void setupAffStats(Player p)
    {

        if(UI.containsKey(p.getName())) {
            UI.get(p.getName()).del();
            UI.remove(p.getName());
        }
        UI.put(p.getName(), new BarSet(this, p));
        _setupAffStats(p);
    }
    public void updateAffStats(PlayerSuperData ps)
    {
        UI.get(ps.p.getName()).update(ps);
        _updateAffStats(ps);
    }

    public void _setupAffStats(Player p)
    {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard sb = manager.getNewScoreboard();
        Objective obj = sb.registerNewObjective("s_"+p.getName(),"dummy","Statistiques");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score variteAlim = obj.getScore(ChatColor.GOLD+"Equilibre Alimentaire :");
        variteAlim.setScore(100);
        Score soif = obj.getScore(ChatColor.BLUE+"Eau :");
        soif.setScore(100);
        Score temp = obj.getScore(ChatColor.RED+"Temperature :");
        temp.setScore(20);
        p.setScoreboard(sb);
    }
    private void _updateAffStats(PlayerSuperData ps)
    {
        Player p = ps.p;
        Scoreboard sb = p.getScoreboard();
        Objective o = sb.getObjective("s_"+p.getName());
        Score varieteAlim = o.getScore(ChatColor.GOLD+ "Equilibre Alimentaire :");
        Score soif = o.getScore(ChatColor.BLUE+"Eau :");
        Score temp = o.getScore(ChatColor.RED+"Temperature :");

        varieteAlim.setScore((int)(ps.getVarieteAlimentaire()));
        soif.setScore((int)(ps.getEau()));
        temp.setScore((int)ps.getTemperature());
    }


    //Utils
    public static String getHashst(String s)
    {
        String hash = Integer.toString(s.hashCode());
        return hash.substring(0,Math.min(14,hash.length()-1));
    }

    public static void callCommande(String command)
    {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, command);
    }

    ItemStack getArrowStack(Player player) {
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.getType() == Material.ARROW) {
                return stack;
            }
        }
        return null;
    }

}
