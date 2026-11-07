package com.johnpickup.util;

import java.util.ArrayList;
import java.util.List;

public class Lists {
    public static <T> List<T> with(List<T> list, T item) {
        List<T> result = new ArrayList<>(list);
        result.add(item);
        return result;
    }

    public static <T> List<T> without(List<T> list, T item) {
        List<T> result = new ArrayList<>(list);
        result.remove(item);
        return result;
    }

    public static <T> List<T> withAll(List<T> list, List<T> items) {
        List<T> result = new ArrayList<>(list);
        result.addAll(items);
        return result;
    }

    public static <T> List<T> withoutAll(List<T> list, List<T> items) {
        List<T> result = new ArrayList<>(list);
        result.removeAll(items);
        return result;
    }

}
