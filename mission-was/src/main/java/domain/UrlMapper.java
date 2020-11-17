package domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.Controller;
import dto.UrlMappingCreateDto;

public class UrlMapper {

    private final Map<String, Controller> mapper;

    private UrlMapper(List<UrlMappingCreateDto> urlMappingCreateDtos) {
        this.mapper = new HashMap<>();
        for (UrlMappingCreateDto urlMappingCreateDto : urlMappingCreateDtos) {
            this.mapper.put(urlMappingCreateDto.getUrl(), urlMappingCreateDto.getController());
        }
    }

    public static UrlMapper from(List<UrlMappingCreateDto> urlMappingCreateDtos) {
        return new UrlMapper(urlMappingCreateDtos);
    }

    public Controller getController(String url) {
        return this.mapper.get(url);
    }

    public boolean contains(String url) {
        return this.mapper.containsKey(url);
    }
}
