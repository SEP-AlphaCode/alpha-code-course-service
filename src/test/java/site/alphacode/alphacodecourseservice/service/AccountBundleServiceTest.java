package site.alphacode.alphacodecourseservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import site.alphacode.alphacodecourseservice.dto.request.create.CreateAccountBundle;
import site.alphacode.alphacodecourseservice.dto.response.AccountBundleDto;
import site.alphacode.alphacodecourseservice.dto.response.PagedResult;
import site.alphacode.alphacodecourseservice.entity.AccountBundle;
import site.alphacode.alphacodecourseservice.exception.ConflictException;
import site.alphacode.alphacodecourseservice.exception.ResourceNotFoundException;
import site.alphacode.alphacodecourseservice.repository.AccountBundleRepository;
import site.alphacode.alphacodecourseservice.service.implement.AccountBundleServiceImplement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountBundleService Unit Tests")
class AccountBundleServiceTest {

    @Mock
    private AccountBundleRepository accountBundleRepository;

    @InjectMocks
    private AccountBundleServiceImplement accountBundleService;

    private UUID accountBundleId;
    private UUID accountId;
    private UUID bundleId;
    private AccountBundle accountBundle;
    private CreateAccountBundle createAccountBundle;

    @BeforeEach
    void setUp() {
        accountBundleId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        bundleId = UUID.randomUUID();

        accountBundle = AccountBundle.builder()
                .id(accountBundleId)
                .accountId(accountId)
                .bundleId(bundleId)
                .status(1)
                .createdDate(LocalDateTime.now())
                .build();

        createAccountBundle = new CreateAccountBundle();
        createAccountBundle.setAccountId(accountId);
        createAccountBundle.setBundleId(bundleId);
    }

    @Test
    @DisplayName("Should get account bundle by account id and bundle id successfully")
    void testGetByAccountIdAndBundleId_Success() {
        // Given
        when(accountBundleRepository.findNoneDeleteByAccountIdAndBundleId(accountId, bundleId))
                .thenReturn(Optional.of(accountBundle));

        // When
        AccountBundleDto result = accountBundleService.getByAccountIdAndBundleId(accountId, bundleId);

        // Then
        assertNotNull(result);
        assertEquals(accountBundleId, result.getId());
        verify(accountBundleRepository).findNoneDeleteByAccountIdAndBundleId(accountId, bundleId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when account bundle not found")
    void testGetByAccountIdAndBundleId_NotFound() {
        // Given
        when(accountBundleRepository.findNoneDeleteByAccountIdAndBundleId(accountId, bundleId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            accountBundleService.getByAccountIdAndBundleId(accountId, bundleId);
        });
        verify(accountBundleRepository).findNoneDeleteByAccountIdAndBundleId(accountId, bundleId);
    }

    @Test
    @DisplayName("Should throw ConflictException when account bundle already exists")
    void testCreate_AlreadyExists() {
        // Given
        when(accountBundleRepository.findNoneDeleteByAccountIdAndBundleId(accountId, bundleId))
                .thenReturn(Optional.of(accountBundle));

        // When & Then
        assertThrows(ConflictException.class, () -> {
            accountBundleService.create(createAccountBundle);
        });
        verify(accountBundleRepository).findNoneDeleteByAccountIdAndBundleId(accountId, bundleId);
        verify(accountBundleRepository, never()).save(any(AccountBundle.class));
    }

    @Test
    @DisplayName("Should delete account bundle by id successfully")
    void testDeleteById_Success() {
        // Given
        when(accountBundleRepository.findById(accountBundleId)).thenReturn(Optional.of(accountBundle));
        when(accountBundleRepository.save(any(AccountBundle.class))).thenReturn(accountBundle);

        // When
        assertDoesNotThrow(() -> {
            accountBundleService.deleteById(accountBundleId);
        });

        // Then
        verify(accountBundleRepository).findById(accountBundleId);
        verify(accountBundleRepository).save(any(AccountBundle.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent account bundle")
    void testDeleteById_NotFound() {
        // Given
        when(accountBundleRepository.findById(accountBundleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            accountBundleService.deleteById(accountBundleId);
        });
        verify(accountBundleRepository).findById(accountBundleId);
        verify(accountBundleRepository, never()).save(any(AccountBundle.class));
    }
}

