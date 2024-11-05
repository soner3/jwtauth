package net.sonerapp.jwtauth.interfaces.mapper;

import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import net.sonerapp.jwtauth.application.dto.UserControllerDto.UserDto;
import net.sonerapp.jwtauth.core.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper extends Converter<User, UserDto> {

    UserDto convert(@NonNull User user);

}
