package org.onstage.device.controller;

import lombok.RequiredArgsConstructor;
import org.onstage.common.beans.UserSecurityContext;
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
    private final UserSecurityContext userSecurityContext;

    @PostMapping("/login")
    public ResponseEntity<DeviceDTO> loginDevice(@RequestBody DeviceDTO deviceDTO) {
        String userId = userSecurityContext.getUserId();
        Device device = deviceService.loginDevice(deviceMapper.fromDTO(deviceDTO, userId));
        return ResponseEntity.ok(deviceMapper.toDTO(device));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable final String id, @RequestBody DeviceDTO deviceDTO) {
        String userId = userSecurityContext.getUserId();
        Device existingDevice = deviceService.getById(id);
        return ResponseEntity.ok(deviceMapper.toDTO(deviceService.updateDevice(existingDevice, deviceMapper.fromDTO(deviceDTO, userId))));
    }
}
