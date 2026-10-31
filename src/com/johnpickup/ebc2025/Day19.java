package com.johnpickup.ebc2025;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day19 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day19");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();
                Board board = new Board(lines);
                System.out.println(board);
                System.out.println("Part 1: " + board.part1());


            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Board {
        final List<Gate> gates;
        Board(List<String> lines) {
            gates = lines.stream().map(Gate::new).toList();
        }

        public int part1() {
            int x = 0;
            int y = 0;
            int minTotalFlaps = 0;
            int maxTotalFlaps = 0;
            for (Gate gate : gates) {
                int distanceToGate = gate.getX() - x;
                int minDeltaY = gate.getLower() - minTotalFlaps;
                int maxDeltaY = gate.getHigher() - maxTotalFlaps;
                x = gate.getX();

                if (minDeltaY < distanceToGate) {
                    minDeltaY += Math.ceilDiv(distanceToGate - minDeltaY, 2);
                }
                if (maxDeltaY > distanceToGate) {
                    maxDeltaY -= Math.floorDiv(distanceToGate - minDeltaY, 2);
                }
                minTotalFlaps += minDeltaY;

                maxTotalFlaps += maxDeltaY;
            }
            return minTotalFlaps;
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = getHeight(); y >= 0; y--) {
                for (int x = 0; x <= getWidth(); x++) {
                    int finalY = y;
                    sb.append(gateAtX(x).map(g -> g.blocks(finalY) ? '#' : '.').orElse('.'));
                }
                sb.append('\n');
            }
            return sb.toString();
        }

        int getWidth() {
            return gates.stream().map(Gate::getX).max(Integer::compareTo).orElseThrow();
        }
        int getHeight() {
            return gates.stream().map(Gate::getHigher).max(Integer::compareTo).map(h -> h+2).orElseThrow();
        }

        Optional<Gate> gateAtX(int x) {
            return gates.stream().filter(g -> g.getX()==x).findFirst();
        }
    }

    @Getter
    static class Gate {
        private final int x;
        private final int lower;
        private final int higher;
        Gate(String line) {
            String[] parts = line.split(",");
            x = Integer.parseInt(parts[0]);
            lower = Integer.parseInt(parts[1]);
            higher = lower + Integer.parseInt(parts[2])-1;
        }

        public boolean blocks(int y) {
            return y < lower || y > higher;
        }
    }
}
