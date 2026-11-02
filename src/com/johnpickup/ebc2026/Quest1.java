package com.johnpickup.ebc2026;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Quest1 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2026", "Quest1");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                Defences defences = new Defences(lines.getFirst());
                System.out.printf("part 1: %d%n", defences.part1());
                System.out.printf("part 2: %d%n", defences.part2());
                System.out.printf("part 3: %d%n", defences.part3());

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Defences {
        private final List<Integer> outpost;
        TreeMap<Integer, Integer> frameSizes;
        TreeMap<Integer, Integer> boxSizes;
        Defences(String input) {
            outpost = Arrays.stream(input.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            populateSizes();
        }

        private void populateSizes() {
            frameSizes = new TreeMap<>();
            int width = 1;
            int size = 0;
            int biggest = outpost.stream().max(Integer::compareTo).orElseThrow();
            while (size < biggest) {
                size = width * 2 + (width + 2) * 2;
                width++;
                frameSizes.put(size, size);
            }
            boxSizes = new TreeMap<>();
            width = 1;
            size = 0;
            while (size < biggest) {
                size = (width * 2 + 1) * (width * 2 + 1) - 1;
                width++;
                boxSizes.put(size, size);
            }
        }

        int part1() {
            return outpost.stream().map(this::missingForSquare).reduce(0, Integer::sum);
        }

        private int missingForSquare(int cones) {
            double diff = Math.pow(Math.ceil(Math.sqrt(cones * 1.0d)), 2d) - cones;
            return (int)diff;
        }

        int part2() {
            return outpost.stream().map(this::missingForFrame).reduce(0, Integer::sum);
        }

        private int missingForFrame(int cones) {
            Map.Entry<Integer, Integer> entry = frameSizes.ceilingEntry(cones);
            return entry.getValue() - cones;
        }

        int part3() {
            return outpost.stream().map(this::missingForBox).reduce(0, Integer::sum);
        }

        private int missingForBox(int cones) {
            Map.Entry<Integer, Integer> entry = boxSizes.ceilingEntry(cones);
            return entry.getValue() - cones;
        }
    }

}
