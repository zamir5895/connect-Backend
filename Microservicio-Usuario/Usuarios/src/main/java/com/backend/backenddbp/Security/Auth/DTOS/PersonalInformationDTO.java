package com.backend.backenddbp.Security.Auth.DTOS;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonalInformationDTO {
    private LocalDate dateOfBirth;
    private String gender;
    private String direccion;
    private String pais;
    private String ciudad;
    private Double latitude;
    private Double longitude;
    private String telefono;

}
