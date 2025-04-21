package org.example.greetingright.service;

import org.example.greetingright.entity.DatasetWish;
import org.example.greetingright.entity.User;
import org.example.greetingright.entity.Wish;
import org.example.greetingright.repository.DatasetWishRepository;
import org.example.greetingright.repository.UserRepository;
import org.example.greetingright.repository.WishRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlaskWishServiceIMPL {

    @Value("${flask.server.url}")
    private String flaskServerUrl;

    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    private final DatasetWishRepository datasetWishRepository;

    public FlaskWishServiceIMPL(WishRepository wishRepository, UserRepository userRepository, DatasetWishRepository datasetWishRepository) {
        this.wishRepository = wishRepository;
        this.userRepository = userRepository;
        this.datasetWishRepository = datasetWishRepository;
    }

    public Wish generateWish(Long userId, String userRequest) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }

        User user = userOptional.get();

        // Fetch all datasetWishIDs for the user
        Set<Long> datasetWishIDs = datasetWishRepository.findByUser(user)
                .stream()
                .map(DatasetWish::getDatasetWishID)
                .collect(Collectors.toSet());

        Map<String, Object> flaskRequest = new HashMap<>();
        flaskRequest.put("text", userRequest);
        flaskRequest.put("blacklist_ids", datasetWishIDs);
        System.out.println("Request to Flask: " + flaskRequest);
        System.out.println("datasetWishIDs : " + datasetWishIDs);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> flaskResponse = restTemplate.postForEntity(
                flaskServerUrl + "/generate-wish",
                flaskRequest,
                Map.class
        );

        Map responseBody = flaskResponse.getBody();
        String newWishText = responseBody.get("wish").toString();
        Long datasetWishId = Long.valueOf(responseBody.get("wish_id").toString());

        // Save the new wish
        Wish newWish = new Wish();
        newWish.setBirthdayWish(newWishText);
        newWish.setCreationDate(new Date());
        newWish.setUser(user);
        wishRepository.save(newWish);

        // Save the new dataset wish
        DatasetWish newDatasetWish = new DatasetWish();
        newDatasetWish.setDatasetWishID(datasetWishId);
        newDatasetWish.setUser(user);
        datasetWishRepository.save(newDatasetWish);

        return newWish;
    }

    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username))
                .getId();
    }
}