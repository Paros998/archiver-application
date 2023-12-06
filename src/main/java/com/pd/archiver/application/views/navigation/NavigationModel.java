package com.pd.archiver.application.views.navigation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Navigation model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NavigationModel {
    private String url;
    private String label;
}