package com.epharmacy.pharmacy_medicine_service.dto.responsedto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicinePageResponseDTO {

    private List<MedicineResponseDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

   
}
