package Principal;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.io.*;
import java.util.*;

public class Main extends JavaPlugin implements Listener {



    //Materiaux
    List<Material> tntOnly;
    Random r;
    Map<String,PlayerSuperData> superdatas;
    Map<String,BarSet> UI;
    Factions factions;
    public boolean immunise;

    public void saveDatas(String path)
    {
        try {
            String pathsd = path + "/superdatas.ser";
            String pathfact = path + "/factions.ser";
            FileOutputStream fsd = new FileOutputStream(pathsd);
            ObjectOutputStream osd = new ObjectOutputStream(fsd);
            osd.writeObject(superdatas);
            osd.close();
            fsd.close();
            FileOutputStream ff = new FileOutputStream(pathfact);
            ObjectOutputStream of = new ObjectOutputStream(ff);
            of.writeObject(factions);
            of.close();
            of.close();
        }catch (IOException i)
        {
            System.out.println(i.getMessage());
        }
    }
    public Factions chargerFactions(String path)
    {
        String pathfact = path + "/factions.ser";
        File f = new File(pathfact);
        if(f.exists() && !f.isDirectory()) {
            try {
                FileInputStream ff = new FileInputStream(pathfact);
                ObjectInputStream of = new ObjectInputStream(ff);
                Factions ret = (Factions)of.readObject();
                of.close();
                of.close();
                return ret;
            }catch (IOException i)
            {
                System.out.println(i.getMessage());
                return null;
            }catch (ClassNotFoundException c) {
                c.printStackTrace();
                return null;
            }

        }
        return null;
    }
    public Map<String,PlayerSuperData> chargerSuperdatas(String path)
    {
        String pathsd = path + "/superdatas.ser";
        File f = new File(pathsd);
        if(f.exists() && !f.isDirectory()) {
            try {
                FileInputStream ff = new FileInputStream(pathsd);
                ObjectInputStream of = new ObjectInputStream(ff);
                Map<String,PlayerSuperData> ret = (Map<String,PlayerSuperData>)of.readObject();
                of.close();
                of.close();
                return ret;
            }catch (IOException i)
            {
                System.out.println(i.getMessage());
                return null;
            }catch (ClassNotFoundException c) {
                c.printStackTrace();
                return null;
            }
        }
        return null;
    }

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
        if(label.equalsIgnoreCase("rejoindre"))
        {
            if(sender instanceof  Player)
            {
                Player p = (Player) sender;
                factions.ajouterJoueur(args[0],p);
                sender.sendMessage(factions.getStringFactions());
                return true;
            }
        }
        if(label.equalsIgnoreCase("factions"))
        {
            if(sender instanceof  Player)
            {
                Player p = (Player) sender;
                sender.sendMessage(factions.getStringFactions());
                return true;
            }
        }
        if(label.equalsIgnoreCase("partir_factions"))
        {
            if(sender instanceof  Player)
            {
                Player p = (Player) sender;
                factions.supprimerJoueur(p);
                sender.sendMessage(factions.getStringFactions());
                return true;
            }
        }
        if(label.equalsIgnoreCase("win"))
        {
            if(sender instanceof  Player)
            {
                Player p = (Player) sender;
                factions.destructionDeFactionPar(p);
                sender.sendMessage(factions.getStringFactions());
                return true;
            }
        }
        if(label.equalsIgnoreCase("marqueur"))
        {
            if(sender instanceof  Player)
            {
                Player p = (Player) sender;
                factions.donnerMarqueurFaction(p);
                return true;
            }
        }
        if(label.equalsIgnoreCase("spread_factions"))
        {
            if(sender instanceof  Player)
            {
                Player p = (Player) sender;
                factions.spreadFactions(p.getWorld(),this);
                return true;
            }
        }
        if(label.equalsIgnoreCase("lootMe"))
        {
            if(sender instanceof  Player)
            {
                Player p = (Player) sender;
                factions.creerLootZone(this);
                return true;
            }
        }
        if(label.equalsIgnoreCase("set_base"))
        {
            if(sender instanceof  Player)
            {
                Player p = (Player) sender;
                Zone z = factions.creerBase(p);
                if(z!= null)
                {
                    sender.sendMessage(factions.getStringFactions());
                    z.tracerFrontiere(p.getWorld(), r);
                    callCommande("say "+p.getName()+" de la faction "+factions.factionDe(p)+" a créé sa base");
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.getServer().getLogger().info("TurboStop");
        saveDatas("TurboPlinPlayerDatas");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.getServer().getLogger().info("TurboStart");
        Recettes.ajouterRecettes(this);
        r = new Random();
        immunise = true;
        superdatas = chargerSuperdatas("TurboPlinPlayerDatas");
        if(superdatas == null) {
            superdatas = new HashMap<> ();
        }
        UI=new HashMap<>();
        factions = chargerFactions("TurboPlinPlayerDatas");
        if(factions == null) {
            factions = new Factions();
        }

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
    public void onPlayerSaut(PlayerMoveEvent event)
    {
        if(event.getPlayer().getGameMode()!=GameMode.CREATIVE && !event.getPlayer().hasPotionEffect(PotionEffectType.LEVITATION)) {
            if (event.getTo().getY() > event.getFrom().getY()) {
                if (event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation().subtract(0, 1, 0)).getType() == Material.AIR) {
                    PlayerSuperData ps = superdatas.get(event.getPlayer().getName());
                    ps.setFatigue(Math.min(100, ps.getFatigue() + 2));
                }
                if (event.getPlayer().getWorld().getBlockAt(event.getPlayer().getLocation().subtract(0, 1, 0))
                        .getState().getBlockData() instanceof Stairs) {
                    PlayerSuperData ps = superdatas.get(event.getPlayer().getName());
                    ps.setFatigue(Math.min(100, ps.getFatigue() + 1));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player p = event.getPlayer();
            long cd = superdatas.get(p.getName()).cooldown;
            if(System.currentTimeMillis()-cd>300) {
                superdatas.get(p.getName()).cooldown = System.currentTimeMillis();
                if (event.hasBlock()) {
                    Block b = event.getClickedBlock();
                    if (p.getInventory().getItemInMainHand() != null) {
                        ItemStack it = p.getInventory().getItemInMainHand();
                        if(Recettes.isPetard(it)) {
                            if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
                            {
                                b.getWorld().createExplosion(b.getLocation(),1,false,false);
                                b.setType(Material.AIR);
                                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
                            }
                        }
                        else {
                            SecurityDoor.actionSec(it, b, p, event.getAction());
                        }
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
    public void onBlockBreak(BlockBreakEvent event)
    {
        Player p = (Player) event.getPlayer();
        Block b = event.getBlock();
        if(p.getGameMode() == GameMode.SURVIVAL && tntOnly.contains(b.getType()))
        {
            event.setCancelled(true);
        }
        else
        {
            if(b.getType() == Material.DIRT || b.getType() == Material.GRASS_BLOCK)
            {
                if(r.nextDouble()<0.01) {
                    p.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_INGOT));
                }
                if(r.nextDouble()<0.02) {
                    p.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_INGOT));
                }
                if(r.nextDouble()<0.01) {
                    p.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GOLD_NUGGET));
                }
                if(r.nextDouble()<0.02) {
                    p.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.IRON_NUGGET));
                }
                if(r.nextDouble()<0.02) {
                    p.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.FLINT));
                }
                if(r.nextDouble()<0.02) {
                    p.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.CLAY_BALL));
                }
            }
        }
    }

    @EventHandler
    public void onPlacerBlock(BlockPlaceEvent event)
    {
        Player p = event.getPlayer();
        Block b = event.getBlock();
        if(factions.estDansBaseEnnemie(p))
        {
            if(b.getType() != Material.TNT)
            {
                if(b.getType() != Material.FIRE) {
                    event.setCancelled(true);
                    callCommande("tell " + p.getName() + " Vous êtes en faction adverse et ne" +
                            " pouvez pas placer de block ici");
                }
            }
        }
    }

    @EventHandler
    public void onDormir(PlayerBedEnterEvent event)
    {
        Player p = event.getPlayer();
        if(event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            superdatas.get(p.getName()).setFatigue(0);
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
                    if(p.getEquipment().getChestplate()!= null)
                    {
                        zp.getEquipment().setChestplate(p.getEquipment().getChestplate().clone());
                    }
                    if(p.getEquipment().getLeggings()!= null)
                    {
                        zp.getEquipment().setLeggings(p.getEquipment().getLeggings().clone());
                    }
                    if(p.getEquipment().getBoots()!= null)
                    {
                        zp.getEquipment().setBoots(p.getEquipment().getBoots().clone());
                    }
                    if(p.getEquipment().getHelmet()!= null)
                    {
                        zp.getEquipment().setHelmet(p.getEquipment().getHelmet().clone());
                    }
                    if(p.getEquipment().getItemInMainHand()!= null)
                    {
                        zp.getEquipment().setItemInMainHand(p.getEquipment().getItemInMainHand().clone());
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
    public void playerReset(Player p)
    {
        if(superdatas.containsKey(p.getName())) {
            superdatas.remove(p.getName());
        }
        superdatas.put(p.getName(), new PlayerSuperData(p));
        setupAffStats(p);
    }

    private void playerUpdate(Player p)
    {
        PlayerSuperData sd = superdatas.get(p.getName());
        sd.updateSoif();
        sd.updateTemperature();
        sd.updateVarieteAlimentaire();
        sd.updateFatigue();
        if(p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR && !immunise) {
            factions.looter(p);
            sd.appliquerEffetTemperature();
            sd.appliquerEffetsPluie(r);
            sd.appliquerEffetsNutrition();
            sd.appliquerEffetsSoif();
            sd.appliquerEffetsFatigue();
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
        //setupAffStatsScoreBoard(p);
    }
    public void updateAffStats(PlayerSuperData ps)
    {
        UI.get(ps.p.getName()).update(ps);
        //updateAffStatsScoreBoard(ps);
    }

    public void setupAffStatsScoreBoard(Player p)
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
    private void updateAffStatsScoreBoard(PlayerSuperData ps)
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
