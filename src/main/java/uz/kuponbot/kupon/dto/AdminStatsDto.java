package uz.kuponbot.kupon.dto;

public record AdminStatsDto(
    long totalUsers,
    long totalCoupons,
    long activeCoupons,
    long usedCoupons,
    long totalProducts,
    long totalOrders
) {
}