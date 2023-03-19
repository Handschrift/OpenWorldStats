package dev.handschrift.init;

import dev.handschrift.commands.WorldStatsCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public class OpenWorldStats extends JavaPlugin implements Listener {
    @EventHandler
    public void ex(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onEnable() {

        final FileConfiguration configuration = this.getConfig();
        configuration.options().copyDefaults(true);
        configuration.addDefault("filesizeupdate_in_ticks", 72000);

        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("worldstats").setExecutor(new WorldStatsCommand());

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        if (configuration.getInt("filesizeupdate_in_ticks") > 0) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                this.getLogger().log(Level.INFO, "Calculating the filesize...");
                final ArrayList<String> lines = new ArrayList<>();
                final File datFile = new File(this.getDataFolder(), "size.dat");

                for (World world : Bukkit.getServer().getWorlds()) {
                    lines.add(world.getName() + ":" + calculateFileSize(world.getWorldFolder()));
                }
                try {
                    if (!datFile.exists()) {
                        datFile.createNewFile();
                    }
                    Files.write(datFile.toPath(), lines, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.getLogger().log(Level.INFO, "Finished calculating the filesize");
            }, 0L, configuration.getInt("filesizeupdate_in_ticks"));
        }

    }

    private long calculateFileSize(File file) {

        final AtomicLong size = new AtomicLong(0);

        try {
            Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new AssertionError("An error occurred while calculating the size of the file");
        }

        return size.get();

    }

}
