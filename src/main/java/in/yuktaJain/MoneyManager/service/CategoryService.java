package in.yuktaJain.MoneyManager.service;


import in.yuktaJain.MoneyManager.dto.CategoryDTO;
import in.yuktaJain.MoneyManager.entity.CategoryEntity;
import in.yuktaJain.MoneyManager.entity.ProfileEntity;
import in.yuktaJain.MoneyManager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    //save category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new RuntimeException("Category with this name already exists");
        }

        CategoryEntity newCategory = toEntity(categoryDTO, profile);
        newCategory = categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    //get categories for current user
    public List<CategoryDTO> getCategoriesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    //get categories by type for current user
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> entities = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return entities.stream().map(this::toDTO).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or not accessible"));
        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory = categoryRepository.save(existingCategory);
        return toDTO(existingCategory);
    }

    //helper methods
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile) {
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile() != null ?  entity.getProfile().getId(): null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .type(entity.getType())
                .build();

    }
}
