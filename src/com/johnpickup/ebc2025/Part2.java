package com.johnpickup.ebc2025;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Part2 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Part2");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Complex A = Complex.parse(lines.get(0));
                Complex C10 = new Complex(10, 10);

                // Part 1
                Complex result = new Complex(0, 0);
                for (int i = 0; i < 3; i++) {
                    result = result.multiply(result);
                    result = result.divide(C10);
                    result = result.add(A);
                }
                System.out.println("Part 1: " + result);

                // Part 2
                Complex topLeft = Complex.parse(lines.get(0));
                int engraved = 0;
                for (int x = 0; x < 101; x++) {
                    //System.out.println();
                    for (int y = 0; y < 101; y++) {
                        Complex p = topLeft.add(new Complex(x*10, y*10));
                        Complex pointCheck = new Complex(0, 0);
                        for (int i = 0; i < 100; i++) {
                            pointCheck = pointCheck.multiply(pointCheck);
                            pointCheck = pointCheck.divide(new Complex(100000,100000));
                            pointCheck = pointCheck.add(p);
                            if (Math.abs(pointCheck.x) > 1000000 || Math.abs(pointCheck.y) > 1000000) break;
                        }
                        boolean eng = (Math.abs(pointCheck.x) <= 1000000 && Math.abs(pointCheck.y) <= 1000000);
                        if (eng) engraved++;
                        //System.out.print(eng?'*':'.');
                    }
                }
                System.out.println("Part 2: " + engraved);

                engraved = 0;
                for (int x = 0; x < 1001; x++) {
                    for (int y = 0; y < 1001; y++) {
                        Complex p = topLeft.add(new Complex(x, y));
                        Complex pointCheck = new Complex(0, 0);
                        for (int i = 0; i < 100; i++) {
                            pointCheck = pointCheck.multiply(pointCheck);
                            pointCheck = pointCheck.divide(new Complex(100000,100000));
                            pointCheck = pointCheck.add(p);
                            if (Math.abs(pointCheck.x) > 1000000 || Math.abs(pointCheck.y) > 1000000) break;
                        }
                        boolean eng = (Math.abs(pointCheck.x) <= 1000000 && Math.abs(pointCheck.y) <= 1000000);
                        if (eng) engraved++;
                    }
                }
                System.out.println("Part 3: " + engraved);


            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class Complex {
        final long x;
        final long y;
        static Complex parse(String s) {
            int start = s.indexOf('[');
            int end = s.indexOf(']');
            String[] parts = s.substring(start + 1, end).split(",");
            return new Complex(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }

        Complex add(Complex other) {
            return new Complex(this.x + other.x, this.y + other.y);
        }

        Complex multiply(Complex other) {
            //[X1,Y1] * [X2,Y2] = [X1 * X2 - Y1 * Y2, X1 * Y2 + Y1 * X2]
            return new Complex(this.x * other.x - this.y * other.y, this.x * other.y + this.y * other.x);
        }

        Complex divide(Complex other) {
            // [X1,Y1] / [X2,Y2] = [X1 / X2, Y1 / Y2]
            return new Complex(this.x / other.x, this.y / other.y);
        }

        @Override
        public String toString() {
            return "[" + x + "," + y + ']';
        }
    }
}
