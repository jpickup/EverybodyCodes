package com.johnpickup.Story4;

import com.johnpickup.util.Coord;
import com.johnpickup.util.Range;
import com.johnpickup.util.SparseGrid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Quest2 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("Story4", "Quest2");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                Swarm swarm = new Swarm(lines);

                System.out.printf("Part1: %d%n", swarm.part1());
                System.out.printf("Part2: %d%n", 0);
                System.out.printf("Part3: %d%n", 0);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Swarm {
        final Coord start;
        final Coord a;
        final Coord b;
        final Coord c;
        final char[] moves;

        Swarm(List<String> lines) {
            start = extractCoord(lines.get(0));
            a = extractCoord(lines.get(1));
            b = extractCoord(lines.get(2));
            c = extractCoord(lines.get(3));
            moves =lines.get(4).split("=")[1].toCharArray();

        }

        private Coord extractCoord(String s) {
            return new Coord(s.substring(s.indexOf('[')+1, s.indexOf(']')));
        }

        public int part1() {
            Set<Coord> locations = new HashSet<>();
            locations.add(start);
            Coord location = start;
            for (char move : moves) {
                Coord target = switch(move) {
                    case 'A' -> a;
                    case 'B' -> b;
                    case 'C' -> c;
                    default -> throw new RuntimeException("Unknown move: " + move);
                };
                Coord delta = target.calcDelta(location);
                location = new Coord(location.getX() + delta.getX()/2,
                        location.getY() + delta.getY()/2);
                locations.add(location);
            }
            return locations.size();
        }
    }
}
