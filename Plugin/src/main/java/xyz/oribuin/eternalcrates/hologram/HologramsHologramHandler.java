package xyz.oribuin.eternalcrates.hologram;

import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramEntityController;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.entity.HologramEntity;
import com.sainttx.holograms.api.line.HologramLine;
import com.sainttx.holograms.api.line.TextLine;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.oribuin.eternalcrates.util.PluginUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HologramsHologramHandler implements HologramHandler {

    private final HologramManager hologramManager;
    private final HologramEntityController hologramEntityController;
    private final Set<String> holograms;

    public HologramsHologramHandler() {
        HologramPlugin hologramPlugin = JavaPlugin.getPlugin(HologramPlugin.class);
        this.hologramManager = hologramPlugin.getHologramManager();
        this.hologramEntityController = hologramPlugin.getEntityController();
        this.holograms = new HashSet<>();
    }

    @Override
    public void createOrUpdateHologram(Location location, String text) {
        String key = PluginUtils.locationAsKey(location);
        Hologram hologram = this.hologramManager.getHologram(key);
        if (hologram == null) {
            hologram = new Hologram(PluginUtils.locationAsKey(location), location.clone().add(0, 0.5, 0));
            hologram.addLine(new TextLine(hologram, text));
            this.hologramManager.addActiveHologram(hologram);
            this.holograms.add(key);
        } else {
            for (HologramLine line : new ArrayList<>(hologram.getLines()))
                hologram.removeLine(line);
            hologram.addLine(new TextLine(hologram, text));
        }
    }

    @Override
    public void deleteHologram(Location location) {
        String key = PluginUtils.locationAsKey(location);
        Hologram hologram = this.hologramManager.getHologram(key);
        if (hologram != null) {
            hologram.despawn();
            this.hologramManager.deleteHologram(hologram);
        }
        this.holograms.remove(key);
    }

    @Override
    public void deleteAllHolograms() {
        for (String key : this.holograms) {
            Hologram hologram = this.hologramManager.getHologram(key);
            if (hologram != null) {
                hologram.despawn();
                this.hologramManager.removeActiveHologram(hologram);
            }
        }
        this.holograms.clear();
    }

    @Override
    public boolean isHologram(Entity entity) {
        HologramEntity hologramEntity = this.hologramEntityController.getHologramEntity(entity);
        if (hologramEntity == null)
            return false;

        HologramLine hologramLine = hologramEntity.getHologramLine();
        if (hologramLine == null)
            return false;

        Hologram hologram = hologramLine.getHologram();
        if (hologram == null)
            return false;

        String id = hologram.getId();
        if (id == null)
            return false;

        return this.holograms.stream().anyMatch(id::equals);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
