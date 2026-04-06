package org.atlas_erp.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class TemplateDTO {
    private String id;
    private String name;
    private String description;
    private List<String> requiredVariables;
}
