package net.modificationstation.stationapi.impl.recipe;

import java.util.Comparator;

/**
 * Fixed comparator that properly implements the comparison contract.
 * Sorts recipes with shaped recipes first, then by size (smaller first).
 *
 * This fixes the buggy comparator in CraftingRecipeManager that violates
 * the general contract and causes IllegalArgumentException in Java 17+.
 */
public class CraftingRecipeComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == o2) return 0;

        try {
            // Get class names to determine recipe type
            String o1Class = o1.getClass().getSimpleName();
            String o2Class = o2.getClass().getSimpleName();

            // Determine recipe types
            boolean o1Shapeless = o1Class.contains("Shapeless");
            boolean o2Shapeless = o2Class.contains("Shapeless");

            // Different types: Shaped comes before Shapeless
            if (o1Shapeless != o2Shapeless) {
                return o1Shapeless ? 1 : -1;
            }

            // Same type: compare by size (smaller first)
            try {
                int o1Size = (Integer) o1.getClass().getMethod("getSize").invoke(o1);
                int o2Size = (Integer) o2.getClass().getMethod("getSize").invoke(o2);
                return Integer.compare(o1Size, o2Size);
            } catch (Exception e) {
                // If we can't get sizes, maintain order
                return 0;
            }
        } catch (Exception e) {
            // Fallback: maintain original order
            return 0;
        }
    }
}
