package com.bmp.jigsaw.expander.mixins;

import com.bmp.jigsaw.expander.LargeJigsawStructure;
import com.bmp.jigsaw.expander.LargeStructureTracker;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ChunkGenerator.class)
public class CreateReferencesMixin {

    @Inject(method = "createReferences", at = @At("TAIL"))
    private void addLargeStructureReferences(WorldGenLevel level, StructureManager structureManager, ChunkAccess chunk, CallbackInfo ci) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        int minBlockX = chunkPos.getMinBlockX();
        int minBlockZ = chunkPos.getMinBlockZ();
        SectionPos sectionPos = SectionPos.bottomOf(chunk);

        for (Map.Entry<Long, BoundingBox> entry : LargeStructureTracker.getAll()) {
            BoundingBox structureBB = entry.getValue();

            // Skip if this chunk doesn't intersect the structure bounding box
            if (!structureBB.intersects(minBlockX, minBlockZ, minBlockX + 15, minBlockZ + 15)) {
                continue;
            }

            // Determine the start chunk from the stored key
            long startChunkLong = entry.getKey();
            int startChunkX = ChunkPos.getX(startChunkLong);
            int startChunkZ = ChunkPos.getZ(startChunkLong);

            // Skip if already handled by vanilla's 8-chunk scan
            if (Math.abs(startChunkX - chunkX) <= 8 && Math.abs(startChunkZ - chunkZ) <= 8) {
                continue;
            }

            // Fetch the structure start chunk and add references for overlapping pieces
            try {
                ChunkAccess startChunk = level.getChunk(startChunkX, startChunkZ);
                for (StructureStart structureStart : startChunk.getAllStarts().values()) {
                    if (structureStart.isValid()
                            && structureStart.getStructure() instanceof LargeJigsawStructure
                            && structureStart.getBoundingBox().intersects(minBlockX, minBlockZ, minBlockX + 15, minBlockZ + 15)) {
                        structureManager.addReferenceForStructure(sectionPos, structureStart.getStructure(), startChunkLong, chunk);
                    }
                }
            } catch (Exception e) {
                // If the chunk can't be accessed (e.g., outside generation range), skip silently
            }
        }
    }
}
