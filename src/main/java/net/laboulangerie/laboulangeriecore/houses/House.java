package net.laboulangerie.laboulangeriecore.houses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

public class House implements Serializable {
    private List<Location> blocks = new ArrayList<>();
    private UUID uuid;
    private String name;
    private List<HouseFlags> flags = new ArrayList<>();
    private List<UUID> members = new ArrayList<>();
    private int[] anchor = {0, 0, 0};

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

    /**
     * Get UUIDs of players in this house
     * @return
     */
    public List<UUID> getMembers() {
        return members;
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

    public void addMember(UUID memberId) {
        members.add(memberId);
    }

    public void removeMember(UUID memberId) {
        members.remove(memberId);
    }

    public boolean hasMember(UUID memberId) {
        return members.contains(memberId);
    }
    /**
     * The "anchor" is the coordinates of the average of all blocks in
     * the house
     * @return a 3 wide uni-dimensional array containing respectively, the x, y & z values
     */
    public int[] getAnchor() {
        return anchor;
    }

    public void setAnchor(int[] anchor) {
        if (anchor.length != 3) throw new IllegalArgumentException("Array must have a length of 3!");
        this.anchor = anchor;
    }
}
