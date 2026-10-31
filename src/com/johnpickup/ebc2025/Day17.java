package com.johnpickup.ebc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.Dijkstra;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day17 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day17");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                Volcano volcano = new Volcano(lines);

                System.out.println("Part 1: " + volcano.part1());
                System.out.println("Part 2: " + volcano.part2());
                if (inputFilename.contains("3")) {
                    System.out.println("Part 3: " + volcano.part3());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Volcano {
        final CharGrid grid;
        public Volcano(List<String> lines) {
            grid = new CharGrid(lines);
        }

        public int part1() {
            Coord start = grid.findCells('@').stream().findFirst().orElseThrow();
            int result = 0;

            for (int x = grid.bounds().getLower().getX(); x <= grid.bounds().getUpper().getX(); x++) {
                for (int y = grid.bounds().getLower().getY(); y <= grid.bounds().getUpper().getY(); y++) {
                    Coord coord = new Coord(x, y);
                    if (withinDistance(coord, start, 10)) {
                        Character cell = grid.getCell(coord);
                        if (cell != '@') result += Integer.parseInt("" + cell);
                    }
                }
            }
            return result;
        }

        public int part2() {
            Coord start = grid.findCells('@').stream().findFirst().orElseThrow();
            int maxDestruction = 0;
            int radiusMaxDestruction = 0;

            for (int radius = 1; radius <= grid.getWidth()/2; radius++) {
                int destruction = calcDestruction(radius);
                if (destruction > maxDestruction) {
                    maxDestruction = destruction;
                    radiusMaxDestruction = radius;
                }
            }
            return radiusMaxDestruction * maxDestruction;
        }

        public int part3() {
            Coord v = grid.findCells('@').stream().findFirst().orElseThrow();
            Coord start = grid.findCells('S').stream().findFirst().orElseThrow();
            for (int radius = 1; radius <= grid.getWidth()/2; radius++) {
                int maxTime = radius * 30;


            }
            return 0;
        }

        private int calcDestruction(int radius) {
            Coord start = grid.findCells('@').stream().findFirst().orElseThrow();
            int result = 0;

            for (int x = grid.bounds().getLower().getX(); x <= grid.bounds().getUpper().getX(); x++) {
                for (int y = grid.bounds().getLower().getY(); y <= grid.bounds().getUpper().getY(); y++) {
                    Coord coord = new Coord(x, y);
                    if (withinDistance(coord, start, radius) && !withinDistance(coord, start, radius-1)) {
                        Character cell = grid.getCell(coord);
                        if (cell != '@') result += Integer.parseInt("" + cell);
                    }
                }
            }
            return result;
        }

    }

    static boolean withinDistance(Coord c1, Coord c2, int d) {
        long dx = c1.getX() - c2.getX();
        long dy = c1.getY() - c2.getY();
        return dx*dx + dy*dy <= (long)d*d;
    }

    @RequiredArgsConstructor
    static class VolcanoState {
        final Coord coord;
        final boolean containsVolcano;
    }

    @RequiredArgsConstructor
    static class VolcanoCircuit extends Dijkstra<VolcanoState> {
        final CharGrid grid;

        @Override
        protected Set<VolcanoState> allStates() {
            return Set.of();
        }

        @Override
        protected VolcanoState initialState() {
            return new VolcanoState(grid.findCells('S').stream().findFirst().orElseThrow(), false);
        }

        @Override
        protected VolcanoState targetState() {
            return new VolcanoState(grid.findCells('S').stream().findFirst().orElseThrow(), true);
        }

        @Override
        protected long calculateCost(VolcanoState fromState, VolcanoState toState) {
            Character cell = grid.getCell(toState.coord);
            if (cell=='S') return 0;
            if (cell=='@') return Integer.MAX_VALUE;
            return Integer.parseInt(""+cell);
        }

        @Override
        protected boolean statesAreConnected(VolcanoState toState, VolcanoState fromState) {
            if (grid.getCell(toState.coord) == '@') return false;
            return fromState.coord.north().equals(toState.coord)
                    || fromState.coord.south().equals(toState.coord)
                    || fromState.coord.east().equals(toState.coord)
                    || fromState.coord.west().equals(toState.coord);
        }

        @Override
        protected boolean findAllRoutes() {
            return false;
        }
    }
}
