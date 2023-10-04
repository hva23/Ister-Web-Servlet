package com.ister.isterservlet.service;

import com.ister.isterservlet.common.RequestStatus;
import com.ister.isterservlet.domain.Location;
import com.ister.isterservlet.repository.LocationRepository;

import javax.swing.text.html.HTMLDocument;
import java.util.*;

public class LocationService {
    LocationRepository locationRepository;

    public LocationService() {
        this.locationRepository = new LocationRepository("jdbc:mysql://localhost:8080/Ister", "root", "v@h@bI2442");
    }

    public RequestStatus addLocation(Location location) {

        if (locationRepository.create(location)) {
            System.out.println("Location added successfully");
            return RequestStatus.Successful;
        } else {
            System.out.println("Something went wrong!");
            return RequestStatus.AlreadyExists;
        }
    }


    public RequestStatus editLocation(Location location) {
        if (locationRepository.findById(location.getId()).isPresent()) {
            if (locationRepository.update(location)) {
                System.out.println("the location updated successfully");
                return RequestStatus.Successful;
            } else {
                System.out.println("Something went wrong!");
                return RequestStatus.Failed;
            }
        } else {
            System.out.println("The location doesn't exist");
            return RequestStatus.DoesNotExist;
        }
    }


    public RequestStatus deleteLocation(Location location) {
        if (locationRepository.findById(location.getId()).isPresent()) {
            if (locationRepository.delete(location)) {
                System.out.println("the location deleted successfully");
                return RequestStatus.Successful;
            } else {
                System.out.println("Something went wrong!");
                return RequestStatus.Failed;
            }
        } else {
            System.out.println("The location doesn't exist");
            return RequestStatus.DoesNotExist;
        }
    }


    public String getLocationData(Long id) {
        Optional<Location> locationOptional = locationRepository.findById(id);
        return locationOptional.map(location -> String.format("""
                        Location ID : %d
                        Province : %s
                        City : %s
                        Latitude : %s
                        Longitude : %s
                        """,
                location.getId(),
                location.getProvince(),
                location.getCity(),
                location.getLatitude(),
                location.getLongitude())).orElse("Location doesn't exist");
    }

    public Location getLocation(Long id) {
        Optional<Location> locationOptional = locationRepository.findById(id);
        return locationOptional.orElse(null);
    }

    public Location getLocationByProvinceAndCity(String province, String city) {
        Optional<Location> locationOptional = locationRepository.findByProvinceAndCity(province, city);
        return locationOptional.orElse(null);
    }

    public String getAllLocations() {
        Location location;
        List<Location> locationList = locationRepository.getAll();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < locationList.size(); i++) {
            location = locationList.get(i);
            stringBuilder.append(String.format("""
                            -- #%02d --
                            Location ID : %d
                            Province : %s
                            City : %s
                            Latitude : %s
                            Longitude : %s
                                                        
                            """,
                    i + 1,
                    location.getId(),
                    location.getProvince(),
                    location.getCity(),
                    location.getLatitude(),
                    location.getLongitude()));
        }
        return stringBuilder.toString();
    }


}
