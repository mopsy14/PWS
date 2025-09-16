package PWS.simulation.bodies;

public class Star extends SpaceBody {
    public double luminosity;

    public Star(double x, double y, double z, double mass, double r, double vx, double vy, double vz, double luminosity) {
        super(x, y, z, mass, r, vx, vy, vz);
        this.luminosity = luminosity;
    }
}
