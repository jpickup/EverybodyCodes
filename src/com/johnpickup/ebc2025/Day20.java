package com.johnpickup.ebc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.Dijkstra;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day20 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day20");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                Maze maze = new Maze(lines);
                System.out.println("Part 1: " + maze.part1());
                System.out.println("Part 2: " + maze.part2());


            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }
    static class Maze {
        final CharGrid grid;
        Maze(List<String> lines) {
            grid = new CharGrid(lines);
        }

        public int part1() {
            int result = 0;
            Set<Coord> trampolines = grid.findCells('T');
            for (Coord trampoline1 : trampolines) {
                for (Coord trampoline2 : trampolines) {
                    if (trampoline1.compareTo(trampoline2) < 0) {
                        if (areAdjacent(trampoline1, trampoline2)) result++;
                    }
                }
            }
            return result;
        }

        public long part2() {
            if (grid.findCells('S').isEmpty() || grid.findCells('E').isEmpty()) return 0;
            MazeSolver solver = new MazeSolver(grid);
            return solver.lowestCost();
        }
    }

    static boolean areAdjacent(Coord c1, Coord c2) {
        if (c1.compareTo(c2) > 0) return areAdjacent(c2, c1);  // only want a standard ordering

        boolean result = false;
        if (c1.getY() == c2.getY() && Math.abs(c1.getX() - c2.getX()) == 1)
            // same row, next to each other
            result = true;
        else if (Math.abs(c1.getY() - c2.getY()) == 1) {
            int c1X = c1.getX() - c1.getY();
            int c2X = c2.getX() - c2.getY();


            if (c1X % 2 == 1 && c2X % 2 == 0) {
                // adjacent rows, same column
                result = c1X-1 == c2X;
            }
        }
        return result;
    }

    @RequiredArgsConstructor
    static class MazeSolver extends Dijkstra<Coord> {
        final CharGrid grid;
        @Override
        protected Set<Coord> allStates() {
            Set<Coord> result = new HashSet<>();
            result.addAll(grid.findCells('T'));
            result.addAll(grid.findCells('S'));
            result.addAll(grid.findCells('E'));
            return result;
        }

        @Override
        protected Coord initialState() {
            return grid.findCells('S').stream().findFirst().orElseThrow();
        }

        @Override
        protected Coord targetState() {
            return grid.findCells('E').stream().findFirst().orElseThrow();
        }

        @Override
        protected long calculateCost(Coord fromState, Coord toState) {
            return 1;
        }

        @Override
        protected boolean statesAreConnected(Coord toState, Coord fromState) {
            return areAdjacent(fromState, toState);
        }

        @Override
        protected boolean findAllRoutes() {
            return false;
        }
    }
}
