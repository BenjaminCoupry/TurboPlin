package Principal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class Bar {
    private final Main plugin;

    public BossBar getBar() {
        return bar;
    }

    private BossBar bar;

    public Bar(Main plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player p)
    {
        bar.addPlayer(p);
    }
    public void createWaterBar()
    {
        bar = Bukkit.createBossBar(ChatColor.BLUE + "Eau", BarColor.BLUE, BarStyle.SOLID);
        bar.setVisible(true);
    }
    public void createAlimBar()
    {
        bar = Bukkit.createBossBar(ChatColor.DARK_GREEN + "Variete Alimentaire", BarColor.GREEN, BarStyle.SOLID);
        bar.setVisible(true);
    }
    public void createTempBar()
    {
        bar = Bukkit.createBossBar(ChatColor.RED + "Temperature", BarColor.RED, BarStyle.SOLID);
        bar.setVisible(true);
    }
    public void createFatigueBar()
    {
        bar = Bukkit.createBossBar(ChatColor.YELLOW + "Fatigue", BarColor.RED, BarStyle.SOLID);
        bar.setVisible(true);
    }

    public void updateValue(double val)
    {
        double k = val/100.0;
        k = Math.min(Math.max(0,k),1);
        bar.setProgress(k);

    }
    public void del()
    {
        bar.setVisible(false);
        bar.removeAll();
    }
}
