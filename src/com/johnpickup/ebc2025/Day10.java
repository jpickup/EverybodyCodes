package com.johnpickup.ebc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.Lists;
import com.johnpickup.util.Sets;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day10 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day10");
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                ChessBoard board = new ChessBoard(lines);

                System.out.println("Part 1: " + board.part1());
                System.out.println("Part 2: " + board.part2());
                if (inputFilename.contains("3")) {
                    System.out.println("Part 3: " + board.part3());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class ChessBoard {
        final CharGrid grid;
        final Coord dragon;
        final Set<Coord> sheep;
        final Set<Coord> hides;
        ChessBoard(List<String> lines) {
            grid = new CharGrid(lines);
            dragon = grid.findCells('D').stream().findFirst().orElseThrow();
            sheep = grid.findCells('S');
            hides = grid.findCells('#');
        }

        @Override
        public String toString() {
            return grid.toString();
        }

        public int part1() {
            Set<Coord> dragons = new HashSet<>();
            dragons.add(dragon);
            int moveCount = isTest ? 3 : 4;
            for (int i = 0; i < moveCount; i++) {
                dragons.addAll(findNextDragons(dragons));
            }
            return Sets.intersection(dragons, sheep).size();
        }

        private Set<Coord> findNextDragons(Set<Coord> dragons) {
            Set<Coord> additional = new HashSet<>();
            for (Coord d : dragons) {
                additional.addAll(findPossibleDragonCoords(d, grid.getWidth(), grid.getHeight()));
            }
            return additional;
        }

        public int part2() {
            int rounds = isTest ? 3 : 20;
            int eaten = 0;
            Set<Coord> dragons = new HashSet<>();
            dragons.add(dragon);
            Set<Coord> movedSheep = new HashSet<>(sheep);

            for (int round = 1; round <= rounds; round++) {
                Set<Coord> nextDragons = findNextDragons(dragons);
                Set<Coord> eatenSheep = Sets.intersection(nextDragons, movedSheep);
                eatenSheep.removeAll(hides);
                eaten += eatenSheep.size();
                movedSheep.removeAll(eatenSheep);
                movedSheep = moveDown(movedSheep);
                eatenSheep = Sets.intersection(nextDragons, movedSheep);
                eatenSheep.removeAll(hides);
                eaten += eatenSheep.size();
                movedSheep.removeAll(eatenSheep);
                dragons = nextDragons;
            }
            return eaten;
        }

        public long part3() {
            BoardState initialState = new BoardState(grid.getWidth(), grid.getHeight(), dragon, sheep, hides, 0, sheep.size(), true, Collections.emptyList());

            Set<BoardState> allPossibleStates = generateStates(initialState);

            return allPossibleStates.stream().filter(BoardState::isSuccess).count();
        }

        private Set<BoardState> generateStates(BoardState state) {
            Set<BoardState> nextStates = state.possibleNextStates();
            if (nextStates.isEmpty()) return Collections.singleton(state);

            Set<BoardState> result = new HashSet<>();
            for (BoardState nextState : nextStates) {
                result.addAll(generateStates(nextState));
            }
            return result;
        }

        private Set<Coord> moveDown(Set<Coord> movedSheep) {
            Set<Coord> result = new HashSet<>();
            for (Coord c : movedSheep) {
                if (c.getY()+1 < grid.getHeight()) {
                    result.add(c.moveBy(0,1));
                }
            }
            return result;
        }
    }

    static Set<Coord> findPossibleDragonCoords(Coord c, int width, int height) {
        Set<Coord> result = new HashSet<>();
        result.add(c.moveBy(-2, -1));
        result.add(c.moveBy(-2, 1));
        result.add(c.moveBy(2, -1));
        result.add(c.moveBy(2, 1));
        result.add(c.moveBy(-1, -2));
        result.add(c.moveBy(-1, 2));
        result.add(c.moveBy(1, -2));
        result.add(c.moveBy(1, 2));
        return result.stream().filter(coord -> coord.inBounds(width, height)).collect(Collectors.toSet());
    }

    @RequiredArgsConstructor
    @Builder
    @EqualsAndHashCode
    static class BoardState {
        final int width;
        final int height;
        final Coord dragon;
        final Set<Coord> sheep;
        final Set<Coord> hides;
        final int eatenCount;
        final int initialSheepCount;
        final boolean isSheepMove;
        final List<Move> moves;

        Set<BoardState> possibleNextStates() {
            if (isSheepMove)
                return possibleNextSheepStates();
            else
                return possibleNextDragonStates();
        }

        private Set<BoardState> possibleNextSheepStates() {
            Set<Set<Coord>> possibleSheepCoords = findPossibleSheepCoords(dragon, sheep, hides, width, height);
            if (possibleSheepCoords.isEmpty()) {
                return Collections.singleton(boardStateWithSheep(sheep));       // no sheep move
            } else {
                return possibleSheepCoords.stream()
                        .map(this::boardStateWithSheep)
                        .collect(Collectors.toSet());
            }
        }

        private BoardState boardStateWithSheep(Set<Coord> newSheep) {
            return BoardState.builder()
                    .width(width)
                    .height(height)
                    .dragon(dragon)
                    .sheep(newSheep)
                    .hides(hides)
                    .eatenCount(eatenCount)
                    .initialSheepCount(initialSheepCount)
                    .isSheepMove(false)
                    .build();
        }

        private Set<Set<Coord>> findPossibleSheepCoords(Coord dragon, Set<Coord> sheep, Set<Coord> hides, int width, int height) {
            Set<Set<Coord>> result = new HashSet<>();
            for (Coord s : sheep) {
                Set<Coord> withoutThisSheep = new HashSet<>(sheep);
                withoutThisSheep.remove(s);
                Set<Coord> nextSheepCoords = possibleSheepMoves(s, withoutThisSheep, hides, dragon, width, height);
                for (Coord nextSheepCoord : nextSheepCoords) {
                    Set<Coord> nextSheepSet = new HashSet<>(withoutThisSheep);
                    if (nextSheepCoord.inBounds(width, height)) nextSheepSet.add(nextSheepCoord);       // don't include if off the bottom
                    result.add(nextSheepSet);
                }
            }
            return result;
        }

        private Set<Coord> possibleSheepMoves(Coord s, Set<Coord> otherSheep, Set<Coord> hides, Coord dragon, int width, int height) {
            List<Coord> allPossible = List.of(s.north(), s.south(), s.east(), s.west());
            List<Integer> otherColumns = otherSheep.stream().map(Coord::getX).toList();
            return allPossible.stream()
                    .filter(c -> c.inBounds(width, height) || c.getY() >= height)  // can drop off bottom
                    .filter(c -> !otherSheep.contains(c))
                    .filter(c -> !c.equals(dragon) || hides.contains(c))
                    .filter(c -> !otherColumns.contains(c.getX()))                 // column not occupied
                    .collect(Collectors.toSet());
        }

        private Set<BoardState> possibleNextDragonStates() {
            Set<Coord> possibleDragonCoords = findPossibleDragonCoords(dragon, width, height);
            return possibleDragonCoords.stream()
                    .map(this::boardStateWithDragonMove)
                    .filter(BoardState::isValidState)
                    .collect(Collectors.toSet());
        }

        private boolean isValidState() {
            boolean invalid = sheep.isEmpty() && isSheepMove;
            return !invalid;
        }

        private BoardState boardStateWithDragonMove(Coord d) {
            if (sheep.contains(d) && !hides.contains(d)) {
                Set<Coord> newSheep = new HashSet<>(sheep);
                sheep.remove(d);
                return BoardState.builder()
                        .width(width)
                        .height(height)
                        .dragon(d)
                        .sheep(newSheep)
                        .hides(hides)
                        .eatenCount(eatenCount+1)
                        .initialSheepCount(initialSheepCount)
                        .isSheepMove(true)
                        .moves(Lists.with(moves, new Move(d, 'D')))
                        .build();
            } else {
                return BoardState.builder()
                        .width(width)
                        .height(height)
                        .dragon(d)
                        .sheep(sheep)
                        .hides(hides)
                        .eatenCount(eatenCount)
                        .initialSheepCount(initialSheepCount)
                        .isSheepMove(true)
                        .moves(Lists.with(moves, new Move(d, 'D')))
                        .build();
            }
        }

        boolean isSuccess() {
            return eatenCount == initialSheepCount;
        }
    }

    @RequiredArgsConstructor
    static class Move {
        final Coord coord;
        final char who;
    }
}
