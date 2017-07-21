package net.torocraft.nemesissystem.discovery;

import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.nbt.NbtField;
import net.torocraft.nemesissystem.util.nbt.NbtSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NemesisDiscovery {

    @NbtField
    private final UUID nemesisId;

    @NbtField
    private boolean name;

    @NbtField
    private boolean location;

    @NbtField
    private List<Integer> traits = new ArrayList<>();

    @NbtField
    private List<Integer> strengths = new ArrayList<>();

    @NbtField
    private List<Integer> weaknesses = new ArrayList<>();

    public NemesisDiscovery(final UUID nemesisId) {
        this.nemesisId = nemesisId;
    }

    public void readFromNBT(NBTTagCompound c) {
        NbtSerializer.read(c, this);
    }

    public void writeToNBT(NBTTagCompound c) {
        NbtSerializer.write(c, this);
    }

    public UUID getNemesisId() { return nemesisId; }

    public boolean isName() { return name; }

    public void setName(boolean name) { this.name = name; }

    public boolean isLocation() { return location; }

    public void setLocation(boolean location) { this.location = location; }

    public List<Integer> getTraits() { return traits; }

    public void setTraits(List<Integer> traits) { this.traits = traits; }

    public List<Integer> getStrengths() { return strengths; }

    public void setStrengths(List<Integer> strengths) { this.strengths = strengths; }

    public List<Integer> getWeaknesses() { return weaknesses; }

    public void setWeaknesses(List<Integer> weaknesses) { this.weaknesses = weaknesses; }

}

