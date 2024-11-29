package backend.dev.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseData {
    private int page;
    private int per_page;
    private int total;
    private int total_pages;
    private ArrayList<MovieData> data = new ArrayList<>();
}
