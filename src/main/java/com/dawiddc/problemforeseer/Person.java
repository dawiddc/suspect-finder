package com.dawiddc.problemforeseer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class Person {
    Integer personId;
    Integer hotelId;
}