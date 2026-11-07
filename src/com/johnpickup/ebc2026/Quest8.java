package com.johnpickup.ebc2026;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Quest8 {
    static boolean isTest;
    static boolean debug = false;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2026", "Quest8");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                EntField entField = new EntField(lines);

                System.out.printf("Part1: %d%n", entField.part1());
                System.out.printf("Part2: %d%n", entField.part2());
                System.out.printf("Part3: %d%n", EntField.part3(entField.grid));

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class EntField {
        final CharGrid grid;
        EntField(List<String> lines) {
            grid = new CharGrid(lines);
            grid.findCells('.').forEach(c -> grid.setCell(c, ' '));
        }

        public long part1() {
            return calcEnergyForDirection(HexDirection.SOUTH_EAST);
        }

        public long part2() {
            return calcEnergyForDirection(HexDirection.SOUTH_EAST)
                   + calcEnergyForDirection(HexDirection.SOUTH_WEST)
                    + calcEnergyForDirection(HexDirection.WEST)
                    + calcEnergyForDirection(HexDirection.NORTH_WEST)
                    + calcEnergyForDirection(HexDirection.NORTH_EAST)
                    + calcEnergyForDirection(HexDirection.EAST)
                    ;
        }

        static char warrior = '9'+1;
        static char initial = '1'-3;

        public static long part3(CharGrid initialGrid) {
            long warriorCount = 0;
            long energy = 0;
            HexDirection direction = HexDirection.SOUTH_EAST;
            CharGrid stateGrid = new CharGrid(initialGrid);
            while (warriorCount <= 100000) {
                Set<Coord> litCells = litCellsForDirection(stateGrid, direction);
                energy += litCells.size();
                if (debug) System.out.println(showCells(stateGrid, litCells));
                final CharGrid nextGrid = growGrid(stateGrid, litCells);
                if (debug) System.out.println(showCells(nextGrid, litCells));
                Set<Coord> warriors = nextGrid.findCells(warrior);
                if (debug) System.out.printf("%s Energy=%d NewWarriors=%d%n", direction, litCells.size(), warriors.size());
                warriorCount += warriors.size();
                warriors.forEach(w -> nextGrid.setCell(w, initial));
                direction = direction.rotate();
                stateGrid = nextGrid;
            }
            return energy;
        }

        private static CharGrid growGrid(CharGrid grid, Set<Coord> litCells) {
            CharGrid result = new CharGrid(grid);
            grid.allCells().forEach(c -> {
                char cell = grid.getCell(c);
                if (cell != ' ') {
                    if (litCells.contains(c)) {
                        result.setCell(c, (char) (cell + 1));
                    } else if (cell < '1') {
                        result.setCell(c, (char) (cell + 1));
                    }
                }
            });
            return result;
        }

        private long calcEnergyForDirection(HexDirection direction) {
//            Set<Coord> litCells = grid.allCells().filter(c -> catchesSun(c, direction)).collect(Collectors.toSet());
//            System.out.printf("%s - %d%n", direction, litCells.size());
//            System.out.println(showCells(litCells));
//            System.out.println("----------------------");
            return litCellsForDirection(grid, direction).size();
        }

        private static Set<Coord> litCellsForDirection(CharGrid grid, HexDirection direction) {
            return grid.allCells().filter(c -> catchesSun(grid, c, direction)).collect(Collectors.toSet());
        }

        private static String showCells(CharGrid grid, Set<Coord> cells) {
            Set<Coord> external = cells.stream().filter(c -> !grid.inBounds(c)).collect(Collectors.toSet());
            if (! external.isEmpty()) {
                throw new RuntimeException("External " + external);
            }
            CharGrid displayGrid = new CharGrid(grid);
            displayGrid.allCells().forEach(c -> { if (!cells.contains(c)) displayGrid.setCell(c, ' '); });
            return displayGrid.toString();
        }

        private static int height(CharGrid grid, Coord c) {
            if (grid.getCell(c) == ' ') return 0;
            return grid.getCell(c) - '0';
        }

        private static boolean catchesSun(CharGrid grid, Coord c, HexDirection direction) {
            HexDirection sunDirection = direction.opposite();
            int height = height(grid, c);
            if (height <= 0) return false;
            boolean result = true;
            while (result && grid.inBounds(c)) {
                c = sunDirection.applyTo(c);
                if (grid.getCell(c) == ' ') continue;
                height++;
                result = height > height(grid, c);
            }
            return result;
        }
    }

    enum HexDirection {
        WEST,
        SOUTH_WEST,
        NORTH_WEST,
        EAST,
        NORTH_EAST,
        SOUTH_EAST;

        Coord applyTo(Coord c) {
            return switch (this) {
                case EAST -> c.east();
                case WEST -> c.west();
                case NORTH_EAST -> c.northEast();
                case NORTH_WEST -> c.northWest();
                case SOUTH_EAST -> c.southEast();
                case SOUTH_WEST -> c.southWest();
            };
        }

        HexDirection opposite() {
            return switch (this) {
                case EAST -> WEST;
                case WEST -> EAST;
                case NORTH_EAST -> SOUTH_WEST;
                case NORTH_WEST -> SOUTH_EAST;
                case SOUTH_EAST -> NORTH_WEST;
                case SOUTH_WEST -> NORTH_EAST;
            };
        }

        HexDirection rotate() {
            return switch (this) {
                case EAST -> SOUTH_EAST;
                case WEST -> NORTH_WEST;
                case NORTH_EAST -> EAST;
                case NORTH_WEST -> NORTH_EAST;
                case SOUTH_EAST -> SOUTH_WEST;
                case SOUTH_WEST -> WEST;
            };
        }
    }
}
