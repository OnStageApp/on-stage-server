package org.onstage.device.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.device.client.DeviceDTO;
import org.onstage.device.model.Device;
import org.onstage.device.model.mapper.DeviceMapper;
import org.onstage.device.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;
    private final DeviceMapper deviceMapper;

    @PostMapping("/login")
    public ResponseEntity<DeviceDTO> createOrUpdateDevice(@RequestBody DeviceDTO deviceDTO) {
        Device device = deviceService.createOrUpdateDevice(deviceMapper.fromDTO(deviceDTO));
        return ResponseEntity.ok(deviceMapper.toDTO(device));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable final String id, @RequestBody DeviceDTO deviceDTO) {
        Device existingDevice = deviceService.getById(id);
        return ResponseEntity.ok(deviceMapper.toDTO(deviceService.updateDevice(existingDevice, deviceMapper.fromDTO(deviceDTO))));
    }
}
