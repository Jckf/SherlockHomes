package it.flaten.homes;

import it.flaten.homes.beans.Home;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Homes extends JavaPlugin {
    public final static List<Class<?>> databaseClasses = new ArrayList<Class<?>>() {{
        add(Home.class);
    }};

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.installDDL();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        return Homes.databaseClasses;
    }

    public Home setHome(Player player,String name) {
        Home home = this.getHome(player,name);

        if (home == null) {
            List<Home> homes = this.getHomes(player);

            if (homes.size() >= this.getConfig().getInt("max")) {
                return null;
            }

            home = this.getDatabase().createEntityBean(Home.class);
        }

        home.setPlayer(player.getName());
        home.setName(name);

        Location location = player.getLocation();

        home.setWorld(location.getWorld().getName());
        home.setX(location.getBlockX());
        home.setY(location.getBlockY());
        home.setZ(location.getBlockZ());

        this.getDatabase().save(home);

        return home;
    }

    public Home removeHome(Player player,String name) {
        Home home = this.getHome(player,name);

        if (home == null) {
            return null;
        }

        this.getDatabase().delete(home);

        return home;
    }

    public List<Home> getHomes(Player player) {
        return this.getDatabase().find(Home.class)
            .where()
                .ieq("player",player.getName())
            .orderBy("name DESC")
            .findList();
    }

    public Home getHome(Player player,String name) {
        return this.getDatabase().find(Home.class)
            .where()
                .ieq("player",player.getName())
                .ieq("name",name)
            .findUnique();
    }

    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String args[]) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used in-game.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            return false;
        }

        switch (args[0]) {
            case "list":
                List<Home> homes = this.getHomes(player);

                if (homes.size() == 0) {
                    player.sendMessage(ChatColor.GRAY + "You have no homes.");
                    return true;
                }

                List<String> names = new ArrayList<>();

                for (Home home : homes) {
                    names.add(home.getName());
                }

                player.sendMessage(ChatColor.GRAY + "Homes (" + homes.size() + "/" + this.getConfig().getInt("max") + "): " + StringUtils.join(names,", "));
                return true;

            case "set":
                if (args.length < 2) {
                    return false;
                }

                Home setHome = this.setHome(player,StringUtils.join(args," ",1,args.length));

                if (setHome == null) {
                    player.sendMessage(ChatColor.GRAY + "Could not set home.");
                    return true;
                }

                player.sendMessage(ChatColor.GRAY + "Home \"" + setHome.getName() + "\" set.");
                return true;

            case "remove":
                if (args.length < 2) {
                    return false;
                }

                Home removedHome = this.removeHome(player,StringUtils.join(args," ",1,args.length));

                if (removedHome == null) {
                    player.sendMessage(ChatColor.GRAY + "Could not remove home.");
                    return true;
                }

                player.sendMessage(ChatColor.GRAY + "Home \"" + removedHome.getName() + "\" removed.");
                return true;

            case "goto":
                if (args.length < 2) {
                    return false;
                }

                Home gotoHome = this.getHome(player,StringUtils.join(args," ",1,args.length));

                if (gotoHome == null) {
                    player.sendMessage(ChatColor.GRAY + "No such home.");
                    return true;
                }

                World world = this.getServer().getWorld(gotoHome.getWorld());

                if (world == null) {
                    player.sendMessage(ChatColor.GRAY + "The world \"" + gotoHome.getWorld() + "\" does not exist.");
                    return true;
                }

                player.teleport(new Location(
                    world,
                    gotoHome.getX() + 0.5,
                    gotoHome.getY() + 0.5,
                    gotoHome.getZ() + 0.5
                ));
                return true;
        }

        return false;
    }
}
