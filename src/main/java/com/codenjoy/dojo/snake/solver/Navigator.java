package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.helpers.FieldData;
import com.codenjoy.dojo.snake.helpers.Route;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

final class Navigator {
    private final List<Point> deltas = new ArrayList<Point>() {{
        add(new PointImpl(0, -1));
        add(new PointImpl(-1, 0));
        add(new PointImpl(1, 0));
        add(new PointImpl(0, 1));
    }};

    private Field printField;
    private Field field;
    private final Point start;
    private boolean shortestRouteSearchDone = false;
    private Set<Point> savedRoutePoints;
    private LinkedList<Point> snake;

    Navigator(FieldData fieldData) {
        this.start = fieldData.getHead();
        this.snake = fieldData.getSnake();
        this.field = new Field(fieldData);
        this.printField = new Field(fieldData);
        field.set(start, Field.HEAD);
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
                .filter(field::isAvailable)
                .collect(Collectors.toList());
    }

    private Set<Point> nearestAvailableForEach(List<Point> currentPoints) {
        return currentPoints
                .stream()
                .map(this::nearestAvailable)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Point nearestWithValue(Point point, int val) {
        return nearest(point)
                .stream()
                .filter(p -> field.get(p) == val)
                .max(Comparator.comparingInt(this::setPriority))
                .orElseThrow(() -> new RuntimeException(String.format("No requested %d value near cell %s", val, point.toString())));
    }

    private int setPriority (Point point) {
        int score = 0;
        if (point.getX() == 1 || point.getX() == 13 || point.getY() == 1 || point.getY() == 13) {
            score--;
        }
        return score;
    }

    private Optional<Point> nearestPointWithMark(Point point) {
        return nearest(point)
                .stream()
                .filter(field::isMark)
                .max(Comparator.comparingInt(o -> field.get(o)));
    }

    private Set<Point> performLeeAlgorithmStep(int [] marker, List<Point> toInvestigate) {
        field.moveSnakeTail();
        marker[0]++;
        Set<Point> options = nearestAvailableForEach(toInvestigate);
        options.forEach(point -> field.set(point, marker[0]));
        return options;
    }

    public Optional<Route> getShortestRoute(List<Point> targets) {
        int[] marker = new int[1];
        List<Point> toInvestigate = new ArrayList<Point>() {{ add(start); }};
        List<Point> foundTargets = new ArrayList<>();
        while (!toInvestigate.isEmpty() && foundTargets.isEmpty()) {
            Set<Point> options = performLeeAlgorithmStep(marker, toInvestigate);
            Optional<Point> foundTarget = targets.stream().filter(options::contains).findFirst();
            foundTarget.ifPresent(target -> {
                foundTargets.add(target);
                savedRoutePoints = options; // Points are saved for other methods (e.g routeToMostDistantPoint in case target is in dead end)
            });

            toInvestigate.clear();
            toInvestigate.addAll(options);
        }
        shortestRouteSearchDone = true; // Some advanced algorithms require markers that are put during this method
        if (!foundTargets.isEmpty()) {
            Route path = getTraceToHead(foundTargets.get(0));
            return Optional.of(path);
        } else {
            return Optional.empty();
        }
    }

    public Route routeToMostDistantPoint(){
        if (!shortestRouteSearchDone) throw new RuntimeException("routeToMostDistantPoint can be launched only after basic search is done and marks are put on field");
        int[] marker = new int[1];
        List<Point> toInvestigate = new ArrayList<>(savedRoutePoints);
        Point target = toInvestigate.get(0);
        marker[0] = field.get(target);

        while (!toInvestigate.isEmpty() && marker[0] <= snake.size()) {
            Set<Point> options = performLeeAlgorithmStep(marker, toInvestigate);
            toInvestigate.clear();
            toInvestigate.addAll(options);
            if (!toInvestigate.isEmpty()){
                target = toInvestigate.get(0);
            }
        }
        return getTraceToHead(target);
    }

    int countAvailableSurroundings() {
        if (shortestRouteSearchDone) throw new RuntimeException("routeToMostDistantPoint must be launched on empty field without markers");
        int[] maxAvailable = new int[1];
        List<Point> routeOptions = nearestAvailable(start);

        routeOptions.forEach(option -> {
            if (field.get(option) == 0 && maxAvailable[0] < snake.size()) {
                field.set(option, Field.RESERVE);
                List<Point> toInvestigate = new ArrayList<Point>() {{ add(option); }};
                Set<Point> collectedPoints = new HashSet<>();

                while (!toInvestigate.isEmpty() && maxAvailable[0] < snake.size()) {
                    field.moveSnakeTail();
                    Set<Point> options = nearestAvailableForEach(toInvestigate);
                    options.forEach(point -> field.set(point, Field.RESERVE));
                    collectedPoints.addAll(options);
                    toInvestigate.clear();
                    toInvestigate.addAll(options);
                }

                if (collectedPoints.size() > maxAvailable[0]) {
                    maxAvailable[0] = collectedPoints.size();
                }
                collectedPoints.clear();
                field.resetSnake(snake);
            }
        });

        return maxAvailable[0];
    }

    private Route getTraceToHead (Point markedPoint) {
        Route path = new Route();
        int[] marker = new int[1];
        marker[0] = field.get(markedPoint);
        Point step = markedPoint;
        path.add(step);
        while (marker[0] > 1) {
            marker[0]--;
            step = nearestWithValue(step, marker[0]);
            path.add(step);
        }
        return path;
    }

    public Optional<Route> getOutOfDeadEnd(FieldData fd) {
        Optional<Point> escapePoint = getSoonestOpening(snake);
        if (escapePoint.isPresent()){
            int[] marker = new int[1];
            marker[0] = field.get(escapePoint.get());
            return Optional.of(getLongerRouteVersion(getTraceToHead(escapePoint.get()), fd, snake.size()));
        }
        return Optional.empty();
    }

    private Optional<Point> getSoonestOpening (LinkedList<Point> snake) {
        if (!shortestRouteSearchDone) throw new RuntimeException("getSoonestOpening can be launched only after basic search is done and marks are put on field");
        Iterator<Point> tail = snake.descendingIterator();
        Optional<Point> result;
        while (tail.hasNext()) {
            result = nearestPointWithMark(tail.next());
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    public Route getLongerRouteVersion(Route original, FieldData fieldData, int desiredLength) {
        Field field = new Field(fieldData);
        field.setReserved(original);
        Route result = new Route();
        int reservedSteps = original.size();

        Iterator<Point> currentRoot = original.descendingIterator();
        LinkedList<Point> pointPair = new LinkedList<>();
        pointPair.add(fieldData.getHead());

        while (currentRoot.hasNext() && (result.size() + reservedSteps < desiredLength)) {
            Point next = currentRoot.next();
            pointPair.add(next);
            reservedSteps--;
            result.addAll(addRootHook(pointPair, field));
            result.add(next);
            pointPair.removeFirst();
        }

        currentRoot.forEachRemaining(result::add);
        Collections.reverse(result);
        if (result.size() > original.size() && result.size() < desiredLength) {
            return getLongerRouteVersion(result, fieldData, desiredLength);
        } else {
            return result;
        }
    }

    private List<Point> addRootHook(List<Point> pointPair, Field field) {
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
            field.setReserved(result.get());
            return result.get();
        } else {
            return new LinkedList<>();
        }
    }

    public Route nearestSafeRandom() {
        List<Point> moves = new ArrayList<>(deltas);
        Route result = new Route();
        while (moves.size() > 0 && result.isEmpty()) {
            int dice = new Random().nextInt(moves.size());
            Point target = new PointImpl(start) {{
                change(moves.get(dice));
            }};
            moves.remove(dice);
            if (field.isSafe(target) || moves.isEmpty()) {
                result.add(target);
            }
        }
        return result;
    }

    public void print(Route r){
        printField.markPath(r);
        System.out.println(printField.toString(r));
    }

    @Override
    public String toString(){
        return field.toString();
    }

}