package com.johnpickup.ebc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day14 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day14");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();
                Pattern pattern = new Pattern(lines);

                System.out.println("Part 1: " + pattern.part1());
                System.out.println("Part 2: " + pattern.part2());
                if (inputFilename.contains("3")) {
                    System.out.println("Part 3: " + pattern.part3());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class Pattern {
        private final CharGrid grid;
        Pattern(List<String> lines) {
            grid = new CharGrid(lines);
        }

        public long part1() {
            return countPatterns(10);
        }

        public long part2() {
            return countPatterns(2025);
        }

        public long part3() {
            long result = 0L;
            Pattern floor = new Pattern(new CharGrid(34, 34));

            int last = 0;
            for (int i = 1 ; i <= 1000000000; i++) {
                floor = floor.iterate();
                if (floor.centreMatches(this)) {
                    System.out.printf("%d - %d --> %d%n", i, i-last, floor.activeCount());
                    last = i;
                    result += floor.activeCount();
                }
                if (result > 10000) break;      // solve the pattern elsewhere
            }
            return result;
            /*
             * Test pattern is:
             125 - 125 --> 552
             1017 - 892 --> 588
             4220 - 3203 --> 552
             5112 - 892 --> 588
             8315 - 3203 --> 552
             9207 - 892 --> 588
             12410 - 3203 --> 552
             13302 - 892 --> 588
             16505 - 3203 --> 552
             17397 - 892 --> 588
             20600 - 3203 --> 552
             * so it repeats, and we just need to count how many are in 1000000000
             *
             * cycle length is 892+3203 = 4095
             *
             * Real pattern is:
             399 - 399 --> 532
            2378 - 1979 --> 660
            2383 - 5 --> 548
            3303 - 920 --> 552
            3446 - 143 --> 568
            4020 - 574 --> 624
            4070 - 50 --> 576
            4494 - 424 --> 532
            6473 - 1979 --> 660
            6478 - 5 --> 548
            7398 - 920 --> 552
            7541 - 143 --> 568
            8115 - 574 --> 624
            * round is much longer
            *
             */
        }

        private boolean centreMatches(Pattern pattern) {
            boolean result = true;

            for (int x=0; x < pattern.grid.getWidth(); x++) {
                for (int y = 0; y < pattern.grid.getHeight(); y++) {
                    Coord c1 = new Coord(x, y);
                    Coord c2 = new Coord(x+13, y+13);       // 34/2 - 8/2 = 13
                    result &= (pattern.grid.getCell(c1) == this.grid.getCell(c2));
                    if (!result) break;
                }
            }
            return result;
        }

        public long countPatterns(int rounds) {
            long result = 0;
            Pattern pattern = this;
            for (int i = 0 ; i < rounds; i++) {
                pattern = pattern.iterate();
                result += pattern.activeCount();
            }
            return result;
        }

        private int activeCount() {
            return grid.findCells('#').size();
        }

        public Pattern iterateRounds(int number) {
            Pattern result = this;
            for (int i = 0 ; i < number; i++) {
                result = result.iterate();
            }
            return result;
        }

        private Pattern iterate() {
            CharGrid newGrid = new CharGrid(grid.getWidth(), grid.getHeight());

            for (int x=0; x < grid.getWidth(); x++) {
                for (int y = 0; y < grid.getHeight(); y++) {
                    Coord coord = new Coord(x, y);
                    if (grid.getCell(coord) == '#') {
                        newGrid.setCell(coord, (activeNeighbourCount(coord) % 2 == 1) ? '#' : '.');
                    } else {
                        newGrid.setCell(coord, (activeNeighbourCount(coord) % 2 == 0) ? '#' : '.');
                    }
                }
            }
            return new Pattern(newGrid);
        }

        private int activeNeighbourCount(Coord coord) {
            return (isActive(coord.northEast()) ? 1 : 0)
                    + (isActive(coord.northWest()) ? 1 : 0)
                    + (isActive(coord.southEast()) ? 1 : 0)
                    + (isActive(coord.southWest()) ? 1 : 0)
                    ;
        }

        private boolean isActive(Coord coord) {
            return grid.getCell(coord) == '#';
        }

    }
}
