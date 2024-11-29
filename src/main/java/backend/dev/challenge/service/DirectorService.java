package backend.dev.challenge.service;

import backend.dev.challenge.model.DirectorsResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.util.concurrent.CompletableFuture;

public interface DirectorService {
    CompletableFuture<DirectorsResponse> getDirectors(int threshold) throws JsonProcessingException, JSONException;
}
