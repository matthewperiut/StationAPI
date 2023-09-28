package net.modificationstation.stationapi.api.worldgen.surface.condition;

import net.minecraft.level.Level;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.Vec3f;

public class SlopeSurfaceCondition implements SurfaceCondition {
	private final Vec3f a = new Vec3f();
	private final Vec3f b = new Vec3f();
	private final boolean greater;
	private final float angle;
	
	public SlopeSurfaceCondition(float angle, boolean degrees, boolean greater) {
		this.angle = degrees ? (float) Math.toRadians(angle) : angle;
		this.greater = greater;
	}
	
	@Override
	public boolean canApply(Level level, int x, int y, int z, BlockState state) {
		a.set(2, level.getHeight(x + 1, z) - level.getHeight(x - 1, z), 0);
		a.normalize();
		
		b.set(0, level.getHeight(x, z + 1) - level.getHeight(x, z - 1), 2);
		b.normalize();
		
		a.cross(b);
		a.normalize();
		
		b.set(a.getX(), 0, a.getZ());
		b.normalize();
		
		float dot = a.dot(b);
		return greater ? angle < dot : angle > dot;
	}
}
