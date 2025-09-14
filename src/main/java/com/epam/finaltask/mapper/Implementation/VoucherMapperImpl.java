package com.epam.finaltask.mapper.Implementation;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.Voucher;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapperImpl implements VoucherMapper {

    @Override
    public Voucher toVoucher(VoucherDTO dto) {
        if (dto == null) return null;

        Voucher voucher = Voucher.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .arrivalDate(dto.getArrivalDate())
                .evictionDate(dto.getEvictionDate())
                .isHot(Boolean.TRUE.equals(dto.getIsHot()))
                .build();

        return voucher;
    }

    @Override
    public VoucherDTO toVoucherDTO(Voucher voucher) {
        if (voucher == null) return null;

        VoucherDTO voucherDTO = VoucherDTO.builder()
                .title(voucher.getTitle())
                .description(voucher.getDescription())
                .price(voucher.getPrice())
                .arrivalDate(voucher.getArrivalDate())
                .evictionDate(voucher.getEvictionDate())
                .isHot(voucher.isHot())
                .build();

        return voucherDTO;
    }
}
