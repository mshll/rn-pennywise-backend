package RNcornerStoneBackend.RNcornerStoneBackend.Chore.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import RNcornerStoneBackend.RNcornerStoneBackend.Chore.Entity.ChoreEntity;
import RNcornerStoneBackend.RNcornerStoneBackend.Chore.Entity.Status;
import RNcornerStoneBackend.RNcornerStoneBackend.Chore.Repository.ChoreRepository;
import RNcornerStoneBackend.RNcornerStoneBackend.Chore.bo.ChoreResponse;
import RNcornerStoneBackend.RNcornerStoneBackend.child.Repository.ChildRepository;
import RNcornerStoneBackend.RNcornerStoneBackend.child.entity.ChildEntity;
import RNcornerStoneBackend.RNcornerStoneBackend.user.entity.Role;
import RNcornerStoneBackend.RNcornerStoneBackend.user.entity.UserEntity;
import RNcornerStoneBackend.RNcornerStoneBackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ChoreService {
    private final ChoreRepository choreRepository;
    private final UserRepository userRepository;
    private final ChildRepository childRepository;

    public ChoreService(ChoreRepository choreRepository, UserRepository userRepository,
            ChildRepository childRepository) {
        this.choreRepository = choreRepository;
        this.userRepository = userRepository;
        this.childRepository = childRepository;
    }
    // public ChoreResponse createChore(Long parentId, Long childId, ChoreResponse
    // choreDTO) {
    // UserEntity parent = userRepository.findById(parentId)
    // .orElseThrow(() -> new EntityNotFoundException("Parent not found"));
    // ChildEntity child = childRepository.findById(childId)
    // .orElseThrow(() -> new EntityNotFoundException("Child not found"));
    // ChoreEntity chore = new ChoreEntity();
    // chore.setParent(parent);
    // chore.setChild(child);
    // chore.setTitle(choreDTO.getTitle());
    // chore.setDescription(choreDTO.getDescription());
    // chore.setRewardsAmount(choreDTO.getRewardAmount());
    // chore.setStatus(Status.PENDING);
    // ChoreEntity savedChore = choreRepository.save(chore);
    // ChoreResponse savedChoreResponse = new ChoreResponse();
    // savedChoreResponse.setId(savedChore.getId());
    // savedChoreResponse.setParentId(savedChore.getParent().getId());
    // savedChoreResponse.setChildId(savedChore.getChild().getId());
    // savedChoreResponse.setTitle(savedChore.getTitle());
    // savedChoreResponse.setDescription(savedChore.getDescription());
    // savedChoreResponse.setRewardAmount(savedChore.getRewardsAmount());
    // savedChoreResponse.setStatus(savedChore.getStatus());
    // return savedChoreResponse;
    // }

    public ChoreResponse createChore(Long childId, ChoreResponse choreDTO) {
        // Get the current authenticated user from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity parent = (UserEntity) authentication.getPrincipal(); // Assumes the principal is of type UserEntity

        // Ensure the authenticated user is a parent
        if (parent.getRole() != Role.PARENT) {
            throw new RuntimeException("Only parents can create chores");
        }

        // Find the child by ID
        ChildEntity child = childRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException("Child not found"));

        // Create the chore entity
        ChoreEntity chore = new ChoreEntity();
        chore.setParent(parent);
        chore.setChild(child);
        chore.setTitle(choreDTO.getTitle());
        chore.setDescription(choreDTO.getDescription());
        chore.setRewardsAmount(choreDTO.getRewardAmount());
        chore.setStatus(Status.PENDING);

        // Save the chore and map the response
        ChoreEntity savedChore = choreRepository.save(chore);
        ChoreResponse savedChoreResponse = new ChoreResponse();
        savedChoreResponse.setId(savedChore.getId());
        savedChoreResponse.setParentId(savedChore.getParent().getId());
        savedChoreResponse.setChildId(savedChore.getChild().getId());
        savedChoreResponse.setTitle(savedChore.getTitle());
        savedChoreResponse.setDescription(savedChore.getDescription());
        savedChoreResponse.setRewardAmount(savedChore.getRewardsAmount());
        savedChoreResponse.setStatus(savedChore.getStatus());

        return savedChoreResponse;
    }

    // public ChoreResponse updateChoreStatus(Long choreId, Long parentId, Status
    // newStatus) {
    // ChoreEntity chore = choreRepository.findById(choreId)
    // .orElseThrow(() -> new EntityNotFoundException("Chore not found"));
    // UserEntity parent = userRepository.findById(parentId)
    // .orElseThrow(() -> new EntityNotFoundException("Parent not found"));
    // if (!chore.getParent().getId().equals(parentId)) {
    // throw new IllegalArgumentException("Only the assigned parent can update the
    // chore status");
    // }
    // if (newStatus != Status.PENDING && newStatus != Status.COMPLETED && newStatus
    // != Status.UNCOMPLETED) {
    // throw new IllegalArgumentException("Invalid status. Must be PENDING,
    // COMPLETED, or UNCOMPLETED");
    // }
    // chore.setStatus(newStatus);
    // ChoreEntity updatedChore = choreRepository.save(chore);
    // ChoreResponse updatedChoreResponse = new ChoreResponse();
    // updatedChoreResponse.setId(updatedChore.getId());
    // updatedChoreResponse.setParentId(updatedChore.getParent().getId());
    // updatedChoreResponse.setChildId(updatedChore.getChild().getId());
    // updatedChoreResponse.setTitle(updatedChore.getTitle());
    // updatedChoreResponse.setDescription(updatedChore.getDescription());
    // updatedChoreResponse.setRewardAmount(updatedChore.getRewardsAmount());
    // updatedChoreResponse.setStatus(updatedChore.getStatus());
    // return updatedChoreResponse;
    // }

    public ChoreResponse updateChoreStatus(Long choreId, Status newStatus) {
        // Get the current authenticated user from the SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity parent = (UserEntity) authentication.getPrincipal(); // Assumes the principal is of type UserEntity

        // Ensure the authenticated user is a parent
        if (parent.getRole() != Role.PARENT) {
            throw new RuntimeException("Only parents can update chore status");
        }

        // Find the chore by ID
        ChoreEntity chore = choreRepository.findById(choreId)
                .orElseThrow(() -> new EntityNotFoundException("Chore not found"));

        // Ensure the chore belongs to the authenticated parent
        if (!chore.getParent().getId().equals(parent.getId())) {
            throw new IllegalArgumentException("Only the assigned parent can update the chore status");
        }

        // Validate the status
        if (newStatus != Status.PENDING && newStatus != Status.COMPLETED && newStatus != Status.UNCOMPLETED) {
            throw new IllegalArgumentException("Invalid status. Must be PENDING, COMPLETED, or UNCOMPLETED");
        }

        // Update the chore's status
        chore.setStatus(newStatus);
        ChoreEntity updatedChore = choreRepository.save(chore);

        // Map the updated chore entity to ChoreResponse
        ChoreResponse updatedChoreResponse = new ChoreResponse();
        updatedChoreResponse.setId(updatedChore.getId());
        updatedChoreResponse.setParentId(updatedChore.getParent().getId());
        updatedChoreResponse.setChildId(updatedChore.getChild().getId());
        updatedChoreResponse.setTitle(updatedChore.getTitle());
        updatedChoreResponse.setDescription(updatedChore.getDescription());
        updatedChoreResponse.setRewardAmount(updatedChore.getRewardsAmount());
        updatedChoreResponse.setStatus(updatedChore.getStatus());

        return updatedChoreResponse;
    }

    public List<ChoreResponse> getChoresForCurrentChild() {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = (UserEntity) authentication.getPrincipal();

        // Ensure the user is a child
        if (user.getRole() != Role.CHILD) {
            throw new RuntimeException("Only children can access their chores");
        }

        // Find the child entity associated with the user
        ChildEntity child = childRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Child not found"));

        // Get all chores for the child
        List<ChoreEntity> chores = choreRepository.findByChildId(child.getId());

        // Convert to response objects
        return chores.stream()
                .map(this::convertToChoreResponse)
                .collect(Collectors.toList());
    }

    private ChoreResponse convertToChoreResponse(ChoreEntity chore) {
        ChoreResponse response = new ChoreResponse();
        response.setId(chore.getId());
        response.setParentId(chore.getParent().getId());
        response.setChildId(chore.getChild().getId());
        response.setTitle(chore.getTitle());
        response.setDescription(chore.getDescription());
        response.setRewardAmount(chore.getRewardsAmount());
        response.setStatus(chore.getStatus());
        return response;
    }

}