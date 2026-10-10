package com.johnpickup.ebc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.Sets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day10 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day10");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                ChessBoard board = new ChessBoard(lines);

                System.out.println("Part 1: " + board.part1());
                System.out.println("Part 2: " + board.part2());


            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class ChessBoard {
        final CharGrid grid;
        final Coord dragon;
        final Set<Coord> sheep;
        final Set<Coord> hides;
        ChessBoard(List<String> lines) {
            grid = new CharGrid(lines);
            dragon = grid.findCells('D').stream().findFirst().orElseThrow();
            sheep = grid.findCells('S');
            hides = grid.findCells('#');
        }

        @Override
        public String toString() {
            return grid.toString();
        }

        public int part1() {
            Set<Coord> dragons = new HashSet<>();
            dragons.add(dragon);
            int moveCount = isTest ? 3 : 4;
            for (int i = 0; i < moveCount; i++) {
                dragons.addAll(findNextDragons(dragons));
            }
            return Sets.intersection(dragons, sheep).size();
        }

        private Set<Coord> findNextDragons(Set<Coord> dragons) {
            Set<Coord> additional = new HashSet<>();
            for (Coord d : dragons) {
                Set<Coord> possible = findPossible(d);
                additional.addAll(possible.stream().filter(this::isValid).toList());
            }
            return additional;
        }

        public int part2() {
            int rounds = isTest ? 3 : 20;
            int eaten = 0;
            Set<Coord> dragons = new HashSet<>();
            dragons.add(dragon);
            Set<Coord> movedSheep = new HashSet<>(sheep);

            for (int round = 1; round <= rounds; round++) {
                Set<Coord> nextDragons = findNextDragons(dragons);
                Set<Coord> eatenSheep = Sets.intersection(nextDragons, movedSheep);
                eatenSheep.removeAll(hides);
                eaten += eatenSheep.size();
                movedSheep.removeAll(eatenSheep);
                movedSheep = moveDown(movedSheep);
                eatenSheep = Sets.intersection(nextDragons, movedSheep);
                eatenSheep.removeAll(hides);
                eaten += eatenSheep.size();
                movedSheep.removeAll(eatenSheep);
                dragons = nextDragons;
            }
            return eaten;
        }

        private Set<Coord> moveDown(Set<Coord> movedSheep) {
            Set<Coord> result = new HashSet<>();
            for (Coord c : movedSheep) {
                if (c.getY()+1 < grid.getHeight()) {
                    result.add(c.moveBy(0,1));
                }
            }
            return result;
        }

        private Set<Coord> findPossible(Coord c) {
            Set<Coord> result = new HashSet<>();
            result.add(c.moveBy(-2, -1));
            result.add(c.moveBy(-2, 1));
            result.add(c.moveBy(2, -1));
            result.add(c.moveBy(2, 1));
            result.add(c.moveBy(-1, -2));
            result.add(c.moveBy(-1, 2));
            result.add(c.moveBy(1, -2));
            result.add(c.moveBy(1, 2));
            return result;
        }

        private boolean isValid(Coord coord) {
            return coord.getX()>= 0 && coord.getY()>=0
                    && coord.getX()<grid.getWidth() && coord.getY()<grid.getHeight();
        }
    }
}
