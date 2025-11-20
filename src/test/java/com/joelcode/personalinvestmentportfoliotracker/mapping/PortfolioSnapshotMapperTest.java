package com.joelcode.personalinvestmentportfoliotracker.services.mapping;

import com.joelcode.personalinvestmentportfoliotracker.dto.portfoliosnapshot.PortfolioSnapshotCreateRequest;
import com.joelcode.personalinvestmentportfoliotracker.dto.portfoliosnapshot.PortfolioSnapshotDTO;
import com.joelcode.personalinvestmentportfoliotracker.entities.Account;
import com.joelcode.personalinvestmentportfoliotracker.entities.PortfolioSnapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PortfolioSnapshotMapperTest {

    @Test
    void testToEntity_Success() {
        // Arrange
        Account account = new Account();
        account.setAccountId(UUID.randomUUID());

        PortfolioSnapshotCreateRequest request = new PortfolioSnapshotCreateRequest(UUID.randomUUID(), LocalDate.now(),
                BigDecimal.valueOf(55000.0), BigDecimal.valueOf(5000.0), BigDecimal.valueOf(50000.0), BigDecimal.valueOf(5000.0),
                BigDecimal.valueOf(100.0));

        // Act
        PortfolioSnapshot snapshot = PortfolioSnapshotMapper.toEntity(request, account);

        // Assert
        assertNotNull(snapshot);
        assertEquals(account, snapshot.getAccount());
        assertEquals(LocalDate.now(), snapshot.getSnapshotDate());
        assertEquals(BigDecimal.valueOf(55000.0), snapshot.getTotalValue());
        assertEquals(BigDecimal.valueOf(5000.0), snapshot.getCashBalance());
        assertEquals(BigDecimal.valueOf(50000.0), snapshot.getTotalCostBasis());
        assertEquals(BigDecimal.valueOf(5000.0), snapshot.getTotalGain());
        assertEquals(BigDecimal.valueOf(100.0), snapshot.getDayChange());
    }

    @Test
    void testToEntity_NullTotalGain() {
        // Arrange
        Account account = new Account();
        PortfolioSnapshotCreateRequest request = new PortfolioSnapshotCreateRequest(UUID.randomUUID(), LocalDate.now(),
                BigDecimal.valueOf(10000), BigDecimal.valueOf(7000), BigDecimal.valueOf(4000), BigDecimal.ZERO,
                BigDecimal.ZERO);

        // Act
        PortfolioSnapshot snapshot = PortfolioSnapshotMapper.toEntity(request, account);

        // Assert
        assertNotNull(snapshot);
        assertEquals(BigDecimal.ZERO, snapshot.getTotalGain());
        assertEquals(BigDecimal.ZERO, snapshot.getDayChange());
    }

    @Test
    void testToEntity_NullAccount() {
        // Arrange
        PortfolioSnapshotCreateRequest request = new PortfolioSnapshotCreateRequest(UUID.randomUUID(), LocalDate.now(),
                BigDecimal.valueOf(10000), BigDecimal.valueOf(7000), BigDecimal.valueOf(4000), BigDecimal.valueOf(3939),
                BigDecimal.valueOf(38.3));

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            PortfolioSnapshotMapper.toEntity(request, null);
        });
    }

    @Test
    void testToDTO_Success() {
        // Arrange
        PortfolioSnapshot snapshot = new PortfolioSnapshot();
        UUID snapshotId = UUID.randomUUID();
        snapshot.setSnapshotId(snapshotId);

        Account account = new Account();
        account.setAccountId(UUID.randomUUID());
        snapshot.setAccount(account);

        snapshot.setSnapshotDate(LocalDate.now());
        snapshot.setTotalValue(BigDecimal.valueOf(55000.0));
        snapshot.setCashBalance(BigDecimal.valueOf(5000.0));
        snapshot.setTotalCostBasis(BigDecimal.valueOf(50000.0));
        snapshot.setTotalGain(BigDecimal.valueOf(5000.0));
        snapshot.setDayChange(BigDecimal.valueOf(100.0));

        // Act
        PortfolioSnapshotDTO dto = PortfolioSnapshotMapper.toDTO(snapshot);

        // Assert
        assertNotNull(dto);
        assertEquals(snapshotId, dto.getSnapshotId());
        assertEquals(account.getAccountId(), dto.getAccountId());
        assertEquals(LocalDate.now(), dto.getSnapshotDate());
        assertEquals(BigDecimal.valueOf(55000.0), dto.getTotalValue());
        assertEquals(BigDecimal.valueOf(5000.0), dto.getCashBalance());
    }

    @Test
    void testToDTO_Null() {
        // Act
        PortfolioSnapshotDTO dto = PortfolioSnapshotMapper.toDTO(null);

        // Assert
        assertNull(dto);
    }
}