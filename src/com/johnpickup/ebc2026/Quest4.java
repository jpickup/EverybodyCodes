package com.johnpickup.ebc2026;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Quest4 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2026", "Quest4");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                List<Result> results = lines.stream().map(Result::new).toList();

                System.out.printf("Part1: %d%n", part1(results));
                System.out.printf("Part2: %d%n", part2(results));
                System.out.printf("Part3: %d%n", part3(results));

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static long part1(List<Result> results) {
        Map<Integer, List<Result>> resultById = results.stream().collect(Collectors.groupingBy(Result::getId));

        Map<Integer, Integer> totalTimeById = resultById.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                e -> e.getValue().stream().map(Result::getTime).reduce(0, Integer::sum)));

        List<Integer> times = new ArrayList<>(totalTimeById.values());
        times.sort(Integer::compareTo);
        long part1 = 0;
        for (int i = 0; i < times.size(); i++) {
            part1 += (long)(i+1) * times.get(i);
        }
        return part1;
    }

    static long part2(List<Result> results) {
        Map<Integer, List<Result>> resultById = results.stream().collect(Collectors.groupingBy(Result::getId));
        Map<Integer, Integer> totalTimeById = resultById.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                e -> e.getValue().stream().map(Result::getTime).reduce(0, Integer::sum)));
        Map<Integer, Integer> countById = resultById.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                e -> e.getValue().stream().map(r -> 1).reduce(0, Integer::sum)));
        Map<Integer, List<Integer>> idsByCount = new HashMap<>();
        for (Map.Entry<Integer, Integer> countEntry : countById.entrySet()) {
            idsByCount.putIfAbsent(countEntry.getValue(), new ArrayList<>());
            idsByCount.get(countEntry.getValue()).add(countEntry.getKey());
        }
        AtomicInteger rank = new AtomicInteger(1);
        AtomicLong part2 = new AtomicLong(0L);
        idsByCount.entrySet().stream()
                .sorted((a, b) -> b.getKey().compareTo(a.getKey()))
                .forEach(e -> {
                    List<Integer> idsWithCount = e.getValue();
                    Map<Integer, Integer> totalTimeForCountById = idsWithCount.stream().collect(Collectors.toMap(id -> id, totalTimeById::get));
                    List<Integer> times = new ArrayList<>(totalTimeForCountById.values());
                    times.sort(Integer::compareTo);
                    for (int i = 0; i < times.size(); i++) {
                        part2.addAndGet((long)rank.get() * (long)times.get(i));
                        rank.addAndGet(1);
                    }
                });
        return part2.get();
    }

    static long part3(List<Result> results) {
        return 0L;
    }

    @Data
    static class Result {
        final int id;
        final int quest;
        final int part;
        final int time;
        Result(String s) {
            String[] parts = s.split(" ");
            id = getNum(parts[0]);
            quest = getNum(parts[1]);
            part = getNum(parts[2]);
            time = getNum(parts[3]);
        }

        private int getNum(String part) {
            return Integer.parseInt(part.split("=")[1]);
        }
    }
}
