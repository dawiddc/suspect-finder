package com.dawiddc.problemforeseer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static Integer dayCount;
    private static Integer hotelCount;
    private static Integer peopleCount;
    private static double nightSpentAtHotelProbability;
    private static Map<List<Integer>, Integer> pairMeetCount = new HashMap<>();

    public static void main(String[] args) {
        checkAndAssignArgs(args);
        findSuspectedPairs();
        printPairMeetCount();

        try {
            System.out.println("Press any key to exit...");
            System.in.read();
        } catch (IOException e) {
            System.exit(0);
        }
        System.exit(0);
    }

    private static void checkAndAssignArgs(String[] args) {
        if (args.length < 4) {
            System.out.println("Not enough args!");
            try {
                System.in.read();
            } catch (IOException e) {
                System.exit(0);
            }
            System.exit(0);
        }

        peopleCount = Integer.valueOf(args[0]);
        nightSpentAtHotelProbability = Double.parseDouble(args[1]);
        hotelCount = Integer.valueOf(args[2]);
        dayCount = Integer.valueOf(args[3]);
    }

    private static List<Person> createAndFillPeopleList(Integer peopleCount) {
        List<Person> people = new ArrayList<>(peopleCount);

        for (int i = 1; i <= peopleCount; i++) {
            Person person = Person.builder().personId(i).build();
            people.add(person);
        }
        return people;
    }

    private static List<List<Person>> createAndFillDaysPeopleList(List<Person> people) {
        List<List<Person>> daysPeopleList = new ArrayList<>(dayCount);
        Random r = new Random();

        for (int i = 0; i < dayCount; i++) {
            List<Person> dayWithPeopleGoingToHotel = people.stream()
                    .filter(person -> Math.random() < nightSpentAtHotelProbability)
                    .collect(Collectors.toList());
            dayWithPeopleGoingToHotel.forEach(person -> person.setHotelId(r.nextInt(hotelCount)));
            daysPeopleList.add(dayWithPeopleGoingToHotel);
        }
        return daysPeopleList;
    }

    private static void findSuspectedPairs() {
        List<Person> people = createAndFillPeopleList(peopleCount);
        List<List<Person>> daysPeopleList = createAndFillDaysPeopleList(people);

        for (List<Person> day : daysPeopleList) {
            List<List<Person>> peopleAtTheSameHotelList = createAndFillPeopleAtTheSameHotelList(day);
            for (List<Person> peopleAtTheSameHotel : peopleAtTheSameHotelList) {
                List<List<Integer>> pairs = calculatePairs(peopleAtTheSameHotel);
                insertPairsToPairMap(pairs);
            }
        }
    }

    private static List<List<Person>> createAndFillPeopleAtTheSameHotelList(List<Person> day) {
        List<List<Person>> peopleAtTheSameHotelList = new ArrayList<>();
        for (int i = 0; i < hotelCount; i++) {
            final int hotelId = i;
            List<Person> peopleAtTheSameHotel = day.stream().filter(p -> p.getHotelId() == hotelId).collect(Collectors.toList());
            if (!peopleAtTheSameHotel.isEmpty()) {
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

    private static void printPairMeetCount() {
        long suspectedPairsCount = 0;
        for (Integer value : pairMeetCount.values()) {
            suspectedPairsCount += value;
        }
        System.out.println("Suspected pair count: " + suspectedPairsCount);

        System.out.println("\n\nHistogram:\n");
        int maxMeetCount = pairMeetCount.values().stream().max(Comparator.comparing(Integer::valueOf)).orElse(0);
        for (int i = 1; i <= maxMeetCount; i++) {
            final int meetCount = i;
            long sum = pairMeetCount.values().stream().filter(value -> value == meetCount).count();
            System.out.println(i + "=" + sum + "\n");
        }

        Set<Integer> suspectedPeople = new HashSet<>();
        pairMeetCount.keySet().forEach(suspectedPeople::addAll);
        System.out.println("\nSuspected people count: " + suspectedPeople.size());
    }
}