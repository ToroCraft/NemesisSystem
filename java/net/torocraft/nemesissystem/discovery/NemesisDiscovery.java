package net.torocraft.nemesissystem.discovery;

import net.minecraft.nbt.NBTTagCompound;
import net.torocraft.nemesissystem.util.nbt.NbtField;
import net.torocraft.nemesissystem.util.nbt.NbtSerializer;

import java.util.UUID;

public class NemesisDiscovery {

    @NbtField
    private final UUID nemesisId;

    @NbtField
    private boolean name;

    @NbtField
    private boolean location;

    @NbtField
    private int[] traits = new int[]{};

    @NbtField
    private int[] strengths = new int[]{};

    @NbtField
    private int[] weaknesses = new int[]{};

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

    public int[] getTraits() { return traits; }

    public void setTraits(int[] traits) { this.traits = traits; }

    public int[] getStrengths() { return strengths; }

    public void setStrengths(int[] strengths) { this.strengths = strengths; }

    public int[] getWeaknesses() { return weaknesses; }

    public void setWeaknesses(int[] weaknesses) { this.weaknesses = weaknesses; }

}

