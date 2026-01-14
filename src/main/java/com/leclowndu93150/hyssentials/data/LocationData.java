package com.leclowndu93150.hyssentials.data;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import javax.annotation.Nonnull;

public record LocationData(
    @Nonnull String worldName,
    double x,
    double y,
    double z,
    float yaw,
    float pitch
) {
    public Vector3d toPosition() {
        return new Vector3d(x, y, z);
    }

    public Vector3f toRotation() {
        // return new Vector3f(yaw, pitch, 0.0f);
        // The yaw value was messing up the third-person character model, so it was updated to 0.0f.
        return new Vector3f(0.0f, pitch, 0.0f);
    }

    public static LocationData from(String worldName, Vector3d position, Vector3f rotation) {
        return new LocationData(
            worldName,
            position.getX(),
            position.getY(),
            position.getZ(),
            rotation.getYaw(),
            rotation.getPitch()
        );
    }
}
