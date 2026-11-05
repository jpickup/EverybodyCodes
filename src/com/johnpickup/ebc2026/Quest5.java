package com.johnpickup.ebc2026;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Quest5 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2026", "Quest5");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                Land land = new Land(lines);

                System.out.printf("Part1: %d%n", Land.part1(land));
                System.out.printf("Part2: %d%n", Land.part2(Land.part1));
                System.out.printf("Part3: %d%n", Land.part3(Land.part2));

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    static class Land {
        public static final char LAND = '#';
        public static final char WATER = '.';
        public static final char SAND = '=';
        public static final char TREE = '@';
        public static final char UNKNOWN = ' ';
        final CharGrid grid;
        static Land part1 = null;
        static Land part2 = null;
        Land(List<String> lines) {
            grid = new CharGrid(lines);
        }

        @Override
        public String toString() {
            return grid.toString();
        }

        public static int part1(Land initial) {
            Land prevLand = null;
            Land newLand = new Land(new CharGrid(initial.grid));
            newLand.setCentreAsLand();
            while (!newLand.equals(prevLand)) {
                prevLand = newLand;
                newLand = newLand.iteratePart1(initial);
            }
            newLand.fillWater();
            part1 = newLand;
            return newLand.grid.findCells(LAND).size();
        }

        public static int part2(Land initial) {
            Land prevLand = null;
            Land newLand = new Land(new CharGrid(initial.grid));
            while (!newLand.equals(prevLand)) {
                prevLand = newLand;
                newLand = newLand.iteratePart2(initial);
            }
            part2 = newLand;
            return newLand.grid.findCells(SAND).size();
        }

        public static int part3(Land initial) {
            Land treeLand = new Land(new CharGrid(initial.grid));
            treeLand.fillInitialTrees();
            treeLand.growTrees();
            treeLand.growTrees();
            return treeLand.grid.findCells(TREE).size();
        }

        private void growTrees() {
            List<Coord> newTrees = grid.findCells(LAND).stream().filter(this::canGrowTree).toList();
            newTrees.forEach(c -> grid.setCell(c, TREE));
        }

        private boolean canGrowTree(Coord coord) {
            long treeCount = coord.adjacent8().stream().filter(c -> grid.getCell(c)==TREE).count();
            long sandCount = coord.adjacent8().stream().filter(c -> grid.getCell(c)==SAND).count();
            return treeCount >= 4 && sandCount == 0;
        }

        private void fillInitialTrees() {
            List<Coord> initialTrees = grid.findCells(LAND).stream().filter(c -> canBeTree(c)).toList();
            initialTrees.forEach(c -> grid.setCell(c, TREE));
        }

        /* Tree pattern
            .+++.
            +++++
            ++X++
            +++++
            .+++.
         */
        private boolean canBeTree(Coord c) {
            List<Character> landOrTree = Arrays.asList(LAND, TREE);
            return 
                    // top row
                    isOneOf(c.north().north().east(), landOrTree)
                    && isOneOf(c.north().north().west(), landOrTree)
                    && isOneOf(c.north().north(), landOrTree)
                    // 2nd row
                    && isOneOf(c.north().east().east(), landOrTree)
                    && isOneOf(c.north().east(), landOrTree)
                    && isOneOf(c.north(), landOrTree)
                    && isOneOf(c.north().west(), landOrTree)
                    && isOneOf(c.north().west().west(), landOrTree)
                    // middle row
                    && isOneOf(c.east().east(), landOrTree)
                    && isOneOf(c.east(), landOrTree)
                    && isOneOf(c.west(), landOrTree)
                    && isOneOf(c.west().west(), landOrTree)
                    // 4th row
                    && isOneOf(c.south().east().east(), landOrTree)
                    && isOneOf(c.south().east(), landOrTree)
                    && isOneOf(c.south(), landOrTree)
                    && isOneOf(c.south().west(), landOrTree)
                    && isOneOf(c.south().west().west(), landOrTree)
                    // bottom row
                    && isOneOf(c.south().south().east(), landOrTree)
                    && isOneOf(c.south().south().west(), landOrTree)
                    && isOneOf(c.south().south(), landOrTree)
                    ;
        }
        
        private boolean isOneOf(Coord coord, Collection<Character> item) {
            return item.stream().anyMatch(ch -> grid.getCell(coord) == ch);
        }

        private void fillWater() {
            grid.allCells().forEach(c -> {
                if (grid.getCell(c) != LAND) grid.setCell(c, WATER);
            });
        }

        private Land iteratePart1(Land initial) {
            CharGrid resultGrid = new CharGrid(grid);
            resultGrid.allCells().forEach(c -> {
                if (grid.getCell(c) != LAND && hasAdjacentLand(c) && canBeLand(c)) {
                    resultGrid.setCell(c, LAND);
                }
            });
            return new Land(resultGrid);
        }

        private Land iteratePart2(Land initial) {
            CharGrid resultGrid = new CharGrid(grid);
            resultGrid.allCells().forEach(c -> {
                if (grid.getCell(c) != WATER && (hasAdjacentWater(c) || (hasAdjacentSand(c) && canBeSand(c)))) {
                    resultGrid.setCell(c, SAND);
                }
            });
            return new Land(resultGrid);
        }

        private boolean canBeLand(Coord c) {
            return grid.countAdjacent(c, LAND) + grid.countAdjacent(c, '1') >= 5;
        }

        private boolean canBeSand(Coord c) {
            return grid.countAdjacent(c, WATER) + grid.countAdjacent(c, SAND) + grid.countAdjacent(c, UNKNOWN) >= 5;
        }

        private boolean hasAdjacentLand(Coord c) {
            return grid.hasAdjacent(c, LAND);
        }

        private boolean hasAdjacentSand(Coord c) {
            return grid.hasAdjacent(c, SAND);
        }

        private boolean hasAdjacentWater(Coord c) {
            return grid.hasAdjacent(c, WATER);
        }

        private void setCentreAsLand() {
            Coord centre = new Coord(grid.getWidth()/2, grid.getHeight()/2);
            grid.setCell(centre, LAND);
        }
    }
}
