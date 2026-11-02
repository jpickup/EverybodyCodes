package com.johnpickup.ebc2026;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Quest3 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2026", "Quest3");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                List<Vote> votes = lines.stream().map(Vote::new).toList();
                System.out.printf("Result : %s%n", votes.stream().map(Vote::score).reduce(BigDecimal.ZERO, BigDecimal::add));
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }


    @RequiredArgsConstructor
    static class Vote {
        final boolean isIso2;
        final String standard;
        final String vote;
        Vote(String s) {
            String[] parts = s.split("-");
            isIso2 = parts[0].contains("2");
            String[] part = parts[1].split(" ");
            standard = part[0];
            vote = part[1];
        }

        public BigDecimal score() {
            BigDecimal result = BigDecimal.ZERO;
            String expandedStandard = standard;
            while (expandedStandard.length() < vote.length()) expandedStandard += standard;
            while (expandedStandard.length() > vote.length()) expandedStandard = expandedStandard.substring(1);
            for (int i = 0 ; i < vote.length(); i++) {
                int power = vote.length() - i - 1;
                int base = expandedStandard.charAt(i) - '0';
                int factor  = vote.charAt(i) - '0';
                BigDecimal digitScore = BigDecimal.valueOf(factor).multiply(BigDecimal.valueOf(base).pow(power));
                result = result.add(digitScore);
            }
            return isValidIso2Vote(result) ? result : BigDecimal.ZERO;
        }

        public boolean isValidIso2Vote(BigDecimal check) {
            if (!isIso2) return true;
            // is there another combination of powers that generates the same result but is smaller
            for (BigDecimal v = new BigDecimal(vote).subtract(BigDecimal.ONE); v.compareTo(BigDecimal.ZERO) > 0; v = v.subtract(BigDecimal.ONE)) {
                Vote smallerVote = new Vote(false, standard, v.toString());
                BigDecimal smallerVoteScore = smallerVote.score();
                if (smallerVoteScore.equals(check)) return false;
            }
            return true;
        }
    }
}
