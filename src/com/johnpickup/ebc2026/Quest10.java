package com.johnpickup.ebc2026;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.Direction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Quest10 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2026", "Quest10");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                System.out.printf("Part1: %d %n", new Cave(lines).part1());
                System.out.printf("Part2: %d %n", new Cave(lines).part2(10000));
                if (inputFilename.contains("3")) System.out.printf("Part3: %d %n", new Cave(lines).part3(1000000000000L));

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class Cave {
        final CharGrid grid;
        final List<Ghost> ghosts = new ArrayList<>();
        Cave(List<String> lines) {
            grid = new CharGrid(lines);
            ghosts.addAll(grid.findCells('>').stream().map(c -> new Ghost(c, Direction.EAST, 0)).toList());
            ghosts.addAll(grid.findCells('<').stream().map(c -> new Ghost(c, Direction.WEST, 0)).toList());
            ghosts.addAll(grid.findCells('^').stream().map(c -> new Ghost(c, Direction.NORTH, 0)).toList());
            ghosts.addAll(grid.findCells('v').stream().map(c -> new Ghost(c, Direction.SOUTH, 0)).toList());
            // clear the space of ghosts
            ghosts.forEach(g -> grid.setCell(g.location, '.'));
        }

        @Override
        public String toString() {
            CharGrid outputGrid = new CharGrid(grid);
            ghosts.forEach(g -> outputGrid.setCell(g.location, g.direction.toString().charAt(0)));
            Map<Coord, List<Ghost>> byLocation = ghosts.stream().collect(Collectors.groupingBy(Ghost::getLocation));
            byLocation.entrySet().stream()
                    .filter(e -> e.getValue().size()>1)
                    .forEach(e -> outputGrid.setCell(e.getKey(), Integer.toString(e.getValue().size()).charAt(0)));
            return outputGrid.toString();
        }

        public long part1() {
            for (int i = 0 ; i < 10; i++) {
                executeRound(Long.MAX_VALUE);
            }
            return collisions();
        }

        public long part2(long maxCollisions) {
            long roundNum = 0;
            while (!ghosts.isEmpty()) {
                roundNum++;
                executeRound(maxCollisions);
            }
            return roundNum;
        }

        public long part3(long maxCollisions) {
            long roundNum = 0;
            Map<List<GhostKey>, Long> states = new HashMap<>();
            Map<List<GhostKey>, List<Ghost>> prevState = new HashMap<>();
            while (!ghosts.isEmpty()) {
                List<GhostKey> stateKey = stateKey(ghosts);
                if (states.containsKey(stateKey)) {
                    roundNum += jumpForward(ghosts, prevState.get(stateKey), roundNum, states.get(stateKey), maxCollisions);
                }
                states.put(stateKey, roundNum);
                prevState.put(stateKey, ghosts.stream().map(Ghost::copy).toList());
                roundNum++;
                executeRound(maxCollisions);
            }
            return roundNum;
        }

        private List<GhostKey> stateKey(List<Ghost> ghosts) {
            return ghosts.stream().map(GhostKey::new).toList();
        }

        private long jumpForward(List<Ghost> currentGhosts, List<Ghost> previousGhosts, long currentRound, long prevRound, long maxCollisions) {
            //System.out.printf("State seen before at %d, curr=%d, diff=%d%n", prevRound, currentRound, currentRound-prevRound);
            // ghost order is always the same, no need to search in one list for the other, sizes must match too
            long minCyclesLeft = Long.MAX_VALUE;
            for (int i = 0; i < currentGhosts.size(); i++) {
                long collisionDelta = currentGhosts.get(i).collisions - previousGhosts.get(i).collisions;
                long cyclesLeft = collisionDelta==0 ? Long.MAX_VALUE : (maxCollisions - currentGhosts.get(i).collisions) / collisionDelta;
                if (cyclesLeft < minCyclesLeft) minCyclesLeft = cyclesLeft;
            }
            //System.out.printf("Can proceed for another %d cycles%n", minCyclesLeft);
            if (minCyclesLeft == Long.MAX_VALUE || minCyclesLeft <= 1) {
                return 0L;
            } else {
                minCyclesLeft--;        // one less
                // Update all ghosts
                for (int i = 0; i < currentGhosts.size(); i++) {
                    long collisionDelta = currentGhosts.get(i).collisions - previousGhosts.get(i).collisions;
                    currentGhosts.get(i).collisions += collisionDelta * minCyclesLeft;
                }
                removeExpired(maxCollisions);
                // return number of cycles skipped
                return minCyclesLeft * (currentRound-prevRound);
            }
        }

        long collisions() {
            return ghosts.stream().map(Ghost::getCollisions).reduce(0L, Long::sum);
        }

        void executeRound(long maxCollisions) {
            for (Ghost ghost : ghosts) {
                ghost.move(grid);
            }
            removeExpired(maxCollisions);

            Map<Coord, List<Ghost>> byLocation = ghosts.stream().collect(Collectors.groupingBy(Ghost::getLocation));
            byLocation.values().stream()
                    .filter(gs -> gs.size()>1)
                    .forEach(gs -> gs.forEach(Ghost::flip));
            removeExpired(maxCollisions);
        }

        void removeExpired(long maxCollisions) {
            List<Ghost> expired = ghosts.stream().filter(g -> g.collisions >= maxCollisions).toList();
            ghosts.removeAll(expired);
        }
    }

    @Getter
    @AllArgsConstructor
    static class Ghost {
        Coord location;
        Direction direction;
        long collisions = 0;

        public void move(CharGrid grid) {
            Coord newLocation = direction.apply(location);
            if (grid.getCell(newLocation) == '#') {
                direction = direction.right();
                collisions++;
            } else {
                location = newLocation;
            }
        }

        public void flip() {
            direction = direction.opposite();
            collisions++;
        }

        Ghost copy() {
            return new Ghost(location, direction, collisions);
        }
    }

    @EqualsAndHashCode
    static class GhostKey {
        final Coord location;
        final Direction direction;
        GhostKey(Ghost ghost) {
            location = ghost.location;
            direction = ghost.direction;
        }
    }

}
