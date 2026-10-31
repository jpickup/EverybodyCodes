package com.johnpickup.ebc2025;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day18 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day18");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            Plant.init();
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        //.filter(s -> !s.isEmpty())
                        .toList();

                List<Plant> plants = Plant.parse(lines);
                Plant root = Plant.findRoot();
                System.out.println("Part 1: " + root.totalEnergy());

                long part2 = 0;
                for (List<Boolean> test : Plant.tests) {
                    part2 += root.totalEnergyForTest(test);
                }
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }


    @EqualsAndHashCode
    @Getter
    @ToString
    static class Plant {
        static final Map<Integer, Plant> plants = new HashMap<>();
        final int id;
        final int thickness;
        final List<Integer> freeBranches;
        final Map<Integer, Integer> branches;
        static List<List<Boolean>> tests = new ArrayList<>();

        static Pattern plantPattern = Pattern.compile("Plant ([0-9]+) with thickness (-?[0-9]+):");
        static Pattern freeBranchPattern = Pattern.compile("- free branch with thickness (-?[0-9]+)");
        static Pattern branchPattern = Pattern.compile("- branch to Plant ([0-9]+) with thickness (-?[0-9]+)");

        static void init() {
            plants.clear();
            tests = new ArrayList<>();;
        }

        Plant(List<String> lines) {
            Matcher plantMatcher = plantPattern.matcher(lines.getFirst());
            if (!plantMatcher.matches()) throw new RuntimeException("Plant line doesn't parse: " + lines.getFirst());
            id = Integer.parseInt(plantMatcher.group(1));
            thickness = Integer.parseInt(plantMatcher.group(2));
            freeBranches = new ArrayList<>();
            branches = new HashMap<>();

            for (String line : lines) {
                Matcher freeMatcher = freeBranchPattern.matcher(line);
                if (freeMatcher.matches()) {
                    freeBranches.add(Integer.parseInt(freeMatcher.group(1)));
                }
                Matcher branchMatcher = branchPattern.matcher(line);
                if (branchMatcher.matches()) {
                    branches.put(Integer.parseInt(branchMatcher.group(1)), Integer.parseInt(branchMatcher.group(2)));
                }
            }
        }

        static List<Plant> parse(List<String> lines) {
            List<Plant> result = new ArrayList<>();
            List<String> groupLines = new ArrayList<>();
            for (String line : lines) {
                if (line.isBlank()) {
                    if (!groupLines.isEmpty()) {
                        result.add(createPlant(groupLines));
                        groupLines.clear();
                    }
                } else {
                    groupLines.add(line);
                }
            }
            if (groupLines.getFirst().startsWith("Plant")) {
                result.add(createPlant(groupLines));
                groupLines.clear();
            } else {
                tests = parseTests(groupLines);
            }
            return result;
        }

        private static List<List<Boolean>> parseTests(List<String> lines) {
            return lines.stream().map(Plant::parseTest).toList();
        }

        private static List<Boolean> parseTest(String line) {
            return Arrays.stream(line.split(" ")).map(Integer::parseInt).map(i -> i == 1).toList();
        }

        private static Plant createPlant(List<String> plantLines) {
            Plant plant = new Plant(plantLines);
            plants.put(plant.id, plant);
            return plant;
        }

        static Plant findRoot() {
            Set<Integer> plantIds = new HashSet<>(plants.keySet());
            for (Plant plant : plants.values()) {
                plantIds.removeAll(plant.branches.keySet());
            }
            if (plantIds.size() != 1) throw new RuntimeException("Can't find root");
            return plants.get(plantIds.stream().findFirst().orElseThrow());
        }

        static Plant getPlant(int id) {
            return plants.get(id);
        }

        public int totalEnergy() {
            return freeEnergy() + childEnergy();
        }

        private int childEnergy() {
            Integer children = branches.entrySet().stream()
                    .map(ent -> getPlant(ent.getKey()).totalEnergy() * ent.getValue())
                    .reduce(0, Integer::sum);
            return (children < thickness) ? 0 : children;
        }

        private int freeEnergy() {
            return (freeBranches.size()==1) ? 1 : 0;
        }


        public int totalEnergyForTest(List<Boolean> test) {
            return freeEnergyForTest(test) + childEnergyForTest(test);
        }

        private int childEnergyForTest(List<Boolean> test) {
            Integer children = branches.entrySet().stream()
                    .map(ent -> getPlant(ent.getKey()).totalEnergyForTest(test) * ent.getValue())
                    .reduce(0, Integer::sum);
            return (children < thickness) ? 0 : children;
        }

        private int freeEnergyForTest(List<Boolean> test) {
            if (freeBranches.size()==1)
                return test.get(id-1) ? 1 : 0;
            else
                return 0;
        }
    }
}
