package io.github.seggan.kingoftheziggurat.bots;

import io.github.seggan.kingoftheziggurat.Bot;
import static io.github.seggan.kingoftheziggurat.MoveDirection.*;
import io.github.seggan.kingoftheziggurat.MoveDirection;

import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.awt.Point;
import java.lang.Math;

public class WeightCrab extends Bot {

    @Override
    protected boolean fight(Bot opponent) {
        return getElevation() > 1;
    }

    @Override
    protected void tick() {

        if (getElevation() == 6) {
            move(NONE);
            return;
        }

        Map<MoveDirection, Integer> weights = new HashMap<>();

        Map<Point, Integer> botPositionCounts = new HashMap<>();

        for (Bot bot : getPlayers()) {
            Point position = bot.getPosition();
            botPositionCounts.put(position, botPositionCounts.getOrDefault(position, 0) + 1);
        }

        for (MoveDirection direction : MoveDirection.values()) {

            int weight = (getElevationRelative(direction) - getElevation()) * 89;
            Point candidate = relativePositionFrom(direction, getPosition());

            for (Map.Entry<Point, Integer> entry : botPositionCounts.entrySet()) {

                Point position = entry.getKey();
                int count = entry.getValue();

                if (defaultUpFrom(position).equals(candidate)) {
                    weight -= 2 * count;
                }

                if (elevationAt(candidate) > elevationAt(position) && withinReachOf(candidate, position)) {
                    weight -= count;
                }

            }

            weights.put(direction, weight);

        }

        MoveDirection chosen = weights.entrySet().stream().collect(
            Collectors.maxBy((a, b) ->
                a.getValue().compareTo(b.getValue())
            )).get().getKey();

        move(chosen);
    }

    private static Point relativePositionFrom(MoveDirection direction, Point from) {
        int x = from.x;
        int y = from.y;
        switch(direction) {
            case NORTH:
                return new Point(x, y - 1);
            case SOUTH:
                return new Point(x, y + 1);
            case EAST:
                return new Point(x + 1, y);
            case WEST:
                return new Point(x - 1, y);
            case NORTH_EAST:
                return new Point(x + 1, y - 1);
            case NORTH_WEST:
                return new Point(x - 1, y - 1);
            case SOUTH_EAST:
                return new Point(x + 1, y + 1);
            case SOUTH_WEST:
                return new Point(x - 1, y + 1);
            default: // why won't it let me just do an exhaustive enum match ;(
                return from;
        }
    }

    private int elevationAt(Point point) {
        int fromCorner = Math.min(point.x, point.y) + 1;
        return Math.min(fromCorner, 12 - fromCorner);
    }

    private Point defaultUpFrom(Point from) {
        for (MoveDirection direction : MoveDirection.values()) {
            if (elevationAt(relativePositionFrom(direction, from)) > elevationAt(from)) {
                return relativePositionFrom(direction, from);
            }
        }
        return from;
    }

    private static boolean withinReachOf(Point a, Point b) {
        return Math.abs(a.x - b.x) <= 1 && Math.abs(a.y - b.y) <= 1;
    }

}
