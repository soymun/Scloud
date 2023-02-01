package com.example.zipzip.Mappers;


import com.example.zipzip.DTO.UserDto;
import com.example.zipzip.Entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMappers {

    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);
}
