package com.johnpickup.ebc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day12 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day12");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                CharGrid grid = new CharGrid(lines);

                System.out.println("Part 1: " + part1(new Barrels(grid, Collections.singleton(new Coord(0,0)))));
                System.out.println("Part 2: " + part2(new Barrels(grid, Arrays.asList(new Coord(0,0), new Coord(grid.getWidth()-1, grid.getHeight()-1)))));
                if (inputFilename.contains("3")) {
                    System.out.println("Part 3: " + part3(grid));
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static int part1(Barrels barrels) {
        barrels = barrels.run();
        return barrels.ignitedCount();
    }

    static int part2(Barrels barrels) {
        barrels.ignite(new Coord(barrels.grid.getWidth()-1, barrels.grid.getHeight()-1));
        barrels = barrels.run();
        return barrels.ignitedCount();
    }

    static int part3(CharGrid grid) {
        Set<Coord> allCoords = new Barrels(grid, Collections.emptySet()).allCoords();
        Character biggest1 = allCoords.stream().map(c -> grid.getCell(c)).max(Character::compare).orElseThrow();
        Set<Coord> firstSet = grid.findCells(biggest1);
        Barrels firstBest = null;
        for (Coord first : firstSet) {
            Barrels barrels = new Barrels(grid, Collections.singleton(first));
            barrels = barrels.run();
            if (barrels.ignitedCount() > Optional.ofNullable(firstBest).map(Barrels::ignitedCount).orElse(0))
                firstBest = barrels;
        }

        Set<Coord> step2 = new HashSet<>(allCoords);
        step2.removeAll(firstBest.ignited);
        Barrels nextBest = null;
        for (Coord s2 : step2) {
            HashSet<Coord> ignited = new HashSet<>(firstBest.ignited);
            ignited.add(s2);
            Barrels barrels = new Barrels(grid, ignited);
            barrels = barrels.run();
            if (barrels.ignitedCount() > Optional.ofNullable(nextBest).map(Barrels::ignitedCount).orElse(0))
                nextBest = barrels;
        }

        Set<Coord> step3 = new HashSet<>(allCoords);
        step3.removeAll(nextBest.ignited);
        Barrels nextBest2 = null;
        for (Coord s3 : step3) {
            HashSet<Coord> ignited = new HashSet<>(nextBest.ignited);
            ignited.add(s3);
            Barrels barrels = new Barrels(grid, ignited);
            barrels = barrels.run();
            if (barrels.ignitedCount() > Optional.ofNullable(nextBest2).map(Barrels::ignitedCount).orElse(0))
                nextBest2 = barrels;
        }

        //3294 - incorrect
        //3892 - incorrect

        return nextBest2.ignitedCount();
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    static class Barrels {
        final CharGrid grid;
        final Set<Coord> ignited;
        Barrels(CharGrid grid, Collection<Coord> initialIgnited) {
            this.grid = grid;
            ignited = new HashSet<>(initialIgnited);
        }

        Barrels cycle() {
            HashSet<Coord> next = new HashSet<>(ignited);
            for (Coord coord : ignited) {
                if (coord.getX() < grid.getWidth()-1 && grid.getCell(coord) >= grid.getCell(coord.east()))  next.add(coord.east());
                if (coord.getY() < grid.getHeight()-1 && grid.getCell(coord) >= grid.getCell(coord.south())) next.add(coord.south());
                if (coord.getX() > 0 && grid.getCell(coord) >= grid.getCell(coord.west()))  next.add(coord.west());
                if (coord.getY() > 0 && grid.getCell(coord) >= grid.getCell(coord.north())) next.add(coord.north());
            }
            return new Barrels(grid, next);
        }

        int ignitedCount() {
            return ignited.size();
        }

        public void ignite(Coord coord) {
            ignited.add(coord);
        }

        public Barrels run() {
            Barrels prev = null;
            Barrels barrels = this;
            while (!barrels.equals(prev)) {
                prev = barrels;
                barrels = barrels.cycle();
            }

            return barrels;
        }

        public Set<Coord> allCoords() {
            Set<Coord> result = new HashSet<>();
            for (int x = 0; x < grid.getWidth(); x++) {
                for (int y= 0; y < grid.getHeight(); y++) {
                    result.add(new Coord(x, y));
                }
            }
            return result;
        }
    }
}
