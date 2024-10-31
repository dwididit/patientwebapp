package dev.dwidi.patientwebapp.entity.embedded;

import dev.dwidi.patientwebapp.enums.AustralianState;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Embeddable
@Data
public class AustralianAddress {
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "suburb", nullable = false, length = 100)
    private String suburb;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 3)
    private AustralianState state;

    @Column(name = "postcode", nullable = false, length = 4)
    private String postcode;
}