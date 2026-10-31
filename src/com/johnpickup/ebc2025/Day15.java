package com.johnpickup.ebc2025;

import com.johnpickup.util.*;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day15 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day15");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                List<Instruction> instructions = Arrays.stream(lines.getFirst().split(",")).map(Instruction::new).toList();
                Maze maze = new Maze(instructions);
                //System.out.println(maze);
                System.out.println("Solution: " + maze.solve());

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @ToString
    static class Instruction {
        final char direction;
        final int count;
        Instruction(String s) {
            direction = s.charAt(0);
            count = Integer.parseInt(s.substring(1));
        }
    }


    static class Maze {
        final SparseGrid<Character> grid = new SparseGrid<>();
        Maze(List<Instruction> instructions) {
            Coord c = new Coord(0,0);
            Direction d = Direction.NORTH;
            grid.setCell(c, 'S');
            for (Instruction instruction : instructions) {
                switch (instruction.direction) {
                    case 'L': d = d.left(); break;
                    case 'R': d = d.right(); break;
                    default: throw new RuntimeException("Unknown direction " + instruction);
                }
                for (int i = 0; i < instruction.count; i++) {
                    c = c.move(d, 1);
                    grid.setCell(c, '#');
                }
            }
            grid.setCell(c, 'E');
        }

        @Override
        public String toString() {
            return grid.toString();
        }

        public int solve() {
            MazeSolver solver = new MazeSolver(this);
            Set<List<Coord>> routes = solver.findRoutes();
            List<Coord> route = routes.stream().min(Comparator.comparingInt(List::size)).orElse(Collections.emptyList());

            int result = 0;
            Coord last = new Coord(0,0);
            for (Coord coord : route) {
                result += coord.distanceFrom(last);
                last = coord;
            }
            return result;
        }
    }

    @RequiredArgsConstructor
    static class MazeSolver extends Dijkstra<Coord> {
        final Maze maze;

        @Override
        protected Set<Coord> allStates() {
            Set<Coord> result = new HashSet<>();
            Range<Coord> bounds = maze.grid.bounds();
            for (int x = bounds.getLower().getX(); x <= bounds.getUpper().getX(); x++) {
                for (int y = bounds.getLower().getY(); y <= bounds.getUpper().getY(); y++) {
                    Coord coord = new Coord(x, y);
                    if ('#' != getCell(coord)) {
                        result.add(coord);
                    }
                }
            }
            return result;
        }

        private char getCell(Coord c) {
            Range<Coord> bounds = maze.grid.bounds();
            if (c.getX()<bounds.getLower().getX()) return '#';
            if (c.getX()>bounds.getUpper().getX()) return '#';
            if (c.getY()<bounds.getLower().getY()) return '#';
            if (c.getY()>bounds.getUpper().getY()) return '#';
            return Optional.ofNullable(maze.grid.getCell(c)).orElse(' ');
        }

        @Override
        protected Coord initialState() {
            return maze.grid.findCells('S').stream().findFirst().orElseThrow();
        }

        @Override
        protected Coord targetState() {
            return maze.grid.findCells('E').stream().findFirst().orElseThrow();
        }

        @Override
        protected long calculateCost(Coord fromState, Coord toState) {
            return fromState.distanceFrom(toState);
        }

        @Override
        protected boolean statesAreConnected(Coord toState, Coord fromState) {
            int minX = Math.min(fromState.getX(), toState.getX());
            int minY = Math.min(fromState.getY(), toState.getY());
            int maxX = Math.max(fromState.getX(), toState.getX());
            int maxY = Math.max(fromState.getY(), toState.getY());
            // must be same row or column
            if (fromState.getX() != toState.getX() && fromState.getY() != toState.getY()) return false;
            
            // no barriers between on X line
            if (fromState.getX() != toState.getX()) {
                for (int x =minX; x <= maxX; x++) {
                    if (getCell(new Coord(x, fromState.getY())) == '#') return false;
                }
            }
            // no barriers between on Y line
            if (fromState.getY() != toState.getY()) {
                for (int y = minY; y <= maxY; y++) {
                    if (getCell(new Coord(fromState.getX(), y)) == '#') return false;
                }
            }

            return true;
//            // all diagonals are spaces = useless, ignore
//            return (getCell(toState.southWest()) != ' ')
//                    || (getCell(toState.southEast()) != ' ')
//                    || (getCell(toState.northWest()) != ' ')
//                    || (getCell(toState.northEast()) != ' ');
        }

        @Override
        protected boolean findAllRoutes() {
            return false;
        }
    }

}
