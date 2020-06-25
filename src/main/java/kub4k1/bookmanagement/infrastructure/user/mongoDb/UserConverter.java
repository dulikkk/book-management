package kub4k1.bookmanagement.infrastructure.user.mongoDb;

import kub4k1.bookmanagement.domain.user.dto.UserDto;

class UserConverter {

    UserDocument toDocument(UserDto userDto){
        return UserDocument.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .active(userDto.isActive())
                .roles(userDto.getRoles())
                .build();
    }

    UserDto toDto(UserDocument userDocument){
        return UserDto.builder()
                .id(userDocument.getId())
                .username(userDocument.getUsername())
                .email(userDocument.getEmail())
                .password(userDocument.getPassword())
                .active(userDocument.isActive())
                .roles(userDocument.getRoles())
                .build();
    }
}
