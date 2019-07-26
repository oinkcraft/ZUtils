package com.github.zaphx.discordbot.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;

public class UUIDFetcher {
    /**
     * The GSON object from the mojang service
     */
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    /**
     * The api to look into for UUID
     */
    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    /**
     * The api to look into for the names
     */
    private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
    /**
     * A map of every UUID to name pair
     */
    private static HashMap<UUID, String> names = new HashMap<>();
    /**
     * A map of every name to UUID pair
     */
    private static HashMap<String, UUID> uuids = new HashMap<>();

    public static UUID getUUID(Player p) {
        return getUUID(p.getName());
    }

    /**
     * Gets the UUID of a {@link OfflinePlayer}
     *
     * @param p the {@link OfflinePlayer} to get its UUID from
     * @return the {@link UUID} of the {@link OfflinePlayer}
     */
    public static UUID getUUID(OfflinePlayer p) {
        return getUUID(p.getName());
    }

    /**
     * Gets the {@link UUID} of a {@link String}
     *
     * @param name the name of the {@link Player}
     * @return the {@link UUID} of the {@link Player}
     */
    @SuppressWarnings("deprecation")
    public static UUID getUUID(String name) {
        if (name == null) {
            return null;
        }
        name = name.toLowerCase();

        if (uuids.containsKey(name))
            return uuids.get(name);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, System.currentTimeMillis() / 1000)).openConnection();
            connection.setReadTimeout(5000);

            PlayerUUID player = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), PlayerUUID.class);

            if (player == null)
                return null;

            if (player.getId() == null)
                return null;

            uuids.put(name, player.getId());

            return player.getId();
        } catch (Exception e) {
            Bukkit.getConsoleSender()
                    .sendMessage("Your server has no connection to the mojang servers or is runnig slowly.");
            return Bukkit.getOfflinePlayer(name).getUniqueId();
        }
    }

    /**
     * Gets the name of a {@link UUID}
     *
     * @param uuid the {@link UUID} of the {@link Player}
     * @return the name of the {@link Player}
     */
    public static String getName(UUID uuid) {
        if (names.containsKey(uuid))
            return names.get(uuid);

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            connection.setReadTimeout(5000);

            PlayerUUID[] allUserNames = gson.fromJson(
                    new BufferedReader(new InputStreamReader(connection.getInputStream())), PlayerUUID[].class);
            PlayerUUID currentName = allUserNames[allUserNames.length - 1];

            if (currentName == null)
                return Bukkit.getOfflinePlayer(uuid).getName();

            if (currentName.getName() == null) {
                return Bukkit.getOfflinePlayer(uuid).getName();
            }

            names.put(uuid, currentName.getName());

            return currentName.getName();
        } catch (Exception e) {
            Bukkit.getConsoleSender()
                    .sendMessage("§cYour server has no connection to the mojang servers or is runnig slow.");
            names.put(uuid, Bukkit.getOfflinePlayer(uuid).getName());
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
    }
}


class PlayerUUID {

    /**
     * The player name
     */
    private String name;
    /**
     * The player UUID
     */
    private UUID id;

    /**
     * This method gets the name of the player
     * @return The name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * This method gets the UUID of the player
     * @return The UUID of the player
     */
    public UUID getId() {
        return id;
    }


}