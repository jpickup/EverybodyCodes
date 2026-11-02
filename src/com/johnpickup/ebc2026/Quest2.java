package com.johnpickup.ebc2026;

import lombok.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Quest2 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2026", "Quest2");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                List<Alloys> alloys = lines.stream().map(Alloys::new).toList();
                System.out.printf("Part 1    : %d%n", alloys.stream().map(Alloys::part1).reduce(0L, Long::sum));
                System.out.printf("Part 2 & 3: %d%n", alloys.stream().map(Alloys::part2).reduce(0L, Long::sum));
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @ToString
    static class Alloys {
        final Set<Pair> pairs;
        Alloys(String s) {
            pairs = new HashSet<>(Arrays.stream(s.split(" ")).map(Pair::new).toList());
        }

        public long part1() {
            Set<Integer> alloyIds = new HashSet<>();
            pairs.forEach(p -> alloyIds.add(p.left));
            pairs.forEach(p -> alloyIds.add(p.right));
            List<Integer> sortedIds = new ArrayList<>();
            for (Integer alloyId : alloyIds) {
                insertInOrder(alloyId, sortedIds);
            }
            String result = sortedIds.stream().map(id -> Integer.toString(id)).reduce("", (a, b) -> a + b);
            return Long.parseLong(result);
        }

        public long part2() {
            makeMorePairs();
            Set<Integer> alloyIds = new HashSet<>();
            pairs.forEach(p -> alloyIds.add(p.left));
            pairs.forEach(p -> alloyIds.add(p.right));
            List<Integer> sortedIds = new ArrayList<>();
            for (Integer alloyId : alloyIds) {
                insertInOrder(alloyId, sortedIds);
            }
            String result = sortedIds.stream().map(id -> Integer.toString(id)).reduce("", (a, b) -> a + b);
            return Long.parseLong(result);
        }

        private void makeMorePairs() {
            Set<Pair> newPairs = new HashSet<>();
            boolean added = false;
            do {
                newPairs.clear();
                for (Pair pair1 : pairs) {
                    for (Pair pair2 : pairs) {
                        if (!pair1.equals(pair2)) {
                            Optional.ofNullable(pair1.join(pair2)).ifPresent(newPairs::add);
                        }
                    }
                }
                added = pairs.addAll(newPairs);
            } while (added);
        }

        private void insertInOrder(int alloyId, List<Integer> sortedIds) {
            int harderIndex = 0;
            while (harderIndex < sortedIds.size()) {
                if (isHarder(alloyId, sortedIds.get(harderIndex++))) {
                    harderIndex--;
                    break;
                }
            }
            sortedIds.add(harderIndex, alloyId);
        }

        private boolean isHarder(int alloy1, int alloy2) {
            boolean result = false;
            for (Pair pair : pairs) {
                if (pair.canCompare(alloy1, alloy2)) {
                    result |= pair.isHarder(alloy1, alloy2);
                }
            }
            return result;
        }
    }

    @ToString
    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class Pair {
        final int left;
        final int right;
        final boolean leftSofter;
        Pair(String s) {
            leftSofter = s.contains("<");
            left = Integer.parseInt(s.substring(0,1));
            right = Integer.parseInt(s.substring(2,3));
        }

        boolean canCompare(int i1, int i2) {
            return (left == i1 && right == i2) || (left == i2 && right == i1);
        }

        boolean isHarder(int i1, int i2) {
            if (left == i1 && right == i2) return !leftSofter;
            if (left == i2 && right == i1) return leftSofter;
            throw new RuntimeException("Should not get here!");
        }

        int softer() {
            return leftSofter ? left : right;
        }

        int harder() {
            return !leftSofter ? left : right;
        }

        boolean hasHarder(int i) {
            return harder() == i;
        }

        boolean hasSofter(int i) {
            return softer() == i;
        }

        public Pair join(Pair other) {
            if (leftSofter && other.hasHarder(left)) {
                return new Pair(other.softer(), harder(), true);
            }
            if (leftSofter && other.hasSofter(right)) {
                return new Pair(softer(), other.harder(), true);
            }

            if (!leftSofter && other.hasHarder(right)) {
                return new Pair(other.softer(), harder(), true);
            }
            if (!leftSofter && other.hasSofter(left)) {
                return new Pair(softer(), other.harder(), true);
            }

            return null;
        }

    }
}
