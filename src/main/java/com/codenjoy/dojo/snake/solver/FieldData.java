package com.codenjoy.dojo.snake.solver;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Board;

import java.util.List;

public final class FieldData {
    private final List<Point> snake;
    private final Point head;
    private final List<Point> apples;
    private final List<Point> stones;
    private final List<Point> walls;

    public List<Point> getWalls() {
        return walls;
    }

    private static List<Point> sortedSnake(Board board, List<Point> snake, Point head) {
        return new SnakeBuilder().build(board, snake, head);
    }

    public FieldData(Board board) {
        this(   sortedSnake(board, board.getSnake(), board.getHead()),
                board.getHead(),
                board.getApples(),
                board.getStones(),
                board.getWalls());
    }

    public FieldData(List<Point> snake, Point head, List<Point> apples, List<Point> stones, List<Point> walls) {
        this.snake = snake;
        this.head = head;
        this.apples = apples;
        this.stones = stones;
        this.walls = walls;
    }

    public List<Point> getSnake() {
        return snake;
    }

    public List<Point> getApples() {
        return apples;
    }

    public List<Point> getStones() {
        return stones;
    }

    public Point getHead() {
        return head;
    }

    public boolean isValid() {
        return !snake.isEmpty() && head != null;
    }
}
