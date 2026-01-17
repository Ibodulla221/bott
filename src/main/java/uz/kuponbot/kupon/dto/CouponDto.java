package uz.kuponbot.kupon.dto;

import java.time.LocalDateTime;

public record CouponDto(
    Long id,
    String code,
    Long userTelegramId,
    String userName,
    String status,
    LocalDateTime createdAt,
    LocalDateTime usedAt
) {
}