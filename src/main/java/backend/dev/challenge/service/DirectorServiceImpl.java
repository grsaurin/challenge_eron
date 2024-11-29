package backend.dev.challenge.service;

import backend.dev.challenge.model.ApiResponseData;
import backend.dev.challenge.model.MovieData;
import backend.dev.challenge.model.DirectorsResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class DirectorServiceImpl implements DirectorService {

    @Value("${endpoint.url}")
    private String URL;

    private final RestTemplate template;

    public DirectorServiceImpl(RestTemplate template) {
        this.template = template;
    }

    @Override
    @Async
    public CompletableFuture<DirectorsResponse> getDirectors(int threshold) {
        //get param
        int iterator = 1;
        int stopCondition = 1000;
       // HashMap<String, Integer> directorsMap = new HashMap<>();
        Predicate<Integer> predicate = value -> value > Integer.valueOf(threshold);
        ArrayList<MovieData> allMovieData = new ArrayList<>();

        try{
        //consuming endpoint to get raw data
        do{
            String jsonResp = template.getForObject(URL, String.class, iterator);
            if(jsonResp.isEmpty()) {
               return CompletableFuture.completedFuture(new DirectorsResponse());
            }
            Gson gsonParser = new GsonBuilder().create();
            ApiResponseData ard = gsonParser.fromJson(jsonResp, ApiResponseData.class);
            if(iterator == 1 ) {
                stopCondition = ard.getTotal_pages();
            }
            allMovieData.addAll(ard.getData());
            iterator++;
        } while (iterator <= stopCondition);

        //filtering data
        Map<String, Integer> result = getDirectorData(allMovieData).entrySet()
                .stream()
                .filter(item -> predicate.test(item.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //sorting result data and returning into Object to get correct Json format response
        return CompletableFuture.completedFuture(new DirectorsResponse(new ArrayList<>(result.keySet().stream().toList().stream().sorted().collect(Collectors.toList()))));
        } catch (HttpClientErrorException clientErrorException) {
            throw new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED, "fail to process request in CLIENT side");
        } catch (HttpServerErrorException serverErrorException) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "fail to process request in destination server");
        } catch (UnknownHttpStatusCodeException unknownStatusCodeException) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "unknown status code");
        }
    }

    private Map<String, Integer> getDirectorData(ArrayList<MovieData> movieData) {
        HashMap<String, Integer> directorsMap = new HashMap<>();
        for(MovieData md : movieData) {
            String[] dirs = md.getDirector().split(",");
            for(String dirName : dirs) {
                dirName = dirName.trim();
                if(directorsMap.containsKey(dirName)) {
                    directorsMap.replace(dirName, directorsMap.get(dirName)+1);
                } else {
                    directorsMap.put(dirName, 1);
                }
            }
        }
        return directorsMap;
    }
}
