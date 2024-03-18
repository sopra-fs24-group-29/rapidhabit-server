package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g.,
 * UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {
    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);
    @Mapping(source = "username", target = "username")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "creationDate",target = "creationDate")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "birthdate", target = "birthdate")
    UserGetDTO convertEntityToUserGetDTO(User user);

    // Assuming this is the method causing issues, adjust as needed for your use case

    @Mapping(target = "username", source = "username") // Map 'username' from DTO to 'name' in User if that's the intended mapping
    @Mapping(source = "birthdate", target = "birthdate") // Ignore this if your UserPutDTO does not have a 'birthdate' field
    User convertPutDTOtoEntity(UserPutDTO userPutDTO);
}
