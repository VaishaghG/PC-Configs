package com.pcconfigurations.backend.algorithms;

import com.pcconfigurations.backend.entity.Product;
import java.util.List;

/**
 * Binary Search on Products sorted by ID.
 *
 * Pre-condition: the list MUST be sorted ascending by product ID.
 *
 * Time Complexity:  O(log n)
 * Space Complexity: O(1)
 *
 * Returns the product with the given ID, or null if not found.
 */
public class ProductBinarySearch {

    /**
     * Searches for a product with the given id in a sorted list.
     *
     * @param sortedProducts List of products sorted ascending by id
     * @param targetId       The product id to search for
     * @return The matching Product or null
     */
    public static Product search(List<Product> sortedProducts, long targetId) {
        if (sortedProducts == null || sortedProducts.isEmpty()) {
            return null;
        }
        int low = 0;
        int high = sortedProducts.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            long midId = sortedProducts.get(mid).getId();

            if (midId == targetId) {
                return sortedProducts.get(mid);
            } else if (midId < targetId) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null; // not found
    }
}
