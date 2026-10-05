package com.johnpickup.ebc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day3 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day3");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                List<Integer> numbers = Arrays.stream(lines.get(0).split(",")).map(Integer::parseInt).toList();
                Set<Integer> unique = new HashSet<>(numbers);
                Integer part1 = unique.stream().reduce(0, Integer::sum);
                System.out.println("Part 1: " + part1);

                Integer part2 = unique.stream().sorted().limit(20).reduce(0, Integer::sum);
                System.out.println("Part 2: " + part2);

                Map<Integer, Integer> sizeCounts = new HashMap<>();
                numbers.forEach(n -> sizeCounts.put(n, sizeCounts.getOrDefault(n, 0) + 1));
                int part3 = sizeCounts.values().stream().max(Integer::compareTo).get();

                System.out.println("Part 3: " + part3);


            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

}
