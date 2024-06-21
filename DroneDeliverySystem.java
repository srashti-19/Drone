import java.util.*;
import java.awt.geom.Point2D;

public class DroneDeliverySystem {

    public static void main(String[] args) {
     
        List<Point2D> randomPoints = generateRandomPoints(1000);

      
        long startTime = System.currentTimeMillis();
        Pair closestPairBruteForce = findClosestPairBruteForce(randomPoints);
        long endTime = System.currentTimeMillis();
        System.out.println("Brute-force closest pair: " + closestPairBruteForce);
        System.out.println("Brute-force execution time: " + (endTime - startTime) + " ms");

       
        startTime = System.currentTimeMillis();
        Pair closestPairDivideConquer = findClosestPairDivideAndConquer(randomPoints);
        endTime = System.currentTimeMillis();
        System.out.println("Divide-and-conquer closest pair: " + closestPairDivideConquer);
        System.out.println("Divide-and-conquer execution time: " + (endTime - startTime) + " ms");

        // Simulate drone path visiting every point
        double totalDistance = simulateDronePath(randomPoints);
        System.out.println("Total distance traveled by drone: " + totalDistance);
    }

    public static List<Point2D> generateRandomPoints(int n) {
        Random rand = new Random();
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            points.add(new Point2D.Double(rand.nextInt(100), rand.nextInt(100)));
        }
        return points;
    }

    public static Pair findClosestPairBruteForce(List<Point2D> points) {
        double minDistance = Double.MAX_VALUE;
        Pair closestPair = null;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                double distance = points.get(i).distance(points.get(j));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPair = new Pair(points.get(i), points.get(j));
                }
            }
        }
        return closestPair;
    }

    public static Pair findClosestPairDivideAndConquer(List<Point2D> points) {
        List<Point2D> sortedByX = new ArrayList<>(points);
        sortedByX.sort(Comparator.comparingDouble(Point2D::getX));
        List<Point2D> sortedByY = new ArrayList<>(points);
        sortedByY.sort(Comparator.comparingDouble(Point2D::getY));
        return closestPairRecursive(sortedByX, sortedByY);
    }

    private static Pair closestPairRecursive(List<Point2D> pointsSortedByX, List<Point2D> pointsSortedByY) {
        int n = pointsSortedByX.size();
        if (n <= 3) {
            return findClosestPairBruteForce(pointsSortedByX);
        }

        int mid = n / 2;
        Point2D midPoint = pointsSortedByX.get(mid);

        List<Point2D> leftOfCenter = pointsSortedByX.subList(0, mid);
        List<Point2D> rightOfCenter = pointsSortedByX.subList(mid, n);

        List<Point2D> tempLeftY = new ArrayList<>();
        List<Point2D> tempRightY = new ArrayList<>();

        for (Point2D point : pointsSortedByY) {
            if (point.getX() <= midPoint.getX()) {
                tempLeftY.add(point);
            } else {
                tempRightY.add(point);
            }
        }

        Pair closestPairLeft = closestPairRecursive(leftOfCenter, tempLeftY);
        Pair closestPairRight = closestPairRecursive(rightOfCenter, tempRightY);

        Pair closestPair = closestPairLeft.getDistance() < closestPairRight.getDistance() ? closestPairLeft : closestPairRight;
        double shortestDistance = closestPair.getDistance();

        List<Point2D> strip = new ArrayList<>();
        for (Point2D point : pointsSortedByY) {
            if (Math.abs(point.getX() - midPoint.getX()) < shortestDistance) {
                strip.add(point);
            }
        }

        return stripClosest(strip, shortestDistance, closestPair);
    }

    private static Pair stripClosest(List<Point2D> strip, double d, Pair closestPair) {
        double minDistance = d;
        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).getY() - strip.get(i).getY()) < minDistance; j++) {
                double distance = strip.get(i).distance(strip.get(j));
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPair = new Pair(strip.get(i), strip.get(j));
                }
            }
        }
        return closestPair;
    }

    public static double simulateDronePath(List<Point2D> points) {
        double totalDistance = 0;
        Point2D currentPoint = points.get(0);
        Set<Point2D> visited = new HashSet<>();
        visited.add(currentPoint);

        while (visited.size() < points.size()) {
            Pair closestPair = findClosestPoint(currentPoint, points, visited);
            totalDistance += closestPair.getDistance();
            currentPoint = closestPair.point2;
            visited.add(currentPoint);
        }
        return totalDistance;
    }

    private static Pair findClosestPoint(Point2D currentPoint, List<Point2D> points, Set<Point2D> visited) {
        double minDistance = Double.MAX_VALUE;
        Point2D closestPoint = null;

        for (Point2D point : points) {
            if (!visited.contains(point)) {
                double distance = currentPoint.distance(point);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPoint = point;
                }
            }
        }

        return new Pair(currentPoint, closestPoint);
    }

    static class Pair {
        Point2D point1, point2;

        public Pair(Point2D point1, Point2D point2) {
            this.point1 = point1;
            this.point2 = point2;
        }

        public double getDistance() {
            return point1.distance(point2);
        }

        @Override
        public String toString() {
            return "Pair{" + "point1=" + point1 + ", point2=" + point2 + ", distance=" + getDistance() + '}';
        }
    }
}
