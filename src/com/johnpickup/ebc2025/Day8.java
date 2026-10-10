package com.johnpickup.ebc2025;

import com.johnpickup.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day8 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day8");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                List<Integer> steps = Arrays.stream(lines.getFirst().split(",")).map(Integer::parseInt).toList();

                System.out.println("Part 1: " + part1(steps));
                System.out.println("Part 2: " + part2(steps));
                System.out.println("Part 3: " + part3());


            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static List<Pair<Integer, Integer>> arcs = new ArrayList<>();

    private static int part3() {
        int nails = isTest ? 8 : 256;
        int result = 0;

        for (int i = 1; i <= nails; i++) {
            for (int j = i+1; j <= nails; j++) {
                Pair<Integer, Integer> newArc = createArc(i, j);
                int newMax = countCrosses(newArc, arcs);
                if (arcs.contains(newArc)) newMax++;
                if (newMax > result) result = newMax;
            }
        }
        return result;
    }

    private static int part2(List<Integer> steps) {
        arcs.clear();
        int result=0;
        int prev = Integer.MIN_VALUE;
        for (Integer step : steps) {
            if (prev>=0) {
                Pair<Integer, Integer> newArc = createArc(prev, step);
                result += countCrosses(newArc, arcs);
                arcs.add(newArc);
            }
            prev = step;
        }
        return result;
    }

    private static int countCrosses(Pair<Integer, Integer> arc, List<Pair<Integer, Integer>> arcs) {
        int result = 0;
        for (Pair<Integer, Integer> existing : arcs) {
            if (crosses(arc, existing)) result ++;
        }
        return result;
    }

    private static boolean crosses(Pair<Integer, Integer> arc1, Pair<Integer, Integer> arc2) {
        int arc1Min = Math.min(arc1.getValue1(), arc1.getValue2());
        int arc2Min = Math.min(arc2.getValue1(), arc2.getValue2());
        int arc1Max = Math.max(arc1.getValue1(), arc1.getValue2());
        int arc2Max = Math.max(arc2.getValue1(), arc2.getValue2());
        boolean option1 = (arc1Min > arc2Min) && (arc1Min < arc2Max) && (arc1Max > arc2Max);
        boolean option2 = (arc1Min < arc2Min) && (arc1Max > arc2Min) && (arc1Max < arc2Max);
        return option1 || option2;
    }

    private static int part1(List<Integer> steps) {
        int prev = Integer.MIN_VALUE;
        int result=0;
        int part1TargetDiff = isTest ? 4 : 16;
        for (Integer step : steps) {
            int diff = Math.abs(step - prev);
            if (diff == part1TargetDiff) result++;
            prev = step;
        }
        return result;
    }

    private static Pair<Integer, Integer> createArc(int from, int to) {
        return new Pair<>(Math.min(from, to), Math.max(from, to));
    }
}
