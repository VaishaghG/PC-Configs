package com.pcconfigurations.backend.algorithms;

import com.pcconfigurations.backend.entity.Product;
import java.util.ArrayList;
import java.util.List;

/**
 * Merge Sort for Products by price.
 *
 * Time Complexity:  O(n log n)
 * Space Complexity: O(n)
 *
 * This implementation is independent of JPA repositories and controllers.
 * It operates purely on in-memory lists.
 */
public class ProductMergeSort {

    public enum SortOrder { ASCENDING, DESCENDING }

    /**
     * Sort a list of products by price using Merge Sort.
     *
     * @param products  The list to sort (not mutated; returns a new list)
     * @param order     ASCENDING or DESCENDING
     * @return A new sorted list
     */
    public static List<Product> sortByPrice(List<Product> products, SortOrder order) {
        if (products == null || products.size() <= 1) {
            return products == null ? new ArrayList<>() : new ArrayList<>(products);
        }
        List<Product> copy = new ArrayList<>(products);
        mergeSort(copy, 0, copy.size() - 1, order);
        return copy;
    }

    private static void mergeSort(List<Product> list, int left, int right, SortOrder order) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(list, left, mid, order);
            mergeSort(list, mid + 1, right, order);
            merge(list, left, mid, right, order);
        }
    }

    private static void merge(List<Product> list, int left, int mid, int right, SortOrder order) {
        List<Product> leftPart  = new ArrayList<>(list.subList(left, mid + 1));
        List<Product> rightPart = new ArrayList<>(list.subList(mid + 1, right + 1));

        int i = 0, j = 0, k = left;

        while (i < leftPart.size() && j < rightPart.size()) {
            int cmp = leftPart.get(i).getPrice().compareTo(rightPart.get(j).getPrice());
            boolean takeLeft = (order == SortOrder.ASCENDING) ? cmp <= 0 : cmp >= 0;
            if (takeLeft) {
                list.set(k++, leftPart.get(i++));
            } else {
                list.set(k++, rightPart.get(j++));
            }
        }

        while (i < leftPart.size())  list.set(k++, leftPart.get(i++));
        while (j < rightPart.size()) list.set(k++, rightPart.get(j++));
    }
}
