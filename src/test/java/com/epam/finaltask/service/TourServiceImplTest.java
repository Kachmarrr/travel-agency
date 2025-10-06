package com.epam.finaltask.service;

import com.epam.finaltask.DTO.TourDTO;
import com.epam.finaltask.exception.BadRequestException;
import com.epam.finaltask.exception.NotFoundException;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.enums.HotelType;
import com.epam.finaltask.model.enums.TourType;
import com.epam.finaltask.model.enums.TransferType;
import com.epam.finaltask.model.enums.TourStatus;
import com.epam.finaltask.repository.TourRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.Implementation.TourServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourServiceImplTest {

    @Mock
    private TourRepository tourRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TourMapper tourMapper;

    @InjectMocks
    private TourServiceImpl tourService;

    // --- helpers ---
    private Tour buildTour(Long id, String title, BigDecimal price, TourType tourType, TransferType transferType,
                           HotelType hotelType, TourStatus status, Boolean isHot, Long userId,
                           LocalDate arrival, LocalDate eviction) {
        Tour t = new Tour();
        t.setId(id);
        t.setTitle(title);
        t.setPrice(price);
        t.setTourType(tourType);
        t.setTransferType(transferType);
        t.setHotelType(hotelType);
        t.setStatus(status);
        t.setHot(isHot != null ? isHot : false);
        t.setArrivalDate(arrival);
        t.setEvictionDate(eviction);
        if (userId != null) {
            User u = new User();
            u.setId(userId);
            t.setUser(u);
        }
        return t;
    }

    private TourDTO buildTourDTO(Long id, String title, Double price, TourType tourType, TransferType transferType,
                                 HotelType hotelType, TourStatus status, Boolean isHot, Long userId,
                                 LocalDate arrival, LocalDate eviction) {
        TourDTO dto = new TourDTO();
        dto.setId(id);
        dto.setTitle(title);
        dto.setPrice(price);
        dto.setTourType(tourType);
        dto.setTransferType(transferType);
        dto.setHotelType(hotelType);
        dto.setStatus(status);
        dto.setIsHot(isHot);
        dto.setUserId(userId);
        dto.setArrivalDate(arrival);
        dto.setEvictionDate(eviction);
        return dto;
    }

    // --- create ---
    @Test
    void create_nullDto_throwsRuntimeException() {
        assertThrows(RuntimeException.class, () -> tourService.create(null));
    }

    @Test
    void create_success_setsDefaultStatusAndReturnsDto() {
        TourDTO dto = buildTourDTO(null, "Trip", 1000.0, null, null, null, null, false, null, null, null);
        Tour toSave = buildTour(null, "Trip", BigDecimal.valueOf(1000), null, null, null, null, false, null, null, null);
        Tour saved = buildTour(1L, "Trip", BigDecimal.valueOf(1000), null, null, null, TourStatus.AVAILABLE, false, null, null, null);
        TourDTO expectedDto = buildTourDTO(1L, "Trip", 1000.0, null, null, null, TourStatus.AVAILABLE, false, null, null, null);

        when(tourMapper.toTour(dto)).thenReturn(toSave);
        when(tourRepository.save(toSave)).thenReturn(saved);
        when(tourMapper.toTourDTO(saved)).thenReturn(expectedDto);

        TourDTO result = tourService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(tourRepository).save(toSave);
        verify(tourMapper).toTourDTO(saved);
    }

    // --- order ---
    @Test
    void order_tourNotFound_throwsNotFound() {
        when(tourRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> tourService.order(5L, 1L));
    }

    @Test
    void order_alreadyOrdered_throwsRuntime() {
        Tour tour = buildTour(2L, "T", BigDecimal.ONE, null, null, null, TourStatus.AVAILABLE, false, 10L, null, null);
        when(tourRepository.findById(2L)).thenReturn(Optional.of(tour));
        assertThrows(RuntimeException.class, () -> tourService.order(2L, 1L));
    }

    @Test
    void order_userNotFound_throwsNotFound() {
        Tour tour = buildTour(3L, "T3", BigDecimal.ONE, null, null, null, TourStatus.AVAILABLE, false, null, null, null);
        when(tourRepository.findById(3L)).thenReturn(Optional.of(tour));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> tourService.order(3L, 99L));
    }

    @Test
    void order_success_assignsUserAndReturnsDto() {
        Tour tour = buildTour(4L, "T4", BigDecimal.valueOf(200), null, null, null, TourStatus.AVAILABLE, false, null, null, null);
        User user = new User(); user.setId(7L);
        Tour saved = buildTour(4L, "T4", BigDecimal.valueOf(200), null, null, null, TourStatus.AVAILABLE, false, 7L, null, null);
        TourDTO expectedDto = buildTourDTO(4L, "T4", 200.0, null, null, null, TourStatus.AVAILABLE, false, 7L, null, null);

        when(tourRepository.findById(4L)).thenReturn(Optional.of(tour));
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(tourRepository.save(any(Tour.class))).thenReturn(saved);
        when(tourMapper.toTourDTO(saved)).thenReturn(expectedDto);

        TourDTO result = tourService.order(4L, 7L);

        assertNotNull(result);
        assertEquals(7L, result.getUserId());
        verify(tourRepository).save(any(Tour.class));
    }

    // --- update ---
    @Test
    void update_tourNotFound_throwsNotFound() {
        TourDTO dto = buildTourDTO(100L, "x", 10.0, null, null, null, null, null, null, null, null);
        when(tourRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> tourService.update(dto));
    }

    @Test
    void update_success_updatesFieldsAndReturnsDto() {
        Tour existing = buildTour(20L, "old", BigDecimal.valueOf(50), TourType.ADVENTURE, TransferType.BUS, HotelType.ONE_STAR, TourStatus.AVAILABLE, false, null, LocalDate.of(2025,1,1), LocalDate.of(2025,1,7));
        TourDTO dto = buildTourDTO(20L, "newTitle", 150.0, TourType.ADVENTURE, TransferType.PLANE, HotelType.THREE_STARS, TourStatus.PAID, true, 9L, LocalDate.of(2025,2,1), LocalDate.of(2025,2,10));
        User user = new User(); user.setId(9L);

        Tour saved = buildTour(20L, "newTitle", BigDecimal.valueOf(150), TourType.ADVENTURE, TransferType.PLANE, HotelType.ONE_STAR, TourStatus.PAID, true, 9L, LocalDate.of(2025,2,1), LocalDate.of(2025,2,10));
        TourDTO expectedDto = buildTourDTO(20L, "newTitle", 150.0, TourType.ADVENTURE, TransferType.PLANE, HotelType.ONE_STAR, TourStatus.PAID, true, 9L, LocalDate.of(2025,2,1), LocalDate.of(2025,2,10));

        when(tourRepository.findById(20L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(9L)).thenReturn(Optional.of(user));
        when(tourRepository.save(existing)).thenReturn(saved);
        when(tourMapper.toTourDTO(saved)).thenReturn(expectedDto);

        TourDTO result = tourService.update(dto);

        assertNotNull(result);
        assertEquals("newTitle", result.getTitle());
        assertEquals(150.0, result.getPrice());
        assertEquals(9L, result.getUserId());
        verify(tourRepository).save(existing);
    }

    @Test
    void update_userNotFound_throwsRuntimeException() {
        Tour existing = buildTour(21L, "old", BigDecimal.valueOf(50), null, null, null, null, false, null, null, null);
        TourDTO dto = buildTourDTO(21L, null, null, null, null, null, null, null, 55L, null, null);
        when(tourRepository.findById(21L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(55L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> tourService.update(dto));
    }

    // --- delete ---
    @Test
    void delete_notFound_throwsNotFound() {
        when(tourRepository.existsById(42L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> tourService.delete(42L));
    }

    @Test
    void delete_success_callsRepository() {
        when(tourRepository.existsById(50L)).thenReturn(true);
        doNothing().when(tourRepository).deleteById(50L);
        tourService.delete(50L);
        verify(tourRepository).deleteById(50L);
    }

    // --- changeHotStatus ---
    @Test
    void changeHotStatus_tourNotFound_throwsNotFound() {
        when(tourRepository.findById(77L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> tourService.changeHotStatus(77L, null));
    }

    @Test
    void changeHotStatus_toggleWhenDtoNull() {
        Tour existing = buildTour(80L, "t", BigDecimal.ONE, null, null, null, TourStatus.AVAILABLE, false, null, null, null);
        Tour saved = buildTour(80L, "t", BigDecimal.ONE, null, null, null, TourStatus.AVAILABLE, true, null, null, null);
        TourDTO expectedDto = buildTourDTO(80L, "t", 1.0, null, null, null, TourStatus.AVAILABLE, true, null, null, null);

        when(tourRepository.findById(80L)).thenReturn(Optional.of(existing));
        when(tourRepository.save(existing)).thenReturn(saved);
        when(tourMapper.toTourDTO(saved)).thenReturn(expectedDto);

        TourDTO result = tourService.changeHotStatus(80L, null);
        assertTrue(result.getIsHot());
    }

    @Test
    void changeHotStatus_setExplicitFromDto() {
        Tour existing = buildTour(81L, "t2", BigDecimal.ONE, null, null, null, TourStatus.AVAILABLE, false, null, null, null);
        TourDTO incoming = buildTourDTO(null, null, null, null, null, null, null, true, null, null, null);
        Tour saved = buildTour(81L, "t2", BigDecimal.ONE, null, null, null, TourStatus.AVAILABLE, true, null, null, null);
        TourDTO expected = buildTourDTO(81L, "t2", 1.0, null, null, null, TourStatus.AVAILABLE, true, null, null, null);

        when(tourRepository.findById(81L)).thenReturn(Optional.of(existing));
        when(tourRepository.save(existing)).thenReturn(saved);
        when(tourMapper.toTourDTO(saved)).thenReturn(expected);

        TourDTO result = tourService.changeHotStatus(81L, incoming);
        assertTrue(result.getIsHot());
    }

    // --- findById ---
    @Test
    void findById_notFound_throwsNotFound() {
        when(tourRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> tourService.findById(999L));
    }

    @Test
    void findById_success_returnsDto() {
        Tour t = buildTour(5L, "abc", BigDecimal.valueOf(300), null, null, null, TourStatus.AVAILABLE, false, null, null, null);
        TourDTO dto = buildTourDTO(5L, "abc", 300.0, null, null, null, TourStatus.AVAILABLE, false, null, null, null);
        when(tourRepository.findById(5L)).thenReturn(Optional.of(t));
        when(tourMapper.toTourDTO(t)).thenReturn(dto);
        TourDTO res = tourService.findById(5L);
        assertEquals(5L, res.getId());
    }

    // --- findAllByUserId ---
    @Test
    void findAllByUserId_returnsMappedList() {
        Tour t1 = buildTour(11L, "a", BigDecimal.ONE, null, null, null, null, false, 3L, null, null);
        Tour t2 = buildTour(12L, "b", BigDecimal.TEN, null, null, null, null, false, 3L, null, null);
        TourDTO d1 = buildTourDTO(11L, "a", 1.0, null, null, null, null, false, 3L, null, null);
        TourDTO d2 = buildTourDTO(12L, "b", 10.0, null, null, null, null, false, 3L, null, null);

        when(tourRepository.findAllByUserId(3L)).thenReturn(List.of(t1, t2));
        when(tourMapper.toTourDTO(t1)).thenReturn(d1);
        when(tourMapper.toTourDTO(t2)).thenReturn(d2);

        var list = tourService.findAllByUserId(3L);
        assertEquals(2, list.size());
    }

    // --- findAllByTourType ---
    @Test
    void findAllByTourType_null_throwsRuntime() {
        assertThrows(RuntimeException.class, () -> tourService.findAllByTourType(null));
    }

    @Test
    void findAllByTourType_success() {
        Tour t = buildTour(13L, "ct", BigDecimal.ONE, TourType.ADVENTURE, null, null, null, false, null, null, null);
        TourDTO dto = buildTourDTO(13L, "ct", 1.0, TourType.ADVENTURE, null, null, null, false, null, null, null);
        when(tourRepository.findAllByTourType(TourType.ADVENTURE)).thenReturn(List.of(t));
        when(tourMapper.toTourDTO(t)).thenReturn(dto);
        var res = tourService.findAllByTourType(TourType.ADVENTURE);
        assertEquals(1, res.size());
    }

    // --- findAllByTransferType ---
    @Test
    void findAllByTransferType_null_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> tourService.findAllByTransferType(null));
    }

    @Test
    void findAllByTransferType_success() {
        Tour t = buildTour(14L, "tr", BigDecimal.ONE, null, TransferType.BUS, null, null, false, null, null, null);
        TourDTO dto = buildTourDTO(14L, "tr", 1.0, null, TransferType.BUS, null, null, false, null, null, null);
        when(tourRepository.findAllByTransferType(TransferType.BUS)).thenReturn(List.of(t));
        when(tourMapper.toTourDTO(t)).thenReturn(dto);
        var res = tourService.findAllByTransferType(TransferType.BUS);
        assertEquals(1, res.size());
    }

    // --- findAllByPrice ---
    @Test
    void findAllByPrice_null_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> tourService.findAllByPrice(null));
    }

    @Test
    void findAllByPrice_success() {
        Tour t = buildTour(15L, "p", BigDecimal.valueOf(123.0), null, null, null, null, false, null, null, null);
        TourDTO dto = buildTourDTO(15L, "p", 123.0, null, null, null, null, false, null, null, null);
        when(tourRepository.findAllByPrice(BigDecimal.valueOf(123.0))).thenReturn(List.of(t));
        when(tourMapper.toTourDTO(t)).thenReturn(dto);
        var res = tourService.findAllByPrice(123.0);
        assertEquals(1, res.size());
    }

    // --- findAllByHotelType ---
    @Test
    void findAllByHotelType_null_throwsBadRequest() {
        assertThrows(BadRequestException.class, () -> tourService.findAllByHotelType(null));
    }

    @Test
    void findAllByHotelType_success() {
        Tour t = buildTour(16L, "h", BigDecimal.ONE, null, null, HotelType.ONE_STAR, null, false, null, null, null);
        TourDTO dto = buildTourDTO(16L, "h", 1.0, null, null, HotelType.ONE_STAR, null, false, null, null, null);
        when(tourRepository.findAllByHotelType(HotelType.ONE_STAR)).thenReturn(List.of(t));
        when(tourMapper.toTourDTO(t)).thenReturn(dto);
        var res = tourService.findAllByHotelType(HotelType.ONE_STAR);
        assertEquals(1, res.size());
    }

    // --- findAll ---
    @Test
    void findAll_returnsList() {
        Tour t1 = buildTour(21L, "x1", BigDecimal.ONE, null, null, null, null, false, null, null, null);
        Tour t2 = buildTour(22L, "x2", BigDecimal.TEN, null, null, null, null, false, null, null, null);
        when(tourRepository.findAll()).thenReturn(List.of(t1, t2));
        when(tourMapper.toTourDTO(t1)).thenReturn(buildTourDTO(21L, "x1", 1.0, null, null, null, null, false, null, null, null));
        when(tourMapper.toTourDTO(t2)).thenReturn(buildTourDTO(22L, "x2", 10.0, null, null, null, null, false, null, null, null));
        var res = tourService.findAll();
        assertEquals(2, res.size());
    }

    // --- findAllByStatus ---
    @Test
    void findAllByStatus_returnsList() {
        Tour t = buildTour(30L, "s", BigDecimal.ONE, null, null, null, TourStatus.AVAILABLE, false, null, null, null);
        when(tourRepository.findAllByStatus(TourStatus.AVAILABLE)).thenReturn(List.of(t));
        when(tourMapper.toTourDTO(t)).thenReturn(buildTourDTO(30L, "s", 1.0, null, null, null, TourStatus.AVAILABLE, false, null, null, null));
        var res = tourService.findAllByStatus(TourStatus.AVAILABLE);
        assertEquals(1, res.size());
    }
}
