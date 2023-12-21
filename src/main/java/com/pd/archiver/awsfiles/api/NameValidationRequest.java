package com.pd.archiver.awsfiles.api;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * The type Name validation request.
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NameValidationRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String extension;
}