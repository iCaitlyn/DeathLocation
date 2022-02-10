package private.mc.deathlocation;

import java.io.IOException;
import java.util.logging.Level;
import java.io.InputStream;
import org.bukkit.configuration.Configuration;
import java.io.Reader;
import java.io.InputStreamReader;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

public class Config
{
    private final String fileName;
    private final JavaPlugin plugin;
    private File configFile;
    private FileConfiguration fileConfiguration;
    
    public Config(final JavaPlugin plugin, final String fileName) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }
        this.plugin = plugin;
        this.fileName = fileName;
        final File dataFolder = plugin.getDataFolder();
        if (dataFolder == null) {
            throw new IllegalStateException();
        }
        this.configFile = new File(plugin.getDataFolder(), fileName);
    }
    
    public void reloadConfig() {
        this.fileConfiguration = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
        final InputStream defConfigStream = this.plugin.getResource(this.fileName);
        if (defConfigStream != null) {
            final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration((Reader)new InputStreamReader(defConfigStream));
            this.fileConfiguration.setDefaults((Configuration)defConfig);
        }
    }
    
    public FileConfiguration getConfig() {
        if (this.fileConfiguration == null) {
            this.reloadConfig();
        }
        return this.fileConfiguration;
    }
    
    public void saveConfig() {
        if (this.fileConfiguration != null && this.configFile != null) {
            try {
                this.getConfig().save(this.configFile);
            }
            catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, ex);
            }
        }
    }
    
    public void saveDefaultConfig() {
        if (!this.configFile.exists()) {
            this.plugin.saveResource(this.fileName, false);
        }
    }
}
