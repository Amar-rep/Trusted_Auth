package com.example_amar.central_athority.repository;

import com.example_amar.central_athority.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device,Integer> {
    Optional<Device> findByDeviceId(String deviceId);
}
