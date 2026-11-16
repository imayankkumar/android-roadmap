package com.imayankkumar.roadmap.nullsafety;

/**
 * Old Java code with no nullability annotations.
 * Kotlin sees the return type as String! (a platform type): it can't tell if null is possible.
 */
public class CouponService {

    public String getCouponCode(String orderId) {
        if (orderId.startsWith("A")) {
            return "SAVE10";
        }
        return null; // no coupon for this order
    }
}
