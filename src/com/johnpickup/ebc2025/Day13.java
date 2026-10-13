package com.johnpickup.ebc2025;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day13 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day13");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                Dial dial = new Dial(lines.stream().map(Range::new).toList());
                System.out.println("Part 1 : " + dial.rotate(2025L));            //346
                System.out.println("Part 2 : " + dial.rotate(20252025L));
                System.out.println("Part 3: " + dial.rotate(202520252025L));

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Dial {
        final Map<Integer, Range> ranges;
        final long numPositions;

        Dial(List<Range> inputRanges) {
            this.ranges = new HashMap<>(inputRanges.size()+1);
            this.ranges.put(0, new Range(1, 1));
            for (int i = 0; i < inputRanges.size(); i++) {
                if (i % 2 == 0)
                    ranges.put(i/2 + 1, inputRanges.get(i));
                else
                    ranges.put(inputRanges.size()-i/2, inputRanges.get(i).reverse());
            }
            numPositions = ranges.values().stream().map(Range::size).reduce(0L, Long::sum);
        }

        public long rotate(long turns) {
            long index = turns % numPositions;
            int ridx = 0;
            while (index >= ranges.get(ridx).size()) {
                index -= ranges.get(ridx).size();
                ridx++;
            }

            return ranges.get(ridx).get(index);
        }
    }

    @RequiredArgsConstructor
    static class Range {
        final long lower;
        final long upper;
        Range(String s) {
            String[] parts = s.split("-");
            if (parts.length==1) {
                lower = Long.parseLong(s);
                upper = lower;
            } else {
                lower = Long.parseLong(parts[0]);
                upper = Long.parseLong(parts[1]);
            }
        }

        long size() {
            return Math.abs(upper - lower) + 1;
        }

        public long get(long index) {
            if (lower<=upper) {
                return lower + index;
            } else {
                return lower - index;
            }
        }

        @Override
        public String toString() {
            return String.format("%d-%d", lower, upper);
        }

        public Range reverse() {
            return new Range(upper, lower);
        }
    }
}
