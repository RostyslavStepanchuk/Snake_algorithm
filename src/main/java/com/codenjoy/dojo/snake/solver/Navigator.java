package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Navigator {
    private final List<Point> deltas = new ArrayList<Point>() {{
        add(new PointImpl(0, -1));
        add(new PointImpl(-1, 0));
        add(new PointImpl(1, 0));
        add(new PointImpl(0, 1));
    }};

    private Field field;
    private final Point start;
    private Point target;

    public Navigator(FieldData fieldData) {
        this(fieldData.getHead(), fieldData.getApples().get(0), fieldData);
    }

    public Navigator(Point start, Point target, FieldData fieldData) {
        this.start = start;
        this.target = target;
        this.field = new Field(fieldData);
    }

    private List<Point> nearest(Point origin) {
        return deltas
                .stream()
                .map((Function<Point, Point>) delta -> new PointImpl(origin) {{
                    change(delta);
                }})
                .collect(Collectors.toList());
    }

    private List<Point> nearestAvailable(Point point) {
        return nearest(point)
                .stream()
                .filter(p -> field.get(p) == 0)
                .collect(Collectors.toList());
    }

    private Point nearestWithValue(Point point, int val) {
        return nearest(point)
                .stream()
                .filter(p -> field.get(p) == val)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("No requested %d value near cell %s", val, point.toString())));
    }

    public Optional<Route> getShortestRoute() {
        boolean found = false;
        int[] marker = new int[1];
        field.set(start, Field.HEAD);
        List<Point> toInvestigate = new ArrayList<Point>() {{
            add(start);
        }};
        while (!toInvestigate.isEmpty() && !found) {
            marker[0]++;
            List<Point> options = toInvestigate
                    .stream()
                    .map(this::nearestAvailable)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            options.forEach(point -> field.set(point, marker[0]));
            if (options.contains(target)) {
                found = true;
            }
            toInvestigate.clear();
            toInvestigate.addAll(options);
        }
        if (found) {
            Route path = new Route();
            Point step = target;
            path.add(step);
            while (marker[0] > 1) {
                marker[0]--;
                step = nearestWithValue(step, marker[0]);
                path.add(step);
            }
            print(path);
            return Optional.of(path);
        } else {
            return Optional.empty();
        }
    }

    public Point nearestSafeRandom() {
        List<Point> moves = new ArrayList<>(deltas);
        while (moves.size() > 1) {
            int dice = new Random().nextInt(moves.size());
            Point target = new PointImpl(start) {{
                change(moves.get(dice));
            }};
            moves.remove(dice);
            if (field.isSafe(target)) {
                return target;
            }
        }
        return new PointImpl(start) {{ change(moves.get(0)); }};
    }

    void print(){
        print(new Route());
    }

    void print(Route r){
        field.print(r);
    }

    private List<Point> getRootHook (List<Point> pointPair, Field field) {
        Optional<LinkedList<Point>> result = deltas
                .stream()
                .map(delta -> pointPair
                        .stream()
                        .map((Function<Point, Point>) point -> new PointImpl(point){{change(delta);}})
                        .collect(Collectors.toCollection(LinkedList::new)))
                .filter(pair -> pair
                        .stream()
                        .allMatch(p -> field.get(p) == 0))
                .findFirst();

        if (result.isPresent()) {
            field.setBarriers(result.get());
            return result.get();
        } else {
            return new LinkedList<>();
        }
    }

    public Route expandRoute (Route original, FieldData fieldData, int desiredLength) {
        Field field = new Field(fieldData);
        field.setBarriers(original);
        Route result = new Route();
        int remainingSteps = original.size();

        LinkedList<Point> pointPair = new LinkedList<>();
        Iterator<Point> currentRoot = original.iterator();
        Point start = currentRoot.next();
        pointPair.add(start);
        result.add(start);
        remainingSteps--;

        while (currentRoot.hasNext() && result.size() + remainingSteps < desiredLength) {
            Point next = currentRoot.next();
            pointPair.add(next);
            remainingSteps--;
            result.addAll(getRootHook(pointPair, field));
            result.add(next);
            pointPair.remove();
        }

        currentRoot.forEachRemaining(result::add);

        if (result.size() > original.size() && result.size() < desiredLength) {
            return expandRoute(result, fieldData, desiredLength);
        } else {
            return result;
        }
    }

    public void printLongRoute(Route route, FieldData fd) {
        field = new Field(fd);
        field.markPath(route);
        print(route);
    }

    public static void main(String[] args) {
        long start = new Date().getTime();

        Set<Point> result = new HashSet<>();
        for (int y = 0; y < 11 ; y++) {
            for (int x = 0; x < 11 ; x++) {
                if (y == 0 || x == 0 || y == 10 || x == 10){
                    result.add(new PointImpl(x,y));
                }
            }
        }
        ArrayList<Point> walls = new ArrayList<>(result);

        List<Point> barriers = new ArrayList<>();
        barriers.add(new PointImpl(4,1));
        barriers.add(new PointImpl(4,2));
        barriers.add(new PointImpl(3,2));

        barriers.add(new PointImpl(9,8));
        barriers.add(new PointImpl(8,8));

        barriers.add(new PointImpl(6,9));
        barriers.add(new PointImpl(6,8));
        barriers.add(new PointImpl(6,7));
        barriers.add(new PointImpl(6,6));
        barriers.add(new PointImpl(6,5));
        barriers.add(new PointImpl(6,4));
        barriers.add(new PointImpl(6,3));
        barriers.add(new PointImpl(6,2));

        barriers.addAll(walls);

        List<Point> snake = new ArrayList<Point>(){{add(new PointImpl(2,2)); add(new PointImpl(1,2)); add(new PointImpl(1,1)); add(new PointImpl(2,1)); add(new PointImpl(3,1)); }};
        List<Point> stones = new ArrayList<Point>(){{add(new PointImpl(1,9));}};
        List<Point> apples = new ArrayList<Point>(){{add(new PointImpl(9,9));}};

        Point head = new PointImpl(2,2);
        Point target = new PointImpl(9,9);

        FieldData fieldData = new FieldData(snake, head, apples, stones, barriers);


        Navigator nav = new Navigator(fieldData);

        Optional<Route> shortest = nav.getShortestRoute();

        if (shortest.isPresent()) {
            Route r = shortest.get();
            r.forEach(System.out::print);
            System.out.println();
            Route longRoute = nav.expandRoute(r, fieldData, 100);
            longRoute.forEach(System.out::print);
            System.out.println();
            nav.printLongRoute(longRoute, fieldData);
        } else {
            System.out.println("ROUTE WASN'T FOUND");
        }

        System.out.println("TIME USED: " + (new Date().getTime() - start) + "ms");
    }
}