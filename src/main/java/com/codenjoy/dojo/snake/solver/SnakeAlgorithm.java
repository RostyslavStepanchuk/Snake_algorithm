package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.snake.logger.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.services.Direction.UP;

public class SnakeAlgorithm {
    private final Board board;

    public SnakeAlgorithm(Board board) {
        this.board = board;
    }

    private String getMoveString(Point dest) {
        Point me = board.getHead();
        if (me.getX()>dest.getX()) return LEFT.toString();
        if (me.getX()<dest.getX()) return RIGHT.toString();
        if (me.getY()>dest.getY()) return DOWN.toString();
        if (me.getY()<dest.getY()) return UP.toString();
        throw new RuntimeException(String.format("Unknown destination: me: %s, des: %s ", me.toString(), dest.toString()));
    }

    public String makeMove(){

        FieldData fieldData = new FieldData(board);
        if (fieldData.getSnake().size() >= 55){
            fieldData.allowToEatStone();
        }
        Navigator nav = new Navigator(fieldData);

        try {
            Route result;
            Optional<Route> shortest = nav.getShortestRoute(fieldData.getApples());

            if (shortest.isPresent()) {
                result = safetyAdjustedRoute(shortest.get(), nav, fieldData);
            } else {
                result = nav.getRouteOut(fieldData);
            }
            System.out.print("RESULT ROUTE: ");
            result.forEach(System.out::print);
            System.out.println();
            System.out.println("NEXT MOVE: " + result.getLast());
            return getMoveString(result.getLast());

        } catch (Exception e) {
            e.printStackTrace();
            Logger.getInstance().logError(e, board);
            return getMoveString(nav.nearestSafeRandom());
        }
    }

    private boolean routeIsNotDeadEnd(Route route, LinkedList<Point> snake, int snakeSize) {
        route.addAll(snake);
        LinkedList<Point> futureSnake = new LinkedList<>(route.subList(0, snakeSize + 1));
        FieldData futureField = new FieldData(futureSnake, futureSnake.getFirst(), board.getApples(), board.getStones(), board.getWalls());
        Navigator simulator = new Navigator(futureField);
        return simulator.countAvailableSurroundings() >= snakeSize;
    }

    private Route safetyAdjustedRoute (Route original, Navigator nav, FieldData fieldData) {
        LinkedList<Point> snake = fieldData.getSnake();
        if (routeIsNotDeadEnd(new Route(original), snake, snake.size())) {
            System.out.println("ORIGINAL ROUTE ACCEPTED");
            return original;
        }
        Route longerRoute = nav.getLongerRouteVersion(original,fieldData, snake.size());

        if (routeIsNotDeadEnd(new Route(longerRoute), snake, snake.size())) {
            System.out.println("TRYING LONGER ROUTE");
            return longerRoute;
        }
            System.out.println("LONGER ROUTE IS NOT VALID");
            return nav.getLongerRouteVersion(nav.routeToMostDistantPoint(), fieldData, fieldData.getSnake().size());
    }
}
