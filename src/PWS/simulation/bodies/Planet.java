package PWS.simulation.bodies;

import PWS.simulation.Simulation;

import java.util.ArrayList;
import java.util.List;

public class Planet extends SpaceBody {
    double receivedLight = 0;

    public Planet(double x, double y, double z, double mass, double r, double vx, double vy, double vz) {
        super(x, y, z, mass, r, vx, vy, vz);
    }

    public double getReceivedLight() {
        return receivedLight*Math.PI*r*r;
    }

    @Override
    public synchronized void updateLighting() {
        for (SpaceBody body : Simulation.INSTANCE.spaceBodies) {
            if (body instanceof Star star) {
                double squared_distance = (star.x-x)*(star.x-x)+(star.y-y)*(star.y-y)+(star.z-z)*(star.z-z) - r*r - star.r*star.r;
                if (isPathEmpty(star, squared_distance)) {
                    receivedLight += star.luminosity / (4 * Math.PI * squared_distance);
                }
            }
        }
    }

    private boolean isPathEmpty(Star star, double squared_distance) {
        final long steps = (long) (Math.sqrt(squared_distance) / (2 * r));
        double currentX = x;
        double currentY = y;
        double currentZ = z;
        double dxStep = (star.x-x) / steps;
        double dyStep = (star.y-y) / steps;
        double dzStep = (star.z-z) / steps;
        List<SpaceBody> bodies = new ArrayList<>();
        for (SpaceBody body : Simulation.INSTANCE.spaceBodies) {
            if (body != this && body != star)
                bodies.add(body);
        }
        for (long i = 0; i < steps; i++) {
            if (isInBody(currentX, currentY, currentZ, bodies)) {
                return false;
            }
            currentX+=dxStep;
            currentY+=dyStep;
            currentZ+=dzStep;
        }
        return true;
    }
    private boolean isInBody(double x, double y, double z, List<SpaceBody> bodies) {
        for (SpaceBody body : bodies) {
            double squared_distance = (body.x - x) * (body.x - x) + (body.y - y) * (body.y - y) + (body.z - z) * (body.z - z);
            if (squared_distance < body.r * body.r)
                return true;
        }
        return false;
    }
}
