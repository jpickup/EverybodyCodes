package com.johnpickup.ebc2025;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day4 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day4");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                List<Integer> gears = lines.stream().filter(s -> !s.contains("|")).map(Integer::parseInt).toList();
                double ratio = gears.getFirst() * 1.0 / gears.getLast();

                long part1 = (long)(2025 * ratio);
                System.out.println("Part 1: " + part1);
                BigDecimal part2 = BigDecimal.valueOf(10000000000000d).multiply(BigDecimal.valueOf(gears.getLast())).divide(BigDecimal.valueOf(gears.getFirst()), 0, RoundingMode.UP);
                System.out.println("Part 2: " + part2);

                if (inputFilename.contains("3")) {
                    List<Ratio> ratios = lines.stream().map(Ratio::parse).toList();

                    BigDecimal rotations = BigDecimal.valueOf(100);
                    Ratio prev = null;
                    for (Ratio next : ratios) {
                        if (prev != null) {
                            rotations = rotations.multiply(BigDecimal.valueOf(prev.output)).divide(BigDecimal.valueOf(next.input), 20, RoundingMode.DOWN);
                        }
                        prev = next;
                    }
                    System.out.println("Part 3: " + rotations.setScale(0, RoundingMode.DOWN));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class Ratio {
        final int input;
        final int output;

        public static Ratio parse(String s) {
            if (s.contains("|")) {
                String[] parts = s.split("\\|");
                return new Ratio(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            } else {
                return new Ratio(Integer.parseInt(s), Integer.parseInt(s));
            }
        }
    }
}
