package dev.handschrift.commands;

import dev.handschrift.init.OpenWorld;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.RoundingMode;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class WorldStatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            final NumberFormat format = NumberFormat.getInstance(Locale.US);
            format.setMaximumFractionDigits(2);
            format.setRoundingMode(RoundingMode.CEILING);

            final ArrayList<String> list = new ArrayList<>();
            final Player player = (Player) sender;
            final OpenWorld world = new OpenWorld(player.getWorld());
            final StringBuilder messageBuilder = new StringBuilder();

            list.add(ChatColor.YELLOW + "-----------------------------------------------------");
            list.add(ChatColor.DARK_AQUA + MessageFormat.format("{0} player(s) have spawned at least once in the world."
                    , world.getTotalPlayersJoined()));
            list.add(MessageFormat.format("The World is {0} years, {1} months and {2} days old and has a file size of {3} GB"
                    , world.getAge().getYears(), world.getAge().getMonths(), world.getAge().getDays(), world.getFileSizeInGB()));
            list.add(ChatColor.YELLOW + "-----------------------------------------------------");

            for (String line : list) {
                messageBuilder.append(line).append("\n");
            }

            player.sendMessage(messageBuilder.toString());

            return true;
        }
        return false;
    }
}
