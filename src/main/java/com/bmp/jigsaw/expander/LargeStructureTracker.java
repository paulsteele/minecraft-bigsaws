package com.bmp.jigsaw.expander;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LargeStructureTracker {
    private static final ConcurrentHashMap<Long, BoundingBox> STRUCTURE_BOUNDS = new ConcurrentHashMap<>();

    public static void register(ChunkPos chunkPos, BoundingBox boundingBox) {
        STRUCTURE_BOUNDS.put(chunkPos.toLong(), boundingBox);
    }

    public static Collection<Map.Entry<Long, BoundingBox>> getAll() {
        return STRUCTURE_BOUNDS.entrySet();
    }

    public static void clear() {
        STRUCTURE_BOUNDS.clear();
    }
}
