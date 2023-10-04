package com.ister.isterservlet.service;

import com.ister.isterservlet.common.RequestStatus;
import com.ister.isterservlet.domain.Location;
import com.ister.isterservlet.repository.AdminRepository;
import com.ister.isterservlet.repository.LocationRepository;

public class AdminService {
    AdminRepository adminRepository;

    public AdminService() {
        this.adminRepository = new AdminRepository("jdbc:mysql://localhost:8080/Ister", "root", "v@h@bI2442");
    }

    public RequestStatus addAdmin(String id) {
        if (adminRepository.create(id)) {
            System.out.println("Admin added successfully");
            return RequestStatus.Successful;
        } else {
            System.out.println("Something went wrong!");
            return RequestStatus.AlreadyExists;
        }
    }


    public RequestStatus deleteAdmin(String id) {
        if(adminRepository.delete(id)) {
            System.out.println("Admin deleted successfully");
            return RequestStatus.Successful;
        } else {
            System.out.println("Something went wrong!");
            return RequestStatus.DoesNotExist;
        }
    }

    public boolean isAdmin(String id) {
        if(adminRepository.findById(id).isPresent()) return true;
        return false;
    }
}
