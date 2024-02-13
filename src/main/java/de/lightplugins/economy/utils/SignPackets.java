package de.lightplugins.economy.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import de.lightplugins.economy.master.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.BiPredicate;

public class SignPackets {

    private final Main plugin;

    private final Map<Player, Menu> inputs;

    public SignPackets(Main plugin) {
        this.plugin = plugin;
        this.inputs = new HashMap<>();
        this.listen();
    }

    public Menu newMenu(List<String> text) {
        return new Menu(text);
    }

    private void listen() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();

                Menu menu = inputs.remove(player);

                if (menu == null) {
                    return;
                }
                event.setCancelled(true);

                boolean success = menu.response.test(player, event.getPacket().getStringArrays().read(0));

                if (!success && menu.reopenIfFail && !menu.forceClose) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> menu.open(player), 2L);
                }
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        player.sendBlockChange(menu.location, menu.location.getBlock().getBlockData());
                    }
                }, 2L);
            }
        });
    }

    public final class Menu {

        private final List<String> text;

        private BiPredicate<Player, String[]> response;
        private boolean reopenIfFail;

        private Location location;

        private boolean forceClose;

        Menu(List<String> text) {
            this.text = text;
        }

        public Menu reopenIfFail(boolean value) {
            this.reopenIfFail = value;
            return this;
        }

        public Menu response(BiPredicate<Player, String[]> response) {
            this.response = response;
            return this;
        }

        /**
         * Open the sign GUI for the player
         *
         * @param player the player to open the sign GUI for
         */
        public void open(Player player) {
            // Ensure player is not null
            Objects.requireNonNull(player, "player");

            // Return if the player is not online
            if (!player.isOnline()) {
                return;
            }

            // Set the location of the sign
            location = player.getLocation();
            location.setY(location.getBlockY() - 4);  //  -4 offset because the sign should under the player

            // Create and send the sign block change
            player.sendBlockChange(location, Material.OAK_SIGN.createBlockData());

            // Set the text for the sign
            String[] signText = new String[4];  // maybe String[3] is working ?!
            for (int i = 0; i < 3; i++) {
                signText[i] = color(text.get(i)); // Assuming color method is defined elsewhere
            }
            player.sendSignChange(location, signText);

            // Create and send the packet to open the sign editor
            PacketContainer openSign = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
            BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            openSign.getBlockPositionModifier().write(0, position);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign);

            // Add the player and this sign to the inputs map
            inputs.put(player, this);
        }

        public void close(Player player, boolean force) {
            this.forceClose = force;
            if (player.isOnline()) {
                player.closeInventory();
            }
        }

        public void close(Player player) {
            close(player, false);
        }

        private String color(String input) {
            return Main.colorTranslation.hexTranslation(input);
        }
    }
}
