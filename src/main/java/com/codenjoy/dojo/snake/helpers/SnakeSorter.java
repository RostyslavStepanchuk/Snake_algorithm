package com.codenjoy.dojo.snake.helpers;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.snake.logger.Logger;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.*;

import static com.codenjoy.dojo.snake.model.Elements.*;

public final class SnakeSorter {
    private final StringBuilder moveDescription = Logger.getInstance().getMoveDescription();

    private static final int OFFSET_PLUS_X = 0;
    private static final int OFFSET_MINUS_X = 1;
    private static final int OFFSET_PLUS_Y = 2;
    private static final int OFFSET_MINUS_Y = 3;
    private static final int NOT_NEAR_STANDING = -1;

    private static final Map<Elements, HashMap<Integer,List<Elements>>> matchingSymbols = new HashMap<Elements, HashMap<Integer,List<Elements>>>(){{
        put(HEAD_DOWN, new HashMap<Integer, List<Elements>>(){{
            put(OFFSET_PLUS_Y, Arrays.asList(TAIL_END_UP, TAIL_VERTICAL, TAIL_LEFT_DOWN, TAIL_RIGHT_DOWN));}});
        put(HEAD_UP, new HashMap<Integer, List<Elements>>(){{
            put(OFFSET_MINUS_Y, Arrays.asList(TAIL_END_DOWN, TAIL_VERTICAL, TAIL_RIGHT_UP, TAIL_LEFT_UP)); }});
        put(HEAD_LEFT, new HashMap<Integer, List<Elements>>(){{
            put(OFFSET_PLUS_X, Arrays.asList(TAIL_END_RIGHT, TAIL_HORIZONTAL, TAIL_LEFT_DOWN, TAIL_LEFT_UP)); }});
        put(HEAD_RIGHT, new HashMap<Integer, List<Elements>>(){{
            put(OFFSET_MINUS_X, Arrays.asList(TAIL_END_LEFT, TAIL_HORIZONTAL, TAIL_RIGHT_DOWN, TAIL_RIGHT_UP)); }});
        put(TAIL_HORIZONTAL, new HashMap<Integer, List<Elements>>(){{
            put(OFFSET_MINUS_X, Arrays.asList(TAIL_END_LEFT, TAIL_RIGHT_UP, TAIL_RIGHT_DOWN, TAIL_HORIZONTAL));
            put(OFFSET_PLUS_X, Arrays.asList(TAIL_END_RIGHT, TAIL_LEFT_UP, TAIL_LEFT_DOWN, TAIL_HORIZONTAL)); }});
        put(TAIL_VERTICAL, new HashMap<Integer, List<Elements>>(){{
            put(OFFSET_MINUS_Y, Arrays.asList(TAIL_END_DOWN, TAIL_LEFT_UP, TAIL_RIGHT_UP, TAIL_VERTICAL));
            put(OFFSET_PLUS_Y, Arrays.asList(TAIL_END_UP, TAIL_LEFT_DOWN, TAIL_RIGHT_DOWN, TAIL_VERTICAL)); }});
        put(TAIL_LEFT_DOWN, new HashMap<Integer, List<Elements>>(){{
            put(OFFSET_MINUS_Y, Arrays.asList(TAIL_VERTICAL, TAIL_END_DOWN, TAIL_RIGHT_UP, TAIL_LEFT_UP));
            put(OFFSET_MINUS_X, Arrays.asList(TAIL_HORIZONTAL, TAIL_END_LEFT, TAIL_RIGHT_UP, TAIL_RIGHT_DOWN)); }});
        put(TAIL_RIGHT_DOWN, new HashMap<Integer, List<Elements>>(){{
            put(OFFSET_MINUS_Y, Arrays.asList(TAIL_VERTICAL, TAIL_END_DOWN, TAIL_LEFT_UP, TAIL_RIGHT_UP));
            put(OFFSET_PLUS_X, Arrays.asList(TAIL_HORIZONTAL, TAIL_END_RIGHT, TAIL_LEFT_UP, TAIL_LEFT_DOWN)); }});
        put(TAIL_LEFT_UP, new HashMap<Integer, List<Elements>>(){{
            put(OFFSET_PLUS_Y, Arrays.asList(TAIL_VERTICAL, TAIL_END_UP, TAIL_LEFT_DOWN, TAIL_RIGHT_DOWN));
            put(OFFSET_MINUS_X, Arrays.asList(TAIL_HORIZONTAL, TAIL_END_LEFT, TAIL_RIGHT_DOWN, TAIL_RIGHT_UP)); }});
        put(TAIL_RIGHT_UP, new HashMap<Integer, List<Elements>>(){{
            put(OFFSET_PLUS_Y, Arrays.asList(TAIL_VERTICAL, TAIL_END_UP, TAIL_RIGHT_DOWN, TAIL_LEFT_DOWN));
            put(OFFSET_PLUS_X, Arrays.asList(TAIL_HORIZONTAL, TAIL_END_RIGHT, TAIL_LEFT_UP, TAIL_LEFT_DOWN)); }});
    }};

    public LinkedList<Point> sort(Board board, List<Point> boardSnake) {
        LinkedList<Point> result = new LinkedList<>();
        LinkedList<Point> origin = new LinkedList<>(boardSnake);
        result.add(origin.pollFirst());

        int counter =0;
        moveDescription.append("Snake provided to sorter: ").append(Arrays.toString(boardSnake.toArray())).append("\n");
        moveDescription.append("Snake symbols: ").append(board.getAt(board.getHead()));
        while (!origin.isEmpty() && counter < 1000) {
            Point p = origin.pollFirst();
            if (pointsConnected(p, result.getLast(), board)) {
                result.add(p);
                moveDescription.append("|").append(p).append(board.getAt(p)).append("|");
            } else {
                origin.add(p);
            }
            counter++;
        }
        if (counter > 2000) {
            throw new RuntimeException("Snake connections iterations exceeded 2000");
        }
        moveDescription.append("\n");
        moveDescription.append("Sorter result snake: ").append(Arrays.toString(result.toArray())).append("\n");
        return result;
    }

    private boolean pointsConnected(Point connectWhat, Point connectTo, Board board){
        int offset = pointsRelation(connectWhat, connectTo);
        if (offset != NOT_NEAR_STANDING) {
                return symbolsConnectable(connectWhat, connectTo, offset , board);
        }
        return false;
    }

    private boolean symbolsConnectable (Point connectWhat, Point connectTo, int offset, Board board) {
        Elements candidate = board.getAt(connectWhat);
        Elements currentEnd = board.getAt(connectTo);
        Map<Integer, List<Elements>> relevantSymbolsMap = matchingSymbols.get(currentEnd);
        if (relevantSymbolsMap == null) throw new RuntimeException("Symbol "+ candidate.ch() + " was not found in matchingSymbols Map");
        if (!relevantSymbolsMap.containsKey(offset)) {
            return false;
        }
        List<Elements> relevantConnections = relevantSymbolsMap.get(offset);
        return relevantConnections.stream().anyMatch(element -> element == candidate);
    }

    private int pointsRelation(Point p1, Point p2) {
        int xOffset = p1.getX()-p2.getX();
        int yOffset = p1.getY()-p2.getY();
        if (Math.abs(xOffset) + Math.abs(yOffset) == 1){
            if (xOffset == -1) return OFFSET_MINUS_X;
            if (xOffset == 1) return OFFSET_PLUS_X;
            if (yOffset == -1) return OFFSET_MINUS_Y;
            if (yOffset == 1 ) return OFFSET_PLUS_Y;
            throw new RuntimeException(String.format("Invalid offsets x:%d , y:%d", xOffset, yOffset));
        }  else return NOT_NEAR_STANDING;
    }
}
