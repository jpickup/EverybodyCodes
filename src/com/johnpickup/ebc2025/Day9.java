package com.johnpickup.ebc2025;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day9 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day9");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                List<DnaProfile> dnaProfiles = lines.stream().map(DnaProfile::new).toList();

                System.out.println("Part 1: " + part1(dnaProfiles));
                System.out.println("Part 2: " + part2(dnaProfiles));
                if (inputFilename.contains("3")) {
                    System.out.println("Part 3: " + part3(dnaProfiles));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static long part2(List<DnaProfile> dnaProfiles) {
        long result = 0;
        for (DnaProfile child : dnaProfiles) {
            for (int p1=0; p1 < dnaProfiles.size(); p1++) {
                for (int p2=p1+1; p2 < dnaProfiles.size(); p2++) {
                    DnaProfile parent1 = dnaProfiles.get(p1);
                    DnaProfile parent2 = dnaProfiles.get(p2);
                    if (child.isPossibleChild(Arrays.asList(parent1, parent2))) {
                        result += child.similarity(parent1) * child.similarity(parent2);
                    }
                }
            }
        }

        return result;
    }

    private static long part3(List<DnaProfile> dnaProfiles) {
        List<Family> families = new ArrayList<>();
        for (DnaProfile child : dnaProfiles) {
            for (int p1=0; p1 < dnaProfiles.size(); p1++) {
                for (int p2=p1+1; p2 < dnaProfiles.size(); p2++) {
                    DnaProfile parent1 = dnaProfiles.get(p1);
                    DnaProfile parent2 = dnaProfiles.get(p2);
                    if (child.isPossibleChild(Arrays.asList(parent1, parent2))) {
                        Family family = new Family();
                        family.add(child);
                        family.add(parent1);
                        family.add(parent2);
                        families.add(family);
                    }
                }
            }
        }

        boolean madeProgress;
        do {
            madeProgress = false;
            for (int f1 = 0; f1 < families.size(); f1++) {
                for (int f2 = f1 + 1; f2 < families.size(); f2++) {
                    Family family1 = families.get(f1);
                    Family family2 = families.get(f2);
                    if (family1.sharesMember(family2)) {
                        family1.merge(family2);
                        families.remove(family2);
                        madeProgress = true;
                    }
                }
            }
        } while (madeProgress);

        Family biggest = families.getFirst();

        for (Family family : families) {
            if (family.size() > biggest.size()) biggest = family;
        }

        return biggest.scaleSum();

    }

    static long part1(List<DnaProfile> dnaProfiles) {
        DnaProfile child = null;
        for (DnaProfile dnaProfile : dnaProfiles) {
            if (dnaProfile.isPossibleChild(dnaProfiles)) {
                child = dnaProfile;
            }
        }

        long result = 1L;
        for (DnaProfile dnaProfile : dnaProfiles) {
            if (dnaProfile != child) {
                result *= child.similarity(dnaProfile);
            }
        }
        return result;
    }

    static class Family {
        final Set<DnaProfile> members = new HashSet<>();
        boolean hasMember(DnaProfile p) {
            return members.contains(p);
        }

        void add(DnaProfile p) {
            members.add(p);
        }

        void remove(DnaProfile p) {
            members.remove(p);
        }

        int size() {
            return members.size();
        }

        public boolean sharesMember(Family other) {
            return members.stream().anyMatch(other::hasMember);
        }

        public void merge(Family other) {
            members.addAll(other.members);
        }

        public int scaleSum() {
            return members.stream().map(DnaProfile::getId).reduce(0, Integer::sum);
        }
    }


    @EqualsAndHashCode(exclude = {"symbols"})
    static class DnaProfile {
        @Getter
        final int id;
        final char[] symbols;
        DnaProfile(String input) {
            String[] parts = input.split(":");
            id = Integer.parseInt(parts[0]);
            symbols = parts[1].toCharArray();
        }

        public boolean isPossibleChild(List<DnaProfile> dnaProfiles) {
            boolean result = true;
            for (int i = 0 ; i < symbols.length; i++) {
                boolean isPossibleChild = false;
                for (DnaProfile dnaProfile : dnaProfiles) {
                    if (this == dnaProfile) continue;
                    isPossibleChild |= (this.symbols[i] == dnaProfile.symbols[i]);
                }
                result &= isPossibleChild;
            }
            return result;
        }

        public long similarity(DnaProfile dnaProfile) {
            long result = 0;
            for (int i = 0 ; i < symbols.length; i++) {
                if (this.symbols[i] == dnaProfile.symbols[i]) result ++;
            }
            return result;
        }
    }
}
