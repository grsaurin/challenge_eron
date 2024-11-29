package backend.dev.challenge.controller;

import backend.dev.challenge.model.DirectorsResponse;
import backend.dev.challenge.service.DirectorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/api/directors")
public class DirectorController {

    private final DirectorService directorsService;

    @Autowired
    public DirectorController(DirectorService directorsService) {
        this.directorsService = directorsService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getDirectors(@RequestParam(name = "threshold") int threshold) {
        try{
            CompletableFuture<DirectorsResponse> directorsResponse = directorsService.getDirectors(threshold);
            return new ResponseEntity<>(directorsResponse.get(), HttpStatus.OK);
        }  catch (HttpStatusCodeException httpCodeExc) {
            return new ResponseEntity(httpCodeExc.getMessage(), httpCodeExc.getStatusCode());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity(new RuntimeException(e), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (JSONException | JsonProcessingException e) {
                return new ResponseEntity<>( new RuntimeException(e),  HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
