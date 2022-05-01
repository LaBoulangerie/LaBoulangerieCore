package net.laboulangerie.laboulangeriecore.houses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

public class House implements Serializable {
    private List<Location> blocks = new ArrayList<Location>();
    private UUID uuid;
    private String name;
    private List<HouseFlags> flags = new ArrayList<HouseFlags>();

    public House(String name) {
        this.name = name;
        uuid = UUID.randomUUID();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public List<Location> getBlocks() {
        return blocks;
    }

    public List<HouseFlags> getFlags() {
        return flags;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addBlock(Location block) {
        blocks.add(block);
    }

    public void addBlocks(List<Location> bLocations) {
        this.blocks.addAll(blocks);
    }

    public void removeBlock(Location block) {
        blocks.remove(block);
    }

    public boolean hasBlock(Location block) {
        return blocks.contains(block);
    }

    public void addFlag(HouseFlags flag) {
        flags.add(flag);
    }

    public void removeFlag(HouseFlags flag) {
        flags.remove(flag);
    }
}
