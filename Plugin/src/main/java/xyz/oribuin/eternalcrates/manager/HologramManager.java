package xyz.oribuin.eternalcrates.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import xyz.oribuin.eternalcrates.EternalCrates;
import xyz.oribuin.eternalcrates.hologram.CMIHologramHandler;
import xyz.oribuin.eternalcrates.hologram.DecentHologramsHandler;
import xyz.oribuin.eternalcrates.hologram.GHoloHologramHandler;
import xyz.oribuin.eternalcrates.hologram.HologramHandler;
import xyz.oribuin.eternalcrates.hologram.HologramsHologramHandler;
import xyz.oribuin.eternalcrates.hologram.HolographicDisplaysHologramHandler;
import xyz.oribuin.eternalcrates.hologram.TrHologramHandler;
import xyz.oribuin.orilibrary.manager.Manager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Esophose @ RoseStacker
 */
public class HologramManager extends Manager {

    private final EternalCrates plugin = (EternalCrates) this.getPlugin();
    private final Map<String, Class<? extends HologramHandler>> hologramHandlers;
    private HologramHandler hologramHandler;

    public HologramManager(EternalCrates plugin) {
        super(plugin);

        this.hologramHandlers = new LinkedHashMap<>() {{
            this.put("HolographicDisplays", HolographicDisplaysHologramHandler.class);
            this.put("Holograms", HologramsHologramHandler.class);
            this.put("GHolo", GHoloHologramHandler.class);
            this.put("DecentHolograms", DecentHologramsHandler.class);
            this.put("CMI", CMIHologramHandler.class);
            this.put("TrHologram", TrHologramHandler.class);
        }};
    }

    @Override
    public void enable() {
        if (this.attemptLoad())
            return;

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (this.attemptLoad())
                return;

            this.plugin.getLogger().warning("Couldn't find a supported hologram handler.");
        }, 1);
    }

    public boolean attemptLoad() {
        for (Map.Entry<String, Class<? extends HologramHandler>> handler : this.hologramHandlers.entrySet()) {
            if (!Bukkit.getPluginManager().isPluginEnabled(handler.getKey()))
                continue;

            try {
                this.hologramHandler = handler.getValue().getConstructor().newInstance();
                if (!this.hologramHandler.isEnabled())
                    continue;

                this.plugin.getLogger().info(String.format("Loaded %s as the Hologram Handler.", handler.getKey()));
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        return false;
    }

    @Override
    public void disable() {
        if (this.hologramHandler != null) {
            this.hologramHandler.deleteAllHolograms();
            this.hologramHandler = null;
        }
    }

    /**
     * Creates or updates a hologram at the given location
     *
     * @param location The location of the hologram
     * @param text     The text for the hologram
     */
    public void createOrUpdateHologram(Location location, String text) {
        if (this.hologramHandler != null)
            this.hologramHandler.createOrUpdateHologram(location, text);
    }

    /**
     * Deletes a hologram at a given location if one exists
     *
     * @param location The location of the hologram
     */
    public void deleteHologram(Location location) {
        if (this.hologramHandler != null)
            this.hologramHandler.deleteHologram(location);
    }

    /**
     * Deletes all holograms
     */
    public void deleteAllHolograms() {
        if (this.hologramHandler != null)
            this.hologramHandler.deleteAllHolograms();
    }

    /**
     * Checks if the given Entity is part of a hologram
     *
     * @param entity The Entity to check
     * @return true if the Entity is a hologram, otherwise false
     */
    public boolean isHologram(Entity entity) {
        if (this.hologramHandler != null)
            return this.hologramHandler.isHologram(entity);
        return false;
    }

    public HologramHandler getHologramHandler() {
        return hologramHandler;
    }

}
