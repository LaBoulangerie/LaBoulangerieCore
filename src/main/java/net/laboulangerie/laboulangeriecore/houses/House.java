package net.laboulangerie.laboulangeriecore.houses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

public class House implements ConfigurationSerializable {
    private List<Location> blocks = new ArrayList<>();
    private UUID uuid;
    private String name;
    private List<HouseFlags> flags = new ArrayList<>();
    private List<UUID> members = new ArrayList<>();
    private Location anchor;

    public House(String name) {
        this.name = name;
        uuid = UUID.randomUUID();
    }

    @SuppressWarnings("unchecked")
    public House(Map<String, Object> data) {
        blocks = (List<Location>) data.get("blocks");
        uuid = UUID.fromString((String) data.get("uuid"));
        name = (String) data.get("name");
        flags = ((List<String>) data.get("flags")).stream().map(HouseFlags::valueOf).collect(Collectors.toList());
        members = (List<UUID>) data.get("members");
        anchor = (Location) data.get("anchor");
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

    public void addBlocks(List<Location> blocks) {
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
     */
    public Location getAnchor() {
        return anchor;
    }

    public void setAnchor(Location anchor) {
        this.anchor = anchor;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("blocks", blocks);
        map.put("uuid", uuid.toString());
        map.put("anchor", anchor);
        map.put("name", name);
        map.put("flags", flags.stream().map(HouseFlags::toString).collect(Collectors.toList()));
        map.put("members", members);
        return map;
    }
}
