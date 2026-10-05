package com.johnpickup.ebc2025;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

@RequiredArgsConstructor
public class Day1 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day1");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());


                List<String> names = Arrays.asList(lines.get(0).split(","));
                List<String> moves = Arrays.asList(lines.get(1).split(","));

                Day1 part1 = new Day1(names, moves.stream().map(Move::parse).collect(Collectors.toList()));

                System.out.println("Part 1: " +  part1.solvePart1());
                System.out.println("Part 2: " +  part1.solvePart2());
                System.out.println("Part 3: " +  part1.solvePart3());

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private String solvePart1() {
        int idx = 0;
        for (Move move : moves) {
            idx = idx + move.number * (move.direction==Direction.LEFT?-1:1);
            if (idx < 0) idx = 0;
            if (idx >= names.size()) idx = names.size()-1;
        }
        return names.get(idx);
    }

    private String solvePart2() {
        int idx = 0;
        for (Move move : moves) {
            idx = idx + move.number * (move.direction==Direction.LEFT?-1:1);
            if (idx < 0) idx = names.size() + idx;
            if (idx >= names.size()) idx = idx - names.size();
        }
        return names.get(idx);
    }

    private String solvePart3() {
        String[] copy = names.toArray(new String[0]);
        for (Move move : moves) {
            int idx = move.number * (move.direction==Direction.LEFT?-1:1);
            while (idx < 0) idx = names.size() + idx;
            while (idx >= names.size()) idx = idx - names.size();
            String n1 = copy[0];
            String n2 = copy[idx];
            copy[0] = n2;
            copy[idx] = n1;
        }
        return copy[0];
    }

    private final List<String> names;
    private final List<Move> moves;


    @RequiredArgsConstructor
    static class Move {
        private final Direction direction;
        private final int number;
        static Move parse(String s) {
            return new Move(s.charAt(0) == 'L' ? Direction.LEFT : Direction.RIGHT, Integer.parseInt(s.substring(1)));
        }
    }

    enum Direction {
        LEFT,
        RIGHT;
    }
}
