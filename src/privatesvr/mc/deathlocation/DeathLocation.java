package privatesvr.mc.deathlocation;

import org.bukkit.event.EventHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DeathLocation extends JavaPlugin implements Listener
{
    private PluginDescriptionFile pdffile;
    private Config log;

    
    public DeathLocation() {
        this.pdffile = this.getDescription();
    }
    
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.reloadConfig();
        this.getConfig().options().header("Set the message sent to the player here! You can use '&' color characters!\nMake sure to include %x, %y and %z as placeholders for the coordinates!\nYou can now also set whether the latest death log should be enabled or not! This feature logs the last death of the player (Includes time and location)");
        if (!this.getConfig().contains("message")) {
            this.getConfig().set("message", (Object)"&7You died at x: &b%x &7y: &b%y &7z: &b%z&7!");
        }
        else if (!this.getConfig().getString("message").contains("%x") || !this.getConfig().getString("message").contains("%y") || !this.getConfig().getString("message").contains("%z")) {
            System.err.println(ChatColor.RED + "You need to specify %x, %y and %z placeholders in the message! Message has been set to default!");
            this.getConfig().set("message", (Object)"&7You died at x: &b%x &7y: &b%y &7z: &b%z&7!");
        }
        if (!this.getConfig().contains("enable-death-log")) {
            this.getConfig().set("enable-death-log", (Object)true);
        }
        if (!this.getConfig().contains("use-permission-for-log")) {
            this.getConfig().set("use-permission-for-log", (Object)true);
        }
        this.saveConfig();
        this.log = new Config(this, "latestdeathlog.yml");
    }
    
    public void onDisable() {
        this.saveConfig();
    }
    
    public boolean onCommand(final CommandSender s, final Command cmd, final String label, final String[] args) {
        if (cmd.getName().equalsIgnoreCase("dl")) {
            if (args.length == 0) {                     
            	s.sendMessage(this.msg("&4&l&m------&r &c&lDeathLocation &r&8| &r&7Help &r&4&l&m------"));
            	s.sendMessage(this.msg("&7 - &4/dl &7 - Shows this help message."));
            	s.sendMessage(this.msg("&7 - &4/dl about &7 - Shows information about the plugin, like the plugin creator(s)."));
            	s.sendMessage(this.msg("&7 - &4/dl check &7 - Shows the coordinates of your last death."));
            	if (s instanceof Player) {
                    final Player p = (Player)s;
                    if (p.hasPermission("dl.lookup")) {
                    	s.sendMessage(this.msg("&7 - &4/dl lookup <player>&7 - Shows the coordinates of the player's last death."));
                    }
                    if (p.hasPermission("dl.return")) {
                    	p.sendMessage(this.msg("&7 - &4/dl return [safe]&7 - Sends you back to your exact last death location. May be dangerous depending on how you died."));
                    }
                    if (p.hasPermission("dl.reload")) {
                    	p.sendMessage(this.msg("&7 - &4/dl reload&7 - Reloads the plugin."));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("about")) {
            	s.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "dl" + ChatColor.DARK_GRAY + "]" + ChatColor.GRAY + " DeathLocation version " + ChatColor.RED + this.pdffile.getVersion() + ChatColor.GRAY + " by Yupie (Yupie123), iCaitlyn");
                return true;
            }
            else if (args[0].equalsIgnoreCase("lookup")) {
                if (s instanceof Player) {
                    final Player p = (Player)s;
                    if (!p.hasPermission("dl.lookup")) {
                        p.sendMessage(this.msg("&cNo can do!"));
                        return false;
                    }
                }
                if (args.length != 2) {
                    s.sendMessage(this.msg("&cUse /dl lookup <player> to look up a players latest death location."));
                    return false;
                }
                try {
	                if (!this.log.getConfig().contains(this.getServer().getPlayer(args[1]).getUniqueId().toString())) {
	                    s.sendMessage(this.msg("&cThat player isn't logged!"));
	                    return false;
	                }
                } catch(Exception ex)
                {
                	s.sendMessage(this.msg("&cThat player is offline!"));
                    return false;
                }
                final ConfigurationSection section = this.log.getConfig().getConfigurationSection(this.getServer().getPlayer(args[1]).getUniqueId().toString());
                s.sendMessage(this.msg("&7Player &e" + section.getString("player") + " &7died at x: &e" + section.getString("location.x") + " &7y: &e" + section.getString("location.y") + " &7z: &e" + section.getString("location.z") + " &7in the world &e" + section.getString("location.world") + " &7on &e" + section.getString("time") + "&7."));
            }
            else if (args[0].equalsIgnoreCase("check")) {
                if (s instanceof Player) {
                    final Player p = (Player)s;
                    if (!p.hasPermission("dl.use")) {
                        p.sendMessage(this.msg("&cNo can do!"));
                        return false;
                    }
                }
                if (args.length >= 2) {
                    s.sendMessage(this.msg("&cUse /dl check to look up your latest death location."));
                    return false;
                }
	            if (!this.log.getConfig().contains(this.getServer().getPlayer(s.getName()).getUniqueId().toString())) {
	                s.sendMessage(this.msg("&cNone of your deaths has been logged!"));
	                return false;
	            }
                final ConfigurationSection section = this.log.getConfig().getConfigurationSection(this.getServer().getPlayer(s.getName()).getUniqueId().toString());
                s.sendMessage(this.msg("&7You died at x: &e" + section.getString("location.x") + " &7y: &e" + section.getString("location.y") + " &7z: &e" + section.getString("location.z") + " &7in the world &e" + section.getString("location.world") + " &7on &e" + section.getString("time") + "&7."));
            }
            else if (args[0].equalsIgnoreCase("return")) {
            	if (s instanceof Player) {
                    final Player p = (Player)s;
                    if (!p.hasPermission("dl.return")) {
                        p.sendMessage(this.msg("&cNo can do!"));
                        return false;
                    }
                }
                if (args.length >= 4) {
                    s.sendMessage(this.msg("&cUse /dl return to return to your last death location. May be dangerous depending on how you died."));
                    return false;
                }
                else if (args.length == 2 && args[1].equalsIgnoreCase("safe")) {
                	if (!this.log.getConfig().contains(this.getServer().getPlayer(s.getName()).getUniqueId().toString())) {
		                s.sendMessage(this.msg("&cNone of your deaths has been logged!"));
		                return false;
		            }
                	
                	final ConfigurationSection section = this.log.getConfig().getConfigurationSection(this.getServer().getPlayer(s.getName()).getUniqueId().toString());
                	int y = Bukkit.getWorld(section.getString("location.world")).getHighestBlockYAt(Integer.parseInt(section.getString("location.x")), Integer.parseInt(section.getString("location.z")));
                	
                	List<PotionEffect> potions = new ArrayList<PotionEffect>();
	                potions.add(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 5 * 2, 1));
	                potions.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 8 * 2, 2));
	                potions.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 8 * 2, 2));
	                potions.add(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8 * 2, 2));
                	
                	((Player)s).teleport(new Location(Bukkit.getWorld(section.getString("location.world")), Double.parseDouble(section.getString("location.x")), y + 3, Double.parseDouble(section.getString("location.z"))));
                	((Player)s).sendMessage(this.msg("&7Returning you to your last death location (safely)..."));
                	
	                ((Player)s).addPotionEffects(potions);
                }
                else {
		            if (!this.log.getConfig().contains(this.getServer().getPlayer(s.getName()).getUniqueId().toString())) {
		                s.sendMessage(this.msg("&cNone of your deaths has been logged!"));
		                return false;
		            }
	                final ConfigurationSection section = this.log.getConfig().getConfigurationSection(this.getServer().getPlayer(s.getName()).getUniqueId().toString());
	                
	                List<PotionEffect> potions = new ArrayList<PotionEffect>();
	                potions.add(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 5, 1));
	                potions.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 8, 2));
	                potions.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 8, 2));
	                potions.add(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 2));
	                
	                ((Player)s).teleport(new Location(Bukkit.getWorld(section.getString("location.world")), Double.parseDouble(section.getString("location.x")), Double.parseDouble(section.getString("location.y")) + 1, Double.parseDouble(section.getString("location.z"))));
	                s.sendMessage(this.msg("&7Returning you to your last death location..."));
	                
	                ((Player)s).addPotionEffects(potions);
                }
            }
            else if (args[0].equalsIgnoreCase("reload")) {
                if (s instanceof Player) {
                    final Player p = (Player)s;
                    if (!p.hasPermission("dl.reload")) {
                        p.sendMessage(this.msg("&cNo can do!"));
                        return false;
                    }
                }
              	
                this.reloadConfig();
                
                this.getConfig().options().header("Set the message sent to the player here! You can use '&' color characters!\nMake sure to include %x, %y and %z as placeholders for the coordinates!\nYou can now also set whether the latest death log should be enabled or not! This feature logs the last death of the player (Includes time and location)");
                if (!this.getConfig().contains("message")) {
                	this.getConfig().set("message", (Object)"&7You died at x: &b%x &7y: &b%y &7z: &b%z&7!");
                	this.saveConfig();
                }
                else if (!this.getConfig().getString("message").contains("%x") || !this.getConfig().getString("message").contains("%y") || !this.getConfig().getString("message").contains("%z")) {
                	System.err.println(ChatColor.RED + "You need to specify %x, %y and %z placeholders in the message! Message has been set to default!");
                	this.getConfig().set("message", (Object)"&7You died at x: &b%x &7y: &b%y &7z: &b%z&7!");
                	this.saveConfig();
                }
                if (!this.getConfig().contains("enable-death-log")) {
                	this.getConfig().set("enable-death-log", (Object)true);
                	this.saveConfig();
                }
                if (!this.getConfig().contains("use-permission-for-log")) {
                	this.getConfig().set("use-permission-for-log", (Object)true);
                	this.saveConfig();
                }
                
                s.sendMessage(this.msg("&7Config for DeathLocation has been reloaded!"));
            }
            else {
                s.sendMessage(this.msg("&cUnknown sub-command!"));
            }
        }
        return false;
    }
    
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player p = e.getEntity();
        if (p.hasPermission("dl.use") || p.isOp()) {
            final int x = p.getLocation().getBlockX();
            final int y = p.getLocation().getBlockY();
            final int z = p.getLocation().getBlockZ();
            String msg = this.getConfig().getString("message");
            msg = msg.replaceAll("%x", String.valueOf(x)).replaceAll("%y", String.valueOf(y)).replaceAll("%z", String.valueOf(z));
            p.sendMessage(this.msg(msg));
        }
        if (p.hasPermission("dl.return")) {
        	if (p.hasPermission("essentials.back") && !p.hasPermission("essentials.back.ondeath")) {
        		p.sendMessage(this.msg("&6Use the &l/dl return &6command to return to your death point, if /back does not work."));
        	}
        	else {
        		p.sendMessage(this.msg("&6Use the &l/dl return &6command to return to your death point."));
        	}
        }
        if (this.getConfig().getBoolean("enable-death-log")) {
            if (this.getConfig().getBoolean("use-permission-for-log") && !p.hasPermission("dl.use") && !p.isOp()) {
                return;
            }
            if (!this.log.getConfig().contains(p.getUniqueId().toString())) {
                this.log.getConfig().createSection(p.getUniqueId().toString());
            }
            final ConfigurationSection section = this.log.getConfig().getConfigurationSection(p.getUniqueId().toString());
            final Date d = new Date();
            final SimpleDateFormat format = new SimpleDateFormat();
            section.set("player", (Object)p.getName());
            section.set("time", (Object)format.format(d).toString());
            section.set("location.x", (Object)p.getLocation().getBlockX());
            section.set("location.y", (Object)p.getLocation().getBlockY());
            section.set("location.z", (Object)p.getLocation().getBlockZ());
            section.set("location.world", (Object)p.getLocation().getWorld().getName());
            this.log.saveConfig();
        }
    }
    
    private String msg(final String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
