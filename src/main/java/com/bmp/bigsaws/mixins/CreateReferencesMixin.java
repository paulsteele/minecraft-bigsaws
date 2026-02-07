package com.bmp.bigsaws.mixins;

import com.bmp.bigsaws.LargeStructureTracker;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

        for (LargeStructureTracker.TrackedStructure tracked : LargeStructureTracker.getAll()) {
            // Skip if this chunk doesn't intersect the structure bounding box
            if (!tracked.boundingBox().intersects(minBlockX, minBlockZ, minBlockX + 15, minBlockZ + 15)) {
                continue;
            }

            int startChunkX = ChunkPos.getX(tracked.startChunkLong());
            int startChunkZ = ChunkPos.getZ(tracked.startChunkLong());

            // Skip if already handled by vanilla's 8-chunk scan
            if (Math.abs(startChunkX - chunkX) <= 8 && Math.abs(startChunkZ - chunkZ) <= 8) {
                continue;
            }

            // Add the reference using data stored in the tracker — no distant chunk access needed
            structureManager.addReferenceForStructure(sectionPos, tracked.structure(), tracked.startChunkLong(), chunk);
        }
    }
}
