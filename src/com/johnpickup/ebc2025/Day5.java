package com.johnpickup.ebc2025;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day5 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day5");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                List<Sword> swords = new ArrayList<>(lines.stream().map(Sword::parseLine).toList());
                System.out.println("Part1: " + swords.getFirst().spine());

                Optional<Long> min = swords.stream().map(Sword::spine).min(Long::compareTo);
                Optional<Long> max = swords.stream().map(Sword::spine).max(Long::compareTo);
                System.out.println("Part2: " + (max.orElseThrow() - min.orElseThrow()));

                swords.sort(Comparator.reverseOrder());
                //swords.forEach(System.out::println);
                BigDecimal checksum = BigDecimal.ZERO;
                int idx = 1;
                for (Sword sword : swords) {
                    checksum = checksum.add(BigDecimal.valueOf(sword.id).multiply(BigDecimal.valueOf(idx)));
                    idx++;
                }
                System.out.println("Part3: " + checksum);


            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class Sword implements Comparable<Sword> {
        final int id;
        List<Segment> segments = new ArrayList<>();

        public void addDigit(Integer i) {
            Segment takes = null;

            for (Segment segment : segments) {
                if (segment.accommodates(i)) {
                    takes = segment;
                    break;
                }
            }

            if (takes == null) {
                segments.add(new Segment(i));
            } else {
                takes.take(i);
            }
            //System.out.printf("Added %d to give:%n%s%n%n", i, this);
        }

        public long spine() {
            StringBuilder result = new StringBuilder();
            segments.forEach(s -> result.append(s.middle));
            return Long.parseLong(result.toString());
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append("*** " + id + " ***\n");
            segments.forEach(result::append);
            return result.toString();
        }

        public static Sword parseLine(String line) {
            String[] parts = line.split(":");
            int swordId = Integer.parseInt(parts[0]);
            List<Integer> inputs = Arrays.stream(parts[1].split(",")).map(Integer::parseInt).toList();
            Sword sword = new Sword(swordId);
            inputs.forEach(sword::addDigit);
            return sword;
        }

        @Override
        public int compareTo(Sword other) {
            if (this.spine() != other.spine()) {
                return Long.compare(this.spine(), other.spine());
            }
            int result = 0;
            int idx = 0;
            while (result == 0) {
                Segment left = this.segments.size()>idx ? this.segments.get(idx) : null;
                Segment right = other.segments.size()>idx ? other.segments.get(idx) : null;
                if (left==null && right==null) break;
                result = Integer.compare(left==null?0:left.value(), right==null?0:right.value());
                idx++;
            }
            if (result != 0) return result;
            return Integer.compare(this.id, other.id);
        }
    }

    @RequiredArgsConstructor
    static class Segment implements Comparable<Segment> {
        Integer left;
        final Integer middle;
        Integer right;

        public boolean accommodates(Integer i) {
            return (left == null && i < middle) || (right == null && i > middle);
        }

        public void take(Integer i) {
            if (left == null && i < middle) left = i;
            else if (right == null && i > middle) right = i;
            else throw new RuntimeException("This should be impossible!");
        }

        @Override
        public String toString() {
            return String.format("%2s-%2s-%2s%n", left==null?"  ":left, middle, right==null?"  ":right);
        }

        public int value() {
            return Integer.parseInt(String.format("%s%s%s", left==null?"":left, middle, right==null?"":right));
        }

        @Override
        public int compareTo(Segment o) {
            return this.value() - o.value();
        }
    }
}
