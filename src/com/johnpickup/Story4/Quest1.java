package com.johnpickup.Story4;

import com.johnpickup.util.Range;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Quest1 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("Story4", "Quest1");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();
                List<Sequence> sequences = lines.stream().map(Sequence::new).toList();
                System.out.printf("Part1: %d%n", sequences.stream().map(Sequence::part1).reduce(0, Integer::sum));
                System.out.printf("Part2: %d%n", sequences.stream().map(Sequence::part2).reduce(0, Integer::sum));
                if (inputFilename.contains("3")) System.out.printf("Part3: %d%n", sequences.stream().map(Sequence::part3).reduce(0, Integer::sum));

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Sequence {
        final List<Integer> values;
        Sequence(String s) {
            values = Arrays.stream(s.split(",")).map(Integer::parseInt).toList();
        }

        public int part1() {
            int pos = 0;
            Set<Integer> visited = new HashSet<>();
            for (Integer value : values) {
                int backwards = pos - value;
                int forwards = pos + value;
                if (backwards > 0 && !visited.contains(backwards) ) {
                    pos = backwards;
                } else {
                    pos = forwards;
                }
                visited.add(pos);
            }
            return pos;
        }

        public int part2() {
            int pos = 0;
            Set<Integer> visited = new HashSet<>();
            for (Integer value : values) {
                int backwards = pos - value;
                int forwards = pos + value;
                if (backwards > 0 && !visited.contains(backwards) ) {
                    pos = backwards;
                } else {
                    pos = forwards;
                    while (visited.contains(pos)) pos++;
                }
                visited.add(pos);
            }
            return pos;
        }

        public int part3() {
            int pos = 0;
            Set<Integer> visited = new HashSet<>();
            Set<Range<Integer>> aboveArcs = new HashSet<>();
            Set<Range<Integer>> belowArcs = new HashSet<>();
            int turn = 0;
            for (Integer value : values) {
                Set<Range<Integer>> arcsToUse = (turn % 2 == 0) ? aboveArcs : belowArcs;
                int prevPos = pos;
                int backwards = pos - value;
                int forwards = pos + value;
                int furthestPossible = visited.stream().max(Integer::compareTo).orElse(0) + value;
                Range<Integer> backwardsArc = new Range<>(prevPos, backwards);
                if (backwards > 0 && !visited.contains(backwards) && !arcsCross(backwardsArc, arcsToUse)) {
                    pos = backwards;
                } else {
                    int nextPos = forwards;
                    while ((visited.contains(nextPos) || arcsCross(new Range<>(prevPos, nextPos), arcsToUse)) && nextPos < furthestPossible) nextPos++;
                    if (nextPos <= furthestPossible && !arcsCross(new Range<>(prevPos, nextPos), arcsToUse)) pos = nextPos;
                }
                if (pos != prevPos) {
                    visited.add(pos);
                    arcsToUse.add(new Range<>(prevPos, pos));
                    //System.out.printf("%d,",pos);
                    turn++;
                }
            }
            //System.out.println(" - " + pos);
            return pos;
        }

        private boolean arcsCross(Range<Integer> arc, Set<Range<Integer>> arcs) {
//            Optional<Range<Integer>> cross = arcs.stream().filter(a -> crosses(arc, a)).findFirst();
//            cross.ifPresent(integerRange -> System.out.printf("%s crosses %s%n", arc, integerRange));
//            return cross.isPresent();
            return arcs.stream().anyMatch(a -> crosses(arc, a));
        }

        private boolean crosses(Range<Integer> arc1 ,Range<Integer> arc2) {
            if (arc1.getLower().equals(arc2.getLower())) return false;
            if (arc1.getUpper().equals(arc2.getUpper())) return false;
            if (arc1.isWithin(arc2)) return false;
            if (arc2.isWithin(arc1)) return false;
            return arc1.overlaps(arc2);
        }
    }
}
