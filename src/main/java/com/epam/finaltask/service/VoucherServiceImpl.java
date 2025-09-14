package com.epam.finaltask.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;

@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final VoucherMapper voucherMapper;

    public VoucherServiceImpl(VoucherRepository voucherRepository, UserRepository userRepository, VoucherMapper voucherMapper) {
        this.voucherRepository = voucherRepository;
        this.userRepository = userRepository;
        this.voucherMapper = voucherMapper;
    }

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        if (voucherDTO == null) throw new RuntimeException("Voucher data is required");

        Voucher v = voucherMapper.toVoucher(voucherDTO);
        if (v.getStatus() == null) v.setStatus(VoucherStatus.REGISTERED);
        Voucher saved = voucherRepository.save(v);
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    public VoucherDTO order(String id, String userId) {
        UUID voucherId;
        UUID uId;
        try {
            voucherId = UUID.fromString(id);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid voucher id: " + id);
        }
        try {
            uId = UUID.fromString(userId);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid user id: " + userId);
        }

        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(() -> new RuntimeException("Voucher not found: " + id));

        if (voucher.getUser() != null) {
            throw new RuntimeException("Voucher is already ordered by another user.");
        }

        var user = userRepository.findById(uId).orElseThrow(() -> new RuntimeException("User not found: " + userId));

        voucher.setUser(user);
        voucher.setStatus(VoucherStatus.REGISTERED);

        Voucher saved = voucherRepository.save(voucher);
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        UUID voucherId;
        try {
            voucherId = UUID.fromString(id);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid voucher id: " + id);
        }

        Voucher existing = voucherRepository.findById(voucherId).orElseThrow(() -> new RuntimeException("Voucher not found: " + id));

        if (voucherDTO.getTitle() != null) existing.setTitle(voucherDTO.getTitle());
        if (voucherDTO.getDescription() != null) existing.setDescription(voucherDTO.getDescription());
        if (voucherDTO.getPrice() != null) existing.setPrice(voucherDTO.getPrice());
        if (voucherDTO.getTourType() != null) {
            try {
                existing.setTourType(com.epam.finaltask.model.TourType.valueOf(voucherDTO.getTourType()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (voucherDTO.getTransferType() != null) {
            try {
                existing.setTransferType(TransferType.valueOf(voucherDTO.getTransferType()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (voucherDTO.getHotelType() != null) {
            try {
                existing.setHotelType(com.epam.finaltask.model.HotelType.valueOf(voucherDTO.getHotelType()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (voucherDTO.getStatus() != null) {
            try {
                existing.setStatus(VoucherStatus.valueOf(voucherDTO.getStatus()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (voucherDTO.getArrivalDate() != null) existing.setArrivalDate(voucherDTO.getArrivalDate());
        if (voucherDTO.getEvictionDate() != null) existing.setEvictionDate(voucherDTO.getEvictionDate());
        if (voucherDTO.getIsHot() != null) existing.setHot(voucherDTO.getIsHot());

        if (voucherDTO.getUserId() != null) {
            try {
                UUID uid = UUID.fromString(voucherDTO.getId());
                var user = userRepository.findById(uid).orElseThrow(() -> new RuntimeException("User not found: " + voucherDTO.getUserId()));
                existing.setUser(user);
            } catch (IllegalArgumentException ex) {
            }
        }

        Voucher saved = voucherRepository.save(existing);
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    public void delete(String voucherId) {
        UUID vid;
        try {
            vid = UUID.fromString(voucherId);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid voucher id: " + voucherId);
        }
        if (!voucherRepository.existsById(vid)) {
            throw new RuntimeException("Voucher not found: " + voucherId);
        }
        voucherRepository.deleteById(vid);
    }

    @Override
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        UUID vid;
        try {
            vid = UUID.fromString(id);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid voucher id: " + id);
        }
        Voucher existing = voucherRepository.findById(vid).orElseThrow(() -> new RuntimeException("Voucher not found: " + id));

        //   if DTO isHot — set it; otherwise toggle
        if (voucherDTO != null && voucherDTO.getIsHot() != null) {
            existing.setHot(voucherDTO.getIsHot());
        } else {
            existing.setHot(!existing.isHot());
        }

        Voucher saved = voucherRepository.save(existing);
        return voucherMapper.toVoucherDTO(saved);
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        UUID uid;
        try {
            uid = UUID.fromString(userId);
        } catch (Exception ex) {
            throw new RuntimeException("Invalid user id: " + userId);
        }
        return voucherRepository.findAllByUserId(uid).stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTourType(com.epam.finaltask.model.TourType tourType) {
        if (tourType == null) throw new RuntimeException("tourType is required");
        return voucherRepository.findAllByTourType(tourType).stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        if (transferType == null) throw new RuntimeException("transferType is required");
        TransferType tt;
        try {
            tt = TransferType.valueOf(transferType);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Unknown transfer type: " + transferType);
        }
        return voucherRepository.findAllByTransferType(tt).stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        if (price == null) throw new RuntimeException("price is required");
        return voucherRepository.findAllByPrice(price).stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(com.epam.finaltask.model.HotelType hotelType) {
        if (hotelType == null) throw new RuntimeException("hotelType is required");
        return voucherRepository.findAllByHotelType(hotelType).stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAll() {
        return voucherRepository.findAll().stream().map(voucherMapper::toVoucherDTO).collect(Collectors.toList());
    }
}
