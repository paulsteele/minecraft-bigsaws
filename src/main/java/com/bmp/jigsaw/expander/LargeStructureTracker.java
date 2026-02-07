package com.bmp.jigsaw.expander;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class LargeStructureTracker {

    public record TrackedStructure(long startChunkLong, BoundingBox boundingBox, Structure structure, StructureStart structureStart) {}

    private static final ConcurrentHashMap<Long, TrackedStructure> STRUCTURES = new ConcurrentHashMap<>();

    public static void register(ChunkPos chunkPos, BoundingBox boundingBox, Structure structure, StructureStart structureStart) {
        long key = chunkPos.toLong();
        STRUCTURES.put(key, new TrackedStructure(key, boundingBox, structure, structureStart));
    }

    public static TrackedStructure get(long startChunkLong) {
        return STRUCTURES.get(startChunkLong);
    }

    public static Collection<TrackedStructure> getAll() {
        return STRUCTURES.values();
    }

    public static void clear() {
        STRUCTURES.clear();
    }
}
