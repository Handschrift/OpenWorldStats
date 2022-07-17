package dev.handschrift.init;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class OpenWorld {

    private final String name;
    private Period age;
    private final double fileSize;

    private final int totalPlayersJoined;


    public OpenWorld(World world) {
        this.name = world.getName();
        try {
            this.fileSize = calculateFileSize(new File(JavaPlugin.getPlugin(OpenWorldStats.class).getDataFolder(), "size.dat"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.totalPlayersJoined = Bukkit.getOfflinePlayers().length;
        try {
            this.age = Period.between(LocalDate.ofInstant(getAgeOfWorld(world), ZoneId.systemDefault()), LocalDate.now());
        } catch (IOException exception) {
            this.age = Period.ZERO;
        }
    }

    private Instant getAgeOfWorld(World world) throws IOException {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            return Files.readAttributes(world.getWorldFolder().toPath(), BasicFileAttributes.class).creationTime().toInstant();
        } else {
            return Instant.ofEpochMilli(System.currentTimeMillis() - (world.getGameTime() / 20 * 1000));
        }
    }

    private long calculateFileSize(File file) throws IOException {

        for (String line : Files.readAllLines(file.toPath())) {
            final String name = line.split(":")[0];
            final long currentSize = Long.parseLong(line.split(":")[1]);

            if (this.name.toLowerCase().equals(name)) {
                return currentSize;
            }

        }
        return 0;

    }

    public double getFileSizeInGB() {
        return fileSize / 1048576.0 / 1000.0;
    }

    public String getName() {
        return name;
    }

    public Period getAge() {
        return age;
    }

    public int getTotalPlayersJoined() {
        return totalPlayersJoined;
    }
}
