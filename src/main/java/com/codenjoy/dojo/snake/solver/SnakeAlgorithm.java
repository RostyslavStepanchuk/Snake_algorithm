package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.snake.helpers.FieldData;
import com.codenjoy.dojo.snake.helpers.Route;
import com.codenjoy.dojo.snake.helpers.SnakeSorter;
import com.codenjoy.dojo.snake.logger.Logger;

import java.util.LinkedList;
import java.util.Optional;

import static com.codenjoy.dojo.services.Direction.*;
import static com.codenjoy.dojo.services.Direction.UP;

public final class SnakeAlgorithm {
    private final Board board;
    private final Logger logger = Logger.getInstance();
    private final StringBuilder moveDescription = logger.getMoveDescription();


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
        moveDescription.append(board.toString());
        FieldData fieldData = new FieldData(board, new SnakeSorter().sort(board, board.getSnake()));
        if (fieldData.getSnake().size() < board.getSnake().size()) {
            throw new RuntimeException("Snake was not fully sorted");
        }
        Route result = getRoute(fieldData);
        logger.describeMoveResult(result);
        logger.saveMoveData(moveDescription.toString());
        return getMoveString(result.getLast());
    };

    private Route getRoute(FieldData fieldData){
        if (fieldData.getSnake().size() >= 55){
            fieldData.considerStoneAsSecondApple();
        }
        Navigator nav = new Navigator(fieldData);

        if (board.isGameOver()) {
            moveDescription.append(board.toString());
            throw new RuntimeException("GAME IS OVER");
        }

        Route result;
        Optional<Route> shortest = nav.getShortestRoute(fieldData.getApples());

        if (shortest.isPresent()) {
            result = safetyAdjustedRoute(shortest.get(), nav, fieldData);
        } else {
            moveDescription.append("GETTING OUT OF THE BOX").append("\n");
            result = nav.getOutOfDeadEnd(fieldData);
        }
        return result;
    }

    private boolean routeIsNotDeadEnd(Route routeCopy, LinkedList<Point> snake, int snakeSize) {
        routeCopy.addAll(snake);
        LinkedList<Point> futureSnake = new LinkedList<>(routeCopy.subList(0, snakeSize + 1));
        FieldData futureField = new FieldData(futureSnake, futureSnake.getFirst(), board.getApples(), board.getStones(), board.getWalls());
        Navigator simulator = new Navigator(futureField);
        return simulator.countAvailableSurroundings() >= snakeSize;
    }

    private Route safetyAdjustedRoute (Route original, Navigator nav, FieldData fieldData) {
        LinkedList<Point> snake = fieldData.getSnake();
        if (routeIsNotDeadEnd(new Route(original), snake, snake.size())) {
            moveDescription.append("SHORTEST ROUTE ACCEPTED").append("\n");
            return original;
        }
        Route longerRoute = nav.getLongerRouteVersion(original,fieldData, snake.size());

        if (routeIsNotDeadEnd(new Route(longerRoute), snake, snake.size())) {
            moveDescription.append("LONGER ROUTE ACCEPTED TO AVOID DEAD END").append("\n");
            return longerRoute;
        }
        moveDescription.append("MOVING TO DISTANT POINT").append("\n");
        return nav.getLongerRouteVersion(nav.routeToMostDistantPoint(), fieldData, fieldData.getSnake().size() * 2);
    }

    public Point makeSandboxMove(){
        FieldData fieldData = new FieldData(board, new LinkedList<>(board.getSnake()));
        Route result = getRoute(fieldData);
        moveDescription.append("APPLE AT: ").append(board.getApples().get(0)).append("\n");
        moveDescription.append("STONE AT: ").append(board.getStones().get(0)).append("\n");
        logger.describeMoveResult(result);

        System.out.println(moveDescription.toString());
        return result.getLast();
    }

}
