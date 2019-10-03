package com.codenjoy.dojo.snake.mycode.helpers;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.snake.client.Board;
import com.codenjoy.dojo.snake.mycode.logger.Logger;
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

    // Look, me in future what a monster you almost released to this world:

//    private static final Map<Elements, HashMap<Integer,List<Elements>>> matchingSymbols = new HashMap<Elements, HashMap<Integer,List<Elements>>>(){{
//        put(HEAD_DOWN, new HashMap<Integer, List<Elements>>(){{
//            put(OFFSET_PLUS_Y, Arrays.asList(TAIL_END_UP, TAIL_VERTICAL, TAIL_LEFT_DOWN, TAIL_RIGHT_DOWN));}});
//        put(HEAD_UP, new HashMap<Integer, List<Elements>>(){{
//            put(OFFSET_MINUS_Y, Arrays.asList(TAIL_END_DOWN, TAIL_VERTICAL, TAIL_RIGHT_UP, TAIL_LEFT_UP)); }});
//        put(HEAD_LEFT, new HashMap<Integer, List<Elements>>(){{
//            put(OFFSET_PLUS_X, Arrays.asList(TAIL_END_RIGHT, TAIL_HORIZONTAL, TAIL_LEFT_DOWN, TAIL_LEFT_UP)); }});
//        put(HEAD_RIGHT, new HashMap<Integer, List<Elements>>(){{
//            put(OFFSET_MINUS_X, Arrays.asList(TAIL_END_LEFT, TAIL_HORIZONTAL, TAIL_RIGHT_DOWN, TAIL_RIGHT_UP)); }});
//        put(TAIL_HORIZONTAL, new HashMap<Integer, List<Elements>>(){{
//            put(OFFSET_MINUS_X, Arrays.asList(TAIL_END_LEFT, TAIL_RIGHT_UP, TAIL_RIGHT_DOWN, TAIL_HORIZONTAL));
//            put(OFFSET_PLUS_X, Arrays.asList(TAIL_END_RIGHT, TAIL_LEFT_UP, TAIL_LEFT_DOWN, TAIL_HORIZONTAL)); }});
//        put(TAIL_VERTICAL, new HashMap<Integer, List<Elements>>(){{
//            put(OFFSET_MINUS_Y, Arrays.asList(TAIL_END_DOWN, TAIL_LEFT_UP, TAIL_RIGHT_UP, TAIL_VERTICAL));
//            put(OFFSET_PLUS_Y, Arrays.asList(TAIL_END_UP, TAIL_LEFT_DOWN, TAIL_RIGHT_DOWN, TAIL_VERTICAL)); }});
//        put(TAIL_LEFT_DOWN, new HashMap<Integer, List<Elements>>(){{
//            put(OFFSET_MINUS_Y, Arrays.asList(TAIL_VERTICAL, TAIL_END_DOWN, TAIL_RIGHT_UP, TAIL_LEFT_UP));
//            put(OFFSET_MINUS_X, Arrays.asList(TAIL_HORIZONTAL, TAIL_END_LEFT, TAIL_RIGHT_UP, TAIL_RIGHT_DOWN)); }});
//        put(TAIL_RIGHT_DOWN, new HashMap<Integer, List<Elements>>(){{
//            put(OFFSET_MINUS_Y, Arrays.asList(TAIL_VERTICAL, TAIL_END_DOWN, TAIL_LEFT_UP, TAIL_RIGHT_UP));
//            put(OFFSET_PLUS_X, Arrays.asList(TAIL_HORIZONTAL, TAIL_END_RIGHT, TAIL_LEFT_UP, TAIL_LEFT_DOWN)); }});
//        put(TAIL_LEFT_UP, new HashMap<Integer, List<Elements>>(){{
//            put(OFFSET_PLUS_Y, Arrays.asList(TAIL_VERTICAL, TAIL_END_UP, TAIL_LEFT_DOWN, TAIL_RIGHT_DOWN));
//            put(OFFSET_MINUS_X, Arrays.asList(TAIL_HORIZONTAL, TAIL_END_LEFT, TAIL_RIGHT_DOWN, TAIL_RIGHT_UP)); }});
//        put(TAIL_RIGHT_UP, new HashMap<Integer, List<Elements>>(){{
//            put(OFFSET_PLUS_Y, Arrays.asList(TAIL_VERTICAL, TAIL_END_UP, TAIL_RIGHT_DOWN, TAIL_LEFT_DOWN));
//            put(OFFSET_PLUS_X, Arrays.asList(TAIL_HORIZONTAL, TAIL_END_RIGHT, TAIL_LEFT_UP, TAIL_LEFT_DOWN)); }});
//    }};

    private static final Map<Elements, List<Integer>> elementsAllowedOffsets = new HashMap<Elements, List<Integer>>(){{
        put(HEAD_DOWN, Collections.singletonList(OFFSET_PLUS_Y));
        put(HEAD_UP, Collections.singletonList(OFFSET_MINUS_Y));
        put(HEAD_LEFT, Collections.singletonList(OFFSET_PLUS_X));
        put(HEAD_RIGHT, Collections.singletonList(OFFSET_MINUS_X));

        put(TAIL_HORIZONTAL, Arrays.asList(OFFSET_MINUS_X, OFFSET_PLUS_X));
        put(TAIL_VERTICAL, Arrays.asList(OFFSET_MINUS_Y, OFFSET_PLUS_Y));
        put(TAIL_LEFT_DOWN, Arrays.asList(OFFSET_MINUS_Y, OFFSET_MINUS_X));
        put(TAIL_RIGHT_DOWN, Arrays.asList(OFFSET_MINUS_Y, OFFSET_PLUS_X));
        put(TAIL_LEFT_UP, Arrays.asList(OFFSET_PLUS_Y, OFFSET_MINUS_X));
        put(TAIL_RIGHT_UP, Arrays.asList(OFFSET_PLUS_Y, OFFSET_PLUS_X));
    }};

    public LinkedList<Point> sort(Board board, List<Point> boardSnake) {
        LinkedList<Point> result = new LinkedList<>();
        LinkedList<Point> origin = new LinkedList<>(boardSnake);
        result.add(origin.pollFirst());

        int counter =0;
        while (!origin.isEmpty() && counter < 5000) {
            Point p = origin.pollFirst();
            if (pointsConnected(p, result.getLast(), board)) {
                result.add(p);
            } else {
                origin.add(p);
            }
            counter++;
        }
        if (counter > 4999) {
            throw new RuntimeException("Snake connections iterations exceeded 5000");
        }
        moveDescription.append("Sorted snake").append("(size: ").append(result.size()).append("). :").append(Arrays.toString(result.toArray())).append("\n");
        return result;
    }

    private boolean pointsConnected(Point connectWhat, Point connectTo, Board board){
        int offset = pointsRelation(connectWhat, connectTo);
        if (offset != NOT_NEAR_STANDING) {
                return positionMatchSymbol(connectTo, offset , board);
        }
        return false;
    }

    private boolean positionMatchSymbol(Point connectTo, int offset, Board board){
        Elements currentEnd = board.getAt(connectTo);
        List<Integer> validOffsets = elementsAllowedOffsets.get(currentEnd);
        return validOffsets.contains(offset);
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
