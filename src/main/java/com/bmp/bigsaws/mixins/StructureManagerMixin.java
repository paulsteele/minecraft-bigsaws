package com.bmp.bigsaws.mixins;

import com.bmp.bigsaws.LargeJigsawStructure;
import com.bmp.bigsaws.LargeStructureTracker;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.StructureAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(StructureManager.class)
public abstract class StructureManagerMixin {

    @Shadow
    @Final
    private LevelAccessor level;

    @Shadow
    public abstract StructureStart getStartForStructure(SectionPos sectionPos, Structure structure, StructureAccess structureAccess);

    @Inject(method = "fillStartsForStructure", at = @At("HEAD"), cancellable = true)
    private void handleLargeStructureRefs(Structure structure, LongSet structureRefs, Consumer<StructureStart> startConsumer, CallbackInfo ci) {
        if (!(structure instanceof LargeJigsawStructure)) return;

        for (long ref : structureRefs) {
            // Check tracker first — serves the StructureStart from memory,
            // avoiding the distant chunk access that would crash in WorldGenRegion
            LargeStructureTracker.TrackedStructure tracked = LargeStructureTracker.get(ref);
            if (tracked != null) {
                if (tracked.structureStart().isValid()) {
                    startConsumer.accept(tracked.structureStart());
                }
                continue;
            }

            // Not in tracker (e.g. loaded from disk after restart) — use vanilla's chunk access.
            // This is safe because after save/load, chunks are served from disk, not WorldGenRegion.
            SectionPos sectionPos = SectionPos.of(new ChunkPos(ref), this.level.getMinSection());
            StructureStart start = this.getStartForStructure(
                    sectionPos, structure, this.level.getChunk(sectionPos.x(), sectionPos.z(), ChunkStatus.STRUCTURE_STARTS)
            );
            if (start != null && start.isValid()) {
                startConsumer.accept(start);
            }
        }

        ci.cancel();
    }
}
