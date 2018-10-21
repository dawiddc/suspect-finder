package com.dawiddc.problemforeseer;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final int ITERATION_COUNT = 10;
    private static Integer dayCount;
    private static Integer hotelCount;
    private static Integer peopleCount;
    private static double nightSpentAtHotelProbability;
    private static Map<List<Integer>, Integer> pairMeetCount = new HashMap<>();

    private static long suspectedEventsFinalCount = 0;
    private static long suspectedPairsFinalCount = 0;
    private static Map<Integer, Long> histogramFinalValueMap = new TreeMap<>();
    private static long suspectedPeopleFinalCount = 0;
    private static long startTime = 0;

    public static void main(String[] args) {
        checkAndAssignArgs(args);
        startTime = System.nanoTime();
        for (int i = 0; i < ITERATION_COUNT; i++) {
            pairMeetCount = new HashMap<>();
            System.out.println("\n******************");
            System.out.println("ITERATION " + i + 1);
            findSuspectedPairs();
            storeAndPrintPairMeetCount();
        }
        printFinalStats();
        exit();
    }

    private static void checkAndAssignArgs(String[] args) {
        if (args.length < 4) {
            System.out.println("Not enough args!");
            exit();
        }
        peopleCount = Integer.valueOf(args[0]);
        nightSpentAtHotelProbability = Double.parseDouble(args[1]);
        hotelCount = Integer.valueOf(args[2]);
        dayCount = Integer.valueOf(args[3]);
    }

    private static void findSuspectedPairs() {
        List<Person> people = createAndFillPeopleList(peopleCount);
        List<List<Person>> dayList = createAndFillDayList(people);
        Random rand = new Random();

        for (List<Person> day : dayList) {
            day.forEach(person -> person.setHotelId(rand.nextInt(hotelCount)));
            List<List<Person>> peopleAtTheSameHotelList = createAndFillPeopleAtTheSameHotelList(day);
            for (List<Person> peopleAtTheSameHotel : peopleAtTheSameHotelList) {
                List<List<Integer>> pairs = calculatePairs(peopleAtTheSameHotel);
                insertPairsToPairMap(pairs);
            }
        }
    }

    private static List<Person> createAndFillPeopleList(Integer peopleCount) {
        List<Person> people = new ArrayList<>(peopleCount);

        for (int i = 1; i <= peopleCount; i++) {
            Person person = Person.builder().personId(i).build();
            people.add(person);
        }
        return people;
    }

    private static List<List<Person>> createAndFillDayList(List<Person> people) {
        List<List<Person>> dayList = new ArrayList<>(dayCount);

        for (int i = 0; i < dayCount; i++) {
            List<Person> dayWithPeopleGoingToHotel = people.stream()
                    .filter(person -> Math.random() < nightSpentAtHotelProbability)
                    .collect(Collectors.toList());
            dayList.add(dayWithPeopleGoingToHotel);
        }
        return dayList;
    }

    private static List<List<Person>> createAndFillPeopleAtTheSameHotelList(List<Person> day) {
        List<List<Person>> peopleAtTheSameHotelList = new ArrayList<>();
        for (int i = 0; i < hotelCount; i++) {
            final int hotelId = i;
            List<Person> peopleAtTheSameHotel = day.stream().filter(p -> p.getHotelId() == hotelId).collect(Collectors.toList());
            if (peopleAtTheSameHotel.size() > 1) {
                peopleAtTheSameHotelList.add(peopleAtTheSameHotel);
            }
        }
        return peopleAtTheSameHotelList;
    }

    private static List<List<Integer>> calculatePairs(List<Person> peopleAtTheSameHotel) {
        List<List<Integer>> pairs = new ArrayList<>();

        for (int i = 0; i < peopleAtTheSameHotel.size() - 1; i++) {
            for (int j = i + 1; j < peopleAtTheSameHotel.size(); j++) {
                List<Integer> pair = new ArrayList<>(2);
                pair.add(peopleAtTheSameHotel.get(i).getPersonId());
                pair.add(peopleAtTheSameHotel.get(j).getPersonId());
                pairs.add(pair);
            }
        }

        return pairs;
    }

    private static void insertPairsToPairMap(List<List<Integer>> pairs) {
        for (List<Integer> pair : pairs) {
            pair.sort(Comparator.comparing(Integer::valueOf));
        }

        for (List<Integer> pair : pairs) {
            if (pairMeetCount.containsKey(pair)) {
                pairMeetCount.replace(pair, pairMeetCount.get(pair) + 1);
            } else {
                pairMeetCount.put(pair, 1);
            }
        }
    }

    private static void storeAndPrintPairMeetCount() {
        long suspectedEventsCount = 0;
        List<Integer> suspectedEventsValues = pairMeetCount.values().stream()
                .filter(v -> v > 1)
                .collect(Collectors.toList());
        for (Integer value : suspectedEventsValues) {
            suspectedEventsCount += CombinatoricsUtils.binomialCoefficientDouble(value, 2);
        }
        suspectedEventsFinalCount += suspectedEventsCount / ITERATION_COUNT;
        System.out.println("\nSuspected events count: " + suspectedEventsCount);

        long suspectedPairsCount = pairMeetCount.values().stream().filter(v -> v > 1).count();
        suspectedPairsFinalCount += suspectedPairsCount / ITERATION_COUNT;
        System.out.println("Suspected pair count: " + suspectedPairsCount);

        System.out.println("\nHistogram:");
        int maxMeetCount = pairMeetCount.values().stream().max(Comparator.comparing(Integer::valueOf)).orElse(0);
        for (int i = 1; i <= maxMeetCount; i++) {
            final int meetCount = i;
            long sum = pairMeetCount.values().stream().filter(value -> value == meetCount).count();
            if (histogramFinalValueMap.containsKey(i)) {
                histogramFinalValueMap.put(i, histogramFinalValueMap.get(i) + (sum / ITERATION_COUNT));
            } else {
                histogramFinalValueMap.put(i, (sum / ITERATION_COUNT));
            }
            System.out.println(i + "=" + sum);
        }

        final Set<Integer> suspectedPeople = new HashSet<>();
        pairMeetCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .forEach(suspectedPeople::addAll);
        suspectedPeopleFinalCount += suspectedPeople.size() / ITERATION_COUNT;
        System.out.println("\nSuspected people count: " + suspectedPeople.size());
    }

    private static void printFinalStats() {
        System.out.println("\n******************************************************");
        System.out.println("Elapsed time: " + (double) (System.nanoTime() - startTime) / 1000000000.0 + " seconds");
        System.out.println("AVERAGE VALUES:");
        System.out.println("Suspected events final count: " + suspectedEventsFinalCount);
        System.out.println("Suspected pair final count: " + suspectedPairsFinalCount);
        System.out.println("\nHistogram:");
        int i = 1;
        for (Long value : histogramFinalValueMap.values()) {
            System.out.println(i + "=" + value);
            i++;
        }
        System.out.println("\nSuspected people final count: " + suspectedPeopleFinalCount);
    }

    private static void exit() {
        try {
            System.out.println("\nPress any key to exit...");
            System.in.read();
        } catch (IOException e) {
            System.exit(0);
        }
        System.exit(0);
    }
}