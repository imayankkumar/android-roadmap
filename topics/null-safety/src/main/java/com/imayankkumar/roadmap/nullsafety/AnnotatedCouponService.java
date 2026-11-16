package com.imayankkumar.roadmap.nullsafety;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * The same service, fixed at the boundary. @NullMarked says "non-null unless marked",
 * and @Nullable marks the one value that can be null. Kotlin reads these as String?.
 */
@NullMarked
public class AnnotatedCouponService {

    public @Nullable String getCouponCode(String orderId) {
        if (orderId.startsWith("A")) {
            return "SAVE10";
        }
        return null;
    }
}
