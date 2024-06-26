package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.chat.ChatEntryPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.user.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupPutDTO;
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

    @Mapping(source = "firstname", target = "firstname")
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "email", target = "email")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Group convertGroupPostDTOtoEntity(GroupPostDTO groupPostDTO);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Group convertGroupPutDTOtoEntity(GroupPutDTO groupPutDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstname", target = "firstname")
    @Mapping(source = "lastname", target = "lastname")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "status", target = "status")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "message", target = "message")
    ChatMessage convertChatEntryPutDTOToEntity(ChatEntryPutDTO chatEntryPutDTO);

}
