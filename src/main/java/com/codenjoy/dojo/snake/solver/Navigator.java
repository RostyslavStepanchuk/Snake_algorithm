package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.logger.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Navigator {
    private final List<Point> deltas = new ArrayList<Point>() {{
        add(new PointImpl(0, -1));
        add(new PointImpl(-1, 0));
        add(new PointImpl(1, 0));
        add(new PointImpl(0, 1));
    }};

    private Field printField;
    private Field field;
    private final Point start;
    private boolean basicSearchDone = false;
    private Set<Point> savedRoutePoints;
    private LinkedList<Point> snake;

    public Navigator(FieldData fieldData) {
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
                .filter(p -> field.get(p) == 0)
                .collect(Collectors.toList());
    }

    private Set<Point> nearestAvailableForAll(List<Point> currentPoints) {
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
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("No requested %d value near cell %s", val, point.toString())));
    }

    private Optional<Point> nearestRootPoint(Point point) {
        return nearest(point)
                .stream()
                .filter(p -> field.get(p) > 0)
                .max(Comparator.comparingInt(o -> field.get(o)));
    }



    public Optional<Route> getShortestRoute(List<Point> targets) {
        boolean found = false;
        int[] marker = new int[1];
        List<Point> toInvestigate = new ArrayList<Point>() {{
            add(start);
        }};
        Point foundPoint = targets.get(0);
        while (!toInvestigate.isEmpty() && !found) {
            field.moveSnakeTail();
            marker[0]++;
            Set<Point> options = nearestAvailableForAll(toInvestigate);
            options.forEach(point -> field.set(point, marker[0]));
            Optional<Point> foundTarget = targets.stream().filter(options::contains).findFirst();
            if (foundTarget.isPresent()) {
                found = true;
                savedRoutePoints = options;
                foundPoint = foundTarget.get();
            }
            foundTarget.ifPresent(point -> {

            });
            toInvestigate.clear();
            toInvestigate.addAll(options);
        }
        basicSearchDone = true;
        if (found) {
            Route path = getTraceToHead(foundPoint);
            return Optional.of(path);
        } else {
            return Optional.empty();
        }
    }

    public Route routeToMostDistantPoint(){
        if (!basicSearchDone) throw new RuntimeException("routeToMostDistantPoint can be launched only after basic search is done and reflected on field");
        StringBuilder sb = new StringBuilder();
        int[] marker = new int[1];
        List<Point> toInvestigate = new ArrayList<>(savedRoutePoints);
        sb.append("Saved points to start from: ");
        toInvestigate.forEach(sb::append);
        sb.append("\n");
        Point target = toInvestigate.get(0);
        sb.append("taken as a target: ");
        sb.append(target);
        sb.append("\n");
        marker[0] = field.get(target);
        sb.append("starting marker: " + marker[0] + "\n");

        while (!toInvestigate.isEmpty() && marker[0] <= snake.size()) {
            field.moveSnakeTail();
            marker[0]++;
            sb.append("new marker: " + marker[0] + "\n");
            Set<Point> options = nearestAvailableForAll(toInvestigate);
            sb.append("new options: ");
            options.forEach(sb::append);
            sb.append("\n");
            options.forEach(point -> field.set(point, marker[0]));
            toInvestigate.clear();
            toInvestigate.addAll(options);
            if (!toInvestigate.isEmpty()){
                target = toInvestigate.get(0);
                sb.append("new target: " + target + "\n");
            }
        }
        sb.append(field.toString());
        sb.append("\n");
        Logger.getInstance().logProblem("DISTANT POINT CHECK", sb.toString());
        return getTraceToHead(target);
    }

    public int countAvailableSurroundings() {
        StringBuilder sb = new StringBuilder();
        int result = 0;
        Set<Point> collectedPoints = new HashSet<>();
        List<Point> rootOptions = nearestAvailable(start);


        while (!rootOptions.isEmpty() && collectedPoints.size() < snake.size()) {
            sb.append("Snake in the beginning ");
            snake.forEach(sb::append);
            sb.append("\n");
            List<Point> finalRootOptions = rootOptions;
            sb.append("Route options: ");
            rootOptions.forEach(sb::append);
            sb.append("\n");
            sb.append("Investigating " + finalRootOptions.get(0) + "\n");
            List<Point> toInvestigate = new ArrayList<Point>() {{
                add(finalRootOptions.get(0));
                field.set(finalRootOptions.get(0), Field.RESERVE);
            }};
            collectedPoints.clear();

            while (!toInvestigate.isEmpty()) {
                field.moveSnakeTail();
                Set<Point> options = nearestAvailableForAll(toInvestigate);
                sb.append("Options collected during wave: ");
                options.forEach(sb::append);
                sb.append("\n");
                options.forEach(point -> field.set(point, Field.RESERVE));
                collectedPoints.addAll(options);
                toInvestigate.clear();
                toInvestigate.addAll(options);
            }
            sb.append("Got points: ");
            sb.append(collectedPoints.size() + "\n");
            sb.append("Field in the end: ");
            sb.append("\n");
            sb.append(field.toString());
            sb.append("\n");
            if (collectedPoints.size() > result) {
                result = collectedPoints.size();
            }
            field.setSnake(snake);
            rootOptions = nearestAvailable(start);
        }
        sb.append("Final result is: " + result);
        Logger.getInstance().logProblem("countavailablesurroundings", sb.toString());
        return result;
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

    public Route getRouteOut (FieldData fd) {
        Point escapePoint = getSoonestOpening(snake);
        int[] marker = new int[1];
        marker[0] = field.get(escapePoint);
        return getLongerRouteVersion(getTraceToHead(escapePoint), fd, snake.size());
    }

    private Point getSoonestOpening (LinkedList<Point> snake) {
        if (!basicSearchDone) throw new RuntimeException("getSoonestOpening can be launched only after basic search is done and reflected on field");
        Iterator<Point> tail = snake.descendingIterator();
        while (tail.hasNext()) {
            Point current = tail.next();
            if (field.isSnake(current)) {
                Optional<Point> result = nearestRootPoint(current);
                if (result.isPresent()) return result.get();
            }
        }
        Logger.getInstance().logProblem("NO ROUTE MARKERS FOUND", field.toString());
        throw new RuntimeException("No route markers were found near snake. Check field and basicSearch results");
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
            System.out.println("RESULT ROUTE: ");
            result.forEach(System.out::print);
            System.out.println();
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
            field.setBarriers(result.get());
            return result.get();
        } else {
            return new LinkedList<>();
        }
    }

    public void print(Route r){
        printField.print(r);
    }

    public void printRaw(){
        field.printRaw();
    }

    @Override
    public String toString(){
        return field.toString();
    }

}