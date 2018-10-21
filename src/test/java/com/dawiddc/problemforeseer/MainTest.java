package com.dawiddc.problemforeseer;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple Main.
 */
public class MainTest {
    @Test
    public void shouldAddPairsToMapProperly() {
        Map<List<Integer>, Integer> pairMeetCount = new HashMap<>();
        List<Integer> pair1 = Arrays.asList(1, 2);
        List<Integer> pair2 = Arrays.asList(1, 3);
        List<Integer> pair3 = Arrays.asList(1, 4);
        List<Integer> pair4 = Arrays.asList(2, 3);
        List<Integer> pair5 = Arrays.asList(2, 4);
        List<Integer> pair6 = Arrays.asList(3, 4);
        List<Integer> pair7 = Arrays.asList(3, 4);
        List<Integer> pair8 = Arrays.asList(4, 3);
        List<List<Integer>> pairs = Arrays.asList(pair1, pair2, pair3, pair4, pair5, pair6, pair7, pair8);

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

        Assert.assertEquals(6, pairMeetCount.size());
    }

    @Test
    public void shouldCalculatePairsProperly() {
        List<List<Integer>> pairs = new ArrayList<>();

        List<Person> peopleAtTheSameHotel = Arrays.asList(
                Person.builder().personId(1).build(),
                Person.builder().personId(2).build(),
                Person.builder().personId(3).build(),
                Person.builder().personId(4).build(),
                Person.builder().personId(5).build());

        for (int i = 0; i < peopleAtTheSameHotel.size() - 1; i++) {
            for (int j = i + 1; j < peopleAtTheSameHotel.size(); j++) {
                List<Integer> pair = new ArrayList<>(2);
                pair.add(peopleAtTheSameHotel.get(i).getPersonId());
                pair.add(peopleAtTheSameHotel.get(j).getPersonId());
                pairs.add(pair);
            }
        }

        Assert.assertEquals(10, pairs.size());
        Assert.assertEquals(2, (int) pairs.get(5).get(0));
        assertEquals(4, (int) pairs.get(5).get(1));
    }
}