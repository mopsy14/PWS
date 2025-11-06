package PWS.simulation.bodies;

import PWS.simulation.Simulation;

public class Star extends SpaceBody {
    public double luminosity;

    public Star(double x, double y, double z, double mass, double r, double vx, double vy, double vz, double luminosity, Simulation simulation) {
        super(x, y, z, mass, r, vx, vy, vz, simulation);
        this.luminosity = luminosity;
    }

    @Override
    public String toString() {
        return "Star{" +
                "luminosity=" + luminosity +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", mass=" + mass +
                ", r=" + r +
                ", vx=" + vx +
                ", vy=" + vy +
                ", vz=" + vz +
                ", simulation=" + simulation +
                '}';
    }
}
