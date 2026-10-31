package com.johnpickup.ebc2025;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day11 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day11");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                Long[] array = lines.stream().map(Long::parseLong).toList().toArray(new Long[]{});
                Flock flock = new Flock(array);

                System.out.println("Part 1: " + part1(flock));
                System.out.println("Part 2: " + part2(flock));
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static long part1(Flock flock) {
        long round = 1;
        Flock prev;
        do {
            prev = flock;
            flock = flock.phase1();
            //System.out.printf("Phase 1 - %d %s %d%n", round, flock, flock.checksum());
            round++;
        } while (round <= 10 && !flock.equals(prev));
        round--;
        do {
            prev = flock;
            flock = flock.phase2();
            //System.out.printf("Phase 2 - %d %s %d%n", round, flock, flock.checksum());
            round++;
        } while (round <= 10 && !flock.equals(prev));
        return flock.checksum();
    }

    static long part2Naive(Flock flock) {
        long round = 0;
        Flock prev;
        while (true) {
            prev = flock;
            flock = flock.phase1();
            //System.out.printf("Phase 1 - %d %s %d%n", round, flock, flock.checksum());
            if (flock.equals(prev))
                break;
            else
                round++;
        }
        while (true) {
            prev = flock;
            flock = flock.phase2();
            //System.out.printf("Phase 2 - %d %s %d%n", round, flock, flock.checksum());
            if (flock.equals(prev))
                break;
            else
                round++;
        }
        return round;
    }

    static long part2(Flock flock) {
        long round = 0;
        Flock prev;
        while (true) {
            prev = flock;
            flock = flock.phase1();
            //System.out.printf("Phase 1 - %d %s %d%n", round, flock, flock.checksum());
            if (flock.equals(prev))
                break;
            else
                round++;
        }
        while (true) {
            prev = flock;
            flock = flock.phase2();
            //System.out.printf("Phase 2 - %d %s %d%n", round, flock, flock.checksum());
            if (flock.equals(prev))
                break;
            else
                round++;
        }
        return round;
    }

    @RequiredArgsConstructor
    @ToString
    @EqualsAndHashCode
    static class Flock {
        final Long[] columns;

        long checksum() {
            long result = 0;
            for (int i = 0; i < columns.length; i++) {
                result += columns[i] * (i+1);
            }
            return result;
        }

        Flock phase1() {
            Long[] newColumns = Arrays.copyOf(columns, columns.length);
            for (int i = 1 ; i < columns.length; i++) {
                if (newColumns[i-1] > newColumns[i]) {
                    newColumns[i-1] = newColumns[i-1] - 1;
                    newColumns[i] = newColumns[i] + 1;
                }
            }
            return new Flock(newColumns);
        }

        Flock phase2() {
            Long[] newColumns = Arrays.copyOf(columns, columns.length);
            for (int i = 1 ; i < columns.length; i++) {
                if (newColumns[i-1] < newColumns[i]) {
                    newColumns[i-1] = newColumns[i-1] + 1;
                    newColumns[i] = newColumns[i] - 1;
                }
            }
            return new Flock(newColumns);
        }
    }
}
