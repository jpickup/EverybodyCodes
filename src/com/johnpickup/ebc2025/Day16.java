package com.johnpickup.ebc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day16 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day16");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                Wall wall = new Wall(lines.getFirst());
                System.out.println("Part 1: " + wall.part1());
                if (inputFilename.contains("Day16/2")) {
                    System.out.println("Part 2: " + wall.part2());
                }
                if (inputFilename.contains("Day16/3")) {
                    System.out.println("Part 3: " + wall.part3());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Wall {
        private final List<Integer> inputs;
        Wall(String line) {
            String[] parts = line.split(",");
            inputs = Arrays.stream(parts).map(Integer::parseInt).toList();
        }

        public int part1() {
            int[] columns = solvePart1(90);
            return Arrays.stream(columns).reduce(0, Integer::sum);
        }

        public long part2() {
            return solvePart2().stream().reduce(1L, (a,b) -> (a*b));
        }

        public long part3() {
            List<Long> buildInputs = solvePart2();
            long target = 202520252025000L;
            return 0L;
        }

        private List<Long> solvePart2() {
            List<Long> result = new ArrayList<>();
            int[] remaining = new int[inputs.size()];
            for (int i = 0 ; i < inputs.size(); i++) {
                remaining[i]=inputs.get(i);
            }
            long idx = 1;
            while (Arrays.stream(remaining).reduce(0, Integer::sum) > 0) {
                boolean allMatch = true;
                for (int i=0; i < inputs.size(); i++) {
                    if ((i+1) % idx==0) {
                        allMatch &= (remaining[i] > 0);
                    }
                }
                if (allMatch) {
                    result.add(idx);
                    for (int i=0; i < inputs.size(); i++) {
                        if ((i+1) % idx==0) remaining[i]--;
                    }
                }
                idx++;
            }

            return result;
        }

        private int[] solvePart1(int width) {
            int[] result = new int[width];
            for (Integer input : inputs) {
                for (int col=0; col<width; col++) {
                    if ((col+1) % input == 0) {
                        result[col]++;
                    }
                }
            }
            return result;
        }
    }

}
