package com.epam.finaltask.restcontroller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.VoucherService;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherRestController {

    private final VoucherService voucherService;

    public VoucherRestController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public ResponseEntity<Map<String, List<VoucherDTO>>> findAll() {
        List<VoucherDTO> list = voucherService.findAll();
        return ResponseEntity.ok(Collections.singletonMap("results", list));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, List<VoucherDTO>>> findAllByUser(@PathVariable String userId) {
        List<VoucherDTO> list = voucherService.findAllByUserId(userId);
        return ResponseEntity.ok(Collections.singletonMap("results", list));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody VoucherDTO dto) {
        VoucherDTO created = voucherService.create(dto);
        Map<String, Object> body = new HashMap<>();
        body.put("statusCode", "OK");
        body.put("statusMessage", "Voucher is successfully created");
        body.put("data", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable String id, @RequestBody VoucherDTO dto) {
        VoucherDTO updated = voucherService.update(id, dto);
        Map<String, Object> body = new HashMap<>();
        body.put("statusCode", "OK");
        body.put("statusMessage", "Voucher is successfully updated");
        body.put("data", updated);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        voucherService.delete(id);
        Map<String, Object> body = new HashMap<>();
        body.put("statusCode", "OK");
        body.put("statusMessage", String.format("Voucher with Id %s has been deleted", id));
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> changeStatus(@PathVariable String id,
                                                            @RequestBody(required = false) VoucherDTO dto) {
        VoucherDTO result = voucherService.changeHotStatus(id, dto);
        Map<String, Object> body = new HashMap<>();
        body.put("statusCode", "OK");
        body.put("statusMessage", "Voucher status is successfully changed");
        body.put("data", result);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, List<VoucherDTO>>> search(
            @RequestParam(value = "tourType", required = false) String tourType,
            @RequestParam(value = "transferType", required = false) String transferType,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "hotelType", required = false) String hotelType) {

        if (tourType != null) {
            return ResponseEntity.ok(Collections.singletonMap("results", voucherService.findAllByTourType(
                    com.epam.finaltask.model.TourType.valueOf(tourType))));
        }
        if (transferType != null) {
            return ResponseEntity.ok(Collections.singletonMap("results", voucherService.findAllByTransferType(transferType)));
        }
        if (price != null) {
            return ResponseEntity.ok(Collections.singletonMap("results", voucherService.findAllByPrice(price)));
        }
        if (hotelType != null) {
            return ResponseEntity.ok(Collections.singletonMap("results", voucherService.findAllByHotelType(
                    com.epam.finaltask.model.HotelType.valueOf(hotelType))));
        }
        return ResponseEntity.ok(Collections.singletonMap("results", voucherService.findAll()));
    }
}
