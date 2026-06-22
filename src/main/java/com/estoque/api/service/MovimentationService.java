package com.estoque.api.service;

import com.estoque.api.dto.MovimentationResponseDTO;
import com.estoque.api.model.Movimentation;
import com.estoque.api.model.MovimentationType;
import com.estoque.api.model.Product;
import com.estoque.api.model.User;
import com.estoque.api.repository.MovimentationRepository;
import com.estoque.api.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MovimentationService {

    private final MovimentationRepository movimentationRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(MovimentationService.class);

    public MovimentationService(MovimentationRepository movimentationRepository,
                                UserRepository userRepository) {
        this.movimentationRepository = movimentationRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void registerMovimentation(Product product, MovimentationType type, 
                                      Integer previousQuantity, Integer newQuantity) {
        User user = getAuthenticatedUser();
        
        Movimentation movimentation = new Movimentation();
        movimentation.setProduct(product);
        movimentation.setUser(user);
        movimentation.setType(type);
        movimentation.setQuantity(Math.abs(newQuantity - previousQuantity));
        movimentation.setPreviousQuantity(previousQuantity);
        movimentation.setCurrentQuantity(newQuantity);
        
        movimentationRepository.save(movimentation);
        logger.info("Movimentação registrada: {} do produto {} ({}) - Quantidade: {}", 
        type, product.getName(), product.getId(), Math.abs(newQuantity - previousQuantity));
    }

    public Page<MovimentationResponseDTO> listUserMovimentations(Pageable pageable) {
        User user = getAuthenticatedUser();
        Page<Movimentation> movimentations = movimentationRepository.findByUserId(user.getId(), pageable);
        return movimentations.map(this::mapToResponseDTO);
    }

    private MovimentationResponseDTO mapToResponseDTO(Movimentation movimentation) {
        return new MovimentationResponseDTO(
                movimentation.getId(),
                movimentation.getProduct().getId(),
                movimentation.getProduct().getName(),
                movimentation.getUser().getEmail(),
                movimentation.getType(),
                movimentation.getQuantity(),
                movimentation.getPreviousQuantity(),
                movimentation.getCurrentQuantity(),
                movimentation.getCreatedAt()
        );
    }
}