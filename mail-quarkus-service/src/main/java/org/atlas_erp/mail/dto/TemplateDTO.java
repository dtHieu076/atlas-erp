package org.atlas_erp.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateDTO {
    private String id;
    private String name;
    private String description;
    private List<String> requiredVariables;
}
