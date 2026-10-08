package com.johnpickup.ebc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day6 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day6");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                String line = lines.getFirst();
                int knights = 0;
                int part1 = 0;
                int part2 = 0;
                long part3 = 0L;

                Map<Character, Integer> knightsByType = new HashMap<>();

                for (char c : line.toCharArray()) {
                    if (c == 'A') {
                        knights++;
                    } else if (c == 'a') {
                        part1 += knights;
                    }

                    if (Character.toUpperCase(c) == c) {
                        knightsByType.put(c, knightsByType.getOrDefault(c, 0) + 1);
                    } else if (Character.toLowerCase(c) == c) {
                        part2 += knightsByType.getOrDefault(Character.toUpperCase(c), 0);
                    }
                }
                System.out.println("Part 1: " + part1);
                System.out.println("Part 2: " + part2);

                int repeats = 1000;
                int limit = 1000;

                if (inputFilename.contains("3")) {
                    int length = line.length();
                    int maxLen = length * repeats;
                    int idx = 0;
                    while (idx < maxLen) {
                        char c = line.charAt(idx % length);
                        if (Character.isLowerCase(c)) {
                            int knightsForChar = 0;
                            for (int i = 1; i <= limit; i++) {
                                char prior = getLineChar(line, idx - i, maxLen);
                                char post = getLineChar(line, idx + i, maxLen);
                                if (Character.toUpperCase(c) == prior) {
                                    knightsForChar++;
                                }
                                if (Character.toUpperCase(c) == post) {
                                    knightsForChar++;
                                }
                            }
                            part3 += knightsForChar;
                        }
                        idx++;
                    }
                    System.out.println("Part 3: " + part3);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static char getLineChar(String s, int idx, int maxLen) {
        if (idx < 0) return ' ';
        if (idx >= maxLen) return ' ';
        return s.charAt(idx % s.length());
    }

}
