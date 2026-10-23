package com.pcconfigurations.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class PcBuildRequest {
    @NotBlank(message = "Build name is required")
    private String buildName;

    private String notes;

    @NotNull(message = "Component IDs are required")
    private List<Long> componentIds;
}
