package com.wt.mse.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpNicknameService implements NicknameService
{
    private static final Logger LOGGER = Logger.getLogger(HttpNicknameService.class.getName());

    private final HttpClient client;
    private final ObjectMapper mapper;

    public HttpNicknameService()
    {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build(), new ObjectMapper());
    }

    public HttpNicknameService(HttpClient client, ObjectMapper mapper)
    {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public String nickFromUUID(String rawUuid) throws IOException
    {
        if(rawUuid == null || rawUuid.isBlank())
        {
            return "! wrong UUID !";
        }

        String uuid = rawUuid.replace("-", "");
        if(!uuid.matches("^[0-9a-fA-F]{32}$"))
        {
            return "! wrong UUID !";
        }

        String apiUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid;
        try
        {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(apiUrl)).GET().timeout(Duration.ofSeconds(5)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if(statusCode >= 500 || statusCode == 429)
            {
                return "! no response !";
            }

            if(statusCode == 404 || statusCode == 204)
            {
                return "! player not found !";
            }

            String responseBody = response.body();
            if(responseBody == null || responseBody.isBlank())
            {
                return "! player not found !";
            }

            JsonNode root = mapper.readTree(responseBody);
            JsonNode nameNode = root.get("name");

            if(nameNode == null || nameNode.isNull())
            {
                return "! player not found !";
            }

            return nameNode.asText();
        }
        catch(URISyntaxException e)
        {
            LOGGER.log(Level.SEVERE, "Niepoprawny URI", e);
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "Przerwano żądanie", e);
        }
        catch(IOException e)
        {
            LOGGER.log(Level.WARNING, "Błąd I/O", e);
        }

        return "! no response !";
    }
}
