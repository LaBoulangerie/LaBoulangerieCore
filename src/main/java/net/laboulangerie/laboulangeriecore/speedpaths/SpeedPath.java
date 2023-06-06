package net.laboulangerie.laboulangeriecore.speedpaths;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class SpeedPath {

    private static final Material ANY = Material.BARRIER;
    private List<Material> materials;
    private float speed;

    public SpeedPath(List<Material> materials, float speed) {
        this.materials = materials;
        this.speed = speed;
    }

    public boolean isOnIt(Location location) {
        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i) == ANY)
                continue;
            if (getMaterialAtLocation(location, i) != materials.get(i))
                return false;
        }
        return true;
    }

    private Material getMaterialAtLocation(Location location, int yOffset) {
        return snapToBlock(location).subtract(0, yOffset, 0).getBlock().getRelative(BlockFace.DOWN).getType();
    }

    private Location snapToBlock(Location location) {
        return location.clone().set(location.blockX(), Math.round(location.y()), location.blockZ());
    }

    public float getSpeed() {
        return speed;
    }
}
