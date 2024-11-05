package net.sonerapp.jwtauth.interfaces.mapper;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Service;

import net.sonerapp.jwtauth.application.dto.UserControllerDto.UserDto;
import net.sonerapp.jwtauth.core.model.User;

@Service
public class ConversionServiceAdapter {

    private final ConversionService conversionService;

    public ConversionServiceAdapter(@Lazy final ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public UserDto mapUserToUserDto(final User source) {
        return (UserDto) conversionService.convert(source, TypeDescriptor.valueOf(User.class),
                TypeDescriptor.valueOf(UserDto.class));
    }

}
