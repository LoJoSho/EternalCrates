package xyz.oribuin.eternalcrates.animation.defaults;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import xyz.oribuin.eternalcrates.animation.ParticleAnimation;
import xyz.oribuin.eternalcrates.particle.ParticleData;
import xyz.oribuin.eternalcrates.util.VectorUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Esophose
 * https://github.com/Rosewood-Development/PlayerParticles/blob/master/src/main/java/dev/esophose/playerparticles/styles/ParticleStyleRings.java
 */
public class IcosphereAnimation extends ParticleAnimation {

    private int step;

    public IcosphereAnimation() {
        super("icosphere", 2);
    }

    @Override
    public List<Location> particleLocations(Location crateLocation) {

//        final ParticleData particle = this.getParticleData();
        final List<Location> locations = new ArrayList<>();
        int ticksPerSpawn = 5;
        if (this.step % ticksPerSpawn != 0)
            return locations;

        int divisions = 1;
        double radius = 2;
        Icosahedron icosahedron = new Icosahedron(divisions, radius);
        Set<Vector> points = new HashSet<>();
        int particlesPerLine = 8;
        for (Icosahedron.Triangle triangle : icosahedron.getTriangles())
            points.addAll(this.getPointsAlongTriangle(triangle, particlesPerLine));

        double multiplier = ((double) this.step / ticksPerSpawn);
        double angularVelocityX = 0.00314159265;
        double xRotation = multiplier * angularVelocityX;
        double angularVelocityY = 0.00369599135;
        double yRotation = multiplier * angularVelocityY;
        double angularVelocityZ = 0.00405366794;
        double zRotation = multiplier * angularVelocityZ;

        for (Vector point : points) {
            VectorUtils.rotateVector(point, xRotation, yRotation, zRotation);
            locations.add(crateLocation.clone().add(point));
        }

        return locations;
    }

    @Override
    public void updateTimer() {
        this.step++;
    }

    private Set<Vector> getPointsAlongTriangle(Icosahedron.Triangle triangle, int pointsPerLine) {
        Set<Vector> points = new HashSet<>();
        points.addAll(this.getPointsAlongLine(triangle.point1, triangle.point2, pointsPerLine));
        points.addAll(this.getPointsAlongLine(triangle.point2, triangle.point3, pointsPerLine));
        points.addAll(this.getPointsAlongLine(triangle.point3, triangle.point1, pointsPerLine));
        return points;
    }

    private Set<Vector> getPointsAlongLine(Vector point1, Vector point2, int pointsPerLine) {
        double distance = point1.distance(point2);
        Vector angle = point2.clone().subtract(point1).normalize();
        double distanceBetween = distance / pointsPerLine;

        Set<Vector> points = new HashSet<>();
        for (double i = 0; i < distance; i += distanceBetween)
            points.add(point1.clone().add(angle.clone().multiply(i)));

        return points;
    }

    /**
     * Largely taken from https://www.javatips.net/api/vintagecraft-master/src/main/java/at/tyron/vintagecraft/Client/Render/Math/Icosahedron.java
     */
    public static class Icosahedron {

        public static double X = 0.525731112119133606f;
        public static double Z = 0.850650808352039932f;

        public static double[][] vdata = {{-X, 0, Z}, {X, 0, Z}, {-X, 0, -Z}, {X, 0, -Z}, {0, Z, X}, {0, Z, -X},
                {0, -Z, X}, {0, -Z, -X}, {Z, X, 0}, {-Z, X, 0}, {Z, -X, 0}, {-Z, -X, 0}};

        public static int[][] tindx = {{0, 4, 1}, {0, 9, 4}, {9, 5, 4}, {4, 5, 8}, {4, 8, 1}, {8, 10, 1}, {8, 3, 10},
                {5, 3, 8}, {5, 2, 3}, {2, 7, 3}, {7, 10, 3}, {7, 6, 10}, {7, 11, 6}, {11, 0, 6}, {0, 1, 6}, {6, 1, 10},
                {9, 0, 11}, {9, 11, 2}, {9, 2, 5}, {7, 2, 11}};

        public Icosahedron(int depth, double radius) {
            for (int[] ints : tindx)
                this.subdivide(vdata[ints[0]], vdata[ints[1]], vdata[ints[2]], depth, radius);
        }

        private final List<Triangle> triangles = new ArrayList<>();

        private void addTriangle(double[] vA0, double[] vB1, double[] vC2, double radius) {
            Triangle triangle = new Triangle(
                    new Vector(vA0[0], vA0[1], vA0[2]).multiply(radius),
                    new Vector(vB1[0], vB1[1], vB1[2]).multiply(radius),
                    new Vector(vC2[0], vC2[1], vC2[2]).multiply(radius)
            );
            this.triangles.add(triangle);
        }

        private void subdivide(double[] vA0, double[] vB1, double[] vC2, int depth, double radius) {
            double[] vAB = new double[3];
            double[] vBC = new double[3];
            double[] vCA = new double[3];

            if (depth == 0) {
                this.addTriangle(vA0, vB1, vC2, radius);
                return;
            }

            for (int i = 0; i < 3; i++) {
                vAB[i] = (vA0[i] + vB1[i]) / 2;
                vBC[i] = (vB1[i] + vC2[i]) / 2;
                vCA[i] = (vC2[i] + vA0[i]) / 2;
            }

            double modAB = mod(vAB);
            double modBC = mod(vBC);
            double modCA = mod(vCA);

            for (int i = 0; i < 3; i++) {
                vAB[i] /= modAB;
                vBC[i] /= modBC;
                vCA[i] /= modCA;
            }

            this.subdivide(vA0, vAB, vCA, depth - 1, radius);
            this.subdivide(vB1, vBC, vAB, depth - 1, radius);
            this.subdivide(vC2, vCA, vBC, depth - 1, radius);
            this.subdivide(vAB, vBC, vCA, depth - 1, radius);
        }

        public static double mod(double[] v) {
            return Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        }

        public List<Triangle> getTriangles() {
            return this.triangles;
        }

        public static class Triangle {
            public Vector point1;
            public Vector point2;
            public Vector point3;

            public Triangle(Vector point1, Vector point2, Vector point3) {
                this.point1 = point1;
                this.point2 = point2;
                this.point3 = point3;
            }
        }
    }

}
