package com.johnpickup.ebc2025;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day7 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day7");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Day7 day7 = new Day7(lines);

                System.out.printf("Part1: %s%n", day7.part1());
                System.out.printf("Part2: %s%n", day7.part2());
                if (inputFilename.contains("3")) {
                    System.out.printf("Part3: %s%n", day7.part3());
                    System.out.printf("Part3v2: %s%n", day7.part3v2());     // brute force
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    public String part1() {
        for (String name : names) {
            if (isValid(name)) {
                return name;
            }
        }
        throw new RuntimeException("No match");
    }

    public int part2() {
        int result = 0;
        for (String name : validNames()) {
            result += names.indexOf(name)+1;
        }
        return result;
    }

    public long part3() {
        long result = 0;
        Set<String> validNames = uniquePrefixes(validNames());
        for (String name : validNames) {
            result += howManyCanBeValid(name);
        }
        return result;
    }

    private Set<String> uniquePrefixes(List<String> strings) {
        return strings.stream().filter(s -> isNotASuffix(s, strings)).collect(Collectors.toSet());
    }

    private boolean isNotASuffix(String s, List<String> strings) {
        return strings.stream().noneMatch(str -> s.startsWith(str) && !s.equals(str));
    }

    public long part3v2() {
        Set<String> result = new HashSet<>();
        List<String> validNames = validNames();
        for (String name : validNames) {
            result.addAll(allValid(name));
        }
        return result.size();
    }

    private Set<String> allValid(String name) {
        if (name.length() >= 11) return Collections.singleton(name);
        Set<String> result = new HashSet<>();
        boolean correctLength = name.length() >= 7;
        if (correctLength) result.add(name);
        Set<Character> possibleNext = new HashSet<>();
        for (Rule rule : rules) {
            if (rule.target == name.charAt(name.length()-1)) {
                possibleNext.addAll(rule.after);
            }
        }
        for (Character c : possibleNext) {
            result.addAll(allValid(name + c));
        }

        return result;
    }

    private long howManyCanBeValid(String name) {
        if (name.length() >= 11) return 1;
        boolean correctLength = name.length() >= 7;
        long result = (correctLength ? 1 : 0);
        Set<Character> possibleNext = new HashSet<>();
        for (Rule rule : rules) {
            if (rule.target == name.charAt(name.length()-1)) {
                possibleNext.addAll(rule.after);
            }
        }
        for (Character c : possibleNext) {
            result += howManyCanBeValid(name + c);
        }

        return result;
    }

    public List<String> validNames() {
        List<String> result = new ArrayList<>();
        for (String name : names) {
            if (isValid(name)) {
                result.add(name);
            }
        }
        return result;
    }

    private boolean isValid(String name) {
        boolean result = true;
        for (int i = 0 ; i < name.length()-1; i++) {
            char c = name.charAt(i);
            char next = name.charAt(i+1);
            result &= ruleMatches(c, next);
        }
        return result;
    }

    private boolean ruleMatches(char c, char next) {
        for (Rule rule : rules) {
            if (rule.matches(c, next)) {
                return true;
            }
        }
        return false;
    }

    private final List<String> names;
    private final List<Rule> rules;
    Day7(List<String> lines) {
        names = Arrays.stream(lines.getFirst().split(",")).toList();
        rules = lines.stream().skip(1).map(Rule::new).toList();
    }

    static class Rule {
        final char target;
        final List<Character> after;
        Rule(String definition) {
            target = definition.charAt(0);
            after = Arrays.stream(definition.substring(4).split(",")).map(s -> s.charAt(0)).toList();
        }

        public boolean matches(char c, char next) {
            return target==c && after.contains(next);
        }
    }
}
