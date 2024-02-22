package hei.school.sarisary.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ListUrl {
    private String original_url;
    private String transformed_url;
}
